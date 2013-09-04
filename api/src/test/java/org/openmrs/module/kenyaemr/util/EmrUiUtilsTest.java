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

package org.openmrs.module.kenyaemr.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.util.EmrUiUtils}
 */
public class EmrUiUtilsTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private EmrUiUtils kenyaUi;

	@Autowired
	private RegimenManager regimenManager;

	private UiUtils ui;

	private RegimenOrder regimen;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-regimens.xml");
		regimenManager.loadDefinitionsFromXML(stream);

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

		regimen = new RegimenOrder(new LinkedHashSet<DrugOrder>(Arrays.asList(aspirin, stavudine)));
	}

	/**
	 * @see EmrUiUtils#formatVisitDates(org.openmrs.Visit)
	 */
	@Test
	public void formatVisitDates() {
		// Check a retrospective visit
		Visit visit = new Visit();
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(TestUtils.date(2011, 1, 1)));
		visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(TestUtils.date(2011, 1, 1)));
		Assert.assertEquals("01-Jan-2011", kenyaUi.formatVisitDates(visit));

		// Check a regular visit on single day
		visit.setStartDatetime(TestUtils.date(2011, 1, 1, 10, 0, 0));
		visit.setStopDatetime(TestUtils.date(2011, 1, 1, 11, 0, 0));
		Assert.assertEquals("01-Jan-2011 10:00 \u2192 11:00", kenyaUi.formatVisitDates(visit));

		// Check a regular visit spanning multiple days
		visit.setStartDatetime(TestUtils.date(2011, 1, 1, 10, 0, 0));
		visit.setStopDatetime(TestUtils.date(2011, 1, 2, 11, 0, 0));
		Assert.assertEquals("01-Jan-2011 10:00 \u2192 02-Jan-2011 11:00", kenyaUi.formatVisitDates(visit));

		// Check a visit with no end
		visit.setStartDatetime(TestUtils.date(2011, 1, 1, 10, 0, 0));
		visit.setStopDatetime(null);
		Assert.assertEquals("01-Jan-2011 10:00", kenyaUi.formatVisitDates(visit));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.EmrUiUtils#formatDrug(org.openmrs.module.kenyaemr.regimen.DrugReference, org.openmrs.ui.framework.UiUtils)
	 * @verifies format drug reference as concept or drug
	 */
	@Test
	public void formatDrug_shouldFormatDrugReferenceAsConceptOrDrug() throws Exception {
		// Test concept only reference
		DrugReference drugRef1 = DrugReference.fromConceptUuid("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");	// STAVUDINE
		Assert.assertNotNull(kenyaUi.formatDrug(drugRef1, ui));

		// Test concept only reference
		DrugReference drugRef2 = DrugReference.fromDrugUuid("a74fefc6-931d-47b9-b282-5d6c8c8d8060"); // Rifampicin (100mg)
		Assert.assertNotNull(kenyaUi.formatDrug(drugRef2, ui));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.EmrUiUtils#formatRegimenShort(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 * @verifies format regimen
	 */
	@Test
	public void formatRegimenShort_shouldFormatRegimen() throws Exception {
		// Check empty regimen
		RegimenOrder empty = new RegimenOrder(new HashSet<DrugOrder>());
		Assert.assertEquals("Empty", kenyaUi.formatRegimenShort(empty, ui));

		// Check regular regimen
		Assert.assertEquals("ASPIRIN, STAVUDINE", kenyaUi.formatRegimenShort(regimen, ui));
	}

	/**
	 * @see EmrUiUtils#formatRegimenLong(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void formatRegimenLong() {
		// Check empty regimen
		RegimenOrder empty = new RegimenOrder(new HashSet<DrugOrder>());
		Assert.assertEquals("Empty", kenyaUi.formatRegimenLong(empty, ui));

		// Check regular regimen
		Assert.assertEquals("ASPIRIN 100mg OD + STAVUDINE 30ml BD", kenyaUi.formatRegimenLong(regimen, ui));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.EmrUiUtils#simpleRegimen(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimen_shouldConvertToSimpleObject() {
		// Check null regimen
		SimpleObject obj1 = kenyaUi.simpleRegimen(null, ui);
		Assert.assertEquals("None", obj1.get("shortDisplay"));
		Assert.assertEquals("None", obj1.get("longDisplay"));

		// Check normal regimen
		SimpleObject obj3 = kenyaUi.simpleRegimen(regimen, ui);
		Assert.assertNotNull(obj3.get("shortDisplay"));
		Assert.assertNotNull(obj3.get("longDisplay"));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.EmrUiUtils#simpleRegimenHistory(org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenHistory_shouldConvertToSimpleObjects() throws IOException, SAXException, ParserConfigurationException {
		Concept medset = org.openmrs.module.kenyaemr.Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);

		// Check empty history
		RegimenChangeHistory emptyHistory = RegimenChangeHistory.forPatient(Context.getPatientService().getPatient(6), medset);
		List<SimpleObject> objs = kenyaUi.simpleRegimenHistory(emptyHistory, ui);

		Assert.assertEquals(0, objs.size());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.EmrUiUtils#simpleRegimenDefinitions(java.util.Collection, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenDefinitions_shouldConvertToSimpleObjects() throws IOException, SAXException, ParserConfigurationException {
		List<SimpleObject> objs = kenyaUi.simpleRegimenDefinitions(regimenManager.getRegimenGroups("category1").get(0).getRegimens(), ui);

		Assert.assertEquals("regimen1", objs.get(0).get("name"));
		Assert.assertEquals("group1", ((Map<String, Object>)objs.get(0).get("group")).get("code"));
	}
}