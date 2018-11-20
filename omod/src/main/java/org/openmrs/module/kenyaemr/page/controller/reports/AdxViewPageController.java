/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.reports;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Page for viewing ADX message generated for DHIS2
 */
public class AdxViewPageController {

	private AdministrationService administrationService;

	private LocationService locationService;

	public void get(@RequestParam("request") ReportRequest reportRequest,
					@RequestParam("returnUrl") String returnUrl,
					PageRequest pageRequest,
					PageModel model,
					@SpringBean ReportManager reportManager,
					@SpringBean KenyaUiUtils kenyaUi,
					@SpringBean ReportService reportService) throws Exception {

		ReportDefinition definition = reportRequest.getReportDefinition().getParameterizable();
		ReportDescriptor report = reportManager.getReportDescriptor(definition);

		//CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

		ReportData reportData = reportService.loadReportData(reportRequest);

		model.addAttribute("evaluationEnd", reportRequest.getEvaluateCompleteDatetime());
		model.addAttribute("evaluationStart", reportRequest.getEvaluateStartDatetime());
		model.addAttribute("startDate", definition.getParameter("startDate"));
		model.addAttribute("adx", render(reportData));
		model.addAttribute("reportName", definition.getName());
		model.addAttribute("returnUrl", returnUrl);
	}

	public String render(ReportData reportData) throws IOException {

		DateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = (Date) reportData.getContext().getParameterValue("startDate");
		administrationService = Context.getAdministrationService();
		locationService = Context.getLocationService();

		Integer locationId = Integer.parseInt(administrationService.getGlobalProperty("kenyaemr.defaultLocation"));
		Location location = locationService.getLocation(locationId);
		ObjectNode mappingDetails = gPDHISDatasetMapping(reportData.getDefinition().getName());

		String mfl = "Unknown";
		String columnPrefix = mappingDetails.get("prefix").getTextValue();

		if (location != null) {
			mfl = new Facility(location).getMflCode();
		}

		StringBuilder w = new StringBuilder();
		w.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		w.append("<adx xmlns=\"urn:ihe:qrph:adx:2015\"\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"xsi:schemaLocation=\"urn:ihe:qrph:adx:2015 ../schema/adx_loose.xsd\"\n" +
				"exported=\"" + isoDateTimeFormat.format(new Date()) + "\">\n");

		for (String dsKey : reportData.getDataSets().keySet()) {

			String datasetName = null;
			if (mappingDetails.get("datasets").getElements() != null) {
				for (Iterator<JsonNode> it = mappingDetails.get("datasets").iterator(); it.hasNext(); ) {
					ObjectNode node = (ObjectNode) it.next();
					if (node.get("name").asText().equals(dsKey)) {
						datasetName = node.get("dhisName").getTextValue();
						break;
					}
				}
			}
			mappingDetails.get("datasets").getElements();
			w.append("\t").append("<group orgUnit=\"" + mfl + "\" period=\"" + isoDateFormat.format(reportDate)
					+ "/P1M\" dataSet=\"" + datasetName + "\">\n");
			DataSet dataset = reportData.getDataSets().get(dsKey);
			List<DataSetColumn> columns = dataset.getMetaData().getColumns();
			for (DataSetRow row : dataset) {
				for (DataSetColumn column : columns) {
					String name = column.getName();
					Object value = row.getColumnValue(column);

					w.append("\t\t").append("<dataValue dataElement=\"" + columnPrefix + "" + name + "\" value=\"" + value.toString() + "\"/>\n");
				}
			}
			w.append("</group>\n");
		}
		w.append("</adx>\n");
		//w.flush();
		return w.toString();
	}

	private ObjectNode gPDHISDatasetMapping(String reportName) throws IOException {
		String mappingString = "[\n" +
				"\t{\n" +
				"\t    \"reportName\": \"DATIM Report\",\n" +
				"\t    \"prefix\":\"\",\n" +
				"\t    \"datasets\": [\n" +
				"\t        {\n" +
				"\t            \"name\": \"3\",\n" +
				"\t            \"dhisName\": \"datim2345\"\n" +
				"\t        },\n" +
				"\t        {\n" +
				"\t            \"name\": \"1\",\n" +
				"\t            \"dhisName\": \"300\"\n" +
				"\t        }\n" +
				"\t    ]\n" +
				"\t},\n" +
				"\t{\n" +
				"\t    \"reportName\": \"MOH 731 Report- Green Card\",\n" +
				"\t    \"prefix\":\"Y18_\",\n" +
				"\t    \"datasets\": [\n" +
				"\t        {\n" +
				"\t            \"name\": \"1\",\n" +
				"\t            \"dhisName\": \"HTS\"\n" +
				"\t        },\n" +
				"\t        {\n" +
				"\t            \"name\": \"2\",\n" +
				"\t            \"dhisName\": \"PMTCT\"\n" +
				"\t        },\n" +
				"\t        {\n" +
				"\t            \"name\": \"3\",\n" +
				"\t            \"dhisName\": \"Vo4KDrUFwnA\"\n" +
				"\t        }\n" +
				"\t    ]\n" +
				"\t}\n" +
				"]";

		ObjectMapper mapper = new ObjectMapper();
		ArrayNode conf = (ArrayNode) mapper.readTree(mappingString);

		for (Iterator<JsonNode> it = conf.iterator(); it.hasNext();) {
			ObjectNode node = (ObjectNode) it.next();
			if (node.get("reportName").asText().equals(reportName)) {
				return node;
			}
		}

		return null;
	}
}