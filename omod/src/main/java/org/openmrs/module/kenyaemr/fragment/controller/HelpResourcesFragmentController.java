/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * help fragment
 */
public class HelpResourcesFragmentController {
	private static final String[][] dataToolspdfHelpResources = new String[][] {
			{ "Data extraction and reporting using datatools", "Data_tools_User_SOP_UPDATED.pdf" }

	};
	private static final String[][] otzpdfHelpResources = new String[][] {
			{ "OTZ navigation ", "OTZ_Module_Job_Aid.pdf" }

	};
	private static final String[][] ovcpdfHelpResources = new String[][] {
			{ "OVC user navigation", "OVC_Module Job_Aid.pdf" }

	};
	private static final String[][] preppdfHelpResources = new String[][] {
			{ "PrEP navigation", "PrEP_Module_User_Guide.pdf" }

	};
	private static final String[][] dwpipdfHelpResources = new String[][] {
			{ "DWAPI installation on Ubuntu", "DWAPI_Installation_on_ubuntu.pdf" },
			{ "DWAPI Installation on Windows", "DWAPI_Installation_on_windows" },
			{ "DWAPI Data transmission job Aid", "DWAPI_Data_Transmission.pdf" },
			{ "DWAPI Deduplication function", "DWAPI_De_duplication.pdf" },
			{ "DWAPI Data Cleaning function", "DWAPI_Data_Cleaning.pdf" }

	};

	private static final String[][] dataToolsvideoHelpResources = new String[][] {
			{ "Data extraction and reporting using datatools", "Data_Tools_Reporting.mp4" }
	};




	public void controller(PageModel model )  {

		List<HelpResourcesFragmentController.HelpResource> dataToolsPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : dataToolspdfHelpResources) {
			dataToolsPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> dataTooVideoResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : dataToolsvideoHelpResources) {
			dataTooVideoResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> otzPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : otzpdfHelpResources) {
			otzPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> ovcPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : ovcpdfHelpResources) {
			ovcPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> prepPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : preppdfHelpResources) {
			prepPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> dwapiPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : dwpipdfHelpResources) {
			dwapiPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}



		User authenticatedUser = Context.getAuthenticatedUser();

		model.addAttribute("isAuthenticated", authenticatedUser != null ? Context.getAuthenticatedUser() : false);

		model.put("dataToolsPdfResources", dataToolsPdfResources);
		model.put("ovcPdfResources", ovcPdfResources);
		model.put("otzPdfResources", otzPdfResources);
		model.put("prepPdfResources", prepPdfResources);
		model.put("dwapiPdfResources", dwapiPdfResources);
		model.put("dataTooVideoResources", dataTooVideoResources);
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
