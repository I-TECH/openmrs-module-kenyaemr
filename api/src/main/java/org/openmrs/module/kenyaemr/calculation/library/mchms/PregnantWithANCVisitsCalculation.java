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
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates if a pregnant woman has a given number of ANC visits during a given period of time
 */
public class PregnantWithANCVisitsCalculation extends AbstractPatientCalculation {

	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Set<Integer> female = Filters.female(cohort, context);

        Form ancForm = MetadataUtils.existing(Form.class,MchMetadata._Form.MCHMS_ANTENATAL_VISIT);
        CalculationResultMap allEncountersForMCHConsultation = Calculations.allEncounters(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION), female, context);
        Integer minVisits = (Integer) parameterValues.get("visits");

        Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
        Set<Integer> patientActiveOnMchms = Filters.inProgram(mchmsProgram, female, context);

        Set<Integer> pregnantCohort = CalculationUtils.patientsThatPass(calculate(new IsPregnantCalculation(),female,context));
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {

            boolean result = false;
            if (pregnantCohort.contains(ptId) || patientActiveOnMchms.contains(ptId)){
                ListResult mchcsEncountersResult = (ListResult) allEncountersForMCHConsultation.get(ptId);
                List<Encounter> encounters = CalculationUtils.extractResultValues(mchcsEncountersResult);

                int counter = 0;

                for(Encounter e: encounters){
                    if(ancForm.getUuid().equals(e.getForm().getUuid())){
                        counter++;
                        if(counter >= minVisits){
                            result = true;
                            break;
                        }
                    }
                }

            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }
}
