/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition;

import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.Set;

/**
 * Cohort definition based on patient regimen orders
 */
public class RegimenOrderCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty(required = false, group = "date range")
	private Date onOrAfter;

	@ConfigurationProperty(required = false, group = "date range")
	private Date onDate;

	@ConfigurationProperty(group = "conceptsToInclude")
	private Set<Concept> conceptSet;

	@ConfigurationProperty(group = "masterConceptSet")
	private Concept masterConceptSet;

	public Date getOnDate() {
		return onDate;
	}

	public Date getOnOrAfter() {
		return onOrAfter;
	}

	public Set<Concept> getConceptSet() {
		return conceptSet;
	}

	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}

	public void setConceptSet(Set<Concept> conceptSet) {
		this.conceptSet = conceptSet;
	}

	public Concept getMasterConceptSet() {
		return masterConceptSet;
	}

	public void setMasterConceptSet(Concept masterConceptSet) {
		this.masterConceptSet = masterConceptSet;
	}
}