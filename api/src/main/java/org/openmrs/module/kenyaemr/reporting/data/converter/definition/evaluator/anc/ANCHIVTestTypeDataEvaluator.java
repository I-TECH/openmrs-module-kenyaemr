package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.anc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCHIVStatusBeforeFirstANCDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCHIVTestTypeDataDefinition;
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
 *Evaluates a ANC enrollment Data Definition to produce a Parity
 */
@Handler(supports= ANCHIVTestTypeDataDefinition.class, order=50)
public class ANCHIVTestTypeDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select\n" +
                "       max(v.encounter_id),\n" +
                "       (case (SELECT count(encounter_id)  FROM kenyaemr_etl.etl_mch_antenatal_visit WHERE\n" +
                "           encounter_id != (SELECT MAX(v1.encounter_id) FROM kenyaemr_etl.etl_mch_antenatal_visit v1)\n" +
                "            and  final_test_result = \"Negative\")  when 0 then \"Initial\" else \"Retest\" end)\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
