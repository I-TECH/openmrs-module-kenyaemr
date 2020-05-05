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

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinitionGroup;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Arrays;
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
		ConceptService concService = Context.getConceptService();

		List<RegimenDefinitionGroup> regimenGroups = regimenManager.getRegimenGroups(category);

		if (includeGroups != null) {
			regimenGroups = filterGroups(regimenGroups, includeGroups);
		}

		List<RegimenDefinition> regimenDefinitions = new ArrayList<RegimenDefinition>();
		for (RegimenDefinitionGroup group : regimenGroups) {
			regimenDefinitions.addAll(group.getRegimens());
		}
		List<SimpleObject> drugs = new ArrayList<SimpleObject>();
		List<Concept> arvDrugs = Arrays.asList(
				concService.getConcept(84309),
				concService.getConcept(86663),
				concService.getConcept(84795),
				concService.getConcept(78643),
				concService.getConcept(70057),
				concService.getConcept(75628),
				concService.getConcept(74807),
				concService.getConcept(80586),
				concService.getConcept(75523),
				concService.getConcept(79040),
				concService.getConcept(83412),
				concService.getConcept(71648),
				concService.getConcept(159810),
				concService.getConcept(154378),
				concService.getConcept(74258),
				concService.getConcept(164967)
		);

		for (Concept con: arvDrugs) {
			drugs.add(SimpleObject.create(
					"name", con.getName(),
					"drugUuid",con.getUuid()
			));
		}





		model.addAttribute("maxComponents", 5);
		model.addAttribute("drugs", regimenManager.getDrugs(category));
		model.addAttribute("regimenGroups", regimenGroups);
		model.addAttribute("regimenDefinitions", kenyaUi.simpleRegimenDefinitions(regimenDefinitions, ui));
		model.addAttribute("arvDrugs",drugs);

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
