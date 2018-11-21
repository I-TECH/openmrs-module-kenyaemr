/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.reports;

import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Page for viewing ADX message generated for DHIS2
 */
public class AdxViewHomePageController {


    public void get(@RequestParam("request") ReportRequest reportRequest,
                    @RequestParam("returnUrl") String returnUrl,
                    PageRequest pageRequest,
                    PageModel model,
                    @SpringBean ReportManager reportManager,
                    @SpringBean KenyaUiUtils kenyaUi,
                    @SpringBean ReportService reportService) throws Exception {


        model.addAttribute("reportRequest", reportRequest);
        model.addAttribute("returnUrl", returnUrl);
    }
}