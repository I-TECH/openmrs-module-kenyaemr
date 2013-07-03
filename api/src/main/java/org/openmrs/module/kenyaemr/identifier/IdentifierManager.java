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

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.LuhnModNIdentifierValidator;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Patient identifier manager
 */
@Component
public class IdentifierManager {

	protected Map<String, IdentifierDescriptor> identifiers = new LinkedHashMap<String, IdentifierDescriptor>();

	/**
	 * Updates this manager after context refresh
	 */
	public synchronized void refresh() {
		identifiers.clear();

		List<IdentifierDescriptor> descriptors = Context.getRegisteredComponents(IdentifierDescriptor.class);

		// Sort by identifier descriptor order
		Collections.sort(descriptors);

		for (IdentifierDescriptor descriptor : descriptors) {
			PatientIdentifierType identifierType = Metadata.getPatientIdentifierType(descriptor.getIdentifierTypeUuid());

			identifiers.put(identifierType.getUuid(), descriptor);
		}
	}

	/**
	 * Gets the identifiers to be displayed for the given patient
	 * @param patient the patient
	 * @return the identifiers
	 */
	public List<PatientIdentifier> getPatientDisplayIdentifiers(Patient patient) {
		List<PatientIdentifier> displayIds = getPatientIdentifiers(patient, true);

		if (displayIds.size() == 0) {
			displayIds = getPatientIdentifiers(patient, false);
		}

		return displayIds;
	}

	/**
	 * Gets the identifiers to be displayed for the given patient
	 * @param patient the patient
	 * @param withOrder true if only identifiers with order values should be returned
	 * @return the identifiers
	 */
	public List<PatientIdentifier> getPatientIdentifiers(Patient patient, boolean withOrder) {
		List<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>();

		for (IdentifierDescriptor descriptor : this.identifiers.values()) {
			PatientIdentifierType identifierType = Metadata.getPatientIdentifierType(descriptor.getIdentifierTypeUuid());
			PatientIdentifier identifier = patient.getPatientIdentifier(identifierType);

			if (identifier != null && (!withOrder || descriptor.getOrder() != null)) {
				identifiers.add(identifier);
			}
		}

		return identifiers;
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
	 * TODO Everything below here needs refactored to put all metadata dependencies into the application context
	 */

	protected static final String OPENMRS_MEDICAL_RECORD_NUMBER_NAME = "Kenya EMR - OpenMRS Medical Record Number";
	protected static final String HIV_UNIQUE_PATIENT_NUMBER_NAME = "Kenya EMR - OpenMRS HIV Unique Patient Number";

	/**
	 * Gets whether all identifier types are configured
	 * @return true if all types are configured
	 */
	public boolean isConfigured() {
		return getMrnIdentifierSource() != null && getHivUniqueIdentifierSource() != null;
	}

	/**
	 * Gets the medical record number identifier source
	 * @return the identifier source
	 */
	public IdentifierSource getMrnIdentifierSource() {
		return getIdentifierSource(OPENMRS_MEDICAL_RECORD_NUMBER_NAME);
	}

	/**
	 * Gets the unique patient number identifier source
	 * @return the identifier source
	 */
	public IdentifierSource getHivUniqueIdentifierSource() {
		return getIdentifierSource(HIV_UNIQUE_PATIENT_NUMBER_NAME);
	}

	/**
	 * Setup the medical record number identifier source
	 * @param startFrom the base identifier to start from
	 */
	public void setupMrnIdentifierSource(String startFrom) {
		if (getMrnIdentifierSource() != null) {
			throw new APIException("MRN identifier source is already set up");
		}

		PatientIdentifierType idType = Metadata.getPatientIdentifierType(Metadata.OPENMRS_ID_IDENTIFIER_TYPE);
		setupIdentifierSource(idType, startFrom, OPENMRS_MEDICAL_RECORD_NUMBER_NAME, null, "M");
	}

	/**
	 * Setup the unique patient number identifier source
	 * @param startFrom the base identifier to start from
	 */
	public void setupHivUniqueIdentifierSource(String startFrom) {
		if (getHivUniqueIdentifierSource() != null) {
			throw new APIException("UPN identifier source is already set up");
		}

		if (startFrom == null) {
			startFrom = "00001";
		}

		PatientIdentifierType idType = Metadata.getPatientIdentifierType(Metadata.UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE);
		setupIdentifierSource(idType, startFrom, HIV_UNIQUE_PATIENT_NUMBER_NAME, "0123456789", null);
	}

	/**
	 * Generates the next unique patient number identifier value
	 * @param comment the reference comment
	 * @return the identifier value
	 */
	public String getNextHivUniquePatientNumber(String comment) {
		if (comment == null) {
			comment = "KenyaEMR Service";
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
}