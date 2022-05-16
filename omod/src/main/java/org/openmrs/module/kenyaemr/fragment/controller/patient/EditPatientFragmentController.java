/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.Relationship;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.fragment.controller.upi.UpiUtilsDataExchange;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.validator.TelephoneNumberValidator;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaemr.wrapper.PersonWrapper;
import org.openmrs.module.kenyaui.form.AbstractWebForm;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for creating and editing patients in the registration app
 */
public class EditPatientFragmentController {

	// We don't record cause of death, but data model requires a concept
	private static final String CAUSE_OF_DEATH_PLACEHOLDER = Dictionary.UNKNOWN;
	//private static final String INSCHOOL = Dictionary.INSCHOOL;
	private AdministrationService administrationService = Context.getAdministrationService();
	final String isKDoD = (administrationService.getGlobalProperty("kenyaemr.isKDoD"));
	final String clientNumberFieldEnabled = (administrationService.getGlobalProperty("clientNumber.enabled"));
	final String clientNumberPreferredLabel = (administrationService.getGlobalProperty("client_number_label"));
	final String clientRegistryClientVerificationApi = (administrationService.getGlobalProperty(CommonMetadata.GP_CLIENT_VERIFICATION_GET_END_POINT));
	final String clientRegistryApiToken = (administrationService.getGlobalProperty(CommonMetadata.GP_CLIENT_VERIFICATION_API_TOKEN));


