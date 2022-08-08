/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.DrugOrdersListForPatientDataDefinition;
import org.openmrs.module.reporting.common.DrugOrderSet;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Evaluates an DrugOrdersForPatientDataDefinition to produce a PatientData
 */
@Handler(supports=DrugOrdersListForPatientDataDefinition.class, order=50)
public class DrugOrdersListForPatientDataEvaluator implements PatientDataEvaluator {

    private static final String NEW_STOP_DATE_FIELD_NAME = "dateStopped";

    private static final String ORDER_STOP_DATE_FIELD_NAME = (ModuleUtil.compareVersion(
            OpenmrsConstants.OPENMRS_VERSION_SHORT, "1.10") > -1) ? NEW_STOP_DATE_FIELD_NAME : "discontinuedDate";

	private static final String ORDER_ACTIVATION_DATE_FIELD_NAME = (ModuleUtil.compareVersion(
	    OpenmrsConstants.OPENMRS_VERSION_SHORT, "1.10") > -1) ? "dateActivated" : "startDate";
	
	@Autowired
	EvaluationService evaluationService;

	/**
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return drug orders restricted by drug
	 * @should return drug orders restricted by drug concept
	 * @should return drug orders restricted by drug concept set
	 * @should return drug orders active on a particular date
	 * @should return drug orders started on or before a given date
	 * @should return drug orders started on or after a given date
	 * @should return drug orders completed on or before a given date
	 * @should return drug orders completed on or after a given date
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		DrugOrdersListForPatientDataDefinition def = (DrugOrdersListForPatientDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		SqlQueryBuilder qr = new SqlQueryBuilder();
		String qry = "select do.patient_id as patientId, p.order_id \n" +
				"from orders do \n" +
				"inner join drug_order p on p.order_id=do.order_id\n" +
				"where do.voided = 0 and \n" +
				"((do.order_action = 'DISCONTINUE' and do.order_reason_non_coded in ('previously existing orders','order fulfilled')) or do.order_action = 'NEW') and \n" +
				"do.concept_id in (:concepts) \n" +
				"and do.date_activated <= date(:endDate) and (do.auto_expire_date is null or do.auto_expire_date > date(:endDate)) and (do.date_stopped is null or do.date_stopped > date(:endDate));";
		qr.append(qry);



		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("do.patient.patientId", "do");
		q.from(DrugOrder.class, "do");
		q.wherePatientIn("do.patient.patientId", context);
        if (NEW_STOP_DATE_FIELD_NAME.equals(ORDER_STOP_DATE_FIELD_NAME)) {
            q.where("do.action != 'DISCONTINUE'");
        }

		List<Concept> concepts = null;

		if (def.getDrugConceptsToInclude() != null) {
			concepts = def.getDrugConceptsToInclude();
		}
		if (def.getDrugConceptSetsToInclude() != null) {
			if (concepts == null) {
				concepts = new ArrayList<Concept>();
			}
			for (Concept conceptSet : def.getDrugConceptSetsToInclude()) {
				if (conceptSet.isSet()) {
					concepts.addAll(conceptSet.getSetMembers());
				}
			}
		}

		if (def.getDrugsToInclude() != null && concepts != null) {
			q.startGroup();
			q.whereIn("do.drug", def.getDrugsToInclude());
			q.or();
			q.whereIn("do.concept", concepts);
			q.endGroup();
		}
		else if (def.getDrugsToInclude() != null) {
			q.whereIn("do.drug", def.getDrugsToInclude());
		}
		else if (concepts != null) {
			q.whereIn("do.concept", concepts);
		}
		
		if (def.getActiveOnDate() != null) {
			q.whereLessOrEqualTo("do." + ORDER_ACTIVATION_DATE_FIELD_NAME, def.getActiveOnDate());
			q.whereGreaterOrNull("do.autoExpireDate", def.getActiveOnDate());
			q.whereGreaterOrNull("do."+ORDER_STOP_DATE_FIELD_NAME, def.getActiveOnDate());
		}
		
		if (def.getStartedOnOrBefore() != null) {
			q.whereLessOrEqualTo("do." + ORDER_ACTIVATION_DATE_FIELD_NAME, def.getStartedOnOrBefore());
		}
		
		if (def.getStartedOnOrAfter() != null) {
			q.whereGreaterOrEqualTo("do." + ORDER_ACTIVATION_DATE_FIELD_NAME, def.getStartedOnOrAfter());
		}
		
		if (def.getCompletedOnOrBefore() != null) {
			q.startGroup();
			q.whereLessOrEqualTo("do.autoExpireDate", def.getCompletedOnOrBefore());
			q.or();
			q.whereLessOrEqualTo("do."+ORDER_STOP_DATE_FIELD_NAME, def.getCompletedOnOrBefore());
			q.endGroup();
		}

		if (def.getCompletedOnOrAfter() != null) {
			q.startGroup();
			q.whereGreaterOrEqualTo("do.autoExpireDate", def.getCompletedOnOrAfter());
			q.or();
			q.whereGreaterOrEqualTo("do."+ORDER_STOP_DATE_FIELD_NAME, def.getCompletedOnOrAfter());
			q.endGroup();
		}

		qr.addParameter("endDate", def.getActiveOnDate());
		qr.addParameter("concepts", concepts);
		OrderService os = Context.getOrderService();
		List<Object[]> results = evaluationService.evaluateToList(qr, context);
		for (Object[] row : results) {
			Integer pId = (Integer)row[0];
			Integer oId = (Integer) row[1];

			DrugOrder drugOrder = (DrugOrder)os.getOrder(oId);
			DrugOrderSet drugOrderSet = (DrugOrderSet)c.getData().get(pId);
			if (drugOrderSet == null) {
				drugOrderSet = new DrugOrderSet();
				c.addData(pId, drugOrderSet);
			}
			drugOrderSet.add(drugOrder);
		}
		
		return c;
	}
}
