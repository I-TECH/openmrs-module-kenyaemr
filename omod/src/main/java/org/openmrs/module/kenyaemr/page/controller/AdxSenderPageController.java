/**
 * The contents of this file are subject to the OpenMRS Public License Version 1.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for the specific language governing rights and limitations under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.page.controller;

import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.reporting.renderer.AdxReportSender;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Send ADX report to DHIS.
 */
@SharedPage
public class AdxSenderPageController {

    /**
     * Exports report data as the given type
     */
    public String get(@RequestParam("request") ReportRequest reportRequest,
                      PageRequest pageRequest,
                      @SpringBean ReportManager reportManager,
                      @SpringBean KenyaUiUtils kenyaUi,
                      @SpringBean ReportService reportService) throws Exception {

        ReportDefinition definition = reportRequest.getReportDefinition().getParameterizable();
        ReportDescriptor report = reportManager.getReportDescriptor(definition);

        CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

        ReportData reportData = reportService.loadReportData(reportRequest);

        return sendAdx(report, reportData);
    }

    protected String sendAdx(ReportDescriptor report, ReportData data) throws IOException {
        ReportRenderer renderer = new AdxReportSender();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            renderer.render(data, null, out);
            return "adxSent";
        } catch (RenderingException ex) {
            return "adxNotSent";
        }
    }
}