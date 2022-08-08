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

import org.openmrs.OpenmrsMetadata;
import org.openmrs.Provider;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 06/07/15.
 */
public class EncounterProviderConverter implements DataConverter {

    @Override
    public Object convert(Object obj) {

        if (obj == null) {
            return "";
        }

        Provider encProvider = (Provider) obj;
        StringBuilder builder = new StringBuilder();
        if (encProvider != null) {
            String familyName = encProvider.getPerson().getFamilyName();
            String givenName = encProvider.getPerson().getGivenName();
            String middleName = encProvider.getPerson().getMiddleName();
            if (familyName != null) {
                builder.append(familyName).append(" ");
            }

            if (givenName != null) {
                builder.append(givenName).append(" ");
            }

            if (middleName != null) {
                builder.append(middleName);
            }
            return builder.toString();
        }

        return  "";

    }

    @Override
    public Class<?> getInputDataType() {
        return OpenmrsMetadata.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }
}
