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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.Provider;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts a provider to a simple object
 */
@Component
public class ProviderSimplifier extends AbstractSimplifier<Provider> {

	@Autowired
	private PersonSimplifier personSimplifier;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Provider provider) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", provider.getId());

		// Add person
		ret.put("person", personSimplifier.convert(provider.getPerson()));

		// Add identifier
		ret.put("identifier", provider.getIdentifier());
		return ret;
	}
}