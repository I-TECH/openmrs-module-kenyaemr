/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;


import java.util.Set;
/**
 * Calculates whether patients are (alive and) are eligible for tb enrollment
 * Eligibility criteria include:
 * Is currently not in tb program
 * Has started anti tb drugs
 * Has not been referred elsewhere
 */
public class PatientDueForTbProgramEnrollmentCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

   // protected static final Log log = LogFactory.getLog(PatientDueForTbProgramEnrollmentCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

        //Check whether in tb greencard
        Concept OnAntiTbQuestion = Context.getConceptService().getConcept(164948);
        Concept StartAntiTbQuestion = Context.getConceptService().getConcept(162309);

        CalculationResultMap tbCurrent = Calculations.lastObs(OnAntiTbQuestion, cohort, context);
        CalculationResultMap tbStarted = Calculations.lastObs(StartAntiTbQuestion, cohort, context);

        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {

            boolean patientInTbProgram = false;
            boolean dueForTbEnrollment = false;
            boolean patientDueForTbEnrollment = false;

            //Patient current on anti tb drugs and/or anti tb start dates
            Obs tbCurrentObs = EmrCalculationUtils.obsResultForPatient(tbCurrent, ptId);
            Obs tbStartObs = EmrCalculationUtils.obsResultForPatient(tbStarted, ptId);
            //Eligibility not for patients already in tb program
            if (inTbProgram.contains(ptId)) {
                patientInTbProgram = true;
            }
            //Not enrolled but Currently on antiTb drugs
            if (tbCurrentObs != null && tbStartObs != null) {
                //Started on antiTb drugs
                if (tbCurrentObs.getValueCoded().getConceptId().equals(1066) && tbStartObs.getValueCoded().getConceptId().equals(1065)) {
                    patientDueForTbEnrollment = true;
                }
            }

            if (!patientInTbProgram && patientDueForTbEnrollment)
                    dueForTbEnrollment = true;

                ret.put(ptId, new BooleanResult(dueForTbEnrollment, this));
            }

        return ret;

    }
    /*
    TODO:Enable this flag after complete tests
    */
    @Override
    public String getFlagMessage () {
        return "Due for TB Enrollment";
    }

}