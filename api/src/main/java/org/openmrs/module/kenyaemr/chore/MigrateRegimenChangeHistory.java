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
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenJsonGenerator;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * handles migration of orders to populate model for DrugRegimenHistory
 */
@Component("kenyaemr.chore.migrateRegimenChangeHistory")
public class MigrateRegimenChangeHistory extends AbstractChore {
	
	/**
	 * @see AbstractChore#perform(PrintWriter)
	 */

	@Override
	public void perform(PrintWriter out) {

		ConceptService conceptService = Context.getConceptService();
		Concept ARVRegimenConcept = conceptService.getConcept(1085);
		Concept TBRegimenConcept = conceptService.getConcept(160021);

		int tbRegimenConceptId = 160021;
		int arvRegimenConceptId = 1085;

		List<Patient> allPatients = Context.getPatientService().getAllPatients(false);


		/*for (Patient patient : allPatients) {
			RegimenChangeHistory tbRegimenHistory = RegimenChangeHistory.forPatient(patient, TBRegimenConcept);
			RegimenChangeHistory hivRegimenHistory = RegimenChangeHistory.forPatient(patient, ARVRegimenConcept);

			List<RegimenChange> tbRegimenChanges = tbRegimenHistory.getChanges();
			List<RegimenChange> arvRegimenChanges = hivRegimenHistory.getChanges();

			System.out.println("Processing patient: " + patient.getPatientId() + ", " +
					"TB Rg changes: " + tbRegimenChanges.size() + ", ARV rg changes: " + arvRegimenChanges.size());

			if (tbRegimenChanges.size() < 1 && arvRegimenChanges.size() < 1) { continue;}

			if (tbRegimenChanges.size() > 0) {
				//processRegimenChanges(patient, tbRegimenConceptId, tbRegimenChanges);
			}

			if (arvRegimenChanges.size() > 0) {
				//processRegimenChanges(patient, arvRegimenConceptId, arvRegimenChanges);

			}

		}*/
		out.println("Completed migration for drug regimen history");

	}

	private void processRegimenChanges(Patient patient, int masterSet, List<RegimenChange> changes) {
		for (RegimenChange change : changes) {
            RegimenOrder regimen = change.getStarted();
            if (regimen != null) {
				Date startDate = change.getDate();
				OrderSet regimenOrderset = RegimenJsonGenerator.getOrderSetFromMembers(regimen.getDrugOrders());
				if (regimenOrderset != null) {
					String ordersetName = regimenOrderset.getName();
					Integer ordersetID = regimenOrderset.getOrderSetId();

					Date endDate = null;
					List<String> changeReasons = new ArrayList<String>();

					/*DrugRegimenHistory changeEvent = new DrugRegimenHistory();
					changeEvent.setPatient(patient);
					changeEvent.setOrderSetId(ordersetID);
					changeEvent.setRegimenName(ordersetName);
					changeEvent.setDateStarted(startDate);
					changeEvent.setProgram(masterSet == 1085 ? "HIV" : "TB");*/
					//historyService.saveDrugRegimenHistory(changeEvent);

                /*if (change.getChangeReasons() != null) {
                    for (Concept c : change.getChangeReasons()) {
                        changeReasons.add(ui.format(c));
                    }
                }
                if (change.getChangeReasonsNonCoded() != null) {
                    changeReasons.addAll(change.getChangeReasonsNonCoded());
                }*/

				}
            }

        }
	}
}
