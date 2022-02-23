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
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCacxTestDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.ui.framework.SimpleObject;

import java.util.*;

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

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        PatientService patientService = Context.getPatientService();

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        // check for last screening results
        LastCacxTestDateCalculation lastCacxAssmntCalculation  = new LastCacxTestDateCalculation();
        CalculationResultMap lastCacxAssmnt= lastCacxAssmntCalculation.evaluate(cohort, null, context);

        //get a list of all the cacx screening
        CalculationResultMap cacxList = Calculations.allObs(Context.getConceptService().getConcept(164934), aliveAndFemale, context);

        CalculationResultMap lastCacxDate = calculate(new LastCacxTestDateCalculation(), aliveAndFemale, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:aliveAndFemale) {
            Patient patient = patientService.getPatient(ptId);
            boolean needsCacxTest = false;
            String cacxAssmnt = null;
            Date cacxAssmntDate = null;
            String lastCacxResult = null;
            Date lastCacxResultDate = null;

            CalculationResult lastCacxAss = lastCacxAssmnt.get(ptId);
            CalculationResult lastCacxRes = lastCacxAssmnt.get(ptId);

            if(lastCacxAss != null && lastCacxAss.getValue() != null) {
                Object lastAss = lastCacxAss.getValue();
                SimpleObject res = (SimpleObject) lastAss;
                cacxAssmnt = res.get("lastCacxAssmnet").toString();
                cacxAssmntDate = (Date) res.get("lastCacxAssmntDate");
                if(lastCacxResult != null && lastCacxRes.getValue() != null) {
                    lastCacxResult = res.get("lastCacxResult").toString();
                    lastCacxResultDate = (Date) res.get("lastCacxResultDate");
                }
            }

            ListResult listResult = (ListResult) cacxList.get(ptId);
            List<Obs> listObsCacx = CalculationUtils.extractResultValues(listResult);
            // Newly initiated and without cervical cancer test
            if(inHivProgram.contains(ptId) && patient.getAge() >= 15){

                // have never taken the cacx test and new to hiv program
                if(listObsCacx.size() == 0 ) {
                    needsCacxTest = true;
                }

                // no cervical cancer screening done within the past year
                if(cacxAssmnt != null && lastCacxResult == null) {
                    needsCacxTest = true;
                }

                // cacx flag should be 12 months after last cacx if negative
                if(cacxAssmnt != null && lastCacxResult != null && lastCacxResult == "Negitive"  && (daysSince(lastCacxResultDate, context) >= 365)) {
                    needsCacxTest = true;
                }

                // cacx flag should be 6 months after last cacx if positive
                if(lastCacxResult != null && lastCacxResult == "Positive"  && (daysSince(lastCacxResultDate, context) >= 183)) {
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
