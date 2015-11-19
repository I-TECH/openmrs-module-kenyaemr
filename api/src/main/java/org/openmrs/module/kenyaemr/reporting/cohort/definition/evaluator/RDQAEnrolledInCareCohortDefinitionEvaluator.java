package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQAEnrolledInCareCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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
@Handler(supports = {RDQAEnrolledInCareCohortDefinition.class})
public class RDQAEnrolledInCareCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private CommonCohortLibrary commonCohorts;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        RDQAEnrolledInCareCohortDefinition definition = (RDQAEnrolledInCareCohortDefinition) cohortDefinition;
        CohortDefinition cd = commonCohorts.enrolledExcludingTransfers(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV));//, "onOrAfter=${startDate},onOrBefore=${endDate}"
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        now.set(2002,1,1);
        Date startDate = now.getTime();

        context.addParameterValue("onOrAfter", startDate);
        context.addParameterValue("onOrBefore", today);

        Cohort enrolledInCare = Context.getService(CohortDefinitionService.class).evaluate(cd, context);


        return new EvaluatedCohort(enrolledInCare, definition, context);
    }


}
