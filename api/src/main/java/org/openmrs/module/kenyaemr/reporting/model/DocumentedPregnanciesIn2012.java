package org.openmrs.module.kenyaemr.reporting.model;

import java.util.Date;

/**
 * Calculates the pregnancies of the patient in 2012
 */
public class DocumentedPregnanciesIn2012 {

    private boolean isPregnant;
    private Date edd;

    public DocumentedPregnanciesIn2012(boolean isPregnant, Date edd) {
        this.isPregnant = isPregnant;
        this.edd = edd;
    }

    public boolean isPregnant() {
        return isPregnant;
    }

    public void setPregnant(boolean isPregnant) {
        this.isPregnant = isPregnant;
    }

    public Date getEdd() {
        return edd;
    }

    public void setEdd(Date edd) {
        this.edd = edd;
    }
}
