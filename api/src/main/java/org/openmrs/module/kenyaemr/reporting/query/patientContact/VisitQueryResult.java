package org.openmrs.module.kenyaemr.reporting.query.patientContact;

import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;

/**
 * Result of an Evaluated Visit Query
 */
public class VisitQueryResult  extends VisitIdSet implements Evaluated<VisitQuery> {

    //***** PROPERTIES *****

    private VisitQuery definition;
    private EvaluationContext context;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public VisitQueryResult() {
        super();
    }

    /**
     * Full Constructor
     */
    public VisitQueryResult(VisitQuery definition, EvaluationContext context) {
        this.definition = definition;
        this.context = context;
    }

    //***** PROPERTY ACCESS *****

    /**
     * @return the definition
     */
    public VisitQuery getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(VisitQuery definition) {
        this.definition = definition;
    }

    /**
     * @return the context
     */
    public EvaluationContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(EvaluationContext context) {
        this.context = context;
    }


}
