package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by codehub on 9/7/15.
 */
public class CurrentArtRegimenCohortAnalysisCalculation extends BaseEmrCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        CalculationResultMap ret = new CalculationResultMap();

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }
        Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
        CalculationResultMap currentARVDrugOrders = activeDrugOrders(arvs, cohort, context);

        for (Integer ptId : cohort) {
            ListResult patientDrugOrders = (ListResult) currentARVDrugOrders.get(ptId);

            if (patientDrugOrders != null) {
                RegimenOrder regimen = new RegimenOrder(new HashSet<DrugOrder>(CalculationUtils.<DrugOrder>extractResultValues(patientDrugOrders)));
                ret.put(ptId, new SimpleResult(regimen, this, context));
            }
            else {
                ret.put(ptId, null);
            }
        }
        return ret;

    }
}
