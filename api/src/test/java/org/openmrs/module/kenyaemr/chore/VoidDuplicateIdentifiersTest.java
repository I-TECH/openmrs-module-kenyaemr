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

package org.openmrs.module.kenyaemr.chore;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.validator.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.sql.PreparedStatement;

/**
 * Tests for {@link VoidDuplicateIdentifiers}
 */
public class VoidDuplicateIdentifiersTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private VoidDuplicateIdentifiers chore;

	/**
	 * @see FixMissingOpenmrsIdentifiers#perform(java.io.PrintWriter)
	 */
	@Test
	public void perform() throws Exception {
		// Update OpenMRS ID of patient #8 to be duplicate of that of patient #7
		executeSql("UPDATE patient_identifier SET identifier = '6TS-4' WHERE patient_identifier_id = 5;");

		// Need to give patient #8 a preferred identifier
		executeSql("UPDATE patient_identifier SET preferred = 1, voided = 0 WHERE patient_identifier_id = 6;");

		chore.perform(new PrintWriter(System.out));

		// Identifier validation uses SQL to we need to flush Hibernate level changes first
		Context.flushSession();

		ValidateUtil.validate(TestUtils.getPatient(7));
		ValidateUtil.validate(TestUtils.getPatient(8));
	}

	/**
	 * Helper method to execute SQL on the test database
	 * @param sql the SQL statement
	 */
	private void executeSql(String sql) throws Exception {
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.execute();
		ps.close();

		Context.clearSession();
	}
}