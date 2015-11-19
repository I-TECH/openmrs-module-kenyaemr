package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACummulativeOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
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
@Handler(supports = {RDQACummulativeOnARTCohortDefinition.class})
public class RDQACummulativeOnARTCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ArtCohortLibrary artCohorts;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        RDQACummulativeOnARTCohortDefinition definition = (RDQACummulativeOnARTCohortDefinition) cohortDefinition;
        CohortDefinition cd = artCohorts.startedArtExcludingTransferinsOnDate();//, "onOrBefore=${endDate}"
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();
;
        context.addParameterValue("onOrBefore", today);

        Cohort cumulativeOnArt = Context.getService(CohortDefinitionService.class).evaluate(cd, context);


        return new EvaluatedCohort(cumulativeOnArt, definition, context);
    }


}
