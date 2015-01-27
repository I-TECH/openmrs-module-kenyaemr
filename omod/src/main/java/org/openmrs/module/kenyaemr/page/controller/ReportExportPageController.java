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

import org.apache.commons.io.FileUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.UiResource;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACohortSampleFrameDefinition;
import org.openmrs.module.kenyaemr.reporting.renderer.MergedCsvReportRenderer;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ContentType;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.FileDownload;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Download report data as Excel or CSV
 */
@SharedPage
public class ReportExportPageController {

	private static final String EXPORT_TYPE_EXCEL = "excel";
	private static final String EXPORT_TYPE_CSV = "csv";

	/**
	 * Exports report data as the given type
	 */
	public FileDownload get(@RequestParam("request") ReportRequest reportRequest,
							@RequestParam("type") String type,
					PageRequest pageRequest,
					@SpringBean ReportManager reportManager,
					@SpringBean KenyaUiUtils kenyaUi,
					@SpringBean ResourceFactory resourceFactory,
					@SpringBean ReportService reportService) throws Exception {

		ReportDefinition definition = reportRequest.getReportDefinition().getParameterizable();
		ReportDescriptor report = reportManager.getReportDescriptor(definition);

		CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

		ReportData reportData = reportService.loadReportData(reportRequest);

		if (EXPORT_TYPE_EXCEL.equals(type)) {
			return renderAsExcel(report, reportData, resourceFactory);
		}
		else if (EXPORT_TYPE_CSV.equals(type)) {
			return renderAsCsv(report, reportData);
		}
		else {
			throw new RuntimeException("Unrecognised export type: " + type);
		}
	}

	/**
	 * Renders an indicator report as Excel
	 * @param report the report
	 * @param data the evaluated report data
	 * @return the Excel file as a download
	 * @throws IOException
	 */
	protected FileDownload renderAsExcel(ReportDescriptor report,
										 ReportData data,
										 ResourceFactory resourceFactory) throws IOException {


		if (!(report instanceof IndicatorReportDescriptor) && !(report instanceof HybridReportDescriptor)) {
			throw new RuntimeException("Only indicator/hybrid reports can be rendered as Excel");
		}

		ReportDefinition definition = report.getTarget();
		UiResource template = (report instanceof IndicatorReportDescriptor) ? ((IndicatorReportDescriptor) report).getTemplate() : ((HybridReportDescriptor) report).getTemplate();

		if (template == null || !template.getPath().endsWith(".xls")) {
			throw new RuntimeException("Report doesn't specify a Excel template");
		}

		// Load report template
		byte[] templateData = loadTemplateResource(resourceFactory, template);

		ExcelTemplateRenderer renderer;
		{
			// this is a bit of a hack, copied from ExcelRendererTest in the reporting module, to avoid
			// needing to save the template and report design in the database
			ReportDesignResource resource = new ReportDesignResource();
			resource.setName("template.xls");
			resource.setContents(templateData);

			final ReportDesign design = new ReportDesign();
			design.setName(report.getName());
			design.setReportDefinition(definition);
			design.setRendererType(ExcelTemplateRenderer.class);

			if (report instanceof HybridReportDescriptor){
				Properties props = new Properties();
				String repeatingSections = ((HybridReportDescriptor) report).getRepeatingSection();
				if (repeatingSections != null){
					props.put("repeatingSections", repeatingSections);
					design.setProperties(props);
				}
			}

			design.addResource(resource);

			renderer = new ExcelTemplateRenderer() {
				public ReportDesign getDesign(String argument) {
					return design;
				}
			};
		}

		addExtraContextValues(data, data.getContext());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		renderer.render(data, null, out);

		return new FileDownload(
				getDownloadFilename(definition, data.getContext(), "xls"),
				ContentType.EXCEL.getContentType(),
				out.toByteArray()
		);
	}

	/**
	 * Adds some extra context values which can be used in Excel templates
	 * @param context the evaluation context
	 */
	protected void addExtraContextValues(ReportData data, EvaluationContext context) {
		Facility facility = new Facility(Context.getService(KenyaEmrService.class).getDefaultLocation());
		KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);
		ReportDefinition reportData = data.getDefinition();

