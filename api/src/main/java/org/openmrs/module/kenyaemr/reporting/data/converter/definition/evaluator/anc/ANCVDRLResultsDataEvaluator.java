package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.anc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCVDRLResultsDataDefinition;
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
 * Evaluates Definition if tested for syphilis test results
 */
@Handler(supports=ANCVDRLResultsDataDefinition.class, order=50)
public class ANCVDRLResultsDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select\n" +
                "      v.encounter_id,\n" +
                "     CONCAT_WS('\\r\\n',(case v.syphilis_test_status when 1229 then \"Yes\" when 1228 then \"Yes\" when 1304 then \"Yes\" when 1402 then \"No\" else \"Not Done\" end),\n" +
                "     (case v.syphilis_test_status when 1229 then \"Negative\" when 1228 then \"Positive\" when 1304 then \"N/A\" when 1402 then \"N/A\" else \"N/A\" end))as syphilis_done_results\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
