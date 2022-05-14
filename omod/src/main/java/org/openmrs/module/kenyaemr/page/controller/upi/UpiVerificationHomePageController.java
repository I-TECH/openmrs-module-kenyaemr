/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.upi;

import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.List;

@AppPage(EmrConstants.APP_REGISTRATION)
public class UpiVerificationHomePageController {

    public void get(@SpringBean KenyaUiUtils kenyaUi, UiUtils ui, PageModel model) {


        List<SimpleObject> pendingVerification = new ArrayList<SimpleObject>();
        List<SimpleObject> verified = new ArrayList<SimpleObject>();
        PersonAttributeType verificationStatusPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.VERIFICATION_STATUS_WITH_NATIONAL_REGISTRY);
        List<Patient> allPatients = Context.getPatientService().getAllPatients();

        for (Patient patient : allPatients) {
            if (patient.getAttribute(verificationStatusPA) != null) {
                // Has attempted verification
                if (patient.getAttribute(verificationStatusPA).getValue().equals("Pending")) {
                    // Has attempted verification but has not received UPI
                    SimpleObject patientPendingObject = SimpleObject.create("id", patient.getId(), "uuid", patient.getUuid(), "givenName", patient
                            .getGivenName(), "middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "", "familyName", patient.getFamilyName(), "birthdate", kenyaUi.formatDate(patient.getBirthdate()), "gender", patient.getGender());
                    pendingVerification.add(patientPendingObject);
                }
                // Has attempted verification and has received UPI
                if (patient.getAttribute(verificationStatusPA).getValue().equals("Yes") || patient.getAttribute(verificationStatusPA).getValue().equals("Verified")) {
                    SimpleObject patientVerifiedObject = SimpleObject.create("id", patient.getId(), "uuid", patient.getUuid(), "givenName", patient
                            .getGivenName(), "middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "", "familyName", patient.getFamilyName(), "birthdate", kenyaUi.formatDate(patient.getBirthdate()), "gender", patient.getGender());
                    verified.add(patientVerifiedObject);
                }

            }
        }
        model.put("patientPendingList", ui.toJson(pendingVerification));
        model.put("patientPendingListSize", pendingVerification.size());
        model.put("patientVerifiedList", ui.toJson(verified));
        model.put("patientVerifiedListSize", verified.size());
        model.put("totalAttemptedVerification", pendingVerification.size() + verified.size());
    }
}
