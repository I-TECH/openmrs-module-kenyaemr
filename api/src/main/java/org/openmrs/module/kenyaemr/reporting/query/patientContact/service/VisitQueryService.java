package org.openmrs.module.kenyaemr.reporting.query.patientContact.service;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;

/**
 * Interface for methods used to manage and evaluate Visit Queries
 */
public interface VisitQueryService extends DefinitionService<VisitQuery> {

    /**
     * @see DefinitionService#evaluate(Definition, EvaluationContext)
     */
    public VisitQueryResult evaluate(VisitQuery query, EvaluationContext context) throws EvaluationException;

    /**
     * @see DefinitionService#evaluate(Mapped, EvaluationContext)
     */
    public VisitQueryResult evaluate(Mapped<? extends VisitQuery> mappedQuery, EvaluationContext context) throws EvaluationException;

}
