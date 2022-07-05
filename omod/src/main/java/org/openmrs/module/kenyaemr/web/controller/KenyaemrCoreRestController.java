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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaemrorderentry.labDataExchange.LabOrderDataExchange;
import org.openmrs.module.kenyaemrorderentry.util.Utils;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * The rest controller for exposing resources through kenyacore and kenyaemr modules
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/kenyaemr")
public class KenyaemrCoreRestController extends BaseRestController {
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Gets a list of available forms for a patient
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/availableforms") // gets all visit forms for a patient
    @ResponseBody
    public Object getAllAvailableFormsForVisit(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
        ArrayNode formList = JsonNodeFactory.instance.arrayNode();

        if (!activeVisits.isEmpty()) {
            Visit patientVisit = activeVisits.get(0);

            FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);
            List<FormDescriptor> uncompletedFormDescriptors = formManager.getAllUncompletedFormsForVisit(patientVisit);

            if (!uncompletedFormDescriptors.isEmpty()) {

                /**
                 *  {uuid: string;
                 *   encounterType?: EncounterType;
                 *   name: string;
                 *   display: string;
                 *   version: string;
                 *   published: boolean;
                 *   retired: boolean;}
                 */
                for (FormDescriptor descriptor : uncompletedFormDescriptors) {
                    ObjectNode formObj = JsonNodeFactory.instance.objectNode();
                    Form frm = descriptor.getTarget();
                    formObj.put("uuid", descriptor.getTargetUuid());
                    formObj.put("encounterType", frm.getEncounterType().getEncounterTypeId());
                    formObj.put("name", frm.getName());
                    formObj.put("display", frm.getName());
                    formObj.put("version", frm.getVersion());
                    formObj.put("published", frm.getPublished());
                    formObj.put("retired", frm.getRetired());
                    formList.add(formObj);
                }
            }
        }


        return formList.toString();
    }

    /**
     * Gets a list of completed forms for a patient during a visit
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/completedforms") // gets all visit forms for a patient
    @ResponseBody
    public Object getAllCommpletedFormsForVisit(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
        ArrayNode formList = JsonNodeFactory.instance.arrayNode();

        if (!activeVisits.isEmpty()) {
            Visit patientVisit = activeVisits.get(0);

            FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);
            List<FormDescriptor> completedFormDescriptors = formManager.getCompletedFormsForVisit(patientVisit);

            if (!completedFormDescriptors.isEmpty()) {


                for (FormDescriptor descriptor : completedFormDescriptors) {
                    ObjectNode formObj = JsonNodeFactory.instance.objectNode();
                    Form frm = descriptor.getTarget();
                    formObj.put("uuid", descriptor.getTargetUuid());
                    formObj.put("encounterType", frm.getEncounterType().getEncounterTypeId());
                    formObj.put("name", frm.getName());
                    formObj.put("display", frm.getName());
                    formObj.put("version", frm.getVersion());
                    formObj.put("published", frm.getPublished());
                    formObj.put("retired", frm.getRetired());
                    formList.add(formObj);
                }
            }
        }

        return formList.toString();
    }

    /**
     * @see BaseRestController#getNamespace()
     */

    @Override
    public String getNamespace() {
        return "v1/kenyaemr";
    }
}
