/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.wrapper.AbstractCustomizableWrapper;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.util.OpenmrsUtil;

/**
 * Wrapper class for visits
 */
public class VisitWrapper extends AbstractCustomizableWrapper<Visit, VisitAttribute> {

	/**
	 * Creates a visit wrapper
	 * @param target the visit
	 */
	public VisitWrapper(Visit target) {
		super(target);
	}

	/**
	 * Checks the visit to see if it overlaps with any other visit for that patient
	 * @return true if visit overlaps
	 */
	public boolean overlaps() {
		Patient patient = target.getPatient();

		for (Visit existingVisit : Context.getVisitService().getVisitsByPatient(patient)) {
			// If visit exists in database, don't compare to itself
			if (existingVisit.getVisitId().equals(target.getVisitId())) {
				continue;
			}

			if (OpenmrsUtil.compareWithNullAsLatest(target.getStartDatetime(), existingVisit.getStopDatetime()) <= 0 &&
					OpenmrsUtil.compareWithNullAsLatest(target.getStopDatetime(), existingVisit.getStartDatetime()) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the source form (may be null)
	 * @return the form
	 */
	public Form getSourceForm() {
		return (Form) getAsAttribute(CommonMetadata._VisitAttributeType.SOURCE_FORM);
	}
}