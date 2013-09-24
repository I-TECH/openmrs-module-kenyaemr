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

package org.openmrs.module.kenyaemr.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.metadata.MetadataConfiguration;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Map;

/**
 * Tests for importing of KenyaEMR metadata packages
 */
public class MetadataIntegrationTest extends BaseModuleContextSensitiveTest {

	protected static final Log log = LogFactory.getLog(MetadataIntegrationTest.class);

	@Autowired
	private MetadataConfiguration metadataConfiguration;

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	@Autowired
	private ProgramManager programManager;

	@Autowired
	private FormManager formManager;

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
	 *
	 * Not currently working due to suspected issue in metadatasharing
	 */
	@Ignore
	@Test
	@SkipBaseSetup
	public void loadAllMetadataPackages() throws Exception {
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
	 * Tests...
	 */
	@Test
	@SkipBaseSetup
	public void loadAllMetadataProvidersAndRefreshManagers() {
		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
		mchMetadata.install();

		// Easiest way to check that we're not missing any programs, forms or encounter types
		programManager.refresh();
		formManager.refresh();
	}
}