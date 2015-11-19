package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACurrentOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

/**
 * Evaluator for patients eligible for RDQA
 */
@Handler(supports = {RDQACurrentOnARTCohortDefinition.class})
public class RDQACurrentOnARTCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private Moh731CohortLibrary moh731Cohorts;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        RDQACurrentOnARTCohortDefinition definition = (RDQACurrentOnARTCohortDefinition) cohortDefinition;
        CohortDefinition cd = moh731Cohorts.currentlyOnArt();//, "fromDate=${startDate},toDate=${endDate}"
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        now.set(2002,1,1);
        Date startDate = now.getTime();

        context.addParameterValue("fromDate", startDate);
        context.addParameterValue("toDate", today);

        Cohort currentOnArt = Context.getService(CohortDefinitionService.class).evaluate(cd, context);


        return new EvaluatedCohort(currentOnArt, definition, context);
    }


}
