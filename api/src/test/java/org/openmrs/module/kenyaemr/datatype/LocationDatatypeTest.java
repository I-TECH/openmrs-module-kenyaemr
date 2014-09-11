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

package org.openmrs.module.kenyaemr.datatype;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link LocationDatatype}
 */
public class LocationDatatypeTest extends BaseModuleContextSensitiveTest {

	private LocationDatatype datatype = new LocationDatatype();

	/**
	 * @see LocationDatatype#deserialize(String)
	 */
	@Test
	public void deserialize() {
		Assert.assertThat(datatype.deserialize(null), is(nullValue()));
		Assert.assertThat(datatype.deserialize(""), is(nullValue()));
		Assert.assertThat(datatype.deserialize("2"), is(Context.getLocationService().getLocation(2)));
	}

	/**
	 * @see LocationDatatype#serialize(org.openmrs.Location)
	 */
	@Test
	public void serialize() {
		Assert.assertNull(datatype.serialize(null));
		Assert.assertThat(datatype.serialize(null), is(nullValue()));
		Assert.assertThat(datatype.serialize(Context.getLocationService().getLocation(2)), is("2"));
	}
}