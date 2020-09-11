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
 * updates order result date and order date stopped
 */
@Component("kenyaemr.chore.UpdateOrdersResultAndDateStopped")
public class UpdateOrdersResultAndDateStopped extends AbstractChore {

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updateResultDateSql = "UPDATE obs c\n" +
                "INNER JOIN orders e\n" +
                "ON e.patient_id = c.person_id\n" +
                "SET c.obs_datetime = e.date_activated\n" +
                "WHERE c.order_id = e.order_id and c.obs_datetime <> e.date_activated;";

        String updateDateStoppedSql = "UPDATE orders c\n" +
                "INNER JOIN obs e\n" +
                "ON e.person_id = c.patient_id\n" +
                "SET c.date_stopped = e.obs_datetime\n" +
                "WHERE c.order_id = e.order_id;";

        Context.getAdministrationService().executeSQL(updateResultDateSql, false);
        Context.getAdministrationService().executeSQL(updateDateStoppedSql, false);


        out.println("Completed updating orders result date");

    }




}
