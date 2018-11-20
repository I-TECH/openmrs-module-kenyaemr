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

import org.openmrs.User;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts a user to a simple object
 */
@Component
public class UserSimplifier extends AbstractSimplifier<User> {

	@Autowired
	private UiUtils ui;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(User user) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", user.getId());
		ret.put("username", user.getUsername());
		ret.put("person", ui.simplifyObject(user.getPerson()));
		return ret;
	}
}