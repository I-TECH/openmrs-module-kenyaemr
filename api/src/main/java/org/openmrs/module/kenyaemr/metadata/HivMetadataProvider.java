/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.metadata.AbstractMetadataProvider;
import org.openmrs.module.kenyacore.metadata.Requires;
import org.openmrs.module.kenyacore.metadata.installer.CoreMetadataInstaller;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HIV metadata provider
 */
@Component
@Requires("kenyaemr.common.metadata")
public class HivMetadataProvider extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(HivMetadataProvider.class);

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing HIV metadata");

		installer.encounterType("HIV Enrollment", "Enrollment onto HIV program", Metadata.EncounterType.HIV_ENROLLMENT);
		installer.encounterType("HIV Consultation", "Collection of HIV-specific data during the main consultation", Metadata.EncounterType.HIV_CONSULTATION);
		installer.encounterType("HIV Discontinuation", "Discontinuation from HIV program", Metadata.EncounterType.HIV_DISCONTINUATION);

		installer.form("HIV Enrollment", null, Metadata.EncounterType.HIV_ENROLLMENT, "1", Metadata.Form.HIV_ENROLLMENT);
		installer.form("Clinical Encounter - HIV addendum", null, Metadata.EncounterType.HIV_CONSULTATION, "1", Metadata.Form.CLINICAL_ENCOUNTER_HIV_ADDENDUM);
		installer.form("Family History", null, Metadata.EncounterType.REGISTRATION, "1", Metadata.Form.FAMILY_HISTORY);
		installer.form("MOH 257 Face Page", null, Metadata.EncounterType.HIV_CONSULTATION, "1", Metadata.Form.MOH_257_FACE_PAGE);
		installer.form("MOH 257 Visit Summary", null, Metadata.EncounterType.HIV_CONSULTATION, "1", Metadata.Form.MOH_257_VISIT_SUMMARY);
		installer.form("HIV Discontinuation", null, Metadata.EncounterType.HIV_DISCONTINUATION, "1", Metadata.Form.HIV_DISCONTINUATION);

		installer.program("HIV Program", "Treatment for HIV-positive patients", Dictionary.HIV_PROGRAM, Metadata.Program.HIV);
	}
}