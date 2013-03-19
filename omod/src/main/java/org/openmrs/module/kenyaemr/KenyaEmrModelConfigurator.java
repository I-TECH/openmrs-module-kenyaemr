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

package org.openmrs.module.kenyaemr;

import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentModelConfigurator;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageModelConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Makes utility classes available to pages and fragments in this module
 */
@Component
public class KenyaEmrModelConfigurator implements PageModelConfigurator, FragmentModelConfigurator {

	@Autowired
	KenyaEmrUiUtils kenyaEmrUiUtils;

	private static final MetadataConstants METADATA_CONSTANTS = new MetadataConstants();

	@Override
	public void configureModel(PageContext pageContext) {
		pageContext.getModel().addAttribute("MetadataConstants", METADATA_CONSTANTS);
		pageContext.getModel().addAttribute("kenyaEmrUi", kenyaEmrUiUtils);
	}

	@Override
	public void configureModel(FragmentContext fragmentContext) {
		fragmentContext.getModel().addAttribute("MetadataConstants", METADATA_CONSTANTS);
		fragmentContext.getModel().addAttribute("kenyaEmrUi", kenyaEmrUiUtils);
	}
}