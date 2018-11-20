/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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