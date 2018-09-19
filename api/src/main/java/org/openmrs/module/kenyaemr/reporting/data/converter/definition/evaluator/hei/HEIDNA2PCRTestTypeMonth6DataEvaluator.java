package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIDNA2PCRTestTypeMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEISerialNumberDataDefinition;
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
@Handler(supports= HEIDNA2PCRTestTypeMonth6DataDefinition.class, order=50)
public class HEIDNA2PCRTestTypeMonth6DataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "f.patient_id,\n" +
                "  (case f.dna_pcr_contextual_status when 162081 then \"Repeat\" when 162083 then \"Final test (end of pediatric window)\" when 162082 then \"Confirmation\" when 162080 then \"Initial\" else \"\" end) as second_dna_pcr_type\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "  INNER JOIN etl_patient_demographics d ON\n" +
                "    d.patient_id = f.patient_id\n" +
                "WHERE round(DATEDIFF(f.visit_date,d.DOB)/7) =24\n" +
                "GROUP BY patient_id";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}