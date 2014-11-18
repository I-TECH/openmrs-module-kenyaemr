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

package org.openmrs.module.kenyaemr.reporting.indicator.evaluator;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.indicator.HivCareVisitsIndicator;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.indicator.evaluator.IndicatorEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Evaluator for HIV care visit indicators
 */
@Handler(supports = HivCareVisitsIndicator.class)
public class HivCareVisitsIndicatorEvaluator implements IndicatorEvaluator {

	protected static final Log log = LogFactory.getLog(HivCareVisitsIndicatorEvaluator.class);

	@Autowired
	private KenyaEmrService kenyaEmrService;

	@Autowired
	private CommonCohortLibrary cohortLibrary;

	@Override
	public SimpleIndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException {
		HivCareVisitsIndicator visitIndicator = (HivCareVisitsIndicator) indicator;

		List<Form> hivCareForms = Arrays.asList(
			MetadataUtils.existing(Form.class, HivMetadata._Form.CLINICAL_ENCOUNTER_HIV_ADDENDUM),
			MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY)
		);

		Date fromDate = visitIndicator.getStartDate();
		Date toDate = DateUtil.getEndOfDayIfTimeExcluded(visitIndicator.getEndDate());

		Location defaultLocation = kenyaEmrService.getDefaultLocation();

		List<Encounter> hivCareEncounters = Context.getEncounterService().getEncounters(null, defaultLocation, fromDate, toDate, hivCareForms, null, null, null, null, false);
		List<Encounter> filtered = new ArrayList<Encounter>();

		if (HivCareVisitsIndicator.Filter.FEMALES_18_AND_OVER.equals(visitIndicator.getFilter())) {
			EvaluatedCohort females18AndOver = Context.getService(CohortDefinitionService.class).evaluate(
					ReportUtils.map(cohortLibrary.femalesAgedAtLeast18(), "effectiveDate", "${endDate}"), context
			);

			for (Encounter enc : hivCareEncounters) {
				if (females18AndOver.contains(enc.getPatient().getPatientId())) {
					filtered.add(enc);
				}
			}
		}
		else if (HivCareVisitsIndicator.Filter.SCHEDULED.equals(visitIndicator.getFilter())) {
			for (Encounter enc : hivCareEncounters) {
				if (wasScheduledVisit(enc)) {
					filtered.add(enc);
				}
			}
		}
		else if (HivCareVisitsIndicator.Filter.UNSCHEDULED.equals(visitIndicator.getFilter())) {
			for (Encounter enc : hivCareEncounters) {
				if (!wasScheduledVisit(enc)) {
					filtered.add(enc);
				}
			}
		}
		else {
			filtered = hivCareEncounters;
		}

		SimpleIndicatorResult result = new SimpleIndicatorResult();
		result.setIndicator(indicator);
		result.setContext(context);
		result.setNumeratorResult(filtered.size());

		return result;
	}

	/**
	 * Determines whether the given encounter was part of a scheduled visit
	 * @param encounter the encounter
	 * @return true if was part of scheduled visit
	 */
	private boolean wasScheduledVisit(Encounter encounter) {
		// Firstly look for a scheduled visit obs which has value = true
		Concept scheduledVisit = Dictionary.getConcept(Dictionary.SCHEDULED_VISIT);
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().equals(scheduledVisit) && obs.getValueAsBoolean()) {
				return true;
			}
		}

		Date visitDate = (encounter.getVisit() != null) ? encounter.getVisit().getStartDatetime() : encounter.getEncounterDatetime();
		Concept returnVisitDate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
		List<Obs> returnVisitObss = Context.getObsService().getObservationsByPersonAndConcept(encounter.getPatient(), returnVisitDate);

		for (Obs returnVisitObs : returnVisitObss) {
			if (DateUtils.isSameDay(returnVisitObs.getValueDate(), visitDate)) {
				return true;
			}
		}

		return false;
	}
}