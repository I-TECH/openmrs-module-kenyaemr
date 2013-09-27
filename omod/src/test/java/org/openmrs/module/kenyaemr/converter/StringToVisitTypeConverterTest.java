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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.VisitType;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link StringToVisitTypeConverter}
 */
public class StringToVisitTypeConverterTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	private StringToVisitTypeConverter converter;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		commonMetadata.install();

		converter = new StringToVisitTypeConverter();
	}

	/**
	 * @see StringToVisitConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertString() {
		Assert.assertThat(converter.convert(null), is(nullValue()));
		Assert.assertThat(converter.convert(""), is(nullValue()));

		// Check actual visit type
		VisitType outpatient = MetadataUtils.getVisitType(CommonMetadata._VisitType.OUTPATIENT);

		Assert.assertThat(converter.convert(outpatient.getVisitTypeId().toString()), is(outpatient));
	}
}