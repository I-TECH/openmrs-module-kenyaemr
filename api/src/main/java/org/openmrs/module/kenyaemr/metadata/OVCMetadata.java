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

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.patientIdentifierType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.relationshipType;

/**
 * OVC metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class OVCMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String OVC_DISCONTINUATION = "5cf00d9e-09da-11ea-8d71-362b9e155667";
		public static final String OVC_ENROLLMENT = "5cf0124e-09da-11ea-8d71-362b9e155667";
	}

	public static final class _Form {
		public static final String OVC_DISCONTINUATION_FORM = "5cf013e8-09da-11ea-8d71-362b9e155667";
		public static final String OVC_ENROLLMENT_FORM = "5cf01528-09da-11ea-8d71-362b9e155667";
	}


	public static final class _Program {
		public static final String OVC = Metadata.Program.OVC;
	}

	public static final class _Concept {

		public static final String OVC = "7dbb5b43-861e-4816-a88f-caa976ac2050";
	}

	public static final class _PatientIdentifierType {

		public static final String CPIMS_NUMBER = "5065ae70-0b61-11ea-8d71-362b9e155667";

	}
	public static final class _RelationshipType {
		public static final String CAREGIVER = "a8058424-5ddf-4ce2-a5ee-6e08d01b5960";

	}

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("OVC Enrollment", "Enrollment onto OVC program", _EncounterType.OVC_ENROLLMENT));
		install(encounterType("OVC Discontinuation", "Discontinuation from OVC program", _EncounterType.OVC_DISCONTINUATION));

		install(form("OVC Enrollment Form", null, _EncounterType.OVC_ENROLLMENT, "1", _Form.OVC_ENROLLMENT_FORM));
		install(form("OVC Discontinuation Form", null, _EncounterType.OVC_DISCONTINUATION, "1", _Form.OVC_DISCONTINUATION_FORM));


		//Installing identifiers

		install(patientIdentifierType("CPIMS Number", "OVC client CPIMS number ", null, null, null,
				PatientIdentifierType.LocationBehavior.NOT_USED, false, _PatientIdentifierType.CPIMS_NUMBER));

		//Installing relationship
		install(relationshipType("Care-giver", "Care-giver", "One that gives care, watches over, or protects", _RelationshipType.CAREGIVER));

		install(program("OVC", "OVC program", _Concept.OVC, _Program.OVC));


	}
}