package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct.maternity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity.HivPositiveDeliveriesCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity.MaternityClientsWithAPHCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Evaluator for HIV Positive deliveries at maternity
 */
@Handler(supports = {HivPositiveDeliveriesCohortDefinition.class})
public class MaternityHIVPositiveDeliveriesCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
    @Autowired
    EvaluationService evaluationService;
    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ETLMoh731GreenCardCohortLibrary moh731GreenCardCohortLibrary;


    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        HivPositiveDeliveriesCohortDefinition definition = (HivPositiveDeliveriesCohortDefinition) cohortDefinition;
        if (definition == null)
            return null;

        String query = "select distinct ld.patient_id\n" +
                "                from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "                 left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= ld.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= ld.patient_id\n" +
                "                 where ld.final_test_result=\"Positive\" or hiv_status = 703 or v.final_test_result =\"Positive\" ;";

        Cohort newCohort = new Cohort();
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.append(query);
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        builder.addParameter("endDate", endDate);
        builder.addParameter("startDate", startDate);
        List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

        newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}