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
	}

	public static final class _Form {
		public static final String VMMC_DISCONTINUATION_FORM = "bc6a9e7d-58f7-43c0-8334-d8011fef4000";
		public static final String VMMC_ENROLLMENT_FORM = "a74e3e4a-9e2a-41fb-8e64-4ba8a71ff984";
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

		install(form("VMMC Enrollment Form", null, _EncounterType.VMMC_ENROLLMENT, "1", _Form.VMMC_ENROLLMENT_FORM));
		install(form("VMMC Discontinuation Form", null, _EncounterType.VMMC_DISCONTINUATION, "1", _Form.VMMC_DISCONTINUATION_FORM));


		//Installing identifiers
		install(program("VMMC", "VMMC program", _Concept.VMMC, _Program.VMMC));


	}
}