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
 * converter for HTS Strategy to required abbreviateions status
 */
public class HTSStrategyConverter implements DataConverter {

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        String value = (String) obj;

        if(value == null) {
            return  "";
        }

        else if(value.equals("164163")) {
            return "HP";
        }
        else if(value.equals("164953")){
            return "NP";
        }
        else if(value.equals("164954")){
            return "VI";
        }
        else if(value.equals("164955")){
            return "VS";
        }
        else if(value.equals("159938")){
            return "HB";
        }
        else if(value.equals("159939")){
            return "MO";
        }
        else if(value.equals("161557")){
            return "Index testing";
        }
        else if(value.equals("166606")){
            return "SNS";
        }
        else if(value.equals("5622")){
            return "O";
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

}
