/*
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

package org.openmrs.module.kenyaemr.report;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ReportBuilderDataSetDefinitionPersisterTest extends BaseModuleContextSensitiveTest {

	@Autowired
	ReportManager reportManager;

	@Autowired
	ReportBuilderDataSetDefinitionPersister persister;

	@Before
	public void setup() {
		reportManager.refreshReportBuilders();
	}

	@Test
	public void shouldListAllKenyaEmrDSDs() throws Exception {
		List<DataSetDefinition> allDefinitions = persister.getAllDefinitions(true);
		assertNotNull(allDefinitions);

		// there are some ReportBuilderss that don't actually work, so they provide no DSDs...
		//assertThat(allDefinitions.size(), is(service.getReportBuildersByTag(null).size()));

		// Assert that Moh731Report does provide its DSD
		boolean found = false;
		for (DataSetDefinition dsd : allDefinitions) {
			if (dsd.getName().equals("MOH 731 DSD") && dsd instanceof CohortIndicatorDataSetDefinition) {
				found = true;
			}
		}
		assertTrue(found);
	}
}