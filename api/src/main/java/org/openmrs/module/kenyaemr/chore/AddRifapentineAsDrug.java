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
 * Add rifapentine concept 83360 in drugs table
 */
@Component("kenyaemr.chore.AddRifapentineAsDrug")
public class AddRifapentineAsDrug extends AbstractChore {

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updateConceptSql = "INSERT INTO drug (concept_id, name,combination, dosage_form, route, creator, date_created,retired, uuid)\n" +
                "VALUES (83360,'RIFAPENTINE', 0, 161553, 1513, 1, now(), 0, '0cde7d0b-b68a-415b-95a4-b8039838e701');";


        Context.getAdministrationService().executeSQL(updateConceptSql, false);


        out.println("Completed adding rifapentine as a Drug in drug table concept");

    }




}
