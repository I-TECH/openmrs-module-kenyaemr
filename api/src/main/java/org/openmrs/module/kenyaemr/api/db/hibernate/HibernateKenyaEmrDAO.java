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

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.module.kenyaemr.api.db.KenyaEmrDAO;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Hibernate specific data access functions. This class should not be used directly.
 */
public class HibernateKenyaEmrDAO implements KenyaEmrDAO {

	private SessionFactory sessionFactory;

	/**
	 * Sets the session factory
	 * @param sessionFactory the session factory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Convenience method to get current session
	 * @return the session
	 */
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public List<Object> executeSqlQuery(String query, Map<String, Object> substitutions) {
		SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery(query);

		for (Map.Entry<String, Object> e : substitutions.entrySet()) {
			if (e.getValue() instanceof Collection) {
				q.setParameterList(e.getKey(), (Collection) e.getValue());
			} else if (e.getValue() instanceof Object[]) {
				q.setParameterList(e.getKey(), (Object[]) e.getValue());
			} else if (e.getValue() instanceof Cohort) {
				q.setParameterList(e.getKey(), ((Cohort) e.getValue()).getMemberIds());
			} else if (e.getValue() instanceof Date) {
				q.setDate(e.getKey(), (Date) e.getValue());
			} else {
				q.setParameter(e.getKey(), e.getValue());
			}


		}

		q.setReadOnly(true);

		List<Object> r = q.list();
		return r;
	}

	@Override
	public List<Object> executeHqlQuery(String query, Map<String, Object> substitutions) {
		Query q = sessionFactory.getCurrentSession().createQuery(query);

		applySubstitutions(q, substitutions);

		// optimizations go here
		q.setReadOnly(true);

		return q.list();
	}

	private void applySubstitutions(Query q, Map<String, Object> substitutions) {
		for (Map.Entry<String, Object> e : substitutions.entrySet()) {
			if (e.getValue() instanceof Collection) {
				q.setParameterList(e.getKey(), (Collection) e.getValue());
			} else if (e.getValue() instanceof Object[]) {
				q.setParameterList(e.getKey(), (Object[]) e.getValue());
			} else if (e.getValue() instanceof Cohort) {
				q.setParameterList(e.getKey(), ((Cohort) e.getValue()).getMemberIds());
			} else if (e.getValue() instanceof Date) {
				q.setDate(e.getKey(), (Date) e.getValue());
			} else {
				q.setParameter(e.getKey(), e.getValue());
			}
		}
	}

}