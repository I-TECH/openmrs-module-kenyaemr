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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.openmrs.module.kenyaemr.report.IndicatorReportManager;


/**
 * Implementations of business methods for the Kenya EMR application
 */
public class KenyaEmrServiceImpl extends BaseOpenmrsService implements KenyaEmrService {
	
    private static final String OPENMRS_MEDICAL_RECORD_NUMBER_NAME = "Kenya EMR - OpenMRS Medical Record Number";
    private static final String HIV_UNIQUE_PATIENT_NUMBER_NAME = "Kenya EMR - OpenMRS HIV Unique Patient Number";
	private boolean hasBeenConfigured = false;
	
	// maps classname to manager instance
	private Map<String, IndicatorReportManager> reportManagers;
	
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
		hasBeenConfigured &= isConfiguredHivUniqueIdGenerator();
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
     * @return whether or not an id generator is configured for the OpenMRS MRN
     */
    boolean isConfiguredHivUniqueIdGenerator() {
    	try {
    		IdentifierSource source = getHivUniqueIdentifierSource();
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
     * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getHivUniqueIdentifierSource()
     */
    @Override
    public IdentifierSource getHivUniqueIdentifierSource() throws ConfigurationRequiredException {
    	IdentifierSource source = getIdentifierSource(HIV_UNIQUE_PATIENT_NUMBER_NAME);
    	if (source == null) {
    		throw new ConfigurationRequiredException("HIV Unique Patient Number Identifier Source not configured");
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

	private void setupIdentifierSource(String startFrom, String name, PatientIdentifierType idType, String baseCharacterSet) {
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
    	idGen.setName(name);
    	idGen.setDescription("Identifier Generator for " + idType.getName());
    	idGen.setIdentifierType(idType);
    	if (OPENMRS_MEDICAL_RECORD_NUMBER_NAME.equals(name)) {
    		idGen.setPrefix("M");
    	}
    	idGen.setBaseCharacterSet(baseCharacterSet);
    	idGen.setFirstIdentifierBase(startFrom);
		idService.saveIdentifierSource(idGen);
    	
    	AutoGenerationOption auto = new AutoGenerationOption(idType, idGen, true, true);
	    idService.saveAutoGenerationOption(auto);

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
    	setupIdentifierSource(startFrom, OPENMRS_MEDICAL_RECORD_NUMBER_NAME, idType, null);
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#setupHivUniqueIdentifierSource(java.lang.String)
	 */
	@Override
	public void setupHivUniqueIdentifierSource(String startFrom) {
		try {
	    	IdentifierSource source = getHivUniqueIdentifierSource();
	    	throw new APIException(source.getName() + " is already set up");
	    }
	    catch (ConfigurationRequiredException ex) {
	    	// this is the good case: we are only allowed to configure this if it isn't set up yet
	    }
		if (startFrom == null) {
			startFrom = "00001";
		}
	    
    	PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID);
    	setupIdentifierSource(startFrom, HIV_UNIQUE_PATIENT_NUMBER_NAME, idType, "0123456789");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#refreshReportManagers()
	 */
	@Override
	public void refreshReportManagers() {
		synchronized(this) {
			reportManagers = new LinkedHashMap<String, IndicatorReportManager>();
			for (IndicatorReportManager manager : Context.getRegisteredComponents(IndicatorReportManager.class)) {
				reportManagers.put(manager.getClass().getName(), manager);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getReportManager(java.lang.String)
	 */
	@Override
	public IndicatorReportManager getReportManager(String className) {
	    return reportManagers.get(className);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getAllReportManagers()
	 */
	@Override
	public List<IndicatorReportManager> getAllReportManagers() {
	    return new ArrayList<IndicatorReportManager>(reportManagers.values());
	}
}
