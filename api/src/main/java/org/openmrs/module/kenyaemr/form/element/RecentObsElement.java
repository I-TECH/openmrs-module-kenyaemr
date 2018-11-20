/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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