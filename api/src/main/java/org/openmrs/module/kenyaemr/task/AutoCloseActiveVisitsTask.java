/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.task;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * A scheduled task that automatically closes all unvoided active visits
 * Set date_stopped to 11 23:59:59 of the date_started
 */
public class AutoCloseActiveVisitsTask extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(AutoCloseActiveVisitsTask.class);
	
	/**
	 * @see AbstractTask#execute()
	 */
	@Override
	public void execute() {
		if (!isExecuting) {
			if (log.isDebugEnabled()) {
				log.debug("Starting Auto Close Visits Task...");
			}

			startExecuting();
			List<Patient> allPatients = Context.getPatientService().getAllPatients();

			// Fetch all visits haven't ended or been closed and close them
			List<Visit> visits = Context.getVisitService().getVisits(null, allPatients, null, null, null, new Date(), null, null, null, true, false);
			if (visits.size() > 0) {
				for (Visit visit : visits) {
					try {

					visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(visit.getStartDatetime()));

					} catch (Exception e) {
						log.error("Error while auto closing visits:", e);
					} finally {
						stopExecuting();
					}
				}
			}
		}
	}
}
