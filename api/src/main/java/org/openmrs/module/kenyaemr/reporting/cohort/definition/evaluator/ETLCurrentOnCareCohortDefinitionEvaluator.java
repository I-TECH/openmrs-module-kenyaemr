package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.CumulativeOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLCurrentOnCareCohortDefinition;
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
 * Evaluator for cumulative on ART
 */
@Handler(supports = {ETLCurrentOnCareCohortDefinition.class})
public class ETLCurrentOnCareCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		ETLCurrentOnCareCohortDefinition definition = (ETLCurrentOnCareCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry=" select distinct e.patient_id\n" +
				"from ( \n" +
				"select fup.visit_date,fup.patient_id,p.dob,p.Gender, min(e.visit_date) as enroll_date,\n" +
				"max(fup.visit_date) as latest_vis_date,\n" +
				"mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
				"p.unique_patient_no\n" +
				"from kenyaemr_etl.etl_patient_hiv_followup fup \n" +
				"join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id \n" +
				"join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id \n" +
				"where fup.visit_date <= :endDate \n" +
				"group by patient_id \n" +
//                "--  we may need to filter lost to follow-up using this\n" +
				"having (latest_tca>:endDate or \n" +
				"((latest_tca between :startDate and :endDate) or (latest_vis_date between :startDate and :endDate)) )\n" +
//                "-- drop missd completely\n" +
				") e\n" +
//                "-- drop discountinued\n" +
				"where e.patient_id not in (select patient_id from kenyaemr_etl.etl_patient_program_discontinuation \n" +
				"where date(visit_date) <= :endDate and program_name='HIV' \n" +
				"group by patient_id \n" +
				"having if(e.latest_tca>max(visit_date),1,0)=0) ";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("startDate", startDate);
		builder.addParameter("endDate", endDate);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
