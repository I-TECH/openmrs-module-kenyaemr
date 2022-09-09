/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.metadata.SecurityMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Report overview page
 */
@SharedPage
public class ReportPageController {
	private AdministrationService admService;
	public static final String KPIF_MONTHLY_REPORT = "Monthly report";
	public static final String MOH_731 = "MOH 731";

	public void get(@RequestParam("reportUuid") String reportUuid,
					@RequestParam(required = false, value = "startDate") Date startDate,
					@RequestParam("returnUrl") String returnUrl,
					PageRequest pageRequest,
					PageModel model,
					UiUtils ui,
					@SpringBean ReportManager reportManager,
					@SpringBean KenyaUiUtils kenyaUi,
					@SpringBean ReportService reportService,
					@SpringBean ReportDefinitionService definitionService) throws Exception {

		ReportDefinition definition = definitionService.getDefinitionByUuid(reportUuid);
		ReportDescriptor report = reportManager.getReportDescriptor(definition);
		admService = Context.getAdministrationService();
		CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));
		User loggedInUser = Context.getUserContext().getAuthenticatedUser();
		Set<Role> userRoles = loggedInUser.getAllRoles();
		boolean isSuperUser = loggedInUser.isSuperUser();

		boolean isIndicator = false;
		if (report instanceof IndicatorReportDescriptor || report instanceof HybridReportDescriptor)
			isIndicator = true;

		boolean excelRenderable = false;
		if (report instanceof IndicatorReportDescriptor && isIndicator && ((IndicatorReportDescriptor) report).getTemplate() != null) {
			excelRenderable = true;
		} else if (report instanceof HybridReportDescriptor && isIndicator && ((HybridReportDescriptor) report).getTemplate() != null) {
			excelRenderable = true;
		}

		ObjectNode mappingDetails = null;
		if (report.getName().equals(KPIF_MONTHLY_REPORT)){
			mappingDetails = EmrUtils.getDatasetMappingForReport(definition.getName(), admService.getGlobalProperty("kenyakeypop.adx3pmDatasetMapping"));
		}
		else if(report.getName().equals(MOH_731)){
			mappingDetails = EmrUtils.getDatasetMappingForReport(definition.getName(), admService.getGlobalProperty("kenyaemr.adxDatasetMapping"));
		}

		model.addAttribute("report", report);
		model.addAttribute("definition", definition);
		model.addAttribute("isIndicator", isIndicator);
		model.addAttribute("adxConfigured", mappingDetails != null ? true : false);
		model.addAttribute("excelRenderable", excelRenderable);
		model.addAttribute("returnUrl", returnUrl);
		model.addAttribute("period", definition.getName().replaceAll("[^0-9]", ""));




		if (isIndicator) {
			Map<String, String> startDateOptions = new LinkedHashMap<String, String>();
			SimpleDateFormat pretty = new SimpleDateFormat("MMMM yyyy");
			Date d = DateUtil.getStartOfMonth(new Date());
			for (int i = 0; i < 6; ++i) {
				d = DateUtil.getStartOfMonth(d, -1);
				startDateOptions.put(kenyaUi.formatDateParam(d), pretty.format(d));
			}

			model.addAttribute("startDateOptions", startDateOptions);
			model.addAttribute("startDateSelected", startDate != null ? kenyaUi.formatDateParam(startDate) : null);
			model.addAttribute("startDate", startDate);
		}

		SimpleDateFormat datePeriodForAll = new SimpleDateFormat("MMM-yyyy");
		String date = "";
		if(startDate != null) {
			date ="_"+ datePeriodForAll.format(startDate);
		}
		model.addAttribute("date", date);

		model.addAttribute("requests", getRequests(definition, ui, reportService));

		// Showing list of subcounties
		List<String> subCountyList = new ArrayList<String>();

		String userRole = null;
		for (Role role : userRoles) {
			if(role.getName().equalsIgnoreCase(SecurityMetadata._Role.SYSTEM_ADMIN)) {
				userRole ="System Administrator";
				break;
			}
		}
		if (isSuperUser || userRole != null) {
			subCountyList = EmrUtils.getSubCountyList();
		     }
		model.addAttribute("subCountyList", subCountyList.size() > 0 ? subCountyList : Arrays.asList("None"));
		}

		/**
		 * Gets the existing requests for the given report
		 * @param definition the report definition
		 * @param ui the UI utils
		 * @param reportService the report service
		 * @return the simplified requests
		 */
		public SimpleObject[] getRequests(ReportDefinition definition, UiUtils ui, ReportService reportService) {
			List<ReportRequest> requests = reportService.getReportRequests(definition, null, null, null);
			return ui.simplifyCollection(requests);
		}
	}