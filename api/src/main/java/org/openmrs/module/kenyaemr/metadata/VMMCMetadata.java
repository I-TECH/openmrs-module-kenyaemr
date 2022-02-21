/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * VMMC metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class VMMCMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String VMMC_DISCONTINUATION = "4f02dfed-a2ec-40c2-b546-85dab5831871";
		public static final String VMMC_ENROLLMENT = "85019fbe-9339-49f7-8341-e9a04311bb99";
		public static final String VMMC_PROCEDURE = "35c6fcc2-960b-11ec-b909-0242ac120002";
		public static final String VMMC_MEDICAL_HISTORY_EXAMINATION = "a2010bf5-2db0-4bf4-819f-8a3cffbcb21b";
		public static final String VMMC_CLIENT_FOLLOWUP = "2504e865-638e-4a63-bf08-7e8f03a376f3";
		public static final String VMMC_POST_OPERATION = "6632e66c-9ae5-11ec-b909-0242ac120002";
	}

	public static final class _Form {
		public static final String VMMC_DISCONTINUATION_FORM = "bc6a9e7d-58f7-43c0-8334-d8011fef4000";
		public static final String VMMC_ENROLLMENT_FORM = "a74e3e4a-9e2a-41fb-8e64-4ba8a71ff984";
		public static final String VMMC_PROCEDURE_FORM = "5ee93f48-960b-11ec-b909-0242ac120002";
		public static final String VMMC_MEDICAL_HISTORY_EXAMINATION_FORM = "d42aeb3d-d5d2-4338-a154-f75ddac78b59";
		public static final String VMMC_CLIENT_FOLLOWUP_FORM = "08873f91-7161-4f90-931d-65b131f2b12b";
		public static final String VMMC_POST_OPERATION_FORM = "620b3404-9ae5-11ec-b909-0242ac120002";
	}


	public static final class _Program {
		public static final String VMMC = Metadata.Program.VMMC;
	}

	public static final class _Concept {

		public static final String VMMC = "162223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	}
	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("VMMC Enrollment", "Enrollment onto VMMC program", _EncounterType.VMMC_ENROLLMENT));
		install(encounterType("VMMC Discontinuation", "Discontinuation from VMMC program", _EncounterType.VMMC_DISCONTINUATION));
		install(encounterType("VMMC Procedure", "VMMC procedure encounter", _EncounterType.VMMC_PROCEDURE));
		install(encounterType("VMMC Medical History and Examination", "VMMC Medical History and Examination", _EncounterType.VMMC_MEDICAL_HISTORY_EXAMINATION));
		install(encounterType("VMMC Client Follow up", "VMMC Client Follow up", _EncounterType.VMMC_CLIENT_FOLLOWUP));
		install(encounterType("VMMC Immediate Post-Operation Assessment", "VMMC Immediate Post-Operation Assessment", _EncounterType.VMMC_POST_OPERATION));

		install(form("VMMC Enrollment Form", null, _EncounterType.VMMC_ENROLLMENT, "1", _Form.VMMC_ENROLLMENT_FORM));
		install(form("VMMC Discontinuation Form", null, _EncounterType.VMMC_DISCONTINUATION, "1", _Form.VMMC_DISCONTINUATION_FORM));
		install(form("VMMC Procedure Form", null, _EncounterType.VMMC_PROCEDURE, "1", _Form.VMMC_PROCEDURE_FORM));
		install(form("VMMC Medical History and Examination Form", null, _EncounterType.VMMC_MEDICAL_HISTORY_EXAMINATION, "1", _Form.VMMC_MEDICAL_HISTORY_EXAMINATION_FORM));
		install(form("VMMC Client Follow-Up Form", null, _EncounterType.VMMC_CLIENT_FOLLOWUP, "1", _Form.VMMC_CLIENT_FOLLOWUP_FORM));
		install(form("VMMC Immediate Post-Operation Assessment Form", null, _EncounterType.VMMC_POST_OPERATION, "1", _Form.VMMC_POST_OPERATION_FORM));

		//Installing identifiers
		install(program("VMMC", "VMMC program", _Concept.VMMC, _Program.VMMC));


	}
}