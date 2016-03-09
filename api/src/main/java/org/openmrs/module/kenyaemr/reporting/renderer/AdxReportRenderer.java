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

import org.openmrs.annotation.Handler;
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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ReportRenderer that renders to the ADX format
 */
@Handler
public class AdxReportRenderer extends ReportDesignRenderer {

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
        Date reportDate = (Date) reportData.getContext().getContextValues().get("start_of_last_month");

        Writer w = new OutputStreamWriter(out, "UTF-8");
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        w.write("<adx xmlns=\"urn:ihe:qrph:adx:2015\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xsi:schemaLocation=\"urn:ihe:qrph:adx:2015 ../schema/adx_loose.xsd\"\n" +
                "exported=\"" + isoDateTimeFormat.format(new Date()) + "\">\n" +
                "<group orgUnit=\"11936\" period=\"" + isoDateFormat.format(reportDate)
                + "/P1M\" dataSet=\"" + reportData.getDefinition().getName() + "\">\n");
        for (String dsKey : reportData.getDataSets().keySet()) {
            DataSet dataset = reportData.getDataSets().get(dsKey);
            List<DataSetColumn> columns = dataset.getMetaData().getColumns();
            for (DataSetRow row : dataset) {
                for (DataSetColumn column : columns) {
                    String name = column.getName();
                    Object value = row.getColumnValue(column);
                    w.write("<dataValue dataElement=\"" + name + "\" value=\"" + value.toString() + "\"/>\n");
                }
            }
        }
        w.write("</group>\n" + "</adx>");
        w.flush();
    }
}
