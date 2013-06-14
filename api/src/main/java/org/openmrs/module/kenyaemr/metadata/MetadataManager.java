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

package org.openmrs.module.kenyaemr.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Metadata package manager
 */
@Component
public class MetadataManager {

	protected static final Log log = LogFactory.getLog(MetadataManager.class);

	/**
	 * Refreshes all metadata
	 */
	public synchronized void refresh() {
		for (MetadataConfiguration configuration : Context.getRegisteredComponents(MetadataConfiguration.class)) {
			log.warn("Loading metadata package set from " + configuration.getModuleId() + ":" + configuration.getPath());

			try {
				ClassLoader loader = configuration.getClassLoader();
				InputStream stream = loader.getResourceAsStream(configuration.getPath());
				loadPackagesFromXML(stream, loader);
			}
			catch (Exception ex) {
				throw new RuntimeException("Cannot find " + configuration.getModuleId() + ":" + configuration.getPath() + ". Make sure it's in api/src/main/resources");
			}
		}
	}

	/**
	 * Loads packages specified in an XML packages list
	 * @param stream the input stream containing the package list
	 * @param loader the class loader to use for loading the packages (null to use the default)
	 * @return whether any changes were made to the db
	 * @throws Exception
	 */
	protected synchronized boolean loadPackagesFromXML(InputStream stream, ClassLoader loader) throws Exception {
		boolean anyChanges = false;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document document = builder.parse(stream);
		Element root = document.getDocumentElement();

		NodeList packageNodes = root.getElementsByTagName("package");
		for (int p = 0; p < packageNodes.getLength(); p++) {
			Element packageElement = (Element)packageNodes.item(p);
			String groupUuid = packageElement.getAttribute("groupUuid");
			String filename = packageElement.getAttribute("filename");

			anyChanges |= installMetadataPackageIfNecessary(groupUuid, filename, loader);
		}

		return anyChanges;
	}

	/**
	 * Gets all imported packages in the system
	 * @return the packages
	 */
	public List<ImportedPackage> getImportedPackages() {
		return Context.getService(MetadataSharingService.class).getAllImportedPackages();
	}

	/**
	 * Checks whether the given version of the MDS package has been installed yet, and if not, install it
	 * @param groupUuid the package group UUID
	 * @param filename the package filename
	 * @param loader the class loader to use for loading the packages (null to use the default)
	 * @return whether any changes were made to the db
	 * @throws IOException
	 */
	protected static boolean installMetadataPackageIfNecessary(String groupUuid, String filename, ClassLoader loader) throws IOException {
		try {
			Matcher matcher = Pattern.compile("[\\w/-]+-(\\d+).zip").matcher(filename);
			if (!matcher.matches())
				throw new RuntimeException("Filename must match PackageNameWithNoSpaces-X.zip");
			Integer version = Integer.valueOf(matcher.group(1));

			ImportedPackage installed = Context.getService(MetadataSharingService.class).getImportedPackageByGroup(groupUuid);
			if (installed != null && installed.getVersion() >= version) {
				log.info("Metadata package " + filename + " is already installed with version " + installed.getVersion());
				return false;
			}

			if (loader == null) {
				loader = MetadataManager.class.getClassLoader();
			}

			if (loader.getResource(filename) == null) {
				throw new RuntimeException("Cannot find " + filename + " for group " + groupUuid);
			}

			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
			metadataImporter.loadSerializedPackageStream(loader.getResourceAsStream(filename));
			metadataImporter.importPackage();
			return true;
		} catch (Exception ex) {
			log.error("Failed to install metadata package " + filename, ex);
			return false;
		}
	}
}