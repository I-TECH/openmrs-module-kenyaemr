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

import org.openmrs.Concept;
import org.openmrs.PatientIdentifierType;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.DeceasedPatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.CountyAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.SubCountyAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentARTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateLastSeenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferInCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.ValueAtDateOfOtherPatientCalculationCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.CustomDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueNumericConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.PatientProgramEnrollmentDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CustomDataConverter;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Builds({"kenyaemr.hiv.report.artCohortAnalysis.data.on.ART.cohorts"})
public class PreArtandArtClientsReportBuilder extends AbstractHybridReportBuilder {

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Autowired
	private HivCohortLibrary hivCohortLibrary;

	/**
	 *
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(org.openmrs.module.kenyacore.report.CohortReportDescriptor, org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition)
	 */
	@Override
	protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.setName("preArtArtClients");
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Identifier", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		dsd.addColumn("Marital Status", new ObsForPersonDataDefinition("Marital Status", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CIVIL_STATUS), null, null), "", new CustomDataConverter());
		dsd.addColumn("County", new CalculationDataDefinition("ARV Start Date", new CountyAddressCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Sub County/District", new CalculationDataDefinition("ARV Start Date", new SubCountyAddressCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date of Diagnosis", new ObsForPersonDataDefinition("Date of Diagnosis", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), null, null), "", new CustomDataConverter());
		dsd.addColumn("Date of enrollment to care", new CalculationDataDefinition("Date of enrollment to care", new DateOfEnrollmentCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Transfer in (TI)", new CalculationDataDefinition("Transfer in (TI)", new IsTransferInCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date Transfered in", new CalculationDataDefinition("Date Transfered in", new TransferInDateCalculation()), "", new CustomDateConverter());
		//dsd.addColumn("Current IPT status", new CalculationDataDefinition("Current IPT status", new TransferInDateCalculation()), "", new CustomDateConverter());
		//dsd.addColumn("IPT start date", new CalculationDataDefinition("IPT start date", new TransferInDateCalculation()), "", new CustomDateConverter());
		//dsd.addColumn("Current TB status", new CalculationDataDefinition("Current TB status", new TransferInDateCalculation()), "", new CustomDateConverter());
		//dsd.addColumn("Date of TB diagnosis", new CalculationDataDefinition("Date of TB diagnosis", new TransferInDateCalculation()), "", new CustomDateConverter());
		//dsd.addColumn("Anti TB start date", new CalculationDataDefinition("Anti TB start date", new TransferInDateCalculation()), "", new CustomDateConverter());
		//dsd.addColumn("Date medically eligible for ART", new CalculationDataDefinition("Date medically eligible for ART", new TransferInDateCalculation()), "", new CustomDateConverter());
		dsd.addColumn("Original/Initial ART regimen", new CalculationDataDefinition("Original/Initial ART regimen", new InitialArtRegimenCalculation()), "", new RegimenConverter());
		dsd.addColumn("Initial ART start Date", new CalculationDataDefinition("Initial ART start Date", new InitialArtStartDateCalculation()), "", new DateArtStartDateConverter());
		dsd.addColumn("Reason for ART initiation", new CalculationDataDefinition("Reason for ART initiation", new InitialArtStartDateCalculation()), "", new DateArtStartDateConverter());
		dsd.addColumn("Current ART regimen", new CalculationDataDefinition("Current ART regimen", new CurrentArtRegimenCalculation()), "", new RegimenConverter());
		dsd.addColumn("Start date for Current regimen", new CalculationDataDefinition("Start date for Current regimen", new CurrentARTStartDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date of Last Visit", new CalculationDataDefinition("Date of Last Visit", new DateLastSeenCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Initial CD4 count", new ObsForPersonDataDefinition("Initial CD4 count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsValueNumericConverter(1));
		dsd.addColumn("Date of initial CD4 Count", new ObsForPersonDataDefinition("Date of initial CD4 Count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		dsd.addColumn("Last documented CD4 count", new ObsForPersonDataDefinition("Last documented CD4 count", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsValueNumericConverter(1));
		dsd.addColumn("Date of last CD4 count", new ObsForPersonDataDefinition("Date of last CD4 count", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		//dsd.addColumn("Latest VL", new ObsForPersonDataDefinition("Latest VL", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		//dsd.addColumn("Latest VL Date", new ObsForPersonDataDefinition("Latest VL Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		//dsd.addColumn("2nd last VL", new ObsForPersonDataDefinition("2nd last VL", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		//dsd.addColumn("2nd last VL Date", new ObsForPersonDataDefinition("2nd last VL Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		//dsd.addColumn("3rd Last vL", new ObsForPersonDataDefinition("3rd Last vL", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		//dsd.addColumn("3rd last VL Date", new ObsForPersonDataDefinition("3rd last VL Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		dsd.addColumn("Last appointment date", new ObsForPersonDataDefinition("Last appointment date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
		//dsd.addColumn("Alive and on Follow-up", new ObsForPersonDataDefinition("Alive and on Follow-up", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
		//dsd.addColumn("Stopped (Interrupted)", new ObsForPersonDataDefinition("Stopped (Interrupted)", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
		//dsd.addColumn("Date stopped", new ObsForPersonDataDefinition("Date stopped", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
		dsd.addColumn("Transferred out", new CalculationDataDefinition("Transferred out", new IsTransferOutCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date Transferred out", new CalculationDataDefinition("Date Transferred out", new TransferOutDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Defaulted", new CalculationDataDefinition("Defaulted", new MissedLastAppointmentCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Lost to Follow-up", new CalculationDataDefinition("Lost to Follow-up", new LostToFollowUpCalculation()), "", new CalculationResultConverter());
		//dsd.addColumn("Date classified as LTFU", new CalculationDataDefinition("Date classified as LTFU", new LostToFollowUpCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Died", new CalculationDataDefinition("Died", new DeceasedPatientsCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date reported dead", new CalculationDataDefinition("Date reported dead", new DateOfDeathCalculation()), "", new CalculationResultConverter());
		//dsd.addColumn("Pregnancies in 2012", new CalculationDataDefinition("Pregnancies in 2012", new LostToFollowUpCalculation()), "", new CalculationResultConverter());
		//dsd.addColumn("EDD of pregnancies in 2012", new CalculationDataDefinition("EDD of pregnancies in 2012", new LostToFollowUpCalculation()), "", new CalculationResultConverter());
	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		CohortDefinition cd = hivCohortLibrary.enrolled();
        cd.setName("preArtArtClients");
		return ReportUtils.map(cd, "");
	}

}
