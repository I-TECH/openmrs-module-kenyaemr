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

package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.BaseIndicatorReportBuilder;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.artDrugs.ArvReportIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Art Drug report
 */
@Component
@Builds("kenyaemr.common.report.arvDrug")
public class ArvDrugReportBuilder extends BaseIndicatorReportBuilder{

	protected static final Log log = LogFactory.getLog(ArvDrugReportBuilder.class);

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	ArvReportIndicatorLibrary arvReportIndicatorLibrary;

	@Override
	protected List<DataSetDefinition> buildDataSets()  {
		log.debug("Setting up ARV Report definition");

			return Arrays.asList(arvDataSet());
	}

	/**
	 * Creates the dataset for arv drug report
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition arvDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("ART Drugs");
		dsd.setDescription("Groups Patients depending on ART Regimen and Age");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addDimension("age", ReportUtils.map(commonDimensions.standardAgeGroups(), "onDate=${endDate}"));

		ColumnParameters children =new ColumnParameters("CP", "Children", "age=<15");
		ColumnParameters adults =new ColumnParameters("AP", "Adults", "age=15+");
		ColumnParameters colTotal = new ColumnParameters("TP", "grand total", "");

		String indParams = "startDate=${startDate},endDate=${endDate}";

		List<ColumnParameters> allColumns = Arrays.asList(children, adults, colTotal);
		//AZT+3TC+NVP
		EmrReportingUtils.addRow(dsd, "AZT+3TC+NVP", "Patients having (AZT+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcNvp(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//AZT+3TC+EFV
		EmrReportingUtils.addRow(dsd, "AZT+3TC+EFV", "Patients having (AZT+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcEfv(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//AZT+3TC+ABC
		EmrReportingUtils.addRow(dsd, "AZT+3TC+ABC", "Patients having (AZT+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcAbc(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//TDF+3TC+NVP
		EmrReportingUtils.addRow(dsd, "TDF+3TC+NVP", "Patients having (TDF+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcNvp(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//TDF+3TC+EFV
		EmrReportingUtils.addRow(dsd, "TDF+3TC+EFV", "Patients having (TDF+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcEfv(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//TDF+3TC+AZT
		EmrReportingUtils.addRow(dsd, "TDF+3TC+AZT", "Patients having (TDF+3TC+AZT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcAzt(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ABC+3TC+NVP
		EmrReportingUtils.addRow(dsd, "ABC+3TC+NVP", "Patients having (ABC+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcNvp(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ABC+3TC+EFV
		EmrReportingUtils.addRow(dsd, "ABC+3TC+EFV", "Patients having (ABC+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcEfv(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//D4T+3TC+NVP
		EmrReportingUtils.addRow(dsd, "D4T+3TC+NVP", "Patients having (D4T+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcNvp(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//D4T+3TC+EFV
		EmrReportingUtils.addRow(dsd, "D4T+3TC+EFV", "Patients having (D4T+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcEfv(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//D4T+3TC+ABC
		EmrReportingUtils.addRow(dsd, "D4T+3TC+ABC", "Patients having (D4T+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcAbc(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ABC+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+3TC+LVP+RIT", "Patients having (ABC+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3TcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//AZT+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "AZT+3TC+LVP+RIT", "Patients having (AZT+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3TcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "TDF+3TC+LVP+RIT", "Patients having (TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3TcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//TDF+ABC+LVP/r
		EmrReportingUtils.addRow(dsd, "TDF+ABC+LVP+RIT", "Patients having (TDF+ABC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ABC+DDI+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+DDI+LVP+RIT", "Patients having (ABC+DDI+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcDdiLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//D4T+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "D4T+3TC+LVP+RIT", "Patients having (D4T+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//AZT+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "AZT+TDF+3TC+LVP+RIT", "Patients having (AZT+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAztTdf3tcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ABC+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+TDF+3TC+LVP+RIT", "Patients having (ABC+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbcTdf3tcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ETR+RAL+DRV+RIT
		EmrReportingUtils.addRow(dsd, "ETR+RAL+DRV+RIT", "Patients having (ETR+RAL+DRV+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrRalDrvRit(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		//ETR+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ETR+TDF+3TC+LVP+RIT", "Patients having (ETR+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrTdf3tcLvpR(), indParams), allColumns, Arrays.asList("01", "02", "03"));

		return dsd;
	}

}
