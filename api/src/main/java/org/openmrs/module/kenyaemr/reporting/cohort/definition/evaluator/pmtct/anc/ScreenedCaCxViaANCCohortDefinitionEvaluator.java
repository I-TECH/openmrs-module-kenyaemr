package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct.anc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.anc.ScreenedCaCxViaANCCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for patients who screened for Cacx Via in ANC
 */


	@Handler(supports = {ScreenedCaCxViaANCCohortDefinition.class})
	public class ScreenedCaCxViaANCCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

		private final Log log = LogFactory.getLog(this.getClass());
		@Autowired
		private ETLMoh731GreenCardCohortLibrary moh731GreencardCohorts;
	@Autowired
	EvaluationService evaluationService;
		@Override
		public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

			ScreenedCaCxViaANCCohortDefinition definition = (ScreenedCaCxViaANCCohortDefinition) cohortDefinition;

			if (definition == null)
				return null;

			String qry = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.cacx_screening_method =162816;";

			Cohort newCohort = new Cohort();
			SqlQueryBuilder builder = new SqlQueryBuilder();
			builder.append(qry);
			Date startDate = (Date)context.getParameterValue("startDate");
			Date endDate = (Date)context.getParameterValue("endDate");
			builder.addParameter("endDate", endDate);
			builder.addParameter("startDate", startDate);
			List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

			newCohort.setMemberIds(new HashSet<Integer>(ptIds));


			return new EvaluatedCohort(newCohort, definition, context);
		}

}




