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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.form.velocity.EmrVelocityFunctions;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.*;

/**
 * Controller for MCH care summary
 */
public class MchmsCarePanelFragmentController {
    protected static final Log log = LogFactory.getLog(EmrVelocityFunctions.class);

    public void controller(@FragmentParam("patient") Patient patient,
                           @FragmentParam("complete") Boolean complete,
                           FragmentModel model) {
        Map<String, Object> calculations = new HashMap<String, Object>();
        PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
        context.setNow(new Date());
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        PatientWrapper patientWrapper = new PatientWrapper(patient);
        EncounterWrapper lastMchEnrollmentWrapped = null;
        EncounterWrapper lastMchFollowUpWrapped = null;

        Encounter lastMchEnrollment = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT));
        Encounter lastMchFollowup = patientWrapper.lastEncounter(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_CONSULTATION));
         //Check whether already in hiv program
        CalculationResultMap enrolled = Calculations.firstEnrollments(hivProgram, Arrays.asList(patient.getPatientId()), context);
        PatientProgram program = EmrCalculationUtils.resultForPatient(enrolled, patient.getPatientId());

        //log.info("Program enrolled ==>"+program);

        if (lastMchEnrollment != null) {
            lastMchEnrollmentWrapped = new EncounterWrapper(lastMchEnrollment);
            }
        if (lastMchFollowup != null) {
            lastMchFollowUpWrapped = new EncounterWrapper(lastMchFollowup);
        }
        Obs hivEnrollmentStatusObs = null;
        Obs hivFollowUpStatusObs = null;
        if (lastMchEnrollmentWrapped != null) {
            hivEnrollmentStatusObs = lastMchEnrollmentWrapped.firstObs(Dictionary.getConcept(Dictionary.HIV_STATUS));
           }
        if (lastMchFollowUpWrapped != null) {
            hivFollowUpStatusObs = lastMchFollowUpWrapped.firstObs(Dictionary.getConcept(Dictionary.HIV_STATUS));
        }
        //Check if already enrolled
       if(program != null) {
           String regimenName = null;
           Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");   //last DRUG_REGIMEN_EDITOR encounter
           if (lastDrugRegimenEditorEncounter != null) {
               SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
               regimenName = o.get("regimenShortDisplay").toString();
               if (regimenName != null) {
                   calculations.put("hivStatus", "Positive");
                   calculations.put("onHaart", "Yes (" + regimenName + ")");
               } else {
                   calculations.put("hivStatus", "Positive");
                   calculations.put("onHaart", "Not specified");
               }
           }
           //Check mch enrollment and followup forms
       }else if(hivEnrollmentStatusObs != null || hivFollowUpStatusObs != null) {
            String regimenName = null;
            calculations.put("hivStatus", hivEnrollmentStatusObs.getValueCoded() != null ? hivEnrollmentStatusObs.getValueCoded()  : hivFollowUpStatusObs.getValueCoded());

            Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");   //last DRUG_REGIMEN_EDITOR encounter
            if (lastDrugRegimenEditorEncounter != null) {
                SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
                regimenName = o.get("regimenShortDisplay").toString();
                if (regimenName != null) {
                    if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("positive")) {
                        calculations.put("onHaart", "Yes (" + regimenName + ")");
                    } else {
                        calculations.put("onHaart", "Not specified");
                    }
                } else {
                    calculations.put("onHaart", "Not specified");
                }

            } else {
                if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("negative")) {
                    calculations.put("onHaart", "Not applicable");
                }
                if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("unknown")) {
                    calculations.put("onHaart", "Not applicable");
                }
                if (hivEnrollmentStatusObs.getValueCoded().getName().getName().equalsIgnoreCase("positive")) {
                    calculations.put("onHaart", "Not specified");
                }
            }
        }
            model.addAttribute("calculations", calculations);

        }
}