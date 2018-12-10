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

import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.Collection;
import java.util.Map;

public class PersonAddressCalculation extends AbstractPatientCalculation {

	private String address;
	

	public PersonAddressCalculation(String address) {
		this.address = address.toLowerCase();
	}

	public PersonAddressCalculation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			String personAddressString = null;

			Patient patient = Context.getPatientService().getPatient(ptId);
			
			if (this.address == null) {

				PersonAddress personAddress = patient.getPersonAddress();
				
				if (personAddress != null) {

					String landmark = personAddress.getAddress2();
					String estate = personAddress.getCityVillage();

					personAddressString = "";
					if (landmark != null && !landmark.equals("")) {
						personAddressString += landmark;
					}

					if (estate != null && !estate.equals("")) {
						personAddressString += " | ";
						personAddressString += estate;
					}
				}
			}
			else {
				
				if (this.address.equals("province")) {
					personAddressString = patient.getPersonAddress().getStateProvince();
				}
				
				if (this.address.equals("district")) {
					personAddressString = patient.getPersonAddress().getCountyDistrict();
				}

				if (this.address.equals("division")) {
					personAddressString = patient.getPersonAddress().getAddress4();
				}

				if (this.address.equals("location")) {
					personAddressString = patient.getPersonAddress().getAddress6();
				}				

				if (this.address.equals("sublocation")) {
					personAddressString = patient.getPersonAddress().getAddress5();
				}

				if (this.address.equals("landmark")) {
					personAddressString = patient.getPersonAddress().getAddress2();
				}

				if (this.address.equals("village")) {
					personAddressString = patient.getPersonAddress().getCityVillage();
				}
				
			}

			ret.put(ptId, new SimpleResult(personAddressString, this, context));
		}

		return ret;

	}

}
