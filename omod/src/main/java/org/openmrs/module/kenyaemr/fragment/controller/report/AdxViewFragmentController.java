/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.report;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Page for viewing ADX message generated for DHIS2
 */
public class AdxViewFragmentController {

    private AdministrationService administrationService;

    private LocationService locationService;
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

        //CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

        ReportData reportData = reportService.loadReportData(reportRequest);
       /* ByteArrayOutputStream outputStream = (ByteArrayOutputStream) buildXmlDocument(reportData);
        postAdxToIL(outputStream);*/

        Date reportStartDate = (Date) reportData.getContext().getParameterValue("startDate");
        Date reportEndDate = (Date) reportData.getContext().getParameterValue("endDate");

        model.addAttribute("endDate", isoDateFormat.format(reportEndDate));
        model.addAttribute("startDate", isoDateFormat.format(reportStartDate));
        model.addAttribute("reportRequest", reportRequest);
        model.addAttribute("adx", render(reportData));
        model.addAttribute("reportName", definition.getName());
        model.addAttribute("returnUrl", returnUrl);
    }

    public String render(ReportData reportData) throws IOException {


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
    public SimpleObject buildXmlDocument(@RequestParam("request") ReportRequest reportRequest,
                                          @RequestParam("returnUrl") String returnUrl,
                                          @SpringBean ReportService reportService) throws ParserConfigurationException, IOException, TransformerException {

        ReportData reportData = reportService.loadReportData(reportRequest);

        administrationService = Context.getAdministrationService();
        locationService = Context.getLocationService();

        Date reportDate = (Date) reportData.getContext().getParameterValue("startDate");

        Integer locationId = Integer.parseInt(administrationService.getGlobalProperty("kenyaemr.defaultLocation"));
        Location location = locationService.getLocation(locationId);
        ObjectNode mappingDetails = gPDHISDatasetMapping(reportData.getDefinition().getName());

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
        document.appendChild(root);

        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        ByteArrayOutputStream out= new ByteArrayOutputStream();
        StreamResult printOut = new StreamResult(System.out);
        StreamResult inMemory = new StreamResult(out);

        //transformer.transform(domSource, printOut);
        transformer.transform(domSource, inMemory);
        return postAdxToIL(out);
       // return out;

    }

    private SimpleObject postAdxToIL(ByteArrayOutputStream outStream) throws IOException {
        URL url = new URL("https://webhook.site/6d64dae3-a0ff-4527-8d28-117567e76ec5");

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
        System.out.println("POST Response Code :: " + responseCode);

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
            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
        return SimpleObject.create("statusCode", String.valueOf(responseCode), "statusMsg", httpResponse);
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

        for (Iterator<JsonNode> it = conf.iterator(); it.hasNext(); ) {
            ObjectNode node = (ObjectNode) it.next();
            if (node.get("reportName").asText().equals(reportName)) {
                return node;
            }
        }

        return null;
    }
}