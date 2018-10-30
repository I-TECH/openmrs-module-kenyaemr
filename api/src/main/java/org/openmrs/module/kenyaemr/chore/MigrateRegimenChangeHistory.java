/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.openmrs.Concept;
import org.openmrs.OrderSet;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenJsonGenerator;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * handles migration of orders to populate model for DrugRegimenHistory
 */
@Component("kenyaemr.chore.migrateRegimenChangeHistory")
public class MigrateRegimenChangeHistory extends AbstractChore {

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private FormService formService;
	/**
	 * @see AbstractChore#perform(PrintWriter)
	 */

	@Override
	public void perform(PrintWriter out) {

		ConceptService conceptService = Context.getConceptService();
		Concept ARVRegimenConcept = conceptService.getConcept(1205);
		Concept TBRegimenConcept = conceptService.getConcept(160021);
		List<Concept> regimenConcepts = Arrays.asList(ARVRegimenConcept, TBRegimenConcept);


		List<Patient> allPatients = Context.getPatientService().getAllPatients();


		/*for (Patient patient : allPatients) {
			for (Concept masterSet : regimenConcepts) {
				RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
				List<RegimenChange> changes = history.getChanges();
				System.out.println("Processing patient: " + patient.getPatientId());
				System.out.println("Processing patient changes: " + changes.size());

				for (RegimenChange change : changes) {
					RegimenOrder regimen = change.getStarted();
					Date startDate = change.getDate();
					OrderSet regimenOrderset = RegimenJsonGenerator.getOrderSetFromMembers(regimen.getDrugOrders());
					*//*if (regimenOrderset != null) {
						String ordersetName = regimenOrderset.getName();
						Integer ordersetID = regimenOrderset.getOrderSetId();

						Date endDate = null;
						List<String> changeReasons = new ArrayList<String>();



						*//**//*if (change.getChangeReasons() != null) {
							for (Concept c : change.getChangeReasons()) {
								changeReasons.add(ui.format(c));
							}
						}
						if (change.getChangeReasonsNonCoded() != null) {
							changeReasons.addAll(change.getChangeReasonsNonCoded());
						}*//**//*

					}*//*
				}

			}

		}*/

		out.println("Matching found "+" observations");

	}
}
