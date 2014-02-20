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
import java.util.List;
import java.util.Set;

/**
 * Cohort definition based on patient drug orders
 */

public class RegimenOrderCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty(required = false, group = "date range")
	private Date onOrAfter;

	@ConfigurationProperty(required = false, group = "date range")
	private Date onOrBefore;

	@ConfigurationProperty(group = "conceptsToInclude")
	private Set<Concept> conceptSet;

	@ConfigurationProperty(group = "masterConceptSet")
	private Concept  masterConceptSet;

	public Date getOnOrBefore() {
		return onOrBefore;
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

	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
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
