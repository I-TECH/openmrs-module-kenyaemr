package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ANCDewormedDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ANCVLTestResultsDataDefinition;
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
 * Evaluates ANC VL Test results
 */
@Handler(supports= ANCVLTestResultsDataDefinition.class, order=50)
public class ANCVLTestResultsDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select\n" +
                "  v.encounter_id,\n" +
                "  (case l.test_result when 1302 then \"LDL\"  else l.test_result end) as viral_load\n" +
                "from  kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  left join\n" +
                "  kenyaemr_etl.etl_laboratory_extract l ON\n" +
                "   v.visit_date = l.visit_date GROUP BY v.encounter_id";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
