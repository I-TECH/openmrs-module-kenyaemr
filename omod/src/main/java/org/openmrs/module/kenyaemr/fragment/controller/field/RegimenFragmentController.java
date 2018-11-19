/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.field;

import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinitionGroup;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class RegimenFragmentController {

	public void controller(@FragmentParam("category") String category,
						   @FragmentParam(value = "includeGroups", required = false) Set<String> includeGroups,
						   FragmentModel model,
						   UiUtils ui,
						   @SpringBean RegimenManager regimenManager,
						   @SpringBean EmrUiUtils kenyaUi) {

		List<RegimenDefinitionGroup> regimenGroups = regimenManager.getRegimenGroups(category);

		if (includeGroups != null) {
			regimenGroups = filterGroups(regimenGroups, includeGroups);
		}

		List<RegimenDefinition> regimenDefinitions = new ArrayList<RegimenDefinition>();
		for (RegimenDefinitionGroup group : regimenGroups) {
			regimenDefinitions.addAll(group.getRegimens());
		}

		model.addAttribute("maxComponents", 5);
		model.addAttribute("drugs", regimenManager.getDrugs(category));
		model.addAttribute("regimenGroups", regimenGroups);
		model.addAttribute("regimenDefinitions", kenyaUi.simpleRegimenDefinitions(regimenDefinitions, ui));
	}

	/**
	 * Filter regimen groups by code
	 * @param groups the groups
	 * @param includeGroupCodes the group codes to include
	 * @return the filtered groups
	 */
	private static List<RegimenDefinitionGroup> filterGroups(List<RegimenDefinitionGroup> groups, Set<String> includeGroupCodes) {
		List<RegimenDefinitionGroup> filtered = new ArrayList<RegimenDefinitionGroup>();
		for (RegimenDefinitionGroup group : groups) {
			if (includeGroupCodes.contains(group.getCode())) {
				filtered.add(group);
			}
		}
		return filtered;
	}
}