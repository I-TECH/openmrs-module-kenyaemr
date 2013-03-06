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

package org.openmrs.module.kenyaemr.report;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Each ReportManager's DSDs are exposed as "${reportbuilderclassname}:${dsdName}"
 */
@Handler(supports=DataSetDefinition.class)
public class KenyaEmrDataSetDefinitionPersister implements DataSetDefinitionPersister {

	@Override
	public DataSetDefinition getDefinition(Integer integer) {
		// don't allow fetching by PK (we have none)
		return null;
	}

	@Override
	public DataSetDefinition getDefinitionByUuid(String uniqueName) {
		BuilderAndDsdName builderAndDsdName = new BuilderAndDsdName(uniqueName);
		ReportBuilder reportBuilder = ReportManager.getReportBuilder(builderAndDsdName.getBuilderClassname());
		return toDataSetDefinition(reportBuilder, builderAndDsdName.getDsdName());
	}

	@Override
	public List<DataSetDefinition> getAllDefinitions(boolean b) {
		List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();
		for (ReportBuilder reportBuilder : ReportManager.getReportBuildersByTag(null)) {
			ReportDefinition reportDefinition = reportBuilder.getReportDefinition();
			if (reportDefinition == null || reportDefinition.getDataSetDefinitions() == null) {
				continue;
			}
			for (String dsdName : reportDefinition.getDataSetDefinitions().keySet()) {
				ret.add(toDataSetDefinition(reportBuilder, dsdName));
			}
		}
		return ret;
	}

	@Override
	public int getNumberOfDefinitions(boolean b) {
		return getAllDefinitions(b).size();
	}

	@Override
	public List<DataSetDefinition> getDefinitions(String s, boolean b) {
		// don't allow searching by string
		return Collections.emptyList();
	}

	@Override
	public DataSetDefinition saveDefinition(DataSetDefinition dataSetDefinition) {
		throw new IllegalArgumentException("Not Allowed");
	}

	@Override
	public void purgeDefinition(DataSetDefinition dataSetDefinition) {
		throw new IllegalArgumentException("Not Allowed");
	}

	private DataSetDefinition toDataSetDefinition(ReportBuilder manager, String dsdName) {
		Mapped<? extends DataSetDefinition> mapped = manager.getReportDefinition().getDataSetDefinitions().get(dsdName);
		DataSetDefinition dataSetDefinition = mapped.getParameterizable();
		// since the ReportBuilder always creates these on the fly, they have arbitrary UUIDs, so we set a known one.
		dataSetDefinition.setUuid(new BuilderAndDsdName(manager, dsdName).toString());
		return dataSetDefinition;
	}

	class BuilderAndDsdName {
		private String builderClassname;
		private String dsdName;

		public BuilderAndDsdName(ReportBuilder builder, String dsdName) {
			this.builderClassname = builder.getClass().getName();
			this.dsdName = dsdName;
		}

		public BuilderAndDsdName(String s) {
			String[] split = s.split(":");
			builderClassname = split[0].replaceAll("-", ".");
			dsdName = split[1];
		}

		public String getBuilderClassname() {
			return builderClassname;
		}

		public String getDsdName() {
			return dsdName;
		}

		@Override
		public String toString() {
			return builderClassname.replaceAll("\\.", "-") + ":" + dsdName;
		}
	}
}