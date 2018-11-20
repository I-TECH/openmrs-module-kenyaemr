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
import org.joda.time.Months;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.calculation.patient.PatientCalculationContext;
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
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Created by codehub on 05/06/15.
 */
public class NeedsViralLoadTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    /**
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return "Due for Viral Load";
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        CalculationResultMap ret = new CalculationResultMap();

        // need to exclude those on ART already
        Set<Integer> onArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
        //check for last viral load recorded
        CalculationResultMap viralLoadLast = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        //check for last ldl
        CalculationResultMap ldlLast = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
        //get a list of all the viral load
        CalculationResultMap viralLoadList = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        //check for non detectables
        CalculationResultMap ldlViralLoad = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
        //find for prgnant females

        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);

        //get the initial art start date
        CalculationResultMap artStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for(Integer ptId:cohort) {
            boolean needsViralLoadTest = false;
            Obs lastViralLoadObs = EmrCalculationUtils.obsResultForPatient(viralLoadLast, ptId);
            Obs lastLdlObs = EmrCalculationUtils.obsResultForPatient(ldlLast, ptId);
            Date dateInitiated = EmrCalculationUtils.datetimeResultForPatient(artStartDate, ptId);
            ListResult listResult = (ListResult) viralLoadList.get(ptId);
            List<Obs> listObsViralLoads = CalculationUtils.extractResultValues(listResult);
            ListResult ldlList = (ListResult) ldlViralLoad.get(ptId);
            List<List> listLdl = CalculationUtils.extractResultValues(ldlList);
            //find pregnancy obs
            Obs pregnantStatus = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);
            // Newly initiated and more than 3 months without ldl,vl or orders
            if(inHivProgram.contains(ptId) && onArt.contains(ptId)){
                if(listObsViralLoads.size() == 0 && listLdl.size() == 0 && dateInitiated != null && (daysSince(dateInitiated, context) >= 183)) {
                    needsViralLoadTest = true;
                }
                // vl flag should be 3 months after last vl if unsuppressed --with both ldl and vl
                if(lastViralLoadObs != null && lastViralLoadObs.getValueNumeric() >= 1000 && (daysSince(lastViralLoadObs.getObsDatetime(), context) >= 92)) {
                    needsViralLoadTest = true;

                    if(lastLdlObs != null && (daysSince(lastLdlObs.getObsDatetime(), context) < 365)) {
                        needsViralLoadTest = false;
                    }
                }
                // vl flag should be 12 months after last vl if suppressed --with both ldl and vl
                if(lastViralLoadObs != null && lastViralLoadObs.getValueNumeric() < 1000 && (daysSince(lastViralLoadObs.getObsDatetime(), context) >= 365)) {
                    needsViralLoadTest = true;

                    if(lastLdlObs != null && (daysSince(lastLdlObs.getObsDatetime(), context) < 365)) {
                        needsViralLoadTest = false;
                    }
                }
                // vl flag should be 12 months for clients with only ldl if suppressed
                if(lastLdlObs != null && listObsViralLoads.size() == 0 && (daysSince(lastLdlObs.getObsDatetime(), context) < 365)) {
                    needsViralLoadTest = false;
                }
                if(lastLdlObs != null && listObsViralLoads.size() == 0 && (daysSince(lastLdlObs.getObsDatetime(), context) >= 365)) {
                    needsViralLoadTest = true;
                }

                //check for pregnancy and artInitiation
                if(pregnantStatus != null && pregnantStatus.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES)) && dateInitiated != null) {
                    Date whenVLWillBeDue = DateUtil.adjustDate(DateUtil.adjustDate(dateInitiated, 6, DurationUnit.MONTHS), -1, DurationUnit.DAYS);

                    // vl flag should be 6 months after art start date if no previous vl
                    if(lastViralLoadObs == null && lastLdlObs == null && (context.getNow().after(whenVLWillBeDue))){
                        needsViralLoadTest = true;
                    }
                    // vl flag should be 3 months after last vl if unsuppressed
                    if(lastViralLoadObs != null && lastViralLoadObs.getValueNumeric() >= 1000 && (monthsBetween(lastViralLoadObs.getObsDatetime(), context.getNow()) >= 3)) {
                        needsViralLoadTest = true;

                        if(lastLdlObs != null && (daysSince(lastLdlObs.getObsDatetime(), context) < 183)) {
                            needsViralLoadTest = false;
                        }
                    }
                    // vl flag should be 6 months after last vl if suppressed
                    if(lastViralLoadObs != null && lastViralLoadObs.getValueNumeric() < 1000 && (monthsBetween(lastViralLoadObs.getObsDatetime(), context.getNow()) >= 6)) {
                        needsViralLoadTest = true;

                        if(lastLdlObs != null && (daysSince(lastLdlObs.getObsDatetime(), context) < 183)) {
                            needsViralLoadTest = false;
                        }
                    }

                    // vl flag should be 6 months after last vl if suppressed
                    if(lastViralLoadObs != null && listObsViralLoads.size() == 0 && daysSince(lastViralLoadObs.getObsDatetime(), context) >= 183) {
                        needsViralLoadTest = true;
                    }
                    if(lastViralLoadObs != null && listObsViralLoads.size() == 0 && daysSince(lastViralLoadObs.getObsDatetime(), context) < 183) {
                        needsViralLoadTest = false;
                    }
                }


            }
            ret.put(ptId, new BooleanResult(needsViralLoadTest, this));
        }
        return  ret;

    }

    int monthsBetween(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Months.monthsBetween(dateTime1, dateTime2).getMonths());
    }

}
