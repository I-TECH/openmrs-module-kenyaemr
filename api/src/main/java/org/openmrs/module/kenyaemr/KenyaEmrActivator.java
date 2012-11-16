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


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaEmrActivator implements ModuleActivator {
	
	private static final String METADATA_FILENAME_FORMS = "Kenya_EMR_Forms-32.zip";
    private static final String METADATA_FILENAME_CORE = "Kenya_EMR_Core_Metadata-8.zip";
    private static final String METADATA_FILENAME_ROLES = "Kenya_EMR_Roles_and_Privileges-1.zip";
    private static final String METADATA_FILENAME_LOCATIONS = "Kenya_Facility_List-1.zip";
    
	protected Log log = LogFactory.getLog(getClass());
		
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing Kenya OpenMRS EMR Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Kenya OpenMRS EMR Module refreshed");
		Context.getService(KenyaEmrService.class).refreshReportManagers();
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting Kenya OpenMRS EMR Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 * @should install initial data only once
	 */
	public void started() {
		setupGlobalProperties();
		try {
			setupInitialData();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to setup initial data", ex);
		}
		Context.getService(KenyaEmrService.class).refreshReportManagers();

		FormManager.setupStandardForms();

		log.info("Kenya OpenMRS EMR Module started");
	}
	

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping Kenya OpenMRS EMR Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Kenya OpenMRS EMR Module stopped");
	}
		
	/**
	 * Public for testing
	 */
	public void setupGlobalProperties() {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(KenyaEmrConstants.GP_DEFAULT_LOCATION);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(KenyaEmrConstants.GP_DEFAULT_LOCATION);
			gp.setDescription("The facility for which this installation is configured. Visits and encounters will be created with this location value.");
			gp.setDatatypeClassname(LocationDatatype.class.getName());
			Context.getAdministrationService().saveGlobalProperty(gp);
		}
	}
	
    /**
     * Public for testing
     * 
     * @return whether any changes were made to the db
     * @throws Exception
     */
    public boolean setupInitialData() throws Exception {
    	boolean anyChanges = false;
    	anyChanges |= installMetadataPackageIfNecessary("c66d041c-563e-4438-83eb-ad5f32c6e97a", METADATA_FILENAME_FORMS);
    	anyChanges |= installMetadataPackageIfNecessary("d4b71375-f64a-442d-a0c2-9f507c432925", METADATA_FILENAME_ROLES);
    	anyChanges |= installMetadataPackageIfNecessary("29177ba6-a634-42d5-9314-e12689856ff1", METADATA_FILENAME_CORE);
    	anyChanges |= installMetadataPackageIfNecessary("659bc9bc-f293-4d22-9e53-6358762d9e3f", METADATA_FILENAME_LOCATIONS);
    	return anyChanges;
    }

    /**
     * Checks whether the given version of the MDS package has been installed yet, and if not, install it
     * 
     * @param groupUuid
     * @param filename
     * @return whether any changes were made to the db
     * @throws IOException 
     */
    private boolean installMetadataPackageIfNecessary(String groupUuid, String filename) throws IOException {
    	//NameWithNoSpaces-v1.zip
    	try {
			Matcher matcher = Pattern.compile("\\w+-(\\d+).zip").matcher(filename);
			if (!matcher.matches())
				throw new RuntimeException("Filename must match PackageNameWithNoSpaces-1.zip");
			Integer version = Integer.valueOf(matcher.group(1));
			
			ImportedPackage installed = Context.getService(MetadataSharingService.class).getImportedPackageByGroup(groupUuid);
			if (installed != null && installed.getVersion() >= version) {
				log.info("Metadata package " + filename + " is already installed with version " + installed.getVersion());
				return false;
			}
			
			if (getClass().getClassLoader().getResource(filename) == null) {
				throw new RuntimeException("Cannot find " + filename + " for group " + groupUuid + ". Make sure it's in api/src/main/resources");
			}
			
			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
			metadataImporter.loadSerializedPackageStream(getClass().getClassLoader().getResourceAsStream(filename));
			metadataImporter.importPackage();
			return true;
    	} catch (Exception ex) {
    		log.error("Failed to install metadata package " + filename, ex);
    		return false;
    	}
    }

}
