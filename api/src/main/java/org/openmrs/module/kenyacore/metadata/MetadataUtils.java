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

package org.openmrs.module.kenyacore.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;

/**
 * Utility methods for fail-fast fetching of metadata
 */
public class MetadataUtils {

	/**
	 * Gets the specified concept (by mapping or UUID)
	 * @param identifier the mapping or UUID
	 * @return the concept
	 * @throws IllegalArgumentException if no such concept could be found
	 */
	public static Concept getConcept(String identifier) {
		Concept concept = null;

		if (identifier.contains(":")) {
			String[] tokens = identifier.split(":");
			concept = Context.getConceptService().getConceptByMapping(tokens[1].trim(), tokens[0].trim());
		}
		else {
			// Assume its a UUID
			concept = Context.getConceptService().getConceptByUuid(identifier);
		}

		if (concept == null) {
			throw new IllegalArgumentException("No concept with identifier '" + identifier + "'");
		}

		return concept;
	}

	/**
	 * Gets the specified encounter type
	 * @param uuid the uuid
	 * @return the encounter type
	 * @throws IllegalArgumentException if no such encounter type exists
	 */
	public static EncounterType getEncounterType(String uuid) {
		EncounterType ret = Context.getEncounterService().getEncounterTypeByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such encounter type with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified form
	 * @param uuid the uuid
	 * @return the form
	 */
	public static Form getForm(String uuid) {
		Form ret = Context.getFormService().getFormByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such form with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified location
	 * @param uuid the identifier
	 * @return the location
	 */
	public static Location getLocation(String uuid) {
		Location ret = Context.getLocationService().getLocationByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such location with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified location attribute type
	 * @param uuid the uuid
	 * @return the location attribute type
	 */
	public static LocationAttributeType getLocationAttributeType(String uuid) {
		LocationAttributeType ret = Context.getLocationService().getLocationAttributeTypeByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such location attribute type with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified patient identifier type
	 * @param uuid the uuid
	 * @return the patient identifier type
	 * @throws IllegalArgumentException if no such patient identifier type exists
	 */
	public static PatientIdentifierType getPatientIdentifierType(String uuid) {
		PatientIdentifierType ret = Context.getPatientService().getPatientIdentifierTypeByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such patient identifier type with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified person attribute type
	 * @param uuid the uuid
	 * @return the person attribute type
	 */
	public static PersonAttributeType getPersonAttributeType(String uuid) {
		PersonAttributeType ret = Context.getPersonService().getPersonAttributeTypeByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such person attribute type with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified program
	 * @param uuid the uuid
	 * @return the program
	 */
	public static Program getProgram(String uuid) {
		Program ret = Context.getProgramWorkflowService().getProgramByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such program with identifier " + uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified visit type
	 * @param uuid the uuid
	 * @return the visit type
	 * @throws IllegalArgumentException if no such visit type exists
	 */
	public static VisitType getVisitType(String uuid) {
		VisitType ret = Context.getVisitService().getVisitTypeByUuid(uuid);
		if (ret == null) {
			throw new IllegalArgumentException("No such visit type with identifier " + uuid);
		}
		return ret;
	}
}