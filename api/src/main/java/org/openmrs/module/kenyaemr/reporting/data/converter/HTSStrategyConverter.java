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

        else if(value.equals("HP:Provider Initiated Testing(PITC)")) {
            return "HP";
        }
        else if(value.equals("NP: HTS for non-patients")){
            return "NP";
        }
        else if(value.equals("VI:Integrated VCT Center")){
            return "VI";
        }
        else if(value.equals("Stand Alone VCT Center")){
            return "VS";
        }
        else if(value.equals("Home Based Testing")){
            return "HB";
        }
        else if(value.equals("MO: Mobile Outreach HTS")){
            return "MO";
        }
        else if(value.equals("Index testing")){
            return "Index testing";
        }
        else if(value.equals("SNS - Social Networks")){
            return "SNS";
        }
        else if(value.equals("Other")){
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
