package org.openmrs.module.kenyaemr.calculation.library.models;

import java.util.Date;

/**
 * Created by codehub on 27/08/15.
 */
public class TransferInAndDate {

    private String state;
    private Date date;

    public TransferInAndDate(String state, Date date) {
        this.state = state;
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
