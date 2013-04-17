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

package org.openmrs.module.kenyaemr.reporting.dataset.definition.persister;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder;
import org.openmrs.module.kenyaemr.reporting.ReportManager;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * A ReportBuilder's DSDs aren't persisted in the database so aren't given primary keys or UUIDs. However we
 * define our own kind of UUID in the format "${reportbuilderclassname}:${dsdName}" which provides enough
 * information to fetch a DSD from the ReportManager
 */
@Handler(supports=DataSetDefinition.class)
public class ReportBuilderDataSetDefinitionPersister implements DataSetDefinitionPersister {

	@Autowired
	ReportManager reportManager;

	/**
	 * @see DataSetDefinitionPersister#getDefinitionByUuid(String)
	 */
	@Override
	public DataSetDefinition getDefinitionByUuid(String uuid) {
		BuilderAndDsdName builderAndDsdName = new BuilderAndDsdName(uuid);
		ReportBuilder reportBuilder = reportManager.getReportBuilder(builderAndDsdName.getBuilderClassname());

		return toDataSetDefinition(reportBuilder, builderAndDsdName.getDsdName());
	}

	/**
	 * @see DataSetDefinitionPersister#getAllDefinitions(boolean)
	 *
	 * Iterates over all report builders to get all DSDs
	 */
	@Override
	public List<DataSetDefinition> getAllDefinitions(boolean includeRetired) {
		List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();

		for (ReportBuilder reportBuilder : reportManager.getAllReportBuilders()) {
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
	public int getNumberOfDefinitions(boolean includeRetired) {
		return getAllDefinitions(includeRetired).size();
	}

	/**
	 * @see DataSetDefinitionPersister#getDefinition(Integer)
	 *
	 * Throws exception as we can't fetch a DSD by primary key
	 */
	@Override
	public DataSetDefinition getDefinition(Integer integer) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see DataSetDefinitionPersister#getDefinitions(String, boolean)
	 */
	@Override
	public List<DataSetDefinition> getDefinitions(String name, boolean exactMatchOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSetDefinition saveDefinition(DataSetDefinition dataSetDefinition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void purgeDefinition(DataSetDefinition dataSetDefinition) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a data set definition
	 * @param builder the report builder
	 * @param dsdName the DSD name
	 * @return the data set definition
	 */
	private DataSetDefinition toDataSetDefinition(ReportBuilder builder, String dsdName) {
		Mapped<? extends DataSetDefinition> mapped = builder.getReportDefinition().getDataSetDefinitions().get(dsdName);
		DataSetDefinition dataSetDefinition = mapped.getParameterizable();

		// Since the ReportBuilders always creates these on the fly, they have arbitrary UUIDs, so we set a known one.
		dataSetDefinition.setUuid(new BuilderAndDsdName(builder, dsdName).toUUID());

		return dataSetDefinition;
	}

	/**
	 * Helper class for parsing and formatting our own special "UUIDs"
	 */
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

		public String toUUID() {
			return builderClassname.replaceAll("\\.", "-") + ":" + dsdName;
		}
	}
}