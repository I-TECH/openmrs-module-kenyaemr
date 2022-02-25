package org.openmrs.module.kenyaemr.calculation.library.hiv.art;
        import org.apache.commons.logging.Log;
        import org.apache.commons.logging.LogFactory;
        import org.openmrs.calculation.patient.PatientCalculationContext;
        import org.openmrs.calculation.result.CalculationResult;
        import org.openmrs.calculation.result.CalculationResultMap;
        import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
        import org.openmrs.module.kenyacore.calculation.BooleanResult;
        import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
        import org.openmrs.module.kenyaemr.calculation.library.hiv.StablePatientsCalculation;
        import org.openmrs.ui.framework.SimpleObject;
        import java.util.Collection;
        import java.util.Date;
        import java.util.Map;


        import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;



        public class ViralLoadCategoriesCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
        private String vlLevel;
        protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
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