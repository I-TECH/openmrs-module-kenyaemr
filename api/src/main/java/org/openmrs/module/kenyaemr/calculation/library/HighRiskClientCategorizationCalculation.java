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
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.OVCMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.*;



/**
 * There is a need to categorize high-risk PMTCT Client
 */

public class HighRiskClientCategorizationCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(HighRiskClientCategorizationCalculation.class);

    @Override
    public String getFlagMessage() {
        return "High Risk Client";
    }

    /**
     * Evaluates the calculation
     * PMTCT client enrolled in MCH
     * Allow new HV positive irrespective of time identified
     * All infected AGYW < 19 including OVC & DREAM girls
     * All clients with detectable VL > 200 copies/ml at baseline for known positive or anytime in the PMTCT followUP period
     * Tranfer Ins or Transist clients
     */

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> alive = Filters.alive(cohort, context);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
        Program ovcDreamProgram = MetadataUtils.existing(Program.class, OVCMetadata._Program.OVC);

        PatientService patientService = Context.getPatientService();

        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, cohort, context);
        Set<Integer> inOvcDreamProgram = Filters.inProgram(ovcDreamProgram, alive, context);

        CalculationResultMap transferInDate = calculate(new TransferInDateCalculation(), cohort, context);
        CalculationResultMap inHivProgramResultMap = Calculations.activeEnrollment(hivProgram, alive, context);
        CalculationResultMap ret = new CalculationResultMap();

        Concept latestVL = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
        CalculationResultMap lastVLObs = Calculations.lastObs(latestVL, inHivProgram, context);

        for (Integer ptId : cohort) {
            boolean result = false;
            Integer hivEnrollmentDiffDays = 0;
            Date currentDate = new Date();
            Patient patient = patientService.getPatient(ptId);
            Double vl = EmrCalculationUtils.numericObsResultForPatient(lastVLObs, ptId);
            Date transferInDateValue = EmrCalculationUtils.datetimeResultForPatient(transferInDate, ptId);
            // PMTCT client enrolled in MCH
            if (inMchmsProgram.contains(ptId) && inHivProgram.contains(ptId)) {

                PatientProgram patientProgramHiv = EmrCalculationUtils.resultForPatient(inHivProgramResultMap, ptId);
                Date hivEnrolmentDate = patientProgramHiv.getDateEnrolled();
                hivEnrollmentDiffDays = daysBetween(currentDate, hivEnrolmentDate);
                // Check new HIV+ clients enrolled in the past one month
                if (hivEnrollmentDiffDays <= 31) {
                    result = true;
                }
                //All infected AGYW >10 and < 19
                if (patient.getAge() >= 10 && patient.getAge() <= 19){
                    result = true;
                }
                //All infected in OVC & DREAM girls
                if (inOvcDreamProgram.contains(ptId)) {
                    result = true;
                }
                /// All clients with detectable VL > 200 copies/ml
                if (vl != null && vl >= 200.0) {
                    result = true;
                }
                //All transfer in clients
                if (transferInDateValue != null) {
                    result = true;
                }
            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }

    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Math.abs(Days.daysBetween(d1, d2).getDays());
    }
}

