/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.module.reporting.common.DrugOrderSet;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Drug Orders For Patient Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.DrugOrdersListForPatientDataDefinition")
public class DrugOrdersListForPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	//***** PROPERTIES *****

	@ConfigurationProperty
	private List<Drug> drugsToInclude;

	@ConfigurationProperty
	private List<Concept> drugConceptsToInclude;

	@ConfigurationProperty
	private List<Concept> drugConceptSetsToInclude;

	@ConfigurationProperty
	private Date activeOnDate;

	@ConfigurationProperty
	private Date startedOnOrBefore;

	@ConfigurationProperty
	private Date startedOnOrAfter;

	@ConfigurationProperty
	private Date completedOnOrBefore;

	@ConfigurationProperty
	private Date completedOnOrAfter;

	//****** CONSTRUCTORS ******

	/**
	 * Default Constructor
	 */
	public DrugOrdersListForPatientDataDefinition() {
		super();
	}

	/**
	 * Name only Constructor
	 */
	public DrugOrdersListForPatientDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return DrugOrderSet.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the drugsToInclude
	 */
	public List<Drug> getDrugsToInclude() {
		return drugsToInclude;
	}

	/**
	 * @param drugsToInclude the drugsToInclude to set
	 */
	public void setDrugsToInclude(List<Drug> drugsToInclude) {
		this.drugsToInclude = drugsToInclude;
	}
	
	/**
	 * @param drug the individual drug to add
	 */
	public void addDrugToInclude(Drug drug) {
		if (drugsToInclude == null) {
			drugsToInclude = new ArrayList<Drug>();
		}
		drugsToInclude.add(drug);
	}

	/**
	 * @return the drugConceptsToInclude
	 */
	public List<Concept> getDrugConceptsToInclude() {
		return drugConceptsToInclude;
	}

	/**
	 * @param drugConceptsToInclude the drugConceptsToInclude to set
	 */
	public void setDrugConceptsToInclude(List<Concept> drugConceptsToInclude) {
		this.drugConceptsToInclude = drugConceptsToInclude;
	}
	
	/**
	 * @param concept the individual drug concept to add
	 */
	public void addDrugConceptToInclude(Concept concept) {
		if (drugConceptsToInclude == null) {
			drugConceptsToInclude = new ArrayList<Concept>();
		}
		drugConceptsToInclude.add(concept);
	}

	/**
	 * @return the drugConceptSetsToInclude
	 */
	public List<Concept> getDrugConceptSetsToInclude() {
		return drugConceptSetsToInclude;
	}

	/**
	 * @param drugConceptSetsToInclude the drugConceptSetsToInclude to set
	 */
	public void setDrugConceptSetsToInclude(List<Concept> drugConceptSetsToInclude) {
		this.drugConceptSetsToInclude = drugConceptSetsToInclude;
	}
	
	/**
	 * @param concept the drug concept set to add
	 */
	public void addDrugConceptSetToInclude(Concept concept) {
		if (drugConceptSetsToInclude == null) {
			drugConceptSetsToInclude = new ArrayList<Concept>();
		}
		drugConceptSetsToInclude.add(concept);
	}

	/**
	 * @return the activeOnDate
	 */
	public Date getActiveOnDate() {
		return activeOnDate;
	}

	/**
	 * @param activeOnDate the activeOnDate to set
	 */
	public void setActiveOnDate(Date activeOnDate) {
		this.activeOnDate = activeOnDate;
	}

	/**
	 * @return the startedOnOrBefore
	 */
	public Date getStartedOnOrBefore() {
		return startedOnOrBefore;
	}

	/**
	 * @param startedOnOrBefore the startedOnOrBefore to set
	 */
	public void setStartedOnOrBefore(Date startedOnOrBefore) {
		this.startedOnOrBefore = startedOnOrBefore;
	}

	/**
	 * @return the startedOnOrAfter
	 */
	public Date getStartedOnOrAfter() {
		return startedOnOrAfter;
	}

	/**
	 * @param startedOnOrAfter the startedOnOrAfter to set
	 */
	public void setStartedOnOrAfter(Date startedOnOrAfter) {
		this.startedOnOrAfter = startedOnOrAfter;
	}

	/**
	 * @return the completedOnOrBefore
	 */
	public Date getCompletedOnOrBefore() {
		return completedOnOrBefore;
	}

	/**
	 * @param completedOnOrBefore the completedOnOrBefore to set
	 */
	public void setCompletedOnOrBefore(Date completedOnOrBefore) {
		this.completedOnOrBefore = completedOnOrBefore;
	}

	/**
	 * @return the completedOnOrAfter
	 */
	public Date getCompletedOnOrAfter() {
		return completedOnOrAfter;
	}

	/**
	 * @param completedOnOrAfter the completedOnOrAfter to set
	 */
	public void setCompletedOnOrAfter(Date completedOnOrAfter) {
		this.completedOnOrAfter = completedOnOrAfter;
	}
}