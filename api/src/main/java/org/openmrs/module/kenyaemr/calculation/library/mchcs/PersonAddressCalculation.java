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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
			PersonAddress personAddress = patient.getPersonAddress();
			List<String> addresses = new ArrayList<String>();

			// get village
			if (personAddress.getCityVillage() != null) {
				addresses.add(patient.getPersonAddress().getCityVillage());
			}

			// get landmark
			if (personAddress.getAddress2() != null) {
				addresses.add(patient.getPersonAddress().getAddress2());
			}

			if (addresses.size() > 0) {
				personAddressString = StringUtils.join(addresses, "|");

			}

			ret.put(ptId, new SimpleResult(personAddressString, this, context));
		}

		return ret;

	}

}
