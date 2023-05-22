/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.admin;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.Link;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *Shows patients linked to a particular provider
 */
@AppPage(EmrConstants.APP_CLINICIAN)
public class ProviderCaseManagerPageController {

	public void controller(@SpringBean KenyaUiUtils kenyaUi, @RequestParam(value = "personId", required = false) Person person,
						   UiUtils ui, PageModel model) {


		List<SimpleObject> patientsForThisProvider = new ArrayList<SimpleObject>();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(person)) {
				if (relationship.getRelationshipType().getaIsToB().equalsIgnoreCase("Case manager")) {
					Person personA = relationship.getPersonA();
					SimpleObject patientObject = SimpleObject.create("id", personA.getId(), "uuid", personA.getUuid(), "givenName", personA
							.getGivenName(), "middleName", personA.getMiddleName() != null ? personA.getMiddleName() : "", "familyName", personA.getFamilyName(),
							"startDate",kenyaUi.formatDate(relationship.getStartDate()),"endDate",relationship.getEndDate() != null ? kenyaUi.formatDate(relationship.getEndDate()) : "");
					patientsForThisProvider.add(patientObject);

				    }
				}

		String genderCode = person.getGender().toLowerCase();
		String linkUrl, linkIcon;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("personId", person.getId());
		linkUrl = ui.pageLink(EmrConstants.MODULE_ID, "admin/providerCaseManager", params);
		linkIcon = ui.resourceLink("kenyaui", "images/glyphs/person_" + genderCode + ".png");
		Link link = new Link(kenyaUi.formatPersonName(person), linkUrl, linkIcon);

		model.put("patientsForThisProviderList", ui.toJson(patientsForThisProvider));
		model.put("patientsForThisProviderSize", patientsForThisProvider.size());
		model.put("personLink", link);

	}
}