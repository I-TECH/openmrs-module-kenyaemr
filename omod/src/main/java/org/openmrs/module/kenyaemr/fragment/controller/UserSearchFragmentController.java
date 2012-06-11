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

import java.util.Iterator;
import java.util.List;

import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles AJAX User searches
 */
public class UserSearchFragmentController {
	
	public List<SimpleObject> search(@RequestParam(value = "q", required = false) String query,
	                                 @RequestParam(value = "role", required = false) List<Role> roles,
	                                 UiUtils ui) {
		List<User> users = Context.getUserService().getUsers(query, roles, true);
		for (Iterator<User> i = users.iterator(); i.hasNext(); ) {
			if ("daemon".equals(i.next().getUsername())) {
				i.remove();
			}
		}
		return SimpleObject.fromCollection(users, ui, "userId", "username", "systemId", "personName", "roles");
	}
	
}
