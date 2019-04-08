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

import org.openmrs.PatientIdentifier;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.stereotype.Component;

/**
 * Converts a patient identifier to a simple object
 */
@Component
public class PatientIdentifierSimplifier extends AbstractSimplifier<PatientIdentifier> {

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(PatientIdentifier pid) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", pid.getId());
		ret.put("identifierType", pid.getIdentifierType().getName());
		ret.put("identifier", pid.getIdentifier());
		ret.put("preferred", pid.isPreferred());
		return ret;
	}
}