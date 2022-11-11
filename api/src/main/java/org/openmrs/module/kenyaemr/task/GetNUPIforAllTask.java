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

import java.util.HashSet;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.nupi.UpiUtilsDataExchange;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.PersonAttributeType;

/**
 * A scheduled task that updates ccc numbers on NUPI in case ccc was missed
 */
public class GetNUPIforAllTask extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(AutoCloseActiveVisitsTask.class);
	
	/**
	 * @see AbstractTask#execute()
	 */
	@Override
	public void execute() {
		System.out.println("GET NUPI FOR ALL PATIENTS TASK");
		if (!isExecuting) {
			if (log.isDebugEnabled()) {
				log.debug("Starting Get NUPI for all patients Task...");
				System.out.println("Starting Get NUPI for all patients Task...");
			}

			PatientService patientService = Context.getPatientService();

			startExecuting();
			List<Patient> allPatients = patientService.getAllPatients();
			System.out.println("Got all patients: " + allPatients.size());

			// Get patients on HIV program
			Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

			// NUPI
			PatientIdentifierType nationalUniquePatientIdentifier = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);

			// National ID
			PatientIdentifierType nationalID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);

			// Passport Number
			PatientIdentifierType passportNumber = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);

			// Birth Certificate
			PatientIdentifierType birthCertificateNumber = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);

			// NUPI update status attribute
			PersonAttributeType patNUPIver = MetadataUtils.possible(PersonAttributeType.class, CommonMetadata._PersonAttributeType.VERIFICATION_STATUS_WITH_NATIONAL_REGISTRY);

			// loop checking for patients without NUPI
			HashSet<Patient> patientsGroup = new HashSet<Patient>();
			for (Patient patient : allPatients) {
				if (patient != null) {
					ProgramWorkflowService pwfservice = Context.getProgramWorkflowService();
					List<PatientProgram> programs = pwfservice.getPatientPrograms(patient, hivProgram, null, null, null,null, true);
					if (programs.size() > 0) {
						if(patient.getDead() == false && patient.getPatientIdentifier(nationalUniquePatientIdentifier) == null && (patient.getPatientIdentifier(nationalID) != null || patient.getPatientIdentifier(passportNumber) != null || patient.getPatientIdentifier(birthCertificateNumber) != null)) {
							if(patient.getAttribute(patNUPIver) != null && patient.getAttribute(patNUPIver).getValue().trim().equalsIgnoreCase("Pending")) {
								patientsGroup.add(patient);
							}
						}
					}	
				}
			}
			System.out.println("NUPI to be checked: " + patientsGroup.size());

			// get NUPI for all
			UpiUtilsDataExchange upiUtils = new UpiUtilsDataExchange();
			try {
				System.out.println("Getting NUPI for all patients with identifiers and status marked as PENDING");
				Integer result = upiUtils.getNUPIforAll(patientsGroup);
				System.out.println("Finished the Get NUPI for patients update: " + result);
			} catch(Exception x) {
				System.err.println("Get NUPI for all patients Error: " + x.getMessage());
				x.printStackTrace();
			}
			
			stopExecuting();
		}
	}
}
