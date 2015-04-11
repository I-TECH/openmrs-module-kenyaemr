package org.openmrs.module.kenyaemr.calculation.library.cohort;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Add description of the class
 */
public class CohortReportUtil {

	static PatientCohortCategoryInfo getPatientCohortCategoryInfo (Date encounterDate, Date nextVisitDate) {
		if (encounterDate == null && nextVisitDate == null) {
			return new PatientCohortCategoryInfo();
		}
		if (nextVisitDate.before(encounterDate)){
			return new PatientCohortCategoryInfo();
		}

		long duration = nextVisitDate.getTime() - encounterDate.getTime();

		long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

		if (diffInDays >= 177) {
			return new PatientCohortCategoryInfo(6, "Month");
		}
		if (diffInDays >= 88) {
			return new PatientCohortCategoryInfo(3, "Month");
		}
		if (diffInDays >= 58) {
			return new PatientCohortCategoryInfo(2, "Month");
		}
		if (diffInDays >= 28) {
			return new PatientCohortCategoryInfo(1, "Month");
		}
		if (diffInDays < 28) {
			return new PatientCohortCategoryInfo(2, "Week");
		}
		return new PatientCohortCategoryInfo();
	}
}
