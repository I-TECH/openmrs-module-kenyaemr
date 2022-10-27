/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.kenyaemr.api.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.kenyaemr.api.NUPIcccService;
import org.openmrs.module.kenyaemr.api.db.hibernate.HibernateNUPIcccDAO;
import org.openmrs.module.kenyaemr.nupi.NUPIcccSyncRegister;

public class NUPIcccServiceImpl extends BaseOpenmrsService implements NUPIcccService {

    HibernateNUPIcccDAO nUPIcccDao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setNUPIcccDao(HibernateNUPIcccDAO nUPIcccDao) {
		this.nUPIcccDao = nUPIcccDao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

    @Override
    public NUPIcccSyncRegister saveOrUpdateRegister(NUPIcccSyncRegister record) {
        return nUPIcccDao.saveOrUpdateRegister(record);
    }

    /**
     * Remove stale records
     * @param patient
     * @return
     */
    @Override
    public Integer purgeRecords(Patient patient) {
        return nUPIcccDao.purgeRecords(patient);
    }

    @Override
    public NUPIcccSyncRegister getPatientRecordById(Integer id) {
        return nUPIcccDao.getPatientRecordById(id);
    }

    @Override
    public NUPIcccSyncRegister getLatestPatientRecordByPatient(Patient patient) {
        return nUPIcccDao.getLatestPatientRecordByPatient(patient);
    }

    @Override
    public Collection<Integer> getAllPatients() {
        return nUPIcccDao.getAllPatients();
    }

    @Override
    public List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient) {
        return nUPIcccDao.getPatientRecordByPatient(patient);
    }

    @Override
    public List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient, Date onOrBefore, Date onOrAfter) {
        return nUPIcccDao.getPatientRecordByPatient(patient, onOrBefore, onOrAfter);
    }

    @Override
    public List<NUPIcccSyncRegister> getAllPatientRecords() {
        return nUPIcccDao.getAllPatientRecords();
    }

    @Override
    public Date getLatestSyncDate() {
        return nUPIcccDao.getLatestSyncDate();
    }
    
}
