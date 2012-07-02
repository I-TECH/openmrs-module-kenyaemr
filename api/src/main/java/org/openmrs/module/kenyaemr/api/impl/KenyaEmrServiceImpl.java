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

import java.util.HashMap;
import java.util.Map;

import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.LuhnModNIdentifierValidator;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.api.ConfigurationRequiredException;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.report.ReportManager;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementations of business methods for the Kenya EMR application
 */
public class KenyaEmrServiceImpl extends BaseOpenmrsService implements KenyaEmrService {
	
    private static final String OPENMRS_MEDICAL_RECORD_NUMBER_NAME = "Kenya EMR - OpenMRS Medical Record Number";
	private boolean hasBeenConfigured = false;
	
	private Map<String, String> reportDefinitionUuids;
	
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
		
		hasBeenConfigured = isConfiguredDefaultLocation();
		hasBeenConfigured &= isConfiguredOpenmrsIdGenerator();
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
     * @return whether or not an id generator is configured for the OpenMRS MRN
     */
    boolean isConfiguredOpenmrsIdGenerator() {
    	try {
    		IdentifierSource source = getMrnIdentifierSource();
    		return source != null;
    	} catch (ConfigurationRequiredException ex) {
    		return false;
    	}
    }
    
	/**
     * @return the identifier source for MRNs
     */
    @Override
    public IdentifierSource getMrnIdentifierSource() {
    	IdentifierSource source = getIdentifierSource(OPENMRS_MEDICAL_RECORD_NUMBER_NAME);
    	if (source == null) {
    		throw new ConfigurationRequiredException("MRN Identifier Source not configured");
    	}
    	return source;
    }


	/**
     * @param name
     * @return the source with the given name
     */
    private IdentifierSource getIdentifierSource(String name) {
    	for (IdentifierSource source : Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false)) {
    		if (source.getName().equals(name)) {
    			return source;
    		}
    	}
    	return null;
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
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#setupMrnIdentifierSource(java.lang.String)
	 */
	@Override
	public void setupMrnIdentifierSource(String startFrom) {
	    try {
	    	IdentifierSource source = getMrnIdentifierSource();
	    	throw new APIException(source.getName() + " is already set up");
	    }
	    catch (ConfigurationRequiredException ex) {
	    	// this is the good case: we are only allowed to configure this if it isn't set up yet
	    }
	    
    	PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierTypeByUuid(MetadataConstants.OPENMRS_ID_UUID);
    	String validatorClass = idType.getValidator();
    	LuhnModNIdentifierValidator validator;
    	try {
    		validator = (LuhnModNIdentifierValidator) Context.loadClass(validatorClass).newInstance(); 
    	} catch (Exception e) {
    		throw new APIException("Unexpected Identifier Validator (" + validatorClass + ") for " + idType.getName(), e);
    	}
    	
    	if (startFrom == null) {
    		startFrom = validator.getBaseCharacters().substring(0, 1);
    	}

    	IdentifierSourceService idService = Context.getService(IdentifierSourceService.class);

    	SequentialIdentifierGenerator idGen = new SequentialIdentifierGenerator();
    	idGen.setName(OPENMRS_MEDICAL_RECORD_NUMBER_NAME);
    	idGen.setDescription("Identifier Generator for " + idType.getName());
    	idGen.setIdentifierType(idType);
    	idGen.setPrefix("M");
    	idGen.setBaseCharacterSet(validator.getBaseCharacters());
    	idGen.setFirstIdentifierBase(startFrom);
		idService.saveIdentifierSource(idGen);
    	
    	AutoGenerationOption auto = new AutoGenerationOption(idType, idGen, true, true);
	    idService.saveAutoGenerationOption(auto);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getReportDefinition(java.lang.String)
	 */
	@Override
	public ReportDefinition getReportDefinition(String id) {
		String uuid = reportDefinitionUuids.get(id);
		return uuid == null ? null : Context.getService(ReportDefinitionService.class).getDefinitionByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#setupReportDefinitions()
	 */
	@Override
	public void setupReportDefinitions() {
		reportDefinitionUuids = new HashMap<String, String>();
		for (ReportManager manager : Context.getRegisteredComponents(ReportManager.class)) {
			String key = manager.getClass().getName();
			String reportUuid = manager.setup();
			reportDefinitionUuids.put(key, reportUuid);
		}
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#cleanupReportDefinitions()
	 */
	@Override
	@Transactional
	public void cleanupReportDefinitions() {
		reportDefinitionUuids = null;
		for (ReportManager manager : Context.getRegisteredComponents(ReportManager.class)) {
			manager.cleanup();
		}
	}

}
