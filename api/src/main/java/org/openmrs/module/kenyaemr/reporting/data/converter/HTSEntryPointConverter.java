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
 * converter for HTS Entry Point to required abbreviateions status
 */
public class HTSEntryPointConverter implements DataConverter {

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        String value = (String) obj;

        if(value == null) {
            return  "";
        }

        else if(value.equals("5485")) {
            return "In Patient Department(IPD)";
        }
        else if(value.equals("160542")){
            return "Out Patient Department(OPD)";
        }
        else if(value.equals("162181")){
            return "Peadiatric Clinic";
        }
        else if(value.equals("160552")){
            return "Nutrition Clinic";
        }
        else if(value.equals("160538")){
            return "PMTCT ANC";
        }
        else if(value.equals("160456")){
            return "PMTCT MAT";
        }
        else if(value.equals("1623")){
            return "PMTCT PNC";
        }
        else if(value.equals("160541")){
            return "TB";
        }
        else if(value.equals("162050")){
            return "CCC";
        }
        else if(value.equals("159940")){
            return "VCT";
        }
        else if(value.equals("159938")){
            return "Home Based Testing";
        }
        else if(value.equals("159939")){
            return "Mobile Outreach";
        }
        else if(value.equals("162223")){
            return "VMMC";
        }
        else if(value.equals("160546")){
            return "STI Clinic";
        }
        else if(value.equals("160522")){
            return "Emergency";
        }
        else if(value.equals("163096")){
            return "Community Testing";
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