		context.addContextValue("facility.name", facility.getTarget().getName());
		context.addContextValue("facility.code", facility.getMflCode());
		context.addContextValue("report.name", reportData.getName());

		Calendar period = new GregorianCalendar();
		period.setTime(context.containsParameter("startDate") ? (Date) context.getParameterValue("startDate") : context.getEvaluationDate());

		String evaluationDate = kenyaui.formatDate(context.getEvaluationDate());
		String evaluationTime = kenyaui.formatTime(context.getEvaluationDate());

		context.addContextValue("evaluationDate", evaluationDate);
		context.addContextValue("evaluationTime", evaluationTime);
		context.addContextValue("period.year", period.get(Calendar.YEAR));
		context.addContextValue("period.month", period.get(Calendar.MONTH));
		context.addContextValue("period.month.name", new SimpleDateFormat("MMMMM").format(period.getTime()));

		//add context values for rdqa report
		CohortDefinition definition = new RDQACohortDefinition();
		CohortDefinition allPatientsDefinition = new RDQACohortSampleFrameDefinition();

		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		EvaluatedCohort rdqaCohort = null;
		EvaluatedCohort allPatientsCohort = null;

		try {
			rdqaCohort = service.evaluate(definition, context);
			allPatientsCohort = service.evaluate(allPatientsDefinition, context);
		} catch (EvaluationException e) {
			e.printStackTrace();
		}

		if (rdqaCohort !=null ){
			Integer eligiblePatients = rdqaCohort.getMemberIds().size();
			context.addContextValue("sampleSize", eligiblePatients);
		}

		if (allPatientsCohort !=null ){
			Integer allPatients = allPatientsCohort.getMemberIds().size();
			context.addContextValue("sampleFrame", allPatients);
		}
		//calculate the time frame for art cohort analysis reports
		String reportName = reportData.getName();
		//get the number out of that name
		int reportPeriod  = 0;
		if (reportName.matches(".*\\d+.*")) {
			reportPeriod = Integer.parseInt(reportName.replaceAll("\\D+",""));
		}

		//calculate date from the report period
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(period.getTime());
		calendar.add(Calendar.MONTH, reportPeriod);

		//put the date in a variable
		String reportEndDatePeriod = kenyaui.formatDate(calendar.getTime());
		String reportStartDatePeriod = kenyaui.formatDate(period.getTime());

		//add this value to the context
		context.addContextValue("period.report.month",reportPeriod);
		context.addContextValue("period.month.name.short", new SimpleDateFormat("MMM").format(period.getTime()));
		context.addContextValue("period.day", period.get(Calendar.DATE));
		context.addContextValue("period.endDate", reportEndDatePeriod);
		context.addContextValue("period.startDate", reportStartDatePeriod);

	}


	/**
	 * Renders an indicator report as CSV
	 * @param report the report
	 * @param data the evaluated report data
	 * @return the file as a download
	 * @throws IOException
	 */
	protected FileDownload renderAsCsv(ReportDescriptor report, ReportData data) throws IOException {
		ReportRenderer renderer = (report instanceof IndicatorReportDescriptor) ? new MergedCsvReportRenderer() : new CsvReportRenderer();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		renderer.render(data, null, out);

		return new FileDownload(getDownloadFilename(report.getTarget(), data.getContext(), "csv"), ContentType.CSV.getContentType(), out.toByteArray());
	}

	/**
	 * Loads a template resource as a byte array
	 * @param resourceFactory the resource factory
	 * @param template the template resource
	 * @return the byte array
	 * @throws IOException if resource couldn't be loaded
	 */
	protected byte[] loadTemplateResource(ResourceFactory resourceFactory, UiResource template) throws IOException {
		File file = resourceFactory.getResource(template.getProvider(), "reports/" + template.getPath());
		return FileUtils.readFileToByteArray(file);
	}

	/**
	 * Gets the filename to use for downloads
	 * @param ec the evaluation context
	 * @return the filename
	 */
	public String getDownloadFilename(ReportDefinition definition, EvaluationContext ec, String extension) {
		Date date = ec.containsParameter("startDate") ? (Date) ec.getParameterValue("startDate") : ec.getEvaluationDate();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		return definition.getName() + " " + df.format(date) + "." + extension;
	}
}