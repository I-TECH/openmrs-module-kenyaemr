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
			{ "Data extraction and reporting using datatools", "Data_tools_User_SOP_UPDATED.pdf" },
			{ "Data tool setup on ubuntu machine", "Data_Tools_Setup_May 2020.pdf" }

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
			{ "DWAPI Installation on Windows", "DWAPI_Installation_on_windows.pdf" },
			{ "DWAPI Data transmission job Aid", "DWAPI_Data_Transmission.pdf" },
			{ "DWAPI Deduplication function", "DWAPI_De_duplication.pdf" },
			{ "DWAPI Data Cleaning function", "DWAPI_Data_Cleaning.pdf" }

	};

	private static final String[][] dataToolsvideoHelpResources = new String[][] {
			{ "Data extraction and reporting using datatools", "Data_Tools_Reporting.mp4" }
	};
	private static final String[][] kenyaEmrNavigationpdfHelpResources = new String[][] {
			{ "KenyaEMR Server reconstruction using clone", "SERVER_RECONSTRUCTION_USING _CLONE_IMAGE_JOB AID.pdf" },
			{ "KenyaEMR server setup - Baremetal", "SERVERSETUP_BareMetal_LongProcess_JobAid.pdf" },
			{ "KenyaEMR Upgrade process", "KenyaEMR_Upgrade_JOB_AID.pdf" },
			{ "Ubuntu Desktop Setup", "UBUNTU_DESKTOP_SETUP_FINAL.pdf" },
			{ "Ubuntu Server Setup", "UBUNTU_SERVER_Installation_JOB_AID.pdf" },
			{ "Alcohol and Drug Abuse Screening form", "Alcohol_Drug_Abuse_JobAid.pdf" },
			{ "Defaulter  Tracing", "Defaulter_Tracing_JobAid_Final.pdf" },
			{ "Enhanced Adherence", "Enhanced_Adherance_Screening_JobAid.pdf" },
			{ "Gender based violence screening", "GBVS_JobAid.pdf" },
			{ "Appointment Management", "KenyaEMR_Appointment_Management_JOB_AID.pdf" },
			{ "Full drug order - Prescription", "KenyaEMR_DRUG_FULL_ORDER_Job_AID .pdf" },
			{ "ART Fast Track  form", "KenyaEMR_fast_track_JOB_AID.pdf" },
			{ "Green card navigation", "KenyaEMR_GREENCARD_FINAL.pdf" },
			{ "Full lab order ", "KenyaEMR_LAB FULL_ORDER_JOB_AID.pdf" },
			{ "KenyaEMR Leap Surge Report ", "KenyaEMR_Leap_Surge_report_JOB_AID.pdf" },
			{ "MCH module child services ", "KenyaEMR_MCH_Module_Child_Job_aid.pdf" },
			{ "MCH module mother services ", "KenyaEMR_MCH_Module_Mother_Job_aid.pdf" },
			{ "KenyaEMR patient merge function", "KenyaEMR_Merge_Function_JOB AID_CBS.pdf" }

	};

	private static final String[][] kenyaEmrNavigationVideoHelpResources = new String[][] {
			{ "Alcohol and Drug Abuse Screening form", "Alcohol_Drug_Abuse_Screening.mp4" },
			{ "Appointment Management", "appointment_management.mp4" },
			{ "ART Fast Track  form", "ART_fast_track.mp4" },
			{ "ART Initiation", "ART_Initiation.mp4" },
			{ "ART Preparation Encounter", "ART_Preparation_Encounter.mp4" },
			{ "Defaulter  Tracing", "Defaulter_Tracing.mp4" },
			{ "Desktop Ubuntu Installation guide", "Desktop_Ubuntu_installation.mp4" },
			{ "Ubuntu Server Installation", "Ubuntu_Server_installation.mp4" },
			{ "Full drug order - Prescription", "Drug_Order.mp4" },
			{ "Full lab order ", "Lab_Order.mp4" },
			{ "Enhanced Adherence", "Enhanced_Adherence.mp4" },
			{ "Gender based violence screening", "Gender_Based_Violence_Screening.mp4" },
			{ "Green card navigation", "Greencard_Navigation.mp4" },
			{ "KenyaEMR installation - Dependancies ", "KenyaEMR_dependancies_installation.mp4" },
			{ "Client registration in KenyaEMR ", "Client_Registration_Onto_the_HTS_Module.mp4" },
			{ "KenyaEMR Installation ", "KenyaEMR_installation.mp4" }
	};

	private static final String[][] ehtspdfHelpResources = new String[][] {
			{ "eHTS module (Desktop)", "HTS_user_JobAid_FINAL.pdf" }

	};
	private static final String[][] ehtsVideoHelpResources = new String[][] {
			{ "Client Registration into eHTS desktop module", "Client_Registration_HTS_Module.mp4" },
			{ "HTS Screening", "HTS_Screening_FormF1.mp4" },
			{ "eHTS Testing - Initial", "HTS_Testing_intial.mp4" },
			{ "eHTS Testing - Retest", "HTS_Retest_Form.mp4" },
			{ "eHTS referral and Linkage", "Referral_LinkageForm.mp4" },
			{ "eHTS PNS service encounter form", "Patient_NotificationService_Encounter.mp4" },
			{ "HTS reporting using Data tools", "HTSReports.mp4" },

	};

	private static final String[][] mzumapdfHelpResources = new String[][] {
			{ "mUzima technical setup", "mUzima_User_Installation_JOB_AID.pdf" },
			{ "mUzima server side configuration", "mUzima_configuration_From_KenyaEMRend.pdf" },
			{ "mUzima application navigation", "mUzima_User_JOB_AIDCBS.pdf" },
			{ "Ressolving errors in error queue", "mUzima_Error_Queue_JOB_AID_CBS.pdf" },
			{ "Linkage SOP", "eHTS_Linkage_SOP.pdf" }
	};
	private static final String[][] airpdfHelpResources = new String[][] {
			{ "AIR reporting process", "Automated_Indicator_Reporting_AIR_JOB_AID.pdf" }
	};
	private static final String[][] ilVideoHelpResources = new String[][] {
			{ "IL Setup on Ubuntu - Video", "IL_installation_on_Ubuntu_environment.mp4" }
	};
	private static final String[][] ilpdfHelpResources = new String[][] {
			{ "IL Installation and setup on Windows", "IL_Installation_windows_and_configuration.pdf" },
			{ "IL Installation and setup on Ubuntu", "IL_Linux_Installation_JobAid.pdf" },
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

		List<HelpResourcesFragmentController.HelpResource> kenyaemrNavigationPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : kenyaEmrNavigationpdfHelpResources) {
			kenyaemrNavigationPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> kenyaemrNavigationVideoResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : kenyaEmrNavigationVideoHelpResources) {
			kenyaemrNavigationVideoResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> htsPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : ehtspdfHelpResources) {
			htsPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> htsVideoResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : ehtsVideoHelpResources) {
			htsVideoResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> muzimaPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : mzumapdfHelpResources) {
			muzimaPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> airPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : airpdfHelpResources) {
			airPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}
		List<HelpResourcesFragmentController.HelpResource> ilPdfResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : ilpdfHelpResources) {
			ilPdfResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}

		List<HelpResourcesFragmentController.HelpResource> ilVideoResources = new ArrayList<HelpResourcesFragmentController.HelpResource>();
		for (String[] res : ilVideoHelpResources) {
			ilVideoResources.add(new HelpResourcesFragmentController.HelpResource(res[0], "/help/"  + res[1]));
		}




		User authenticatedUser = Context.getAuthenticatedUser();

		model.addAttribute("isAuthenticated", authenticatedUser != null ? Context.getAuthenticatedUser() : false);

		model.put("dataToolsPdfResources", dataToolsPdfResources);
		model.put("ovcPdfResources", ovcPdfResources);
		model.put("otzPdfResources", otzPdfResources);
		model.put("prepPdfResources", prepPdfResources);
		model.put("dwapiPdfResources", dwapiPdfResources);
		model.put("dataTooVideoResources", dataTooVideoResources);
		model.put("kenyaemrNavigationVideoResources", kenyaemrNavigationVideoResources);
		model.put("kenyaemrNavigationPdfResources", kenyaemrNavigationPdfResources);
		model.put("htsVideoResources", htsVideoResources);
		model.put("htsPdfResources", htsPdfResources);
		model.put("muzimaPdfResources", muzimaPdfResources);
		model.put("airPdfResources", airPdfResources);
		model.put("ilPdfResources", ilPdfResources);
		model.put("ilVideoResources", ilVideoResources);
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
