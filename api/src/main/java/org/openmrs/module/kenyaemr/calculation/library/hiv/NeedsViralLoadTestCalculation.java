package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 05/06/15.
 */
public class NeedsViralLoadTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

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
        CalculationResultMap ret = new CalculationResultMap();

        // need to exclude those on ART already
        Set<Integer> onArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
        //find the observation for viral load recorded
        CalculationResultMap viralLoad = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        //get the initial art start date
        CalculationResultMap artStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        for(Integer ptId:cohort) {
            boolean needsViralLoadTest = false;
            Double viralLoadValue = EmrCalculationUtils.numericObsResultForPatient(viralLoad, ptId);
            Date dateInitiated = EmrCalculationUtils.datetimeResultForPatient(artStartDate, ptId);
            Date sixMonthsAfterART;
            Date twelveMonthsAfterART;
            if(dateInitiated != null){

                Calendar sixMonthsTime = Calendar.getInstance();
                sixMonthsTime.setTime(dateInitiated);
                sixMonthsTime.add(Calendar.MONTH, 6);

                sixMonthsAfterART = sixMonthsTime.getTime();

                Calendar twelveMonthsTime = Calendar.getInstance();
                twelveMonthsTime.setTime(dateInitiated);
                twelveMonthsTime.add(Calendar.MONTH, 12);

                twelveMonthsAfterART = twelveMonthsTime.getTime();

            }
            if(inHivProgram.contains(ptId) && onArt.contains(ptId)){
                if(viralLoadValue == null) {
                    needsViralLoadTest = true;
                }
            }

            ret.put(ptId, new BooleanResult(needsViralLoadTest, this));
        }
        return  ret;

    }
}
