/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.openmrs.module.kenyacore.calculation.CalculationManager;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying all patient flag content
 */
public class FlagsContentFragmentController {

	public void controller(FragmentModel model, @SpringBean CalculationManager calculationManager) {
		List<SimpleObject> flags = new ArrayList<SimpleObject>();
		for (PatientFlagCalculation calc : calculationManager.getFlagCalculations()) {

			flags.add(SimpleObject.create("className", calc.getClass().getSimpleName(), "message", calc.getFlagMessage()));
		}

		model.addAttribute("flags", flags);
	}
}