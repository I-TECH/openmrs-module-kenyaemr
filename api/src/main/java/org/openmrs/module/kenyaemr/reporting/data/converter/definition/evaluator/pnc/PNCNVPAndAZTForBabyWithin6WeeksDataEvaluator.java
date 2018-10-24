package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCModernFPWithin6WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCNVPAndAZTForBabyWithin6WeeksDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * PNC NVP and AZT for baby <=6 weeks column
 */
@Handler(supports= PNCNVPAndAZTForBabyWithin6WeeksDataDefinition.class, order=50)
public class PNCNVPAndAZTForBabyWithin6WeeksDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "       (case v.azt_dispensed when (v.azt_dispensed = 160123 or v.nvp_dispensed = 80586) then \"Yes\" when (v.azt_dispensed=1066 or v.nvp_dispensed = 1066) then \"No\"\n" +
                "                             when (v.azt_dispensed = 1175 or v.nvp_dispensed = 1175) then \"N/A\" else \"\" end) as azt_nvp_dispensed\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "       join kenyaemr_etl.etl_mchs_delivery d on d.patient_id = v.patient_id\n" +
                "where timestampdiff(week,d.date_of_delivery,date(v.visit_date))>6;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
