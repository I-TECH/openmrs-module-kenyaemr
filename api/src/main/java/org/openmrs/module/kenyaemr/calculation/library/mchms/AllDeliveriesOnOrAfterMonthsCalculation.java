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
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates all deliveries at a facility within a given period of time.
 * TODO: This class assumes all deliveries are registered through MCHMS Delivery form. Investigation on this should be carried out
 */
public class AllDeliveriesOnOrAfterMonthsCalculation extends AbstractPatientCalculation {

	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

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
        for (Integer ptId : cohort) {

            boolean result = false;
            if (deliveries != null && deliveries.contains(ptId)){
                ListResult mchcsEncountersResult = (ListResult) allEncountersForMCHConsultation.get(ptId);
                List<Encounter> encounters = CalculationUtils.extractResultValues(mchcsEncountersResult);

                for(Encounter e: encounters){
                    if(deliveryForm.getUuid().equals(e.getForm().getUuid())){
                            result = true;
                    }
                }
            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }
}
