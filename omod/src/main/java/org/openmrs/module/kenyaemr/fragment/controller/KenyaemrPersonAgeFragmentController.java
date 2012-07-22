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

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.openmrs.Person;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 *
 */
public class KenyaemrPersonAgeFragmentController {
	
	public void controller(@FragmentParam("person") Person person, FragmentModel model) {
		Integer ageInYears = person.getAge();
		model.put("ageInYears", ageInYears);
		if (ageInYears < 1) {
			Period p = new Period(person.getBirthdate().getTime(), System.currentTimeMillis(), PeriodType.yearMonthDay());
			model.put("ageInMonths", p.getMonths());
			model.put("ageInDays", p.getDays());
		}
	}
	
}
