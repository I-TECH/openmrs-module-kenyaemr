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

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * NUPI identifier had previously been used in the system. The identifier type has been repurposed and the number is currently being generated centrally by MOH
 * This chore is void the identifiers and pave way for new numbers
 */
@Component("kenyaemr.chore.VoidPreviousNUPIIdentifiers")
public class VoidPreviousNUPIIdentifiers extends AbstractChore {

    @Autowired
    private PatientService patientService;

    /**
     * @see AbstractChore#perform(PrintWriter)
     */
    @Override
    public void perform(PrintWriter output) {

        PatientIdentifierType nupi = patientService.getPatientIdentifierTypeByUuid(CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
        List<PatientIdentifier> patientIdentifiers = patientService.getPatientIdentifiers(null, Arrays.asList(nupi), null, null, null);
        Calendar cal = new GregorianCalendar(2022, 3, 1, 0, 0, 0);
        cal.setLenient(false);
        Date effectiveDate = cal.getTime();
        for (PatientIdentifier identifier : patientIdentifiers) {
            if (identifier.getDateCreated().before(effectiveDate)) {
                patientService.voidPatientIdentifier(identifier, "Clean up for new NUPI");
            }
        }
    }
}
