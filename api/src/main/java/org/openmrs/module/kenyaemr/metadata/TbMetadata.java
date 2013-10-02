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

import org.openmrs.PatientIdentifierType.LocationBehavior;
import org.openmrs.module.kenyacore.metadata.bundle.AbstractMetadataBundle;
import org.openmrs.module.kenyacore.metadata.bundle.Requires;
import org.openmrs.module.kenyaemr.Dictionary;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.metadata.bundle.Constructors.*;

/**
 * TB metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class TbMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String TB_DISCONTINUATION = "d3e3d723-7458-4b4e-8998-408e8a551a84";
		public static final String TB_ENROLLMENT = "9d8498a4-372d-4dc4-a809-513a2434621e";
		public static final String TB_SCREENING = "ed6dacc9-0827-4c82-86be-53c0d8c449be";
		public static final String TB_CONSULTATION = "fbf0bfce-e9f4-45bb-935a-59195d8a0e35";
	}

	public static final class _Form {
		public static final String TB_COMPLETION = "4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a";
		public static final String TB_ENROLLMENT = "89994550-9939-40f3-afa6-173bce445c79";
		public static final String TB_SCREENING = "59ed8e62-7f1f-40ae-a2e3-eabe350277ce";
		public static final String TB_FOLLOW_UP = "2daabb77-7ad6-4952-864b-8d23e109c69d";
	}

	public static final class _PatientIdentifierType {
		public static final String DISTRICT_REG_NUMBER = "d8ee3b8c-a8fc-4d6b-af6a-9423be5f8906";
	}

	public static final class _Program {
		public static final String TB = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";
	}

	/**
	 * @see org.openmrs.module.kenyacore.metadata.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("TB Screening", "Screening of patient for TB", _EncounterType.TB_SCREENING));
		install(encounterType("TB Enrollment", "Enrollment onto HIV program", _EncounterType.TB_ENROLLMENT));
		install(encounterType("TB Discontinuation", "Discontinuation from HIV program", _EncounterType.TB_DISCONTINUATION));
		install(encounterType("TB FollowUp", "Consultation in Tb Program", _EncounterType.TB_CONSULTATION));

		install(form("TB Screening", null, _EncounterType.TB_SCREENING, "1", _Form.TB_SCREENING));
		install(form("TB Enrollment", null, _EncounterType.TB_ENROLLMENT, "1", _Form.TB_ENROLLMENT));
		install(form("TB Discontinuation", null, _EncounterType.TB_DISCONTINUATION, "1", _Form.TB_COMPLETION));
		install(form("TB FollowUp" ,null, _EncounterType.TB_CONSULTATION, "1", _Form.TB_FOLLOW_UP));

		install(patientIdentifierType("District Registration Number", "Assigned to every TB patient",
				null, null, null,
				LocationBehavior.NOT_USED, false, _PatientIdentifierType.DISTRICT_REG_NUMBER));

		install(program("TB Program", "Treatment for TB patients", Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM, _Program.TB));
	}
}