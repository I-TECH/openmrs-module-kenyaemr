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
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.ui.framework.SimpleObject;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Created by codehub on 05/06/15.
 */
public class NeedsCACXTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    /**
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() { return "Due for CACX Screening";}
    Integer SCREENING_RESULT = 164934;
    Concept POSITIVE = Dictionary.getConcept(Dictionary.POSITIVE);
    Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
    String NORMAL = "Normal";
    String SUSPICIOUS_FOR_CANCER = "suspected cervical cancer";
    String OTHER = "Other";
    String ABNORMAL = "Abnormal";
    String LOW_GRADE_LESION = "Abnormal Pap Smear, Low Grade Squamous Intraepithelial Lesion (LGSIL)";
    String HIGH_GRADE_LESION = "Papanicolaou Smear of Cervix with High Grade Squamous Intraepithelial Lesion (HGSIL)\n";
    String INVASIVE_CANCER = "carcinoma of uterine cervix, invasive";
    String PRESUMED_CANCER = "\tPresumed diagnosis";

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        PatientService patientService = Context.getPatientService();

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        // check for last screening results
        ConceptService conceptService = Context.getConceptService();
        CalculationResultMap cacxLast = Calculations.lastObs(conceptService.getConcept(SCREENING_RESULT), cohort, context);
        
        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:aliveAndFemale) {
            Patient patient = patientService.getPatient(ptId);
            boolean needsCacxTest = false;
            Obs lastCacxTestObs = EmrCalculationUtils.obsResultForPatient(cacxLast, ptId);

            // Newly initiated and without cervical cancer test
            if(inHivProgram.contains(ptId) && patient.getAge() >= 15){

                // no cervical cancer screening done within the past year
                if(lastCacxTestObs == null) {
                    needsCacxTest = true;
                }

                // cacx flag should be 12 months after last cacx if negative or normal
                if(lastCacxTestObs != null && (lastCacxTestObs.getValueCoded().equals(NEGATIVE) || lastCacxTestObs.getValueCoded().equals(NORMAL))  && (daysSince(lastCacxTestObs.getObsDatetime(), context) >= 365)) {
                    needsCacxTest = true;
                }

                // cacx flag should be 6 months after last cacx if positive
                if(lastCacxTestObs != null && lastCacxTestObs.getValueCoded().equals(POSITIVE) && (daysSince(lastCacxTestObs.getObsDatetime(), context) >= 183)) {
                    needsCacxTest = true;
                }

                // cacx flag should remain if there is any suspicion
                if(lastCacxTestObs != null && (lastCacxTestObs.getValueCoded().equals(SUSPICIOUS_FOR_CANCER) || lastCacxTestObs.getValueCoded().equals(OTHER) || lastCacxTestObs.getValueCoded().equals(LOW_GRADE_LESION) || lastCacxTestObs.getValueCoded().equals(HIGH_GRADE_LESION) || lastCacxTestObs.getValueCoded().equals(INVASIVE_CANCER) || lastCacxTestObs.getValueCoded().equals(PRESUMED_CANCER))) {
                    needsCacxTest = true;
                }

            }
            ret.put(ptId, new BooleanResult(needsCacxTest, this));
        }
        return  ret;
    }

    int monthsBetween(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Months.monthsBetween(dateTime1, dateTime2).getMonths());
    }
}
