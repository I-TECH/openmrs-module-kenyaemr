/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.report;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.facilityreporting.api.FacilityreportingService;
import org.openmrs.module.facilityreporting.api.models.FacilityReportDataset;
import org.openmrs.module.facilityreporting.api.restUtil.DatasetIndicatorDetails;
import org.openmrs.module.facilityreporting.api.restUtil.FacilityReporting;
import org.openmrs.module.facilityreporting.api.restUtil.ReportDatasetValueEntryMapper;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Page for viewing ADX message generated for DHIS2
 */
public class AdxViewFragmentController {

    private AdministrationService administrationService;
    private FacilityreportingService facilityreportingService;
    private final Integer MOH_731_ID = 1;
    protected final Log log = LogFactory.getLog(getClass());

    private LocationService locationService;
    public String SERVER_ADDRESS = "http://41.204.187.152:9721/api/";
    DateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public void get(@RequestParam("request") ReportRequest reportRequest,
                    @RequestParam("returnUrl") String returnUrl,
                    PageRequest pageRequest,
                    PageModel model,
                    @SpringBean ReportManager reportManager,
                    @SpringBean KenyaUiUtils kenyaUi,
                    @SpringBean ReportService reportService) throws Exception {

        ReportDefinition definition = reportRequest.getReportDefinition().getParameterizable();
        ReportDescriptor report = reportManager.getReportDescriptor(definition);
        administrationService = Context.getAdministrationService();

        //CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

        ReportData reportData = reportService.loadReportData(reportRequest);
       /* ByteArrayOutputStream outputStream = (ByteArrayOutputStream) buildXmlDocument(reportData);
        postAdxToIL(outputStream);*/

        Date reportStartDate = (Date) reportData.getContext().getParameterValue("startDate");
        Date reportEndDate = (Date) reportData.getContext().getParameterValue("endDate");

        String serverAddress = administrationService.getGlobalProperty("ilServer.address");
        model.addAttribute("endDate", isoDateFormat.format(reportEndDate));
        model.addAttribute("startDate", isoDateFormat.format(reportStartDate));
        model.addAttribute("reportRequest", reportRequest);
        model.addAttribute("adx", render(reportData));
        model.addAttribute("reportName", definition.getName());
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("serverAddress",  serverAddress != null ? serverAddress : SERVER_ADDRESS);
        model.addAttribute("serverAddressLength", SERVER_ADDRESS.length());
    }

