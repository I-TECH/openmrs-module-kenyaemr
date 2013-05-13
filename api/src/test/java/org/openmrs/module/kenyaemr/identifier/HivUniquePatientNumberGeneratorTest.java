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

package org.openmrs.module.kenyaemr.identifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.api.ConfigurationRequiredException;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link HivUniquePatientNumberGenerator}
 */
public class HivUniquePatientNumberGeneratorTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	/**
	 * @see HivUniquePatientNumberGenerator#getIdentifierForSeed(long)
	 */
	@Test
	public void getIdentifierForSeed() {
		HivUniquePatientNumberGenerator generator = new HivUniquePatientNumberGenerator();
		generator.setBaseCharacterSet("0123456789");
		generator.setFirstIdentifierBase("00001");
		generator.setIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID));

		try {
			generator.getIdentifierForSeed(45);
			Assert.fail();
		}
		catch (ConfigurationRequiredException ex) {}

		// MFL code for location 2 is 15002
		TestUtils.saveGlobalProperty("kenyaemr.defaultLocation", Context.getLocationService().getLocation(2), LocationDatatype.class);
		Assert.assertEquals("1500200123", generator.getIdentifierForSeed(123));

		// MFL code for location 3 is 15003
		TestUtils.saveGlobalProperty("kenyaemr.defaultLocation", Context.getLocationService().getLocation(3), LocationDatatype.class);
		Assert.assertEquals("1500300345", generator.getIdentifierForSeed(345));
	}
}