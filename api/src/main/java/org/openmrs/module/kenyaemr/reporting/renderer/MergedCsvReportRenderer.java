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

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * The regular CSV report renderer only renders the first dataset. This renderer takes an indicator report and lists
 * each column of each dataset as a new row
 */
public class MergedCsvReportRenderer implements ReportRenderer {

	/**
	 * @see ReportRenderer#canRender(org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	public boolean canRender(ReportDefinition definition) {
		return true;
	}

	/**
	 * @see ReportRenderer#getRenderingModes(org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		return Collections.singleton(new RenderingMode());
	}

	/**
	 * @see ReportRenderer#getRenderedContentType(ReportRequest)
	 */
	@Override
	public String getRenderedContentType(ReportRequest request) {
		return "text/csv";
	}

	/**
	 * @see ReportRenderer#getFilename(ReportRequest)
	 */
	@Override
	public String getFilename(ReportRequest request) {
		return "test.csv";
	}

	/**
	 * @see ReportRenderer#render(org.openmrs.module.reporting.report.ReportData, String, java.io.OutputStream)
	 */
	@Override
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		Writer w = new OutputStreamWriter(out, "UTF-8");

		for (DataSet dataSet : reportData.getDataSets().values()) {
			if (!(dataSet instanceof MapDataSet)) {
				throw new RuntimeException("Dataset must be MapDataSet");
			}

			MapDataSet ds = (MapDataSet) dataSet;

			for (DataSetColumn column : dataSet.getMetaData().getColumns()) {
				w.write(prepareVal(column.getName()));
				w.write(",");
				w.write(prepareVal(column.getLabel()));
				w.write(",");
				w.write(prepareVal(ds.getData(column)));
				w.write("\n");
			}
		}

		w.flush();
	}

	/**
	 * Convenience method used to escape a string of text
	 * @param val the value to escape
	 * @return the escaped text
	 */
	protected static String prepareVal(Object val) {
		if (val == null) {
			return null;
		}

		String text = String.valueOf(val);
		return "\"" + text.replace("\"", "\\\"") + "\"";
	}
}