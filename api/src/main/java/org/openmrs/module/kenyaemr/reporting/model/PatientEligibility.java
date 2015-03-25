package org.openmrs.module.kenyaemr.reporting.model;

import java.util.Date;

/**
 * A class that holds eligibility details for a patient
 */
public class PatientEligibility {

	private String criteria;
	private Date eligibilityDate;
    private Double cd4Values;

    public PatientEligibility(String criteria, Date eligibilityDate, Double cd4Values) {
        this.criteria = criteria;
        this.eligibilityDate = eligibilityDate;
        this.cd4Values = cd4Values;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Date getEligibilityDate() {
        return eligibilityDate;
    }

    public void setEligibilityDate(Date eligibilityDate) {
        this.eligibilityDate = eligibilityDate;
    }

    public Double getCd4Values() {
        return cd4Values;
    }

    public void setCd4Values(Double cd4Values) {
        this.cd4Values = cd4Values;
    }
}
