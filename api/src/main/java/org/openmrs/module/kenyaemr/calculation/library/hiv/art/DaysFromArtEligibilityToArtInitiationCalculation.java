package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 16/07/15.
 */
public class DaysFromArtEligibilityToArtInitiationCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        CalculationResultMap ages = calculate(new AgeAtARTInitiationCalculation(), cohort, context);

        Set<Integer> female = Filters.female(cohort, context);

        CalculationResultMap hivEnrollmenMap = Calculations.firstEnrollments(hivProgram, cohort, context);
        CalculationResultMap tbEnrollmentMap = Calculations.firstEnrollments(tbProgram, cohort, context);


        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }


        CalculationResultMap allWhoStage = Calculations.allObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
        CalculationResultMap allWhoStageInitial = Calculations.allObs(Dictionary.getConcept(Dictionary.Initial_World_Health_Organization_HIV_stage), cohort, context);
        CalculationResultMap allCd4 = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
        //find hepatits status
        CalculationResultMap hepatitisMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), cohort, context);

        //find pregnant women
        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), female, context);
        //finding those at risk for hiv
        CalculationResultMap hivRiskFactor = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RISK_FACTOR), cohort, context);

        //find those who have status for tb
        CalculationResultMap tbStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);

        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for(Integer ptId: cohort) {
            Integer days = null;
            PatientEligibility patientEligibility = null;

            Obs hepatitisConcept = EmrCalculationUtils.obsResultForPatient(hepatitisMap, ptId);
            Obs isDiscodantCouple = EmrCalculationUtils.obsResultForPatient(hivRiskFactor, ptId);
            Obs hasTbConcpt = EmrCalculationUtils.obsResultForPatient(tbStatus, ptId);
            Obs pregnancyObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);

            ListResult allCd4ListResults = (ListResult) allCd4.get(ptId);
            List<Obs> obsList = CalculationUtils.extractResultValues(allCd4ListResults);
            ListResult allWhoListResults = (ListResult) allWhoStage.get(ptId);
            List<Obs> obsListWho = CalculationUtils.extractResultValues(allWhoListResults);

            ListResult allWhoStageInitialResults = (ListResult) allWhoStageInitial.get(ptId);
            List<Obs> allWhoStageInitialList = CalculationUtils.extractResultValues(allWhoStageInitialResults);

            //combine these 2 list for who into a new list
            List<Obs> newWHOList = new ArrayList<Obs>();
            newWHOList.addAll(obsListWho);
            newWHOList.addAll(allWhoStageInitialList);

            Integer ageInMonths = ((Integer) ages.get(ptId).getValue() * 12);
            PatientProgram hivEnrollment = EmrCalculationUtils.resultForPatient(hivEnrollmenMap, ptId);
            PatientProgram tbEnrollment = EmrCalculationUtils.resultForPatient(tbEnrollmentMap, ptId);

            Date artInitialDate = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);

            if(artInitialDate != null && hivEnrollment != null &&  outcomePeriod != null){

                if (pregnancyObs != null && pregnancyObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
                    patientEligibility = new PatientEligibility("Pregnant or breastfeeding", pregnancyObs.getObsDatetime());
                    if (artStartDate != null && pregnancyObs.getObsDatetime().after(artStartDate)) {
                        patientEligibility = new PatientEligibility("", artStartDate);
                    }
                } else if (hepatitisConcept != null && hepatitisConcept.getValueCoded().equals(Dictionary.getConcept(Dictionary.HEPATITIS_B))) {
                    patientEligibility = new PatientEligibility("HPV/HIV coinfection", hepatitisConcept.getObsDatetime());
                    if (artStartDate != null && hepatitisConcept.getObsDatetime().after(artStartDate)) {
                        patientEligibility = new PatientEligibility("", artStartDate);
                    }
                } else if ((tbEnrollment != null || (hasTbConcpt != null && hasTbConcpt.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED))) || (hasTbConcpt != null && hasTbConcpt.getValueCoded().equals(Dictionary.getConcept(Dictionary.ON_TREATMENT_FOR_DISEASE))))) {
                    if(tbEnrollment != null && artStartDate != null && (tbEnrollment.getDateEnrolled().before(artStartDate) || tbEnrollment.getDateEnrolled().equals(artStartDate))) {
                        patientEligibility = new PatientEligibility("TB/HIV co infection ", tbEnrollment.getDateEnrolled());
                    }
                    else if(hasTbConcpt != null && ((hasTbConcpt.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED))) || hasTbConcpt.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED)))) {
                        patientEligibility = new PatientEligibility("TB/HIV co infection ", hasTbConcpt.getObsDatetime());
                    }
                    else {
                        patientEligibility = new PatientEligibility("", artStartDate);
                    }
                } else if (isDiscodantCouple != null && isDiscodantCouple.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISCORDANT_COUPLE))) {
                    patientEligibility = new PatientEligibility("Discordant couple (HIV-negative partner)", isDiscodantCouple.getObsDatetime());
                    if (artStartDate != null && isDiscodantCouple.getObsDatetime().after(artStartDate)) {
                        patientEligibility = new PatientEligibility("", artStartDate);
                    }
                } else {
                    patientEligibility = getCriteriaAndDate(ageInMonths, obsList, newWHOList, artStartDate, hivEnrollment.getDateEnrolled(), outcomePeriod);

                }
                if(patientEligibility != null && patientEligibility.getEligibilityDate() != null) {
                    days = daysBetween(patientEligibility.getEligibilityDate(), artInitialDate);
                }
            }
            ret.put(ptId, new SimpleResult(days, this));
        }
        return  ret;
    }

    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Math.abs(Days.daysBetween(d1, d2).getDays());
    }

    PatientEligibility getCriteriaAndDate(int ageInMonths, List<Obs> cd4, List<Obs> whoStag, Date artStartDate, Date hivEnrollmentDate, int period) {
        PatientEligibility patientEligibility = null;
        if (ageInMonths <= 120 && hivEnrollmentDate.before(artStartDate)) {//children less than 10 years
            patientEligibility = new PatientEligibility("Age 10 years and below", hivEnrollmentDate);
        }
        else  if(ageInMonths <= 120 && artStartDate.before(hivEnrollmentDate)){
            patientEligibility = new PatientEligibility("Age 10 years and below", artStartDate);
        }
        else  if(ageInMonths <= 120 && artStartDate.equals(hivEnrollmentDate)){
            patientEligibility = new PatientEligibility("Age 10 years and below", hivEnrollmentDate);
        }

        else {
            patientEligibility = checkStartArtWithEalierstBetweenWhoAndCd4(whoStag,cd4, period, artStartDate);
        }

        return  patientEligibility;
    }

    Date whoDate(List<Obs> whoStage, Date artStartDate, int period) {
        Date whoStageDate = null;
        List<Obs> listOfWho = new ArrayList<Obs>();
        Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, period, DurationUnit.MONTHS), 1, DurationUnit.DAYS);

        if(whoStage.size() > 0) {

            for (Obs obsWhoStage : whoStage) {
                Integer stage = EmrUtils.whoStage(obsWhoStage.getValueCoded());

                if (stage != null && (stage == 3 || stage == 4)){

                    if(obsWhoStage.getObsDatetime().before(futureDate)){
                        listOfWho.add(obsWhoStage);
                    }

                }
            }
        }
        if(listOfWho.size() > 0) {
            whoStageDate = correctDateFormat(listOfWho.get(0).getObsDatetime());
        }

        return whoStageDate;

    }

    Date cd4Date(List<Obs> cd4, Date artStartDate, int period) {
        Date cd4Date = null;
        Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, period, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
        List<Obs> cd4Less500 = new ArrayList<Obs>();

        if(cd4.size() > 0) {

            for (Obs obsCd4 : cd4) {
                if (obsCd4.getValueNumeric() <= 500 && obsCd4.getObsDatetime().before(futureDate))  {
                    cd4Less500.add(obsCd4);
                }

            }
        }
        if(cd4Less500.size() > 0){
            cd4Date = correctDateFormat(cd4Less500.get(0).getObsDatetime());
        }

        return cd4Date;

    }

    Date correctDateFormat(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    PatientEligibility ealierstBetweenWhoAndCd4(List<Obs> whoStage, List<Obs> cd4,  Date artStartDate, int period){
        Date whoDate = whoDate(whoStage, artStartDate, period);
        Date cd4Date = cd4Date(cd4, artStartDate, period);
        PatientEligibility patientEligibility = null;
        //compare the 2 dates
        if(cd4Date == null && whoDate == null){
            patientEligibility = null;
        }
        if(cd4Date != null && whoDate == null){
            patientEligibility = new PatientEligibility("CD4 count<=500", cd4Date);
        }
        else if(cd4Date == null && whoDate != null){
            patientEligibility = new PatientEligibility("WHO stage = Stage IV", whoDate);
        }
        else if(whoDate != null && cd4Date != null &&  whoDate.before(cd4Date)){
            patientEligibility = new PatientEligibility("WHO stage = Stage IV", whoDate);
        }
        else if(cd4Date != null && whoDate != null  && cd4Date.before(whoDate)){
            patientEligibility = new PatientEligibility("CD4 count<=500", cd4Date);
        }
        else if(cd4Date != null && whoDate != null && cd4Date.equals(whoDate)){
            patientEligibility = new PatientEligibility("WHO stage = Stage IV", whoDate);
        }

        return  patientEligibility;
    }

    PatientEligibility checkStartArtWithEalierstBetweenWhoAndCd4(List<Obs> whoStage, List<Obs> cd4, int period, Date artStartDate){
        PatientEligibility patientEligibility = null;
        Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, period, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
        PatientEligibility patientEligibilityCriteria = ealierstBetweenWhoAndCd4(whoStage, cd4, artStartDate, period);

        if(patientEligibilityCriteria == null && artStartDate.before(futureDate)){
            patientEligibility = new PatientEligibility("", artStartDate);
        }
        else {
            if(patientEligibilityCriteria != null){
                if(artStartDate.before(patientEligibilityCriteria.getEligibilityDate())){
                    patientEligibility = new PatientEligibility("", artStartDate);
                }
                else {
                    patientEligibility = patientEligibilityCriteria;
                }
            }
        }
        return  patientEligibility;
    }


}