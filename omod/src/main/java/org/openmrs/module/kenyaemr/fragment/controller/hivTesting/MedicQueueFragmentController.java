/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.hivTesting;

import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.PrivilegeConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * medic error queue fragment
 */
public class MedicQueueFragmentController {

	public void controller(FragmentModel model) {

		Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		DbSessionFactory sf = Context.getRegisteredComponents(DbSessionFactory.class).get(0);

		String regStr = "select count(*) from medic_error_data where discriminator='json-registration';";
		String allErrors = "select count(*) from medic_error_data;";
		String queueData = "select count(*) from medic_queue_data;";
		Long totalErrors = (Long) Context.getAdministrationService().executeSQL(allErrors, true).get(0).get(0);
		Long registrationErrors = (Long) Context.getAdministrationService().executeSQL(regStr, true).get(0).get(0);
		Long queueDataTotal = (Long) Context.getAdministrationService().executeSQL(queueData, true).get(0).get(0);

		final String sqlSelectQuery = "SELECT id,message,date_created FROM medic_error_message order by date_created desc limit 5;";
		final List<SimpleObject> ret = new ArrayList<SimpleObject>();
		Transaction tx = null;
		try {

			tx = sf.getHibernateSessionFactory().getCurrentSession().beginTransaction();
			final Transaction finalTx = tx;
			sf.getCurrentSession().doWork(new Work() {

				@Override
				public void execute(Connection connection) throws SQLException {
					PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
					try {

						ResultSet resultSet = statement.executeQuery();
						if (resultSet != null) {
							ResultSetMetaData metaData = resultSet.getMetaData();

							while (resultSet.next()) {
								Object[] row = new Object[metaData.getColumnCount()];
								for (int i = 1; i <= metaData.getColumnCount(); i++) {
									row[i - 1] = resultSet.getObject(i);
								}
								ret.add(SimpleObject.create(
										"id", row[0] != null ? row[0].toString() : "",
										"message", row[1] != null ? row[1].toString() : "",
										"date_created", row[2] != null ? row[2].toString() : ""
								));
							}
						}
						finalTx.commit();
					}
					finally {
						try {
							if (statement != null) {
								statement.close();
							}
						}
						catch (Exception e) {}
					}
				}
			});
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to execute query", e);
		}

		model.put("logs", ret);
		model.put("totalErrors", totalErrors.intValue());
		model.put("registrationErrors", registrationErrors.intValue());
		model.put("queueData", queueDataTotal.intValue());
		Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);

	}

	public List<SimpleObject> errorMessages(UiUtils ui) {

		DbSessionFactory sf = Context.getRegisteredComponents(DbSessionFactory.class).get(0);

		final String sqlSelectQuery = "SELECT id,message,date_created FROM medic_error_message order by date_created desc limit 5;";
		final List<SimpleObject> ret = new ArrayList<SimpleObject>();
		Transaction tx = null;
		try {

			tx = sf.getHibernateSessionFactory().getCurrentSession().beginTransaction();
			final Transaction finalTx = tx;
			sf.getCurrentSession().doWork(new Work() {

				@Override
				public void execute(Connection connection) throws SQLException {
					PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
					try {

						ResultSet resultSet = statement.executeQuery();
						if (resultSet != null) {
							ResultSetMetaData metaData = resultSet.getMetaData();

							while (resultSet.next()) {
								Object[] row = new Object[metaData.getColumnCount()];
								for (int i = 1; i <= metaData.getColumnCount(); i++) {
									row[i - 1] = resultSet.getObject(i);
								}
								ret.add(SimpleObject.create(
										"id", row[0] != null ? row[0].toString() : "",
										"message", row[1] != null ? row[1].toString() : "",
										"date_created", row[2] != null ? row[2].toString() : ""
								));
							}
						}
						finalTx.commit();
					}
					finally {
						try {
							if (statement != null) {
								statement.close();
							}
						}
						catch (Exception e) {}
					}
				}
			});
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to execute query", e);
		}
		return ret;
	}
	public List<SimpleObject> refreshTables(UiUtils ui) {

		DbSessionFactory sf = Context.getRegisteredComponents(DbSessionFactory.class).get(0);

		final String sqlSelectQuery = "SELECT id,message,date_created FROM medic_error_message order by date_created desc limit 5;";
		final List<SimpleObject> ret = new ArrayList<SimpleObject>();
		Transaction tx = null;
		try {

			tx = sf.getHibernateSessionFactory().getCurrentSession().beginTransaction();
			final Transaction finalTx = tx;
			sf.getCurrentSession().doWork(new Work() {

				@Override
				public void execute(Connection connection) throws SQLException {
					PreparedStatement statement = connection.prepareStatement(sqlSelectQuery);
					try {

						ResultSet resultSet = statement.executeQuery();
						if (resultSet != null) {
							ResultSetMetaData metaData = resultSet.getMetaData();

							while (resultSet.next()) {
								Object[] row = new Object[metaData.getColumnCount()];
								for (int i = 1; i <= metaData.getColumnCount(); i++) {
									row[i - 1] = resultSet.getObject(i);
								}
								ret.add(SimpleObject.create(
										"id", row[0] != null ? row[0].toString() : "",
										"message", row[1] != null ? row[1].toString() : "",
										"date_created", row[2] != null ? row[2].toString() : ""
								));
							}
						}
						finalTx.commit();
					}
					finally {
						try {
							if (statement != null) {
								statement.close();
							}
						}
						catch (Exception e) {}
					}
				}
			});
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to execute query", e);
		}
		return ret;
	}
}
