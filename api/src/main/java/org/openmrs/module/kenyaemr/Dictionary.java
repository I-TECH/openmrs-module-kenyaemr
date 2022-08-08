/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MissingMetadataException;

import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary for concepts used by KenyaEMR
 */
public class Dictionary extends Metadata.Concept {

	/**
	 * Gets a concept by an identifier (mapping or UUID)
	 * @param identifier the identifier
	 * @return the concept
	 * @throws org.openmrs.module.metadatadeploy.MissingMetadataException if the concept could not be found
	 */
	public static Concept getConcept(String identifier) {
		Concept concept;

		if (identifier.contains(":")) {
			String[] tokens = identifier.split(":");
			concept = Context.getConceptService().getConceptByMapping(tokens[1].trim(), tokens[0].trim());
		}
		else {
			// Assume it's a UUID
			concept = Context.getConceptService().getConceptByUuid(identifier);
		}

		if (concept == null) {
			throw new MissingMetadataException(Concept.class, identifier);
		}

		// getConcept doesn't always return ConceptNumeric for numeric concepts
		if (concept.getDatatype().isNumeric() && !(concept instanceof ConceptNumeric)) {
			concept = Context.getConceptService().getConceptNumeric(concept.getId());

			if (concept == null) {
				throw new MissingMetadataException(ConceptNumeric.class, identifier);
			}
		}

		return concept;
	}

	/**
	 * Convenience method to fetch a list of concepts
	 * @param identifiers the concept identifiers
	 * @return the concepts
	 * @throws org.openmrs.module.metadatadeploy.MissingMetadataException if a concept could not be found
	 */
	public static List<Concept> getConcepts(String... identifiers) {
		List<Concept> concepts = new ArrayList<Concept>();
		for (String identifier : identifiers) {
			concepts.add(getConcept(identifier));
		}
		return concepts;
	}
}