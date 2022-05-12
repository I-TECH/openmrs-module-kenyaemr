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
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.List;

@AppPage("kenyaemr.afyastat.home")
public class UpiVerificationHomePageController {

    public void get(@SpringBean KenyaUiUtils kenyaUi, UiUtils ui, PageModel model) {


        List<SimpleObject> patientList = new ArrayList<SimpleObject>();


        Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);


        String regStr = "select count(*) from medic_error_data where discriminator='json-registration';";
        String allErrorsSql = "select count(*) from medic_error_data;";
        String queueData = "select count(*) from medic_queue_data;";/*
        Long totalErrors = (Long) Context.getAdministrationService().executeSQL(allErrorsSql, true).get(0).get(0);
        Long registrationErrors = (Long) Context.getAdministrationService().executeSQL(regStr, true).get(0).get(0);
        Long queueDataTotal = (Long) Context.getAdministrationService().executeSQL(queueData, true).get(0).get(0);*/

        PersonAttributeType verificationStatusPA = Context.getPersonService().getPersonAttributeTypeByUuid(CommonMetadata._PersonAttributeType.VERIFICATION_STATUS_WITH_NATIONAL_REGISTRY);
        List<Patient> allPatients = Context.getPatientService().getAllPatients();

        for (int i = 0; i < 200; i++) {
            Patient patient = allPatients.get(i);
            PersonAttribute verificationStatus = patient.getAttribute(verificationStatusPA);
            /*if (verificationStatus.getValue().equals("Pending")) {

            }*/
            SimpleObject patientObject = SimpleObject.create("id", patient.getId(), "uuid", patient.getUuid(), "givenName", patient
                            .getGivenName(), "middleName", patient.getMiddleName() != null ? patient.getMiddleName() : "", "familyName", patient.getFamilyName(), "birthdate", kenyaUi.formatDate(patient.getBirthdate()), "gender", patient.getGender());
            patientList.add(patientObject);

        }
        model.put("patientList", ui.toJson(patientList));
        model.put("patientListSize", patientList.size());
        model.put("totalErrors", patientList.size());
        model.put("registrationErrors", 56);
        model.put("queueData", 23);
        Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
    }
}
