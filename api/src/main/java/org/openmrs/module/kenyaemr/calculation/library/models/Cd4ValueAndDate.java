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
