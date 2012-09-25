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
package org.openmrs.module.kenyaemr;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.OpenmrsData;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Convenience base class for classes that can validate themselves, e.g. command objects
 */
public abstract class ValidatingCommandObject implements Validator {
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return getClass().isAssignableFrom(clazz);
	}
	
	public void require(Errors errors, String field) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, "error.requiredField");
	}
	
	public void requireAny(Errors errors, String... fields) {
		for (String field : fields) {
			Object value = errors.getFieldValue(field);
			if (value != null && !"".equals(value)) {
				return;
			}
		}
		errors.rejectValue(fields[0], "error.requiredField");
	}
	
	public void validateField(Errors errors, String field) {
		validateField(errors, field, false);
	}
		
	public void validateField(Errors errors, String field, boolean required) {
		Object value = errors.getFieldValue(field);
		if (value == null) {
			if (required) {
				errors.rejectValue(field, "error.requiredField");
			} else {
				return;
			}
		}
		errors.pushNestedPath(field);
		ValidateUtil.validate(value, errors);
		errors.popNestedPath();
	}
	
	public void voidData(OpenmrsData data) {
		if (!data.isVoided()) {
			data.setVoided(true);
			data.setDateVoided(new Date());
			data.setVoidedBy(Context.getAuthenticatedUser());
		}
	}
	
	public boolean anyChanges(Object from, Object to, String... props) {
		if (from == null && to == null) {
			return false;
		} else if (from == null || to == null) {
			return true;
		}
		for (String prop : props) {
			try {
				Object fromVal = PropertyUtils.getProperty(from, prop);
				Object toVal = PropertyUtils.getProperty(to, prop);
				if (!OpenmrsUtil.nullSafeEquals(fromVal, toVal)) {
					return true;
				}
			} catch (Exception ex) {
				throw new IllegalArgumentException("Error getting property: " + prop, ex);
			}
		}
		return false;
	}

    public void requireNumericsOnly(PersonAttribute telephoneContact,Errors errors, String field) {

        Pattern pattern = Pattern.compile("\\d{10}");
        Matcher matcher = pattern.matcher(telephoneContact.getValue().trim());

        if(!(matcher.matches())){
            errors.rejectValue("telephoneContact","Phone Number can not exceed ten(10) digits and should be positve numbers only");
        }
    }
	
}
