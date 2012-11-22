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
package org.openmrs.module.kenyaemr.util;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.*;

public class KenyaEmrUtilsTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see KenyaEmrUtils#getModuleBuildProperties()
	 * @verifies return build properties
	 */
	@Test
	public void getModuleBuildProperties_shouldGetBuildProperties() {
		Map<String, String> properties = KenyaEmrUtils.getModuleBuildProperties();

		Assert.assertNotNull(properties);
		Assert.assertNotNull(properties.get("buildDate"));
		Assert.assertNotNull(properties.get("developer"));
	}

	/**
	 * @see KenyaEmrUtils#dateOnly(java.util.Date)
	 * @verifies clear time information from date
	 */
	@Test
	public void dateOnly_shouldClearTimeInformation() {
		Date now = new Date();
		Date dateOnly = KenyaEmrUtils.dateOnly(now);
		Calendar cal = new GregorianCalendar();
		cal.setTime(dateOnly);

		Assert.assertEquals(0, cal.get(Calendar.AM_PM));
		Assert.assertEquals(0, cal.get(Calendar.HOUR));
		Assert.assertEquals(0, cal.get(Calendar.MINUTE));
		Assert.assertEquals(0, cal.get(Calendar.SECOND));
		Assert.assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	/**
	 * @see KenyaEmrUtils#fetchConcepts(java.util.Collection)
	 * @verifies fetch from concepts, integers or strings
	 */
	@Test
	public void fetchConcepts_shouldFetchFromConceptsIntegersOrStrings() {
		Concept cd4 = Context.getConceptService().getConcept(5497);
		List<Object> conceptsOrIds = new ArrayList<Object>();
		conceptsOrIds.add(cd4);
		conceptsOrIds.add(5497);
		conceptsOrIds.add("5497");
		List<Concept> concepts = KenyaEmrUtils.fetchConcepts(conceptsOrIds);
		Assert.assertEquals(cd4, concepts.get(0));
		Assert.assertEquals(cd4, concepts.get(1));
		Assert.assertEquals(cd4, concepts.get(2));
	}

	/**
	 * @see KenyaEmrUtils#fetchConcepts(java.util.Collection)
	 * @verifies throw exception for non concepts, integers or strings
	 */
	@Test
	public void fetchConcepts_shouldThrowExceptionForNonConceptsIntegersOrString() {
		try {
			KenyaEmrUtils.fetchConcepts(Collections.singletonList(new Date()));
			Assert.fail();
		}
		catch (IllegalArgumentException ex) {
		}
	}
}