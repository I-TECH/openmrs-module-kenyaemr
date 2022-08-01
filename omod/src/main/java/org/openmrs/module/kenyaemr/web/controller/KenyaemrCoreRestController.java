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
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The rest controller for exposing resources through kenyacore and kenyaemr modules
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/kenyaemr")
public class KenyaemrCoreRestController extends BaseRestController {
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Gets a list of available/completed forms for a patient
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/forms") // gets all visit forms for a patient
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
        ObjectNode allFormsObj = JsonNodeFactory.instance.objectNode();

        if (!activeVisits.isEmpty()) {
            Visit patientVisit = activeVisits.get(0);

                /**
                 *  {uuid: string;
                 *   encounterType?: EncounterType;
                 *   name: string;
                 *   display: string;
                 *   version: string;
                 *   published: boolean;
                 *   retired: boolean;}
                 */

            FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);
            List<FormDescriptor> uncompletedFormDescriptors = formManager.getAllUncompletedFormsForVisit(patientVisit);

            if (!uncompletedFormDescriptors.isEmpty()) {

                for (FormDescriptor descriptor : uncompletedFormDescriptors) {
                
                    ObjectNode formObj = generateFormDescriptorPayload(descriptor);
                    formObj.put("formCategory", "available");
                    formList.add(formObj);
                }
            }

            List<FormDescriptor> completedFormDescriptors = formManager.getCompletedFormsForVisit(patientVisit);

            if (!completedFormDescriptors.isEmpty()) {

                for (FormDescriptor descriptor : completedFormDescriptors) {
                    ObjectNode formObj = generateFormDescriptorPayload(descriptor);
                    formObj.put("formCategory", "completed");
                    formList.add(formObj);
                }
            }
        }

        allFormsObj.put("results", formList);

        return allFormsObj.toString();
    }

    /**
     * Get a list of programs a patient is eligible for
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/eligiblePrograms") // gets all programs a patient is eligible for
    @ResponseBody
    public Object getEligiblePrograms(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        ProgramManager programManager = CoreContext.getInstance().getManager(ProgramManager.class);
        ArrayNode programList = JsonNodeFactory.instance.arrayNode();

        if (!patient.isVoided()) {
            Collection<ProgramDescriptor> activePrograms = programManager.getPatientActivePrograms(patient);
            Collection<ProgramDescriptor> eligiblePrograms = programManager.getPatientEligiblePrograms(patient);

            /**
             * ProgramEndPoint {
             *   uuid: string;
             *   display: string;
             *   enrollmentFormUuid: string;
             *   discontinuationFormUuid: string;
             *   enrollmentStatus: string;
             * }
             */
            for (ProgramDescriptor descriptor : eligiblePrograms) {
                    ObjectNode programObj = JsonNodeFactory.instance.objectNode();
                    programObj.put("uuid", descriptor.getTargetUuid());
                    programObj.put("display", descriptor.getTarget().getName());
                    programObj.put("enrollmentFormUuid", descriptor.getDefaultEnrollmentForm().getTargetUuid());
                    programObj.put("discontinuationFormUuid", descriptor.getDefaultCompletionForm().getTargetUuid());
                    programObj.put("enrollmentStatus", activePrograms.contains(descriptor) ? "active" : "eligible");
                    programList.add(programObj);
            }
        }

        return programList.toString();
    }

    /**
     * Generate payload for a form descriptor. Required when serving forms to the frontend
     * @param descriptor
     * @return
     */
    private ObjectNode generateFormDescriptorPayload(FormDescriptor descriptor) {
        ObjectNode formObj = JsonNodeFactory.instance.objectNode();
        ObjectNode encObj = JsonNodeFactory.instance.objectNode();
        Form frm = descriptor.getTarget();
        encObj.put("uuid", frm.getEncounterType().getUuid());
        encObj.put("display", frm.getEncounterType().getName());
        formObj.put("uuid", descriptor.getTargetUuid());
        formObj.put("encounterType", encObj);
        formObj.put("name", frm.getName());
        formObj.put("display", frm.getName());
        formObj.put("version", frm.getVersion());
        formObj.put("published", frm.getPublished());
        formObj.put("retired", frm.getRetired());
        return formObj;
    }

    /**
     * @see BaseRestController#getNamespace()
     */

    @Override
    public String getNamespace() {
        return "v1/kenyaemr";
    }
}
