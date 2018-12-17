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