/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.adherenceCounselling;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.HtsConstants;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visit summary fragment
 */
public class CounsellingHistoryFragmentController {

    ConceptService conceptService = Context.getConceptService();
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {

        PatientWrapper patientWrapper = new PatientWrapper(patient);

        Form enhancedAdherenceForm = MetadataUtils.existing(Form.class, HivMetadata._Form.ENHANCED_ADHERENCE_SCREENING);
        List<Encounter> enhancedAdherenceEncounters = patientWrapper.allEncounters(enhancedAdherenceForm);
        //Collections.reverse(enhancedAdherenceEncounters);
        //List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Arrays.asList(enhancedAdherenceForm), null, null, null, null, false);

        List<SimpleObject> encDetails = new ArrayList<SimpleObject>();
        for (Encounter e : enhancedAdherenceEncounters) {


            SimpleObject o = getSessionDetails(e.getObs(), e);
            encDetails.add(o);
            model.put("encDetails", encDetails);
        }
        model.put("enhancedAdherenceEncounters", enhancedAdherenceEncounters);
       }

    SimpleObject getSessionDetails (Set<Obs> obsList, Encounter e) {

        Integer sessionConcept = 1639;
        Double sessionNum = null;
        for(Obs obs:obsList) {
            if (obs.getConcept().getConceptId().equals(sessionConcept) ) {
                sessionNum = obs.getValueNumeric();
            }
        }
        return SimpleObject.create(
                "sessionNum", sessionNum != null ? sessionNum : "",
                "encDate", DATE_FORMAT.format(e.getEncounterDatetime()),
                "encounter", e,
                "form",e.getForm()
        );
    }
}