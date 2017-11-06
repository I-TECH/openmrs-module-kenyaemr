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
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
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
                           FragmentModel model) {
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

        Encounter lastMchConsultation = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION));

        if (lastMchConsultation != null) {
            EncounterWrapper lastMchConsultationWrapped = new EncounterWrapper(lastMchConsultation);

            Obs arvUseObs = lastMchConsultationWrapped.firstObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY));
            if (arvUseObs != null) {
                Concept concept = arvUseObs.getValueCoded();
                if (concept.equals(Dictionary.getConcept(Dictionary.MOTHER_ON_PROPHYLAXIS))
                        || concept.equals(Dictionary.getConcept(Dictionary.MOTHER_ON_HAART))) {
                    String regimen = "Regimen not specified";
                    List<Obs> drugObsList = lastMchConsultationWrapped.allObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY));
                    if (!drugObsList.isEmpty()) {
                        String rgmn = "";
                        for (Obs obs : drugObsList) {
                            if (obs != null) {
                                rgmn += obs.getValueCoded().getName().getName();
                                if (!obs.equals(drugObsList.get(drugObsList.size() - 1))) {
                                    rgmn += " + ";
                                }
                            }
                        }
                        if (!rgmn.isEmpty()) {
                            regimen = rgmn;
                        }
                    }
                    if (concept.equals(Dictionary.getConcept(Dictionary.MOTHER_ON_PROPHYLAXIS))) {
                        calculations.put("onProhylaxis", "Yes (" + regimen + ")");
                        calculations.put("onHaart", "No");
                    } else if (concept.equals(Dictionary.getConcept(Dictionary.MOTHER_ON_HAART))) {
                        calculations.put("onProhylaxis", "No");
                        calculations.put("onHaart", "Yes (" + regimen + ")");
                    }
                } else {
                    calculations.put("onProhylaxis", "No");
                    calculations.put("onHaart", "No");
                }
            } else {
                calculations.put("onProhylaxis", "Not specified");
                calculations.put("onHaart", "Not specified");
            }
        } else {
            calculations.put("onProhylaxis", "Not specified");
            calculations.put("onHaart", "Not specified");
        }
        model.addAttribute("calculations", calculations);
    }
}