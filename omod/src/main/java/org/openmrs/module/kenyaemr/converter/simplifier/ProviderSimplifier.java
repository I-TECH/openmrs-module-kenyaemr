/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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