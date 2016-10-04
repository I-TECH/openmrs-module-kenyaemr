/**
 * The contents of this file are subject to the OpenMRS Public License Version 1.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for the specific language governing rights and limitations under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.reporting.renderer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ReportRenderer that renders to the ADX format
 */
@Handler
public class AdxReportSender extends ReportDesignRenderer {

    private AdministrationService administrationService;

    private LocationService locationService;

    protected static final Log log = LogFactory.getLog(AdxReportSender.class);

    //TODO: Move constants to global properties after successful pilot
    private static final String URL = "http://test.phiresearchlab.org/dhis/api/dataValueSets?dataElementIdScheme=code&orgUnitIdScheme=code";
    private static final String USERNAME = "jgitahi";
    private static final String PASSWORD = "re9439uB";

    /**
     * @see ReportRenderer#getFilename(ReportRequest)
     */
    @Override
    public String getFilename(ReportRequest request) {
        return getFilenameBase(request) + ".xml";
    }

    /**
     * @see ReportRenderer#getRenderedContentType(ReportRequest)
     */
    public String getRenderedContentType(ReportRequest request) {
        return "text/xml";
    }

    /**
     * @see ReportRenderer#render(ReportData, String, OutputStream)
     */
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {

        DateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date reportDate = (Date) reportData.getContext().getParameterValue("startDate");
        administrationService = Context.getAdministrationService();
        locationService = Context.getLocationService();

        Integer locationId = Integer.parseInt(administrationService.getGlobalProperty("kenyaemr.defaultLocation"));
        Location location = locationService.getLocation(locationId);
        String mfl = "Unknown";
        if (location != null) {
            mfl = new Facility(location).getMflCode();
        }

        String xml = "";
        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml += "<adx xmlns=\"urn:ihe:qrph:adx:2015\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xsi:schemaLocation=\"urn:ihe:qrph:adx:2015 ../schema/adx_loose.xsd\"\n" +
                "exported=\"" + isoDateTimeFormat.format(new Date()) + "\">\n";

        for (String dsKey : reportData.getDataSets().keySet()) {
            xml += "<group orgUnit=\"" + mfl + "\" period=\"" + isoDateFormat.format(reportDate)
                    + "/P1M\" dataSet=\"" + reportData.getDefinition().getName().replace(" ", "_") + "-" + dsKey + "\">\n";
            DataSet dataset = reportData.getDataSets().get(dsKey);
            List<DataSetColumn> columns = dataset.getMetaData().getColumns();
            for (DataSetRow row : dataset) {
                for (DataSetColumn column : columns) {
                    String name = column.getName();
                    Object value = row.getColumnValue(column);
                    xml += "<dataValue dataElement=\"" + name + "\" value=\"" + value.toString() + "\"/>\n";
                }
            }
            xml += "</group>\n";
        }
        xml += "</adx>\n";

        String url = URL;
        PostMethod post = new PostMethod(url);
        log.info("URL: " + url);
        try {
            StringRequestEntity requestEntity = new StringRequestEntity(xml, "application/xml+adx", "UTF-8");
            post.setRequestEntity(requestEntity);
            post.setRequestHeader("Content-type", "application/xml+adx");
            String authorization = "Basic " + Base64.encodeBase64((USERNAME + ":" + PASSWORD).getBytes());
            log.info("Authorization bytes: " + authorization);
            authorization = "Basic " + new String(Base64.encodeBase64((USERNAME + ":" + PASSWORD).getBytes()));
            log.info("Authorization string: " + authorization);
            post.addRequestHeader("Authorization", authorization);
            HttpClient httpclient = new HttpClient();

            int result = httpclient.executeMethod(post);
            log.info("result: " + result);
            if (result != 200) {
                throw new RenderingException("Could not send report. HTTP Status Code: " + result);
            }
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsString());
            log.info("Response body:: " + post.getResponseBodyAsString());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IOException: ", e);
        } finally {
            post.releaseConnection();
        }
    }
}
