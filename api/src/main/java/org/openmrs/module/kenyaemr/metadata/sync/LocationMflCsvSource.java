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

package org.openmrs.module.kenyaemr.metadata.sync;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.metadatadeploy.source.AbstractCsvResourceSource;

import java.io.IOException;

/**
 * Location source from Master Facility CSV resource
 */
public class LocationMflCsvSource extends AbstractCsvResourceSource<Location> {

	private LocationAttributeType codeAttrType;

	/**
	 * Constructs a new location source
	 * @param csvFile the csv resource path
	 * @param codeAttrType the attribute type for the MFL code
	 */
	public LocationMflCsvSource(String csvFile, LocationAttributeType codeAttrType) throws IOException {
		super(csvFile, true);

		this.codeAttrType = codeAttrType;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.source.AbstractCsvResourceSource#parseLine(String[])
	 */
	@Override
	public Location parseLine(String[] line) {
		String code = line[0];
		String name = line[1];
		String province = line[2];
		String type = line[6];

		Location location = new Location();
		location.setName(name);
		location.setStateProvince(province);
		location.setDescription(type);
		location.setCountry("Kenya");

		if (StringUtils.isNotEmpty(code)) {
			LocationAttribute codeAttr = new LocationAttribute();
			codeAttr.setAttributeType(codeAttrType);
			codeAttr.setValue(code.trim());
			codeAttr.setOwner(location);
			location.addAttribute(codeAttr);
		}

		return location;
	}
}