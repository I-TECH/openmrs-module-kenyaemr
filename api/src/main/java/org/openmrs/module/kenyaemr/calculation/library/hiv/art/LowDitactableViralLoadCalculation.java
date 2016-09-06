package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.Map;

/**
 * Created by codehub on 10/28/15.
 * This calculation returns the value of Viral load if Not detactable
 */
public class LowDitactableViralLoadCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

        return Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);
    }
}
