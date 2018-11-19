/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.converter.StringToProviderConverter}
 */
public class StringToProviderConverterTest extends BaseModuleWebContextSensitiveTest {

	private StringToProviderConverter converter = new StringToProviderConverter();

	/**
	 * @see org.openmrs.module.kenyaemr.converter.StringToVisitConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertString() {
		Assert.assertNull(converter.convert(null));
		Assert.assertNull(converter.convert(""));
		Assert.assertThat(converter.convert("1"), is(Context.getProviderService().getProvider(1)));
	}
}