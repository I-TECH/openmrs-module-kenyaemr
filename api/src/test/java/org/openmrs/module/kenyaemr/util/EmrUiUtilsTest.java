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
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EmrUiUtils}
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
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-regimens.xml");
		regimenManager.loadDefinitionsFromXML(stream);

		this.ui = new FragmentActionUiUtils(null, null, null);

		DrugOrder dapsone = new DrugOrder();
		dapsone.setConcept(Dictionary.getConcept(Dictionary.DAPSONE));
		dapsone.setDose(100.0d);
		dapsone.setUnits("mg");
		dapsone.setFrequency("OD");
		DrugOrder stavudine = new DrugOrder();
		stavudine.setConcept(Context.getConceptService().getConcept(84309));
		stavudine.setDose(30.0d);
		stavudine.setUnits("ml");
		stavudine.setFrequency("BD");

		regimen = new RegimenOrder(new LinkedHashSet<DrugOrder>(Arrays.asList(dapsone, stavudine)));
	}

	/**
	 * @see EmrUiUtils#formatDrug(org.openmrs.module.kenyaemr.regimen.DrugReference, org.openmrs.ui.framework.UiUtils)
	 * @verifies format drug reference as concept or drug
	 */
	@Test
	public void formatDrug_shouldFormatDrugReferenceAsConceptOrDrug() throws Exception {
		// Test concept only reference
		DrugReference drugRef1 = DrugReference.fromConceptUuid("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");	// STAVUDINE
		Assert.assertThat(kenyaUi.formatDrug(drugRef1, ui), is(notNullValue()));

		// Test concept only reference
		DrugReference drugRef2 = DrugReference.fromDrugUuid("a74fefc6-931d-47b9-b282-5d6c8c8d8060"); // Rifampicin (100mg)
		Assert.assertThat(kenyaUi.formatDrug(drugRef2, ui), is(notNullValue()));
	}

	/**
	 * @see EmrUiUtils#formatRegimenShort(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 * @verifies format regimen
	 */
	@Test
	public void formatRegimenShort_shouldFormatRegimen() throws Exception {
		// Check empty regimen
		RegimenOrder empty = new RegimenOrder(new HashSet<DrugOrder>());
		Assert.assertThat(kenyaUi.formatRegimenShort(empty, ui), is("Empty"));

		// Check regular regimen
		Assert.assertThat(kenyaUi.formatRegimenShort(regimen, ui), is("DAPSONE, STAVUDINE"));
	}

	/**
	 * @see EmrUiUtils#formatRegimenLong(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void formatRegimenLong() {
		// Check empty regimen
		RegimenOrder empty = new RegimenOrder(new HashSet<DrugOrder>());
		Assert.assertThat(kenyaUi.formatRegimenLong(empty, ui), is("Empty"));

		// Check regular regimen
		Assert.assertThat(kenyaUi.formatRegimenLong(regimen, ui), is("DAPSONE 100mg OD + D4T 30ml BD"));
	}

	/**
	 * @see EmrUiUtils#simpleRegimen(org.openmrs.module.kenyaemr.regimen.RegimenOrder, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimen_shouldConvertToSimpleObject() {
		// Check null regimen
		SimpleObject obj1 = kenyaUi.simpleRegimen(null, ui);
		Assert.assertThat(obj1.get("shortDisplay"), is((Object) "None"));
		Assert.assertThat(obj1.get("longDisplay"), is((Object) "None"));

		// Check normal regimen
		SimpleObject obj3 = kenyaUi.simpleRegimen(regimen, ui);
		Assert.assertThat(obj3.get("shortDisplay"), is(notNullValue()));
		Assert.assertThat(obj3.get("longDisplay"), is(notNullValue()));
	}

	/**
	 * @see EmrUiUtils#simpleRegimenHistory(org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenHistory_shouldConvertEmptyHistory() throws IOException, SAXException, ParserConfigurationException {
		Concept medset = org.openmrs.module.kenyaemr.Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);

		// Check empty history
		RegimenChangeHistory emptyHistory = RegimenChangeHistory.forPatient(Context.getPatientService().getPatient(6), medset);
		List<SimpleObject> objs = kenyaUi.simpleRegimenHistory(emptyHistory, ui);

		Assert.assertThat(objs, hasSize(0));
	}

	/**
	 * @see EmrUiUtils#simpleRegimenHistory(org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenHistory_shouldConvertNonEmptyHistory() throws IOException, SAXException, ParserConfigurationException {
		Concept medset = org.openmrs.module.kenyaemr.Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);

		final Date t0 = TestUtils.date(2006, 1, 1);
		final Date t1 = TestUtils.date(2006, 2, 1);
		final Date t2 = TestUtils.date(2006, 3, 1);
		final Date t3 = TestUtils.date(2006, 4, 1);

		Concept drug1 = Dictionary.getConcept("78643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // 3TC
		Concept drug2 = Dictionary.getConcept("86663AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // AZT
		Concept drug3 = Dictionary.getConcept("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // D4T

		DrugOrder order1 = TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug1, t0, t2);
		order1.setDiscontinuedReasonNonCoded("Testing");

		DrugOrder order2 = TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug2, t1, t3);
		order2.setDiscontinuedReason(Dictionary.getConcept(Dictionary.DIED));

		TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug3, t2, null);

		RegimenChangeHistory emptyHistory = RegimenChangeHistory.forPatient(Context.getPatientService().getPatient(6), medset);
		List<SimpleObject> objs = kenyaUi.simpleRegimenHistory(emptyHistory, ui);

		Assert.assertThat(objs, hasSize(4));
		Assert.assertThat(objs.get(0), hasEntry("startDate", (Object) "01-Jan-2006"));
		Assert.assertThat((List<String>) objs.get(1).get("changeReasons"), contains("Testing"));
	}

	/**
	 * @see EmrUiUtils#simpleRegimenDefinitions(java.util.Collection, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenDefinitions_shouldConvertToSimpleObjects() throws IOException, SAXException, ParserConfigurationException {
		List<SimpleObject> objs = kenyaUi.simpleRegimenDefinitions(regimenManager.getRegimenGroups("category1").get(0).getRegimens(), ui);

		Assert.assertThat(objs.get(0), hasEntry("name", (Object) "regimen1"));
		Assert.assertThat(((Map<String, Object>)objs.get(0).get("group")), hasEntry("code", (Object) "group1"));
	}
}