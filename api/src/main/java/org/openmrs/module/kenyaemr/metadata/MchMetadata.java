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
 * MCH metadata bundle
 */
@Component
@Requires({CommonMetadata.class})
public class MchMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String MCHCS_CONSULTATION = "bcc6da85-72f2-4291-b206-789b8186a021";
		public static final String MCHCS_DISCONTINUATION = "5feee3f1-aa16-4513-8bd0-5d9b27ef1208";
		public static final String MCHCS_ENROLLMENT = "415f5136-ca4a-49a8-8db3-f994187c3af6";
		public static final String MCHCS_HEI_COMPLETION = "01894f88-dc73-42d4-97a3-0929118403fb";
		public static final String MCHCS_IMMUNIZATION = "82169b8d-c945-4c41-be62-433dfd9d6c86";
		public static final String MCHMS_ENROLLMENT = "3ee036d8-7c13-4393-b5d6-036f2fe45126";
		public static final String MCHMS_CONSULTATION = "c6d09e05-1f25-4164-8860-9f32c5a02df0";
		public static final String MCHMS_DISCONTINUATION = "7c426cfc-3b47-4481-b55f-89860c21c7de";
	}

	public static final class _Form {
		public static final String MCHCS_ENROLLMENT = "8553d869-bdc8-4287-8505-910c7c998aff";
		public static final String MCHCS_DISCONTINUATION = "1dd02c43-904b-4206-8378-7b1a8414c326";
		public static final String MCHCS_FOLLOW_UP = "755b59e6-acbb-4853-abaf-be302039f902";
		public static final String MCHCS_IMMUNIZATION = "b4f3859e-861c-4a63-bdff-eb7392030d47";
		public static final String MCHCS_HEI_COMPLETION = "d823f1ef-0973-44ee-b113-7090dc23257b";
		public static final String MCHMS_ENROLLMENT = "90a18f0c-17cd-4eec-8204-5af52e8d77cf";
		public static final String MCHMS_ANTENATAL_VISIT = "e8f98494-af35-4bb8-9fc7-c409c8fed843";
		public static final String MCHMS_POSTNATAL_VISIT = "72aa78e0-ee4b-47c3-9073-26f3b9ecc4a7";
		public static final String MCHMS_DELIVERY = "496c7cc3-0eea-4e84-a04c-2292949e2f7f";
		public static final String MCHMS_INFANT_FEEDING = "f4d763bb-8428-476c-be8a-73c705bbc347";
		public static final String MCHMS_PREVENTIVE_SERVICES = "d3ea25c7-a3e8-4f57-a6a9-e802c3565a30";
		public static final String MCHMS_DISCONTINUATION = "25935b9f-68ad-4e0c-9663-d2cacda82bbf";
	}

	public static final class _PatientIdentifierType {
		public static final String HEI_ID_NUMBER = Metadata.IdentifierType.HEI_UNIQUE_NUMBER;
	}

	public static final class _Program {
		public static final String MCHCS = Metadata.Program.MCH_CS;
		public static final String MCHMS = Metadata.Program.MCH_MS;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		///////////////////////////// MCH child services ////////////////////////////////

		install(encounterType("MCH Child Enrollment", "Enrollment of child onto MCH program", _EncounterType.MCHCS_ENROLLMENT));
		install(encounterType("MCH Child Consultation", "Collection of child data during MCH visit", _EncounterType.MCHCS_CONSULTATION));
		install(encounterType("MCH Child HEI Exit", "Reasons that child is exited from HEI", _EncounterType.MCHCS_HEI_COMPLETION));
		install(encounterType("MCH Child Immunization", "Record of child immunizations", _EncounterType.MCHCS_IMMUNIZATION));
		install(encounterType("MCH Child Discontinuation", "Discontinuation of child from MCH program", _EncounterType.MCHCS_DISCONTINUATION));

		install(form("Child Service Enrollment", "MCH-CS Enrollment form", _EncounterType.MCHCS_ENROLLMENT, "1.0", _Form.MCHCS_ENROLLMENT));
		install(form("Child Services Discontinuation", "MCH-CS discontinuation form", _EncounterType.MCHCS_DISCONTINUATION, "1.0", _Form.MCHCS_DISCONTINUATION));
		install(form("MCH Child Follow Up", "MCH-CS follow up form", _EncounterType.MCHCS_CONSULTATION, "1.0", _Form.MCHCS_FOLLOW_UP));
		install(form("Immunization", "MCH-CS immunization form", _EncounterType.MCHCS_IMMUNIZATION, "1.0", _Form.MCHCS_IMMUNIZATION));
		install(form("Child HEI outcomes", "MCH-CS HEI exit form", _EncounterType.MCHCS_HEI_COMPLETION, "1.0", _Form.MCHCS_HEI_COMPLETION));

		install(patientIdentifierType("HEI ID Number", "Assigned to a child patient when enrolling into HEI",
				null, null, null,
				LocationBehavior.NOT_USED, false, _PatientIdentifierType.HEI_ID_NUMBER));

		install(program("MCH - Child Services", "Treatment for children", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, _Program.MCHCS));

		///////////////////////////// MCH mother services ////////////////////////////////

		install(encounterType("MCH Mother Enrollment", "Enrollment of mother onto MCH program", _EncounterType.MCHMS_ENROLLMENT));
		install(encounterType("MCH Mother Consultation", "Collection of mother data during MCH visit", _EncounterType.MCHMS_CONSULTATION));
		install(encounterType("MCH Mother Discontinuation", "Discontinuation of mother from MCH program", _EncounterType.MCHMS_DISCONTINUATION));

		install(form("MCH-MS Enrollment", "MCH-MS Enrollment", _EncounterType.MCHMS_ENROLLMENT, "1.0", _Form.MCHMS_ENROLLMENT));
		install(form("MCH Antenatal Visit", "MCH antenatal visit form", _EncounterType.MCHMS_CONSULTATION, "1.0", _Form.MCHMS_ANTENATAL_VISIT));
		install(form("MCH Postnatal Visit", "MCH postnatal visit form", _EncounterType.MCHMS_CONSULTATION, "1.0", _Form.MCHMS_POSTNATAL_VISIT));
		install(form("Delivery", "MCH-MS delivery form", _EncounterType.MCHMS_CONSULTATION, "1.0", _Form.MCHMS_DELIVERY));
		install(form("Infant Feeding", "MCH-MS infant feeding form", _EncounterType.MCHMS_CONSULTATION, "1.0", _Form.MCHMS_INFANT_FEEDING));
		install(form("Preventive Services", "MCH-MS preventive services form", _EncounterType.MCHMS_CONSULTATION, "1.0", _Form.MCHMS_PREVENTIVE_SERVICES));
		install(form("MCH-MS Discontinuation", "MCH-MS discontinuation form", _EncounterType.MCHMS_DISCONTINUATION, "1.0", _Form.MCHMS_DISCONTINUATION));

		install(program("MCH - Mother Services", "Treatment for mothers", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, _Program.MCHMS));
	}
}