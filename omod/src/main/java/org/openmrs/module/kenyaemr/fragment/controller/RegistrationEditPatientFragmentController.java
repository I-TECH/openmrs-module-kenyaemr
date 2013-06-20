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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
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

		model.addAttribute("civilStatusConcept", Dictionary.getConcept(Dictionary.CIVIL_STATUS));
		model.addAttribute("occupationConcept", Dictionary.getConcept(Dictionary.OCCUPATION));
		model.addAttribute("educationConcept", Dictionary.getConcept(Dictionary.EDUCATION));

		// Create list of education answer concepts
		List<Concept> educationOptions = new ArrayList<Concept>();
		educationOptions.add(Dictionary.getConcept(Dictionary.NONE));
		educationOptions.add(Dictionary.getConcept(Dictionary.PRIMARY_EDUCATION));
		educationOptions.add(Dictionary.getConcept(Dictionary.SECONDARY_EDUCATION));
		educationOptions.add(Dictionary.getConcept(Dictionary.COLLEGE_UNIVERSITY_POLYTECHNIC));
		educationOptions.add(Dictionary.getConcept(Dictionary.UNIVERSITY_COMPLETE));
		model.addAttribute("educationOptions", educationOptions);

		// Fetch person attributes
		model.addAttribute("telephoneContactAttrType", Metadata.getPersonAttributeType(Metadata.TELEPHONE_CONTACT_PERSON_ATTRIBUTE_TYPE));
		model.addAttribute("nationalIdNumberAttrType", Metadata.getPersonAttributeType(Metadata.NATIONAL_ID_NUMBER_PERSON_ATTRIBUTE_TYPE));
		model.addAttribute("nameOfNextOfKinAttrType", Metadata.getPersonAttributeType(Metadata.NAME_OF_NEXT_OF_KIN_PERSON_ATTRIBUTE_TYPE));
		model.addAttribute("nextOfKinRelationshipAttrType", Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_RELATIONSHIP_PERSON_ATTRIBUTE_TYPE));
		model.addAttribute("nextOfKinContactAttrType", Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_CONTACT_PERSON_ATTRIBUTE_TYPE));
		model.addAttribute("nextOfKinAddressAttrType", Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_ADDRESS_PERSON_ATTRIBUTE_TYPE));
	}

	public SimpleObject savePatient(@MethodParam("commandObject") @BindParams EditPatientCommand command, UiUtils ui) {
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

		private Boolean birthdateEstimated;

		private String gender;

		private PersonAddress personAddress;

		private PersonAttribute telephoneContact;

		private Concept maritalStatus;

		private Concept occupation;

		private Concept education;

		private Obs savedMaritalStatus;

		private Obs savedOccupation;

		private Obs savedEducation;

		//additional member variables

		private PersonAttribute nationalIdNumber;

		private PersonAttribute nameOfNextOfKin;

		private PersonAttribute nextOfKinRelationship;

		private PersonAttribute nextOfKinContact;

		private PersonAttribute nextOfKinAddress;

		public EditPatientCommand() {
			location = Context.getService(KenyaEmrService.class).getDefaultLocation();

			personName = new PersonName();
			personAddress = new PersonAddress();
			patientClinicNumber = new PatientIdentifier(null, Metadata.getPatientIdentifierType(Metadata.PATIENT_CLINIC_NUMBER_IDENTIFIER_TYPE), location);
			hivIdNumber = new PatientIdentifier(null, Metadata.getPatientIdentifierType(Metadata.UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE), location);

			telephoneContact = new PersonAttribute();
			telephoneContact.setAttributeType(Metadata.getPersonAttributeType(Metadata.TELEPHONE_CONTACT_PERSON_ATTRIBUTE_TYPE));

			nationalIdNumber = new PersonAttribute();
			nationalIdNumber.setAttributeType(Metadata.getPersonAttributeType(Metadata.NATIONAL_ID_NUMBER_PERSON_ATTRIBUTE_TYPE));

			nameOfNextOfKin = new PersonAttribute();
			nameOfNextOfKin.setAttributeType(Metadata.getPersonAttributeType(Metadata.NAME_OF_NEXT_OF_KIN_PERSON_ATTRIBUTE_TYPE));

			nextOfKinRelationship = new PersonAttribute();
			nextOfKinRelationship.setAttributeType(Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_RELATIONSHIP_PERSON_ATTRIBUTE_TYPE));

			nextOfKinContact = new PersonAttribute();
			nextOfKinContact.setAttributeType(Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_CONTACT_PERSON_ATTRIBUTE_TYPE));

			nextOfKinAddress = new PersonAttribute();
			nextOfKinAddress.setAttributeType(Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_ADDRESS_PERSON_ATTRIBUTE_TYPE));

		}

		public EditPatientCommand(Patient patient) {
			this();

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
			birthdate = patient.getBirthdate();
			birthdateEstimated = patient.getBirthdateEstimated();

			PatientIdentifier id = patient.getPatientIdentifier(Metadata.getPatientIdentifierType(Metadata.PATIENT_CLINIC_NUMBER_IDENTIFIER_TYPE));
			if (id != null) {
				patientClinicNumber = id;
			} else {
				patientClinicNumber.setPatient(patient);
			}

			id = patient.getPatientIdentifier(Metadata.getPatientIdentifierType(Metadata.UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE));
			if (id != null) {
				hivIdNumber = id;
			} else {
				hivIdNumber.setPatient(patient);
			}

			PersonAttribute attr = patient.getAttribute(Metadata.getPersonAttributeType(Metadata.TELEPHONE_CONTACT_PERSON_ATTRIBUTE_TYPE));
			if (attr != null) {
				telephoneContact = attr;
			} else {
				telephoneContact.setPerson(patient);
			}

			savedMaritalStatus = getLatestObs(patient, Dictionary.CIVIL_STATUS);
			if (savedMaritalStatus != null) {
				maritalStatus = savedMaritalStatus.getValueCoded();
			}

			savedOccupation = getLatestObs(patient, Dictionary.OCCUPATION);
			if (savedOccupation != null) {
				occupation = savedOccupation.getValueCoded();
			}

			savedEducation = getLatestObs(patient, Dictionary.EDUCATION);
			if (savedEducation != null) {
				education = savedEducation.getValueCoded();
			}

			PersonAttribute attrNationalId = patient.getAttribute(Metadata.getPersonAttributeType(Metadata.NATIONAL_ID_NUMBER_PERSON_ATTRIBUTE_TYPE));
			if (attrNationalId != null) {
				nationalIdNumber = attrNationalId;
			}
			else {
				nationalIdNumber.setPerson(patient);
			}

			// Next of kin details
			PersonAttribute attrNameOfNextOfKin = patient.getAttribute(Metadata.getPersonAttributeType(Metadata.NAME_OF_NEXT_OF_KIN_PERSON_ATTRIBUTE_TYPE));
			if (attrNameOfNextOfKin != null) {
				nameOfNextOfKin = attrNameOfNextOfKin;
			}
			else {
				nameOfNextOfKin.setPerson(patient);
			}

			PersonAttribute attrNextOfKinRelationship = patient.getAttribute(Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_RELATIONSHIP_PERSON_ATTRIBUTE_TYPE));
			if (attrNextOfKinRelationship != null) {
				nextOfKinRelationship = attrNextOfKinRelationship;
			}
			else {
				nextOfKinRelationship.setPerson(patient);
			}

			PersonAttribute attrNextOfKinContact = patient.getAttribute(Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_CONTACT_PERSON_ATTRIBUTE_TYPE));
			if (attrNextOfKinContact != null) {
				nextOfKinContact = attrNextOfKinContact;
			}
			else {
				nextOfKinContact.setPerson(patient);
			}

			PersonAttribute attrNextOfKinAddress = patient.getAttribute(Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_ADDRESS_PERSON_ATTRIBUTE_TYPE));
			if (attrNextOfKinAddress != null) {
				nextOfKinAddress = attrNextOfKinAddress;
			}
			else {
				nextOfKinAddress.setPerson(patient);
			}
		}

		private Obs getLatestObs(Patient patient, String conceptIdentifier) {
			Concept concept = Dictionary.getConcept(conceptIdentifier);
			List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
			if (obs.size() > 0) {
				// these are in reverse chronological order
				return obs.get(0);
			}
			return null;
		}

		/**
		 * Validates a telephone number
		 * @param errors the errors
		 * @param telephoneNumber the number to validate
		 */
		private void validateTelephoneNumber(Errors errors, String path, String telephoneNumber) {
			String trimmed = telephoneNumber.trim();
			if (trimmed.length() != 10) {
				errors.rejectValue(path, "Phone numbers must be 10 digits long");
			}
			if (!trimmed.matches("\\d{10}")) {
				errors.rejectValue(path, "Phone numbers must only contain numbers");
			}
		}

		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			require(errors, "personName.givenName");
			require(errors, "personName.familyName");
			require(errors, "gender");
			require(errors, "birthdate");

			if (StringUtils.isBlank(patientClinicNumber.getIdentifier())) {
				patientClinicNumber = null;
			}
			if (StringUtils.isBlank(hivIdNumber.getIdentifier())) {
				hivIdNumber = null;
			}
			if (!(StringUtils.isBlank(telephoneContact.getValue()))) {
				validateTelephoneNumber(errors, "telephoneContact", telephoneContact.getValue());
			}
			else {
				telephoneContact = null;
			}
			if (!(StringUtils.isBlank(nextOfKinContact.getValue()))) {
				validateTelephoneNumber(errors, "nextOfKinContact", nextOfKinContact.getValue());
			}
			else {
				nextOfKinContact = null;
			}

			validateField(errors, "personAddress");
			validateField(errors, "patientClinicNumber");
			validateField(errors, "hivIdNumber");

			// check birth date against future dates and really old dates
			if (birthdate != null) {
				if (birthdate.after(new Date()))
					errors.rejectValue("birthdate", "error.date.future");
				else {
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					c.add(Calendar.YEAR, -120); // person cannot be older than 120 years old
					if (birthdate.before(c.getTime())) {
						errors.rejectValue("birthdate", "error.date.nonsensical");
					}
				}
			}
		}

		public Patient save() {
			PatientService ps = Context.getPatientService();

			Patient toSave = original != null ? original : new Patient();

			toSave.setGender(gender);
			toSave.setBirthdate(birthdate);
			toSave.setBirthdateEstimated(birthdateEstimated);

			PatientIdentifier oldPatientClinicNumber = toSave.getPatientIdentifier(Metadata.getPatientIdentifierType(Metadata.PATIENT_CLINIC_NUMBER_IDENTIFIER_TYPE));
			if (anyChanges(oldPatientClinicNumber, patientClinicNumber, "identifier")) {
				if (oldPatientClinicNumber != null) {
					voidData(oldPatientClinicNumber);
				}
				toSave.addIdentifier(patientClinicNumber);
			}

			PatientIdentifier oldHivId = toSave.getPatientIdentifier(Metadata.getPatientIdentifierType(Metadata.UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE));
			if (anyChanges(oldHivId, hivIdNumber, "identifier")) {
				if (oldHivId != null) {
					voidData(oldHivId);
				}
				toSave.addIdentifier(hivIdNumber);
			}

			{ // make sure everyone gets an OpenMRS ID
				PatientIdentifierType openmrsIdType = Metadata.getPatientIdentifierType(Metadata.OPENMRS_ID_IDENTIFIER_TYPE);
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

			if (anyChanges(toSave.getPersonAddress(), personAddress, "address1", "address2", "address5", "address6", "countyDistrict","address3","cityVillage")) {
				if (toSave.getPersonAddress() != null) {
					voidData(toSave.getPersonAddress());
				}
				toSave.addAddress(personAddress);
			}

			PersonAttributeType telContact = Metadata.getPersonAttributeType(Metadata.TELEPHONE_CONTACT_PERSON_ATTRIBUTE_TYPE);
			if (anyChanges(toSave.getAttribute(telContact), telephoneContact, "value")) {
				if (toSave.getAttribute(telContact) != null) {
					voidData(toSave.getAttribute(telContact));
				}
				toSave.addAttribute(telephoneContact);
			}
			//additions
			PersonAttributeType nationalId = Metadata.getPersonAttributeType(Metadata.NATIONAL_ID_NUMBER_PERSON_ATTRIBUTE_TYPE);
			if (anyChanges(toSave.getAttribute(nationalId), nationalIdNumber, "value")) {
				if (toSave.getAttribute(nationalId) != null) {
					voidData(toSave.getAttribute(nationalId));
				}
				toSave.addAttribute(nationalIdNumber);
			}
			//next of kin included here
			PersonAttributeType nameOfNextOfkinpat = Metadata.getPersonAttributeType(Metadata.NAME_OF_NEXT_OF_KIN_PERSON_ATTRIBUTE_TYPE);
			if (anyChanges(toSave.getAttribute(nameOfNextOfkinpat), this.nameOfNextOfKin, "value")) {
				if (toSave.getAttribute(nameOfNextOfkinpat) != null) {
					voidData(toSave.getAttribute(nameOfNextOfkinpat));
				}
				toSave.addAttribute(this.nameOfNextOfKin);
			}

			PersonAttributeType nextOfkinRelationshippat = Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_RELATIONSHIP_PERSON_ATTRIBUTE_TYPE);
			if (anyChanges(toSave.getAttribute(nextOfkinRelationshippat), this.nextOfKinRelationship, "value")) {
				if (toSave.getAttribute(nextOfkinRelationshippat) != null) {
					voidData(toSave.getAttribute(nextOfkinRelationshippat));
				}
				toSave.addAttribute(this.nextOfKinRelationship);
			}

			PersonAttributeType nextOfkinContactpat = Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_CONTACT_PERSON_ATTRIBUTE_TYPE);
			if (anyChanges(toSave.getAttribute(nextOfkinContactpat), this.nextOfKinContact, "value")) {
				if (toSave.getAttribute(nextOfkinContactpat) != null) {
					voidData(toSave.getAttribute(nextOfkinContactpat));
				}
				toSave.addAttribute(this.nextOfKinContact);
			}

			PersonAttributeType nextOfkinAddresspat = Metadata.getPersonAttributeType(Metadata.NEXT_OF_KIN_ADDRESS_PERSON_ATTRIBUTE_TYPE);
			if (anyChanges(toSave.getAttribute(nextOfkinAddresspat), this.nextOfKinAddress, "value")) {
				if (toSave.getAttribute(nextOfkinAddresspat) != null) {
					voidData(toSave.getAttribute(nextOfkinAddresspat));
				}
				toSave.addAttribute(this.nextOfKinAddress);
			}


			Patient ret = Context.getPatientService().savePatient(toSave);

			List<Obs> obsToSave = new ArrayList<Obs>();
			List<Obs> obsToVoid = new ArrayList<Obs>();
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.CIVIL_STATUS), savedMaritalStatus, maritalStatus);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.OCCUPATION), savedOccupation, occupation);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.EDUCATION), savedEducation, education);

			for (Obs o : obsToVoid) {
				Context.getObsService().voidObs(o, "Kenya EMR edit patient");
			}

			for (Obs o : obsToSave) {
				Context.getObsService().saveObs(o, "Kenya EMR edit patient");
			}

			return ret;
		}

		private void handleOncePerPatientObs(Patient patient, List<Obs> obsToSave, List<Obs> obsToVoid, Concept question,
											 Obs savedObs, Concept newValue) {
			if (!OpenmrsUtil.nullSafeEquals(savedObs != null ? savedObs.getValueCoded() : null, newValue)) {
				// there was a change
				if (savedObs != null && newValue == null) {
					// treat going from a value to null as voiding all past civil status obs
					obsToVoid.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, question));
				}
				if (newValue != null) {
					Obs o = new Obs();
					o.setPerson(patient);
					o.setConcept(question);
					o.setObsDatetime(new Date());
					o.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
					o.setValueCoded(newValue);
					obsToSave.add(o);
				}
			}
		}

		public boolean isInHivProgram() {
			if (original == null) {
				return false;
			}
			ProgramWorkflowService pws = Context.getProgramWorkflowService();
			Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);
			for (PatientProgram pp : pws.getPatientPrograms(original, hivProgram, null, null, null, null, false)) {
				if (pp.getActive()) {
					return true;
				}
			}
			return false;
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
		 * @return the birthdateEstimated
		 */
		public Boolean getBirthdateEstimated() {
			return birthdateEstimated;
		}

		/**
		 * @param birthdateEstimated the birthdateEstimated to set
		 */
		public void setBirthdateEstimated(Boolean birthdateEstimated) {
			this.birthdateEstimated = birthdateEstimated;
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
		 * @return the education
		 */
		public Concept getEducation() {
			return education;
		}

		/**
		 * @param education the education to set
		 */
		public void setEducation(Concept education) {
			this.education = education;
		}

		/**
		 * @return the occupation
		 */
		public Concept getOccupation() {
			return occupation;
		}

		/**
		 * @param occupation the occupation to set
		 */
		public void setOccupation(Concept occupation) {
			this.occupation = occupation;
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
		/**
		 * @return the nationalIdNumber
		 */
		public PersonAttribute getNationalIdNumber() {
			return nationalIdNumber;
		}
		/**
		 * @param nationalIdNumber the nationalIdNumber to set
		 */
		public void setNationalIdNumber(PersonAttribute nationalIdNumber) {
			this.nationalIdNumber = nationalIdNumber;
		}
		/**
		 * @return the nameOfNextOfKin
		 */
		public PersonAttribute getNameOfNextOfKin() {
			return nameOfNextOfKin;
		}
		/**
		 * @param nameOfNextOfKin the nameOfNextOfKin to set
		 */
		public void setNameOfNextOfKin(PersonAttribute nameOfNextOfKin) {
			this.nameOfNextOfKin = nameOfNextOfKin;
		}
		/**
		 * @return the nextOfKinRelationship
		 */
		public PersonAttribute getNextOfKinRelationship() {
			return nextOfKinRelationship;
		}
		/**
		 * @param nextOfKinRelationship the nextOfKinRelationship to set
		 */
		public void setNextOfKinRelationship(PersonAttribute nextOfKinRelationship) {
			this.nextOfKinRelationship = nextOfKinRelationship;
		}
		/**
		 * @return the nextOfKinContact
		 */
		public PersonAttribute getNextOfKinContact() {
			return nextOfKinContact;
		}
		/**
		 * @param nextOfKinContact the nextOfKinContact to set
		 */
		public void setNextOfKinContact(PersonAttribute nextOfKinContact) {
			this.nextOfKinContact = nextOfKinContact;
		}
		/**
		 * @return the nextOfKinAddress
		 */
		public PersonAttribute getNextOfKinAddress() {
			return nextOfKinAddress;
		}
		/**
		 * @param nextOfKinAddress the nextOfKinAddress to set
		 */
		public void setNextOfKinAddress(PersonAttribute nextOfKinAddress) {
			this.nextOfKinAddress = nextOfKinAddress;
		}
	}
}