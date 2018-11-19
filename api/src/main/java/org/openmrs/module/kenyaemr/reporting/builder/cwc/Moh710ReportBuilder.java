/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.cwc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.moh710.Moh710IndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MOH 710 Report
 */
@Component
@Builds({"kenyaemr.mchcs.report.moh710"})
public class Moh710ReportBuilder extends AbstractReportBuilder {

	protected static final Log log = LogFactory.getLog(Moh710ReportBuilder.class);

	@Autowired
	private Moh710IndicatorLibrary moh710Indicators;

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
	@SuppressWarnings("unchecked")
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		return Arrays.asList(
				ReportUtils.map(immunizationsDataSet(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Creates the dataset for section #1: Immunizations
	 *
	 * @return the dataset
	 */
	protected DataSetDefinition immunizationsDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2");
		dsd.setDescription("MOH 710 Immunizations");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		dsd.addColumn("BCG-LT1", "Given BCG and aged less than 1 year", ReportUtils.map(moh710Indicators.givenBCGVaccineAgeLessThan1Year(), indParams), "");
		dsd.addColumn("BCG-GT1", "Given BCG and aged 1 year and above", ReportUtils.map(moh710Indicators.givenBCGVaccineAge1YearAndAbove(), indParams), "");

		dsd.addColumn("OPV-0-LT1", "Given OPV at birth and aged less than 1 year", ReportUtils.map(moh710Indicators.givenOPVAgeLessThan1Year(0), indParams), "");
		dsd.addColumn("OPV-0-GT1", "Given OPV at birth and aged 1 year and above", ReportUtils.map(moh710Indicators.givenOPVAge1YearAndAbove(0), indParams), "");

		dsd.addColumn("OPV-1-LT1", "Given OPV 1 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenOPVAgeLessThan1Year(1), indParams), "");
		dsd.addColumn("OPV-1-GT1", "Given OPV 1 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenOPVAge1YearAndAbove(1), indParams), "");
		
		dsd.addColumn("OPV-2-LT1", "Given OPV 2 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenOPVAgeLessThan1Year(2), indParams), "");
		dsd.addColumn("OPV-2-GT1", "Given OPV 2 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenOPVAge1YearAndAbove(2), indParams), "");

		dsd.addColumn("OPV-3-LT1", "Given OPV 3 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenOPVAgeLessThan1Year(3), indParams), "");
		dsd.addColumn("OPV-3-GT1", "Given OPV 3 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenOPVAge1YearAndAbove(3), indParams), "");
		
		dsd.addColumn("IPV-LT1", "Given IPV and aged less than 1 year", ReportUtils.map(moh710Indicators.givenIpvAgeLessThan1Year(), indParams), "");
		dsd.addColumn("IPV-GT1", "Given IPV and aged 1 year and above", ReportUtils.map(moh710Indicators.givenIpvAge1YearAndAbove(), indParams), "");

		dsd.addColumn("DHH-1-LT1", "Given Dpt-Hep-Hib 1 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenDptHepHibVaccineAgeLessThan1Year(1), indParams), "");
		dsd.addColumn("DHH-1-GT1", "Given Dpt-Hep-Hib 1 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenDptHepHibVaccineAge1YearAndAbove(1), indParams), "");
		
		dsd.addColumn("DHH-2-LT1", "Given Dpt-Hep-Hib 2 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenDptHepHibVaccineAgeLessThan1Year(2), indParams), "");
		dsd.addColumn("DHH-2-GT1", "Given Dpt-Hep-Hib 2 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenDptHepHibVaccineAge1YearAndAbove(2), indParams), "");

		dsd.addColumn("DHH-3-LT1", "Given Dpt-Hep-Hib 1 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenDptHepHibVaccineAgeLessThan1Year(3), indParams), "");
		dsd.addColumn("DHH-3-GT1", "Given Dpt-Hep-Hib 1 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenDptHepHibVaccineAge1YearAndAbove(3), indParams), "");

		dsd.addColumn("PNEU-1-LT1", "Given Pneumococcal 1 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenPneumococcalVaccineAgeLessThan1Year(1), indParams), "");
		dsd.addColumn("PNEU-1-GT1", "Given Pneumococcal 1 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenPneumococcalVaccineAge1YearAndAbove(1), indParams), "");
		
		dsd.addColumn("PNEU-2-LT1", "Given Pneumococcal 2 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenPneumococcalVaccineAgeLessThan1Year(2), indParams), "");
		dsd.addColumn("PNEU-2-GT1", "Given Pneumococcal 2 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenPneumococcalVaccineAge1YearAndAbove(2), indParams), "");

		dsd.addColumn("PNEU-3-LT1", "Given Pneumococcal 3 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenPneumococcalVaccineAgeLessThan1Year(3), indParams), "");
		dsd.addColumn("PNEU-3-GT1", "Given Pneumococcal 3 and aged 1 year and above", ReportUtils.map(moh710Indicators.givenPneumococcalVaccineAge1YearAndAbove(3), indParams), "");

		dsd.addColumn("ROTA-1-LT1", "Given Rota 1 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenRotaVirusVaccineAgeLessThan1Year(1), indParams), "");
		dsd.addColumn("ROTA-1-GT1", "Given Rota 1 and  1 year and above", ReportUtils.map(moh710Indicators.givenRotaVirusVaccineAge1YearAndAbove(1), indParams), "");

		dsd.addColumn("ROTA-2-LT1", "Given Rota 2 and aged less than 1 year", ReportUtils.map(moh710Indicators.givenRotaVirusVaccineAgeLessThan1Year(2), indParams), "");
		dsd.addColumn("ROTA-2-GT1", "Given Rota 2 and  1 year and above", ReportUtils.map(moh710Indicators.givenRotaVirusVaccineAge1YearAndAbove(2), indParams), "");

		dsd.addColumn("VA6M-LT1", "Given Vitamin A at 6 Months and aged less than 1 year", ReportUtils.map(moh710Indicators.givenVitAAt6MAgeLessThan1Year(), indParams), "");
		dsd.addColumn("VA6M-GT1", "Given Vitamin A at 6 Months and aged 1 year and above", ReportUtils.map(moh710Indicators.givenVitAAt6MAge1YearAndAbove(), indParams), "");

		dsd.addColumn("YF-LT1", "Given Yellow Fever vaccine and aged less than 1 year", ReportUtils.map(moh710Indicators.givenYellowFeverVaccineAgeLessThan1Year(), indParams), "");
		dsd.addColumn("YF-GT1", "Given Yellow Fever vaccine and aged 1 year and above", ReportUtils.map(moh710Indicators.givenYellowFeverVaccineAge1YearAndAbove(), indParams), "");

		dsd.addColumn("MR-1-LT1", "Given Measles-Rubella 1 vaccine and aged less than 1 year", ReportUtils.map(moh710Indicators.givenMeaslesRubellaVaccine1Age1YearAndAbove(), indParams), "");
		dsd.addColumn("MR-1-GT1", "Given Measles-Rubella 1 vaccine and aged 1 year and above", ReportUtils.map(moh710Indicators.givenMeaslesRubellaVaccine1Age1YearAndAbove(), indParams), "");

		dsd.addColumn("FIC-LT1", "Fully immunized child and aged less than 1 year", ReportUtils.map(moh710Indicators.fullyImmunizedAgeLessThan1Year(), indParams), "");
		dsd.addColumn("FIC-GT1", "Fully immunized child and aged 1 year and above", ReportUtils.map(moh710Indicators.fullyImmunizedAge1YearAndAbove(), indParams), "");

		dsd.addColumn("VA-1Y", "Vitamin A at 1years (200,000IU)", ReportUtils.map(moh710Indicators.givenVitAAt12Months(), indParams), "");
		dsd.addColumn("VA-1.5Y", "Vitamin A at 1 1/2 years(200,000 IU)", ReportUtils.map(moh710Indicators.givenVitAAt18Months(), indParams), "");
		dsd.addColumn("VA-2Y-5Y", "Vitamin A at 2 years to 5 years (200,000IU)", ReportUtils.map(moh710Indicators.givenVitAAt2To5Years(), indParams), "");
		//dsd.addColumn("VAS", "Vitamin A Supplemental Lactating Mothers(200,000 IU)", ReportUtils.map(moh710Indicators.givenVitASupplemental(), indParams), "");
		
		dsd.addColumn("MR-2-1.5Y>2Y", "Measles - Rubella 2(at 1 1/2 - 2 years)*", ReportUtils.map(moh710Indicators.givenMeaslesRubella2VaccineAge18To24Months(), indParams), "");
		dsd.addColumn("MR-2->2Y", "Measles-Rubella 2 Above 2 years", ReportUtils.map(moh710Indicators.givenMeaslesRubellaVaccine2AndAgedOver2Years(), indParams), "");
		
		dsd.addColumn("SER-<1Y", "Squint/White Eye Reflection under 1 year", ReportUtils.map(moh710Indicators.givenIpvAge1YearAndAbove(), indParams), "");

		return dsd;
	}

	
}