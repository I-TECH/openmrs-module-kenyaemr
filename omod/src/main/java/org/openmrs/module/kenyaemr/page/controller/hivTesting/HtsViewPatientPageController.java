/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.hivTesting;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.page.PageModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * View patient page for defaulter tracing app
 */
@AppPage(EmrConstants.APP_HIV_TESTING)
public class HtsViewPatientPageController {
	
	public void controller(PageModel model) {
		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);
		Form htsInitialForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_INITIAL_TEST);
		Form htsRetestForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_CONFIRMATORY_TEST);
		Form htsLinkageForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.REFERRAL_AND_LINKAGE);

		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Arrays.asList(htsInitialForm, htsRetestForm, htsLinkageForm), null, null, null, null, false);

		Collections.reverse(encounters);
	}
}