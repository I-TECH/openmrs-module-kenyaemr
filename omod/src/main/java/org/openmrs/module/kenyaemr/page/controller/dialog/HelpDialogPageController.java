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

package org.openmrs.module.kenyaemr.page.controller.dialog;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.annotation.PublicPage;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for help dialog
 */
@PublicPage
public class HelpDialogPageController {

	// These will eventually be fetched dynamically from outside of KenyaEMR
	private static final String[][] helpResources = new String[][] {
			{ "How to Create a Patient Record", "K_JobAid_1_CreatePt_13.2.pdf" },
			{ "How to Search for a Patient Record", "K_JobAid_2_SearchPt_13.2.pdf" },
			{ "How to Record a Patient's Family History", "K_JobAid_3_FamilyHistory_13.2.pdf" },
			{ "How to Record a Patient's Obstetric History", "K_JobAid_4_ObsHistory_13.2.pdf" },
			{ "How to Enroll a Patient in the HIV Program", "K_JobAid_5_HIVEnroll_13.2.pdf" },
			{ "How to Enter Data from a Clinical Encounter", "K_JobAid_6_Encounter_13.2.pdf" },
			{ "How to Complete a Clinical Encounter - HIV Addendum Form", "K_JobAid_7_HIVEncounter_13.2.pdf" },
			{ "How to Enroll a Patient in the TB Program", "K_JobAid_8_TBEnroll_13.2.pdf" },
			{ "How to Enter Patient Data From a Filled MOH 257", "K_JobAid_10_RE_13.2.pdf" },
			{ "How to Record Starting a Patient on an ARV Regimen", "K_JobAid_11_StartARV_13.2.pdf" },
			{ "How to Record Changes to a Patient's Current ARV Regimen", "K_JobAid_12_ChangeARV_13.2.pdf" },
			{ "How to Record Stops in ART", "K_JobAid_13_StopARV_13.2.pdf" },
			{ "Where to Go From the Main Menu", "K_JobAid_14_MainMenu_13.2.pdf" }
	};

	public void controller(PageModel model) {

		List<HelpResource> resources = new ArrayList<HelpResource>();
		for (String[] res : helpResources) {
			resources.add(new HelpResource(res[0], "/help/" + res[1]));
		}

		model.put("resources", resources);

		try {
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);

			String facilityCode = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();
			String supportNumber = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_SUPPORT_PHONE_NUMBER, EmrConstants.DEFAULT_SUPPORT_PHONE_NUMBER);
			String supportEmail = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_SUPPORT_EMAIL_ADDRESS, EmrConstants.DEFAULT_SUPPORT_EMAIL_ADDRESS);

			model.put("facilityCode", facilityCode);
			model.put("supportNumber", supportNumber);
			model.put("supportEmail", supportEmail);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
		}
	}

	/**
	 * Represents a help resource
	 */
	public static class HelpResource {

		private String name;

		private String url;

		/**
		 * Creates a new help resource
		 * @param name the name
		 * @param url the URL
		 */
		public HelpResource(String name, String url) {
			this.name = name;
			this.url = url;
		}

		/**
		 * Gets the name
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the URL
		 * @return the URL
		 */
		public String getUrl() {
			return url;
		}
	}
}