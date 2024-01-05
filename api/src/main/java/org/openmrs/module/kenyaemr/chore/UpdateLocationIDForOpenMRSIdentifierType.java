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
 * Some OpenMRS ID type have null location_id and is affecting patient registration updates
 *
 */
@Component("kenyaemr.chore.UpdateLocationIDForOpenMRSIdentifierType")
public class UpdateLocationIDForOpenMRSIdentifierType extends AbstractChore {
    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updateSql = "update patient_identifier set location_id =  (select property_value from global_property where property='kenyaemr.defaultLocation');";
        Context.getAdministrationService().executeSQL(updateSql, false);
        out.println("Completed executing task that sets location_id for patient identifier");

    }
}
