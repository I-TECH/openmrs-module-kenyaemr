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
import org.openmrs.module.kenyacore.metadata.installer.CoreMetadataInstaller;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Common metadata provider
 */
@Component("kenyaemr.common.metadata")
public class CommonMetadataProvider extends AbstractMetadataProvider {

	protected static final Log log = LogFactory.getLog(CommonMetadataProvider.class);

	@Autowired
	private CoreMetadataInstaller installer;

	@Override
	public void install() {
		log.info("Installing common metadata");

		installer.globalProperty(EmrConstants.GP_DEFAULT_LOCATION, "The facility for which this installation is configured",
				LocationDatatype.class, null, Metadata.GlobalProperty.DEFAULT_LOCATION);

		installer.encounterType("Registration", "Initial data collection for a patient, not specific to any program", Metadata.EncounterType.REGISTRATION);
		installer.encounterType("Triage", "Collection of limited data prior to a more thorough examination", Metadata.EncounterType.TRIAGE);
		installer.encounterType("Consultation", "Collection of clinical data during the main consultation", Metadata.EncounterType.CONSULTATION);
		installer.encounterType("Lab Results", "Collection of laboratory results", Metadata.EncounterType.LAB_RESULTS);

		installer.form("Surgical and Medical History", null, Metadata.EncounterType.REGISTRATION, "1", Metadata.Form.SURGICAL_AND_MEDICAL_HISTORY);
		installer.form("Obstetric History", null, Metadata.EncounterType.REGISTRATION, "1", Metadata.Form.OBSTETRIC_HISTORY);
		installer.form("Triage", null, Metadata.EncounterType.TRIAGE, "1", Metadata.Form.TRIAGE);
		installer.form("Clinical Encounter", null, Metadata.EncounterType.CONSULTATION, "1", Metadata.Form.CLINICAL_ENCOUNTER);
		installer.form("Lab Results", "Collection of laboratory results", Metadata.EncounterType.LAB_RESULTS, "1", Metadata.Form.LAB_RESULTS);
		installer.form("Other Medications", "Recording of non-regimen medications", Metadata.EncounterType.CONSULTATION, "1", Metadata.Form.OTHER_MEDICATIONS);
		installer.form("Progress Note", "For additional information - mostly complaints and examination findings.", Metadata.EncounterType.CONSULTATION, "1", Metadata.Form.PROGRESS_NOTE);

		installer.visitType("Outpatient", "Visit where the patient is not admitted to the hospital", Metadata.VisitType.OUTPATIENT);
	}
}