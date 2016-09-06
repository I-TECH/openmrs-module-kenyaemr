package org.openmrs.module.kenyaemr.calculation.library.models;

import java.util.Date;

/**
 * Created by codehub on 24/06/15.
 */
public class LostToFU {

    private boolean isLost;
    private Date dateLost;

    public LostToFU(boolean isStopped, Date dateStopped) {
        this.dateLost = dateStopped;
        this.isLost = isStopped;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean isStopped) {
        this.isLost = isStopped;
    }

    public Object getDateLost() {
        return dateLost;
    }

    public void setDateLost(Date dateLost) {
        this.dateLost = dateLost;
    }
}