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
 * For data that migrated from IQCare, an IQCare PK identifier type was assigned to keep the PK used in IQCare for tracing purposes.
 * This new ID type is searchable yet it is not shown on the UI.
 * This chore voids the id type so that it is not searchable and still preserve the entries in the db
 */
@Component("kenyaemr.chore.VoidIQCareIdentifierType")
public class VoidIQCareIdentifierType extends AbstractChore {

    /**
     * @see AbstractChore#perform(PrintWriter)
     */

    @Override
    public void perform(PrintWriter out) {
        String updatePreferredIdentifierSql = " update patient_identifier pi inner " +
                "join patient_identifier_type pit on pi.identifier_type = pit.patient_identifier_type_id and pit.uuid = 'b3d6de9f-f215-4259-9805-8638c887e46b' " +
                "set pi.voided = 1, pi.void_reason = 'Voiding to disable searchability';";


        Context.getAdministrationService().executeSQL(updatePreferredIdentifierSql, false);


        out.println("Completed voiding of IQCare PK identifier type");

    }




}
