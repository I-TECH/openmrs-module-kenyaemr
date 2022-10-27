/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.kenyaemr.api.db.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.api.db.NUPIcccDAO;
import org.openmrs.module.kenyaemr.nupi.NUPIcccSyncRegister;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

public class HibernateNUPIcccDAO implements NUPIcccDAO {

    protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

    @Override
    public NUPIcccSyncRegister saveOrUpdateRegister(NUPIcccSyncRegister record) {
        getSession().saveOrUpdate(record);
        return record;
    }

    /**
     * Remove stale records
     * @param patient
     * @return
     */
    @Override
    public Integer purgeRecords(Patient patient) {
        String hql = "delete from kenyaemr_nupi_ccc_sync_register where patient_id = :patient_id and completed = 0";
        Integer ret = getSession().createQuery(hql).setString("patient_id", Integer.toString(patient.getPatientId())).executeUpdate();
        return(ret);
    }

    @Override
    public NUPIcccSyncRegister getPatientRecordById(Integer id) {
        return (NUPIcccSyncRegister) getSession().createCriteria(NUPIcccSyncRegister.class).add(Restrictions.eq("id", id))
		        .uniqueResult();
    }

    @Override
    public NUPIcccSyncRegister getLatestPatientRecordByPatient(Patient patient) {
        Criteria criteria = getSession().createCriteria(NUPIcccSyncRegister.class);
		criteria.add(Restrictions.eq("patient", patient));
		criteria.addOrder(Order.desc("dateUpdated"));
		criteria.setMaxResults(1);
		
		NUPIcccSyncRegister nUPIcccSyncRegister = (NUPIcccSyncRegister) criteria.uniqueResult();
		
		return nUPIcccSyncRegister;
    }

    @Override
    public Collection<Integer> getAllPatients() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient) {
        return (List<NUPIcccSyncRegister>) getSession().createCriteria(NUPIcccSyncRegister.class)
		        .add(Restrictions.eq("patient", patient)).list();
    }

    @Override
    public List<NUPIcccSyncRegister> getPatientRecordByPatient(Patient patient, Date onOrBefore, Date onOrAfter) {
        return (List<NUPIcccSyncRegister>) getSession().createCriteria(NUPIcccSyncRegister.class)
		        .add(Restrictions.eq("patient", patient)).list();
    }

    @Override
    public List<NUPIcccSyncRegister> getAllPatientRecords() {
        return (List<NUPIcccSyncRegister>) getSession().createCriteria(NUPIcccSyncRegister.class).list();
    }

    @Override
    public Date getLatestSyncDate() {
        Criteria criteria = getSession().createCriteria(NUPIcccSyncRegister.class);
		criteria.addOrder(Order.desc("dateUpdated"));
		criteria.setMaxResults(1);
		NUPIcccSyncRegister nUPIcccSyncRegister = (NUPIcccSyncRegister) criteria.uniqueResult();
		return nUPIcccSyncRegister.getDateUpdated();
    }
    
}
