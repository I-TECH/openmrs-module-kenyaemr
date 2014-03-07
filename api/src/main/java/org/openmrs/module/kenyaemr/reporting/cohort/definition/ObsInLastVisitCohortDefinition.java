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

package org.openmrs.module.kenyaemr.reporting.cohort.definition;

import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Cohort of patients with specific obs in their last visit
 */
public class ObsInLastVisitCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty(required = true)
	private Concept question;

	@ConfigurationProperty(required = false)
	private Date onOrBefore;

	/**
	 * Gets the question concept
	 * @return the question concept
	 */
	public Concept getQuestion() {
		return question;
	}

	/**
	 * Sets the question concept
	 * @param question the question concept
	 */
	public void setQuestion(Concept question) {
		this.question = question;
	}

	/**
	 * Gets the on-or-before date
	 * @return the date
	 */
	public Date getOnOrBefore() {
		return onOrBefore;
	}

	/**
	 * Sets the on-or-before date
	 * @param onOrBefore the  date
	 */
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}
}