/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

//*
// Provide alerts for HPV clients who are due for vaccination.
// This is for girls aged 9 - 14 years
// No prior HPV vaccination question in triage is YES.
//
public class EligibleForHpvVaccinationCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(EligibleForPregnancyTestCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        Integer HPV_VACCINATION = 160325;
        Integer YES = 1065;
        ConceptService cs = Context.getConceptService();
        PatientService patientService = Context.getPatientService();
        Concept hpvVaccinationQuestion = cs.getConcept(HPV_VACCINATION);
        Concept hpvYESAnswer = cs.getConcept(YES);
        CalculationResultMap hpvVaccination = Calculations.allObs(hpvVaccinationQuestion, cohort, context);

        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        for(Integer ptId:aliveAndFemale) {
            boolean hpvVaccineGiven = false;
            Patient patient = patientService.getPatient(ptId);
            if(patient.getAge() >= 9 && patient.getAge() <= 14) {
                ListResult hpvVaccinationResults = (ListResult) hpvVaccination.get(ptId);
                // Has never answered whether she has ever been vaccinated for HPV
                if (hpvVaccinationResults.isEmpty()) {
                    hpvVaccineGiven = true;
                }else{
                    //Has answered that she has never been vaccinated for hpv
                        List<Obs> obsListHpvVaccination;
                        obsListHpvVaccination = CalculationUtils.extractResultValues(hpvVaccinationResults);
                        if (obsListHpvVaccination.size() > 0) {
                            for (Obs obs : obsListHpvVaccination) {
                                if (obs.getConcept().equals(hpvVaccinationQuestion) && !obs.getValueCoded().equals(hpvYESAnswer)) {
                                    hpvVaccineGiven = true;
                                    break;
                                }
                            }
                        }
                }
            }
            ret.put(ptId, new BooleanResult(hpvVaccineGiven, this));
        }
        return ret;
    }
    @Override
    public String getFlagMessage() {
        return "Due for HPV Vaccine";
    }

}

