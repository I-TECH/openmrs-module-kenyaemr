package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 10/26/15.
 * Calculate the tb soputum test date
 * @return sputum date
 */
public class NeedsTbSputumTestDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        // Get TB program
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

        // Get all patients who are alive and in TB program
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

        // Get concepts
        Concept tbsuspect = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
        Concept pulmonaryTb = Dictionary.getConcept(Dictionary.PULMONARY_TB);
        Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);
        Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept SPUTUM_FOR_ACID_FAST_BACILLI = Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI);

        // check if there is any observation recorded per the tuberculosis disease status
        CalculationResultMap lastObsTbDiseaseStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);

        // get last observations for disease classification, patient classification
        // and pulmonary tb positive to determine when sputum will be due for patients in future
        CalculationResultMap lastDiseaseClassiffication = Calculations.lastObs(Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE), inTbProgram, context);
        CalculationResultMap lastTbPulmonayResult = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE), inTbProgram, context);

        // get the first observation ever the patient had a sputum results for month 0
        CalculationResultMap lastSputumResults = Calculations.lastObs(SPUTUM_FOR_ACID_FAST_BACILLI, cohort, context);

        // get the date when Tb treatment was started, the patient should be in tb program to have this date
        CalculationResultMap tbStartTreatmentDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), cohort, context);

        //find only those patients who are due for tb sputum test
        Set<Integer> dueForTbSputumPatients = CalculationUtils.patientsThatPass(calculate(new NeedsTbSputumTestCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Date sputumDate = null;
            //find those patients who have positive sputum results
            Obs diseaseClassification = EmrCalculationUtils.obsResultForPatient(lastDiseaseClassiffication, ptId);
            Obs tbResults = EmrCalculationUtils.obsResultForPatient(lastTbPulmonayResult, ptId);
            Date treatmentStartDate = EmrCalculationUtils.datetimeObsResultForPatient(tbStartTreatmentDate, ptId);
            Obs lastObsTbDiseaseResults = EmrCalculationUtils.obsResultForPatient(lastObsTbDiseaseStatus, ptId);
            Obs lastSputumResultsObs = EmrCalculationUtils.obsResultForPatient(lastSputumResults, ptId);

            // check if a patient is alive
            if (dueForTbSputumPatients.contains(ptId)) {
                if ((lastObsTbDiseaseResults != null) && (lastObsTbDiseaseResults.getValueCoded().equals(tbsuspect)) && lastSputumResultsObs == null && !(inTbProgram.contains(ptId))) {
                    sputumDate = lastObsTbDiseaseResults.getObsDatetime();
                }
                else if(inTbProgram.contains(ptId) && diseaseClassification != null && tbResults != null && (diseaseClassification.getValueCoded().equals(pulmonaryTb)) && (tbResults.getValueCoded().equals(smearPositive)) && treatmentStartDate != null) {

                    if(lastSputumResultsObs != null && !(lastSputumResultsObs.getValueCoded().equals(NEGATIVE))) {

                        //get date after 2,4 and 6 months

                        //find first sputum results after 2 months. If the results is null activate the alert
                        Date months2 = DateUtil.adjustDate(treatmentStartDate, 2, DurationUnit.MONTHS);
                        CalculationResultMap resuts2Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months2, Arrays.asList(ptId), context);

                        //repeat after 4 months
                        Date months4 = DateUtil.adjustDate(treatmentStartDate, 4, DurationUnit.MONTHS);
                        CalculationResultMap resuts4Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months4, Arrays.asList(ptId), context);

                        //repeat for months 6
                        Date months6 = DateUtil.adjustDate(treatmentStartDate, 6, DurationUnit.MONTHS);
                        CalculationResultMap resuts6Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months6, Arrays.asList(ptId), context);

                        if(EmrCalculationUtils.obsResultForPatient(resuts2Months, ptId) == null && months2.before(context.getNow())) {
                            sputumDate = months2;
                        }

                        if (EmrCalculationUtils.obsResultForPatient(resuts4Months, ptId) == null && months4.before(context.getNow())) {
                            sputumDate = months4;
                        }

                        //repeat for 6 months

                        if(EmrCalculationUtils.obsResultForPatient(resuts6Months, ptId) == null && months6.before(context.getNow()) ) {
                            sputumDate = months6;
                        }
                    }
                }

            }
            ret.put(ptId, new SimpleResult(sputumDate, this, context));
        }
        return ret;
    }
}
