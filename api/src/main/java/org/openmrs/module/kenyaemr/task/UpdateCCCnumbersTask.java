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
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.module.kenyaemr.api.NUPIcccService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.module.kenyacore.calculation.Filters;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import org.openmrs.module.kenyaemr.nupi.NUPIcccSyncRegister;
import org.openmrs.module.kenyaemr.nupi.UpiUtilsDataExchange;

/**
 * A scheduled task that updates ccc numbers on NUPI in case ccc was missed
 */
public class UpdateCCCnumbersTask extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(AutoCloseActiveVisitsTask.class);
	NUPIcccService nUPIcccService = Context.getService(NUPIcccService.class);
	
	/**
	 * @see AbstractTask#execute()
	 */
	@Override
	public void execute() {
		System.out.println("UPDATE CCC TASK");
		if (!isExecuting) {
			if (log.isDebugEnabled()) {
				log.debug("Starting Update CCC numbers Task...");
				System.out.println("Starting Update CCC numbers Task...");
			}

			PatientService patientService = Context.getPatientService();

			startExecuting();
			List<Patient> allPatients = patientService.getAllPatients();
			System.out.println("Got all patients: " + allPatients.size());

			// Get patients on HIV program
			Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

			// NUPI
			PatientIdentifierType nationalUniquePatientIdentifier = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);

			// CCC
			PatientIdentifierType cccIdentifier = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);

			// loop checking for patients with NUPI and CCC and not already synced
			HashSet<Patient> patientsGroup = new HashSet<Patient>();
			for (Patient patient : allPatients) {
				if (patient != null) {
					ProgramWorkflowService pwfservice = Context.getProgramWorkflowService();
					List<PatientProgram> programs = pwfservice.getPatientPrograms(patient, hivProgram, null, null, null,null, true);
					if (programs.size() > 0) {
						if(patient.getDead() == false && patient.getPatientIdentifier(cccIdentifier) != null &&  patient.getPatientIdentifier(nationalUniquePatientIdentifier) != null) {
							NUPIcccSyncRegister ncsr = nUPIcccService.getLatestPatientRecordByPatient(patient);
							if((ncsr != null && ncsr.getCompleted() == false) || ncsr == null) {
								patientsGroup.add(patient);
							}
						}
					}	
				}
			}
			System.out.println("Patients with CCC and NUPI: " + patientsGroup.size());

			// Sync with remote
			UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
			try {
				Integer result = upiUtils.updateNUPIcccNumbers(patientsGroup);
				System.out.println("Finished the NUPI ccc numbers update: " + result);
			} catch(Exception x) {
				System.err.println("NUPI ccc number update Error: " + x.getMessage());
				x.printStackTrace();
			}
			
			stopExecuting();
		}
	}
}
