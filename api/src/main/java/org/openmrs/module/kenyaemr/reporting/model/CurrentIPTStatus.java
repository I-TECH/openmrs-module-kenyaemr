package org.openmrs.module.kenyaemr.reporting.model;

import java.util.Date;

/**
 * Returns information about current IPT treatment
 */
public class CurrentIPTStatus {

	private boolean currentlyOnTreatment;
	private Date curentTreatmentStartDate;

	public CurrentIPTStatus(boolean currentTreatmentStatus, Date curentTreatmentStartDate) {
		this.currentlyOnTreatment = currentTreatmentStatus;
		this.curentTreatmentStartDate = curentTreatmentStartDate;
	}

	public boolean isCurrentlyOnTreatment() {
		return currentlyOnTreatment;
	}

	public void setCurrentlyOnTreatment(boolean currentlyOnTreatment) {
		this.currentlyOnTreatment = currentlyOnTreatment;
	}

	public Date getCurentTreatmentStartDate() {
		return curentTreatmentStartDate;
	}

	public void setCurentTreatmentStartDate(Date curentTreatmentStartDate) {
		this.curentTreatmentStartDate = curentTreatmentStartDate;
	}
}
