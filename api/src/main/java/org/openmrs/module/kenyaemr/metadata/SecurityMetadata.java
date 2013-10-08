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

package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.kenyacore.metadata.bundle.AbstractMetadataBundle;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.kenyacore.metadata.bundle.CoreConstructors.*;

/**
 * Security metadata bundle
 */
@Component
public class SecurityMetadata extends AbstractMetadataBundle {

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	public static final class _Role {
		public static final String API_PRIVILEGES_VIEW_AND_EDIT = "API Privileges (View and Edit)";
		public static final String API_PRIVILEGES = "API Privileges";
		public static final String DATA_CLERK = "Data Clerk";
		public static final String INTAKE = "Intake";
		public static final String MANAGER = "Manager";
		public static final String PROVIDER = "Provider";
		public static final String REGISTRATION = "Registration";
		public static final String SYSTEM_ADMIN = "System Administrator";
	}

	/**
	 * @see org.openmrs.module.kenyacore.metadata.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		String[] appIds = {
				EmrConstants.APP_REGISTRATION,
				EmrConstants.APP_INTAKE,
				EmrConstants.APP_CLINICIAN,
				EmrConstants.APP_CHART,
				EmrConstants.APP_REPORTS,
				EmrConstants.APP_ADMIN,
				"kenyadq.dataQuality"
		};

		// Ensure a privilege exists for each app. App framework does create these but not always before this
		// bundle is installed
		for (String appId : appIds) {
			install(privilege(app(appId), "Access to the " + appId + " app"));
		}

		install(role(_Role.API_PRIVILEGES, "All API privileges", null, getApiPrivileges(true)));
		install(role(_Role.API_PRIVILEGES_VIEW_AND_EDIT, "All viewing and editing API privileges", null, getApiPrivileges(false)));

		install(role(_Role.REGISTRATION, "Can access the registration app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(app(EmrConstants.APP_REGISTRATION))));
		install(role(_Role.INTAKE, "Can access the registration and intake apps",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(app(EmrConstants.APP_REGISTRATION), app(EmrConstants.APP_INTAKE))));
		install(role(_Role.MANAGER, "Can access all apps except admin, and manage encounters",
				idSet(_Role.API_PRIVILEGES),
				idSet(app(EmrConstants.APP_REGISTRATION), app(EmrConstants.APP_INTAKE), app(EmrConstants.APP_CLINICIAN), app(EmrConstants.APP_CHART), app(EmrConstants.APP_REPORTS), app("kenyadq.dataQuality"))));
		install(role(_Role.PROVIDER, "Can access all apps except admin, and provide encounters",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(app(EmrConstants.APP_REGISTRATION), app(EmrConstants.APP_INTAKE), app(EmrConstants.APP_CLINICIAN), app(EmrConstants.APP_CHART), app(EmrConstants.APP_REPORTS))));
		install(role(_Role.DATA_CLERK, "Can access the chart and reporting apps",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(app(EmrConstants.APP_CHART), app(EmrConstants.APP_REPORTS), app("kenyadq.dataQuality"))));
		install(role(_Role.SYSTEM_ADMIN, "Can access the admin app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(app(EmrConstants.APP_ADMIN))));
	}

	/**
	 * Fetches sets of API privileges
	 * @param incDelete include delete privileges
	 * @return the privileges
	 */
	protected Set<String> getApiPrivileges(boolean incDelete) {
		Set<String> privileges = new HashSet<String>();

		for (Privilege privilege : userService.getAllPrivileges()) {
			if (privilege.getPrivilege().startsWith("App: ")) {
				continue;
			}
			if (!incDelete && privilege.getPrivilege().startsWith("Delete ")) {
				continue;
			}
			privileges.add(privilege.getPrivilege());
		}

		return privileges;
	}

	/**
	 * Creates an app privilege from an app ID
	 * @param appId the app ID
	 * @return the privilege
	 */
	protected String app(String appId) {
		return "App: " + appId;
	}
}