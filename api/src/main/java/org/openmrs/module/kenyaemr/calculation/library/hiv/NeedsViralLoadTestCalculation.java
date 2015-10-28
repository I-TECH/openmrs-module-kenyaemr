package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.joda.time.DateTime;
import org.joda.time.Months;
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
        //check for non detectables
        CalculationResultMap ldlViralLoad = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
        //check for last ldl
        CalculationResultMap ldlLast = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);

        //find for prgnant females

        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);

        //get the initial art start date
        CalculationResultMap artStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for(Integer ptId:cohort) {
            boolean needsViralLoadTest = false;
            Obs viralLoadObs = EmrCalculationUtils.obsResultForPatient(viralLoad, ptId);
            Date dateInitiated = EmrCalculationUtils.datetimeResultForPatient(artStartDate, ptId);
            ListResult listResult = (ListResult) viralLoadList.get(ptId);
            List<Obs> listObsViralLoads = CalculationUtils.extractResultValues(listResult);
            ListResult ldlList = (ListResult) ldlViralLoad.get(ptId);
            List<List> listLdl = CalculationUtils.extractResultValues(ldlList);
            Obs lastLdlObs = EmrCalculationUtils.obsResultForPatient(ldlLast, ptId);
            //find pregnancy obs
            Obs pregnantEdd = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);

            if(inHivProgram.contains(ptId) && onArt.contains(ptId)){
                if(listObsViralLoads.size() == 0 && listLdl.size() == 0 && dateInitiated != null && (daysSince(dateInitiated, context) >= 180)) {
                    needsViralLoadTest = true;
                }
                //need to check here if patient has had any viral load
                /*if(dateInitiated != null && (daysSince(dateInitiated, context) >= 360)) {
                    needsViralLoadTest = true;
                }*/

                //those continuing should receive one VL every year
                //pick the date of the last viral load numeric values
                if(viralLoadObs != null && (daysSince(viralLoadObs.getObsDatetime(), context) >= 360)) {
                    needsViralLoadTest = true;
                }
                //pick the date of the ldl
                if(lastLdlObs != null && (daysSince(lastLdlObs.getObsDatetime(), context) >= 360)) {
                    needsViralLoadTest = true;
                }

                //if vl more than
                if(viralLoadObs != null && viralLoadObs.getValueNumeric() > 1000 && (daysSince(viralLoadObs.getObsDatetime(), context) > 90)) {
                    needsViralLoadTest = true;
                }

                //check for pregnancy
                if(pregnantEdd != null && pregnantEdd.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES)) && dateInitiated != null) {
                    Date whenVLWillBeDue = DateUtil.adjustDate(DateUtil.adjustDate(dateInitiated, 6, DurationUnit.MONTHS), -1, DurationUnit.DAYS);
                    //if last vl is 6 months older then the patient is due for vl
                        if(viralLoadObs == null && lastLdlObs == null ) {
                            needsViralLoadTest = true;
                        }
                        if(viralLoadObs == null && lastLdlObs == null && (context.getNow().after(whenVLWillBeDue))){
                            needsViralLoadTest = true;
                        }
                        if(viralLoadObs != null && viralLoadObs.getValueNumeric() > 1000 && (monthsBetween(viralLoadObs.getObsDatetime(), context.getNow()) >= 3)){
                            needsViralLoadTest = true;
                        }
                        if(viralLoadObs != null && daysSince(viralLoadObs.getObsDatetime(), context) >= 180) {
                            needsViralLoadTest = true;
                        }
                        if(lastLdlObs != null && daysSince(lastLdlObs.getObsDatetime(), context) >= 180) {
                            needsViralLoadTest = true;
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
