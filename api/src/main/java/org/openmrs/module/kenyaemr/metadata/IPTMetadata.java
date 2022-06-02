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

import org.openmrs.PatientIdentifierType.LocationBehavior;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.patientIdentifierType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;

/**
 * TPT metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class IPTMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String IPT_OUTCOME = "bb77c683-2144-48a5-a011-66d904d776c9";
		public static final String IPT_INITIATION = "de5cacd4-7d15-4ad0-a1be-d81c77b6c37d";
		public static final String IPT_FOLLOWUP = "aadeafbe-a3b1-4c57-bc76-8461b778ebd6";
	}

	public static final class _Form {
		public static final String IPT_OUTCOME = "5bdd3b65-8b7b-46a0-9f7b-dfe764143848";
		public static final String IPT_INITIATION = "61ea2a72-b0f9-47cf-ae86-443f88656acc";
		public static final String IPT_FOLLOWUP = "9d0e4be8-ab72-4394-8df7-b509b9d45179";
	}

	public static final class _PatientIdentifierType {
		public static final String DISTRICT_REG_NUMBER = Metadata.IdentifierType.DISTRICT_REGISTRATION_NUMBER;
	}

	public static final class _Program {
		public static final String IPT = "335517a1-04bc-438b-9843-1ba49fb7fcd9";
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("IPT Initiation", "Initiation into IPT ", _EncounterType.IPT_INITIATION));
		install(encounterType("IPT Outcome", "Discontinuation from IPT", _EncounterType.IPT_OUTCOME));
		install(encounterType("IPT FollowUp", "Follow up in IPT", _EncounterType.IPT_FOLLOWUP));

		install(form("TPT Initiation", null, _EncounterType.IPT_INITIATION, "1", _Form.IPT_INITIATION));
		install(form("TPT Outcome", null, _EncounterType.IPT_OUTCOME, "1", _Form.IPT_OUTCOME));
		install(form("TPT FollowUp" ,null, _EncounterType.IPT_FOLLOWUP, "1", _Form.IPT_FOLLOWUP));

		install(patientIdentifierType("Subcounty Registration Number", "Assigned to every IPT patient",
				null, null, null,
				LocationBehavior.NOT_USED, false, _PatientIdentifierType.DISTRICT_REG_NUMBER));

		install(program("TPT", "Tuberculosis Preventive Therapy (TPT)", Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM, _Program.IPT));
	}
}