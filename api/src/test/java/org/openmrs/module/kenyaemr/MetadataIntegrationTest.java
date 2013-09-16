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
import org.junit.Test;
import org.openmrs.module.kenyacore.metadata.MetadataConfiguration;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
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

	@Test
	@SkipBaseSetup
	public void testMetadataPackageLoading() throws Exception {
		initializeInMemoryDatabase();
		//executeDataSet(INITIAL_XML_DATASET_PACKAGE_PATH);
		executeDataSet("dataset/test-minimal.xml");
		authenticate();

		for (Map.Entry<String, String> entry : metadataConfiguration.getPackages().entrySet()) {
			//String groupUuid = entry.getKey();
			String filename = entry.getValue();

			if (filename.contains("Core") || filename.contains("Locations") || filename.contains("Drugs")) {
				log.warn("Skipping package " + filename);
				continue;
			}
			else {
				log.warn("Importing package " + filename);
			}

			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);

			metadataImporter.loadSerializedPackageStream(inputStream);
			metadataImporter.importPackage();
		}
	}
}