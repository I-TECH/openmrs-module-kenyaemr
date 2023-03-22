/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.shortcuts;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;

@AppPage(EmrConstants.APP_O3_SHORTCUT)
public class O3HomePageController {

    public void get(@SpringBean KenyaUiUtils kenyaUi, UiUtils ui, PageModel model) {
        SimpleObject ret = new SimpleObject();
        Patient patient = (Patient) model.getAttribute("currentPatient");
        if(patient != null) {
            ret.put("patientExists", true);
            ret.put("patientId", patient.getId());
            ret.put("patientUUID", patient.getUuid());
        } else {
            ret.put("patientExists", false);
            ret.put("patientId", "");
            ret.put("patientUUID", "");
        }

        model.addAttribute("patientDetails", ui.toJson(ret));
    }

    public String controller(PageModel model, UiUtils ui, HttpSession session, @SpringBean KenyaUiUtils kenyaUi) {
        SimpleObject ret = new SimpleObject();
        Patient patient = (Patient) model.getAttribute("currentPatient");
        if(patient != null) {
            ret.put("patientExists", true);
            ret.put("patientId", patient.getId());
            ret.put("patientUUID", patient.getUuid());
        } else {
            ret.put("patientExists", false);
            ret.put("patientId", "");
            ret.put("patientUUID", "");
        }

        model.addAttribute("patientDetails", ui.toJson(ret));

        return null;
    }
}