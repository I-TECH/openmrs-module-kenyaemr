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
import org.springframework.stereotype.Component;

/**
 * Makes KenyaEmrUiUtils
 */
@Component
public class KenyaEmrConfigurator implements PageModelConfigurator, FragmentModelConfigurator {

	private static final MetadataConstants METADATA_CONSTANTS = new MetadataConstants();
	private static final KenyaEmrUiUtils KENYAEMR_UI_UTILS = new KenyaEmrUiUtils();

	@Override
	public void configureModel(PageContext pageContext) {
		pageContext.getModel().addAttribute("MetadataConstants", METADATA_CONSTANTS);
		pageContext.getModel().addAttribute("kenyaEmrUi", KENYAEMR_UI_UTILS);
	}

	@Override
	public void configureModel(FragmentContext fragmentContext) {
		fragmentContext.getModel().addAttribute("MetadataConstants", METADATA_CONSTANTS);
		fragmentContext.getModel().addAttribute("kenyaEmrUi", KENYAEMR_UI_UTILS);
	}
}
