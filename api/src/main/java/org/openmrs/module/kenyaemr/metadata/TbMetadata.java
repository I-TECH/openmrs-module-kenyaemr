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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TB metadata provider
 */
@Component("kenyaemr.tb.metadata")
@Requires("kenyaemr.common.metadata")
public class TbMetadata extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(TbMetadata.class);

	public static final class EncounterType {
		public static final String TB_DISCONTINUATION = "d3e3d723-7458-4b4e-8998-408e8a551a84";
		public static final String TB_ENROLLMENT = "9d8498a4-372d-4dc4-a809-513a2434621e";
		public static final String TB_SCREENING = "ed6dacc9-0827-4c82-86be-53c0d8c449be";
	}

	public static final class Form {
		public static final String TB_COMPLETION = "4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a";
		public static final String TB_ENROLLMENT = "89994550-9939-40f3-afa6-173bce445c79";
		public static final String TB_SCREENING = "59ed8e62-7f1f-40ae-a2e3-eabe350277ce";
	}

	public static final class Program {
		public static final String TB = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";
	}

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing TB metadata");

		installer.program("TB Program", "Treatment for TB patients", Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM, Program.TB);

		installer.encounterType("TB Screening", "Screening of patient for TB", EncounterType.TB_SCREENING);
		installer.encounterType("TB Enrollment", "Enrollment onto HIV program", EncounterType.TB_ENROLLMENT);
		installer.encounterType("TB Discontinuation", "Discontinuation from HIV program", EncounterType.TB_DISCONTINUATION);

		installer.form("TB Screening", null, EncounterType.TB_SCREENING, "1", Form.TB_SCREENING);
		installer.form("TB Enrollment", null, EncounterType.TB_ENROLLMENT, "1", Form.TB_ENROLLMENT);
		installer.form("TB Discontinuation", null, EncounterType.TB_DISCONTINUATION, "1", Form.TB_COMPLETION);
	}
}