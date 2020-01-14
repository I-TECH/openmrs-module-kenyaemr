/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.apache.log4j.Logger;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ParentCalculation extends AbstractPatientCalculation {
	private final static Logger logger = Logger.getLogger(ChildrenGivenVaccineCalculation.class);
	
	private String parentToSearch = null;

	public ParentCalculation(String parentToSearch) {
		this.parentToSearch = parentToSearch;
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		PersonService personService = Context.getPersonService();
		RelationshipType parentChildType =  personService.getRelationshipTypeByUuid("8d91a210-c2cc-11de-8d13-0010c6dffd0f");
		String parentGender = null;

		if (this.parentToSearch.equals("Father")) {
			parentGender = "M";
		} else if (this.parentToSearch.equals("Mother")) {
			parentGender = "F";
		}

		if (parentGender != null) {

			for (Integer ptId : cohort) {
				Patient patient = Context.getPatientService().getPatient(ptId);
				List<Relationship> parentChildRel = personService.getRelationships(null, patient, parentChildType);

				// check if it is mothers
				Person parent = null;
				// a_is_to_b = 'Parent' and b_is_to_a = 'Child'
				for (Relationship relationship : parentChildRel) {

					if (patient.equals(relationship.getPersonB())) {
						if (relationship.getPersonA().getGender().equals(parentGender)) {
							parent = relationship.getPersonA();
							break;
						}
					} else if (patient.equals(relationship.getPersonA())) {
						if (relationship.getPersonB().getGender().equals(parentGender)) {
							parent = relationship.getPersonB();
							break;
						}
					}
				}


				if (parent == null) {
					ret.put(ptId, new SimpleResult(parent, this, context));
				} else {
					ret.put(ptId, new SimpleResult(parent.getPersonName().toString(), this, context));
				}
			}
		}

		return ret;

	}

}
