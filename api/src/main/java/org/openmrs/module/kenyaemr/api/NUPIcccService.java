/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.api;

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.kenyaemr.nupi.NUPIcccSyncRegister;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public interface NUPIcccService extends OpenmrsService {
		
	/**
	 * Saves or updates record
	 * 
	 * @param riskScore
	 * @return
	 */
	//@Authorized(KenyaEMRConfig.MODULE_PRIVILEGE)
	@Transactional
	NUPIcccSyncRegister saveOrUpdateRegister(NUPIcccSyncRegister record);	
	
	/**
	 * Returns a NUPIcccSyncRegister for a given id
	 * 
	 * @param id
	 * @return
	 */
	NUPIcccSyncRegister getPatientRecordById(Integer id);
	
	/**
	 * Gets the latest NUPIcccSyncRegister for a patient
	 * 
	 * @param patient
	 * @return
	 */
	NUPIcccSyncRegister getLatestPatientRecordByPatient(Patient patient);

	/**
	 * Get all patients
	 * @return a list of patients
	 */
	public Collection<Integer> getAllPatients();
	
	/**
	 * Gets a list of records for a patient
	 * 
	 * @param patient
	 * @return
	 */
	List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient);
	
	/**
	 * Gets a list of records for a patient
	 * 
	 * @param patient
	 * @return
	 */
	List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient, Date onOrBefore, Date onOrAfter);
	
	/**
	 * Gets a list of records for a patient
	 * 
	 * @return
	 */
	List<NUPIcccSyncRegister> getAllPatientRecords();

	/**
	 * Get latest sync date
	 * @return
	 */
	Date getLatestSyncDate();

}
