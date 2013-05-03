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

import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.IOException;

/**
 * Form utility methods
 */
public class FormUtils {

	protected static final String RESOURCE_HFE_XML_PATH = "hfeXmlPath";

	/**
	 * Gets the XML resource path of the given form (null if form doesn't have an XML resource)
	 * @param form the form
	 * @return the XML resource path
	 */
	public static String getFormXmlPath(Form form) {
		FormResource resource = Context.getFormService().getFormResource(form, RESOURCE_HFE_XML_PATH);
		return resource != null ? ((String) resource.getValue()) : null;
	}

	/**
	 * Set the XML resource path of the given form
	 * @param form the form
	 * @param xmlPath the path
	 */
	public static void setFormXmlPath(Form form, String xmlPath) {
		FormResource resXmlPath = Context.getFormService().getFormResource(form, RESOURCE_HFE_XML_PATH);

		if (resXmlPath == null) {
			resXmlPath = new FormResource();
			resXmlPath.setForm(form);
			resXmlPath.setName(RESOURCE_HFE_XML_PATH);
			resXmlPath.setDatatypeClassname(FreeTextDatatype.class.getName());
		}

		resXmlPath.setValue(xmlPath);

		Context.getFormService().saveFormResource(resXmlPath);
	}

	/**
	 * Gets an HTML from a form
	 * @param form the form
	 * @param resourceFactory the resourceFactory
	 * @return the Html form
	 * @throws RuntimeException if form has no xml path or path is invalid
	 */
	public static HtmlForm getHtmlForm(Form form, ResourceFactory resourceFactory) throws IOException {
		String xmlPath = getFormXmlPath(form);

		if (xmlPath == null) {
			// Look in the database
			HtmlForm hf = HtmlFormEntryUtil.getService().getHtmlFormByForm(form);
			if (hf != null)
				return hf;

			throw new RuntimeException("Form has no XML path or persisted html form");
		}
		else if (!xmlPath.contains(":")) {
			throw new RuntimeException("Form XML path '" + xmlPath + "' must use format <provider>:<path>");
		}

		String[] pathTokens = xmlPath.split(":");
		String providerName = pathTokens[0];
		String resourcePath = pathTokens[1];
		String xml = resourceFactory.getResourceAsString(providerName, resourcePath);

		if (xml == null) {
			throw new RuntimeException("Form XML could not be loaded from path '" + xmlPath + "'");
		}

		HtmlForm hf = new HtmlForm();
		hf.setForm(form);
		hf.setCreator(form.getCreator());
		hf.setDateCreated(form.getDateCreated());
		hf.setChangedBy(form.getChangedBy());
		hf.setDateChanged(form.getDateChanged());
		hf.setXmlData(xml);
		return hf;
	}
}