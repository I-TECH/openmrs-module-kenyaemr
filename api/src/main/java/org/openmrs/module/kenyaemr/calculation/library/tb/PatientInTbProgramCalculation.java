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

import org.openmrs.Program;
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
 * Calculates whether patients are (alive and) in the TB program
 * Eligibility criteria include:
 * Is currently active in TB program
 *
 */
public class PatientInTbProgramCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    protected static final Log log = LogFactory.getLog(PatientInTbProgramCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

        CalculationResultMap ret = new CalculationResultMap();

        for(Integer ptId: cohort){

            boolean tbPatient = false;
            boolean patientInTbProgram = false;

            if (inTbProgram.contains(ptId)) {
                tbPatient = true;
            }
            if (tbPatient)
                patientInTbProgram = true;

            ret.put(ptId, new BooleanResult(patientInTbProgram, this));
           }
        return ret;
        }

    @Override
    public String getFlagMessage() {
        return "On TB";
    }

}