    public String render(ReportData reportData) throws IOException {


        Date reportDate = (Date) reportData.getContext().getParameterValue("startDate");
        Date endDate = (Date) reportData.getContext().getParameterValue("endDate");
        administrationService = Context.getAdministrationService();
        facilityreportingService = Context.getService(FacilityreportingService.class);
        locationService = Context.getLocationService();

        Integer locationId = Integer.parseInt(administrationService.getGlobalProperty("kenyaemr.defaultLocation"));
        String mappingString = administrationService.getGlobalProperty("kenyaemr.adxDatasetMapping");

        Location location = locationService.getLocation(locationId);
        ObjectNode mappingDetails = EmrUtils.getDatasetMappingForReport(reportData.getDefinition().getName(), mappingString);

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

            if (datasetName == null)
                continue;

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

        for (ReportDatasetValueEntryMapper e : getFaclityReportData(MOH_731_ID, isoDateFormat.format(reportDate), isoDateFormat.format(endDate))) {

            Integer datasetId = Integer.parseInt(e.getDatasetID());
            FacilityReportDataset ds = facilityreportingService.getDatasetById(datasetId);

            w.append("\t").append("<group orgUnit=\"" + mfl + "\" period=\"" + isoDateFormat.format(reportDate)
                    + "/P1M\" dataSet=\"" + ds.getMapping() + "\">\n");
            for (DatasetIndicatorDetails row : e.getIndicators()) {
                if (row.getValue() != null && !"".equals(row.getValue()) && StringUtils.isNotEmpty(row.getValue())) {
                    String name = row.getName();
                    Object value = row.getValue();

                    w.append("\t\t").append("<dataValue dataElement=\"" + columnPrefix + "" + name + "\" value=\"" + value.toString() + "\"/>\n");

                }
            }
            w.append("</group>\n");
        }
        w.append("</adx>\n");
        //w.flush();
        return w.toString();
    }
    public SimpleObject buildXmlDocument(@RequestParam("request") ReportRequest reportRequest,
                                         @RequestParam("returnUrl") String returnUrl,
                                         @SpringBean ReportService reportService) throws ParserConfigurationException, IOException, TransformerException {

        ReportData reportData = reportService.loadReportData(reportRequest);

        administrationService = Context.getAdministrationService();
        locationService = Context.getLocationService();

        Date reportDate = (Date) reportData.getContext().getParameterValue("startDate");
        Date endDate = (Date) reportData.getContext().getParameterValue("endDate");

        Integer locationId = Integer.parseInt(administrationService.getGlobalProperty("kenyaemr.defaultLocation"));
        String mappingString = administrationService.getGlobalProperty("kenyaemr.adxDatasetMapping");
        Location location = locationService.getLocation(locationId);
        ObjectNode mappingDetails = EmrUtils.getDatasetMappingForReport(reportData.getDefinition().getName(), mappingString);
        String serverAddress = administrationService.getGlobalProperty("ilServer.address");


        String mfl = "Unknown";
        String columnPrefix = mappingDetails.get("prefix").getTextValue();

        if (location != null) {
            mfl = new Facility(location).getMflCode();
        }

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("adx");
        root.setAttribute("xmlns", "urn:ihe:qrph:adx:2015");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation", "urn:ihe:qrph:adx:2015 ../schema/adx_loose.xsd");
        root.setAttribute("exported", isoDateTimeFormat.format(new Date()));


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

            if (datasetName == null)
                continue;

            Element eDataset = document.createElement("group");
            // add group attributes
            eDataset.setAttribute("orgUnit", mfl);
            eDataset.setAttribute("period", isoDateFormat.format(reportDate).concat("/P1M"));
            eDataset.setAttribute("dataSet", datasetName);


            DataSet dataset = reportData.getDataSets().get(dsKey);
            List<DataSetColumn> columns = dataset.getMetaData().getColumns();
            for (DataSetRow row : dataset) {
                for (DataSetColumn column : columns) {
                    String name = column.getName();
                    Object value = row.getColumnValue(column);

                    // add data values
                    Element dataValue = document.createElement("dataValue");
                    dataValue.setAttribute("dataElement", columnPrefix.concat(name));
                    dataValue.setAttribute("value", value.toString());
                    eDataset.appendChild(dataValue);
                }
            }
            root.appendChild(eDataset);
        }

        // add additional MOH 731 indicators for air

        for (ReportDatasetValueEntryMapper e : getFaclityReportData(MOH_731_ID, isoDateFormat.format(reportDate), isoDateFormat.format(endDate))) {

            Integer datasetId = Integer.parseInt(e.getDatasetID());
            FacilityReportDataset ds = facilityreportingService.getDatasetById(datasetId);
            String datasetName = ds.getMapping();

            Element eDataset = document.createElement("group");
            // add group attributes
            eDataset.setAttribute("orgUnit", mfl);
            eDataset.setAttribute("period", isoDateFormat.format(reportDate).concat("/P1M"));
            eDataset.setAttribute("dataSet", datasetName);

            for (DatasetIndicatorDetails row : e.getIndicators()) {
                if (row.getValue() != null && !"".equals(row.getValue()) && StringUtils.isNotEmpty(row.getValue())) {
                    String name = row.getName();
                    Object value = row.getValue();
                    // add data values
                    Element dataValue = document.createElement("dataValue");
                    dataValue.setAttribute("dataElement", columnPrefix.concat(name));
                    dataValue.setAttribute("value", value.toString());
                    eDataset.appendChild(dataValue);

                }
            }
            root.appendChild(eDataset);
        }
        document.appendChild(root);

        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource domSource = new DOMSource(document);
        ByteArrayOutputStream out= new ByteArrayOutputStream();
        StreamResult inMemory = new StreamResult(out);

