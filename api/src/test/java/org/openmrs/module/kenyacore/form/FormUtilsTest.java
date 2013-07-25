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

package org.openmrs.module.kenyacore.form;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.kenyacore.UIResource;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link FormUtils}
 */
public class FormUtilsTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see org.openmrs.module.kenyacore.form.FormUtils#getFormXmlResource(org.openmrs.Form)
	 */
	@Test
	public void getFormXmlPath_shouldReturnXmlPathIfExists() {
		Form form = Context.getFormService().getForm(1);
		Assert.assertNull(FormUtils.getFormXmlResource(form));

		FormResource resource = new FormResource();
		resource.setForm(form);
		resource.setName(FormUtils.RESOURCE_HFE_XML_PATH);
		resource.setDatatypeClassname(FreeTextDatatype.class.getName());
		resource.setValue("kenyacore:test1.xml");
		Context.getFormService().saveFormResource(resource);

		Assert.assertEquals(new UIResource("kenyacore", "test1.xml"), FormUtils.getFormXmlResource(form));
	}

	@Test
	public void setFormXmlPath_shouldSetPathAsFormResource() {
		Form form = Context.getFormService().getForm(1);
		FormUtils.setFormXmlResource(form, new UIResource("kenyacore", "test2.xml"));

		FormResource resource = Context.getFormService().getFormResource(form, FormUtils.RESOURCE_HFE_XML_PATH);
		Assert.assertEquals("kenyacore:test2.xml", resource.getValue());
	}

	@Test
	public void getHtmlForm_shouldCreateDynamicHtmlFormFormXmlPathResource() throws Exception {
		Form form = Context.getFormService().getForm(1);
		FormUtils.setFormXmlResource(form, new UIResource("kenyacore", "test3.xml"));

		// Mock the resource factory so it will provide this xml content at kenyacore:test3.xml
		String xmlContent = "<htmlform>Test</htmlform>";
		ResourceFactory resourceFactory = mock(ResourceFactory.class);
		when(resourceFactory.getResourceAsString("kenyacore", "htmlforms/test3.xml")).thenReturn(xmlContent);

		HtmlForm hf = FormUtils.getHtmlForm(form, resourceFactory);
		Assert.assertEquals(form, hf.getForm());
		Assert.assertEquals(xmlContent, hf.getXmlData());
	}

	@Test
	public void getHtmlForm_shouldLoadExistingPersistedHtmlForm() throws Exception {
		Form form = Context.getFormService().getForm(1);

		// Persist an html form
		String xmlContent = "<htmlform>Test</htmlform>";
		HtmlForm hf1 = new HtmlForm();
		hf1.setForm(form);
		hf1.setXmlData(xmlContent);
		Context.getService(HtmlFormEntryService.class).saveHtmlForm(hf1);

		// Mock the resource factory
		ResourceFactory resourceFactory = mock(ResourceFactory.class);

		HtmlForm hf2 = FormUtils.getHtmlForm(form, resourceFactory);
		Assert.assertEquals(form, hf2.getForm());
		Assert.assertEquals(xmlContent, hf2.getXmlData());
	}

	@Test
	public void getHtmlForm_shouldThrowExceptionIfNoPathOrPersistedHtmlForm() throws Exception {
		Form form = Context.getFormService().getForm(1);
		ResourceFactory resourceFactory = mock(ResourceFactory.class);

		try {
			FormUtils.getHtmlForm(form, resourceFactory);
			Assert.fail();
		}
		catch (Exception ex) {
		}
	}

	@Test
	public void htmlTag() {
		Assert.assertEquals("<test>", FormUtils.htmlTag("test", null, false));
		Assert.assertEquals("<test />", FormUtils.htmlTag("test", null, true));

		Map<String, Object> attributes = new LinkedHashMap<String, Object>();
		attributes.put("a1", "hello");
		attributes.put("a2", 123);
		attributes.put("a3", "x\"x");

		Assert.assertEquals("<test a1=\"hello\" a2=\"123\" a3=\"x&#34;x\">", FormUtils.htmlTag("test", attributes, false));
		Assert.assertEquals("<test a1=\"hello\" a2=\"123\" a3=\"x&#34;x\" />", FormUtils.htmlTag("test", attributes, true));
	}
}