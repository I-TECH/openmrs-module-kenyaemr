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

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by codehub on 23/06/15.
 */
public class MedicallyEligibleConverter implements DataConverter {

    public MedicallyEligibleConverter(String which) {
        this.which = which;
    }

    public String getWhich() {
        return which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    private String which;

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        Object value = ((CalculationResult) obj).getValue();
        PatientEligibility eligibility = (PatientEligibility) value;

        if (eligibility == null) {
            return null;
        }

        if (which == null){
            return null;
        }

        if(which.equals("date") && eligibility.getEligibilityDate() != null){
            return formatDate(eligibility.getEligibilityDate());
        }

        if(which.equals("reason") && eligibility.getCriteria() != null) {
                return eligibility.getCriteria();
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