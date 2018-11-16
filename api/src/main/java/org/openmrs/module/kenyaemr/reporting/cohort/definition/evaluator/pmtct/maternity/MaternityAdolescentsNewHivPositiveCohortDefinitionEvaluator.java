package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct.maternity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity.AdolescentsNewHIVPositiveAtMaternityCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity.MaternityClientsWithAPHCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLPmtctCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Evaluator for Adolescents discovered with HIV at Maternity
 */
@Handler(supports = {AdolescentsNewHIVPositiveAtMaternityCohortDefinition.class})
public class MaternityAdolescentsNewHivPositiveCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ETLMoh731CohortLibrary mohCohortLibrary;
    @Autowired
    private ETLPmtctCohortLibrary pmtctCohortLibrary;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        MaternityClientsWithAPHCohortDefinition definition = (MaternityClientsWithAPHCohortDefinition) cohortDefinition;
        String query = "select\n" +
                "    distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "       INNER JOIN kenyaemr_etl.etl_patient_demographics d ON\n" +
                "        d.patient_id = ld.patient_id\n" +
                "WHERE date(ld.visit_date)  between \"2018-01-01\" and \"2018-10-30\"\n" +
                "  and  timestampdiff(year,d.DOB,ld.visit_date) BETWEEN 10 AND 19\n" +
                "  and ld.final_test_result = \"Positive\";";
        SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(query);
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(calendar.MONTH);

        Map<String, Date> dateMap = EmrReportingUtils.getReportDates(thisMonth - 1);
        Date startDate = dateMap.get("startDate");
        Date endDate = dateMap.get("endDate");

        context.addParameterValue("startDate", startDate);
        context.addParameterValue("endDate", endDate);


        Cohort adolescentsHIVPositiveAtMaternity = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);


        return new EvaluatedCohort(adolescentsHIVPositiveAtMaternity, definition, context);
    }


}
