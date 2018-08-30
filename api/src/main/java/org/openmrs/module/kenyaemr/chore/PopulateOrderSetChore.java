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

package org.openmrs.module.kenyaemr.chore;

import org.openmrs.api.PatientService;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.orderset.OrderSetManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component("kenyaemr.chore.populateOrderSetChore")
public class PopulateOrderSetChore extends AbstractChore {

	@Autowired
	private PatientService patientService;

	/**
	 * @see AbstractChore#perform(PrintWriter)
	 */
	@Override
	public void perform(PrintWriter output) {
		OrderSetManager setManager = new OrderSetManager();
		setManager.refresh();
	}
}