	/**
	 * Main controller method
	 * @param patient the patient (may be null)
	 * @param person the person (may be null)
	 * @param model the model
	 */
	public void controller(@FragmentParam(value = "patient", required = false) Patient patient,
						   @FragmentParam(value = "person", required = false) Person person,
						   FragmentModel model) {

		if (patient != null && person != null) {
			throw new RuntimeException("A patient or person can be provided, but not both");
		}

		Person existing = patient != null ? patient : person;

		model.addAttribute("clientVerificationApi", clientRegistryClientVerificationApi);
		UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
		model.addAttribute("clientVerificationApiToken", upiUtils.getToken());
		model.addAttribute("command", newEditPatientForm(existing));

		model.addAttribute("civilStatusConcept", Dictionary.getConcept(Dictionary.CIVIL_STATUS));
		model.addAttribute("occupationConcept", Dictionary.getConcept(Dictionary.OCCUPATION));
		model.addAttribute("educationConcept", Dictionary.getConcept(Dictionary.EDUCATION));
		model.addAttribute("enableClientNumberField", (StringUtils.isBlank(clientNumberFieldEnabled) || clientNumberFieldEnabled.equalsIgnoreCase("false")) ? false : true);
		model.addAttribute("clientNumberLabel", clientNumberPreferredLabel);
		model.addAttribute("countryConcept", Dictionary.getConcept(Dictionary.COUNTRY));

		//create list of countries
		List<Concept> countryList = new ArrayList<Concept>();
		countryList.add(Dictionary.getConcept("162883AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		countryList.add(Dictionary.getConcept("162884AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		countryList.add(Dictionary.getConcept("165639AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

		model.addAttribute("countryOptions", countryList);
		// create list of counties

		List<String> countyList = new ArrayList<String>();
		List<Location> locationList = Context.getLocationService().getAllLocations();
		for(Location loc: locationList) {
			String locationCounty = loc.getCountyDistrict();
			if(!StringUtils.isEmpty(locationCounty) && !StringUtils.isBlank(locationCounty)) {
				countyList.add(locationCounty);
			}
		}

		Set<String> uniqueCountyList = new HashSet<String>(countyList);
		model.addAttribute("countyList", uniqueCountyList);

		// create list of next of kin relationship

		List<String> nextOfKinRelationshipOptions = Arrays.asList(
			new String("Partner"),
			new String("Spouse"),
			Dictionary.getConcept(Dictionary.FATHER).getName().getName(),
			Dictionary.getConcept(Dictionary.MOTHER).getName().getName(),
			Dictionary.getConcept(Dictionary.GRANDMOTHER).getName().getName(),
			Dictionary.getConcept(Dictionary.GRANDFATHER).getName().getName(),
			Dictionary.getConcept(Dictionary.SIBLING).getName().getName(),
			Dictionary.getConcept(Dictionary.CHILD).getName().getName(),
			Dictionary.getConcept(Dictionary.AUNT).getName().getName(),
			Dictionary.getConcept(Dictionary.UNCLE).getName().getName(),
			Dictionary.getConcept(Dictionary.GUARDIAN).getName().getName(),
			Dictionary.getConcept(Dictionary.FRIEND).getName().getName(),
			Dictionary.getConcept(Dictionary.CO_WORKER).getName().getName()
		);

		model.addAttribute("nextOfKinRelationshipOptions", nextOfKinRelationshipOptions);

		// Create list of education answer concepts
		List<Concept> educationOptions = new ArrayList<Concept>();
		educationOptions.add(Dictionary.getConcept(Dictionary.NONE));
		educationOptions.add(Dictionary.getConcept(Dictionary.PRIMARY_EDUCATION));
		educationOptions.add(Dictionary.getConcept(Dictionary.SECONDARY_EDUCATION));
		educationOptions.add(Dictionary.getConcept(Dictionary.COLLEGE_UNIVERSITY_POLYTECHNIC));
		model.addAttribute("educationOptions", educationOptions);

		/*Create list of occupation answer concepts  */
		List<Concept> occupationOptions = new ArrayList<Concept>();
		occupationOptions.add(Dictionary.getConcept(Dictionary.FARMER));
		occupationOptions.add(Dictionary.getConcept(Dictionary.TRADER));
		occupationOptions.add(Dictionary.getConcept(Dictionary.EMPLOYEE));
		occupationOptions.add(Dictionary.getConcept(Dictionary.STUDENT));
		occupationOptions.add(Dictionary.getConcept(Dictionary.DRIVER));
		occupationOptions.add(Dictionary.getConcept(Dictionary.NONE));
		occupationOptions.add(Dictionary.getConcept(Dictionary.OTHER_NON_CODED));
		model.addAttribute("occupationOptions", occupationOptions);


		// Create a list of marital status answer concepts
		List<Concept> maritalStatusOptions = new ArrayList<Concept>();
		maritalStatusOptions.add(Dictionary.getConcept(Dictionary.MARRIED_POLYGAMOUS));
		maritalStatusOptions.add(Dictionary.getConcept(Dictionary.MARRIED_MONOGAMOUS));
		maritalStatusOptions.add(Dictionary.getConcept(Dictionary.DIVORCED));
		maritalStatusOptions.add(Dictionary.getConcept(Dictionary.WIDOWED));
		maritalStatusOptions.add(Dictionary.getConcept(Dictionary.LIVING_WITH_PARTNER));
		maritalStatusOptions.add(Dictionary.getConcept(Dictionary.NEVER_MARRIED));
		model.addAttribute("maritalStatusOptions", maritalStatusOptions);

		// Create a list of cause of death answer concepts
		List<Concept> causeOfDeathOptions = new ArrayList<Concept>();
		causeOfDeathOptions.add(Dictionary.getConcept(Dictionary.UNKNOWN));
		model.addAttribute("causeOfDeathOptions", causeOfDeathOptions);

		// Create a list of yes_no options
		List<Concept> yesNoOptions = new ArrayList<Concept>();
		yesNoOptions.add(Dictionary.getConcept(Dictionary.YES));
		yesNoOptions.add(Dictionary.getConcept(Dictionary.NO));
		model.addAttribute("yesNoOptions", yesNoOptions);

		// Get peer educators
		boolean isPeerEducator = false;
		if(patient != null) {
			for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
				if (relationship.getRelationshipType().getaIsToB().equalsIgnoreCase("Peer-educator")
						&& relationship.getEndDate() == null && relationship.getPersonA().getPersonId() == patient.getId()) {
					isPeerEducator = true;
					break;

				}
			}
		}

		model.addAttribute("peerEducator", isPeerEducator);

		/*Create list of cadre answer concepts  */
        List<String> cadreOptions = Arrays.asList(
                new String("Troop"),
                new String("Civilian")
        );
        model.addAttribute("cadreOptions", cadreOptions);

        /*Create list of rank answer Options  */
        List<String> rankOptions = Arrays.asList(
                new String("General(Gen)"),
                new String("Lieutenant General (Lt Gen)"),
                new String("Major General (Maj Gen)"),
                new String("Brigadier (Brig)"),
                new String("Colonel (Col)"),
                new String("Lieutenant Colonel (Lt Col)"),
                new String("Major (Maj)"),
                new String("Captain (Capt)"),
                new String("Lieutenant (Lt)"),
                new String("2nd Lieutenant (2lt)"),
                new String("Warrant officer 1 (WO1)"),
                new String("Warrant officer 2 (WO2)"),
                new String("Senior Sergeant (Ssgt)"),
                new String("Sergeant (Sgt)"),
                new String("Corporal (Cpl)"),
                new String("Private (Spte)")
        );
        model.addAttribute("rankOptions", rankOptions);
        model.addAttribute("isKDoD", isKDoD);
        model.addAttribute("idTypes", Context.getPatientService().getAllPatientIdentifierTypes());

    }

	/**
	 * Saves the patient being edited by this form
	 * @param form the edit patient form
	 * @param ui the UI utils
	 * @return a simple object { patientId }
	 */
	public SimpleObject savePatient(@MethodParam("newEditPatientForm") @BindParams EditPatientForm form, UiUtils ui) {
		ui.validate(form, form, null);

		Patient patient = form.save();

		// if this patient is the current user i need to refresh the current user
		if (patient.getPersonId().equals(Context.getAuthenticatedUser().getPerson().getPersonId())) {
			Context.refreshAuthenticatedUser();
		}

		return SimpleObject.create("id", patient.getId());
	}

	/**
	 * Creates an edit patient form
	 * @param person the person
	 * @return the form
	 */
	public EditPatientForm newEditPatientForm(@RequestParam(value = "personId", required = false) Person person) {
		if (person != null && person.isPatient()) {
			return new EditPatientForm((Patient) person); // For editing existing patient
		} else if (person != null) {
			return new EditPatientForm(person); // For creating patient from existing person
		} else {
			return new EditPatientForm(); // For creating patient and person from scratch
		}
	}

	/**
	 * The form command object for editing patients
	 */
	public class EditPatientForm extends AbstractWebForm {

		private Person original;
		private Location location;
		private PersonName personName;
		private Date birthdate;
		private Boolean birthdateEstimated;
		private String gender;
		private PersonAddress personAddress;

		public Concept getCountry() {
			return country;
		}

		public void setCountry(Concept country) {
			this.country = country;
		}

		private Concept maritalStatus;
		private Concept occupation;
		private Concept education;
		private Concept inSchool;
		private Concept orphan;
		private Obs savedMaritalStatus;
		private Obs savedOccupation;
		private Obs savedEducation;
		private Obs savedInSchool;
		private Obs savedOrphan;
		private Boolean dead = false;
		private Date deathDate;
		private String nationalIdNumber;
		private String patientClinicNumber;
		private String clientNumber;
		private String uniquePatientNumber;
		private String telephoneContact;
		private String nameOfNextOfKin;
		private String nextOfKinRelationship;
		private String nextOfKinContact;
		private String nextOfKinAddress;
		private String subChiefName;
		private String alternatePhoneContact;
		private String nearestHealthFacility;
		private String emailAddress;
		private String guardianFirstName;
		private String guardianLastName;
		private String chtReferenceNumber;
		private String kDoDCadre;
		private String kDoDRank;
		private String kDoDServiceNumber;
		private String kDoDUnit;
		private String passPortNumber;
		private String hudumaNumber;
		private String birthCertificateNumber;
		private String alienIdNumber;
		private String drivingLicenseNumber;
		private String CRVerificationStatus;
		private Concept country;
		private Obs savedCountry;


		public String getNationalUniquePatientNumber() {
			return nationalUniquePatientNumber;
		}

		public void setNationalUniquePatientNumber(String nationalUniquePatientNumber) {
			this.nationalUniquePatientNumber = nationalUniquePatientNumber;
		}

		private String nationalUniquePatientNumber;



		/**
		 * Creates an edit form for a new patient
		 */
		public EditPatientForm() {
			location = Context.getService(KenyaEmrService.class).getDefaultLocation();

			personName = new PersonName();
			personAddress = new PersonAddress();
		}

		/**
		 * Creates an edit form for an existing person
		 */
		public EditPatientForm(Person person) {
			this();

			original = person;

			if (person.getPersonName() != null) {
				personName = person.getPersonName();
			} else {
				personName.setPerson(person);
			}

			if (person.getPersonAddress() != null) {
				personAddress = person.getPersonAddress();
			} else {
				personAddress.setPerson(person);
			}

			gender = person.getGender();
			birthdate = person.getBirthdate();
			birthdateEstimated = person.getBirthdateEstimated();
			dead = person.isDead();
			deathDate = person.getDeathDate();
			PersonWrapper wrapper = new PersonWrapper(person);
			telephoneContact = wrapper.getTelephoneContact();			
		}

		/**
		 * Creates an edit form for an existing patient
		 */
		public EditPatientForm(Patient patient) {
			this((Person) patient);

			PatientWrapper wrapper = new PatientWrapper(patient);

			clientNumber = wrapper.getClientNumber();
			patientClinicNumber = wrapper.getPatientClinicNumber();
			passPortNumber = wrapper.getPassPortNumber();
			hudumaNumber = wrapper.getHudumaNumber();
			alienIdNumber = wrapper.getAlienIdNumber();
			drivingLicenseNumber = wrapper.getDrivingLicenseNumber();
			birthCertificateNumber = wrapper.getBirthCertificateNumber();
			nationalIdNumber = wrapper.getNationalIdNumber();
			nameOfNextOfKin = wrapper.getNextOfKinName();
			nextOfKinRelationship = wrapper.getNextOfKinRelationship();
			nextOfKinContact = wrapper.getNextOfKinContact();
			nextOfKinAddress = wrapper.getNextOfKinAddress();
			subChiefName = wrapper.getSubChiefName();
			alternatePhoneContact = wrapper.getAlternativePhoneContact();
			emailAddress = wrapper.getEmailAddress();
			nearestHealthFacility = wrapper.getNearestHealthFacility();
			guardianFirstName = wrapper.getGuardianFirstName();
			guardianLastName = wrapper.getGuardianLastName();
			chtReferenceNumber = wrapper.getChtReferenceNumber();
			CRVerificationStatus = wrapper.getCRVerificationStatus();
			if(isKDoD.equals("true")){
			kDoDServiceNumber = wrapper.getKDoDServiceNumber();
			kDoDCadre = wrapper.getCadre();
			kDoDRank = wrapper.getRank();
			kDoDUnit = wrapper.getKDoDUnit();
			}
			else{
				uniquePatientNumber = wrapper.getUniquePatientNumber();
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
			savedInSchool = getLatestObs(patient, Dictionary.IN_SCHOOL);
			if (savedInSchool != null) {
				inSchool = savedInSchool.getValueCoded();
			}
			savedOrphan = getLatestObs(patient, Dictionary.ORPHAN);
			if (savedOrphan != null) {
				orphan = savedOrphan.getValueCoded();
			}
			nationalUniquePatientNumber = wrapper.getNationalUniquePatientNumber();

			savedCountry = getLatestObs(patient, Dictionary.COUNTRY);
			if(savedCountry != null) {
				country = savedCountry.getValueCoded();
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
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			if(isKDoD.equals("true")){
			require(errors, "kDoDServiceNumber");
			require(errors, "kDoDCadre");
			}
			else {
				require(errors, "personName.givenName");
				require(errors, "personName.familyName");
				require(errors, "gender");
				require(errors, "birthdate");
			}

			// Require death details if patient is deceased
			if (dead) {
				require(errors, "deathDate");

				if (deathDate != null) {
					if (birthdate != null && deathDate.before(birthdate)) {
						errors.rejectValue("deathDate", "Cannot be before birth date");
					}
					if (deathDate.after(new Date())) {
						errors.rejectValue("deathDate", "Cannot be in the future");
					}
				}
			} else if (deathDate != null) {
				errors.rejectValue("deathDate", "Must be empty if patient not deceased");
			}

			if (StringUtils.isNotBlank(telephoneContact)) {
				validateField(errors, "telephoneContact", new TelephoneNumberValidator());
			}
			if (StringUtils.isNotBlank(alternatePhoneContact)) {
				validateField(errors, "alternatePhoneContact", new TelephoneNumberValidator());
			}
			if (StringUtils.isNotBlank(nextOfKinContact)) {
				validateField(errors, "nextOfKinContact", new TelephoneNumberValidator());
			}

			validateField(errors, "personAddress");

            if(isKDoD.equals("true")){
                validateIdentifierField(errors, "kDoDServiceNumber", CommonMetadata._PatientIdentifierType.KDoD_SERVICE_NUMBER);
            }
            else {
				validateIdentifierField(errors, "uniquePatientNumber", HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
			}
			validateIdentifierField(errors, "nationalIdNumber", CommonMetadata._PatientIdentifierType.NATIONAL_ID);
			validateIdentifierField(errors, "patientClinicNumber", CommonMetadata._PatientIdentifierType.PATIENT_CLINIC_NUMBER);
			validateIdentifierField(errors, "passPortNumber", CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);
			validateIdentifierField(errors, "hudumaNumber", CommonMetadata._PatientIdentifierType.HUDUMA_NUMBER);
			validateIdentifierField(errors, "birthCertificateNumber", CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);
			validateIdentifierField(errors, "alienIdNumber", CommonMetadata._PatientIdentifierType.ALIEN_ID_NUMBER);
			validateIdentifierField(errors, "drivingLicenseNumber", CommonMetadata._PatientIdentifierType.DRIVING_LICENSE);
			validateIdentifierField(errors, "nationalUniquePatientNumber", CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);




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

		/**
		 * Validates an identifier field
		 * @param errors
		 * @param field
		 * @param idTypeUuid
		 */
		protected void validateIdentifierField(Errors errors, String field, String idTypeUuid) {
			String value = (String) errors.getFieldValue(field);

			if (StringUtils.isNotBlank(value)) {
				PatientIdentifierType idType = MetadataUtils.existing(PatientIdentifierType.class, idTypeUuid);
				if (!value.matches(idType.getFormat())) {
					errors.rejectValue(field, idType.getFormatDescription());
				}

				PatientIdentifier stub = new PatientIdentifier(value, idType, null);

				if (original != null && original.isPatient()) { // Editing an existing patient
					stub.setPatient((Patient) original);
				}

				if (Context.getPatientService().isIdentifierInUseByAnotherPatient(stub)) {
					errors.rejectValue(field, "In use by another patient");
				}
			}
		}

		/**
		 * @see org.openmrs.module.kenyaui.form.AbstractWebForm#save()
		 */
		@Override
		public Patient save() {
			Patient toSave;

			if (original != null && original.isPatient()) { // Editing an existing patient
				toSave = (Patient) original;
			}
			else if (original != null) {
				toSave = new Patient(original); // Creating a patient from an existing person
			}
			else {
				toSave = new Patient(); // Creating a new patient and person
			}

			toSave.setGender(gender);
			toSave.setBirthdate(birthdate);
			toSave.setBirthdateEstimated(birthdateEstimated);
			toSave.setDead(dead);
			toSave.setDeathDate(deathDate);
			toSave.setCauseOfDeath(dead ? Dictionary.getConcept(CAUSE_OF_DEATH_PLACEHOLDER) : null);

			if (anyChanges(toSave.getPersonName(), personName, "givenName", "familyName")) {
				if (toSave.getPersonName() != null) {
					voidData(toSave.getPersonName());
				}
				toSave.addName(personName);
			}

			if (anyChanges(toSave.getPersonAddress(), personAddress, "address1", "address2", "address5", "address6", "countyDistrict","address3","cityVillage","stateProvince","country","postalCode","address4")) {
				if (toSave.getPersonAddress() != null) {
					voidData(toSave.getPersonAddress());
				}
				toSave.addAddress(personAddress);
			}

			PatientWrapper wrapper = new PatientWrapper(toSave);

			wrapper.getPerson().setTelephoneContact(telephoneContact);
			wrapper.setNationalIdNumber(nationalIdNumber, location);
			wrapper.setPatientClinicNumber(patientClinicNumber, location);
			wrapper.setClientNumber(clientNumber, location);
			wrapper.setPassPortNumber(passPortNumber, location);
			wrapper.setHudumaNumber(hudumaNumber, location);
			wrapper.setBirthCertificateNumber(birthCertificateNumber, location);
			wrapper.setAlienIdNumber(alienIdNumber, location);
			wrapper.setDrivingLicenseNumber(drivingLicenseNumber, location);
			wrapper.setNationalUniquePatientNumber(nationalUniquePatientNumber, location);


			wrapper.setNextOfKinName(nameOfNextOfKin);
			wrapper.setNextOfKinRelationship(nextOfKinRelationship);
			wrapper.setNextOfKinContact(nextOfKinContact);
			wrapper.setNextOfKinAddress(nextOfKinAddress);
			wrapper.setSubChiefName(subChiefName);
			wrapper.setAlternativePhoneContact(alternatePhoneContact);
			wrapper.setNearestHealthFacility(nearestHealthFacility);
			wrapper.setEmailAddress(emailAddress);
			wrapper.setGuardianFirstName(guardianFirstName);
			wrapper.setGuardianLastName(guardianLastName);
			wrapper.setChtReferenceNumber(chtReferenceNumber);
			wrapper.setCRVerificationStatus(CRVerificationStatus);

			if(isKDoD.equals("true")){
				wrapper.setKDoDServiceNumber(kDoDServiceNumber, location);
				wrapper.setCadre(kDoDCadre);
				wrapper.setRank(kDoDRank);
				wrapper.setKDoDUnit(kDoDUnit);
			}
			else{
				wrapper.setUniquePatientNumber(uniquePatientNumber, location);
			}

			// Make sure everyone gets an OpenMRS ID
			PatientIdentifierType openmrsIdType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.OPENMRS_ID);
			PatientIdentifier openmrsId = toSave.getPatientIdentifier(openmrsIdType);

			if (openmrsId == null) {
				String generated = Context.getService(IdentifierSourceService.class).generateIdentifier(openmrsIdType, "Registration");
				openmrsId = new PatientIdentifier(generated, openmrsIdType, location);
				toSave.addIdentifier(openmrsId);

				if (!toSave.getPatientIdentifier().isPreferred()) {
					openmrsId.setPreferred(true);
				}
			}

			Patient ret = Context.getPatientService().savePatient(toSave);

			// Explicitly save all identifier objects including voided
			for (PatientIdentifier identifier : toSave.getIdentifiers()) {

				Context.getPatientService().savePatientIdentifier(identifier);
			}

			// Save remaining fields as obs
			List<Obs> obsToSave = new ArrayList<Obs>();
			List<Obs> obsToVoid = new ArrayList<Obs>();

			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.CIVIL_STATUS), savedMaritalStatus, maritalStatus);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.OCCUPATION), savedOccupation, occupation);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.EDUCATION), savedEducation, education);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.IN_SCHOOL), savedInSchool, inSchool);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.ORPHAN), savedOrphan, orphan);
			handleOncePerPatientObs(ret, obsToSave, obsToVoid, Dictionary.getConcept(Dictionary.COUNTRY), savedCountry, country);

			for (Obs o : obsToVoid) {
				Context.getObsService().voidObs(o, "KenyaEMR edit patient");
			}

			for (Obs o : obsToSave) {
				Context.getObsService().saveObs(o, "KenyaEMR edit patient");
			}

			return ret;
		}

		/**
		 * Handles saving a field which is stored as an obs
		 * @param patient the patient being saved
		 * @param obsToSave
		 * @param obsToVoid
		 * @param question
		 * @param savedObs
		 * @param newValue
		 */
		protected void handleOncePerPatientObs(Patient patient, List<Obs> obsToSave, List<Obs> obsToVoid, Concept question,
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

		/**
		 * Handles saving a field which is stored as an obs whose value is boolean
		 * @param patient the patient being saved
		 * @param obsToSave
		 * @param obsToVoid
		 * @param question
		 * @param savedObs
		 * @param newValue
		 */
		protected void handleOncePerPatientObs(Patient patient, List<Obs> obsToSave, List<Obs> obsToVoid, Concept question,
											   Obs savedObs, Boolean newValue) {
			if (!OpenmrsUtil.nullSafeEquals(savedObs != null ? savedObs.getValueBoolean() : null, newValue)) {
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
					o.setValueBoolean(newValue);
					obsToSave.add(o);
				}
			}
		}
		public boolean isInHivProgram() {
			if (original == null || !original.isPatient()) {
				return false;
			}
			ProgramWorkflowService pws = Context.getProgramWorkflowService();
			Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
			for (PatientProgram pp : pws.getPatientPrograms((Patient) original, hivProgram, null, null, null, null, false)) {
				if (pp.getActive()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @return the original
		 */
		public Person getOriginal() {
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

		public String getClientNumber() {
			return clientNumber;
		}

		public void setClientNumber(String clientNumber) {
			this.clientNumber = clientNumber;
		}

		/**
		 * @return the patientClinicNumber
		 */
		public String getPatientClinicNumber() {
			return patientClinicNumber;
		}

		/**
		 * @param patientClinicNumber the patientClinicNumber to set
		 */
		public void setPatientClinicNumber(String patientClinicNumber) {
			this.patientClinicNumber = patientClinicNumber;
		}

		/**
		 * @return the passPortNumber
		 */
		public String getPassPortNumber() {
			return passPortNumber;
		}

		/**
		 * @param passPortNumber the passPortNumber to set
		 */
		public void setPassPortNumber(String passPortNumber) {
			this.passPortNumber = passPortNumber;
		}

		/**
		 * @return the hudumaNumber
		 */
		public String getHudumaNumber() {
			return hudumaNumber;
		}

		/**
		 * @param hudumaNumber the hudumaNumber to set
		 */
		public void setHudumaNumber(String hudumaNumber) {
			this.hudumaNumber = hudumaNumber;
		}

		/**
		 * @return the birthCertificateNumber
		 */
		public String getBirthCertificateNumber() {
			return birthCertificateNumber;
		}

		/**
		 * @param birthCertificateNumber the birthCertificateNumber to set
		 */
		public void setBirthCertificateNumber(String birthCertificateNumber) {
			this.birthCertificateNumber = birthCertificateNumber;
		}
		/**
		 * @return the alienIdNumber
		 */

		public String getAlienIdNumber() {
			return alienIdNumber;
		}
		/**
		 * @param alienIdNumber the alienIdNumber to set
		 */
		public void setAlienIdNumber(String alienIdNumber) {
			this.alienIdNumber = alienIdNumber;
		}
		/**
		 * @return the drivingLicenseNumber
		 */

		public String getDrivingLicenseNumber() {
			return drivingLicenseNumber;
		}
		/**
		 * @param drivingLicenseNumber the drivingLicenseNumber to set
		 */
		public void setDrivingLicenseNumber(String drivingLicenseNumber) {
			this.drivingLicenseNumber = drivingLicenseNumber;
		}


		/**
		 * @return the hivIdNumber
		 */
		public String getUniquePatientNumber() {
			return uniquePatientNumber;
		}

		/**
		 * @param uniquePatientNumber the uniquePatientNumber to set
		 */
		public void setUniquePatientNumber(String uniquePatientNumber) {
			this.uniquePatientNumber = uniquePatientNumber;
		}

		/**
		 * @return the nationalIdNumber
		 */
		public String getNationalIdNumber() {
			return nationalIdNumber;
		}

		/**
		 * @param nationalIdNumber the nationalIdNumber to set
		 */
		public void setNationalIdNumber(String nationalIdNumber) {

			this.nationalIdNumber = nationalIdNumber;
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
		public String getTelephoneContact() {
			return telephoneContact;
		}

		/**
		 * @param telephoneContact the telephoneContact to set
		 */
		public void setTelephoneContact(String telephoneContact) {
			this.telephoneContact = telephoneContact;
		}

		public Boolean getDead() {
			return dead;
		}

		public void setDead(Boolean dead) {
			this.dead = dead;
		}
		/*  greencard  */

		public Concept getInSchool() {
			return inSchool;
		}

		public void setInSchool(Concept inSchool) {
			this.inSchool = inSchool;
		}

		public Concept getOrphan() {
			return orphan;
		}

		public void setOrphan(Concept orphan) {
			this.orphan = orphan;
		}

		/*  .greencard   */
		public Date getDeathDate() {
			return deathDate;
		}

		public void setDeathDate(Date deathDate) {
			this.deathDate = deathDate;
		}

		/**
		 * @return the nameOfNextOfKin
		 */
		public String getNameOfNextOfKin() {
			return nameOfNextOfKin;
		}

		/**
		 * @param nameOfNextOfKin the nameOfNextOfKin to set
		 */
		public void setNameOfNextOfKin(String nameOfNextOfKin) {
			this.nameOfNextOfKin = nameOfNextOfKin;
		}

		/**
		 * @return the nextOfKinRelationship
		 */
		public String getNextOfKinRelationship() {
			return nextOfKinRelationship;
		}

		/**
		 * @param nextOfKinRelationship the nextOfKinRelationship to set
		 */
		public void setNextOfKinRelationship(String nextOfKinRelationship) {
			this.nextOfKinRelationship = nextOfKinRelationship;
		}

		/**
		 * @return the nextOfKinContact
		 */
		public String getNextOfKinContact() {
			return nextOfKinContact;
		}

		/**
		 * @param nextOfKinContact the nextOfKinContact to set
		 */
		public void setNextOfKinContact(String nextOfKinContact) {
			this.nextOfKinContact = nextOfKinContact;
		}

		/**
		 * @return the nextOfKinAddress
		 */
		public String getNextOfKinAddress() {
			return nextOfKinAddress;
		}

		/**
		 * @param nextOfKinAddress the nextOfKinAddress to set
		 */
		public void setNextOfKinAddress(String nextOfKinAddress) {
			this.nextOfKinAddress = nextOfKinAddress;
		}

		/**
		 * @return the subChiefName
		 */
		public String getSubChiefName() {
			return subChiefName;
		}

		/**
		 * @param subChiefName the subChiefName to set
		 */
		public void setSubChiefName(String subChiefName) {
			this.subChiefName = subChiefName;
		}

		public String getAlternatePhoneContact() {
			return alternatePhoneContact;
		}

		public void setAlternatePhoneContact(String alternatePhoneContact) {
			this.alternatePhoneContact = alternatePhoneContact;
		}

		public String getNearestHealthFacility() {
			return nearestHealthFacility;
		}

		public void setNearestHealthFacility(String nearestHealthFacility) {
			this.nearestHealthFacility = nearestHealthFacility;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}

		public String getGuardianFirstName() {
			return guardianFirstName;
		}

		public void setGuardianFirstName(String guardianFirstName) {
			this.guardianFirstName = guardianFirstName;
		}

		public String getGuardianLastName() {
			return guardianLastName;
		}

		public void setGuardianLastName(String guardianLastName) {
			this.guardianLastName = guardianLastName;
		}

		public String getChtReferenceNumber() {
			return chtReferenceNumber;
		}

		public void setChtReferenceNumber(String chtReferenceNumber) {
			this.chtReferenceNumber = chtReferenceNumber;
		}

		public String getkDoDCadre() {
			return kDoDCadre;
		}

		public void setkDoDCadre(String cadre) {
			this.kDoDCadre = cadre;
		}

		public String getkDoDRank() {
			return kDoDRank;
		}

		public void setkDoDRank(String rank) {
			this.kDoDRank = rank;
		}

		public String getkDoDServiceNumber() {
			return kDoDServiceNumber;
		}

		public void setkDoDServiceNumber(String kDoDServiceNumber) {
			this.kDoDServiceNumber = kDoDServiceNumber;
		}

		public String getkDoDUnit() {
			return kDoDUnit;
		}

		public void setkDoDUnit(String kDoDUnit) {
			this.kDoDUnit = kDoDUnit;
		}

		public String getCRVerificationStatus() {
			return CRVerificationStatus;
		}

		public void setCRVerificationStatus(String CRVerificationStatus) {
			this.CRVerificationStatus = CRVerificationStatus;
		}

	}

}
