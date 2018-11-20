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
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Months;
import org.joda.time.Days;
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
public class PendingViralLoadResultCalculation  extends AbstractPatientCalculation implements PatientFlagCalculation {

    /**
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return "Pending VL result";
    }
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        CalculationResultMap ret = new CalculationResultMap();
        //check for last viral load recorded
        CalculationResultMap viralLoadLast = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        CalculationResultMap ldlViralLoad = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
        //check for last ldl
        CalculationResultMap viralLoadList = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        CalculationResultMap ldlLast = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
//check for test orders
        CalculationResultMap testOrders = Calculations.allObs(Dictionary.getConcept(Dictionary.TESTS_ORDERED), cohort, context);

        for (Integer ptId : cohort) {
            boolean pendingViralLoadResult = false;
            boolean hasCurrentVlResultAfterLastTestOrder = false;
            boolean hasCurrentLDLResultAfterLastTestOrder = false;
            boolean hasNoResults = false;
            Date lastViralLoadObsDate = null;
            Date lastLdlObsDate = null;
            Date lastVlTestOrderDate = null;
            Integer testAndvlResultDiff = 0;
            Integer testAndldlResultDiff = 0;
            Integer vlAndLdlDiff = 0;
            Integer ldlAndVlDiff = 0;
            Obs lastViralLoadObs = EmrCalculationUtils.obsResultForPatient(viralLoadLast, ptId);
            Obs lastLdlObs = EmrCalculationUtils.obsResultForPatient(ldlLast, ptId);

            ListResult listResult = (ListResult) viralLoadList.get(ptId);
            ListResult testOrdersList = (ListResult) testOrders.get(ptId);
            List<Obs> testObsList = CalculationUtils.extractResultValues(testOrdersList);
            List<Obs> listObsViralLoads = CalculationUtils.extractResultValues(listResult);
            ListResult ldlList = (ListResult) ldlViralLoad.get(ptId);
            List<List> listLdl = CalculationUtils.extractResultValues(ldlList);

            //Find latest viral load test order
            Obs lastVlTestOrder = getLatestVlOrder(testObsList);
            // Newly initiated and more than 3 months without ldl,vl or orders
            if (inHivProgram.contains(ptId) && lastVlTestOrder != null) {
                lastVlTestOrderDate = lastVlTestOrder.getObsDatetime();

                //Check whether patient has vl result for the most current vl test order vl came latest
                if (lastViralLoadObs != null && lastLdlObs != null) {
                    lastViralLoadObsDate = lastViralLoadObs.getObsDatetime();
                    lastLdlObsDate = lastLdlObs.getObsDatetime();
                    vlAndLdlDiff = minutesBetween(lastLdlObsDate, lastViralLoadObsDate);
                    testAndvlResultDiff = minutesBetween(lastVlTestOrderDate, lastViralLoadObsDate);

                    if (testAndvlResultDiff > 0 && vlAndLdlDiff > 0) {
                        hasCurrentVlResultAfterLastTestOrder = true;
                        pendingViralLoadResult = false;
                    }
                    if (testAndvlResultDiff < 0 && vlAndLdlDiff > 0) {
                        pendingViralLoadResult = true;
                    }
                }
                //Check whether patient has ldl result for the most current vl test order ldl came latest
                if (lastLdlObs != null && lastViralLoadObs != null) {
                    lastLdlObsDate = lastLdlObs.getObsDatetime();
                    lastViralLoadObsDate = lastViralLoadObs.getObsDatetime();
                    ldlAndVlDiff = minutesBetween(lastViralLoadObsDate,lastLdlObsDate);
                    testAndldlResultDiff = minutesBetween(lastVlTestOrderDate, lastLdlObsDate);
                    if (testAndldlResultDiff > 0 && ldlAndVlDiff > 0 ) {
                        hasCurrentLDLResultAfterLastTestOrder = true;
                        pendingViralLoadResult = false;
                    }


                    if (testAndldlResultDiff < 0 && ldlAndVlDiff > 0) {
                        pendingViralLoadResult = true;
                    }
                }
                //Check whether patient has vl result for the most current vl test order no ldl
                if (lastViralLoadObs != null && lastLdlObs == null) {
                    lastViralLoadObsDate = lastViralLoadObs.getObsDatetime();
                    testAndvlResultDiff = minutesBetween(lastVlTestOrderDate, lastViralLoadObsDate);

                    if (testAndvlResultDiff > 0) {
                        hasCurrentVlResultAfterLastTestOrder = true;
                        pendingViralLoadResult = false;
                    }
                    if (testAndldlResultDiff < 0) {
                        pendingViralLoadResult = true;
                    }
                }
                //Check whether patient has ldl result for the most current vl test order no vl
                if (lastLdlObs != null && lastViralLoadObs == null) {
                    lastLdlObsDate = lastLdlObs.getObsDatetime();
                    testAndldlResultDiff = minutesBetween(lastVlTestOrderDate, lastLdlObsDate);
                    if (testAndldlResultDiff > 0 ) {
                        hasCurrentLDLResultAfterLastTestOrder = true;
                        pendingViralLoadResult = false;
                    }


                    if (testAndldlResultDiff < 0 ) {
                        pendingViralLoadResult = true;
                    }
                }
                if (lastViralLoadObs == null && lastLdlObs == null) {
                    pendingViralLoadResult = true;
                }
            }

                ret.put(ptId, new BooleanResult(pendingViralLoadResult, this));

            }
            return ret;
    }

    private int minutesBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Minutes.minutesBetween(d1, d2).getMinutes();

    }

    Obs getLatestVlOrder(List<Obs> lstObs) {
        Obs latestObs = null;
        for(Obs o:lstObs) {
            if(o.getValueCoded() == Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD)) {
                if(latestObs == null) {
                    latestObs =o;
                } else {
                    if (o.getObsDatetime().after(latestObs.getObsDatetime()) ) {
                        latestObs =o;
                    }
                }

            }
        }
        return latestObs;
    }
}
