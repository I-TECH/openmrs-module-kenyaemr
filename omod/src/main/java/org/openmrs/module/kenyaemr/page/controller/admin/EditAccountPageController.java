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

import java.util.Collection;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Editing user and provider accounts page
 */
@AppPage(EmrConstants.APP_ADMIN)
public class EditAccountPageController {
	
	public void controller(@RequestParam(value = "personId", required = false) Person person,
	                       PageModel model) {

		model.addAttribute("person", person);

		if (person != null) {
			model.addAttribute("user", getUser(person));
			model.addAttribute("provider", getProvider(person));
		}
		else {
			model.addAttribute("user", null);
			model.addAttribute("provider", null);
		}
	}

	/**
	 * Gets the first user associated with the given person
	 * @param person the person
	 * @return the user
	 */
    private User getUser(Person person) {
    	List<User> users = Context.getUserService().getUsersByPerson(person, true);
    	if (users == null || users.size() == 0) {
    		return null;
    	}
    	return users.get(0);
    }

	/**
	 * Gets the first provider associated with the given person
	 * @param person the person
	 * @return the provider
	 */
    private Provider getProvider(Person person) {
    	Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(person);
    	if (providers == null || providers.size() == 0) {
    		return null;
    	}
    	return providers.iterator().next();
    }
}