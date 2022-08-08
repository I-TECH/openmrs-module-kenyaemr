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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.List;

/**
 * Created by codehub on 18/08/15.
 */
public class IdentifierConverter implements DataConverter {

    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * returns all patient identifiers split by the common inter-cell split character
     */
    @Override
    public Object convert(Object original) {
        if (original == null)
            return "";

        List<PatientIdentifier> piList = (List<PatientIdentifier>) original;
        if(piList.size() > 0) {
            return piList.get(0).toString();
        }

        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }

}
