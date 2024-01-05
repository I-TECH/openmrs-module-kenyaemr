/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.NeedsViralLoadTestCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.PendingViralLoadResultCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;
public class ViralLoadCategoriesCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
        private String vlMessage;
        /*
        KHP3-525: Get the Last VL load- Categorize them into Unsuppressed (>1000), high viremia (400-999), low viremia (0-399)
        * If VL level is above 1000 in the last 12 months, the patient is unsuppressed
        *
        * If VL level is between 400 and 999 in the last 12 months, the patient has high viremia
        *
        * If VL level is between 0 and 399 in the last 12 months, the patient has low viremia
        * */
        @Override
        public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
                Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
                Set<Integer> alive = Filters.alive(cohort, context);
                Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
                Set<Integer> allOnArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));

                //Checks for ltfu
                Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
                //Checks for those due for VL; We should not categorize those patients who are already due for VL test
                Set<Integer> dueForVl = CalculationUtils.patientsThatPass(calculate(new NeedsViralLoadTestCalculation(), cohort, context));
                Set<Integer> pendingVLResult = CalculationUtils.patientsThatPass(calculate(new PendingViralLoadResultCalculation(), cohort, context));
                // All on ART already
                LastViralLoadResultCalculation lastVlResultCalculation = new LastViralLoadResultCalculation();
                CalculationResultMap lastVlResults = lastVlResultCalculation.evaluate(cohort, null, context);
                CalculationResultMap ret = new CalculationResultMap();
                for(Integer ptId:cohort) {
                        boolean eligibleForFlag = false;
                       if(!ltfu.contains(ptId) && inHivProgram.contains(ptId) && !dueForVl.contains(ptId) && !pendingVLResult.contains(ptId)){
                                String lastVlResult = null;
                                Double lastVlResultValue = null;
                                Date lastVLResultDate = null;
                                CalculationResult lastvlresult = lastVlResults.get(ptId);
                                if (lastvlresult != null && lastvlresult.getValue() != null) {
                                        Object lastVl = lastvlresult.getValue();
                                        SimpleObject res = (SimpleObject) lastVl;
                                        lastVlResult = res.get("lastVl").toString();
                                        lastVLResultDate = (Date) res.get("lastVlDate");
                                        if (daysSince(lastVLResultDate, context) <= 365) {
                                                if(lastVlResult =="LDL"){
                                                        vlMessage = "LDL";
                                                } else {
                                                        lastVlResultValue = Double.parseDouble(lastVlResult);
                                                        categorizeViralLoad(lastVlResultValue);
                                                }
                                                eligibleForFlag = true;
                                        }

                               }
                        }
                        ret.put(ptId, new BooleanResult(eligibleForFlag, this));

                }
                return ret;
        }

        private void categorizeViralLoad(Double lastVlResultValue) {
                if (lastVlResultValue >= 200.0) {// Unsuppressed category
                    if (lastVlResultValue >= 200.0 && lastVlResultValue <= 999.0) {
                        vlMessage = "High Risk LLV";
                    } else if (lastVlResultValue >= 1000.0) {
                        vlMessage = "Suspected Treatment Failure";
                    }
                } else if (lastVlResultValue >= 50.0 && lastVlResultValue <= 199.0) { // suppressed category
                        vlMessage = "Low Risk LLV";
                } else if (lastVlResultValue < 50.0) {
                        vlMessage = "LDL";
                }
        }

        @Override
        public String getFlagMessage() {
                return vlMessage;
        }
}