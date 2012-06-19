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
package org.openmrs.module.kenyaemr.api.impl;

import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;


/**
 * Implementations of business methods for the Kenya EMR application
 */
public class KenyaEmrServiceImpl extends BaseOpenmrsService implements KenyaEmrService {
	
	private boolean hasBeenConfigured = false;
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#isConfigured()
	 */
	@Override
	public boolean isConfigured() {
		// Assuming that it's not possible to _un_configure after having configured, i.e. after the first
		// time we return true we can save time by not re-checking things

		if (hasBeenConfigured) {
			return true;
		}
		
		try {
			getDefaultLocation();
			hasBeenConfigured = true;
			return true;
		} catch (ConfigurationRequiredException ex) {
			return false;
		}
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#setDefaultLocation(org.openmrs.Location)
	 */
	@Override
	public void setDefaultLocation(Location location) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(KenyaEmrConstants.GP_DEFAULT_LOCATION);
		gp.setValue(location);
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getDefaultLocation()
	 */
	@Override
	public Location getDefaultLocation() {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(KenyaEmrConstants.GP_DEFAULT_LOCATION);
		if (gp != null) {
			Location ret = (Location) gp.getValue();
			if (ret != null) {
				return ret;
			}
		}
		throw new ConfigurationRequiredException("Global Property: " + KenyaEmrConstants.GP_DEFAULT_LOCATION);
	}
	
}
