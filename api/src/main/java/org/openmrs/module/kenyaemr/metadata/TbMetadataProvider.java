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
 * TB metadata provider
 */
@Component
@Requires("kenyaemr.common.metadata")
public class TbMetadataProvider extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(TbMetadataProvider.class);

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing TB metadata");

		installer.encounterType("TB Screening", "Screening of patient for TB", Metadata.EncounterType.TB_SCREENING);
		installer.encounterType("TB Enrollment", "Enrollment onto HIV program", Metadata.EncounterType.TB_ENROLLMENT);
		installer.encounterType("TB Consultation", "Collection of HIV-specific data during the main consultation", Metadata.EncounterType.TB_CONSULTATION);
		installer.encounterType("TB Discontinuation", "Discontinuation from HIV program", Metadata.EncounterType.TB_DISCONTINUATION);

		installer.form("TB Screening", null, Metadata.EncounterType.TB_SCREENING, "1", Metadata.Form.TB_SCREENING);
		installer.form("TB Enrollment", null, Metadata.EncounterType.TB_ENROLLMENT, "1", Metadata.Form.TB_ENROLLMENT);
		installer.form("TB Followup Visit", null, Metadata.EncounterType.TB_CONSULTATION, "1", Metadata.Form.TB_VISIT);
		installer.form("TB Discontinuation", null, Metadata.EncounterType.TB_DISCONTINUATION, "1", Metadata.Form.TB_COMPLETION);

		installer.program("TB Program", "Treatment for TB patients", Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM, Metadata.Program.TB);
	}
}