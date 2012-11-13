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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.session.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class VisitCompletedFormsFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam("visit") Visit visit) {
		List<Encounter> encounters = new ArrayList<Encounter>(visit.getEncounters());
		CollectionUtils.filter(encounters, new Predicate() {
			@Override
			public boolean evaluate(Object enc) {
				return !((Encounter) enc).getVoided();
			}
		});
		Collections.sort(encounters, new Comparator<Encounter>() {
			@Override
			public int compare(Encounter left, Encounter right) {
				return left.getEncounterDatetime().compareTo(right.getEncounterDatetime());
			}
		});

		model.addAttribute("encounters", encounters);
	}
}
