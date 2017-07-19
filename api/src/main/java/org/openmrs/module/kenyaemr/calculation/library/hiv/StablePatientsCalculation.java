package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
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
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaui.KenyaUiActivator;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * A calculation that returns patients who are stable
 * Eligibility criteria include:
 * have been on current regimen for >=12 months
 * no active OI
 * no missed appointments in the last 6 months
 * most recent vl < 1000 copies/ml
 * bmi >= 18.5
 * has completed 6 months ipt
 * age >= 20 years old
 * longer follow-up intervals (depends on clinician)
 *
 * Adds "Stable" flag on patient's dashboard
 */
public class StablePatientsCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Concept latestHeight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
        Concept latestWeight = Dictionary.getConcept(Dictionary.HEIGHT_CM);
        Concept latestVL = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
        Concept LDLQuestion = Context.getConceptService().getConcept(1305);
        Concept LDLAnswer = Context.getConceptService().getConcept(1302);

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        CalculationResultMap lastHeightObs = Calculations.lastObs(latestHeight, inHivProgram, context);
        CalculationResultMap lastWeightObs = Calculations.lastObs(latestWeight, inHivProgram, context);
        CalculationResultMap lastVLObs = Calculations.lastObs(latestVL, inHivProgram, context);
        CalculationResultMap lastLDLObs = Calculations.lastObs(LDLQuestion, inHivProgram, context);


        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            Double height = EmrCalculationUtils.numericObsResultForPatient(lastHeightObs, ptId);
            Double weight = EmrCalculationUtils.numericObsResultForPatient(lastWeightObs, ptId);
            Double vl = EmrCalculationUtils.numericObsResultForPatient(lastVLObs, ptId);

            Concept ldl = EmrCalculationUtils.codedObsResultForPatient(lastLDLObs, ptId);
            Double bmi= 0.0;
            Obs lastVLObsResult = null;

            boolean patientInHivProgram = false;
            boolean bmiAndAgeQualifier = false;
            boolean vlResultsQualifer = false;
            boolean patientActive = false;
            boolean stable = false;

            // get latest of ldl or vl
            if(ldl != null && vl != null) {
                Obs vlObs = EmrCalculationUtils.obsResultForPatient(lastVLObs, ptId);
                Obs ldlObs = EmrCalculationUtils.obsResultForPatient(lastLDLObs, ptId);
                lastVLObsResult = EmrCalculationUtils.findLastOnOrBefore(Arrays.asList(vlObs, ldlObs), context.getNow());
            } else if(ldl != null && vl == null) {
                lastVLObsResult = EmrCalculationUtils.obsResultForPatient(lastLDLObs, ptId);
            } else if (ldl == null && vl != null) {
                lastVLObsResult = EmrCalculationUtils.obsResultForPatient(lastVLObs, ptId);
            }
            // compute BMI
            if(height != null && weight != null) {
                double heightInMetres = height / 100;
                double heightSquared = heightInMetres * heightInMetres;
                bmi = weight / heightSquared;

            }

            // get patient's age
            Integer age = Context.getPatientService().getPatient(ptId).getAge(new Date());

            if(inHivProgram.contains(ptId)) {
                patientInHivProgram = true;
            }

            if(bmi != 0.0 && bmi >= 18.5 && age >= 20) {
                bmiAndAgeQualifier = true;
            }

            if(lastVLObsResult != null && ((lastVLObsResult.getConcept() == latestVL && lastVLObsResult.getValueNumeric() < 1000) || (lastVLObsResult.getConcept() == LDLQuestion && lastVLObsResult.getValueCoded() == LDLAnswer))) {
                vlResultsQualifer = true;
            }

            if(!ltfu.contains(ptId)) {
                patientActive = true;
            }

            if(patientInHivProgram && bmiAndAgeQualifier && vlResultsQualifer && patientActive)
                stable = true;


            ret.put(ptId, new BooleanResult(stable, this));
        }
        return ret;
    }

    @Override
    public String getFlagMessage() {
        return "Stable";
    }
}