package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.maternity;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.MaternityARVProphylaxisIssuedFromANCDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.MaternityARVProphylaxisToBabyAtMaternityDataDefinition;
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
@Handler(supports= MaternityARVProphylaxisToBabyAtMaternityDataDefinition.class, order=50)
public class MaternityARVProphylaxisToBabyAtMaternityDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "       patient_id,\n" +
                "       (case when etl_mchs_delivery.baby_azt_dispensed = 160123 then \"Yes\" when etl_mchs_delivery.baby_azt_dispensed  = 1066 then \"No\"\n" +
                "             when  etl_mchs_delivery.baby_nvp_dispensed = 80586 then \"Yes\" when etl_mchs_delivery.baby_nvp_dispensed = 80586 = 1066 then \"No\" when 1175 then \"N/A\" else \"\" end) as baby_prophlyaxis_given\n" +
                "from kenyaemr_etl.etl_mchs_delivery;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}