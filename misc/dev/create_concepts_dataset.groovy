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
def includeLocales = [ "en" ] // Set null or empty to include all locales

import org.openmrs.ConceptNumeric
import org.openmrs.api.context.Context
import groovy.util.Node
import groovy.util.XmlNodePrinter

/**
 * Extracts concept identifiers from a class of constants
 */
def identifiersFromClass = { clazz ->
	def identifiers = [] as Set
	for (def field : clazz.fields) {
		identifiers.add(field.get(null))
	}
	return identifiers
}

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
 * Fetches all concepts from a collection of identifiers
 */
def fetchConcepts = { identifiers ->
	def concepts = [] as Set
	for (def identifier : identifiers) {
		def concept = fetchConcept(identifier)

		if (concept) {
			concepts.add(concept)
		} else {
			println "Unable to find concept identified by '" + identifier + "'"
		}
	}
	return concepts
}

/**
 * Returns a copy of a map with all null value entries removed
 */
def withoutNulls = { map -> map.findAll { it.value != null }  }

/**
 * Creates an XML node for the given concept
 */
def createConceptNode = { parent, obj ->
	def attrs = [
			concept_id: obj.id,
			retired: obj.retired ? 1 : 0,
			/*short_name: obj.shortName,
			/*description: obj.description,*/
			datatype_id: obj.datatype.id,
			class_id: obj.conceptClass.id,
			is_set: obj.set ? 1 : 0,
			creator: obj.creator.id,
			date_created: obj.dateCreated,
			version: obj.version,
			changed_by: obj.changedBy.id,
			date_changed: obj.dateChanged,
			retired_by: obj.retiredBy.id,
			date_retired: obj.dateRetired,
			retire_reason: obj.retireReason,
			uuid: obj.uuid
	]
	return new Node(parent, "concept", withoutNulls(attrs))
}

/**
 * Creates an XML node for the given concept name
 */
def createConceptNameNode = { parent, obj ->
	def attrs = [
			concept_id: obj.concept.id,
			name: obj.name,
			locale: obj.locale.language,
			creator: obj.creator.id,
			date_created: obj.dateCreated,
			concept_name_id: obj.id,
			voided: obj.voided ? 1 : 0,
			voided_by: obj.voidedBy.id,
			date_voided: obj.dateVoided,
			void_reason: obj.voidReason,
			uuid: obj.uuid,
			concept_name_type: obj.conceptNameType,
			locale_preferred: obj.localePreferred
	]
	return new Node(parent, "concept_name", withoutNulls(attrs))
}

/**
 * Creates an XML node for the given concept numeric
 */
def createConceptNumericNode = { parent, obj ->
	def attrs = [
			concept_id: obj.id,
			hi_absolute: obj.hiAbsolute,
			hi_critical: obj.hiCritical,
			hi_normal: obj.hiNormal,
			low_absolute: obj.lowAbsolute,
			low_critical: obj.lowCritical,
			low_normal: obj.lowNormal,
			units: obj.units,
			precise: obj.precise ? 1 : 0
	]
	return new Node(parent, "concept_numeric", withoutNulls(attrs))
}

/**
 * Creates an XML node for dataset of concepts
 */
def createDataSetNode = { concepts ->
	def node = new Node(null, "dataset")

	for (def concept : concepts) {
		createConceptNode(node, concept)

		if (concept instanceof ConceptNumeric) {
			createConceptNumericNode(node, concept)
		}

		for (def name : concept.names) {
			if (!includeLocales || includeLocales.contains(name.locale.language)) {
				createConceptNameNode(node, name)
			}
		}
	}
	return node
}

/**
 * Writes a dataset to a temporary file
 */
def writeDataSetToTempFile = { root ->
	def file = File.createTempFile("dataset", ".xml")
	new XmlNodePrinter(new PrintWriter(new FileWriter(file))).print(root);

	// Write to System.out as well
	new XmlNodePrinter().print(root);

	println "Dataset XML written to " + file.absolutePath
}

def constantsClass = Context.loadClass(constantsClassName)

def conceptIdentifiers = identifiersFromClass(constantsClass)

def concepts = fetchConcepts(conceptIdentifiers)

def root = createDataSetNode(concepts)

writeDataSetToTempFile(root)