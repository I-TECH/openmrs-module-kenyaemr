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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.openmrs.*;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PreviousHIVClinicalVisitTCACalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.*;

/**
 * Calculate whether patients are due for a CD4 count. Calculation returns true if if the patient
 * is alive, enrolled in the HIV program, and:
 * 1. Is PLHIV and no Baseline CD4 test
 * 2. Is PLHIV ≥5 years of age and who had previously initiated ART and are re-initiating after more than 3 months)
 * 3. Individuals who have documented persistent unsuppressed viral load (2 viral load vl >1000 copies within 3-6 months)
 */
public class NeedsCd4TestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    @Override
    public String getFlagMessage() {
        return flagMsg;
    }

    String flagMsg = ("Due for CD4 test");

    /**
     * @should determine whether patients need a CD4
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, y
     * <p>
     * java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        CalculationResultMap lastObsCount = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap lastObsPercent = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);
        CalculationResultMap lastObsCountQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT_QUALITATIVE), cohort, context);

        CalculationResultMap prevEncTCADateCalcMap = calculate(new PreviousHIVClinicalVisitTCACalculation(), cohort, context);

        Set<Integer> pendingCD4TestResults = CalculationUtils.patientsThatPass(calculate(new PendingCD4ResultCalculation(), cohort, context));
        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

        ObsService obsService = Context.getObsService();
        PersonService patientService = Context.getPersonService();
        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            Date prevEncTCADate = EmrCalculationUtils.datetimeResultForPatient(prevEncTCADateCalcMap, ptId);
            Date latestEncDate = null;
            boolean needsCD4 = false;

            // Is patient alive and in the HIV program with no active CD4 test order
            if (inHivProgram.contains(ptId) && !pendingCD4TestResults.contains(ptId) && !ltfu.contains(ptId)) {

                ObsResult cd4Count = (ObsResult) lastObsCount.get(ptId);
                ObsResult cd4CountQual = (ObsResult) lastObsCountQualitative.get(ptId);
                ObsResult cd4Percentage = (ObsResult) lastObsPercent.get(ptId);

                //1. Baseline test for ALL PLHIV
                if (cd4Count == null && cd4CountQual == null && cd4Percentage == null) {
                    needsCD4 = true;
                }

                //2. PLHIV ≥5 years of age and who had previously initiated ART and are re-initiating after more than 3 months)
                if (patientService.getPerson(ptId).getAge() >= 5) {
                    EncounterType greenCardEncType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
                    Form pocHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
                    Form rdeHivFollowup = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);

                    Encounter lastHIVEncounter = EmrUtils.lastEncounter(Context.getPatientService().getPatient(ptId), greenCardEncType, Arrays.asList(pocHivFollowup, rdeHivFollowup));

                    if (lastHIVEncounter != null) {
                        latestEncDate = lastHIVEncounter.getEncounterDatetime();
                    }

                    if (latestEncDate != null && prevEncTCADate != null) {
                        if (Days.daysBetween(new LocalDate(prevEncTCADate), new LocalDate(latestEncDate)).getDays() > 90) {
                            needsCD4 = true;
                        }
                    }
                }
                //3. Individuals who have documented persistent unsuppressed viral load (2 viral load vl >1000 copies within 3-6 months)
                List<Obs> vlCountList = obsService.getObservationsByPersonAndConcept(patientService.getPerson(ptId), Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
                if (vlCountList != null) {
                    int countUnsuppressedVLs = 0;

                    for (int i = 0; i < vlCountList.size(); ++i) {
                        Date vlDate = vlCountList.get(i).getObsDatetime();

                        Double vl = vlCountList.get(i).getValueNumeric();
                        DateTime d1 = new DateTime(vlDate.getTime());
                        DateTime d2 = new DateTime(context.getNow());

                        int months = Months.monthsBetween(d1, d2).getMonths();
                        if (months <= 6 && vl > 1000)
                            ++countUnsuppressedVLs;
                    }
                    if (countUnsuppressedVLs > 1) {
                        needsCD4 = true;
                    }
                }
            }
            ret.put(ptId, new BooleanResult(needsCD4, this, context));
        }
        return ret;
    }
}

