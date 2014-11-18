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

package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateARV1Calculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateARV2Calculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateLastSeenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferInCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4Calculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4CountDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OriginalCohortCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PatientOutComeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.hiv.report.artCohortAnalysis.6", "kenyaemr.hiv.report.artCohortAnalysis.12", "kenyaemr.hiv.report.artCohortAnalysis.24", "kenyaemr.hiv.report.artCohortAnalysis.36"})
public class ArtCohortAnalysisReportBuilder extends AbstractCohortReportBuilder {

	@Autowired
	private ArtCohortLibrary artCohortLibrary;

	/**
	 *
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#getParameters(ReportDescriptor)
	 */
	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class)
		);
	}

	/**
	 *
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(CohortReportDescriptor, PatientDataSetDefinition)
	 */
	@Override
	protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);

		dsd.addColumn("id", new PatientIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("UPN", identifierDef, "");
		dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter());
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Original Cohort", new CalculationDataDefinition("Original Cohort", new OriginalCohortCalculation()), "", new CalculationResultConverter() );
		dsd.addColumn("TI", new CalculationDataDefinition("TI", new IsTransferInCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date TI", new CalculationDataDefinition("Date TI", new TransferInDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("TO", new CalculationDataDefinition("TO", new IsTransferOutCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date TO", new CalculationDataDefinition("Date TO", new TransferOutDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("DOE", new CalculationDataDefinition("DOE", new DateOfEnrollmentCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("ARV Start", new CalculationDataDefinition("ARV Start", new DateARV1Calculation()), "", new CalculationResultConverter());
		dsd.addColumn("ARV 2nd Line Start", new CalculationDataDefinition("ARV 2nd Line Start", new DateARV2Calculation()), "", new CalculationResultConverter());
		dsd.addColumn("Last Seen", new CalculationDataDefinition("Date Last Seen", new DateLastSeenCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("OutCome", new CalculationDataDefinition("OutCome", new PatientOutComeCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Last CD4", new CalculationDataDefinition("Last CD4", new LastCd4Calculation()), "", new CalculationResultConverter());
		dsd.addColumn("Last CD4 Date", new CalculationDataDefinition("Last CD4 Date", new LastCd4CountDateCalculation()), "", new CalculationResultConverter());
	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(CohortReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		int months = Integer.parseInt(descriptor.getId().split("\\.")[4]);
		CohortDefinition cd = artCohortLibrary.netCohortMonths(months);
		return ReportUtils.map(cd, "onDate=${endDate}");
	}
}
