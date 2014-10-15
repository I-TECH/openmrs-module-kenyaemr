package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Patients who are in care and have at least 2 visits 3 months a part
 */
public class InCareHasAtLeast2VisitsCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		Set<Integer> alive = Filters.alive(cohort, context);
		Date onDate = context.getNow();// end of reporting period
		Date days90Ago;//variable to hold the date that will evaluated 90 days ago
		//calculate date 90 day ago
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(onDate);
		calendar.add(Calendar.DATE, -90);
		days90Ago = calendar.getTime();
		//declare the encounter types that are looked for
		EncounterType hivEnroll = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType hivConsult = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
		///////////////////////////////////////////////
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setName("has "+hivEnroll.getName()+" or"+hivConsult.getName()+" encounter between "+days90Ago+" and "+onDate);
		cd.setTimeQualifier(TimeQualifier.ANY);
		cd.setEncounterTypeList(Arrays.asList(hivEnroll, hivConsult));
		cd.setOnOrAfter(days90Ago);
		cd.setOnOrBefore(onDate);
		////////////////////////////////////////////
		EvaluatedCohort currentlyInCare = CalculationUtils.evaluateWithReporting(cd, alive, null, context);

		//find a list of patients
		List<Patient> patientStubs = new ArrayList<Patient>();
		for (Integer pid : currentlyInCare.getMemberIds()) {
			patientStubs.add(new Patient(pid));
		}

		//find patients who had at least 2 clinical visits
		// look for visits that started before endOfDay and ended after startOfDay
		List<Visit> visits = Context.getVisitService().getVisits(null, patientStubs, null, null, null, days90Ago, onDate, null, null, true, false);

		for (Integer ptId: cohort){
			boolean incare = false;
			if((currentlyInCare.contains(ptId)) && (visits.size() >= 2)){
				incare = true;
			}
			ret.put(ptId, new BooleanResult(incare, this, context));
		}
		return ret;
	}
}
