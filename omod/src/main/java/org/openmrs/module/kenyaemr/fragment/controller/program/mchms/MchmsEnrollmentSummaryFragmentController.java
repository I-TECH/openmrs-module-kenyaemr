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

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.wrapper.Enrollment;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MCH program enrollment fragment
 */
public class MchmsEnrollmentSummaryFragmentController {

    public String controller(@FragmentParam("patientProgram") PatientProgram patientProgram,
                             @FragmentParam(value = "encounter", required = false) Encounter encounter,
                             @FragmentParam("showClinicalData") boolean showClinicalData,
                             FragmentModel model) {

        Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();
        dataPoints.put("Enrolled", patientProgram.getDateEnrolled());

        Enrollment enrollment = new Enrollment(patientProgram);

        Obs ancNoObs = enrollment.firstObs(Dictionary.getConcept(Dictionary.ANTENATAL_CASE_NUMBER));
        if (ancNoObs != null) {
            dataPoints.put("ANC No", ancNoObs.getValueNumeric().intValue());
        }

        EncounterType mchMsConsultation = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
        Form delivery = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);
        Encounter deliveryEncounter = enrollment.encounterByForm(mchMsConsultation, delivery);

        Obs lmpObs = enrollment.firstObs(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD));
        if (lmpObs != null) {
            if (deliveryEncounter == null) {
                Weeks weeks = Weeks.weeksBetween(new DateTime(lmpObs.getValueDate()), new DateTime(new Date()));
                dataPoints.put("Gestation (weeks)", weeks.getWeeks());
                dataPoints.put("LMP", lmpObs.getValueDate());
                dataPoints.put("EDD (LMP)", CoreUtils.dateAddDays(lmpObs.getValueDate(), 280));
            }
        }

        Obs eddUsoundObs = enrollment.firstObs(Dictionary.getConcept(Dictionary.EXPECTED_DATE_OF_DELIVERY));
        if (eddUsoundObs != null) {
            if (deliveryEncounter == null) {
                dataPoints.put("EDD (Ultrasound)", eddUsoundObs.getValueDate());
            }
        }

        Obs gravidaObs = enrollment.firstObs(Dictionary.getConcept(Dictionary.GRAVIDA));
        if (gravidaObs != null) {
            dataPoints.put("Gravida", gravidaObs.getValueNumeric().intValue());
        }

        Obs parityTermObs = enrollment.firstObs(Dictionary.getConcept(Dictionary.PARITY_TERM));
        Obs parityAbortionObs = enrollment.firstObs(Dictionary.getConcept(Dictionary.PARITY_ABORTION));

        if (parityTermObs != null && parityAbortionObs != null) {
            dataPoints.put("Parity", parityTermObs.getValueNumeric().intValue() + " + " + parityAbortionObs.getValueNumeric().intValue());
        }

        model.put("dataPoints", dataPoints);
        return "view/dataPoints";
    }
}