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

package org.openmrs.module.kenyaemr.form;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.resource.ResourceFactory;

import static org.mockito.Mockito.*;

public class FormUtilsTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see FormUtils#getFormXmlPath(org.openmrs.Form)
	 */
	@Test
	public void getFormXmlPath_shouldReturnXmlPathIfExists() {
		Form form = Context.getFormService().getForm(1);
		Assert.assertNull(FormUtils.getFormXmlPath(form));

		FormResource resource = new FormResource();
		resource.setForm(form);
		resource.setName(FormUtils.RESOURCE_HFE_XML_PATH);
		resource.setDatatypeClassname(FreeTextDatatype.class.getName());
		resource.setValue("kenyaemr:test1.xml");
		Context.getFormService().saveFormResource(resource);

		Assert.assertEquals("kenyaemr:test1.xml", FormUtils.getFormXmlPath(form));
	}

	@Test
	public void setFormXmlPath_shouldSetPathAsFormResource() {
		Form form = Context.getFormService().getForm(1);
		FormUtils.setFormXmlPath(form, "kenyaemr:test2.xml");

		FormResource resource = Context.getFormService().getFormResource(form, FormUtils.RESOURCE_HFE_XML_PATH);
		Assert.assertEquals("kenyaemr:test2.xml", resource.getValue());
	}

	@Test
	public void getHtmlForm_shouldCreateValidHtmlForm() throws Exception {
		Form form = Context.getFormService().getForm(1);
		FormUtils.setFormXmlPath(form, "kenyaemr:test3.xml");

		// Mock the resource factory so it will provide this xml content at kenyaemr:test3.xml
		String xmlContent = "<htmlform>Test</htmlform>";
		ResourceFactory resourceFactory = mock(ResourceFactory.class);
		when(resourceFactory.getResourceAsString("kenyaemr", "test3.xml")).thenReturn(xmlContent);

		HtmlForm hf = FormUtils.getHtmlForm(form, resourceFactory);
		Assert.assertEquals(form, hf.getForm());
		Assert.assertEquals(xmlContent, hf.getXmlData());
	}
}