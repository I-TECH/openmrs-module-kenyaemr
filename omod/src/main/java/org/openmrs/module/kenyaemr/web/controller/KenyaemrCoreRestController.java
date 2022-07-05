/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaemrorderentry.labDataExchange.LabOrderDataExchange;
import org.openmrs.module.kenyaemrorderentry.util.Utils;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * The rest controller for resources within kenyaemr module
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/kenyaemr")
public class KenyaemrCoreRestController extends BaseRestController {
    protected final Log log = LogFactory.getLog(getClass());

    @RequestMapping(method = RequestMethod.POST, value = "/formsforvisit") // gets all visit forms for a patient
    @ResponseBody
    public Object getAllFormsForVisit(HttpServletRequest request, @RequestParam("patient") Patient patient, @SpringBean FormManager formManager) {
        String requestBody = null;

        List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
        Visit patientVisit = activeVisits.get(0);
        List<FormDescriptor> availableFormDescriptors = formManager.getAllFormsForVisit(patientVisit);
        FormService formService = Context.getFormService();
        ArrayNode formList = JsonNodeFactory.instance.arrayNode();

        if (!availableFormDescriptors.isEmpty()) {

            /**
             *  {uuid: string;
             *   encounterType?: EncounterType;
             *   name: string;
             *   display: string;
             *   version: string;
             *   published: boolean;
             *   retired: boolean;}
             */
            for (FormDescriptor descriptor : availableFormDescriptors) {
                ObjectNode formObj = JsonNodeFactory.instance.objectNode();
                formObj.put("uuid", descriptor.getTargetUuid());
                formObj.put("encounterType", formService.getFormByUuid(descriptor.getTargetUuid()).getEncounterType().getEncounterTypeId());
                formObj.put("name", formService.getFormByUuid(descriptor.getTargetUuid()).getName());
                formObj.put("display", formService.getFormByUuid(descriptor.getTargetUuid()).getName());
                formObj.put("version", formService.getFormByUuid(descriptor.getTargetUuid()).getVersion());
                formObj.put("published", formService.getFormByUuid(descriptor.getTargetUuid()).getPublished());
                formObj.put("retired", formService.getFormByUuid(descriptor.getTargetUuid()).getRetired());
                formList.add(formObj);

            }
        }

        return  formList;
    }

    /**
     * @see BaseRestController#getNamespace()
     */

    @Override
    public String getNamespace() {
        return "v1/kenyaemr";
    }
}
