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

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

import java.util.Collections;
import java.util.List;

/**
 * View patient page for defaulter tracing app
 */
@AppPage(EmrConstants.APP_DEFAULTER_TRACING)
public class DefaulterTracingViewPatientPageController {
	
	public void controller(PageModel model,
						   UiUtils ui) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);
		PatientWrapper patientWrapper = new PatientWrapper(patient);
		Form defaulterTracingForm = MetadataUtils.existing(Form.class, HivMetadata._Form.CCC_DEFAULTER_TRACING);
		List<Encounter> defaulterTracingEncounters = patientWrapper.allEncounters(defaulterTracingForm);
		Collections.reverse(defaulterTracingEncounters);
		model.put("pastEncounters", defaulterTracingEncounters);
		model.put("formUuid", "a1a62d1e-2def-11e9-b210-d663bd873d93");
	}
}