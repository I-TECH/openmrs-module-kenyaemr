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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.PersonByNameComparator;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Various actions for the admin app
 */
public class AdminUtilFragmentController {
	
	public List<SimpleObject> accountSearch(@RequestParam(value = "q", required = false) String query,
	                                        @RequestParam(value = "includeUsers", required = false) Boolean includeUsers,
	                                        @RequestParam(value = "includeProviders", required = false) Boolean includeProviders,
	                                        UiUtils ui) {
		Map<Person, User> userAccounts = new HashMap<Person, User>();
		Map<Person, Provider> providerAccounts = new HashMap<Person, Provider>();
		
		if (includeUsers != null && includeUsers) {
			List<User> users = Context.getUserService().getUsers(query, null, true);
			for (User u : users) {
				if (!"daemon".equals(u.getUsername())) {
					userAccounts.put(u.getPerson(), u);
				}
			}
		}
		
		if (includeProviders != null && includeProviders) {
			List<Provider> providers = Context.getProviderService().getProviders(query, null, null, null);
			for (Provider p : providers) {
				if (p.getPerson() != null) {
					providerAccounts.put(p.getPerson(), p);
				}
			}
		}
		
		Set<Person> persons = new TreeSet<Person>(new PersonByNameComparator());
		persons.addAll(userAccounts.keySet());
		persons.addAll(providerAccounts.keySet());
		
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Person p : persons) {
			SimpleObject account = SimpleObject.fromObject(p, ui, "personId", "personName");
			User user = userAccounts.get(p);
			if (user != null) {
				account.put("user", SimpleObject.fromObject(user, ui, "username"));
			}
			Provider provider = providerAccounts.get(p);
			if (provider != null) {
				account.put("provider", SimpleObject.fromObject(provider, ui, "identifier"));
			}
			ret.add(account);
		}
		
		return ret;
	}
	
}
