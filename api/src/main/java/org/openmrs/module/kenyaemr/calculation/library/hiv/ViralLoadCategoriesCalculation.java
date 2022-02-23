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
import org.joda.time.Days;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.IsBreastFeedingCalculation;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastViralLoadResultCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;



import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;



import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;



public class ViralLoadCategoriesCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    private String vlLevel;
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    /**
     * Needs vl test calculation criteria
     * ----------------------------------
     * Immediately = Pregnant + Breastfeeding + Already on ART
     * After 3 months = unsuppressed ALL + Pregnant_Breastfeeding + Newly on ART
     * Aftre 6 months = Children (0-24) + Without first VL
     * After 12 months = suppressed
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */


    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        LastViralLoadResultCalculation lastVlResultCalculation = new LastViralLoadResultCalculation();
        CalculationResultMap lastVlResults = lastVlResultCalculation.evaluate(cohort, null, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort) {
            boolean needsViralLoadTest = false;
            String lastVlResult = null;
            String lastVlResultLDL = null;
            Double lastVlResultValue = null;
            Date lastVLResultDate = null;
            CalculationResult lastvlresult = lastVlResults.get(ptId);

            if (lastvlresult != null && lastvlresult.getValue() != null) {
                Object lastVl = lastvlresult.getValue();
                SimpleObject res = (SimpleObject) lastVl;
                lastVlResult = res.get("lastVl").toString();

                lastVLResultDate = (Date) res.get("lastVlDate");

                if(lastVlResult =="LDL"){
                    lastVlResultLDL = "LDL";
                }else{
                    lastVlResultValue =  Double.parseDouble(lastVlResult);
                }
            }
            //If VL level is above 1000 in the last 12 months, the patient is unsuppressed
            if (lastvlresult !=null && (lastVlResultLDL != null || lastVlResultValue >= 1000) && daysSince(lastVLResultDate,context) < 365){
                vlLevel = "Unsuppressed";
            }
            //If VL level is between 400 and 999 in the last 12 months, the patient has high viremia
            if (lastvlresult !=null && (lastVlResultLDL != null || (lastVlResultValue >= 400 && lastVlResultValue <= 999)) && daysSince(lastVLResultDate,context) < 365){
                vlLevel = "High Virema";
            }
            //If VL level is between 0 and 399 in the last 12 months, the patient has low viremia
            if (lastvlresult !=null && (lastVlResultLDL != null || (lastVlResultValue == 0 && lastVlResultValue <= 399)) && daysSince(lastVLResultDate,context) < 365){
                vlLevel = "Low Virema";
            }

            ret.put(ptId, new BooleanResult(true, this));
        }
        return ret;
    }

    @Override
    public String getFlagMessage() {
        return vlLevel;

    }
}