package org.openmrs.module.kenyaemr.reporting.query.patientContact.service;

import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.openmrs.module.reporting.query.visit.service.VisitQueryService;

/**
 * Base Implementation of VisitQueryService
 */
public class VisitQueryServiceImpl extends BaseDefinitionService<VisitQuery> implements VisitQueryService {

    /**
     * @see DefinitionService#getDefinitionType()
     */
    public Class<VisitQuery> getDefinitionType() {
        return VisitQuery.class;
    }

    /**
     * @see DefinitionService#evaluate(Definition, EvaluationContext)
     * @should evaluate an encounter query
     */
    public VisitQueryResult evaluate(VisitQuery query, EvaluationContext context) throws EvaluationException {
        return (VisitQueryResult)super.evaluate(query, context);
    }

    /**
     * @see DefinitionService#evaluate(Mapped, EvaluationContext)
     */
    public VisitQueryResult evaluate(Mapped<? extends VisitQuery> mappedQuery, EvaluationContext context) throws EvaluationException {
        return (VisitQueryResult)super.evaluate(mappedQuery, context);
    }
}
