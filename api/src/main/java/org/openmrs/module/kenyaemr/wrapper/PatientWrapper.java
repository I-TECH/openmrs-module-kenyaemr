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

package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;

/**
 * Wrapper class for patients
 */
public class PatientWrapper extends PersonWrapper {

	/**
	 * Creates a new wrapper
	 * @param target the target
	 */
	public PatientWrapper(Patient target) {
		super(target);
	}

	/**
	 * Gets the address of next of kin
	 * @return the address
	 */
	public String getNextOfKinAddress() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_ADDRESS);
	}

	/**
	 * Sets the address of next of kin
	 * @param value the address
	 */
	public void setNextOfKinAddress(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_ADDRESS, value);
	}

	/**
	 * Gets the telephone contact of next of kin
	 * @return the telephone number
	 */
	public String getNextOfKinContact() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_CONTACT);
	}

	/**
	 * Sets the telephone contact of next of kin
	 * @param value telephone number
	 */
	public void setNextOfKinContact(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_CONTACT, value);
	}

	/**
	 * Gets the name of next of kin
	 * @return the name
	 */
	public String getNextOfKinName() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_NAME);
	}

	/**
	 * Sets the name of next of kin
	 * @param value the name
	 */
	public void setNextOfKinName(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_NAME, value);
	}

	/**
	 * Gets the relationship of next of kin
	 * @return the relationship
	 */
	public String getNextOfKinRelationship() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_RELATIONSHIP);
	}

	/**
	 * Sets the relationship of next of kin
	 * @param value the relationship
	 */
	public void setNextOfKinRelationship(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.NEXT_OF_KIN_RELATIONSHIP, value);
	}

	/**
	 * Gets the sub chief name
	 * @return the name
	 */
	public String getSubChiefName() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.SUBCHIEF_NAME);
	}
	/**
	 * Sets the sub chief name
	 * @param value the name
	 */
	public void setSubChiefName(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.SUBCHIEF_NAME, value);
	}
}