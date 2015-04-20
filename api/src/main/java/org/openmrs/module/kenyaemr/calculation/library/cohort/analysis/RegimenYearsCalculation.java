package org.openmrs.module.kenyaemr.calculation.library.cohort.analysis;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationContext;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentARTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculates regimen per the 1 year(s) given
 */
public class RegimenYearsCalculation extends BaseEmrCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer years = (params != null && params.containsKey("years")) ? (Integer) params.get("years") : null;

        CalculationResultMap ret = new CalculationResultMap();

        Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);

        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);

        PatientCalculationContext context1 = patientCalculationService.createCalculationContext();
        context1.setNow(context.getNow());

        CalculationResultMap initialArtStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);


        for (Integer ptId : cohort) {

            Date initialRegimenDate = EmrCalculationUtils.resultForPatient(initialArtStartDate, ptId);
            CalculationResultMap currentReg;

            if(initialRegimenDate != null && years != null) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(initialRegimenDate);
                calendar.add(Calendar.YEAR, years);
                context.setNow(calendar.getTime());

                 currentReg = activeDrugOrders(arvs, Arrays.asList(ptId), context);

                ListResult result = (ListResult) currentReg.get(ptId);

                Calendar reportingTime = Calendar.getInstance();
                reportingTime.setTime(context1.getNow());


                if(result != null && (yearsSince(initialRegimenDate, reportingTime.getTime()) > (years * 12))) {

                    RegimenOrder regimen = new RegimenOrder(new HashSet<DrugOrder>(CalculationUtils.<DrugOrder>extractResultValues(result)));
                    ret.put(ptId, new SimpleResult(regimen, this, context));
                }

            }

            else {
                ret.put(ptId, null);
            }



        }

        return ret;
    }

    private  int yearsSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }


}
    