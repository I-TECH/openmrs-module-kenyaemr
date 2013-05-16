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

package org.openmrs.module.kenyaemr.lab;

import org.openmrs.Concept;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.MetadataConstants;

/**
 * Lab test definition
 */
public class LabTestDefinition {

	private Object conceptIdentifier;

	private String name;

	/**
	 * Constructs a lab test definition
	 * @param conceptIdentifier the concept identifier
	 */
	public LabTestDefinition(Object conceptIdentifier)  {
		this.conceptIdentifier = conceptIdentifier;
		this.name = getConcept().getPreferredName(MetadataConstants.LOCALE).getName();
	}

	/**
	 * Constructs a lab test definition with an explicit name
	 * @param conceptIdentifier the concept identifier
	 * @param name the name
	 */
	public LabTestDefinition(Object conceptIdentifier, String name)  {
		this.conceptIdentifier = conceptIdentifier;
		this.name = name;
	}

	/**
	 * Gets the concept
	 * @return the concept
	 */
	public Concept getConcept() {
		return Dictionary.getConcept(conceptIdentifier);
	}

	/**
	 * Gets the display name
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}