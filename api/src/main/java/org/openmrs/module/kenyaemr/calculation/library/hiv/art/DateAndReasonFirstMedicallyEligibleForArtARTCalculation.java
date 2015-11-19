package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
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
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
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
 * Created by codehub on 9/14/15.
 */
public class DateAndReasonFirstMedicallyEligibleForArtARTCalculation extends AbstractPatientCalculation {



    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

        Set<Integer> female = Filters.female(cohort, context);

        CalculationResultMap hivEnrollmenMap = Calculations.firstEnrollments(hivProgram, cohort, context);
        CalculationResultMap tbEnrollmentMap = Calculations.firstEnrollments(tbProgram, cohort, context);
        CalculationResultMap ages = calculate(new AgeAtARTInitiationCalculation(), cohort, context);

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }


        CalculationResultMap allWhoStage = Calculations.allObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
        CalculationResultMap allCd4 = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap hepatitisMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), cohort, context);
        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), female, context);
        CalculationResultMap hivRiskFactor = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RISK_FACTOR), cohort, context);
        CalculationResultMap tbStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);
        EncounterType mchEnrollment = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
        CalculationResultMap enrollmentMap = Calculations.lastEncounter(mchEnrollment, female, context);

        for(Integer ptId: cohort) {

            PatientEligibility patientEligibility = null;

            Obs hepatitisConcept = EmrCalculationUtils.obsResultForPatient(hepatitisMap, ptId);
            Obs isDiscodantCouple = EmrCalculationUtils.obsResultForPatient(hivRiskFactor, ptId);
            Obs hasTbConcpt = EmrCalculationUtils.obsResultForPatient(tbStatus, ptId);
            Obs pregnancyObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
            Encounter pregnantEncounter = EmrCalculationUtils.encounterResultForPatient(enrollmentMap, ptId);

            ListResult allCd4ListResults = (ListResult) allCd4.get(ptId);
            List<Obs> obsList = CalculationUtils.extractResultValues(allCd4ListResults);
            ListResult allWhoListResults = (ListResult) allWhoStage.get(ptId);
            List<Obs> obsListWho = CalculationUtils.extractResultValues(allWhoListResults);

            Integer ageInMonths = ((Integer) ages.get(ptId).getValue() * 12);

            PatientProgram hivEnrollment = EmrCalculationUtils.resultForPatient(hivEnrollmenMap, ptId);
            PatientProgram tbEnrollment = EmrCalculationUtils.resultForPatient(tbEnrollmentMap, ptId);

            if(hivEnrollment != null) {
                if (pregnancyObs != null && pregnancyObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
                    patientEligibility = new PatientEligibility("Pregnant or breastfeeding", pregnancyObs.getObsDatetime());
                    if (artStartDate != null && pregnancyObs.getObsDatetime().after(artStartDate)) {
                        patientEligibility = new PatientEligibility("", artStartDate);
                    }
                }
                else if(pregnantEncounter != null){
                    patientEligibility = new PatientEligibility("Pregnant or breastfeeding", pregnantEncounter.getEncounterDatetime());
                }
                else if (hepatitisConcept != null && hepatitisConcept.getValueCoded().equals(Dictionary.getConcept(Dictionary.HEPATITIS_B))) {
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
                }
                else {
                    patientEligibility = getCriteriaAndDate(ageInMonths, obsList, obsListWho, artStartDate, hivEnrollment.getDateEnrolled());

                }
            }
            ret.put(ptId, new SimpleResult(patientEligibility, this));
        }

        return ret;
    }

    private PatientEligibility getCriteriaAndDate(int ageInMonths, List<Obs> cd4, List<Obs> whoStag, Date artStartDate, Date hivEnrollmentDate) {

        if (ageInMonths <= 120 && hivEnrollmentDate.before(artStartDate)) {//children less than 10 years
            return new PatientEligibility("Age 10 years and below", hivEnrollmentDate);
        }
        else  if(ageInMonths <= 120 && artStartDate.before(hivEnrollmentDate)){
            return new PatientEligibility("Age 10 years and below", artStartDate);
        }
        else  if(ageInMonths <= 120 && artStartDate.equals(hivEnrollmentDate)){
            return new PatientEligibility("Age 10 years and below", hivEnrollmentDate);
        }

        else if (ageInMonths > 120 && ageInMonths <= 180) {
            Date artStartDt = checkIfOnArtBeforeWho(artStartDate, whoStag);
            Date whoDate = whoDate(whoStag, artStartDate);

            if(artStartDt != null && whoDate != null && correctDateFormat(artStartDt).before(correctDateFormat(whoDate)) ) {
                return new PatientEligibility(null, artStartDate);
            }
            else if(artStartDt != null && whoDate != null && correctDateFormat(artStartDt).equals(correctDateFormat(whoDate))) {
                return new PatientEligibility("WHO stage = Stage IV", whoDate);
            }

            else if(artStartDt != null && whoDate != null && correctDateFormat(artStartDt).after(correctDateFormat(whoDate))) {
                return new PatientEligibility("WHO stage = Stage IV", whoDate);
            }

            else if(artStartDt == null && whoDate != null) {
                return new PatientEligibility("WHO stage = Stage IV", whoDate);
            }

            else if(artStartDt != null && whoDate == null) {
                return new PatientEligibility(null, artStartDate);
            }
        }

        else if (ageInMonths > 180){
            Date artStartDt = checkIfOnArtBeforeCd4(artStartDate, cd4);
            Date cd4Date = cd4Date(cd4, artStartDate);
            Date whoDate = whoDate(whoStag, artStartDate);

            if(artStartDt != null && cd4Date != null && correctDateFormat(artStartDt).before(correctDateFormat(cd4Date))) {
                return new PatientEligibility(null, artStartDate);
            }

            else if(artStartDt != null && whoDate != null && correctDateFormat(artStartDt).equals(correctDateFormat(whoDate))) {
                return new PatientEligibility("WHO stage = Stage IV", whoDate);
            }

            else if(artStartDt != null && whoDate != null && correctDateFormat(artStartDt).after(correctDateFormat(whoDate))) {
                return new PatientEligibility("WHO stage = Stage IV", whoDate);
            }

            else if(artStartDt == null && whoDate != null) {
                return new PatientEligibility("WHO stage = Stage IV", whoDate);
            }

            else if(artStartDt != null && cd4Date != null && correctDateFormat(artStartDt).equals(correctDateFormat(cd4Date))) {
                return new PatientEligibility("CD4 count<=500", cd4Date);
            }

            else if(artStartDt != null && cd4Date != null && correctDateFormat(artStartDt).after(correctDateFormat(cd4Date))) {
                return new PatientEligibility("CD4 count<=500", cd4Date);
            }

            else if(artStartDt == null && cd4Date != null) {
                return new PatientEligibility("CD4 count<=500", cd4Date);
            }
            else if(artStartDt != null && cd4Date == null) {
                return new PatientEligibility(null, artStartDate);
            }

        }

        return  null;
    }

    Date whoDate(List<Obs> whoStage, Date artStartDate) {
        Date whoStageDate = null;
        List<Obs> listOfWho = new ArrayList<Obs>();

        if(whoStage.size() > 0) {

            for (Obs obsWhoStage : whoStage) {
                Integer stage = EmrUtils.whoStage(obsWhoStage.getValueCoded());

                if (stage != null && (stage == 3 || stage == 4) && (obsWhoStage.getObsDatetime().before(artStartDate) || obsWhoStage.getObsDatetime().equals(artStartDate))) {
                    listOfWho.add(obsWhoStage);

                }
            }
        }
        if(listOfWho.size() > 0) {
            whoStageDate = correctDateFormat(listOfWho.get(0).getObsDatetime());
        }



        return whoStageDate;

    }

    Date cd4Date(List<Obs> cd4, Date artStartDate) {
        Date cd4Date = null;
        List<Obs> cd4Less500 = new ArrayList<Obs>();

        if(cd4.size() > 0) {

            for (Obs obsCd4 : cd4) {
                if (obsCd4.getValueNumeric() <= 500 && (obsCd4.getObsDatetime().before(artStartDate) || obsCd4.getObsDatetime().equals(artStartDate)) ) {
                    cd4Less500.add(obsCd4);
                }

            }
        }
        if(cd4Less500.size() > 0){
            cd4Date = correctDateFormat(cd4Less500.get(0).getObsDatetime());
        }



        return cd4Date;

    }

    Date checkIfOnArtBeforeWho(Date artDate, List<Obs> whoStage) {
        Date isOnARTDate = null;


        if(whoDate(whoStage, artDate) == null && artDate != null) {
            isOnARTDate = correctDateFormat(artDate);
        }

        else if(whoDate(whoStage, artDate) != null  && artDate != null && whoDate(whoStage, artDate).after(artDate)) {
            isOnARTDate = correctDateFormat(artDate);
        }

        return isOnARTDate;

    }

    Date checkIfOnArtBeforeCd4(Date artDate, List<Obs> cd4) {
        Date isOnARTDate = null;


        if(cd4Date(cd4, artDate) == null && artDate != null) {
            isOnARTDate = correctDateFormat(artDate);
        }

        else if(cd4Date(cd4, artDate) != null  && artDate != null && cd4Date(cd4, artDate).after(artDate)) {
            isOnARTDate = correctDateFormat(artDate);
        }

        return isOnARTDate;

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


}
