/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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