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

package org.openmrs.module.kenyaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataConfiguration;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Tests for importing of KenyaEMR metadata packages
 */
public class MetadataIntegrationTest extends BaseModuleContextSensitiveTest {

	protected static final Log log = LogFactory.getLog(MetadataIntegrationTest.class);

	@Autowired
	private MetadataConfiguration metadataConfiguration;

	@Before
	public void setup() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("dataset/test-minimal.xml");
		executeDataSet("dataset/test-concepts.xml");
		authenticate();

		// Required to run these tests from an IDE as values in OpenmrsConstants won't have been set correctly
		if (OpenmrsConstants.OPENMRS_VERSION.startsWith("Sun")) {
			TestUtils.modifyConstant(OpenmrsConstants.class, "OPENMRS_VERSION", "1.9.3  Build f535e9");
			TestUtils.modifyConstant(OpenmrsConstants.class, "OPENMRS_VERSION_SHORT", "1.9.3.f535e9");
		}
	}

	/**
	 * Tests loading of all standard KenyaEMR metadata packages (except the locations package because that takes ~20 mins)
	 */
	@Test
	@SkipBaseSetup
	public void testAllStandardPackages() throws Exception {
		for (Map.Entry<String, String> entry : metadataConfiguration.getPackages().entrySet()) {
			String groupUuid = entry.getKey();
			String filename = entry.getValue();

			if (filename.contains("Locations")) {
				log.warn("Skipping package " + filename);
				continue;
			}
			else {
				log.info("Importing package " + filename + "(" + groupUuid + ")");
			}

			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);

			metadataImporter.loadSerializedPackageStream(inputStream);
			metadataImporter.importPackage();
		}
	}

	/**
	 * Demonstrates problem with updating existing program objects
	 */
	@Ignore
	@Test
	@SkipBaseSetup
	public void testProgramLoadingFromCorePackage() throws Exception {
		PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackageStream(ClassLoader.getSystemResourceAsStream("metadata/KenyaEMR_Core-38.zip"));
		metadataImporter.importPackage();

		Program mchmsProgram = Context.getProgramWorkflowService().getProgramByUuid(Metadata.Program.MCHMS);
		Assert.assertThat(mchmsProgram, is(notNullValue()));
		Assert.assertThat(mchmsProgram.getName(), is("MCH Program - Maternal Services"));

		mchmsProgram.setName("XXX");
		Context.getProgramWorkflowService().saveProgram(mchmsProgram);

		Context.flushSession();
		Context.clearSession();

		metadataImporter = MetadataSharing.getInstance().newPackageImporter();
		metadataImporter.loadSerializedPackageStream(ClassLoader.getSystemResourceAsStream("metadata/KenyaEMR_Core-38.zip"));
		metadataImporter.importPackage();

		Context.flushSession();
		Context.clearSession();

		mchmsProgram = Context.getProgramWorkflowService().getProgramByUuid(Metadata.Program.MCHMS);
		Assert.assertThat(mchmsProgram, is(notNullValue()));
		Assert.assertThat(mchmsProgram.getName(), is("MCH Program - Maternal Services"));
	}
}