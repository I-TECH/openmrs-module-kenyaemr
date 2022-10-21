/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.kenyaemr.api.db;

import java.util.Date;
import java.util.List;
import java.util.Collection;

import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.nupi.NUPIcccSyncRegister;

public interface NUPIcccDAO {
    /**
	 * Saves or updates record
	 * 
	 * @param riskScore
	 * @return
	 */
	public NUPIcccSyncRegister saveOrUpdateRegister(NUPIcccSyncRegister record);
	
	/**
	 * Returns a record for a given id
	 * 
	 * @param id
	 * @return
	 */
	public NUPIcccSyncRegister getPatientRecordById(Integer id);
	
	/**
	 * Gets the latest record for a patient
	 * 
	 * @param patient
	 * @return
	 */
	public NUPIcccSyncRegister getLatestPatientRecordByPatient(Patient patient);

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
	public List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient);
	
	/**
	 * Gets a list of records for a patient
	 * 
	 * @param patient
	 * @return
	 */
	public List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient, Date onOrBefore, Date onOrAfter);
	
	/**
	 * Gets a list of records
	 * 
	 * @return
	 */
	public List<NUPIcccSyncRegister> getAllPatientRecords();

    /**
     * Gets the latest sync date
     * @return
     */
    Date getLatestSyncDate();
}
