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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientIdentifierType.LocationBehavior;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.validator.ValidateUtil;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Controller for the fragment that lets you create a patient via the MoH 257 data set
 */
public class RegistrationCreatePatientFragmentController {
	
	public void controller(FragmentModel model) {
		PatientService ps = Context.getPatientService();
		List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
		identifierTypes.add(ps.getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID));
		identifierTypes.add(ps.getPatientIdentifierTypeByUuid(MetadataConstants.PATIENT_CLINIC_NUMBER_UUID));
		model.addAttribute("identifierTypes", identifierTypes);
		
		model.addAttribute("telephoneContact", Context.getPersonService().getPersonAttributeTypeByUuid(MetadataConstants.TELEPHONE_CONTACT_UUID));
	}
	
	public SimpleObject createPatient(UiUtils ui,
	                                  @MethodParam("createPatientCommand") @BindParams CreatePatientCommand command) {
		ui.validate(command, command, null);
		Patient created = Context.getPatientService().savePatient(command.toPatient());
		return SimpleObject.create("patientId", created.getPatientId());
	}
	
	public CreatePatientCommand createPatientCommand(Session session) {
		return new CreatePatientCommand(RegistrationUtilFragmentController.getCurrentLocation(session));
	}


	/**
	 * For some reason PatientValidator is letting me create a patient without a birthdate/age, so
	 * I'm going to write a stricter validator here
	 */
	public class CreatePatientCommand implements Validator {

		private Location defaultLocation;
		
		private List<PatientIdentifier> identifiers = new AutoPopulatingList<PatientIdentifier>(PatientIdentifier.class);
		private List<PersonName> names = new AutoPopulatingList<PersonName>(PersonName.class);
		private List<PersonAttribute> attributes = new AutoPopulatingList<PersonAttribute>(PersonAttribute.class);
		private String gender;
		private Date birthdate;
		private Integer age;
		
		public CreatePatientCommand(Location defaultLocation) {
			this.defaultLocation = defaultLocation;
		}

        /**
         * @return the identifiers
         */
        public List<PatientIdentifier> getIdentifiers() {
        	return identifiers;
        }
		
        /**
         * @param identifiers the identifiers to set
         */
        public void setIdentifiers(List<PatientIdentifier> identifiers) {
        	this.identifiers = identifiers;
        }
		
        /**
         * @return the names
         */
        public List<PersonName> getNames() {
        	return names;
        }
		
        /**
         * @param names the names to set
         */
        public void setNames(List<PersonName> names) {
        	this.names = names;
        }	
        
        /**
         * @return the attributes
         */
        public List<PersonAttribute> getAttributes() {
        	return attributes;
        }

        /**
         * @param attributes the attributes to set
         */
        public void setAttributes(List<PersonAttribute> attributes) {
        	this.attributes = attributes;
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
	     * @see org.springframework.validation.Validator#supports(java.lang.Class)
	     */
	    @Override
	    public boolean supports(Class<?> clazz) {
	        return clazz.equals(getClass());
	    }
	    
		/**
	    * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	    */
	    @Override
	    public void validate(Object target, Errors errors) {
	    	CreatePatientCommand command = (CreatePatientCommand) target;
	    	
	    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "error.requiredField");
	    	
	    	if (command.birthdate == null && command.age == null) {
	    		errors.rejectValue("age", "error.requiredField");
	    	}
	    	
	    	if (names.size() == 0) {
	    		errors.rejectValue("names[0].familyName", "error.requiredField");
	    	}
	    	for (int i = 0; i < names.size(); ++i) {
	    		errors.pushNestedPath("names[" + i + "]");
	    		// This doesn't work because of TRUNK-2296 (PersonNameValidator gives the wrong field names)
	    		//     ValidateUtil.validate(names.get(i), errors);
	    		// I don't like the error messages this actually reports...
	    		//     new PersonNameValidator().validatePersonName(names.get(i), errors, false, true);
	    		for (String fieldName : Arrays.asList("givenName", "familyName")) {
	    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "error.requiredField");
	    		}
	    		errors.popNestedPath();
	    	}
	    	
	    	for (int i = 0; i < attributes.size(); ++i) {
	    		errors.pushNestedPath("attributes[" + i + "]");
	    		ValidateUtil.validate(attributes.get(i), errors);
	    		errors.popNestedPath();
	    	}
	    	
	    	boolean nonBlankIdentifiers = false;
	    	for (int i = 0; i < identifiers.size(); ++i) {
	    		errors.pushNestedPath("identifiers[" + i + "]");
	    		PatientIdentifier id = identifiers.get(i);
	    		if (StringUtils.isNotBlank(id.getIdentifier())) {
	    			nonBlankIdentifiers = true;
	    			if (id.getIdentifierType() != null && !LocationBehavior.NOT_USED.equals(id.getIdentifierType().getLocationBehavior()) && id.getLocation() == null) {
	    				id.setLocation(defaultLocation);
	    			}
	    			ValidateUtil.validate(identifiers.get(i), errors);
	    		}
	    		errors.popNestedPath();
	    	}
	    	if (!nonBlankIdentifiers) {
	    		errors.rejectValue("identifiers[" + (identifiers.size() - 1) + "].identifier", "error.requiredField");
	    	}
	    	
	    	if (!errors.hasErrors()) {
	    		// make sure there aren't any errors we're missing
    			Patient pt = command.toPatient();
    			BindException ptErrors = new BindException(pt, "patient");
    			ValidateUtil.validate(pt, ptErrors);

    			// TODO move this to a utility method
    			if (ptErrors.hasErrors()) {
    				for (Object err : ptErrors.getGlobalErrors()) {
    					if (err instanceof FieldError) {
    						FieldError fieldErr = (FieldError) err;
    						errors.reject(fieldErr.getField() + ": " + Context.getMessageSourceService().getMessage(fieldErr, Context.getLocale()));
    					} else {
    						errors.reject(Context.getMessageSourceService().getMessage((ObjectError) err, Context.getLocale()));
    					}
    				}
    			}
	    	}
	    }

		/**
	     * Assembles this command object into a patient. Make sure you validate before trying to save this.
	     */
	    public Patient toPatient() {
		    Patient ret = new Patient();
		    for (PersonName name : names) {
		    	ret.addName(name);
		    }
		    for (PatientIdentifier id : identifiers) {
		    	if (StringUtils.isNotBlank(id.getIdentifier())) {
		    		ret.addIdentifier(id);
		    	}
		    }
		    if (ret.getActiveIdentifiers().size() > 0) {
		    	ret.getActiveIdentifiers().get(0).setPreferred(true);
		    }
		    for (PersonAttribute attr : attributes) {
		    	if (StringUtils.isNotBlank(attr.getValue())) {
		    		ret.addAttribute(attr);
		    	}
		    }
		    ret.setGender(gender);
		    if (birthdate != null) {
		    	ret.setBirthdate(birthdate);
		    } else {
		    	ret.setBirthdateFromAge(age, null);
		    	ret.setBirthdateEstimated(true);
		    }
		    return ret;
	    }

   }
	
}
