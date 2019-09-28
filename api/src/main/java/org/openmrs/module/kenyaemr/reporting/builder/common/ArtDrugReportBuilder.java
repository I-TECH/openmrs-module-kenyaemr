/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ART Drug monthly report
 */
@Component
@Builds({"kenyaemr.common.report.artDrug"})
public class ArtDrugReportBuilder extends AbstractReportBuilder {

	protected static final Log log = LogFactory.getLog(ArtDrugReportBuilder.class);

	@Autowired
	private CommonDimensionLibrary commonDimensions;

	@Autowired
	private ArtIndicatorLibrary artIndicators;

	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
	 */
	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class)
		);
	}

	/**
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
	 */
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		return Arrays.asList(
				ReportUtils.map(regimensDataset(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Creates the dataset for arv drug report
	 * @return the dataset
	 */
	protected DataSetDefinition regimensDataset() {
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

		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept tc3 = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept nvp = Dictionary.getConcept(Dictionary.NEVIRAPINE);
		Concept efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);
		Concept abc = Dictionary.getConcept(Dictionary.ABACAVIR);
		Concept tdf = Dictionary.getConcept(Dictionary.TENOFOVIR);
		Concept d4t = Dictionary.getConcept(Dictionary.STAVUDINE);
		Concept lvp = Dictionary.getConcept(Dictionary.LOPINAVIR);
		Concept rit = Dictionary.getConcept(Dictionary.RITONAVIR);
		Concept ddi = Dictionary.getConcept(Dictionary.DIDANOSINE);
		Concept etr = Dictionary.getConcept(Dictionary.ETRAVIRINE);
		Concept ral = Dictionary.getConcept(Dictionary.RALTEGRAVIR);
		Concept drv = Dictionary.getConcept(Dictionary.DARUNAVIR);
		Concept atv = Context.getConceptService().getConceptByUuid("71647AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		Concept dtg = Context.getConceptService().getConceptByUuid("d1fd0e18-e0b9-46ae-ac0e-0452a927a94b");

		List<ColumnParameters> allColumns = Arrays.asList(children, adults, colTotal);
		List<String> indSuffixes = Arrays.asList("CH", "AD", "TT");

		//AZT+3TC+NVP
		EmrReportingUtils.addRow(dsd, "AZT+3TC+NVP", "Patients having (AZT+3TC+NVP) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tc3, nvp)), indParams), allColumns, indSuffixes);

		//AZT+3TC+EFV
		EmrReportingUtils.addRow(dsd, "AZT+3TC+EFV", "Patients having (AZT+3TC+EFV) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tc3, efv)), indParams), allColumns, indSuffixes);

		//AZT+3TC+ABC
		EmrReportingUtils.addRow(dsd, "AZT+3TC+ABC", "Patients having (AZT+3TC+ABC) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tc3, abc)), indParams), allColumns, indSuffixes);

		//TDF+3TC+NVP
		EmrReportingUtils.addRow(dsd, "TDF+3TC+NVP", "Patients having (TDF+3TC+NVP) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, tc3, nvp)), indParams), allColumns, indSuffixes);

		//TDF+3TC+EFV
		EmrReportingUtils.addRow(dsd, "TDF+3TC+EFV", "Patients having (TDF+3TC+EFV) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, tc3, efv)), indParams), allColumns, indSuffixes);

		//TDF+3TC+AZT
		EmrReportingUtils.addRow(dsd, "TDF+3TC+AZT", "Patients having (TDF+3TC+AZT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, tc3, azt)), indParams), allColumns, indSuffixes);

		//ABC+3TC+NVP
		EmrReportingUtils.addRow(dsd, "ABC+3TC+NVP", "Patients having (ABC+3TC+NVP) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(abc, tc3, nvp)), indParams), allColumns, indSuffixes);

		//ABC+3TC+EFV
		EmrReportingUtils.addRow(dsd, "ABC+3TC+EFV", "Patients having (ABC+3TC+EFV) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(abc, tc3, efv)), indParams), allColumns, indSuffixes);

		//D4T+3TC+NVP
		EmrReportingUtils.addRow(dsd, "D4T+3TC+NVP", "Patients having (D4T+3TC+NVP) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(d4t, tc3, nvp)), indParams), allColumns, indSuffixes);

		//D4T+3TC+EFV
		EmrReportingUtils.addRow(dsd, "D4T+3TC+EFV", "Patients having (D4T+3TC+EFV) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(d4t, tc3, efv)), indParams), allColumns, indSuffixes);

		//D4T+3TC+ABC
		EmrReportingUtils.addRow(dsd, "D4T+3TC+ABC", "Patients having (D4T+3TC+ABC) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(d4t, tc3, abc)), indParams), allColumns, indSuffixes);

		//ABC+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+3TC+LVP+RIT", "Patients having (ABC+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(abc, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//AZT+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "AZT+3TC+LVP+RIT", "Patients having (AZT+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "TDF+3TC+LVP+RIT", "Patients having (TDF+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//TDF+ABC+LVP/r
		EmrReportingUtils.addRow(dsd, "TDF+ABC+LVP+RIT", "Patients having (TDF+ABC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, abc, lvp, rit)), indParams), allColumns, indSuffixes);

		//ABC+DDI+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+DDI+LVP+RIT", "Patients having (ABC+DDI+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(abc, ddi, lvp, rit)), indParams), allColumns, indSuffixes);

		//D4T+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "D4T+3TC+LVP+RIT", "Patients having (D4T+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(d4t, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//AZT+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "AZT+TDF+3TC+LVP+RIT", "Patients having (AZT+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tdf, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//ABC+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ABC+TDF+3TC+LVP+RIT", "Patients having (ABC+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(abc, tdf, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//ETR+RAL+DRV+RIT
		EmrReportingUtils.addRow(dsd, "ETR+RAL+DRV+RIT", "Patients having (ETR+RAL+DRV+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(etr, ral, drv, rit)), indParams), allColumns, indSuffixes);

		//ETR+TDF+3TC+LVP/r
		EmrReportingUtils.addRow(dsd, "ETR+TDF+3TC+LVP+RIT", "Patients having (ETR+TDF+3TC+LVP+RIT) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(etr, tdf, tc3, lvp, rit)), indParams), allColumns, indSuffixes);

		//TDF+3TC+ATV/r
		EmrReportingUtils.addRow(dsd, "TDF+3TC+ATV/r", "Patients having (TDF+3TC+ATV/r) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, tc3, atv)), indParams), allColumns, indSuffixes);

		//AZT+3TC+ATV/r
		EmrReportingUtils.addRow(dsd, "AZT+3TC+ATV/r", "Patients having (AZT+3TC+ATV/r) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tc3, atv)), indParams), allColumns, indSuffixes);

		//AZT+3TC+DTG
		EmrReportingUtils.addRow(dsd, "AZT+3TC+DTG", "Patients having (AZT+3TC+DTG) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(azt, tc3, dtg)), indParams), allColumns, indSuffixes);

		//TDF+3TC+DTG
		EmrReportingUtils.addRow(dsd, "TDF+3TC+DTG", "Patients having (TDF+3TC+DTG) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(tdf, tc3, dtg)), indParams), allColumns, indSuffixes);

		//ABC+3TC+DTG
		EmrReportingUtils.addRow(dsd, "ABC+3TC+DTG", "Patients having (ABC+3TC+DTG) regimen", ReportUtils.map(artIndicators.onRegimen(Arrays.asList(abc, tc3, dtg)), indParams), allColumns, indSuffixes);


		return dsd;
	}
}