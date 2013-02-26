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
package org.openmrs.module.kenyaemr;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 */
public class KenyaEmrUiUtilsTest extends BaseModuleWebContextSensitiveTest {

	private UiUtils ui;

	private RegimenOrder regimen;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-regimens.xml");
		RegimenManager.loadDefinitionsFromXML(stream);

		this.ui = new FragmentActionUiUtils(null, null, null);

		DrugOrder aspirin = new DrugOrder();
		aspirin.setConcept(Context.getConceptService().getConcept(71617));
		aspirin.setDose(100.0d);
		aspirin.setUnits("mg");
		aspirin.setFrequency("OD");
		DrugOrder stavudine = new DrugOrder();
		stavudine.setConcept(Context.getConceptService().getConcept(84309));
		stavudine.setDose(30.0d);
		stavudine.setUnits("ml");
		stavudine.setFrequency("BD");

		regimen = new RegimenOrder();
		regimen.addDrugOrder(aspirin);
		regimen.addDrugOrder(stavudine);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatDate(java.util.Date)
	 * @verifies format date as a string without time information
	 */
	@Test
	public void formatDate_shouldFormatDateWithoutTime() throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 1981);
		cal.set(Calendar.MONTH, Calendar.MAY);
		cal.set(Calendar.DAY_OF_MONTH, 28);
		cal.set(Calendar.HOUR, 7);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 12);

		Assert.assertEquals("28-May-1981", KenyaEmrUiUtils.formatDate(cal.getTime()));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatDate(java.util.Date)
	 * @verifies format null date as empty string
	 */
	@Test
	public void formatDate_shouldFormatNullDateAsEmptyString() throws Exception {
		Assert.assertEquals("", KenyaEmrUiUtils.formatDate(null));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatInterval(java.util.Date)
	 * @verifies return non-empty string
	 */
	@Test
	public void formatMillis_shouldReturnNonEmptyString() throws Exception {
		Assert.assertTrue(StringUtils.isNotEmpty(KenyaEmrUiUtils.formatInterval(new Date())));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatDrug(org.openmrs.module.kenyaemr.regimen.DrugReference, org.openmrs.ui.framework.UiUtils)
	 * @verifies format drug reference as concept or drug
	 */
	@Test
	public void formatDrug_shouldFormatDrugReferenceAsConceptOrDrug() throws Exception {
		// Test concept only reference
		DrugReference drugRef1 = DrugReference.fromConceptUuid("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");	// STAVUDINE
		Assert.assertNotNull(KenyaEmrUiUtils.formatDrug(drugRef1, ui));

		// Test concept only reference
		DrugReference drugRef2 = DrugReference.fromDrugUuid("a74fefc6-931d-47b9-b282-5d6c8c8d8060"); // Rifampicin (100mg)
		Assert.assertNotNull(KenyaEmrUiUtils.formatDrug(drugRef2, ui));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatRegimenShort(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 * @verifies format empty list as empty string
	 */
	@Test
	public void formatRegimenShort_shouldFormatEmptyListAsEmpty() throws Exception {
		Assert.assertEquals("Empty", KenyaEmrUiUtils.formatRegimenShort(new RegimenOrder(), ui));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatRegimenShort(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 * @verifies format regimen
	 */
	@Test
	public void formatRegimenShort_shouldFormatRegimen() throws Exception {
		Assert.assertNotNull(KenyaEmrUiUtils.formatRegimenShort(regimen, ui));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#simpleRegimen(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimen_shouldConvertToSimpleObject() {
		// Check null regimen
		SimpleObject obj1 = KenyaEmrUiUtils.simpleRegimen(null, ui);
		Assert.assertEquals("None", obj1.get("shortDisplay"));
		Assert.assertEquals("None", obj1.get("longDisplay"));

 		// Check empty regimen
		SimpleObject obj2 = KenyaEmrUiUtils.simpleRegimen(new RegimenOrder(), ui);
		Assert.assertEquals("Empty", obj2.get("shortDisplay"));
		Assert.assertEquals("Empty", obj2.get("longDisplay"));

		// Check normal regimen
		SimpleObject obj3 = KenyaEmrUiUtils.simpleRegimen(regimen, ui);
		Assert.assertNotNull(obj3.get("shortDisplay"));
		Assert.assertNotNull(obj3.get("longDisplay"));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#simpleRegimenHistory(org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenHistory_shouldConvertToSimpleObjects() throws IOException, SAXException, ParserConfigurationException {
		Concept medset = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);

		// Check empty history
		RegimenChangeHistory emptyHistory = RegimenChangeHistory.forPatient(Context.getPatientService().getPatient(6), medset);
		List<SimpleObject> objs = KenyaEmrUiUtils.simpleRegimenHistory(emptyHistory, ui);

		Assert.assertEquals(0, objs.size());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#simpleRegimenDefinitions(java.util.Collection, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenDefinitions_shouldConvertToSimpleObjects() throws IOException, SAXException, ParserConfigurationException {
		List<SimpleObject> objs = KenyaEmrUiUtils.simpleRegimenDefinitions(RegimenManager.getRegimenGroups("category1").get(0).getRegimens(), ui);

		Assert.assertEquals("regimen1", objs.get(0).get("name"));
		Assert.assertEquals("group1", ((Map<String, Object>)objs.get(0).get("group")).get("code"));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#isRetrospectiveVisit(org.openmrs.Visit)
	 */
	@Test
	public void isRetrospectiveVisit() {
		Date date1 = TestUtils.date(2011, 1, 1, 10, 0, 0); // Jan 1st, 10:00am
		Date date2 = TestUtils.date(2011, 1, 1, 11, 0, 0); // Jan 1st, 11:00am

		Visit visit1 = new Visit();
		visit1.setStartDatetime(date1);
		visit1.setStopDatetime(date2);

		Assert.assertFalse(KenyaEmrUiUtils.isRetrospectiveVisit(visit1));

		Visit visit2 = new Visit();
		visit2.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit2.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(date1));

		Assert.assertTrue(KenyaEmrUiUtils.isRetrospectiveVisit(visit2));

		// Check case when stop date has been persisted and lost its milliseconds
		Calendar stopFromSql = Calendar.getInstance();
		stopFromSql.setTime(OpenmrsUtil.getLastMomentOfDay(date1));
		stopFromSql.set(Calendar.MILLISECOND, 0);

		Visit visit3 = new Visit();
		visit3.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit3.setStopDatetime(stopFromSql.getTime());

		Assert.assertTrue(KenyaEmrUiUtils.isRetrospectiveVisit(visit3));
	}
}