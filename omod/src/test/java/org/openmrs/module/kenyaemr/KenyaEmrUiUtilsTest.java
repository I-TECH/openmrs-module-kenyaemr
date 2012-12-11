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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class KenyaEmrUiUtilsTest extends BaseModuleWebContextSensitiveTest {

	private UiUtils ui;

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");

		this.ui = new FragmentActionUiUtils(null, null, null);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatDateNoTime(java.util.Date)
	 * @verifies format date as a string without time information
	 */
	@Test
	public void formatDateNoTime_shouldFormatDateWithoutTime() throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 1981);
		cal.set(Calendar.MONTH, Calendar.MAY);
		cal.set(Calendar.DAY_OF_MONTH, 28);
		cal.set(Calendar.HOUR, 7);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 12);

		Assert.assertEquals("28-May-1981", KenyaEmrUiUtils.formatDateNoTime(cal.getTime()));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatDateNoTime(java.util.Date)
	 * @verifies format null date as empty string
	 */
	@Test
	public void formatDateNoTime_shouldFormatNullDateAsEmptyString() throws Exception {
		Assert.assertEquals("", KenyaEmrUiUtils.formatDateNoTime(null));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatRegimen(java.util.List)
	 * @verifies format empty list as empty string
	 */
	@Test
	public void formatRegimen_shouldFormatEmptyListAsEmptyString() throws Exception {
		Assert.assertEquals("", KenyaEmrUiUtils.formatRegimen(new ArrayList<DrugOrder>()));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#formatRegimen(java.util.List)
	 * @verifies format regimen
	 */
	@Test
	public void formatRegimen_shouldFormatDrugOrdersAsRegimen() throws Exception {
		DrugOrder aspirin = new DrugOrder();
		aspirin.setConcept(Context.getConceptService().getConcept(71617));
		DrugOrder stavudine = new DrugOrder();
		stavudine.setConcept(Context.getConceptService().getConcept(84309));
		List<DrugOrder> regimen = Arrays.asList(aspirin, stavudine);

		Assert.assertEquals("ASPIRIN + STAVUDINE", KenyaEmrUiUtils.formatRegimen(regimen));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmrUiUtils#simpleRegimenDefinitions(java.util.Collection, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void simpleRegimenDefinitions_shouldConvertToSimpleObjects() throws IOException, SAXException, ParserConfigurationException {

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-regimens.xml");
		RegimenManager.loadDefinitionsFromXML(stream);

		List<SimpleObject> objs = KenyaEmrUiUtils.simpleRegimenDefinitions(RegimenManager.getRegimenDefinitions("ARV"), ui);
		Assert.assertEquals("regimen1", objs.get(0).get("name"));
		Assert.assertEquals("group1", objs.get(0).get("group"));
	}
}