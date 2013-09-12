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

/**
 * Configuration
 */
def constantsClassName = "org.openmrs.module.kenyaemr.Dictionary"

import org.openmrs.ConceptNumeric
import org.openmrs.api.context.Context
import groovy.util.Node
import groovy.util.XmlNodePrinter

def constantsClass = Context.loadClass(constantsClassName)

/**
 * Fetches a concept by UUID or mapping. Return null if concept doesn't exist.
 */
def fetchConcept = { identifier ->
	def concept

	if (identifier.contains(":")) {
		String[] tokens = identifier.split(":")
		concept = Context.conceptService.getConceptByMapping(tokens[1].trim(), tokens[0].trim())
	}
	else {
		// Assume it's a UUID
		concept = Context.conceptService.getConceptByUuid(identifier)
	}

	// getConcept doesn't always return ConceptNumeric for numeric concepts
	if (concept && concept.datatype.numeric && !(concept instanceof ConceptNumeric)) {
		concept = Context.conceptService.getConceptNumeric(concept.id)

		if (concept == null) {
			throw new RuntimeException("Unable to load numeric concept for '" + identifier + "'")
		}
	}

	return concept
}

/**
 * Fetches all concepts referenced by the given constants class
 */
def fetchConceptsFromConstants = { clazz ->
	def concepts = [] as Set
	for (def field : clazz.fields) {
		def constantVal = field.get(null)
		def concept = fetchConcept(constantVal)

		if (concept) {
			concepts.add(concept)
		} else {
			println "Unable to find concept identified by '" + constantVal + "'"
		}
	}
	return concepts
}

/**
 * Returns a copy of a map with all null value entries removed
 */
def withoutNulls = { map -> map.findAll { it.value }  }

/**
 * Creates a dataset from a set of concepts
 */
def createDataSet = { concepts ->
	def datasetNode = new Node(null, "dataset")

	for (def concept : concepts) {
		def attrs = [
				concept_id: concept.id,
				retired: concept.retired ? 1 : 0,
				/*short_name: concept.shortName,*/
				description: concept.description,
				datatype_id: concept.datatype.id,
				class_id: concept.conceptClass.id,
				is_set: concept.set ? 1 : 0,
				creator: concept.creator.id,
				date_created: concept.dateCreated,
				version: concept.version,
				changed_by: concept.changedBy.id,
				date_changed: concept.dateChanged,
				retired_by: concept.retiredBy,
				date_retired: concept.dateRetired,
				retire_reason: concept.retireReason,
				uuid: concept.uuid
		]
		def conceptNode = new Node(datasetNode, "concept", withoutNulls(attrs))
	}

	return datasetNode
}

def concepts = fetchConceptsFromConstants(constantsClass)

def dataset = createDataSet(concepts)

def xmlPrinter = new XmlNodePrinter()
xmlPrinter.print(dataset)