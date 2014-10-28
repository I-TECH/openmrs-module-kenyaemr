package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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

		CalculationResultMap currentlyInCareHivEnroll = Calculations.allEncountersOnOrAfter(hivEnroll, days90Ago, alive, context);
		CalculationResultMap currentlyInCareHivConsult = Calculations.allEncountersOnOrAfter(hivConsult, days90Ago, alive, context);

		Set<Integer> currentlyInCareHivEnrollPatients = new HashSet<Integer>(currentlyInCareHivEnroll.keySet());
		Set<Integer> currentlyInCareHivConsultPatients = new HashSet<Integer>(currentlyInCareHivConsult.keySet());
		Set<Integer> allPatients = new HashSet<Integer>(currentlyInCareHivEnrollPatients);
		allPatients.addAll(currentlyInCareHivConsultPatients);

		//find patients who had at least 2 clinical visits
		// look for visits that started before endOfDay and ended after startOfDay

		for (Integer ptId: cohort){
			boolean incare = false;
			List<Visit> visits = Context.getVisitService().getVisits(null, Arrays.asList(Context.getPatientService().getPatient(ptId)), null, null, null, days90Ago, onDate, null, null, true, false);
			if((allPatients.contains(ptId)) && (visits.size() >= 2)){
				incare = true;
			}
			ret.put(ptId, new BooleanResult(incare, this, context));
		}
		return ret;
	}
}
