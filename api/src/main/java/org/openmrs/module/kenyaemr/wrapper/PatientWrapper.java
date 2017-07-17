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

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.kenyacore.wrapper.AbstractPatientWrapper;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;

/**
 * Wrapper class for patients. Unfortunately this can't extend both AbstractPatientWrapper and PersonWrapper so we add a
 * PersonWrapper as a property.
 */
public class PatientWrapper extends AbstractPatientWrapper {

	private PersonWrapper person;

	/**
	 * Creates a new wrapper
	 * @param target the target
	 */
	public PatientWrapper(Patient target) {
		super(target);

		this.person = new PersonWrapper(target);
	}

	/**
	 * Gets the person wrapper
	 * @return the wrapper
	 */
	public PersonWrapper getPerson() {
		return person;
	}

	/**
	 * Gets the medical record number
	 * @return the identifier value
	 */
	public String getMedicalRecordNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.OPENMRS_ID);
	}

	/**
	 * Gets the patient clinic number
	 * @return the identifier value
	 */
	public String getPatientClinicNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.PATIENT_CLINIC_NUMBER);
	}

	/**
	 * Sets the patient clinic number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setPatientClinicNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.PATIENT_CLINIC_NUMBER, value, location);
	}

	/**
	 * Gets the unique patient number
	 * @return the identifier value
	 */
	public String getUniquePatientNumber() {
		return getAsIdentifier(HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
	}

	/**
	 * Sets the unique patient number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setUniquePatientNumber(String value, Location location) {
		setAsIdentifier(HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER, value, location);
	}

	/**
	 * Gets the national id number
	 * @return the identifier value
	 */
	public String getNationalIdNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.NATIONAL_ID);
	}

	/**
	 * Sets the national id number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setNationalIdNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.NATIONAL_ID, value, location);
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

	/**
	 * Gets patient's alternate phone contact
	 * @return phone contact
	 */
	public String getAlternativePhoneContact() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.ALTERNATE_PHONE_CONTACT);
	}
	/**
	 * Sets patient's alternative phone contact
	 */
	public void setAlternativePhoneContact(String contact) {
		setAsAttribute(CommonMetadata._PersonAttributeType.ALTERNATE_PHONE_CONTACT, contact);
	}
	/**
	 * Gets patient's alternate phone contact
	 * @return phone contact
	 */
	public String getNearestHealthFacility() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.NEAREST_HEALTH_CENTER);
	}
	/**
	 * Sets patient's alternative phone contact
	 */
	public void setNearestHealthFacility(String facility) {
		setAsAttribute(CommonMetadata._PersonAttributeType.NEAREST_HEALTH_CENTER, facility);
	}

	/**
	 * Gets patient's alternate phone contact
	 * @return phone contact
	 */
	public String getEmailAddress() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.EMAIL_ADDRESS);
	}
	/**
	 * Sets patient's alternative phone contact
	 */
	public void setEmailAddress(String email) {
		setAsAttribute(CommonMetadata._PersonAttributeType.EMAIL_ADDRESS, email);
	}

	/**
	 * Gets guardian's first name
	 * @return guardian's first name
	 */
	public String getGuardianFirstName() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.GUARDIAN_FIRST_NAME);
	}
	/**
	 * Sets guardian's first name
	 */
	public void setGuardianFirstName(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.GUARDIAN_FIRST_NAME, value);
	}

	/**
	 * Gets guardian's last name
	 * @return guardian's last name
	 */
	public String getGuardianLastName() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.GUARDIAN_LAST_NAME);
	}
	/**
	 * Sets guardian's last name
	 */
	public void setGuardianLastName(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.GUARDIAN_LAST_NAME, value);
	}
}