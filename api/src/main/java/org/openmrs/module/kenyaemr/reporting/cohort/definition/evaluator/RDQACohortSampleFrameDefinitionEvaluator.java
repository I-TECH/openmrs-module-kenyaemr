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
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACohortSampleFrameDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluator for  all patients to be sampled for RDQA
 */
@Handler(supports = {RDQACohortSampleFrameDefinition.class})
public class RDQACohortSampleFrameDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		RDQACohortSampleFrameDefinition definition = (RDQACohortSampleFrameDefinition) cohortDefinition;

        if (definition == null)
            return null;

		String qry = "select patient_id " +
				" from patient p " +
				"	inner join patient_identifier pi " +
				"	using(patient_id) " +
				" where identifier_type = 3 and p.voided = 0  ";

		SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition(qry);
		Cohort results = Context.getService(CohortDefinitionService.class).evaluate(sqlCohortDefinition, context);

		return new EvaluatedCohort(results, sqlCohortDefinition, context);
    }

}
