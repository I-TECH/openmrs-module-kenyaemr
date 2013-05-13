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

package org.openmrs.module.kenyaemr.identifier;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.LuhnModNIdentifierValidator;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.api.ConfigurationRequiredException;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class IdentifierManager {

	private static final String OPENMRS_MEDICAL_RECORD_NUMBER_NAME = "Kenya EMR - OpenMRS Medical Record Number";
	private static final String HIV_UNIQUE_PATIENT_NUMBER_NAME = "Kenya EMR - OpenMRS HIV Unique Patient Number";

	/**
	 * Gets whether all identifier types are configured
	 * @return true if all types are configured
	 */
	public boolean isConfigured() {
		try {
			getMrnIdentifierSource();
			getHivUniqueIdentifierSource();
			return true;
		} catch (ConfigurationRequiredException ex) {
			return false;
		}
	}

	/**
	 * Gets the medical record number identifier source
	 * @return the identifier source
	 */
	public IdentifierSource getMrnIdentifierSource() {
		IdentifierSource source = getIdentifierSource(OPENMRS_MEDICAL_RECORD_NUMBER_NAME);
		if (source == null) {
			throw new ConfigurationRequiredException("MRN Identifier Source not configured");
		}
		return source;
	}

	/**
	 * Gets the unique patient number identifier source
	 * @return the identifier source
	 */
	public IdentifierSource getHivUniqueIdentifierSource() throws ConfigurationRequiredException {
		IdentifierSource source = getIdentifierSource(HIV_UNIQUE_PATIENT_NUMBER_NAME);
		if (source == null) {
			throw new ConfigurationRequiredException("HIV Unique Patient Number Identifier Source not configured");
		}
		return source;
	}

	/**
	 * Setup the medical record number identifier source
	 * @param startFrom the base identifier to start from
	 */
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
	 * Setup the unique patient number identifier source
	 * @param startFrom the base identifier to start from
	 */
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
	 * Generates the next unique patient number identifier value
	 * @param comment the reference comment
	 * @return the identifier value
	 */
	public String getNextHivUniquePatientNumber(String comment) {
		if (comment == null) {
			comment = "Kenya EMR Service";
		}
		IdentifierSource source = getHivUniqueIdentifierSource();
		String prefix = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();
		String sequentialNumber = Context.getService(IdentifierSourceService.class).generateIdentifier(source, comment);
		return prefix + sequentialNumber;
	}

	/**
	 * Gets the identifier source with the given name
	 * @param name the source name
	 * @return the source
	 */
	protected IdentifierSource getIdentifierSource(String name) {
		for (IdentifierSource source : Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false)) {
			if (source.getName().equals(name)) {
				return source;
			}
		}
		return null;
	}

	/**
	 * Setup an identifier source
	 * @param startFrom the base identifier to start from
	 * @param name the identifier source name
	 * @param idType the patient identifier type
	 * @param baseCharacterSet the base character set
	 */
	protected void setupIdentifierSource(String startFrom, String name, PatientIdentifierType idType, String baseCharacterSet) {
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

		SequentialIdentifierGenerator idGen;
		if (OPENMRS_MEDICAL_RECORD_NUMBER_NAME.equals(name)) {
			idGen = new SequentialIdentifierGenerator();
			idGen.setPrefix("M");
		} else if (HIV_UNIQUE_PATIENT_NUMBER_NAME.equals(name)) {
			// Can't do this because it can't be persisted to hibernate
			// idGen = new HivUniquePatientNumberGenerator();
			idGen = new SequentialIdentifierGenerator();
		} else {
			throw new RuntimeException("Programming error: don't know how to create identifier source: " + name);
		}

		idGen.setName(name);
		idGen.setDescription("Identifier Generator for " + idType.getName());
		idGen.setIdentifierType(idType);
		idGen.setBaseCharacterSet(baseCharacterSet);
		idGen.setFirstIdentifierBase(startFrom);
		idService.saveIdentifierSource(idGen);

		AutoGenerationOption auto = new AutoGenerationOption(idType, idGen, true, true);
		idService.saveAutoGenerationOption(auto);
	}
}