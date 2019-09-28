/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DiffCareStableUnder4MonthstcaOver15MaleCohortDefinition;
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
 * Evaluator for IDU contacts linked to care
 */
@Handler(supports = {DiffCareStableUnder4MonthstcaOver15MaleCohortDefinition.class})
public class DiffCareStableUnder4MonthstcaOver15MaleCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		DiffCareStableUnder4MonthstcaOver15MaleCohortDefinition definition = (DiffCareStableUnder4MonthstcaOver15MaleCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry="select patient_id from(\n" +
				"                      select c.patient_id,f.stability stability,f.person_present patient_present,c.latest_vis_date latest_visit_date,f.visit_date fup_visit_date,c.latest_tca ltca,\n" +
				"                             c.Gender gender, c.dob dob\n" +
				"                      from kenyaemr_etl.etl_current_in_care c\n" +
				"                             inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
				"                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic where cic.stability=1\n" +
				"                                                                                                          and cic.patient_present = 978 and cic.gender =\"M\" and timestampdiff(year ,cic.dob,cic.latest_visit_date) >=15\n" +
				"                                                                                                          and timestampdiff(month,cic.latest_visit_date,cic.ltca) <4;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("endDate", endDate);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

		newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}
