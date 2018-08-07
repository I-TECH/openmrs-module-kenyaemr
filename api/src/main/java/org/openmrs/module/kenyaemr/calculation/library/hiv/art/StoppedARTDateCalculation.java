package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 9/10/15.
 */
public class StoppedARTDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {


        Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);


        DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition("Completed");
        def.setDrugConceptSetsToInclude(Collections.singletonList(arvs));
        def.setCompletedOnOrBefore(context.getNow());
        CalculationResultMap orders = CalculationUtils.evaluateWithReporting(def, cohort, null, null, context);

        // Calculate the latest discontinued date of any of the orders for each patient
        CalculationResultMap latestDrugStopDates = latestStopDates(orders, context);
        Set<Integer> patientsWhoStoppedART = latestDrugStopDates.keySet();
        Set<Integer> patientsOnARTCurrently = CalculationUtils.patientsThatPass(calculate(new CurrentArtRegimenCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : patientsWhoStoppedART) {
            String stopDate = null;
            if (!(patientsOnARTCurrently.contains(ptId))) {

                CalculationResult latestDateResult = latestDrugStopDates.get(ptId);
                if (latestDateResult != null) {
                    stopDate =  latestDateResult.toString();
                }
            }
            ret.put(ptId, new SimpleResult(stopDate, this));
        }
        return ret;
    }

    private CalculationResultMap latestStopDates(CalculationResultMap orders, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        for (Map.Entry<Integer, CalculationResult> e : orders.entrySet()) {
            Integer ptId = e.getKey();
            ListResult result = (ListResult) e.getValue();
            Date latest = null;

            if (result != null) {
                for (SimpleResult r : (List<SimpleResult>) result.getValue()) {
                    if(((DrugOrder) r.getValue()).getDateStopped() != null) {
                        Date candidate = ((DrugOrder) r.getValue()).getDateStopped();
                        latest = CoreUtils.latest(latest, candidate);
                    }
                }
            }
            ret.put(ptId, latest == null ? null : new SimpleResult(latest, null));
        }
        return ret;
    }
}
