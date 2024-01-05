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

/**
 * converter for Missed appointments
 */
public class MissedAppointmentReasonsConverter implements DataConverter {

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        Integer value = (Integer) obj;

        if(value == null) {
            return  "";
        }

        else if(value.equals(165609)) {
            return "Client has covid-19 infection";
        }
        else if(value.equals(165610)) {
            return "COVID-19 restrictions";
        }
        else if(value.equals(164407)){
            return "Client refilled drugs from another facility";
        }
        else if(value.equals(159367)){
            return "Client has enough drugs";
        }
        else if(value.equals(162619)){
            return "Client travelled";
        }
        else if(value.equals(126240)){
            return "Client could not get an off from work/school";
        }
        else if(value.equals(160583)){
            return "Client is sharing drugs with partner";
        }
        else if(value.equals(162192)){
            return "Client forgot clinic dates";
        }
        else if(value.equals(164349)){
            return "Client stopped medications";
        }
        else if(value.equals(1654)){
            return "Client sick at home/admitted";
        }
        else if(value.equals(5622)){
            return "Other";
        }

        return  "";

    }

    @Override
    public Class<?> getInputDataType() {
        return Object.class;
    }

    @Override
    public Class<?> getDataType() {
        return Integer.class;
    }

}
