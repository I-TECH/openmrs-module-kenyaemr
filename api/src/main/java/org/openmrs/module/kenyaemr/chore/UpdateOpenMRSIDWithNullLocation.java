/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

/**
 * update location id for openmrs identifier
 */
@Component("kenyaemr.chore.UpdateOpenMRSIDWithNullLocation")
public class UpdateOpenMRSIDWithNullLocation extends AbstractChore {

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updatePatientIdentifierLocSql = "update patient_identifier pi inner join patient_identifier_type it on pi.identifier_type = it.patient_identifier_type_id and it.uuid = 'dfacd928-0370-4315-99d7-6ec1c9f7ae76' " +
                "set pi.location_id = (select property_value from global_property where property='kenyaemr.defaultLocation') where pi.location_id is NULL;";


        Context.getAdministrationService().executeSQL(updatePatientIdentifierLocSql, false);


        out.println("Completed updating location id for Openmrs patient identifiers with NULL ids");

    }




}
