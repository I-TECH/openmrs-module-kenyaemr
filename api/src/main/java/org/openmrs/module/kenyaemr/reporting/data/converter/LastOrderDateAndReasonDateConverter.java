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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * handles last CD4 and/or VL order date and reason
 */
public class LastOrderDateAndReasonDateConverter implements DataConverter {

    private String what;

    public LastOrderDateAndReasonDateConverter(){

    }

    public LastOrderDateAndReasonDateConverter(String what) {
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
            return "";
        }

        String value = (String) obj;

        if(value == null) {
            return  "";
        }

        String orderDateStr = value.substring(0, 10);
        String orderReason = value.substring(10);
        if(what.equals("date")) {
            Date date1 = null;
            try {
                date1= new SimpleDateFormat("yyyy-MM-dd").parse(orderDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return formatDate(date1);
        }

        if(what.equals("reason")) {

            Integer reasonCode = Integer.valueOf(orderReason);
            if (reasonCode != null) {
                if (reasonCode.equals(843)) {
                    return "Clinical Treatment Failure";
                } else if (reasonCode.equals(1434)) {
                    return "Pregnancy";
                } else if (reasonCode.equals(162080)) {
                    return "Baseline";
                } else if (reasonCode.equals(162081)) {
                    return "Follow Up";
                } else if (reasonCode.equals(1259)) {
                    return "Single Drug Substitution";
                } else if (reasonCode.equals(159882)) {
                    return "Breastfeeding";
                } else if (reasonCode.equals(163523)) {
                    return "Clinical Failure";
                } else if (reasonCode.equals(161236)) {
                    return "Routine";
                }
            }
            return "";

        }
        return  "";

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
