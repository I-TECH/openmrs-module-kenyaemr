/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.ui.framework.SimpleObject;

import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converter for viral load object
 */
public class RDQASimpleObjectRegimenConverter implements DataConverter {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private String reportVariable;

    public String getReportVariable() {
        return reportVariable;
    }

    public void setReportVariable(String reportVariable) {
        this.reportVariable = reportVariable;
    }

    public RDQASimpleObjectRegimenConverter(String reportVariable) {
        this.reportVariable = reportVariable;
    }

    @Override
    public Object convert(Object original) {

        SimpleResult vlResult = (SimpleResult) original;


        if (vlResult == null)
            return "Missing";

        SimpleObject vlObject =  (SimpleObject) vlResult.getValue();

        if (vlObject == null)
            return "Missing";

        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

        if (reportVariable.equals("data")) {
            if (vlObject.get("vl") != null && !vlObject.get("vl").equals("")) {
                return vlObject.get("vl");

            } else {
                return "Missing";
            }
        } else if (reportVariable.equals("date")) {
            return vlObject.get("vlDate") != null && !vlObject.get("vlDate").equals("") ? df.format((Date)vlObject.get("vlDate")) : "Missing";
        } else {
            return null;
        }

    }
    @Override
    public Class<?> getInputDataType() {
        return SimpleResult.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }

}
