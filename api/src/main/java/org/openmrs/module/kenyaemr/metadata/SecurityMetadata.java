/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.kenyadq.DqConstants;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

/**
 * Security metadata bundle
 */
@Component
public class SecurityMetadata extends AbstractMetadataBundle {

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	public static final class _Privilege {
		public static final String VIEW_LEGACY_INTERFACE = "Emr: View Legacy Interface";
		public static final String MANAGE_DRUG_ORDERS = "Can service drug prescriptions";
		public static final String MANAGE_LAB_REQUESTS = "Can service lab requests";
		public static final String MANAGE_AIR = "Can service AIR";
		public static final String MANAGE_LAB_MANIFEST = "Can manage viral load lab manifest";
	}

	public static final class _Role {
		public static final String API_PRIVILEGES_VIEW_AND_EDIT = "API Privileges (View and Edit)";
		public static final String API_PRIVILEGES = "API Privileges";
		public static final String CLINICIAN = "Clinician";
		public static final String DATA_CLERK = "Data Clerk";
		public static final String INTAKE = "Intake";
		public static final String MANAGER = "Manager";
		public static final String REGISTRATION = "Registration";
		public static final String SYSTEM_ADMIN = "System Administrator";
		public static final String SYSTEM_DEVELOPER = "System Developer";
		public static final String DRUG_ORDER = "Pharmacist";
		public static final String LAB_TECHNICIAN = "Lab Technician";
		public static final String PEER_EDUCATOR = "Peer Educator";
		public static final String HIV_TESTING_COUNSELLOR = "HTS Counsellor";
		public static final String AIR = "AIR";
		public static final String LAB_MANIFEST = "Lab Manifest";
		public static final String ADHERENCE_COUNSELOR = "Adherence Counselor";
		public static final String UPI_VERIFICATION = "Upi verification";
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		String[] appIds = {
				EmrConstants.APP_REGISTRATION,
				EmrConstants.APP_INTAKE,
				EmrConstants.APP_CLINICIAN,
				EmrConstants.APP_CHART,
				EmrConstants.APP_REPORTS,
				EmrConstants.APP_DIRECTORY,
				EmrConstants.APP_FACILITIES,
				EmrConstants.APP_ADMIN,
				DqConstants.APP_DATAQUALITY,
				DqConstants.APP_DATAMANAGER,
				EmrConstants.APP_FACILITY_DASHBOARD,
				EmrConstants.APP_DRUG_ORDER,
				EmrConstants.APP_LAB_ORDER,
				EmrConstants.APP_DEFAULTER_TRACING,
				EmrConstants.APP_HIV_TESTING,
				EmrConstants.APP_PREP,
				EmrConstants.APP_AIR,
				EmrConstants.APP_LAB_MANIFEST,
				EmrConstants.APP_ADHERENCE_COUNSELOR,
				EmrConstants.APP_UPI_VERIFICATION
		};

		// Ensure a privilege exists for each app. App framework does create these but not always before this
		// bundle is installed
		for (String appId : appIds) {
			install(privilege(app(appId), "Access to the " + appId + " app"));
		}

		// Add custom privileges
		install(privilege(_Privilege.VIEW_LEGACY_INTERFACE, "Can view legacy web interface"));
		install(privilege(_Privilege.MANAGE_DRUG_ORDERS, "Can view and edit drug orders"));
		install(privilege(_Privilege.MANAGE_LAB_REQUESTS, "Able to service lab requests"));
		install(privilege(_Privilege.MANAGE_AIR, "Able to service AIR"));
		install(privilege(_Privilege.MANAGE_LAB_MANIFEST, "Can manage viral load lab manifest"));


		// Ensure that some extra API privileges exist as core doesn't create these by default
		install(privilege(PrivilegeConstants.PURGE_PATIENT_IDENTIFIERS, "Able to purge patient identifiers"));

		install(role(_Role.API_PRIVILEGES, "All API privileges",
				null, getApiPrivileges(true))
		);

		install(role(_Role.API_PRIVILEGES_VIEW_AND_EDIT, "All viewing and editing API privileges",
				null, getApiPrivileges(false))
		);

		install(role(_Role.REGISTRATION, "Can access the registration app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_REGISTRATION),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_DEFAULTER_TRACING),
						app(EmrConstants.APP_PREP),
						app(EmrConstants.APP_AIR),
						app(EmrConstants.APP_UPI_VERIFICATION)
				)
		));

