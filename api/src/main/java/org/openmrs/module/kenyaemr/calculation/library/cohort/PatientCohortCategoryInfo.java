package org.openmrs.module.kenyaemr.calculation.library.cohort;

/**
 * Holds data for next visit.
 */
public class PatientCohortCategoryInfo {
	private Integer cohort;
	private String unit;

	public PatientCohortCategoryInfo() {
	}

	public PatientCohortCategoryInfo(Integer cohort, String unit) {
		this.cohort = cohort;
		this.unit = unit;
	}

	public Integer getCohort() {
		return cohort;
	}

	public void setCohort(Integer cohort) {
		this.cohort = cohort;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
