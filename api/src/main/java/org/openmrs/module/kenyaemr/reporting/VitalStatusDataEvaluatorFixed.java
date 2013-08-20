/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.reporting;

import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.Map;

/**
 * Temporary fix for REPORT-535
 */
@Handler(supports=VitalStatusDataDefinition.class, order=49)
public class VitalStatusDataEvaluatorFixed implements PersonDataEvaluator {

	/**
	 * @see PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return the vital status by person
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		Map<Integer, Object> deadData = qs.getPropertyValues(Person.class, "dead", context);
		Map<Integer, Object> deathDateData = qs.getPropertyValues(Person.class, "deathDate", context);
		Map<Integer, Object> causeOfDeathData = qs.getPropertyValues(Person.class, "causeOfDeath", context);

		for (Integer pId : deadData.keySet()) {
			Boolean dead = deadData.get(pId) == Boolean.TRUE;
			Date deathDate = null;
			Concept causeOfDeath = null;
			if (dead) {
				deathDate = (Date)deathDateData.get(pId);
				causeOfDeath = (Concept) causeOfDeathData.get(pId);
			}
			c.addData(pId, new VitalStatus(dead, deathDate, causeOfDeath));
		}

		return c;
	}
}