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

package org.openmrs.module.kenyaemr.reporting.data.patient.evaluator;

import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates an EncountersForPatientDataDefinition to produce a PatientData
 */
@Handler(supports = VisitsForPatientDataDefinition.class, order = 50)
public class VisitsForPatientDataEvaluator implements PatientDataEvaluator {

	/**
	 * @see PatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		VisitsForPatientDataDefinition def = (VisitsForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> m = new HashMap<String, Object>();

		hql.append("from 		Visit ");
		hql.append("where 		voided = false ");

		if (context.getBaseCohort() != null) {
			hql.append("and 		patient.patientId in (:patientIds) ");
			m.put("patientIds", context.getBaseCohort());
		}

		if (def.getTypes() != null && !def.getTypes().isEmpty()) {
			List<Integer> ids = new ArrayList<Integer>();
			for (VisitType visitType : def.getTypes()) {
				ids.add(visitType.getId());
			}
			hql.append("and		visitType.visitTypeId in (:ids) ");
			m.put("ids", ids);
		}

		if (def.getStartedOnOrAfter() != null) {
			hql.append("and		startDatetime >= :startedOnOrAfter ");
			m.put("startedOnOrAfter", def.getStartedOnOrAfter());
		}

		if (def.getStartedOnOrBefore() != null) {
			hql.append("and		startDatetime <= :startedOnOrBefore ");
			m.put("startedOnOrBefore", DateUtil.getEndOfDayIfTimeExcluded(def.getStartedOnOrBefore()));
		}

		hql.append("order by 	startDatetime " + (def.getWhich() == TimeQualifier.LAST ? "desc" : "asc"));

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);

		ListMap<Integer, Visit> visitsForPatients = new ListMap<Integer, Visit>();
		for (Object o : queryResult) {
			Visit v = (Visit) o;
			visitsForPatients.putInList(v.getPatient().getId(), v);
		}

		for (Integer pId : visitsForPatients.keySet()) {
			List<Visit> l = visitsForPatients.get(pId);
			if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
				c.addData(pId, l.get(0));
			}
			else {
				c.addData(pId, l);
			}
		}

		return c;
	}
}