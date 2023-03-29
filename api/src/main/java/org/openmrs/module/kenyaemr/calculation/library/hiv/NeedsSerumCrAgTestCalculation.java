/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class NeedsSerumCrAgTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);

    /**
     * Eligible Serum CrAg test
     * --------------------------------------------
     *
     * @see PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return "Due for Serum CrAg test";
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        PatientService patientService = Context.getPatientService();

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        //Cohorts to consider

        // Patients with pending CrAg results
        Set<Integer> pendingCrAgTestResults = CalculationUtils.patientsThatPass(calculate(new PendingCrAgResultCalculation(), cohort, context));

        LastCd4CountCalculation lastCD4CountCalculation = new LastCd4CountCalculation();
        LastCrAgCalculation lastCrAgCalculation = new LastCrAgCalculation();

        CalculationResultMap cd4Count = lastCD4CountCalculation.evaluate(cohort, null, context);
        CalculationResultMap creatinine = lastCrAgCalculation.evaluate(cohort, null, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Patient patient = patientService.getPatient(ptId);
            boolean needsCrAgTest = false;
            Double lastCD4ResultValue;
            Date lastCD4ResultDate;
            Date lastCrAgResultDate;

            CalculationResult lastCD4Count = cd4Count.get(ptId);
            CalculationResult lastCrAg = creatinine.get(ptId);

            if (patient.getAge() >= 10 && inHivProgram.contains(ptId) && !pendingCrAgTestResults.contains(ptId) && lastCD4Count != null) {

                Obs cd4CountObs = EmrCalculationUtils.obsResultForPatient(cd4Count, ptId);
                Obs crAgObs = lastCrAg != null ? EmrCalculationUtils.obsResultForPatient(creatinine, ptId) : null;

                lastCD4ResultValue = cd4CountObs != null ? cd4CountObs.getValueNumeric() : null;
                lastCD4ResultDate = cd4CountObs != null ? cd4CountObs.getObsDatetime() : null;
                lastCrAgResultDate = crAgObs != null ? crAgObs.getObsDatetime() : null;

                if (lastCD4ResultValue != null && lastCD4ResultValue <= 200 && ((lastCD4ResultDate != null && lastCrAgResultDate != null && lastCrAgResultDate.before(lastCD4ResultDate)) || lastCrAgResultDate == null)) {

                    needsCrAgTest = true;
                }

                ret.put(ptId, new BooleanResult(needsCrAgTest, this));
            }
        }
        return ret;
    }
}
