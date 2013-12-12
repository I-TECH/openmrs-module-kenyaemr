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

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.util.SystemInformation;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * System utilities fragment
 */
public class SystemUtilsFragmentController {

	@AppAction(EmrConstants.APP_ADMIN)
	public List<SimpleObject> getSystemInformation(@SpringBean KenyaUiUtils kenyaUi) {

		Map<String, Object> sysInfo = SystemInformation.getData();

		long maxMemory = (Long) sysInfo.get(SystemInformation.JVM_MAXMEMORY);
		long totalMemory = (Long) sysInfo.get(SystemInformation.JVM_TOTALMEMORY);
		long freeMemory = (Long) sysInfo.get(SystemInformation.JVM_FREEMEMORY);
		long usedMemory = totalMemory - freeMemory;

		StringBuilder memInfo = new StringBuilder();
		memInfo.append(kenyaUi.formatBytes(usedMemory));
		memInfo.append(" / ");
		memInfo.append(kenyaUi.formatBytes(totalMemory));
		memInfo.append(" / ");
		memInfo.append(kenyaUi.formatBytes(maxMemory));

		List<SimpleObject> points = new ArrayList<SimpleObject>();
		points.add(SimpleObject.create("label", "OpenMRS version", "value", sysInfo.get(SystemInformation.OPENMRS_VERSION)));
		points.add(SimpleObject.create("label", "Server timezone", "value", sysInfo.get(SystemInformation.SERVER_TIMEZONE)));
		points.add(SimpleObject.create("label", "Memory (used / total / max)", "value", memInfo.toString()));

		return points;
	}

	@AppAction(EmrConstants.APP_ADMIN)
	public List<SimpleObject> getDatabaseSummary() {
		List<SimpleObject> points = new ArrayList<SimpleObject>();
		points.add(SimpleObject.create("label", "Total patients", "value", Context.getPatientSetService().getCountOfPatients()));
		points.add(SimpleObject.create("label", "Total providers", "value", Context.getProviderService().getAllProviders().size()));
		points.add(SimpleObject.create("label", "Total users", "value", Context.getUserService().getAllUsers().size()));
		return points;
	}
}