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
package org.openmrs.module.kenyaemr.reporting.builder.tb;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbIndicatorLibrary;
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
* New Smear-Negative PTB for 2 and 8  months registered 12-15 months earlier
*/
@Component
@Builds({"kenyaemr.tb.report.new.smear.negative.ptb.2.8.12.15"})
public class NewSmearNegativePTB2And81215ReportBuilder extends AbstractReportBuilder {

	@Autowired
	TbIndicatorLibrary tbIndicatorLibrary;

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
				ReportUtils.map(ptbSmearNegativeResults2and8Months(), "startDate=${startDate},endDate=${endDate}"),
				ReportUtils.map(extraPulmonaryTbResults2and8Months(), "startDate=${startDate},endDate=${endDate}")
		);
	}

	/**
	 * Create data set
	 * @return data set
	 */
	protected DataSetDefinition ptbSmearNegativeResults2and8Months() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("2");
		dsd.setDescription("New Smear Negative PTB");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//those indicators for 2 months
		dsd.addColumn("PTB2-01", "Results at 2 Months - Total Enrolled", ReportUtils.map(tbIndicatorLibrary.newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), indParams), "");
		dsd.addColumn("PTB2-02", "Results at 2 Months - Finalized Initial Treatment", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), indParams), "");
		dsd.addColumn("PTB2-03", "Results at 2 Months - Died", ReportUtils.map(tbIndicatorLibrary.diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), indParams), "");
		dsd.addColumn("PTB2-04", "Results at 2 Months - Absconded", ReportUtils.map(tbIndicatorLibrary.abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), indParams), "");
		dsd.addColumn("PTB2-05", "Results at 2 Months - Transferred Out", ReportUtils.map(tbIndicatorLibrary.transferredOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), indParams), "");
		dsd.addColumn("PTB2-06", "Results at 2 Months - Total Evaluated", ReportUtils.map(tbIndicatorLibrary.transferOutAbscondedDiedFinalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), indParams), "");

		//those indicators for 8 months
		dsd.addColumn("PTB8-01", "Results at 8 Months - Total Enrolled", ReportUtils.map(tbIndicatorLibrary.newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");
		dsd.addColumn("PTB8-02", "Results at 8 Months - Treatment Completed", ReportUtils.map(tbIndicatorLibrary.treatmentCompletedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");
		dsd.addColumn("PTB8-03", "Results at 8 Months - Died", ReportUtils.map(tbIndicatorLibrary.diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");
		dsd.addColumn("PTB8-04", "Results at 8 Months - Out of Control", ReportUtils.map(tbIndicatorLibrary.outOfControlNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");
		dsd.addColumn("PTB8-05", "Results at 8 Months - Transferred Out", ReportUtils.map(tbIndicatorLibrary.transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");
		dsd.addColumn("PTB8-06", "Results at 8 Months - Became Smear Positive", ReportUtils.map(tbIndicatorLibrary.becameSmearPositiveNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");
		dsd.addColumn("PTB8-07", "Results at 8 Months - Total Evaluated", ReportUtils.map(tbIndicatorLibrary.transferOutOutOfControlDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");

		return  dsd;
	}

	/**
	 * create data set
	 * @return data set
	 */
	protected DataSetDefinition extraPulmonaryTbResults2and8Months() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("8");
		dsd.setDescription("Extra Pulmonary TB");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String indParams = "startDate=${startDate},endDate=${endDate}";

		//those indicators for 2 months
		dsd.addColumn("EPTB2-01", "Results at 2 Months - Total Enrolled", ReportUtils.map(tbIndicatorLibrary.extraPulmonaryTbResultsAt2Months(), indParams), "");
		dsd.addColumn("EPTB2-02", "Results at 2 Months - Finalized Initial Treatment", ReportUtils.map(tbIndicatorLibrary.finalizedInitialTreatmentExtraPulmonaryTbResultsAt2Months(), indParams), "");
		dsd.addColumn("EPTB2-03", "Results at 2 Months - Died", ReportUtils.map(tbIndicatorLibrary.diedExtraPulmonaryTbResultsAt2Months(), indParams), "");
		dsd.addColumn("EPTB2-04", "Results at 2 Months - Absconded", ReportUtils.map(tbIndicatorLibrary.abscondedExtraPulmonaryTbResultsAt2Months(), indParams), "");
		dsd.addColumn("EPTB2-05", "Results at 2 Months - Transferred Out", ReportUtils.map(tbIndicatorLibrary.transferredOutExtraPulmonaryTbResultsAt2Months(), indParams), "");
		dsd.addColumn("EPTB2-06", "Results at 2 Months - Total Evaluated", ReportUtils.map(tbIndicatorLibrary.transferOutAbscondedDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), indParams), "");

		//those indicators for 8 months
		dsd.addColumn("EPTB8-01", "Results at 8 Months - Total Enrolled", ReportUtils.map(tbIndicatorLibrary.extraPulmonaryTbResultsAt8Months(), indParams), "");
		dsd.addColumn("EPTB8-02", "Results at 8 Months - Treatment Completed", ReportUtils.map(tbIndicatorLibrary.treatmentCompletedExtraPulmonaryTbResultsAt8Months(), indParams), "");
		dsd.addColumn("EPTB8-03", "Results at 8 Months - Died", ReportUtils.map(tbIndicatorLibrary.diedExtraPulmonaryTbResultsAt8Months(), indParams), "");
		dsd.addColumn("EPTB8-04", "Results at 8 Months - Out of Control", ReportUtils.map(tbIndicatorLibrary.outOfControlExtraPulmonaryTbResultsAt8Months(), indParams), "");
		dsd.addColumn("EPTB8-05", "Results at 8 Months - Transferred Out", ReportUtils.map(tbIndicatorLibrary.transferredOutExtraPulmonaryTbResultsAt8Months(), indParams), "");
		dsd.addColumn("EPTB8-06", "Results at 8 Months - Total Evaluated", ReportUtils.map(tbIndicatorLibrary.transferOutOutOfControlDiedCompletedTreatmentExtraPulmonaryTbResultsAt8Months(), indParams), "");

		return  dsd;
	}
}