		install(role(_Role.INTAKE, "Can access the registration and triage apps",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_REGISTRATION),
						app(EmrConstants.APP_INTAKE),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_DEFAULTER_TRACING),
						app(EmrConstants.APP_PREP),
						app(EmrConstants.APP_UPI_VERIFICATION),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));

		install(role(_Role.MANAGER, "Can access all apps except admin",
				idSet(_Role.API_PRIVILEGES),
				idSet(
						app(EmrConstants.APP_REGISTRATION),
						app(EmrConstants.APP_INTAKE),
						app(EmrConstants.APP_CLINICIAN),
						app(EmrConstants.APP_CHART),
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(DqConstants.APP_DATAQUALITY),
						app(DqConstants.APP_DATAMANAGER),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_DRUG_ORDER),
						app(EmrConstants.APP_LAB_ORDER),
						app(EmrConstants.APP_LAB_MANIFEST),
						app(EmrConstants.APP_DEFAULTER_TRACING),
						app(EmrConstants.APP_PREP),
						app(EmrConstants.APP_AIR),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));

		install(role(_Role.CLINICIAN, "Can access the registration, triage, clinician, chart and reports apps",
				idSet(_Role.API_PRIVILEGES),
				idSet(
						app(EmrConstants.APP_REGISTRATION),
						app(EmrConstants.APP_INTAKE),
						app(EmrConstants.APP_CLINICIAN),
						app(EmrConstants.APP_CHART),
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_DRUG_ORDER),
						app(EmrConstants.APP_LAB_ORDER),
						app(EmrConstants.APP_LAB_MANIFEST),
						app(EmrConstants.APP_DEFAULTER_TRACING),
						app(EmrConstants.APP_PREP),
						app(EmrConstants.APP_AIR),
						app(EmrConstants.APP_UPI_VERIFICATION),
						_Privilege.MANAGE_DRUG_ORDERS,
						_Privilege.VIEW_LEGACY_INTERFACE,
						_Privilege.MANAGE_AIR
				)
		));

		install(role(_Role.DATA_CLERK, "Can access the chart, reporting and data quality apps",
				idSet(_Role.API_PRIVILEGES),
				idSet(
						app(EmrConstants.APP_CHART),
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(DqConstants.APP_DATAQUALITY),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_DRUG_ORDER),
						app(EmrConstants.APP_LAB_ORDER),
						app(EmrConstants.APP_DEFAULTER_TRACING),
						app(EmrConstants.APP_PREP),
						app(EmrConstants.APP_AIR),
						app(EmrConstants.APP_UPI_VERIFICATION),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));

		install(role(_Role.PEER_EDUCATOR, "Can access the defaulter tracing and reporting apps",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_DEFAULTER_TRACING),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));

		install(role(_Role.HIV_TESTING_COUNSELLOR, "Can access the hts and reporting apps",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_HIV_TESTING),
						app(EmrConstants.APP_PREP),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));

		install(role(_Role.SYSTEM_ADMIN, "Can access the admin app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_ADMIN),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD)
				)
		));

		install(role(_Role.DRUG_ORDER, "Can access the drug prescriptions app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_DRUG_ORDER),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));

		install(role(_Role.LAB_TECHNICIAN, "Can access the lab requests app",
				idSet(_Role.API_PRIVILEGES),
				idSet(
						app(EmrConstants.APP_LAB_ORDER),
						app(EmrConstants.APP_LAB_MANIFEST),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));
		install(role(_Role.AIR, "Can access AIR app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_AIR),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						_Privilege.VIEW_LEGACY_INTERFACE,
						_Privilege.MANAGE_AIR
				)
		));
		install(role(_Role.LAB_MANIFEST, "Can access the lab manifest app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_LAB_MANIFEST),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES)
				)
		));

		install(role(_Role.ADHERENCE_COUNSELOR, "Can access the adherence counselor app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_ADHERENCE_COUNSELOR),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));
		install(role(_Role.UPI_VERIFICATION, "Can access the upi verification app",
				idSet(_Role.API_PRIVILEGES_VIEW_AND_EDIT),
				idSet(
						app(EmrConstants.APP_REPORTS),
						app(EmrConstants.APP_DIRECTORY),
						app(EmrConstants.APP_FACILITIES),
						app(EmrConstants.APP_FACILITY_DASHBOARD),
						app(EmrConstants.APP_ADHERENCE_COUNSELOR),
						app(EmrConstants.APP_UPI_VERIFICATION),
						_Privilege.VIEW_LEGACY_INTERFACE
				)
		));
	}

	/**
	 * Fetches sets of API privileges
	 * @param incDestructive include destructive (delete, purge) privileges
	 * @return the privileges
	 */
	protected Set<String> getApiPrivileges(boolean incDestructive) {
		Set<String> privileges = new HashSet<String>();

		for (Privilege privilege : userService.getAllPrivileges()) {
			if (privilege.getPrivilege().startsWith("App: ") || privilege.getPrivilege().startsWith("Emr: ")) {
				continue;
			}

			boolean isDestructive = privilege.getPrivilege().startsWith("Delete ") || privilege.getPrivilege().startsWith("Purge ");

			if (!incDestructive && isDestructive) {
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
