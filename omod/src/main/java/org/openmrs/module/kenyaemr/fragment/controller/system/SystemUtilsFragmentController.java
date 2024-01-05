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

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyadq.DqConstants;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.util.ServerInformation;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.module.reportingcompatibility.service.ReportingCompatibilityService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * System utilities fragment
 */
public class SystemUtilsFragmentController {

	/**
	 * Fetches server information
	 * @param kenyaUi the KenyaUI utils
	 * @return the information as a simple object
	 */
	@AppAction(EmrConstants.APP_ADMIN)
	public List<SimpleObject> getServerInformation(@SpringBean KenyaUiUtils kenyaUi) {

		Map<String, Object> serverInfo = ServerInformation.getAllInformation();
		Map<String, Object> systemInfo = (Map<String, Object>) serverInfo.get("system");
		Map<String, Object> openmrsInfo = (Map<String, Object>) serverInfo.get("openmrs");
		Map<String, Object> runtimeInfo = (Map<String, Object>) serverInfo.get("runtime");
		Map<String, Object> userInfo = (Map<String, Object>) systemInfo.get("user");
		Map<String, Object> javaInfo = (Map<String, Object>) systemInfo.get("java");

		long maxMemory = (Long) runtimeInfo.get("maxmemory");
		long totalMemory = (Long) runtimeInfo.get("totalmemory");
		long freeMemory = (Long) runtimeInfo.get("freememory");
		long usedMemory = totalMemory - freeMemory;

		StringBuilder memInfo = new StringBuilder();
		memInfo.append(kenyaUi.formatBytes(usedMemory));
		memInfo.append(" / ");
		memInfo.append(kenyaUi.formatBytes(totalMemory));
		memInfo.append(" / ");
		memInfo.append(kenyaUi.formatBytes(maxMemory));

		List<SimpleObject> points = new ArrayList<SimpleObject>();
		points.add(SimpleObject.create("label", "OpenMRS version", "value", openmrsInfo.get("version")));
		points.add(SimpleObject.create("label", "System timezone", "value", userInfo.get("timezone")));
		points.add(SimpleObject.create("label", "Java version", "value", javaInfo.get("version") + " (" + javaInfo.get("vendor") + ")"));
		points.add(SimpleObject.create("label", "Memory (used / total / max)", "value", memInfo.toString()));

		return points;
	}

	/**
	 * Fetches a database summary
	 * @return the summary
	 */
	@SharedAction({EmrConstants.APP_ADMIN, DqConstants.APP_DATAMANAGER})
	public List<SimpleObject> getDatabaseSummary() {
		List<SimpleObject> points = new ArrayList<SimpleObject>();
		points.add(SimpleObject.create("label", "Total patients", "value", Context.getService(ReportingCompatibilityService.class).getCountOfPatients()));
		points.add(SimpleObject.create("label", "Total providers", "value", Context.getProviderService().getAllProviders().size()));
		points.add(SimpleObject.create("label", "Total users", "value", Context.getUserService().getAllUsers().size()));
		return points;
	}
}