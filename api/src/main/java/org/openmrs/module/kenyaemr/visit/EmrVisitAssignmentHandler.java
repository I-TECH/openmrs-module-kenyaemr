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

package org.openmrs.module.kenyaemr.visit;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.BaseEncounterVisitHandler;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaemr.wrapper.VisitWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Unlike the regular visit handlers, this one will be called even for existing encounters as we sometimes need to move
 * these into new visits
 */
public class EmrVisitAssignmentHandler extends BaseEncounterVisitHandler implements ExistingEncounterVisitHandler {

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
		// Some forms can auto-create visits
		VisitType autoCreateVisitType = getAutoCreateVisitType(encounter);

		assignToVisit(encounter, autoCreateVisitType);
	}

	/**
	 * Called via our own AOP to ensure the visit handler is used for both new and existing encounters
	 *
	 * @see ExistingEncounterVisitHandler#beforeEditEncounter(org.openmrs.Encounter)
	 */
	@Override
	public void beforeEditEncounter(Encounter encounter) {
		// This determines whether the encounter can move between visits
		VisitType autoCreateVisitType = getAutoCreateVisitType(encounter);

		Visit oldVisit = encounter.getVisit();

		assignToVisit(encounter, autoCreateVisitType);

		// Is encounter is now assigned to a different visit?
		if (oldVisit != null && !oldVisit.equals(encounter.getVisit())) {
			boolean existingIsNowEmpty = CollectionUtils.isEmpty(oldVisit.getEncounters());
			boolean existingWasRetro = new VisitWrapper(oldVisit).getSourceForm() != null;

			// If the existing visit was created via a form and is now empty, void it
			if (existingIsNowEmpty && existingWasRetro) {
				Context.getVisitService().voidVisit(oldVisit, "Left empty by encounter move");
			}
		}
	}

	/**
	 * Does the actual assignment of the encounter to a visit
	 * @param encounter the encounter
	 * @param newVisitType the type of the new visit if one is created
	 */
	protected void assignToVisit(Encounter encounter, VisitType newVisitType) {
		// Do nothing if the encounter already belongs to a visit and can't be moved
		if (encounter.getVisit() != null && newVisitType == null) {
			return;
		}

		// Try using an existing visit
		if (!useExistingVisit(encounter)) {
			if (newVisitType != null) {
				useNewVisit(encounter, newVisitType, encounter.getForm());
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
		if (EmrUtils.dateHasTime(encounter.getEncounterDatetime())) {
			List<Visit> visits = Context.getVisitService().getVisits(null, Collections.singletonList(encounter.getPatient()), null, null, null,
					encounter.getEncounterDatetime(), null, null, null, true, false);

			for (Visit visit : visits) {
				// Skip visits which ended before the encounter date
				if (visit.getStopDatetime() != null && visit.getStopDatetime().before(encounter.getEncounterDatetime())) {
					continue;
				}

				if (checkLocations(visit, encounter)) {
					setVisitOfEncounter(visit, encounter);
					return true;
				}
			}
		}
		// If encounter does not have time, we can move it to fit any visit that day
		else {
			List<Visit> existingVisitsOnDay = Context.getService(KenyaEmrService.class).getVisitsByPatientAndDay(encounter.getPatient(), encounter.getEncounterDatetime());
			if (existingVisitsOnDay.size() > 0) {
				Visit visit = existingVisitsOnDay.get(0);

				if (checkLocations(visit, encounter)) {
					setVisitOfEncounter(visit, encounter);

					// Adjust encounter start if its before visit start
					if (encounter.getEncounterDatetime().before(visit.getStartDatetime())) {
						encounter.setEncounterDatetime(visit.getStartDatetime());
					}

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Uses a new visit for the given encounter
	 * @param encounter the encounter
	 * @param type the visit type
	 * @param sourceForm the source form
	 */
	protected static void useNewVisit(Encounter encounter, VisitType type, Form sourceForm) {
		Visit visit = new Visit();
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(encounter.getEncounterDatetime()));
		visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime()));
		visit.setLocation(encounter.getLocation());
		visit.setPatient(encounter.getPatient());
		visit.setVisitType(type);

		VisitAttribute sourceAttr = new VisitAttribute();
		sourceAttr.setAttributeType(MetadataUtils.existing(VisitAttributeType.class, CommonMetadata._VisitAttributeType.SOURCE_FORM));
		sourceAttr.setOwner(visit);
		sourceAttr.setValue(sourceForm);
		visit.addAttribute(sourceAttr);

		Context.getVisitService().saveVisit(visit);

		setVisitOfEncounter(visit, encounter);
	}

	/**
	 * Gets an auto-create visit type if there is one for the form used to create the encounter
	 * @param encounter the encounter
	 * @return the visit type
	 */
	protected static VisitType getAutoCreateVisitType(Encounter encounter) {
		if (encounter.getForm() != null) {
			FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);

			FormDescriptor fd = formManager.getFormDescriptor(encounter.getForm());

			if (fd != null && fd.getAutoCreateVisitTypeUuid() != null) {
				return MetadataUtils.existing(VisitType.class, fd.getAutoCreateVisitTypeUuid());
			}
		}
		return null;
	}

	/**
	 * Convenience method to check whether the location of a visit and an encounter are compatible
	 * @param visit the visit
	 * @param encounter the encounter
	 * @return true if locations won't conflict
	 */
	protected static boolean checkLocations(Visit visit, Encounter encounter) {
		return visit.getLocation() == null || Location.isInHierarchy(encounter.getLocation(), visit.getLocation());
	}

	/**
	 * Sets the visit of an encounter, updating the both the old visit and the new visit. This is used rather than just
	 * encounter.setVisit(...) so that we don't have to reload the visit objects to update their set of encounters
	 * @param visit the visit
	 * @param encounter the encounter
	 */
	protected static void setVisitOfEncounter(Visit visit, Encounter encounter) {
		// Remove from old visit
		if (encounter.getVisit() != null) {
			encounter.getVisit().getEncounters().remove(encounter);
		}

		// Set to new visit
		encounter.setVisit(visit);

		if (visit != null) {
			visit.addEncounter(encounter);
		}
	}
}