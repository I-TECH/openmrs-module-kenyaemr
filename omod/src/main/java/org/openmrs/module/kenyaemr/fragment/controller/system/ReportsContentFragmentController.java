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

package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for displaying all program content
 */
public class ReportsContentFragmentController {

	public void controller(FragmentModel model, UiUtils ui, @SpringBean ReportManager reportManager) {
		List<SimpleObject> reports = new ArrayList<SimpleObject>();
		for (ReportDescriptor report : reportManager.getAllReportDescriptors()) {
			SimpleObject simpleReport = ui.simplifyObject(report);

			Collection<String> allowedApps = CollectionUtils.collect(report.getApps(), new Transformer() {
				@Override
				public Object transform(Object o) {
					return ((AppDescriptor) o).getLabel();
				}
			});

			simpleReport.put("allowedApps", allowedApps);
			reports.add(simpleReport);
		}

		model.addAttribute("reports", reports);
	}
}