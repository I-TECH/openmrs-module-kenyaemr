package org.openmrs.module.kenyaemr.reporting.model;

import java.util.Date;

/**
 * Created by codehub on 30/03/15.
 */
public class EarliestCd4 {

    private Date obsDate;
    private Double value;


    public EarliestCd4(Date obsDate, Double value) {
        this.obsDate = obsDate;
        this.value = value;
    }

    public Date getObsDate() {
        return obsDate;
    }

    public void setObsDate(Date obsDate) {
        this.obsDate = obsDate;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
