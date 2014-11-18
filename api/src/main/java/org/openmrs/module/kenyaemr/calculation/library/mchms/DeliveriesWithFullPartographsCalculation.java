/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates all deliveries at a facility within a given period of time.
 * TODO: This class assumes all deliveries are registered through MCHMS Delivery form. Investigation on this should be carried out
 */
public class DeliveriesWithFullPartographsCalculation extends AbstractPatientCalculation {


	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		/**
		 * concept list that defines full partograph.
		 * TODO: This is a list of concepts used with delivery form. It is assumed that full partograph requires all these concepts. Verify this
		 */
		List<String> conceptList = Arrays.asList(
				Dictionary.getConcept(Metadata.Concept.PREGNANCY_DURATION_AMOUNT).getUuid(),
				Dictionary.getConcept(Metadata.Concept.METHOD_OF_DELIVERY).getUuid(),
				Dictionary.getConcept(Metadata.Concept.DATE_OF_CONFINEMENT).getUuid(),
				Dictionary.getConcept(Metadata.Concept.ESTIMATED_MATERNAL_BLOOD_LOSS_QUALITATIVE).getUuid(),
				Dictionary.getConcept(Metadata.Concept.MATERNAL_CONDITION_DURING_PUERPERIUM).getUuid(),
				Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_1_MINUTE).getUuid(),
				Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_5_MINUTES).getUuid(),
				Dictionary.getConcept(Metadata.Concept.APGAR_SCORE_AT_10_MINUTES).getUuid(),
				Dictionary.getConcept(Metadata.Concept.NEONTAL_RESUSCITATION).getUuid(),
				Dictionary.getConcept(Metadata.Concept.LOCATION_OF_BIRTH).getUuid(),
				Dictionary.getConcept(Metadata.Concept.DELIVERY_ASSISTANT).getUuid()
		);

		Integer onOrAfter = (Integer)parameterValues.get("onOrAfter");

		Calendar cal = Calendar.getInstance();
		cal.setTime(context.getNow());
		cal.add(Calendar.MONTH, -(onOrAfter));
		Date effectiveDate = cal.getTime();

        Set<Integer> female = Filters.female(cohort, context);

        Form deliveryForm = MetadataUtils.existing(Form.class,MchMetadata._Form.MCHMS_DELIVERY);

        CalculationResultMap allEncountersForMCHConsultation = Calculations.allEncountersOnOrAfter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION), effectiveDate, female, context);
        Set<Integer> deliveries = allEncountersForMCHConsultation.keySet();
        CalculationResultMap ret = new CalculationResultMap();
		ObsService obsService = Context.getObsService();
		PersonService personService = Context.getPersonService();
		for (Integer ptId : cohort) {

            boolean result = false;
            if (deliveries != null && deliveries.contains(ptId)){
                ListResult mchcsEncountersResult = (ListResult) allEncountersForMCHConsultation.get(ptId);
                List<Encounter> encounters = CalculationUtils.extractResultValues(mchcsEncountersResult);

                for(Encounter e: encounters){
                    if(deliveryForm.getUuid().equals(e.getForm().getUuid())){
						//TODO: This code assumes that delivery form is filled in one encounter. This will need to be changed if logic changes
						List<Obs> deliveryObs = obsService.getObservations(Arrays.asList(personService.getPerson(ptId)), Arrays.asList(e), null, null, null, null, null, null, null, null, null, false);
                            result = isCompletePartograph(conceptList, deliveryObs);
							break;

                    }
                }
            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }

	private boolean isCompletePartograph(List<String> fullPartographConcepts, List<Obs> deliveryObs){
		Set<String> deliveryObsConceptUUIDs = new HashSet<String>();
		for (Obs o: deliveryObs) {
			deliveryObsConceptUUIDs.add(o.getConcept().getUuid());
		}

		if (deliveryObsConceptUUIDs.containsAll(fullPartographConcepts))
			return true;
		return false;
	}
}
