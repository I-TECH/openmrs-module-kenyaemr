/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.fragment.controller.program.mchms;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for MCH care summary
 */
public class MchmsCarePanelFragmentController {

    public void controller(@FragmentParam("patient") Patient patient,
                           @FragmentParam("complete") Boolean complete,
                           FragmentModel model,
                           @SpringBean RegimenManager regimenManager)
    {
        Map<String, Object> calculations = new HashMap<String, Object>();

        PatientWrapper patientWrapper = new PatientWrapper(patient);
        EncounterWrapper lastMchEnrollmentWrapped = null;

        Encounter lastMchEnrollment = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT));
        if (lastMchEnrollment != null) {
            lastMchEnrollmentWrapped = new EncounterWrapper(lastMchEnrollment);
        }
        Obs hivStatusObs = null;
        if (lastMchEnrollmentWrapped != null) {
            hivStatusObs = lastMchEnrollmentWrapped.firstObs(Dictionary.getConcept(Dictionary.HIV_STATUS));
        }

        if (hivStatusObs != null) {
            calculations.put("hivStatus", hivStatusObs.getValueCoded());
        } else {
            calculations.put("hivStatus", "Not Specified");
        }

        Concept medSet = regimenManager.getMasterSetConcept("ARV");
        RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, medSet);
        model.addAttribute("regimenHistory", history);
        model.addAttribute("calculations", calculations);
    }
}