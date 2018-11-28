/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.renderer;

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

import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ReportRenderer that renders to the ADX format
 */
@Handler
public class AdxReportRenderer extends ReportDesignRenderer {

    private AdministrationService administrationService;

    private LocationService locationService;

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

        Writer w = new OutputStreamWriter(out, "UTF-8");
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        w.write("<adx xmlns=\"urn:ihe:qrph:adx:2015\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xsi:schemaLocation=\"urn:ihe:qrph:adx:2015 ../schema/adx_loose.xsd\"\n" +
                "exported=\"" + isoDateTimeFormat.format(new Date()) + "\">\n");

        for (String dsKey : reportData.getDataSets().keySet()) {
            w.write("<group orgUnit=\"" + mfl + "\" period=\"" + isoDateFormat.format(reportDate)
                    + "/P1M\" dataSet=\"" + reportData.getDefinition().getName().replace(" ", "_") + "-" + dsKey + "\">\n");
            DataSet dataset = reportData.getDataSets().get(dsKey);
            List<DataSetColumn> columns = dataset.getMetaData().getColumns();
            for (DataSetRow row : dataset) {
                for (DataSetColumn column : columns) {
                    String name = column.getName();
                    Object value = row.getColumnValue(column);
                    w.write("<dataValue dataElement=\"" + name + "\" value=\"" + value.toString() + "\"/>\n");
                }
            }
            w.write("</group>\n");
        }
        w.write("</adx>\n");
        w.flush();
    }
}
