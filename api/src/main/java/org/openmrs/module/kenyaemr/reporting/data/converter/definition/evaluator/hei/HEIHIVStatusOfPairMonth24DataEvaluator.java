package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIHIVStatusMonth24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIHIVStatusOfPairMonth24DataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PersonDataDefinition
 */
@Handler(supports= HEIHIVStatusOfPairMonth24DataDefinition.class, order=50)
public class HEIHIVStatusOfPairMonth24DataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "  c.patient_id,\n" +
                "  mid(max(concat(visit_date, (case when c.disc_patient IS NOT NULL then \"INACTIVE\" else \"ACTIVE\" end))),11) as HIV_outcome\n" +
                "from kenyaemr_etl.etl_current_in_care c\n" +
                "  INNER JOIN kenyaemr_etl.etl_patient_demographics d ON\n" +
                "   d.patient_id = c.patient_id\n" +
                "WHERE round(DATEDIFF(c.visit_date,d.DOB)/7) = 96\n" +
                "GROUP BY patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}