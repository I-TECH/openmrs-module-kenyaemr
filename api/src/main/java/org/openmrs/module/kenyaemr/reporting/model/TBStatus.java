package org.openmrs.module.kenyaemr.reporting.model;

import java.util.Date;

/**
 * Created by codehub on 20/03/15.
 */
public class TBStatus {
    private boolean onTreatment;
    private Date treatmentStartDate;

    public TBStatus(boolean onTreatment, Date treatmentStartDate) {
        this.onTreatment = onTreatment;
        this.treatmentStartDate = treatmentStartDate;
    }

    public boolean isOnTreatment() {
        return onTreatment;
    }

    public void setOnTreatment(boolean onTreatment) {
        this.onTreatment = onTreatment;
    }

    public Date getTreatmentStartDate() {
        return treatmentStartDate;
    }

    public void setTreatmentStartDate(Date treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }
}
