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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Converts a report request to a simple object
 */
@Component
public class ReportRequestToSimpleObjectConverter implements Converter<ReportRequest, SimpleObject> {

	@Autowired
	private UiUtils ui;

	@Autowired
	private KenyaUiUtils kenyaUi;

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public SimpleObject convert(ReportRequest request) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", request.getId());
		ret.put("requestDate", kenyaUi.formatDateTime(request.getRequestDate()));
		ret.put("requestedBy", ui.simplifyObject(request.getRequestedBy()));
		ret.put("status", request.getStatus());
		ret.put("complete", request.getStatus().equals(ReportRequest.Status.COMPLETED));
		ret.put("timeTaken", kenyaUi.formatDuration(getTimeTaken(request)));
		return ret;
	}

	/**
	 * Calculates a time taken display value for the given request
	 * @param request the request
	 * @return the time taken in milliseconds
	 */
	protected long getTimeTaken(ReportRequest request) {
		if (request.getEvaluateStartDatetime() == null) {
			return 0l;
		}

		Date start = request.getEvaluateStartDatetime();
		Date end = request.getEvaluateCompleteDatetime() != null ? request.getEvaluateCompleteDatetime() : new Date();

		return end.getTime() - start.getTime();
	}
}