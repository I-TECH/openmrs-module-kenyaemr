package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCPostpartumVisitTimingDataDefinition;
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
 * PNC postpartum column ---No query
 */
@Handler(supports= PNCPostpartumVisitTimingDataDefinition.class, order=50)
public class PNCPostpartumVisitTimingDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select   v.encounter_id,\n" +
                "       (case when timestampdiff(day ,d.date_of_delivery,date(v.visit_date)) < 3 then 1\n" +
                "             when timestampdiff(day ,d.date_of_delivery,date(v.visit_date)) between 3 and 28 then 2\n" +
                "             when timestampdiff(day ,d.date_of_delivery,date(v.visit_date)) between 29 and 42 then 3\n" +
                "             when timestampdiff(day ,d.date_of_delivery,date(v.visit_date)) > 42 then 4 else \"\" end)\n" +
                "             as postpartum_timing\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "    inner   JOIN kenyaemr_etl.etl_mchs_delivery d ON d.patient_id = v.patient_id;\n";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
