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
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.CalculationContext;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
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
public abstract class KenyaEmrCalculation extends BaseCalculation implements PatientCalculation {
	
	/**
	 * @return A user-friendly name to display, e.g. the label to use if this calculation represents an alert
	 */
	public abstract String getShortMessage();
	
	/**
	 * The default implementation delegates to {@link #getShortMessage()}
	 * 
	 * @return A possibly-more-detailed message than {@link #getShortMessage()} 
	 */
	public String getDetailedMessage() {
		return getShortMessage();
	}
	
	CalculationResultMap ages(Collection<Integer> patientIds, PatientCalculationContext calculationContext) {
		AgeDataDefinition def = new AgeDataDefinition();
		def.setEffectiveDate(calculationContext.getNow());
		return evaluateWithReporting(def, patientIds, new HashMap<String, Object>(), calculationContext);
	}
	
	CalculationResultMap lastObs(String conceptUuid, Collection<Integer> patientIds, PatientCalculationContext calculationContext) {
		Concept concept = getConcept(conceptUuid);
		if (concept == null) {
			throw new RuntimeException("Cannot find concept with uuid = " + conceptUuid);
		}
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("Last " + concept.getPreferredName(MetadataConstants.LOCALE), TimeQualifier.LAST, concept, calculationContext.getNow(), null);
		return evaluateWithReporting(def, patientIds, new HashMap<String, Object>(), calculationContext);
	}
	
	CalculationResultMap firstObs(String conceptUuid, Collection<Integer> patientIds, PatientCalculationContext calculationContext) {
		Concept concept = getConcept(conceptUuid);
		ObsForPersonDataDefinition def = new ObsForPersonDataDefinition("First " + concept.getPreferredName(MetadataConstants.LOCALE), TimeQualifier.FIRST, concept, calculationContext.getNow(), null);
		return evaluateWithReporting(def, patientIds, new HashMap<String, Object>(), calculationContext);
	}
	
	CalculationResultMap lastProgramEnrollment(Program program, Collection<Integer> patientIds, PatientCalculationContext calculationContext) {
		ProgramEnrollmentsForPatientDataDefinition def = new ProgramEnrollmentsForPatientDataDefinition("Last " + program.getName() + " enrollment");
		def.setWhichEnrollment(TimeQualifier.LAST);
		def.setProgram(program);
		def.setActiveOnDate(calculationContext.getNow());
		return evaluateWithReporting(def, patientIds, new HashMap<String, Object>(), calculationContext);
	}
	
	CalculationResultMap activeDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext context) {
    	DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition("On ART");
    	def.setDrugConceptSetsToInclude(Collections.singletonList(medSet));
    	def.setActiveOnDate(context.getNow());
    	return evaluateWithReporting(def, cohort, null, context);
    }
	
	CalculationResultMap calculate(PatientCalculation calculation, Collection<Integer> patientIds, PatientCalculationContext calculationContext) {
		return Context.getService(PatientCalculationService.class).evaluate(patientIds, calculation, calculationContext);
	}
	
	Set<Integer> patientsThatPass(CalculationResultMap map) {
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : map.entrySet()) {
			if (e.getValue() != null && !e.getValue().isEmpty()) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}
	
	Set<Integer> patientsThatDoNotPass(CalculationResultMap map) {
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : map.entrySet()) {
			if (e.getValue() == null || e.getValue().isEmpty()) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}
	
	Set<Integer> alivePatients(Collection<Integer> cohort, PatientCalculationContext context) {
		CalculationResultMap map = evaluateWithReporting(new VitalStatusDataDefinition(), cohort, new HashMap<String, Object>(), context);
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : map.entrySet()) {
			VitalStatus vs = ((VitalStatus) e.getValue().getValue());
			if (!vs.getDead() || OpenmrsUtil.compareWithNullAsEarliest(vs.getDeathDate(), context.getNow()) > 0) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}
	
	Obs obsResultForPatient(CalculationResultMap results, Integer ptId) {
		CalculationResult result = results.get(ptId);
		if (result != null && !result.isEmpty()) {
			Obs val = (Obs) result.getValue();
			return val;
		}
		return null;
	}
	
	Double numericObsResultForPatient(CalculationResultMap results, Integer ptId) {
		Obs o = obsResultForPatient(results, ptId);
		return o == null ? null : o.getValueNumeric();
	}
	
	Concept codedObsResultForPatient(CalculationResultMap results, Integer ptId) {
		Obs o = obsResultForPatient(results, ptId);
		return o == null ? null : o.getValueCoded();
	}	
	
	Concept getConcept(String uuid) {
		return Context.getConceptService().getConceptByUuid(uuid);
	}
	
	CalculationResultMap evaluateWithReporting(DataDefinition def, Collection<Integer> patientIds, Map<String, Object> parameterValues, PatientCalculationContext calculationContext) {
		try {
			EvaluationContext reportingContext = ensureReportingContext(calculationContext, patientIds, parameterValues);
	
			Map<Integer, Object> data;
			if (def instanceof PersonDataDefinition) {
				EvaluatedPersonData result = Context.getService(PersonDataService.class).evaluate((PersonDataDefinition) def, reportingContext);
				data = result.getData();
			} else if (def instanceof PatientDataDefinition) {
				EvaluatedPatientData result = Context.getService(PatientDataService.class).evaluate((PatientDataDefinition) def, reportingContext);
				data = result.getData();
			} else {
				throw new RuntimeException("Unknown DataDefinition type: " + def.getClass());
			}
			CalculationResultMap ret = new CalculationResultMap();
			for (Map.Entry<Integer, Object> e : data.entrySet()) {
				ret.put(e.getKey(), toCalculationResult(e.getValue(), calculationContext));
			}
			return ret;
		} catch (EvaluationException ex) {
			throw new APIException(ex);
		}
	}

    /**
     * Wraps a plain object in the appropriate calculation result subclass
     * 
     * @param o
     * @param ctx
     * @return
     */
    CalculationResult toCalculationResult(Object o, PatientCalculationContext ctx) {
	    if (o instanceof Obs) {
	    	return new ObsResult((Obs) o, this, ctx);
	    } else {
	    	return new SimpleResult(o, this, ctx);
	    }
    }

	/**
     * Returns the reporting {@link EvaluationContext} stored in calculationContext, creating and storing
     * a new one if necessary.
     *
     * (Note: for now we never store this, and always return a new one)
     * 
     * @param calculationContext
     * @param patientIds 
	 * @param parameterValues 
     * @return
     */
    EvaluationContext ensureReportingContext(PatientCalculationContext calculationContext, Collection<Integer> patientIds, Map<String, Object> parameterValues) {
	    EvaluationContext ret = new EvaluationContext();
    	ret.setEvaluationDate(calculationContext.getNow());
    	ret.setBaseCohort(new Cohort(patientIds));
    	ret.setParameterValues(parameterValues);
    	calculationContext.addToCache("reportingEvaluationContext", ret);
	    return ret;
    }
    
    int daysSince(Date date, CalculationContext ctx) {
    	DateTime d1 = new DateTime(date.getTime());
    	DateTime d2 = new DateTime(ctx.getNow().getTime());
    	return Days.daysBetween(d1, d2).getDays();
    }
	
}
