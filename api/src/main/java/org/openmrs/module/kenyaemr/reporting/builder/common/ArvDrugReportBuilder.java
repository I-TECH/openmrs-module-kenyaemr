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

		ColumnParameters children =new ColumnParameters("CP", "<15", "age=<15");
		ColumnParameters adults =new ColumnParameters("AP", "15+", "age=15+");
		ColumnParameters colTotal = new ColumnParameters("TP", "grand total", "");

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//AZT+3TC+NVP
		EmrReportingUtils.addRow(dsd, "AZT+3TC+NVP-01", "Adults Patients having (AZT+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcNvp(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+NVP-02", "Children Patients having (AZT+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcNvp(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+NVP-03", "Total Patients having (AZT+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcNvp(), indParams), Arrays.asList(colTotal));

		//AZT+3TC+EFV
		EmrReportingUtils.addRow(dsd, "AZT+3TC+EFV-01", "Adults Patients having (AZT+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcEfv(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+EFV-02", "Children Patients having (AZT+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcEfv(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+EFV-03", "Total Patients having (AZT+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcEfv(), indParams), Arrays.asList(colTotal));

		//AZT+3TC+ABC
		EmrReportingUtils.addRow(dsd, "AZT+3TC+ABC-01", "Adults Patients having (AZT+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcAbc(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+ABC-02", "Children Patients having (AZT+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcAbc(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+ABC-03", "Total Patients having (AZT+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3tcAbc(), indParams), Arrays.asList(colTotal));

		//TDF+3TC+NVP
		EmrReportingUtils.addRow(dsd, "TDF+3TC+NVP-01", "Adults Patients having (TDF+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcNvp(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+NVP-02", "Children Patients having (TDF+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcNvp(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+NVP-03", "Total Patients having (TDF+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcNvp(), indParams), Arrays.asList(colTotal));

		//TDF+3TC+EFV
		EmrReportingUtils.addRow(dsd, "TDF+3TC+EFV-01", "Adults Patients having (TDF+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcEfv(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+EFV-02", "Children Patients having (TDF+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcEfv(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+EFV-03", "Total Patients having (TDF+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcEfv(), indParams), Arrays.asList(colTotal));

		//TDF+3TC+AZT
		EmrReportingUtils.addRow(dsd, "TDF+3TC+AZT-01", "Adults Patients having (TDF+3TC+AZT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcAzt(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+AZT-02", "Children Patients having (TDF+3TC+AZT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcAzt(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+AZT-03", "Total Patients having (TDF+3TC+AZT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3tcAzt(), indParams), Arrays.asList(colTotal));

		//ABC+3TC+NVP
		EmrReportingUtils.addRow(dsd, "ABC+3TC+NVP-01", "Adults Patients having (ABC+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcNvp(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ABC+3TC+NVP-02", "Children Patients having (ABC+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcNvp(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ABC+3TC+NVP-03", "Total Patients having (ABC+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcNvp(), indParams), Arrays.asList(colTotal));

		//ABC+3TC+EFV
		EmrReportingUtils.addRow(dsd, "ABC+3TC+EFV-01", "Adults Patients having (ABC+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcEfv(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ABC+3TC+EFV-02", "Children Patients having (ABC+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcEfv(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ABC+3TC+EFV-03", "Total Patients having (ABC+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3tcEfv(), indParams), Arrays.asList(colTotal));

		//D4T+3TC+NVP
		EmrReportingUtils.addRow(dsd, "D4T+3TC+NVP-01", "Adults Patients having (D4T+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcNvp(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+NVP-02", "Children Patients having (D4T+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcNvp(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+NVP-03", "Total Patients having (D4T+3TC+NVP) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcNvp(), indParams), Arrays.asList(colTotal));

		//D4T+3TC+EFV
		EmrReportingUtils.addRow(dsd, "D4T+3TC+EFV-01", "Adults Patients having (D4T+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcEfv(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+EFV-02", "Children Patients having (D4T+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcEfv(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+EFV-03", "Total Patients having (D4T+3TC+EFV) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcEfv(), indParams), Arrays.asList(colTotal));

		//D4T+3TC+ABC
		EmrReportingUtils.addRow(dsd, "D4T+3TC+ABC-01", "Adults Patients having (D4T+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcAbc(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+ABC-02", "Children Patients having (D4T+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcAbc(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+ABC-03", "Total Patients having (D4T+3TC+ABC) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcAbc(), indParams), Arrays.asList(colTotal));

		//ABC+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+3TC+LVP+RIT-01", "Adults Patients having (ABC+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3TcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ABC+3TC+LVP+RIT-02", "Children Patients having (ABC+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3TcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ABC+3TC+LVP+RIT-03", "Total Patients having (ABC+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbc3TcLvpR(), indParams), Arrays.asList(colTotal));

		//AZT+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "AZT+3TC+LVP+RIT-01", "Adults Patients having (AZT+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3TcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+LVP+RIT-02", "Children Patients having (AZT+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3TcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "AZT+3TC+LVP+RIT-03", "Total Patients having (AZT+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAzt3TcLvpR(), indParams), Arrays.asList(colTotal));

		//TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "TDF+3TC+LVP+RIT-01", "Adults Patients having (TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3TcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+LVP+RIT-02", "Children Patients having (TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3TcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "TDF+3TC+LVP+RIT-03", "Total Patients having (TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdf3TcLvpR(), indParams), Arrays.asList(colTotal));

		//TDF+ABC+LVP/r
		EmrReportingUtils.addRow(dsd, "TDF+ABC+LVP+RIT-01", "Adults Patients having (TDF+ABC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "TDF+ABC+LVP+RIT-02", "Children Patients having (TDF+ABC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "TDF+ABC+LVP+RIT-03", "Total Patients having (TDF+ABC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcLvpR(), indParams), Arrays.asList(colTotal));

		//ABC+DDI+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+DDI+LVP+RIT-01", "Adults Patients having (ABC+DDI+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcDdiLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ABC+DDI+LVP+RIT-02", "Children Patients having (ABC+DDI+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcDdiLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ABC+DDI+LVP+RIT-03", "Total Patients having (ABC+DDI+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenTdfAbcDdiLvpR(), indParams), Arrays.asList(colTotal));

		//D4T+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "D4T+3TC+LVP+RIT-01", "Adults Patients having (D4T+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+LVP+RIT-02", "Children Patients having (D4T+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "D4T+3TC+LVP+RIT-03", "Total Patients having (D4T+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenD4t3tcLvpR(), indParams), Arrays.asList(colTotal));

		//AZT+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "AZT+TDF+3TC+LVP+RIT-01", "Adults Patients having (AZT+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAztTdf3tcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "AZT+TDF+3TC+LVP+RIT-02", "Children Patients having (AZT+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAztTdf3tcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "AZT+TDF+3TC+LVP+RIT-03", "Total Patients having (AZT+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAztTdf3tcLvpR(), indParams), Arrays.asList(colTotal));

		//ABC+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+TDF+3TC+LVP+RIT-01", "Adults Patients having (ABC+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbcTdf3tcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ABC+TDF+3TC+LVP+RIT-02", "Children Patients having (ABC+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbcTdf3tcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ABC+TDF+3TC+LVP+RIT-03", "Total Patients having (ABC+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenAbcTdf3tcLvpR(), indParams), Arrays.asList(colTotal));

		//ETR+RAL+DRV+RIT
		EmrReportingUtils.addRow(dsd, "ETR+RAL+DRV+RIT-01", "Adults Patients having (ETR+RAL+DRV+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrRalDrvRit(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ETR+RAL+DRV+RIT-02", "Children Patients having (ETR+RAL+DRV+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrRalDrvRit(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ETR+RAL+DRV+RIT-03", "Total Patients having (ETR+RAL+DRV+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrRalDrvRit(), indParams), Arrays.asList(colTotal));

		//ETR+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ETR+TDF+3TC+LVP+RIT-01", "Adults Patients having (ETR+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrTdf3tcLvpR(), indParams), Arrays.asList(adults));
		EmrReportingUtils.addRow(dsd, "ETR+TDF+3TC+LVP+RIT-02", "Children Patients having (ETR+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrTdf3tcLvpR(), indParams), Arrays.asList(children));
		EmrReportingUtils.addRow(dsd, "ETR+TDF+3TC+LVP+RIT-03", "Total Patients having (ETR+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(arvReportIndicatorLibrary.onRegimenEtrTdf3tcLvpR(), indParams), Arrays.asList(colTotal));

		return dsd;
	}

}
