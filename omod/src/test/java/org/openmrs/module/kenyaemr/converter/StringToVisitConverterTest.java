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

package org.openmrs.module.kenyaemr.converter;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyautil.test.TestUtils;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.converter.StringToVisitConverter}
 */
public class StringToVisitConverterTest extends BaseModuleWebContextSensitiveTest {

	private StringToVisitConverter converter;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		converter = new StringToVisitConverter();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.converter.StringToVisitConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertString() {
		Assert.assertNull(converter.convert(null));
		Assert.assertNull(converter.convert(""));

		// Check actual visit
		Patient patient = Context.getPatientService().getPatient(7);
		VisitType outpatientType = MetadataUtils.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE);
		Visit visit = TestUtils.saveVisit(patient, outpatientType, TestUtils.date(2012, 1, 1), null);

		Assert.assertEquals(visit, converter.convert(visit.getVisitId().toString()));
	}
}