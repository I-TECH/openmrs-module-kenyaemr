package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HIVTestTwoDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.VisitDateDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=HIVTestTwoDataDefinition.class, order=50)
public class HIVTestTwoDataEvaluator implements VisitDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedVisitData c = new EvaluatedVisitData(definition, context);

        String qry = "select v.visit_id, \n" +
                "(case o.value_coded when 703 then \"Positive\" when 664 then \"Negative\" when 163611 then \"Invalid\" else \"\" end) patientConsented\n" +
                "from visit v \n" +
                "inner join encounter e on e.visit_id = v.visit_id \n" +
                "inner join obs o on o.encounter_id = e.encounter_id and o.voided=0 \n" +
                "where o.concept_id = 1326 ";

        //we want to restrict visits to those for patients in question
        /*qry = qry + " and v.visit_id in (";
        qry = qry + context.getBaseCohort().getMemberIds();
        qry = qry + ") ";*/

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        System.out.println("Completed processing Date record created");
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
