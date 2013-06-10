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

package org.openmrs.module.kenyaemr.page.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.moduledistro.api.ModuleDistroService;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 *
 */
@AppPage(EmrWebConstants.APP_ADMIN)
public class AdminSoftwareVersionPageController {
	
	public void controller(PageModel model,
	                       @RequestParam(value="priorVersion", required=false) String priorVersion,
	                       HttpServletRequest request) {

		model.addAttribute("priorVersion", null);
		model.addAttribute("log", null);
		if (request instanceof MultipartHttpServletRequest) {
			MultipartFile uploaded = ((MultipartHttpServletRequest) request).getFile("distributionZip");
			if (uploaded != null) {
				// write this to a known file on disk, so we can use ZipFile, since ZipInputStream is buggy
				File file = null;
				try {
					file = File.createTempFile("distribution", ".zip");
					file.deleteOnExit();
					FileUtils.copyInputStreamToFile(uploaded.getInputStream(), file);

					List<String> log = Context.getService(ModuleDistroService.class).uploadDistro(file, request.getSession().getServletContext());
					model.addAttribute("log", log);
					model.addAttribute("priorVersion", priorVersion);
				}
				catch (Exception ex) {
					StringWriter sw = new StringWriter();
					ex.printStackTrace(new PrintWriter(sw));
					model.addAttribute("log", Arrays.asList("Error", ex.getMessage(), sw.toString()));
				}
			}
		}
		String currentVersion = ModuleFactory.getModuleById("kenyaemr").getVersion();

		model.put("currentKenyaEmrVersion", currentVersion);
	}
	
}
