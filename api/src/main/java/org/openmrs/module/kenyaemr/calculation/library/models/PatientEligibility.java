package org.openmrs.module.kenyaemr.calculation.library.models;

import java.util.Date;

/**
 * A class that holds eligibility details for a patient
 */
public class PatientEligibility {

    private String criteria;
    private Date eligibilityDate;

    public PatientEligibility(String criteria, Date eligibilityDate) {
        this.criteria = criteria;
        this.eligibilityDate = eligibilityDate;
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

}
