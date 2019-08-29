/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.defaulterTracing;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.DeceasedPatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.hts.PatientsEligibleForHtsLinkageAndReferralCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.OnHaartCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * View patient page for tracing app
 */
@AppPage(EmrConstants.APP_DEFAULTER_TRACING)
public class DefaulterTracingViewPatientPageController {
	
	public void controller(PageModel model,
						   UiUtils ui) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);
		PatientWrapper patientWrapper = new PatientWrapper(patient);
		Form defaulterTracingForm = MetadataUtils.existing(Form.class, HivMetadata._Form.CCC_DEFAULTER_TRACING);
		Form htsClientTracingForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_CLIENT_TRACING);
		List<Encounter> defaulterTracingEncounters = patientWrapper.allEncounters(defaulterTracingForm);
		List<Encounter> htsTracingEncounters = new ArrayList<Encounter>();
		Collections.reverse(defaulterTracingEncounters);

		boolean everEnrolledInHiv = false;
		boolean hasHtsHistory = false;

		// check if a patient has HIV enrollments
		ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		List<PatientProgram> hivProgramEnrollments = programWorkflowService.getPatientPrograms(patient, programWorkflowService.getProgramByUuid(HivMetadata._Program.HIV), null, null, null, null, false );

		if (hivProgramEnrollments.size() > 0) {
			everEnrolledInHiv = true;
		} else { // for hts clients. check if the client has positive test result and has no successful linkage information
			Cohort c = new Cohort();
			c.addMember(patient.getPatientId());
			CalculationResultMap resultMap = new PatientsEligibleForHtsLinkageAndReferralCalculation().evaluate(c.getMemberIds(), null, Context.getService(PatientCalculationService.class).createCalculationContext());
			boolean hasLastPositiveHtsResult = (Boolean) resultMap.get(patient.getPatientId()).getValue();

			if (hasLastPositiveHtsResult) {
				hasHtsHistory = true;
				List<Encounter> recordedTracingHistory = patientWrapper.allEncounters(htsClientTracingForm);
				if (recordedTracingHistory.size() > 0) {
					htsTracingEncounters = recordedTracingHistory;
					Collections.reverse(htsTracingEncounters);
				}
			}
		}

		model.put("cccDefaulterTracingEncounters", defaulterTracingEncounters);
		model.put("cccDefaulterTracingformUuid", HivMetadata._Form.CCC_DEFAULTER_TRACING);
		model.put("htsTracingEncounters", htsTracingEncounters);
		model.put("htsTracingformUuid", CommonMetadata._Form.HTS_CLIENT_TRACING);
		model.put("htsLinkageAndReferralformUuid", CommonMetadata._Form.REFERRAL_AND_LINKAGE);
		model.put("hasHivEnrollment", everEnrolledInHiv);
		model.put("hasHtsEncounters", hasHtsHistory);
	}
}