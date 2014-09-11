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