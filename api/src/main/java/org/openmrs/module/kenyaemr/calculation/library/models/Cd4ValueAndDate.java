package org.openmrs.module.kenyaemr.calculation.library.models;

import java.util.Date;

/**
 * Created by codehub on 18/06/15.
 */
public class Cd4ValueAndDate {

    private Double cd4Value;
    private Date cd4Date;

    public Cd4ValueAndDate(){}

    public Cd4ValueAndDate(Double cd4Value, Date cd4Date) {
        this.cd4Value = cd4Value;
        this.cd4Date = cd4Date;
    }

    public Double getCd4Value() {
        return cd4Value;
    }

    public void setCd4Value(Double cd4Value) {
        this.cd4Value = cd4Value;
    }

    public Date getCd4Date() {
        return cd4Date;
    }

    public void setCd4Date(Date cd4Date) {
        this.cd4Date = cd4Date;
    }
}
