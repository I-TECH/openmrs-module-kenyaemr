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
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.DeceasedPatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate possible patient outcomes at the end of the cohort period
 */
public class PatientOutComeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Integer months = (parameterValues != null && parameterValues.containsKey("months")) ? (Integer) parameterValues.get("months") : null;
		Date evaluationDate = context.getNow();

		//get date that is months a head from the now
		Calendar calendarMonthsAhead = Calendar.getInstance();
		calendarMonthsAhead.setTime(evaluationDate);
		calendarMonthsAhead.add(Calendar.MONTH, months);

		//override the patient context back to now
		PatientCalculationService service = Context.getService(PatientCalculationService.class);
		PatientCalculationContext pContext = service.createCalculationContext();
		pContext.setNow(calendarMonthsAhead.getTime());

		CalculationResultMap lastEncounterOfHivDisco = Calculations.lastEncounter(MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_DISCONTINUATION), cohort, pContext);

		Set<Integer> lostPatients = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		Set<Integer> alive = Filters.alive(cohort,pContext);

		Set<Integer> patientsWhoStartedArt = CalculationUtils.patientsThatPass(calculate( new InitialArtRegimenCalculation(), cohort, pContext));

		Set<Integer> patientCurrentArt = CalculationUtils.patientsThatPass(calculate(new CurrentArtRegimenCalculation(), cohort, pContext));

		Set<Integer> deceased = CalculationUtils.patientsThatPass(calculate( new DeceasedPatientsCalculation(), cohort, pContext));

		//declare possible options that would be displayed
		Concept transferOut = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
		Concept died = Dictionary.getConcept(Dictionary.DIED);
		Concept ltfu = Context.getConceptService().getConceptByUuid("5240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
		   String status = "Alive";
			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(lastEncounterOfHivDisco, ptId);

			if(encounter != null && encounter.getEncounterDatetime().before(calendarMonthsAhead.getTime())) {
				for(Obs obs :encounter.getAllObs()){
					if(obs.getConcept().equals(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION))){

						if(obs.getValueCoded().equals(transferOut)) {
							status = "Transferred Out";
						}
						if(obs.getValueCoded().equals(died) || !(alive.contains(ptId))) {
							status = "Dead";
						}
						if (obs.getValueCoded().equals(ltfu) || lostPatients.contains(ptId)) {
							status = "Lost To Follow-Up";
						}
					}
				}
			}
			if((patientsWhoStartedArt.contains(ptId)) && (!patientCurrentArt.contains(ptId))) {
				status = "Stopped ART";
			}

			if(deceased.contains(ptId)) {
				status = "Dead";
			}

			if(lostPatients.contains(ptId)){
				status = "Lost To Follow-Up";
			}

			ret.put(ptId, new SimpleResult(status, this));
		}
		 return  ret;
	}
}
