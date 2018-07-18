package org.openmrs.module.kenyaemr.reporting.query.patientContact.evaluator;

import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.ActiveVisitQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.openmrs.module.reporting.query.visit.evaluator.VisitQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * The logic that evaluates an {@link ActiveVisitQuery} and produces a {@link VisitQueryResult}
 */
@Handler(supports=ActiveVisitQuery.class)
public class ActiveVisitQueryEvaluator implements VisitQueryEvaluator {

    @Autowired
    EvaluationService evaluationService;

    public VisitQueryResult evaluate(VisitQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        VisitQueryResult queryResult = new VisitQueryResult(definition, context);

        ActiveVisitQuery query = (ActiveVisitQuery) definition;
        Date asOfDate = query.getAsOfDate() != null ? query.getAsOfDate() : new Date();

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("v.visitId");
        q.from(Visit.class, "v");
        q.whereLessOrEqualTo("v.startDatetime", asOfDate);
        q.whereGreaterEqualOrNull("v.stopDatetime", asOfDate);
        q.whereVisitIn("v.visitId", context);

        List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
        queryResult.getMemberIds().addAll(results);
        return queryResult;
    }

}
