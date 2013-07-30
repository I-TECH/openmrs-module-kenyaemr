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

package org.openmrs.module.kenyacore.identifier;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyacore.ContentManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Patient identifier manager
 */
@Component
public class IdentifierManager implements ContentManager {

	protected Map<String, IdentifierDescriptor> identifiers = new LinkedHashMap<String, IdentifierDescriptor>();

	/**
	 * Updates this manager after context refresh
	 */
	@Override
	public synchronized void refresh() {
		identifiers.clear();

		List<IdentifierDescriptor> descriptors = Context.getRegisteredComponents(IdentifierDescriptor.class);

		// Sort by identifier descriptor order
		Collections.sort(descriptors);

		for (IdentifierDescriptor descriptor : descriptors) {
			if (identifiers.containsKey(descriptor.getTargetUuid())) {
				throw new RuntimeException("Identifier type " + descriptor.getTargetUuid() + " already registered");
			}

			identifiers.put(descriptor.getTargetUuid(), descriptor);
		}
	}

	/**
	 * Gets all registered identifier descriptors
	 * @return the identifier descriptors
	 */
	public Collection<IdentifierDescriptor> getAllIdentifierDescriptors() {
		return identifiers.values();
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
			PatientIdentifierType identifierType = descriptor.getTarget();
			PatientIdentifier identifier = patient.getPatientIdentifier(identifierType);

			if (identifier != null && !identifier.isVoided() && (!withOrder || descriptor.getOrder() != null)) {
				identifiers.add(identifier);
			}
		}

		return identifiers;
	}

	/**
	 * Gets the identifier source for the specified identifier (if there is one).
	 * @param identifierType the identifier type
	 * @return the identifier source
	 */
	public IdentifierSource getIdentifierSource(PatientIdentifierType identifierType) {
		for (IdentifierSource source : Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false)) {
			if (source.getIdentifierType().equals(identifierType)) {
				return source;
			}
		}
		return null;
	}
}