package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQAScreenedForTBCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary;
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
@Handler(supports = {RDQAScreenedForTBCohortDefinition.class})
public class RDQAScreenedForTBCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private TbCohortLibrary tbCohorts;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        RDQAScreenedForTBCohortDefinition definition = (RDQAScreenedForTBCohortDefinition) cohortDefinition;
        CohortDefinition cd = tbCohorts.screenedForTbAndHivPositive();//, "onOrAfter=${startDate},onOrBefore=${endDate}
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        now.set(2002,1,1);
        Date startDate = now.getTime();

        context.addParameterValue("onOrAfter", startDate);
        context.addParameterValue("onOrBefore", today);

        Cohort screenedForTB = Context.getService(CohortDefinitionService.class).evaluate(cd, context);


        return new EvaluatedCohort(screenedForTB, definition, context);
    }


}
