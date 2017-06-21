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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * IPT metadata bundle
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

		install(form("IPT Initiation", null, _EncounterType.IPT_INITIATION, "1", _Form.IPT_INITIATION));
		install(form("IPT Outcome", null, _EncounterType.IPT_OUTCOME, "1", _Form.IPT_OUTCOME));
		install(form("IPT FollowUp" ,null, _EncounterType.IPT_FOLLOWUP, "1", _Form.IPT_FOLLOWUP));

		install(patientIdentifierType("District Registration Number", "Assigned to every TB patient",
				null, null, null,
				LocationBehavior.NOT_USED, false, _PatientIdentifierType.DISTRICT_REG_NUMBER));

		install(program("IPT", "Isoniazid Preventive Therapy (IPT)", Dictionary.TUBERCULOSIS_TREATMENT_PROGRAM, _Program.IPT));
	}
}