/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.nupi;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

@AppPage(EmrConstants.APP_NUPI_VERIFICATION)
public class NupiVerificationHomePageController {

    public void get(@SpringBean KenyaUiUtils kenyaUi, UiUtils ui, PageModel model) {


        List<SimpleObject> pendingVerification = new ArrayList<SimpleObject>();
        List<SimpleObject> verifiedWithErrors = new ArrayList<SimpleObject>();
        Integer verifiedCount = 0;
        Integer verifiedOnART = 0;
        String errorDescription = "";
        PersonAttributeType verificationStatusPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.VERIFICATION_STATUS_WITH_NATIONAL_REGISTRY);
        PersonAttributeType verificationMessagePA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.VERIFICATION_MESSAGE_WITH_NATIONAL_REGISTRY);
        PersonAttributeType verificationErrorDescriptionPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.VERIFICATION_DESCRIPTION_FOR_IPRS_ERROR);
        List<Patient> allPatients = Context.getPatientService().getAllPatients();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        for (Patient patient : allPatients) {
            if (patient.getAttribute(verificationStatusPA) != null) {
                String networkError = "";
                if (patient.getAttribute(verificationMessagePA) != null) {
                    networkError = patient.getAttribute(verificationMessagePA).getValue().trim();
                }
                // Has attempted verification
                if (patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Pending")) {
                    // Has attempted verification but has not received NUPI
                    SimpleObject patientPendingObject = SimpleObject.create("id", patient.getId(), "uuid", patient.getUuid(), "givenName", patient
                            .getGivenName(), "middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "", "familyName", patient.getFamilyName(), "birthdate", kenyaUi.formatDate(patient.getBirthdate()), "gender", patient.getGender(), "error", networkError != null ? networkError : "-");
                    pendingVerification.add(patientPendingObject);
                }
                // Has attempted verification and has received NUPI
                if (patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Yes") || patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Verified") || patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Verified elsewhere")) {
                    verifiedCount++;
                }
                // Has successful verification and has received NUPI and in HIV program
                ProgramWorkflowService pwfservice = Context.getProgramWorkflowService();
                List<PatientProgram> programs = pwfservice.getPatientPrograms(patient, hivProgram, null, null, null, null, true);
                if (programs.size() > 0) {
                    if (patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Yes") || patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Verified") || patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Verified elsewhere")) {
                        verifiedOnART++;
                    }
                }
                // Has successfully verified but IPR has returned errors on verification   : Already has a NUPI but IPRS returned errors
                if (patient.getAttribute(verificationStatusPA).getValue().trim().equalsIgnoreCase("Failed IPRS Check")) {
                     // Check error description
                    if(patient.getAttribute(verificationErrorDescriptionPA) != null) {
                        errorDescription = patient.getAttribute(verificationErrorDescriptionPA).getValue().trim();
                    }
                    SimpleObject errorVerificationObject = SimpleObject.create(
                            "id", patient.getId(),
                            "uuid", patient.getUuid(),
                            "givenName", patient.getGivenName(),
                            "middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "",
                            "familyName", patient.getFamilyName(),
                            "birthdate", kenyaUi.formatDate(patient.getBirthdate()),
                            "gender", patient.getGender(),
                            "error", errorDescription != null ? errorDescription : "-" );
                    verifiedWithErrors.add(errorVerificationObject);
                }
            }
        }
        model.put("patientPendingList", ui.toJson(pendingVerification));
        model.put("patientPendingListSize", pendingVerification.size());
        model.put("patientVerifiedListSize", verifiedCount);
        model.put("patientVerifiedOnARTListSize", verifiedOnART);
        model.put("totalAttemptedVerification", pendingVerification.size() + verifiedCount);
        model.put("patientVerifiedWithErrorsList", ui.toJson(verifiedWithErrors));
        model.put("numberOfVerificationErrorSize", verifiedWithErrors.size());
    }

}


