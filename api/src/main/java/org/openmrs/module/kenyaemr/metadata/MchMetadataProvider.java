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
import org.openmrs.module.kenyaemr.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MCH metadata provider
 */
@Component
@Requires("kenyaemr.common.metadata")
public class MchMetadataProvider extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(MchMetadataProvider.class);

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing MCH metadata");

		installer.program("MCH - Child Services", "Treatment for children", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, Metadata.Program.MCHCS);

		//MCH-MS program
		installer.program("MCH - Mother Services", "Treatment for mothers", Dictionary.MATERNAL_AND_CHILD_HEALTH_PROGRAM, Metadata.Program.MCHMS);

		installer.encounterType("MCH Child Enrollment", "Enrollment of child onto MCH program", Metadata.EncounterType.MCHCS_ENROLLMENT);
		installer.encounterType("MCH Child Consultation", "Collection of child data during MCH visit", Metadata.EncounterType.MCHCS_CONSULTATION);
		//installer.encounterType("MCH Child HEI Exit", "Reasons that child is exited from HEI", Metadata.EncounterType.MCHCS_HEI_EXIT);
		installer.encounterType("MCH Child Immunization", "Record of child immunizations", Metadata.EncounterType.MCHCS_IMMUNIZATION);
		installer.encounterType("MCH Child Discontinuation", "Discontinuation of child from MCH program", Metadata.EncounterType.MCHCS_DISCONTINUATION);

		//MCH-MS encounter types
		installer.encounterType("MCH Mother Enrollment", "Enrollment of mother onto MCH program", Metadata.EncounterType.MCHMS_ENROLLMENT);
		installer.encounterType("MCH Mother Consultation", "Collection of mother data during MCH visit", Metadata.EncounterType.MCHMS_CONSULTATION);
		installer.encounterType("MCH Mother Discontinuation", "Discontinuation of mother from MCH program", Metadata.EncounterType.MCHMS_DISCONTINUATION);

		//MCH-MS forms
		installer.form("MCH-MS Enrollment", "MCH-MS Enrollment", "3ee036d8-7c13-4393-b5d6-036f2fe45126", "1.0", "90a18f0c-17cd-4eec-8204-5af52e8d77cf");
		installer.form("MCH Antenatal Visit", "MCH antenatal visit form", "c6d09e05-1f25-4164-8860-9f32c5a02df0", "1.0", "e8f98494-af35-4bb8-9fc7-c409c8fed843");
		installer.form("MCH Postnatal Visit", "MCH postnatal visit form", "c6d09e05-1f25-4164-8860-9f32c5a02df0", "1.0", "72aa78e0-ee4b-47c3-9073-26f3b9ecc4a7");
		installer.form("Delivery", "MCH-MS delivery form", "c6d09e05-1f25-4164-8860-9f32c5a02df0", "1.0", "496c7cc3-0eea-4e84-a04c-2292949e2f7f");
		installer.form("Infant Feeding", "MCH-MS infant feeding form", "c6d09e05-1f25-4164-8860-9f32c5a02df0", "1.0", "f4d763bb-8428-476c-be8a-73c705bbc347");
		installer.form("Preventive Services", "MCH-MS preventive services form", "c6d09e05-1f25-4164-8860-9f32c5a02df0", "1.0", "d3ea25c7-a3e8-4f57-a6a9-e802c3565a30");
		installer.form("MCH-MS Discontinuation", "MCH-MS discontinuation form", "7c426cfc-3b47-4481-b55f-89860c21c7de", "1.0", "25935b9f-68ad-4e0c-9663-d2cacda82bbf");
	}
}