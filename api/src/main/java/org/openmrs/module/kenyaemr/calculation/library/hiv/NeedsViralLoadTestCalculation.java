package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
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
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentARTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
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
        //find the observation for viral load recorded
        CalculationResultMap viralLoad = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);
        //get a list of all the viral load
        CalculationResultMap viralLoadList = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);

        //find for prgnant females

        CalculationResultMap pregStatusObssEdd = Calculations.lastObs(Dictionary.getConcept(Dictionary.EXPECTED_DATE_OF_DELIVERY), aliveAndFemale, context);

        //get the initial art start date
        CalculationResultMap artStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        //find current art start date
        CalculationResultMap currentArtDate = calculate(new CurrentARTStartDateCalculation(), cohort, context);

        for(Integer ptId:cohort) {
            boolean needsViralLoadTest = false;
            Obs viralLoadObs = EmrCalculationUtils.obsResultForPatient(viralLoad, ptId);
            Date dateInitiated = EmrCalculationUtils.datetimeResultForPatient(artStartDate, ptId);
            ListResult listResult = (ListResult) viralLoadList.get(ptId);
            List<Obs> listObsViralLoads = CalculationUtils.extractResultValues(listResult);
            //find pregnancy obs
            Obs pregnantEdd = EmrCalculationUtils.obsResultForPatient(pregStatusObssEdd, ptId);
            //find the date this patient current art start date
            Date currentDate = EmrCalculationUtils.datetimeResultForPatient(currentArtDate, ptId);

            if(inHivProgram.contains(ptId) && onArt.contains(ptId)){
                if(listObsViralLoads.size() == 0 && dateInitiated != null && (daysSince(dateInitiated, context) > 180) && (daysSince(dateInitiated, context) < 360)) {
                    needsViralLoadTest = true;
                }

                //those continuing should receive one VL every year
                //pick the date of the last viral load
                if(viralLoadObs != null && (daysSince(viralLoadObs.getObsDatetime(), context) > 360)) {
                    needsViralLoadTest = true;
                }

                //if vl less than
                if(viralLoadObs != null && viralLoadObs.getValueNumeric() > 1000 && (daysSince(viralLoadObs.getObsDatetime(), context) > 90)) {
                    needsViralLoadTest = true;
                }

                //check for pregnancy
                if(pregnantEdd != null && viralLoadObs != null) {
                    //find a date 6 months ago from the context date
                    Calendar calendar9MonthsFromEdd = Calendar.getInstance();
                    calendar9MonthsFromEdd.setTime(pregnantEdd.getValueDatetime()); //set the calendar instance to the edd
                    calendar9MonthsFromEdd.add(Calendar.MONTH, -9); // get nine months off to estimate when thye got pregnant

                    //find if they might have taken any vl 6 months prior
                    Calendar calendar6MonthsFromConception = Calendar.getInstance();
                    calendar6MonthsFromConception.setTime(calendar9MonthsFromEdd.getTime());
                    calendar6MonthsFromConception.add(Calendar.MONTH, -6);

                    if(viralLoadObs.getObsDatetime().before(calendar6MonthsFromConception.getTime())) {
                        needsViralLoadTest = true;
                    }
                }

            }

            ret.put(ptId, new BooleanResult(needsViralLoadTest, this));
        }
        return  ret;

    }
}
