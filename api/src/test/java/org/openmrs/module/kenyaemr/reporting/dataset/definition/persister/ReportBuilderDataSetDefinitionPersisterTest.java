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

package org.openmrs.module.kenyaemr.reporting.dataset.definition.persister;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.reporting.ReportManager;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for {@link ReportBuilderDataSetDefinitionPersister}
 */
public class ReportBuilderDataSetDefinitionPersisterTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ReportManager reportManager;

	@Autowired
	private ReportBuilderDataSetDefinitionPersister persister;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		reportManager.refresh();
	}

	/**
	 * @see ReportBuilderDataSetDefinitionPersister#getAllDefinitions(boolean)
	 */
	@Test
	public void getAllDefinitions_shouldListAllKenyaEmrDSDs() throws Exception {
		List<DataSetDefinition> allDefinitions = persister.getAllDefinitions(true);

		Assert.assertThat(allDefinitions, notNullValue());
		Assert.assertThat(allDefinitions.size(), not(0));
	}
}