        //transformer.transform(domSource, printOut);
        transformer.transform(domSource, inMemory);
        if (serverAddress != null)
            SERVER_ADDRESS = serverAddress;

        return postAdxToIL(out, SERVER_ADDRESS);

    }

    private SimpleObject postAdxToIL(ByteArrayOutputStream outStream, String serverAddress) throws IOException {

        //System.out.println("Posting to server at: " + serverAddress);
        URL url = new URL(serverAddress);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/adx+xml");
        con.setRequestProperty("Content-Length", Integer.toString(outStream.size()));
        con.setDoOutput(true);

        DataOutputStream out = new DataOutputStream(con.getOutputStream());


        out.writeBytes(outStream.toString());


        out.flush();
        out.close();

        //Get Response
        int responseCode = con.getResponseCode();
        String httpResponse = null;

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            httpResponse = response.toString();

        }
        return SimpleObject.create("statusCode", String.valueOf(responseCode), "statusMsg", httpResponse);
    }

    private SimpleObject getDataFromFacilityReportingModule(ByteArrayOutputStream outStream, String serverAddress) throws IOException {

        //System.out.println("Posting to server at: " + serverAddress);
        URL url = new URL("http://localhost:8080/openmrs/ws/rest/v1/facilityreporting/getreportdata");
        String params = "{\"REPORTID\":\"1\",\"STARTDATE\":\"2019-01-02\",\"ENDDATE\":\"2019-01-31\",\"ADXORGUNIT\":\"10657\",\"ADXREPORTINGPERIOD\":\"2018-01-01/P1M\"}";

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Content-Length", Integer.toString(outStream.size()));
        con.setDoOutput(true);

        DataOutputStream out = new DataOutputStream(con.getOutputStream());


        out.writeBytes(outStream.toString());


        out.flush();
        out.close();

        //Get Response
        int responseCode = con.getResponseCode();
        String httpResponse = null;

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            httpResponse = response.toString();

        }
        return SimpleObject.create("statusCode", String.valueOf(responseCode), "statusMsg", httpResponse);
    }

    private HttpURLConnection getConnection(URL entries) throws InterruptedException, IOException {
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                Thread.sleep(5);
            }
            HttpURLConnection con = (HttpURLConnection)entries.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/adx+xml");
            //con.setRequestProperty("Content-Length", Integer.toString(outStream.size()));
            con.setDoOutput(true);

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {

                return con;

            } else if (con.getResponseCode() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                //return null;
                System.out.println("Timeout. Retrying");
            } else if (con.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                //return null;
                System.out.println("Server unavailable");
            } else {
                //return null;
            }


            // we did not succeed with connection (or we would have returned the connection).
            con.disconnect();
            // retry
            retry++;
            System.out.println("Failed retry " + retry + "/" );
            if (retry == 4) {
                delay = false;
            } else {
                delay = true;
            }

        } while (retry < 4);

        return null;

    }

    public SimpleObject saveOrUpdateServerAddress(@RequestParam("newUrl") String newUrl) {
        administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject("ilServer.address");

        try {
            if (gp != null) {
                gp.setPropertyValue(newUrl.trim());
                administrationService.saveGlobalProperty(gp);
            } else {
                GlobalProperty globalProperty = new GlobalProperty();
                globalProperty.setProperty("ilServer.address");
                globalProperty.setPropertyValue(newUrl.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SimpleObject.create("statusMgs", "Server address saved successfully");
    }

    protected List<ReportDatasetValueEntryMapper> getFaclityReportData(Integer reportID, String startDate, String endDate) {

        List<ReportDatasetValueEntryMapper> list = new ArrayList<ReportDatasetValueEntryMapper>();
        if (reportID != 0) {
            try {
                list = FacilityReporting.getReportDataForPeriod(reportID, startDate, endDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return list;

    }
}