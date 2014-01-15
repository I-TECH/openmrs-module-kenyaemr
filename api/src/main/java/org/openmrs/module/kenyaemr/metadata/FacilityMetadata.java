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

import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.customdatatype.datatype.RegexValidatedTextDatatype;
import org.openmrs.module.kenyaemr.metadata.sync.LocationMflCsvSource;
import org.openmrs.module.kenyaemr.metadata.sync.LocationMflSynchronization;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttributeType;

/**
 * Locations metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class FacilityMetadata extends AbstractMetadataBundle {

	@Autowired
	private LocationMflSynchronization mflSynchronization;

	public static final class _Location {
		public static final String UNKNOWN = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	}

	public static final class _LocationAttributeType {
		public static final String MASTER_FACILITY_CODE = "8a845a89-6aa5-4111-81d3-0af31c45c002";
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() throws Exception {
		install(true);
	}

	/**
	 * Provides an install method we can use from unit tests when we don't want to sync the entire facility list
	 * @param full whether or not to run the facility sync
	 * @throws Exception
	 */
	public void install(boolean full) throws Exception {
		LocationAttributeType codeAttrType = install(locationAttributeType("Master Facility Code", "Unique facility code allocated by the Ministry of Health",
				RegexValidatedTextDatatype.class, "\\d{5}", 0, 1, _LocationAttributeType.MASTER_FACILITY_CODE));

		if (full) {
			ObjectSource<Location> source = new LocationMflCsvSource("metadata/mfl_2014-07-01.csv", codeAttrType);
			sync(source, mflSynchronization);
		}
	}
}