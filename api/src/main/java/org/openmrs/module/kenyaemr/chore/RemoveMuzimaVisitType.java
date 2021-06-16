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
 * This visit type was erroneously added to the database
 *
 */
@Component("kenyaemr.chore.removeMuzimaVisitType")
public class RemoveMuzimaVisitType extends AbstractChore {
    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updateSql = "update visit_type set retired = 1,date_retired=curdate(),retire_reason='erroneous entry' where name='mUzima Visit';";
        Context.getAdministrationService().executeSQL(updateSql, false);
        out.println("Completed executing task that removes muzima visit");

    }
}
