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
package org.openmrs.module.kenyaemr.fragment.controller;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for creating and editing patients in the registration app
 */
public class RegistrationEditPatientFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam(required=false, value="patient") Patient patient) {
		if (patient != null) {
			model.addAttribute("command", new EditPatientCommand(patient));
		} else {
			model.addAttribute("command", new EditPatientCommand());
		}
		model.addAttribute("telephoneContactAttrType", Context.getPersonService().getPersonAttributeTypeByUuid(MetadataConstants.TELEPHONE_CONTACT_UUID));
	}
	
	public SimpleObject savePatient(@MethodParam("commandObject") @BindParams EditPatientCommand command,
	                        UiUtils ui) {
		ui.validate(command, command, null);
		
		Patient saved = command.save();
		return SimpleObject.fromObject(saved, ui, "patientId");
	}
	
	public EditPatientCommand commandObject(@RequestParam(required=false, value="patientId") Patient patient) {
		if (patient != null) {
			return new EditPatientCommand(patient);
		} else {
			return new EditPatientCommand();
		}
	}
	
	public class EditPatientCommand extends ValidatingCommandObject {
		
		private Patient original;
		
		private Location location;
		
		private PersonName personName;
		
		private PatientIdentifier patientClinicNumber;
		
		private PatientIdentifier hivIdNumber;
		
		private Date birthdate;
		
		private Integer age;
		
		private String gender;
		
		private PersonAddress personAddress;
		
		private PersonAttribute telephoneContact;
		
		private Concept maritalStatus;
		
		public EditPatientCommand() {
			location = Context.getService(KenyaEmrService.class).getDefaultLocation();
			PatientService ps = Context.getPatientService();
			personName = new PersonName();
			personAddress = new PersonAddress();
			patientClinicNumber = new PatientIdentifier(null,
			        ps.getPatientIdentifierTypeByUuid(MetadataConstants.PATIENT_CLINIC_NUMBER_UUID), location);
			hivIdNumber = new PatientIdentifier(null,
			        ps.getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID), location);
			telephoneContact = new PersonAttribute();
			telephoneContact.setAttributeType(Context.getPersonService().getPersonAttributeTypeByUuid(MetadataConstants.TELEPHONE_CONTACT_UUID));
		}
		
		public EditPatientCommand(Patient patient) {
			this();
			PatientService ps = Context.getPatientService();
			original = patient;

			if (patient.getPersonName() != null) {
				personName = patient.getPersonName();
			} else {
				personName.setPerson(patient);
			}
			
			if (patient.getPersonAddress() != null) {
				personAddress = patient.getPersonAddress();
			} else {
				personAddress.setPerson(patient);
			}
			
			gender = patient.getGender();
			
			if (patient.getBirthdateEstimated()) {
				age = patient.getAge();
			} else {
				birthdate = patient.getBirthdate();
			}
			
			PatientIdentifier id = patient.getPatientIdentifier(ps.getPatientIdentifierTypeByUuid(MetadataConstants.PATIENT_CLINIC_NUMBER_UUID));
			if (id != null) {
				patientClinicNumber = id;
			} else {
				patientClinicNumber.setPatient(patient);
			}
			
			id = patient.getPatientIdentifier(ps.getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID));
			if (id != null) {
				hivIdNumber = id;
			} else {
				hivIdNumber.setPatient(patient);
			}
			
			PersonAttribute attr = patient.getAttribute(Context.getPersonService().getPersonAttributeTypeByUuid(MetadataConstants.TELEPHONE_CONTACT_UUID));
			if (attr != null) {
				telephoneContact = attr;
			} else {
				telephoneContact.setPerson(patient);
			}
		}
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			if (StringUtils.isBlank(patientClinicNumber.getIdentifier())) {
				patientClinicNumber = null;
			}
			if (StringUtils.isBlank(hivIdNumber.getIdentifier())) {
				hivIdNumber = null;
			}
			
			require(errors, "gender");
			requireAny(errors, "age", "birthdate");
			
			validateField(errors, "personName");
			validateField(errors, "personAddress");
			validateField(errors, "patientClinicNumber");
			validateField(errors, "hivIdNumber");
		}
		
		public Patient save() {
			PatientService ps = Context.getPatientService();
			
			Patient toSave = original != null ? original : new Patient();
			
			toSave.setGender(gender);
			if (birthdate != null) {
				toSave.setBirthdate(birthdate);
				toSave.setBirthdateEstimated(false);
			} else if (age != null) {
				// avoid re-estimating the birthdate if they didn't change this field
				if (!age.equals(toSave.getAge())) {
					toSave.setBirthdateFromAge(age, null);
				}
			} else {
				throw new RuntimeException("Age or Birthdate was supposed to be specified");
			}
			
			PatientIdentifier oldPatientClinicNumber = toSave.getPatientIdentifier(ps.getPatientIdentifierTypeByUuid(MetadataConstants.PATIENT_CLINIC_NUMBER_UUID));
			if (anyChanges(oldPatientClinicNumber, patientClinicNumber, "identifier")) {
				if (oldPatientClinicNumber != null) {
					voidData(oldPatientClinicNumber);
				}
				toSave.addIdentifier(patientClinicNumber);
			}
			
			PatientIdentifier oldHivId = toSave.getPatientIdentifier(ps.getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID));
			if (anyChanges(oldHivId, hivIdNumber, "identifier")) {
				if (oldHivId != null) {
					voidData(oldHivId);
				}
				toSave.addIdentifier(hivIdNumber);
			}
			
			{ // make sure everyone gets an OpenMRS ID
				PatientIdentifierType openmrsIdType = Context.getPatientService().getPatientIdentifierTypeByUuid(MetadataConstants.OPENMRS_ID_UUID);
				if (toSave.getPatientIdentifier(openmrsIdType) == null) {
				    String generated = Context.getService(IdentifierSourceService.class).generateIdentifier(openmrsIdType, "Registration Create/Edit Patient");
				    PatientIdentifier generatedOpenmrsId = new PatientIdentifier(generated, openmrsIdType, location);
				    toSave.addIdentifier(generatedOpenmrsId);
				}
		    }
			
			if (!toSave.getPatientIdentifier().isPreferred()) {
				toSave.getPatientIdentifier().setPreferred(true);
			}
		
			if (anyChanges(toSave.getPersonName(), personName, "givenName", "familyName")) {
				if (toSave.getPersonName() != null) {
					voidData(toSave.getPersonName());
				}
				toSave.addName(personName);
			}
			
			if (anyChanges(toSave.getPersonAddress(), personAddress, "address1", "address2")) { // TODO address
				if (toSave.getPersonAddress() != null) {
					voidData(toSave.getPersonAddress());
				}
				toSave.addAddress(personAddress);
			}
			
			PersonAttributeType telContact = Context.getPersonService().getPersonAttributeTypeByUuid(MetadataConstants.TELEPHONE_CONTACT_UUID);
			if (anyChanges(toSave.getAttribute(telContact), telephoneContact, "value")) {
				if (toSave.getAttribute(telContact) != null) {
					voidData(toSave.getAttribute(telContact));
				}
				toSave.addAttribute(telephoneContact);
			}
			
			return Context.getPatientService().savePatient(toSave);
		}
		
		/**
		 * @return the original
		 */
		public Patient getOriginal() {
			return original;
		}
		
		/**
		 * @param original the original to set
		 */
		public void setOriginal(Patient original) {
			this.original = original;
		}
		
		/**
		 * @return the personName
		 */
		public PersonName getPersonName() {
			return personName;
		}
		
		/**
		 * @param personName the personName to set
		 */
		public void setPersonName(PersonName personName) {
			this.personName = personName;
		}
		
		/**
		 * @return the patientClinicNumber
		 */
		public PatientIdentifier getPatientClinicNumber() {
			return patientClinicNumber;
		}
		
		/**
		 * @param patientClinicNumber the patientClinicNumber to set
		 */
		public void setPatientClinicNumber(PatientIdentifier patientClinicNumber) {
			this.patientClinicNumber = patientClinicNumber;
		}
		
		/**
		 * @return the hivIdNumber
		 */
		public PatientIdentifier getHivIdNumber() {
			return hivIdNumber;
		}
		
		/**
		 * @param hivIdNumber the hivIdNumber to set
		 */
		public void setHivIdNumber(PatientIdentifier hivIdNumber) {
			this.hivIdNumber = hivIdNumber;
		}
		
		/**
		 * @return the birthdate
		 */
		public Date getBirthdate() {
			return birthdate;
		}
		
		/**
		 * @param birthdate the birthdate to set
		 */
		public void setBirthdate(Date birthdate) {
			this.birthdate = birthdate;
		}
		
		/**
		 * @return the age
		 */
		public Integer getAge() {
			return age;
		}
		
		/**
		 * @param age the age to set
		 */
		public void setAge(Integer age) {
			this.age = age;
		}
		
		/**
		 * @return the gender
		 */
		public String getGender() {
			return gender;
		}
		
		/**
		 * @param gender the gender to set
		 */
		public void setGender(String gender) {
			this.gender = gender;
		}
		
		/**
		 * @return the personAddress
		 */
		public PersonAddress getPersonAddress() {
			return personAddress;
		}
		
		/**
		 * @param personAddress the personAddress to set
		 */
		public void setPersonAddress(PersonAddress personAddress) {
			this.personAddress = personAddress;
		}
		
		/**
		 * @return the maritalStatus
		 */
		public Concept getMaritalStatus() {
			return maritalStatus;
		}
		
		/**
		 * @param maritalStatus the maritalStatus to set
		 */
		public void setMaritalStatus(Concept maritalStatus) {
			this.maritalStatus = maritalStatus;
		}
		
        /**
         * @return the telephoneContact
         */
        public PersonAttribute getTelephoneContact() {
        	return telephoneContact;
        }
		
        /**
         * @param telephoneContact the telephoneContact to set
         */
        public void setTelephoneContact(PersonAttribute telephoneContact) {
        	this.telephoneContact = telephoneContact;
        }
		
	}
	
}
