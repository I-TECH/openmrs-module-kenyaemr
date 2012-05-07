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
import org.openmrs.Location;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaEmrActivator implements ModuleActivator {
	
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
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting Kenya OpenMRS EMR Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		setupInitialData();
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
		
    private void setupInitialData() {
    	setupVisitType(MetadataConstants.HIV_VISIT_TYPE_UUID, "HIV Consult", "Patient came for an HIV consult");
    	setupVisitType(MetadataConstants.OUTPATIENT_CONSULT_VISIT_TYPE_UUID, "Outpatient Consult", "Patient came for a general consult");
    	setupVisitType(MetadataConstants.HOSPITALIZATION_VISIT_TYPE_UUID, "Hospitalization", "Patient was admitted to the hospital");
    	setupLocation(MetadataConstants.UNKNOWN_LOCATION_UUID, "Unknown Location", null);
    }

    private void setupLocation(String uuid, String name, String description) {
    	Location md = Context.getLocationService().getLocationByUuid(uuid);
	    if (md == null) {
	    	md = new Location();
	    	md.setUuid(uuid);
	    }
	    md.setName(name);
	    md.setDescription(description);
	    Context.getLocationService().saveLocation(md);
    }

	private void setupVisitType(String uuid, String name, String description) {
	    VisitType md = Context.getVisitService().getVisitTypeByUuid(uuid);
	    if (md == null) {
	    	md = new VisitType();
	    	md.setUuid(uuid);
	    }
	    md.setName(name);
	    md.setDescription(description);
	    Context.getVisitService().saveVisitType(md);
    }

}
