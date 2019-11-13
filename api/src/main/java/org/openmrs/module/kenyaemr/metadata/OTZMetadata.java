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

import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * OTZ metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class OTZMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String OTZ_DISCONTINUATION = "162382b8-0464-11ea-9a9f-362b9e155667";
		public static final String OTZ_ENROLLMENT = "16238574-0464-11ea-9a9f-362b9e155667";
		public static final String OTZ_ACTIVITY = "162386c8-0464-11ea-9a9f-362b9e155667";
	}

	public static final class _Form {
		public static final String OTZ_DISCONTINUATION_FORM = "3ae955dc-0464-11ea-8d71-362b9e155667";
		public static final String OTZ_ENROLLMENT_FORM = "3ae95898-0464-11ea-8d71-362b9e155667";
		public static final String OTZ_ACTIVITY_FORM = "3ae95d48-0464-11ea-8d71-362b9e155667";
	}


	public static final class _Program {
		public static final String OTZ = Metadata.Program.OTZ;
	}

	public static final class _Concept {

		public static final String OTZ = "6533f692-d1bd-457d-b260-24675d5aba58";
	}

	/**
	 * @see AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("OTZ Enrollment", "Enrollment onto TB program", _EncounterType.OTZ_ENROLLMENT));
		install(encounterType("OTZ Discontinuation", "Discontinuation from TB program", _EncounterType.OTZ_DISCONTINUATION));
		install(encounterType("OTZ Activity", "Consultation in TB Program", _EncounterType.OTZ_ACTIVITY));

		install(form("OTZ Enrollment Form", null, _EncounterType.OTZ_ENROLLMENT, "1", _Form.OTZ_ENROLLMENT_FORM));
		install(form("OTZ Discontinuation Form", null, _EncounterType.OTZ_DISCONTINUATION, "1", _Form.OTZ_DISCONTINUATION_FORM));
		install(form("OTZ Activity Form" ,null, _EncounterType.OTZ_ACTIVITY, "1", _Form.OTZ_ACTIVITY_FORM));


		install(program("OTZ", "OTZ program", _Concept.OTZ, _Program.OTZ));


	}
}