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

        String qry = "select av.encounter_id,(case t.final_test_result when  (t.final_test_result = 703 or t.final_test_result = 664\n" +
                "                                                      or  t.final_test_result = 1138)\n" +
                "and av.final_test_result is not null then \"Retest\" when (t.final_test_result is null\n" +
                "                                              and av.final_test_result is not null) then \"Initial\"\n" +
                "                                              when (t.final_test_result is null and av.final_test_result is null)\n" +
                "                                              then \"Not Done\" else \"Not done\" end) as Hiv_Test_Type\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit av inner join kenyaemr_etl.etl_hts_test t\n" +
                "on t.patient_id = av.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
