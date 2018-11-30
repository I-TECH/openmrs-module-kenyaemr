/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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