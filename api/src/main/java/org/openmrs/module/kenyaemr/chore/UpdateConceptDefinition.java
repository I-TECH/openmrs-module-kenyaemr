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
 * update specified concepts definition
 */
@Component("kenyaemr.chore.UpdateConceptDefinition")
public class UpdateConceptDefinition extends AbstractChore {

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updateConceptSql = "UPDATE concept \n" +
                "SET \n" +
                "    is_set = 1,\n" +
                "    class_id=10,\n" +
                "    datatype_id=4\n" +
                "WHERE concept_id in (165304,165305,165306,165307);";


        Context.getAdministrationService().executeSQL(updateConceptSql, false);


        out.println("Completed updating concept");

    }




}
