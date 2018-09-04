/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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