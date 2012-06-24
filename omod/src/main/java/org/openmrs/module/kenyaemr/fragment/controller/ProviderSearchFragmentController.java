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

import java.util.List;

import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles AJAX Provider searches
 */
public class ProviderSearchFragmentController {
	
	public List<SimpleObject> search(@RequestParam(value = "q", required = false) String query, UiUtils ui) {
		List<Provider> providers = Context.getProviderService().getProviders(query, null, null, null);
		return SimpleObject.fromCollection(providers, ui, "providerId", "name", "person.personName", "attributes.attributeType", "attributes.value");
	}
	
}
