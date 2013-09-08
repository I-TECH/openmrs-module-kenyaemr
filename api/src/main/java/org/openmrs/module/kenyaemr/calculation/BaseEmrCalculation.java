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

package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.CalculationContext;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.*;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

/**
 * Base class for calculations we'll hand-write for this module.
 */
public abstract class BaseEmrCalculation extends BaseCalculation implements PatientCalculation {

	/**
	 * Evaluates ages of each patient
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the ages in a calculation result map
	 */
	protected static CalculationResultMap ages(Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		AgeDataDefinition def = new AgeDataDefinition();
		def.setEffectiveDate(calculationContext.getNow());
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the last encounter of a given type of each patient
	 * @param encounterType the encounter type
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the encounters in a calculation result map
	 */
	protected static CalculationResultMap lastEncounter(EncounterType encounterType, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		String encName = encounterType != null ? encounterType.getName() : "encounter";
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition("Last " + encName);
		if (encounterType != null) {
			def.addType(encounterType);
		}
		def.setWhich(TimeQualifier.LAST);
		def.setOnOrBefore(calculationContext.getNow());
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates all encounters of a given type of each patient
	 * @param encounterType the encounter type
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the encounters in a calculation result map
	 */
	protected static CalculationResultMap allEncounters(EncounterType encounterType, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		String encName = encounterType != null ? encounterType.getName() : "encounters";
		EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition("All " + encName);
		if (encounterType != null) {
			def.addType(encounterType);
		}
		def.setWhich(TimeQualifier.ANY);
		def.setOnOrBefore(calculationContext.getNow());
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the last obs of a given type of each patient
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the obss in a calculation result map
	 */
	protected static CalculationResultMap lastObs(Concept concept, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("Last " + concept.getPreferredName(CoreConstants.LOCALE), TimeQualifier.LAST, concept, calculationContext.getNow(), null);
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the first obs of a given type of each patient
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the obss in a calculation result map
	 */
	protected static CalculationResultMap firstObs(Concept concept, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("First " + concept.getPreferredName(CoreConstants.LOCALE), TimeQualifier.FIRST, concept, calculationContext.getNow(), null);
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the first obs of a given type of each patient
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the obss in a calculation result map
	 */
	protected static CalculationResultMap firstObsOnOrAfterDate(Concept concept, Date onOrAfter, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("First " + concept.getPreferredName(CoreConstants.LOCALE), TimeQualifier.FIRST, concept, calculationContext.getNow(), onOrAfter);
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the last obs of a given type of each patient that occurred at least the given number of days ago
	 * @param concept the obs' concept
	 * @param onOrBefore the number of days that must be elapsed between now and the observation
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the obss in a calculation result map
	 */
	protected static CalculationResultMap lastObsOnOrBeforeDate(Concept concept, Date onOrBefore, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		// Only interested in obs before now
		onOrBefore = EmrCalculationUtils.earliestDate(onOrBefore, calculationContext.getNow());

		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("Last " + concept.getPreferredName(CoreConstants.LOCALE) + " on or before " + onOrBefore,
				TimeQualifier.LAST, concept, onOrBefore, null);
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the last obs of a given type of each patient that occurred at least the given number of days ago
	 * @param concept the obs' concept
	 * @param atLeastDaysAgo the number of days that must be elapsed between now and the observation
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the obss in a calculation result map
	 */
	protected static CalculationResultMap lastObsAtLeastDaysAgo(Concept concept, int atLeastDaysAgo, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		Date onOrBefore = EmrCalculationUtils.dateAddDays(calculationContext.getNow(), -atLeastDaysAgo);
		return lastObsOnOrBeforeDate(concept, onOrBefore, cohort, calculationContext);
	}

	/**
	 * Evaluates all obs of a given type of each patient
	 * @param concept the obs' concept
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the obss in a calculation result map
	 */
	protected static CalculationResultMap allObs(Concept concept, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("All " + concept.getPreferredName(CoreConstants.LOCALE),
				TimeQualifier.ANY, concept, calculationContext.getNow(), null);
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the active program enrollment of the specified program
	 * @param program the program
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the enrollments in a calculation result map
	 */
	protected static CalculationResultMap activeEnrollment(Program program, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		return activeEnrollmentOnDate(program, calculationContext.getNow(), cohort, calculationContext);
	}

	/**
	 * Evaluates the last program enrollment on the specified program
	 * @param program the program
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the enrollments in a calculation result map
	 */
	protected static CalculationResultMap activeEnrollmentOnDate(Program program, Date onDate, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition("Active " + program.getName() + " enrollment");
		def.setWhichEnrollment(TimeQualifier.LAST);
		def.setProgram(program);
		def.setActiveOnDate(onDate);
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the all program enrollments on the specified program
	 * @param program the program
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the list results enrollments in a calculation result map
	 */
	protected static CalculationResultMap allProgramEnrollments(Program program, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition("All " + program.getName() + " enrollments");
		def.setWhichEnrollment(TimeQualifier.ANY);
		def.setProgram(program);
		def.setEnrolledOnOrBefore(calculationContext.getNow());
		return evaluateWithReporting(def, cohort, new HashMap<String, Object>(), null, calculationContext);
	}

	/**
	 * Evaluates the active drug orders for each patient
	 * @param medSet the medset concept that specifies which drugs to include
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the drug orders in a calculation result map
	 */
	protected static CalculationResultMap activeDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition("On " + medSet.getName().getName());
		def.setDrugConceptSetsToInclude(Collections.singletonList(medSet));
		def.setActiveOnDate(calculationContext.getNow());
		return evaluateWithReporting(def, cohort, null, null, calculationContext);
	}

	/**
	 * Evaluates all drug orders for each patient
	 * @param medSet the medset concept that specifies which drugs to include
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the drug orders in a calculation result map
	 */
	protected static CalculationResultMap allDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition("First " + medSet.getName().getName() + " start date");
		def.setDrugConceptSetsToInclude(Collections.singletonList(medSet));
		def.setStartedOnOrBefore(calculationContext.getNow());
		return evaluateWithReporting(def, cohort, null, null, calculationContext);
	}

	/**
	 * Evaluates the first drug orders for each patient
	 * @param medSet the medset concept that specifies which drugs to include
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the drug orders in a calculation result map
	 */
	protected static CalculationResultMap firstDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		// Get all drug orders
		CalculationResultMap orders = allDrugOrders(medSet, cohort, calculationContext);

		// Calculate the earliest start date of any of the orders for each patient
		CalculationResultMap earliestStartDates = earliestStartDates(orders, calculationContext);

		// Return only the drug orders that start on the earliest date
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : orders.keySet()) {
			ListResult allOrders = (ListResult) orders.get(ptId);
			ListResult earliestOrders = new ListResult();
			CalculationResult earliestDateResult = earliestStartDates.get(ptId);

			if (earliestDateResult != null) {
				Date earliestStartDate = (Date) earliestDateResult.getValue();

				for (SimpleResult r : (List<SimpleResult>) allOrders.getValue()) {
					DrugOrder order = (DrugOrder) r.getValue();
					if (order.getStartDate().equals(earliestStartDate)) {
						earliestOrders.add(new SimpleResult(order, null));
					}
				}
			}

			ret.put(ptId, earliestOrders);
		}
		return ret;
	}

	/**
	 * Filters a calculation result map to reduce results to booleans
	 * @param results the result map
	 * @return the reduced result map
	 */
	protected static CalculationResultMap passing(CalculationResultMap results) {
		CalculationResultMap ret = new CalculationResultMap();
		for (Map.Entry<Integer, CalculationResult> e : results.entrySet()) {
			ret.put(e.getKey(), new BooleanResult(ResultUtil.isTrue(e.getValue()), null));
		}
		return ret;
	}

	/**
	 * Evaluates the earliest start date of a set of drug orders
	 * @param orders the drug orders
	 * @param context the calculation context
	 * @return the start dates in a calculation result map
	 */
	protected static CalculationResultMap earliestStartDates(CalculationResultMap orders, PatientCalculationContext context) {
		CalculationResultMap ret = new CalculationResultMap();
		for (Map.Entry<Integer, CalculationResult> e : orders.entrySet()) {
			Integer ptId = e.getKey();
			ListResult result = (ListResult) e.getValue();
			Date earliest = null;

			if (result != null) {
				for (SimpleResult r : (List<SimpleResult>) result.getValue()) {
					Date candidate = ((DrugOrder) r.getValue()).getStartDate();
					earliest = EmrCalculationUtils.earliestDate(earliest, candidate);
				}
			}
			ret.put(ptId, earliest == null ? null : new SimpleResult(earliest, null));
		}
		return ret;
	}

	/**
	 * Evaluates a given calculation on each patient
	 * @param calculation the calculation
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the calculation result map
	 */
	protected static CalculationResultMap calculate(PatientCalculation calculation, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		return Context.getService(PatientCalculationService.class).evaluate(cohort, calculation, calculationContext);
	}

	/**
	 * Extracts only alive patients from a cohort
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the extracted patient ids
	 */
	protected static Set<Integer> alivePatients(Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		CalculationResultMap map = evaluateWithReporting(new VitalStatusDataDefinition(), cohort, new HashMap<String, Object>(), null, calculationContext);
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : map.entrySet()) {
			if (e.getValue() != null) {
				VitalStatus vs = ((VitalStatus) e.getValue().getValue());
				if (!vs.getDead() || OpenmrsUtil.compareWithNullAsEarliest(vs.getDeathDate(), calculationContext.getNow()) > 0) {
					ret.add(e.getKey());
				}
			}
		}
		return ret;
	}

	/**
	 * Convenience method to fetch a a concept by identifer
	 * @param identifier the concept identifier
	 * @return the concept
	 */
	protected static Concept getConcept(String identifier) {
		return MetadataUtils.getConcept(identifier);
	}

	/**
	 * Evaluates a data definition on each patient using a reporting context
	 * @param dataDefinition the data definition
	 * @param cohort the patient ids
	 * @param parameterValues the parameters for the reporting context
	 * @param calculation the calculation (optional)
	 * @param calculationContext the calculation context
	 * @return the calculation result map
	 */
	protected static CalculationResultMap evaluateWithReporting(DataDefinition dataDefinition, Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculation calculation, PatientCalculationContext calculationContext) {
		try {
			EvaluationContext reportingContext = ensureReportingContext(calculationContext, cohort, parameterValues);

			Map<Integer, Object> data;
			if (dataDefinition instanceof PersonDataDefinition) {
				EvaluatedPersonData result = Context.getService(PersonDataService.class).evaluate((PersonDataDefinition) dataDefinition, reportingContext);
				data = result.getData();
			} else if (dataDefinition instanceof PatientDataDefinition) {
				EvaluatedPatientData result = Context.getService(PatientDataService.class).evaluate((PatientDataDefinition) dataDefinition, reportingContext);
				data = result.getData();
			} else {
				throw new RuntimeException("Unknown DataDefinition type: " + dataDefinition.getClass());
			}
			CalculationResultMap ret = new CalculationResultMap();
			for (Integer ptId : cohort) {
				Object reportingResult = data.get(ptId);
				ret.put(ptId, toCalculationResult(reportingResult, calculation, calculationContext));
			}
			return ret;
		} catch (EvaluationException ex) {
			throw new APIException(ex);
		}
	}

	/**
	 * Evaluates a cohort definition on the given base set of patients using a reporting context
	 * @param cohortDefinition the cohort definition
	 * @param cohort the patient ids
	 * @param parameterValues the parameters for the reporting context
	 * @param calculationContext the calculation context
	 * @return the evaluated cohort
	 */
	protected static EvaluatedCohort evaluateWithReporting(CohortDefinition cohortDefinition, Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext calculationContext) {
		try {
			EvaluationContext reportingContext = ensureReportingContext(calculationContext, cohort, parameterValues);
			return Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, reportingContext);
		} catch (EvaluationException ex) {
			throw new APIException(ex);
		}
	}

	/**
	 * Convenience method to wrap a plain object in the appropriate calculation result subclass
	 * @param obj the plain object
	 * @param calculation the calculation (optional)
	 * @param calculationContext the calculation context
	 * @return the calculation result
	 */
	protected static CalculationResult toCalculationResult(Object obj, PatientCalculation calculation, PatientCalculationContext calculationContext) {
		if (obj == null) {
			return null;
		}
		else if (obj instanceof Obs) {
			return new ObsResult((Obs) obj, calculation, calculationContext);
		}
		else if (obj instanceof Collection) {
			ListResult ret = new ListResult();
			for (Object item : (Collection) obj) {
				ret.add(toCalculationResult(item, calculation, calculationContext));
			}
			return ret;
		} else {
			return new SimpleResult(obj, calculation, calculationContext);
		}
	}

	/**
	 * Returns the reporting {@link EvaluationContext} stored in calculationContext, creating and storing
	 * a new one if necessary.
	 *
	 * (Note: for now we never store this, and always return a new one)
	 *
	 * @param calculationContext the calculation context
	 * @param cohort the patient ids
	 * @param parameterValues the parameters for the reporting context
	 * @return the reporting evaluation context
	 */
	protected static EvaluationContext ensureReportingContext(PatientCalculationContext calculationContext, Collection<Integer> cohort, Map<String, Object> parameterValues) {
		EvaluationContext ret = new EvaluationContext();
		ret.setEvaluationDate(calculationContext.getNow());
		ret.setBaseCohort(new Cohort(cohort));
		ret.setParameterValues(parameterValues);
		calculationContext.addToCache("reportingEvaluationContext", ret);
		return ret;
	}

	/**
	 * Calculates the days since the given date
	 * @param date the date
	 * @param calculationContext the calculation context
	 * @return the number of days
	 */
	protected static int daysSince(Date date, CalculationContext calculationContext) {
		DateTime d1 = new DateTime(date.getTime());
		DateTime d2 = new DateTime(calculationContext.getNow().getTime());
		return Days.daysBetween(d1, d2).getDays();
	}
}