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

package org.openmrs.module.kenyaemr.form.widget;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.widget.Widget;
import org.openmrs.module.htmlformentry.widget.WidgetFactory;
import org.openmrs.module.kenyacore.form.FormUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class ObjectSearchWidget implements Widget {

	private BaseOpenmrsObject initialValue;

	private Class<? extends BaseOpenmrsObject> valueClass;

	public ObjectSearchWidget(Class<? extends BaseOpenmrsObject> valueClass) {
		this.valueClass = valueClass;
	}

	/**
	 * @see Widget#setInitialValue(Object)
	 */
	@Override
	public void setInitialValue(Object initialValue) {
		this.initialValue = (BaseOpenmrsObject) initialValue;
	}

	/**
	 * @see Widget#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Override
	public String generateHtml(FormEntryContext context) {
		StringBuilder sb = new StringBuilder();

		if (context.getMode().equals(FormEntryContext.Mode.VIEW)) {
			if (initialValue != null) {
				return WidgetFactory.displayValue(initialValue.toString());
			} else {
				return WidgetFactory.displayEmptyValue("_______________");
			}
		}
		else {
			Map<String, Object> attributes = new LinkedHashMap<String, Object>();
			attributes.put("type", "hidden");
			attributes.put("name", context.getFieldName(this));
			attributes.put("class", "ke-search");
			attributes.put("data-searchtype", valueClass.getSimpleName().toLowerCase());

			if (initialValue != null) {
				attributes.put("value", initialValue.getId());
			}

			sb.append("<span style=\"text-align: left\">");
			sb.append(FormUtils.htmlTag("input", attributes, true));
			sb.append("</span>");
		}
		return sb.toString();
	}

	/**
	 * @see Widget#getValue(org.openmrs.module.htmlformentry.FormEntryContext, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Object getValue(FormEntryContext context, HttpServletRequest request) {
		return request.getParameter(context.getFieldName(this));
	}
}