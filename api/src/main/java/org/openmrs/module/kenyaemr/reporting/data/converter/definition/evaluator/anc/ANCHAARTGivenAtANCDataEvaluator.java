package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.anc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCHAARTGivenAtANCDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCHeightDataDefinition;
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
 * Evaluates ANC HAART Given at ANC
 */
@Handler(supports= ANCHAARTGivenAtANCDataDefinition.class, order=50)
public class ANCHAARTGivenAtANCDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select\n" +
                "  e.encounter_id,\n" +
                "  (case d.date_started when \"\" then \"No\" else \"Yes\" end) as on_arv_before_first_anc\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_drug_event d on d.patient_id= e.patient_id\n" +
                "  inner join kenyaemr_etl.etl_mchs_delivery ld on d.patient_id= ld.patient_id\n" +
                "where d.date_started >= e.visit_date and d.date_started <=  ld.visit_date ;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
