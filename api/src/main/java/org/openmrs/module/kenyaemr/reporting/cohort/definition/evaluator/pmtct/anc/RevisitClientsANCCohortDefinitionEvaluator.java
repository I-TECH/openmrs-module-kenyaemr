package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct.anc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.anc.NewClientsANCCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.anc.RevisitClientsANCCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.anc.ScreenedCaCxPapANCCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.evaluator.EncounterQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for patients for ANC Register
 */
@Handler(supports = {RevisitClientsANCCohortDefinition.class})
public class RevisitClientsANCCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	private ETLMoh731GreenCardCohortLibrary moh731GreencardCohorts;

	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		RevisitClientsANCCohortDefinition definition = (RevisitClientsANCCohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		String qry = "select  v.patient_id\n" +
				"from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"where date(v.visit_date) between (:startDate) and (:endDate)\n" +
				"GROUP BY v.patient_id\n" +
				"having count(v.visit_date) > 1";

		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(qry);
		Calendar calendar = Calendar.getInstance();
		int thisMonth = calendar.get(calendar.MONTH);

		Map<String, Date> dateMap = EmrReportingUtils.getReportDates(thisMonth - 1);
		Date startDate = dateMap.get("startDate");
		Date endDate = dateMap.get("endDate");

		context.addParameterValue("startDate", startDate);
		context.addParameterValue("endDate", endDate);

		Cohort revisitsANC = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);


		return new EvaluatedCohort(revisitsANC, definition, context);
	}
}


