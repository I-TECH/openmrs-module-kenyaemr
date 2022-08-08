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

import org.openmrs.Location;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
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
		public static final String TELEPHONE_LANDLINE = "4ecb5b3f-1518-4056-a266-c4da1def45f5";
		public static final String TELEPHONE_MOBILE = "8760f471-b2bb-4ded-8970-badf95d3bb44";
		public static final String TELEPHONE_FAX = "29e1e758-d03e-4e84-a55e-288fa63d533a";
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
		install(locationAttributeType(
				"Master Facility Code", "Unique facility code allocated by the Ministry of Health",
				RegexValidatedTextDatatype.class, "\\d{5}", 0, 1,
				_LocationAttributeType.MASTER_FACILITY_CODE
		));

		install(locationAttributeType(
				"Official Landline", "Landline telephone contact number",
				FreeTextDatatype.class, "", 0, 1,
				_LocationAttributeType.TELEPHONE_LANDLINE
		));

		install(locationAttributeType(
				"Official Mobile", "Mobile telephone contact number",
				FreeTextDatatype.class, "", 0, 1,
				_LocationAttributeType.TELEPHONE_MOBILE
		));

		install(locationAttributeType(
				"Official Fax", "Fax telephone number",
				FreeTextDatatype.class, "", 0, 1,
				_LocationAttributeType.TELEPHONE_FAX
		));

		if (full) {
			ObjectSource<Location> source = new LocationMflCsvSource("metadata/mfl_2014-05-12.csv");
			sync(source, mflSynchronization);
		}
	}
}