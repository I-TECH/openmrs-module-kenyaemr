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

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.ConfigurationRequiredException;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.api.db.KenyaEmrDAO;
import org.openmrs.module.kenyaemr.identifier.IdentifierManager;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementations of business methods for the Kenya EMR application
 */
public class KenyaEmrServiceImpl extends BaseOpenmrsService implements KenyaEmrService {

	protected static final Log log = LogFactory.getLog(KenyaEmrServiceImpl.class);

	@Autowired
	private IdentifierManager identifierManager;

	private boolean hasBeenConfigured = false;

	private KenyaEmrDAO dao;

	/**
	 * Method used to inject the data access object.
	 * @param dao the data access object.
	 */
	public void setKenyaEmrDAO(KenyaEmrDAO dao) {
		this.dao = dao;
	}
	
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

		hasBeenConfigured = isConfiguredDefaultLocation() && identifierManager.isConfigured();
		return hasBeenConfigured;
	}
	

	/**
     * @return whether or not the defaultLocation is configured
     */
    boolean isConfiguredDefaultLocation() {
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
		try {
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);

			GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(KenyaEmrConstants.GP_DEFAULT_LOCATION);
			if (gp != null) {
				Location ret = (Location) gp.getValue();
				if (ret != null) {
					return ret;
				}
			}
			throw new ConfigurationRequiredException("Global Property: " + KenyaEmrConstants.GP_DEFAULT_LOCATION);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
		}
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getDefaultLocationMflCode()
	 */
	@Override
	public String getDefaultLocationMflCode() {
		try {
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_LOCATION_ATTRIBUTE_TYPES);

			LocationAttributeType mflCodeAttrType = Metadata.getLocationAttributeType(Metadata.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE);
			Location location = getDefaultLocation();
			List<LocationAttribute> list = location.getActiveAttributes(mflCodeAttrType);
			if (list.size() == 0) {
				throw new ConfigurationRequiredException("Default location (" + location.getName() + ") does not have an " + mflCodeAttrType.getName());
			}
			return (String) list.get(0).getValue();
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_LOCATION_ATTRIBUTE_TYPES);
		}
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getLocationByMflCode(String)
	 */
	@Override
	public Location getLocationByMflCode(String mflCode) {
		LocationAttributeType mflCodeAttrType = Metadata.getLocationAttributeType(Metadata.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE);
		Map<LocationAttributeType, Object> attrVals = new HashMap<LocationAttributeType, Object>();
		attrVals.put(mflCodeAttrType, mflCode);

		List<Location> locations = getLocations(null, null, attrVals, false, null, null);

		return locations.size() > 0 ? locations.get(0) : null;
	}

	/**
	 * @see KenyaEmrService#getVisitsByPatientAndDay(org.openmrs.Patient, java.util.Date)
	 */
	@Override
	public List<Visit> getVisitsByPatientAndDay(Patient patient, Date date) {
		Date startOfDay = OpenmrsUtil.firstSecondOfDay(date);
		Date endOfDay = OpenmrsUtil.getLastMomentOfDay(date);

		// look for visits that started before endOfDay and ended after startOfDay
		List<Visit> visits = Context.getVisitService().getVisits(null, Collections.singleton(patient), null, null, null, endOfDay, startOfDay, null, null, true, false);
		Collections.reverse(visits); // We want by date asc
		return visits;
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getLocations(String, org.openmrs.Location, java.util.Map, boolean, Integer, Integer)
	 *
	 * NEEDS MOVED INTO LocationServiceImpl
	 */
	@Override
	public List<Location> getLocations(String nameFragment, Location parent, Map<LocationAttributeType, Object> attributeValues, boolean includeRetired, Integer start, Integer length) {
		Map<LocationAttributeType, String> serializedAttributeValues = CustomDatatypeUtil.getValueReferences(attributeValues);

		return dao.getLocations(nameFragment, parent, serializedAttributeValues, includeRetired, start, length);
	}
}