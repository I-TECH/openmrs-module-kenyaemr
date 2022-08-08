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

import org.openmrs.Visit;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts visit to simple object
 */
@Component
public class VisitSimplifier extends AbstractSimplifier<Visit> {

	@Autowired
	private KenyaUiUtils kenyaui;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Visit visit) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", visit.getId());
		ret.put("visitType", visit.getVisitType().getName());
		ret.put("startDatetime", kenyaui.formatDateParam(visit.getStartDatetime()));
		ret.put("stopDatetime", kenyaui.formatDateParam(visit.getStopDatetime()));
		return ret;
	}
}