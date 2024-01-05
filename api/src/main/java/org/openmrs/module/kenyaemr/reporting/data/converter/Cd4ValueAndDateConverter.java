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

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converter for cd4
 */
public class Cd4ValueAndDateConverter implements DataConverter {

    private String what;

    public Cd4ValueAndDateConverter(String what) {
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

        Object value = ((CalculationResult) obj).getValue();
        Cd4ValueAndDate cd4ValueAndDate = (Cd4ValueAndDate) value;

        if(cd4ValueAndDate == null) {
            return  "";
        }
        if(what.equals("date")) {
            return formatDate(cd4ValueAndDate.getCd4Date());
        }
        if(what.equals("value")) {
            return cd4ValueAndDate.getCd4Value();

        }
        return  null;

    }

    @Override
    public Class<?> getInputDataType() {
        return CalculationResult.class;
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
