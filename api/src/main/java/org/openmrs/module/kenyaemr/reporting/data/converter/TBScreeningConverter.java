/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * converter for tb screening details
 */
public class TBScreeningConverter implements DataConverter {

    private String what;

    public TBScreeningConverter(){

    }

    public TBScreeningConverter(String what) {
        this.what = what;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "Missing";
        }

        String value = (String) obj;

        if(value == null) {
            return  "Missing";
        }

        Integer tbStatusCode = Integer.valueOf(value);


        if(what.equals("screeningDone")) {
            if (tbStatusCode.equals(160737)) {
                return "No";
            } else {
                return "Yes";
            }
        }

        if(what.equals("outcome")) {

            if (tbStatusCode != null) {
                if (tbStatusCode.equals(1660)) {
                    return "No TB";
                } else if (tbStatusCode.equals(164128)) {
                    return "INH";
                } else if (tbStatusCode.equals(142177)) {
                    return "Pr TB";
                } else if (tbStatusCode.equals(1662)) {
                    return "TB Confirmed";
                } else if (tbStatusCode.equals(1111)) {
                    return "TB Rx";
                }
            }
            return "Missing";

        }
        return  "Missing";

    }

    @Override
    public Class<?> getInputDataType() {
        return Object.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }

    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }
}
