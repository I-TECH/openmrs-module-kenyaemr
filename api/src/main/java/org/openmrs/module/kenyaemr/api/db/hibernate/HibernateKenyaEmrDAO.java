/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.api.db.hibernate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.db.hibernate.AttributeMatcherPredicate;
import org.openmrs.attribute.AttributeType;
import org.openmrs.module.kenyaemr.api.db.KenyaEmrDAO;

import java.util.List;
import java.util.Map;

/**
 * Hibernate specific data access functions. This class should not be used directly.
 */
public class HibernateKenyaEmrDAO implements KenyaEmrDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * @see KenyaEmrDAO#getLocations(String, org.openmrs.Location, java.util.Map, boolean, Integer, Integer)
	 *
	 * NEEDS MOVED INTO HibernateLocationDAO
	 */
	@Override
	public List<Location> getLocations(String nameFragment, Location parent, Map<LocationAttributeType, String> serializedAttributeValues, boolean includeRetired, Integer start, Integer length) {

		Criteria criteria = getCurrentSession().createCriteria(Location.class);

		if (StringUtils.isNotBlank(nameFragment)) {
			criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.START));
		}

		if (parent != null) {
			criteria.add(Restrictions.eq("parentLocation", parent));
		}

		if (serializedAttributeValues != null) {
			addAttributeCriteria(criteria, serializedAttributeValues);
		}

		if (!includeRetired)
			criteria.add(Restrictions.eq("retired", false));

		criteria.addOrder(Order.asc("name"));
		if (start != null)
			criteria.setFirstResult(start);
		if (length != null && length > 0)
			criteria.setMaxResults(length);

		return criteria.list();
	}

	/**
	 * Adds attribute value criteria to the given criteria query
	 *
	 * NEEDS MOVED INTO OpenMRS Core
	 *
	 * @param criteria the criteria
	 * @param serializedAttributeValues the serialized attribute values
	 * @param <AT> the attribute type
	 */
	public static <AT extends AttributeType> void addAttributeCriteria(Criteria criteria, Map<AT, String> serializedAttributeValues) {
		for (Map.Entry<AT, String> entry : serializedAttributeValues.entrySet()) {
			criteria.createCriteria("attributes")
					.add(Restrictions.eq("attributeType", entry.getKey()))
					.add(Restrictions.eq("valueReference", entry.getValue()))
					.add(Restrictions.eq("voided", false));
		}
	}
}