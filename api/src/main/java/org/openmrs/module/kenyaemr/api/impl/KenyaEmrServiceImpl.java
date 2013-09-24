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
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.LuhnModNIdentifierValidator;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.api.db.KenyaEmrDAO;
import org.openmrs.module.kenyacore.identifier.IdentifierManager;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementations of business logic methods for KenyaEMR
 */
public class KenyaEmrServiceImpl extends BaseOpenmrsService implements KenyaEmrService {

	protected static final Log log = LogFactory.getLog(KenyaEmrServiceImpl.class);

	protected static final String OPENMRS_MEDICAL_RECORD_NUMBER_NAME = "Kenya EMR - OpenMRS Medical Record Number";
	protected static final String HIV_UNIQUE_PATIENT_NUMBER_NAME = "Kenya EMR - OpenMRS HIV Unique Patient Number";

	@Autowired
	private IdentifierManager identifierManager;

	private boolean setupRequired = true;

	private KenyaEmrDAO dao;

	/**
	 * Method used to inject the data access object.
	 * @param dao the data access object.
	 */
	public void setKenyaEmrDAO(KenyaEmrDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#isSetupRequired()
	 */
	@Override
	public boolean isSetupRequired() {
		// Assuming that it's not possible to _un_configure after having configured, i.e. after the first
		// time we return true we can save time by not re-checking things
		if (!setupRequired) {
			return false;
		}

		boolean defaultLocationConfigured = getDefaultLocation() != null;
		boolean mrnConfigured = identifierManager.getIdentifierSource(MetadataUtils.getPatientIdentifierType(CommonMetadata.PatientIdentifierType.OPENMRS_ID)) != null;
		boolean upnConfigured = identifierManager.getIdentifierSource(MetadataUtils.getPatientIdentifierType(CommonMetadata.PatientIdentifierType.UNIQUE_PATIENT_NUMBER)) != null;

		setupRequired = !(defaultLocationConfigured && mrnConfigured && upnConfigured);
		return setupRequired;
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#setDefaultLocation(org.openmrs.Location)
	 */
	@Override
	public void setDefaultLocation(Location location) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(EmrConstants.GP_DEFAULT_LOCATION);
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

			GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(EmrConstants.GP_DEFAULT_LOCATION);
			return gp != null ? ((Location) gp.getValue()) : null;
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

			LocationAttributeType mflCodeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata.LocationAttributeType.MASTER_FACILITY_CODE);
			Location location = getDefaultLocation();
			if (location != null) {
				List<LocationAttribute> list = location.getActiveAttributes(mflCodeAttrType);
				return (list.size() > 0) ? ((String) list.get(0).getValue()) : null;
			}
			return null;
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
		LocationAttributeType mflCodeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata.LocationAttributeType.MASTER_FACILITY_CODE);
		Map<LocationAttributeType, Object> attrVals = new HashMap<LocationAttributeType, Object>();
		attrVals.put(mflCodeAttrType, mflCode);

		List<Location> locations = getLocations(null, null, attrVals, false, null, null);

		return locations.size() > 0 ? locations.get(0) : null;
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getNextHivUniquePatientNumber(String)
	 */
	@Override
	public String getNextHivUniquePatientNumber(String comment) {
		if (comment == null) {
			comment = "KenyaEMR Service";
		}

		PatientIdentifierType upnType = MetadataUtils.getPatientIdentifierType(CommonMetadata.PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		IdentifierSource source = identifierManager.getIdentifierSource(upnType);

		String prefix = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();
		String sequentialNumber = Context.getService(IdentifierSourceService.class).generateIdentifier(source, comment);
		return prefix + sequentialNumber;
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
	 * @see KenyaEmrService#setupMrnIdentifierSource(String)
	 */
	@Override
	public void setupMrnIdentifierSource(String startFrom) {
		PatientIdentifierType idType = MetadataUtils.getPatientIdentifierType(CommonMetadata.PatientIdentifierType.OPENMRS_ID);
		setupIdentifierSource(idType, startFrom, OPENMRS_MEDICAL_RECORD_NUMBER_NAME, null, "M");
	}

	/**
	 * @see KenyaEmrService#setupHivUniqueIdentifierSource(String)
	 */
	@Override
	public void setupHivUniqueIdentifierSource(String startFrom) {
		PatientIdentifierType idType = MetadataUtils.getPatientIdentifierType(CommonMetadata.PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		setupIdentifierSource(idType, startFrom, HIV_UNIQUE_PATIENT_NUMBER_NAME, "0123456789", null);
	}

	/**
	 * Setup an identifier source
	 * @param idType the patient identifier type
	 * @param startFrom the base identifier to start from
	 * @param name the identifier source name
	 * @param baseCharacterSet the base character set
	 * @param prefix the prefix
	 */
	protected void setupIdentifierSource(PatientIdentifierType idType, String startFrom, String name, String baseCharacterSet, String prefix) {
		if (identifierManager.getIdentifierSource(idType) != null) {
			throw new APIException("Identifier source is already exists for " + idType.getName());
		}

		String validatorClass = idType.getValidator();
		LuhnModNIdentifierValidator validator = null;
		if (validatorClass != null) {
			try {
				validator = (LuhnModNIdentifierValidator) Context.loadClass(validatorClass).newInstance();
			} catch (Exception e) {
				throw new APIException("Unexpected Identifier Validator (" + validatorClass + ") for " + idType.getName(), e);
			}
		}

		if (startFrom == null) {
			if (validator != null) {
				startFrom = validator.getBaseCharacters().substring(0, 1);
			} else {
				throw new RuntimeException("startFrom is required if this isn't using a LuhnModNIdentifierValidator");
			}
		}

		if (baseCharacterSet == null) {
			baseCharacterSet = validator.getBaseCharacters();
		}

		IdentifierSourceService idService = Context.getService(IdentifierSourceService.class);

		SequentialIdentifierGenerator idGen = new SequentialIdentifierGenerator();
		idGen.setPrefix(prefix);
		idGen.setName(name);
		idGen.setDescription("Identifier Generator for " + idType.getName());
		idGen.setIdentifierType(idType);
		idGen.setBaseCharacterSet(baseCharacterSet);
		idGen.setFirstIdentifierBase(startFrom);
		idService.saveIdentifierSource(idGen);

		AutoGenerationOption auto = new AutoGenerationOption(idType, idGen, true, true);
		idService.saveAutoGenerationOption(auto);
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