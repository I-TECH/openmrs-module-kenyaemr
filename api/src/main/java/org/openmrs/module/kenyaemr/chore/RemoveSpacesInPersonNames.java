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
 * This only affects data that was migrated from IQCare.
 *
 */
@Component("kenyaemr.chore.removeSpacesInPersonNames")
public class RemoveSpacesInPersonNames extends AbstractChore {
    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updateSql = " update person_name set given_name = REPLACE(given_name, CHAR(0), ''), middle_name = REPLACE(middle_name, CHAR(0), ''), family_name = REPLACE(family_name, CHAR(0), '');";
        Context.getAdministrationService().executeSQL(updateSql, false);

        out.println("Completed executing task that removes spaces in person name");

    }
}
