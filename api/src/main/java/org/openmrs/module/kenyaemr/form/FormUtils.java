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
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.IOException;

/**
 * Form utility methods
 */
public class FormUtils {

	private static final String RESOURCE_XML_PATH = "xmlPath";

	/**
	 * Checks if the given form has an XML path resource
	 * @param form the form
	 * @return true if form has an XML path
	 */
	public static boolean formHasXmlPath(Form form) {
		return Context.getFormService().getFormResource(form, RESOURCE_XML_PATH) != null;
	}

	/**
	 * Sets the XML resource path
	 * @param form the form
	 * @param xmlPath the path
	 */
	public static void setFormXmlPath(Form form, String xmlPath) {
		FormResource resXmlPath = Context.getFormService().getFormResource(form, "xmlPath");

		if (resXmlPath == null) {
			resXmlPath = new FormResource();
			resXmlPath.setForm(form);
			resXmlPath.setName("xmlPath");
			resXmlPath.setDatatypeClassname(FreeTextDatatype.class.getName());
		}

		resXmlPath.setValue(xmlPath);

		Context.getFormService().saveFormResource(resXmlPath);
	}

	/**
	 * Dynamically builds an HtmlForm object from a form which contains a XML path resource
	 * @param resourceFactory the resourceFactory
	 * @param form the form
	 * @return the Html form
	 */
	public static HtmlForm buildHtmlForm(ResourceFactory resourceFactory, Form form) throws IOException {
		FormResource resXmlPath = Context.getFormService().getFormResource(form, RESOURCE_XML_PATH);
		String pathVal = (String) resXmlPath.getValue();

		String[] pathTokens = pathVal.split(":");
		String providerName = pathTokens[0];
		String resourcePath = pathTokens[1];

		String xml = resourceFactory.getResourceAsString(providerName, resourcePath);

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
