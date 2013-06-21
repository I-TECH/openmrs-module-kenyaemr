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

package org.openmrs.module.kenyaemr.form;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.ExistingVisitAssignmentHandler;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class EmrVisitAssignmentHandler extends ExistingVisitAssignmentHandler {

	/**
	 * @see org.openmrs.api.handler.ExistingVisitAssignmentHandler#getDisplayName(java.util.Locale)
	 */
	@Override
	public String getDisplayName(Locale locale) {
		return Context.getMessageSourceService().getMessage("Assigns to new or existing visit", null, locale);
	}

	/**
	 * @see org.openmrs.api.handler.ExistingVisitAssignmentHandler#beforeCreateEncounter(org.openmrs.Encounter)
	 */
	@Override
	public void beforeCreateEncounter(Encounter encounter) {
		// Do nothing if the encounter already belongs to a visit.
		if (encounter.getVisit() != null) {
			return;
		}

		// Try using an existing visit
		if (useExistingVisit(encounter)) {
			return;
		}

		// Some forms can auto-create visits
		if (encounter.getForm() != null) {
			FormDescriptor fd = KenyaEmr.getInstance().getFormManager().getFormDescriptor(encounter.getForm());
			VisitType autoCreateVisitType = fd.getAutoCreateVisitType();

			if (autoCreateVisitType != null) {
				useNewVisit(encounter, autoCreateVisitType);
			}
		}
	}

	/**
	 * Uses an existing a visit for the given encounter
	 * @param encounter the encounter
	 * @return true if a suitable visit was found
	 */
	protected boolean useExistingVisit(Encounter encounter) {
		// If encounter has time, then we need an exact fit for an existing visit
		if (KenyaEmrUtils.dateHasTime(encounter.getEncounterDatetime())) {
			List<Visit> visits = Context.getVisitService().getVisits(null, Collections.singletonList(encounter.getPatient()), null, null, null,
					encounter.getEncounterDatetime(), null, null, null, true, false);

			for (Visit visit : visits) {
				// Skip visits which ended before the encounter date
				if (visit.getStopDatetime() != null && visit.getStopDatetime().before(encounter.getEncounterDatetime())) {
					continue;
				}

				if (visit.getLocation() == null || Location.isInHierarchy(encounter.getLocation(), visit.getLocation())) {
					encounter.setVisit(visit);
					return true;
				}
			}
		}
		// If encounter does not have time, we can move it to fit any visit that day
		else {
			List<Visit> existingVisitsOnDay = Context.getService(KenyaEmrService.class).getVisitsByPatientAndDay(encounter.getPatient(), encounter.getEncounterDatetime());
			if (existingVisitsOnDay.size() > 0) {
				Visit visit = existingVisitsOnDay.get(0);
				encounter.setEncounterDatetime(visit.getStartDatetime());
				encounter.setVisit(visit);
				return true;
			}
		}

		return false;
	}

	/**
	 * Uses a new visit for the given encounter
	 * @param encounter the encounter
	 * @param type the visit type
	 */
	protected void useNewVisit(Encounter encounter, VisitType type) {
		Visit visit = new Visit();
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(encounter.getEncounterDatetime()));
		visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime()));
		visit.setLocation(encounter.getLocation());
		visit.setPatient(encounter.getPatient());
		visit.setVisitType(type);

		encounter.setVisit(visit);
	}
}