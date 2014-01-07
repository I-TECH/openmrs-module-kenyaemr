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
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.kenyaemr.metadata.sync.LocationMflCsvSource;
import org.openmrs.module.kenyaemr.metadata.sync.LocationMflSynchronization;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.springframework.stereotype.Component;

/**
 * Locations metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class LocationsMetadata extends AbstractMetadataBundle {

	protected static final Log log = LogFactory.getLog(LocationsMetadata.class);

	public static final class _Location {
		public static final String UNKNOWN = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		LocationAttributeType codeAttrType = existing(LocationAttributeType.class, CommonMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		try {
			ObjectSource<Location> source = new LocationMflCsvSource("metadata/mfl_2014-07-01.csv", codeAttrType);
			new LocationMflSynchronization(source, codeAttrType).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}