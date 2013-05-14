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

package org.openmrs.module.kenyaemr.form.handler;

import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import javax.servlet.http.HttpSession;

import java.util.Date;

import static org.mockito.Mockito.mock;

/**
 * Tests for {@link IfModeTagHandler}
 */
public class IfModeTagHandlerTest extends BaseModuleContextSensitiveTest {

	@Test
	public void integration() throws Exception {

		String xml =
			"<htmlform>" +
				"<ifMode mode=\"VIEW\">" +
				"  <obs id=\"obs1\" conceptId=\"5497\" />" +
				"</ifMode>" +
				"<ifMode mode=\"ENTER\">" +
				"  <obs id=\"obs1\" conceptId=\"5497\" />" +
				"</ifMode>" +
				"<ifMode mode=\"EDIT\">" +
				"  <obs id=\"obs1\" conceptId=\"5497\" />" +
				"</ifMode>" +
			"</htmlform>";

		Patient patient = Context.getPatientService().getPatient(6);
		HtmlForm hf = new HtmlForm();
		hf.setForm(Context.getFormService().getForm(1));
		hf.setDateCreated(new Date());
		hf.setXmlData(xml);

		HttpSession httpSession = mock(HttpSession.class);

		FormEntrySession fes = new FormEntrySession(patient, hf, FormEntryContext.Mode.ENTER, httpSession);

		//System.out.println(fes.getHtmlToDisplay());
		//System.out.println("----------------");
		//System.out.println(fes.getFieldAccessorJavascript());
	}
}