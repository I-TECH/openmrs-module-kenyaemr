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
 * MCH metadata provider
 */
@Component("kenyaemr.mch.metadata")
@Requires("kenyaemr.common.metadata")
public class MchMetadata extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(MchMetadata.class);

	public static final class EncounterType {
		public static final String MCHCS_CONSULTATION = "bcc6da85-72f2-4291-b206-789b8186a021";
		public static final String MCHCS_DISCONTINUATION = "5feee3f1-aa16-4513-8bd0-5d9b27ef1208";
		public static final String MCHCS_ENROLLMENT = "415f5136-ca4a-49a8-8db3-f994187c3af6";
		public static final String MCHCS_HEI_COMPLETION = "01894f88-dc73-42d4-97a3-0929118403fb";
		public static final String MCHCS_IMMUNIZATION = "82169b8d-c945-4c41-be62-433dfd9d6c86";
		public static final String MCHMS_ENROLLMENT = "3ee036d8-7c13-4393-b5d6-036f2fe45126";
		public static final String MCHMS_CONSULTATION = "c6d09e05-1f25-4164-8860-9f32c5a02df0";
		public static final String MCHMS_DISCONTINUATION = "7c426cfc-3b47-4481-b55f-89860c21c7de";
	}

	public static final class Form {
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

	public static final class Program {
		public static final String MCHCS = "c2ecdf11-97cd-432a-a971-cfd9bd296b83";
		public static final String MCHMS = "b5d9e05f-f5ab-4612-98dd-adb75438ed34";
	}

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing MCH metadata");

		// MCH child services

		installer.encounterType("MCH Child Enrollment", "Enrollment of child onto MCH program", EncounterType.MCHCS_ENROLLMENT);
		installer.encounterType("MCH Child Consultation", "Collection of child data during MCH visit", EncounterType.MCHCS_CONSULTATION);
		installer.encounterType("MCH Child HEI Exit", "Reasons that child is exited from HEI", EncounterType.MCHCS_HEI_COMPLETION);
		installer.encounterType("MCH Child Immunization", "Record of child immunizations", EncounterType.MCHCS_IMMUNIZATION);
		installer.encounterType("MCH Child Discontinuation", "Discontinuation of child from MCH program", EncounterType.MCHCS_DISCONTINUATION);

		installer.form("Child Service Enrollment", "MCH-CS Enrollment form", EncounterType.MCHCS_ENROLLMENT, "1.0", Form.MCHCS_ENROLLMENT);
		installer.form("Child Services Discontinuation", "MCH-CS discontinuation form", EncounterType.MCHCS_DISCONTINUATION, "1.0", Form.MCHCS_DISCONTINUATION);
		installer.form("MCH Child Follow Up", "MCH-CS follow up form", EncounterType.MCHCS_CONSULTATION, "1.0", Form.MCHCS_FOLLOW_UP);
		installer.form("Immunization", "MCH-CS immunization form", EncounterType.MCHCS_IMMUNIZATION, "1.0", Form.MCHCS_IMMUNIZATION);
		installer.form("Child HEI outcomes", "MCH-CS HEI exit form", EncounterType.MCHCS_HEI_COMPLETION, "1.0", Form.MCHCS_HEI_COMPLETION);

		installer.program("MCH - Child Services", "Treatment for children", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, Program.MCHCS);

		// MCH mother services

		installer.encounterType("MCH Mother Enrollment", "Enrollment of mother onto MCH program", EncounterType.MCHMS_ENROLLMENT);
		installer.encounterType("MCH Mother Consultation", "Collection of mother data during MCH visit", EncounterType.MCHMS_CONSULTATION);
		installer.encounterType("MCH Mother Discontinuation", "Discontinuation of mother from MCH program", EncounterType.MCHMS_DISCONTINUATION);

		installer.form("MCH-MS Enrollment", "MCH-MS Enrollment", EncounterType.MCHMS_ENROLLMENT, "1.0", Form.MCHMS_ENROLLMENT);
		installer.form("MCH Antenatal Visit", "MCH antenatal visit form", EncounterType.MCHMS_CONSULTATION, "1.0", Form.MCHMS_ANTENATAL_VISIT);
		installer.form("MCH Postnatal Visit", "MCH postnatal visit form", EncounterType.MCHMS_CONSULTATION, "1.0", Form.MCHMS_POSTNATAL_VISIT);
		installer.form("Delivery", "MCH-MS delivery form", EncounterType.MCHMS_CONSULTATION, "1.0", Form.MCHMS_DELIVERY);
		installer.form("Infant Feeding", "MCH-MS infant feeding form", EncounterType.MCHMS_CONSULTATION, "1.0", Form.MCHMS_INFANT_FEEDING);
		installer.form("Preventive Services", "MCH-MS preventive services form", EncounterType.MCHMS_CONSULTATION, "1.0", Form.MCHMS_PREVENTIVE_SERVICES);
		installer.form("MCH-MS Discontinuation", "MCH-MS discontinuation form", EncounterType.MCHMS_DISCONTINUATION, "1.0", Form.MCHMS_DISCONTINUATION);

		installer.program("MCH - Mother Services", "Treatment for mothers", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, Program.MCHMS);
	}
}