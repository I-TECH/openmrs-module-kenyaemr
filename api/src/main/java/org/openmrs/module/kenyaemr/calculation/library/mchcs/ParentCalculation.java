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
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.Collection;
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

		for (Integer ptId : cohort) {
			Person parentObj = null;

			Patient patient = Context.getPatientService().getPatient(ptId);
			parentSearch: for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
				logger.info("Relationship found. Enumerating ...");
				logger.info("Relationship type. " + relationship.getRelationshipType().getbIsToA());
				
				if (relationship.getRelationshipType().getbIsToA().toLowerCase().equals("parent")) {
					if (this.parentToSearch.equals("Father")) {
						
						if (relationship.getPersonB().getGender().equals("M")) {
							parentObj = relationship.getPersonB();
							break parentSearch;
						}
						
					} else if (this.parentToSearch.equals("Mother")) {
						
						if (relationship.getPersonB().getGender().equals("F")) {
							parentObj = relationship.getPersonB();
							break parentSearch;
						}

					}
					
				}
				
			}
			
			if (parentObj == null) {
				ret.put(ptId, new SimpleResult(parentObj, this, context));
			}else{
				ret.put(ptId, new SimpleResult(parentObj.getPersonName().toString(), this, context));
			}
		}

		return ret;

	}

}
