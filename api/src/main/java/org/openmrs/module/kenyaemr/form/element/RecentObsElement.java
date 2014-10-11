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

package org.openmrs.module.kenyaemr.form.element;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Map;

/**
 * Custom tag to display any recent obs value recorded for a give concept
 */
public class RecentObsElement implements HtmlGeneratorElement {

	private String conceptId;
	private boolean showDate = true;
	private String noneMessage;

	/**
	 * Creates a new element
	 * @param context the form entry context
	 * @param parameters the tag parameters
	 */
	public RecentObsElement(FormEntryContext context, Map<String, String> parameters) {
		conceptId = parameters.get("conceptId");

		if (StringUtils.isEmpty(conceptId)) {
			throw new RuntimeException("conceptId attribute required");
		}

		if (parameters.containsKey("showDate")) {
			showDate = parameters.get("showDate").equals("true");
		}

		noneMessage = parameters.get("noneMessage");
	}

	/**
	 * @see org.openmrs.module.htmlformentry.element.HtmlGeneratorElement#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Override
	public String generateHtml(FormEntryContext context) {
		if (context.getExistingPatient() == null) {
			return "";
		}

		KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);

		PatientWrapper patient = new PatientWrapper(context.getExistingPatient());

		Obs obs = patient.lastObs(MetadataUtils.existing(Concept.class, conceptId));

		StringBuilder sb = new StringBuilder("<span>");

		if (obs != null) {
			sb.append(kenyaui.formatObsValue(obs));

			if (showDate) {
				sb.append(" <small>(" + kenyaui.formatDate(obs.getObsDatetime()) + ")</small>");
			}
		}
		else if (noneMessage != null) {
			sb.append(noneMessage);
		}

		sb.append("</span>");
		return sb.toString();
	}
}