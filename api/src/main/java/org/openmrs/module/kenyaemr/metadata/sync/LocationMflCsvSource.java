/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.metadata.sync;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.kenyaemr.metadata.FacilityMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.source.AbstractCsvResourceSource;

import java.io.IOException;

/**
 * Location source from Master Facility CSV resource
 */
public class LocationMflCsvSource extends AbstractCsvResourceSource<Location> {

	private LocationAttributeType codeAttrType, landlineAttrType, mobileAttrType, faxAttrType;

	/**
	 * Constructs a new location source
	 * @param csvFile the csv resource path
	 */
	public LocationMflCsvSource(String csvFile) throws IOException {
		super(csvFile, true);

		this.codeAttrType = MetadataUtils.existing(LocationAttributeType.class, FacilityMetadata._LocationAttributeType.MASTER_FACILITY_CODE);
		this.landlineAttrType = MetadataUtils.existing(LocationAttributeType.class, FacilityMetadata._LocationAttributeType.TELEPHONE_LANDLINE);
		this.faxAttrType = MetadataUtils.existing(LocationAttributeType.class, FacilityMetadata._LocationAttributeType.TELEPHONE_FAX);
		this.mobileAttrType = MetadataUtils.existing(LocationAttributeType.class, FacilityMetadata._LocationAttributeType.TELEPHONE_MOBILE);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.source.AbstractCsvResourceSource#parseLine(String[])
	 */
	@Override
	public Location parseLine(String[] line) {
		String code = line[0];
		String name = line[1];
		String province = line[2];
		String county = line[3];
		String district = line[4];
		String division = line[5];
		String type = line[6];
		String landline = line[15];
		String fax = line[16];
		String mobile = line[17];
		String postcode = line[22];

		Location location = new Location();
		location.setName(name);
		location.setDescription(type);

		// Facility address
		location.setAddress5(division);
		location.setAddress6(district);
		location.setCountyDistrict(county);
		location.setStateProvince(province);
		location.setCountry("Kenya");
		location.setPostalCode(postcode);

		setAsAttribute(location, codeAttrType, code);
		setAsAttribute(location, landlineAttrType, landline);
		setAsAttribute(location, faxAttrType, fax);
		setAsAttribute(location, mobileAttrType, mobile);

		return location;
	}

	/**
	 * Adds a value as an attribute if it's not blank
	 * @param location the location
	 * @param type the attribute type
	 * @param value the value
	 */
	protected void setAsAttribute(Location location, LocationAttributeType type, String value) {
		if (StringUtils.isNotBlank(value)) {
			LocationAttribute attr = new LocationAttribute();
			attr.setAttributeType(type);
			attr.setValue(value.trim());
			attr.setOwner(location);
			location.addAttribute(attr);
		}
	}
}