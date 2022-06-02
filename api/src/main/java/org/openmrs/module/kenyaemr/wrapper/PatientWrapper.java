/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	 * Gets the passport number
	 * @return the identifier value
	 */
	public String getPassPortNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);
	}


	/**
	 * Sets the passport number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setPassPortNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER, value, location);
	}

	/**
	 * Gets the huduma number
	 * @return the identifier value
	 */
	public String getHudumaNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.HUDUMA_NUMBER);
	}


	/**
	 * Sets the huduma number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setHudumaNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.HUDUMA_NUMBER, value, location);
	}

	/**
	 * Gets the birth certificate number
	 * @return the identifier value
	 */
	public String getBirthCertificateNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);
	}


	/**
	 * Sets the birth certificate number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setBirthCertificateNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER, value, location);
	}
	/**
	 * Gets the alien ID number
	 * @return the identifier value
	 */
	public String getAlienIdNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.ALIEN_ID_NUMBER);
	}


	/**
	 * Sets the alien ID number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setAlienIdNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.ALIEN_ID_NUMBER, value, location);
	}
	/**
	 * Gets the Driving Licence number
	 * @return the identifier value
	 */
	public String getDrivingLicenseNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.DRIVING_LICENSE);
	}


	/**
	 * Sets the Driving Licence number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setDrivingLicenseNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.DRIVING_LICENSE, value, location);
	}

	/**
	 * Sets the client number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setClientNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.CLIENT_NUMBER, value, location);
	}
	/**
	 * Gets the client number
	 * @return the identifier value
	 */
	public String getClientNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.CLIENT_NUMBER);
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
	/**
	 * Gets patient's CHT Reference Number
	 * @return cht reference number
	 */
	public String getChtReferenceNumber() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.CHT_USERNAME);
	}
	/**
	 * Sets patient's CHT Reference Number
	 */
	public void setChtReferenceNumber(String chtReferenceNumber) {
		setAsAttribute(CommonMetadata._PersonAttributeType.CHT_USERNAME, chtReferenceNumber);
	}

	/**
	 * Gets service number
	 * @return the identifier value
	 */
	public String getKDoDServiceNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.KDoD_SERVICE_NUMBER);
	}

	/**
	 * Sets service number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setKDoDServiceNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.KDoD_SERVICE_NUMBER, value, location);
	}
	/**
	 * Gets patient's cadre for KDoD
	 * @return KDoD cadre
	 */
	public String getCadre() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.KDOD_CADRE);
	}
	/**
	 * Sets KDoD cadre
	 */
	public void setCadre(String KDoDCadre) {
		setAsAttribute(CommonMetadata._PersonAttributeType.KDOD_CADRE, KDoDCadre);
	}
	/**
	 * Gets patient's rank for KDoD
	 * @return KDoD rank
	 */
	public String getRank() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.KDOD_RANK);
	}
	/**
	 * Sets KDoD rank
	 */
	public void setRank(String KDoDRank) {
		setAsAttribute(CommonMetadata._PersonAttributeType.KDOD_RANK, KDoDRank);
	}

	/**
	 * Gets patient's KDoD Unit
	 * @return KDoD unit
	 */
	public String getKDoDUnit() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.KDOD_UNIT);
	}
	/**
	 * Sets KDoD unit
	 */
	public void setKDoDUnit(String KDoDUnit) {
		setAsAttribute(CommonMetadata._PersonAttributeType.KDOD_UNIT, KDoDUnit);
	}

	/**
	 * Gets the patient NUPI
	 * @return the identifier value
	 */
	public String getNationalUniquePatientNumber() {
		return getAsIdentifier(CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
	}


	/**
	 * Sets the patient NUPI number
	 * @param value the identifier value
	 * @param location the identifier location
	 */
	public void setNationalUniquePatientNumber(String value, Location location) {
		setAsIdentifier(CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER, value, location);
	}

	/**
	 * Gets the CR verification status
	 * @return the verification status
	 */
	public String getCRVerificationStatus() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.VERIFICATION_STATUS_WITH_NATIONAL_REGISTRY);
	}

	/**
	 * Sets the CR verification status
	 * @param value the CR verification status
	 */
	public void setCRVerificationStatus(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.VERIFICATION_STATUS_WITH_NATIONAL_REGISTRY, value);
	}

}