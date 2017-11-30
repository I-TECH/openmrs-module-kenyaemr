package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInIptProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInTbProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.calculation.result.SimpleResult;

import java.util.Set;

/**
 * Calculates a consolidation of greencard validations such as :
 * In tb program
 * In IPT program
 * On ART
 *
 */
public class GreenCardCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(PatientInTbProgramCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> alive = Filters.alive(cohort, context);
        //Check whether in tb program
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

        Set<Integer> currentInIPT = CalculationUtils.patientsThatPass(calculate(new PatientInIptProgramCalculation(), cohort, context));

        // Get active ART regimen of each patient
        CalculationResultMap patientArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);

        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {

            boolean tbPatient = false;
            boolean inIPT = false;
            boolean onART = false;
            boolean patientInIPTProgram = false;
            boolean patientInTbProgram = false;
            boolean patientOnARV = false;

            if (currentInIPT.contains(ptId)) {
                patientInIPTProgram = true;
                //log.info("In IPT ==>" + patientInIPTProgram);
            }
            if (inTbProgram.contains(ptId)) {
                tbPatient = true;
               // log.info("In TB ==>" + tbPatient);
            }
            for (Map.Entry<Integer, CalculationResult> e : patientArvs.entrySet()) {
                onART = e.getValue() != null && !e.getValue().isEmpty();
                    //log.info("In ART ==>" + tbPatient);
            }

            if (tbPatient && patientInIPTProgram && onART) {
                ret.put(ptId, new SimpleResult(tbPatient, this));
            }
        }
        //log.info("Green card validations ==>");
        return ret;
    }

}