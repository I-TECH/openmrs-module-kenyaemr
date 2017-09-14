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

package org.openmrs.module.kenyaemr.reporting.builder.rdqa;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfLastCTXCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.LastCD4OrVLResultCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientCheckOutStatusCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientProgramEnrollmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.ValueAtDateOfOtherPatientCalculationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.VisitsForAPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.WeightAtArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.CustomDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.EncounterDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueNumericConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.PatientEntryPointDataConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.PatientProgramEnrollmentConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.PatientProgramEnrollmentDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.WHOStageDataConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.WeightConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQAActiveCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.Cd4OrVLValueAndDateConverter;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLPmtctIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.rdqa.RDQAIndicatorLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.common.report.rdqaReport"})
public class RDQAReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private RDQAIndicatorLibrary rdqa;

    @Autowired
    private ETLPmtctIndicatorLibrary pmtctIndicators;

    @Autowired
    private ETLMoh731IndicatorLibrary hivIndicators;


	/**
	 *
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(org.openmrs.module.kenyacore.report.CohortReportDescriptor, org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition)
	 */
	@Override
	protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		Concept cd4Concept = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Concept weightConcept = Dictionary.getConcept(Dictionary.WEIGHT_KG);

		InitialArtStartDateCalculation artStartDate = new InitialArtStartDateCalculation();
		PatientCalculation hybridCalc = new ValueAtDateOfOtherPatientCalculationCalculation(artStartDate, cd4Concept);


		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.setName("allPatients");
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Patient No", identifierDef, "");
		dsd.addColumn("Enrollment into Program", new CalculationDataDefinition("Enrollment into Program", new PatientProgramEnrollmentCalculation()), "", new PatientProgramEnrollmentConverter());
		dsd.addColumn("Enrollment Date", new CalculationDataDefinition("Enrollment Date", new PatientProgramEnrollmentCalculation()), "", new PatientProgramEnrollmentDateConverter());
		dsd.addColumn("Entry Point", new ObsForPersonDataDefinition("Entry Point", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT), null, null), "", new PatientEntryPointDataConverter());
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));


		dsd.addColumn("Art Start Date", new CalculationDataDefinition("Art Start Date", new InitialArtStartDateCalculation()), "", new DateArtStartDateConverter());
		dsd.addColumn("Weight at Art Start", new CalculationDataDefinition("Weight at Art Start", new WeightAtArtStartDateCalculation()), "", new WeightConverter());

		dsd.addColumn("First CD4 Count", new ObsForPersonDataDefinition("First CD4 Count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsValueNumericConverter(1));
		dsd.addColumn("First CD4 Count Date", new ObsForPersonDataDefinition("First CD4 Count Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
		dsd.addColumn("Last CD4 Count", new ObsForPersonDataDefinition("Last CD4 Count", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsValueNumericConverter(1));
		dsd.addColumn("Last CD4 Count Date", new ObsForPersonDataDefinition("Last CD4 Count Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());

		dsd.addColumn("First WHO Stage", new ObsForPersonDataDefinition("First WHO Stage", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
		dsd.addColumn("First WHO Stage Date", new ObsForPersonDataDefinition("First WHO Stage Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new ObsDatetimeConverter());
		dsd.addColumn("Last WHO Stage", new ObsForPersonDataDefinition("Last WHO Stage", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
		dsd.addColumn("Last WHO Stage Date", new ObsForPersonDataDefinition("Last WHO Stage Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new ObsDatetimeConverter());

		dsd.addColumn("Current Regimen", new CalculationDataDefinition("Current Regimen", new CurrentArtRegimenCalculation()), "", new RegimenConverter());

		dsd.addColumn("Transfer In Date", new CalculationDataDefinition("Transfer In Date", new TransferInDateCalculation()), "", new CustomDateConverter());
		dsd.addColumn("Transfer Out Date", new CalculationDataDefinition("Transfer Out Date", new TransferOutDateCalculation()), "", new CustomDateConverter());
		dsd.addColumn("Date of Death", new CalculationDataDefinition("Date of Death", new DateOfDeathCalculation()), "", new CustomDateConverter());

		dsd.addColumn("CTX/Dapsone last, documentation date", new CalculationDataDefinition("CTX/Dapsone last, documentation date", new DateOfLastCTXCalculation()), "", new RDQACalculationResultConverter());

		EncountersForPatientDataDefinition definition = new EncountersForPatientDataDefinition();
		EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
		EncounterType hivEnrollment = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
		EncounterType consultation = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CONSULTATION);

		List<EncounterType> encounterTypes = Arrays.asList(hivConsultation, consultation, hivEnrollment);

		definition.setWhich(TimeQualifier.LAST);
		definition.setTypes(encounterTypes);
		dsd.addColumn("Last encounter date in the blue card", definition, "", new EncounterDatetimeConverter());

		dsd.addColumn("Next Appointment Date", new ObsForPersonDataDefinition("Next Appointment Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
		dsd.addColumn("Number of visits in paper bluecards", new CalculationDataDefinition("Total Visits", new VisitsForAPatientCalculation()), "", new DataConverter() {
			@Override
			public Class<?> getInputDataType() {
				return Integer.class;
			}

			@Override
			public Class<?> getDataType() {
				return Integer.class;
			}

			@Override
			public Object convert(Object input) {
				return input;
			}
		});

		dsd.addColumn("Patient checked-out", new CalculationDataDefinition("Checked Out", new PatientCheckOutStatusCalculation()), "", new RDQACalculationResultConverter());
	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		CohortDefinition cd = new RDQACohortDefinition();
        cd.setName("RDQA All Patients");
		return ReportUtils.map(cd, "");
	}

    protected Mapped<CohortDefinition> activePatientsCohort() {
        CohortDefinition cd = new RDQAActiveCohortDefinition();
        cd.setName("RDQA Active Patients");
        return ReportUtils.map(cd, "");
    }

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new RDQACohortDefinition();
        cd.setName("RDQA All Patients");
        return ReportUtils.map(cd, "");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allPatients = rdqaDataSetDefinition("allPatients");
        allPatients.addRowFilter(allPatientsCohort());
        DataSetDefinition allPatientsDSD = allPatients;

        PatientDataSetDefinition activePatients = rdqaActiveDataSetDefinition("activePatients");
        activePatients.addRowFilter(activePatientsCohort());
        DataSetDefinition activePatientsDSD = activePatients;

        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, ""),
                ReportUtils.map(activePatientsDSD, ""),
                ReportUtils.map(careAndTreatmentDataSet(), "")
        );
    }

    protected PatientDataSetDefinition rdqaDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        Concept cd4Concept = Dictionary.getConcept(Dictionary.CD4_COUNT);
        Concept weightConcept = Dictionary.getConcept(Dictionary.WEIGHT_KG);

        InitialArtStartDateCalculation artStartDate = new InitialArtStartDateCalculation();
        PatientCalculation hybridCalc = new ValueAtDateOfOtherPatientCalculationCalculation(artStartDate, cd4Concept);


        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Unique Patient No", identifierDef, "");
        dsd.addColumn("Enrollment Date", new CalculationDataDefinition("Enrollment Date", new PatientProgramEnrollmentCalculation()), "", new PatientProgramEnrollmentDateConverter());
        dsd.addColumn("Entry Point", new ObsForPersonDataDefinition("Entry Point", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT), null, null), "", new PatientEntryPointDataConverter());
        dsd.addColumn("Date confirmed positive", new ObsForPersonDataDefinition("Date Confirmed Positive", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));


        dsd.addColumn("Art Start Date", new CalculationDataDefinition("Art Start Date", new InitialArtStartDateCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Weight at Art Start", new CalculationDataDefinition("Weight at Art Start", new WeightAtArtStartDateCalculation()), "", new WeightConverter());

        dsd.addColumn("First CD4 Count", new ObsForPersonDataDefinition("First CD4 Count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsValueNumericConverter(1));
        dsd.addColumn("First CD4 Count Date", new ObsForPersonDataDefinition("First CD4 Count Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Last CD4 Count", new CalculationDataDefinition("Last CD4 Count", new LastCD4OrVLResultCalculation()), "", new Cd4OrVLValueAndDateConverter("value"));
        dsd.addColumn("Last CD4 Count Date", new CalculationDataDefinition("Last CD4 Count Date", new LastCD4OrVLResultCalculation()), "", new Cd4OrVLValueAndDateConverter("date"));

        dsd.addColumn("First WHO Stage", new ObsForPersonDataDefinition("First WHO Stage", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
        dsd.addColumn("First WHO Stage Date", new ObsForPersonDataDefinition("First WHO Stage Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Last WHO Stage", new ObsForPersonDataDefinition("Last WHO Stage", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
        dsd.addColumn("Last WHO Stage Date", new ObsForPersonDataDefinition("Last WHO Stage Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new ObsDatetimeConverter());

        dsd.addColumn("Current Regimen", new CalculationDataDefinition("Current Regimen", new CurrentArtRegimenCalculation()), "", new RegimenConverter());

        dsd.addColumn("Transfer In Date", new CalculationDataDefinition("Transfer In Date", new TransferInDateCalculation()), "", new CustomDateConverter());
        dsd.addColumn("Transfer Out Date", new CalculationDataDefinition("Transfer Out Date", new TransferOutDateCalculation()), "", new CustomDateConverter());
        dsd.addColumn("Date of Death", new CalculationDataDefinition("Date of Death", new DateOfDeathCalculation()), "", new CustomDateConverter());

        dsd.addColumn("CTX/Dapsone last, documentation date", new CalculationDataDefinition("CTX/Dapsone last, documentation date", new DateOfLastCTXCalculation()), "", new RDQACalculationResultConverter());

        EncountersForPatientDataDefinition definition = new EncountersForPatientDataDefinition();
        EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
        EncounterType hivEnrollment = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
        EncounterType consultation = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CONSULTATION);

        List<EncounterType> encounterTypes = Arrays.asList(hivConsultation, consultation, hivEnrollment);

        definition.setWhich(TimeQualifier.LAST);
        definition.setTypes(encounterTypes);
        dsd.addColumn("Last encounter date in the blue card", definition, "", new EncounterDatetimeConverter());

        dsd.addColumn("Next Appointment Date", new ObsForPersonDataDefinition("Next Appointment Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
        dsd.addColumn("Number of visits in paper bluecards", new CalculationDataDefinition("Total Visits", new VisitsForAPatientCalculation()), "", new DataConverter() {
            @Override
            public Class<?> getInputDataType() {
                return Integer.class;
            }

            @Override
            public Class<?> getDataType() {
                return Integer.class;
            }

            @Override
            public Object convert(Object input) {
                return input;
            }
        });

        dsd.addColumn("Patient checked-out", new CalculationDataDefinition("Checked Out", new PatientCheckOutStatusCalculation()), "", new RDQACalculationResultConverter());
        return dsd;
    }

    protected PatientDataSetDefinition rdqaActiveDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);

        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));


        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        Concept cd4Concept = Dictionary.getConcept(Dictionary.CD4_COUNT);
        Concept weightConcept = Dictionary.getConcept(Dictionary.WEIGHT_KG);

        InitialArtStartDateCalculation artStartDate = new InitialArtStartDateCalculation();
        PatientCalculation hybridCalc = new ValueAtDateOfOtherPatientCalculationCalculation(artStartDate, cd4Concept);


        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Unique Patient No", identifierDef, "");
        dsd.addColumn("Enrollment Date", new CalculationDataDefinition("Enrollment Date", new PatientProgramEnrollmentCalculation()), "", new PatientProgramEnrollmentDateConverter());
        dsd.addColumn("Entry Point", new ObsForPersonDataDefinition("Entry Point", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT), null, null), "", new PatientEntryPointDataConverter());
        dsd.addColumn("Date confirmed positive", new ObsForPersonDataDefinition("Date Confirmed Positive", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));


        dsd.addColumn("Art Start Date", new CalculationDataDefinition("Art Start Date", new InitialArtStartDateCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Weight at Art Start", new CalculationDataDefinition("Weight at Art Start", new WeightAtArtStartDateCalculation()), "", new WeightConverter());

        dsd.addColumn("First CD4 Count", new ObsForPersonDataDefinition("First CD4 Count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsValueNumericConverter(1));
        dsd.addColumn("First CD4 Count Date", new ObsForPersonDataDefinition("First CD4 Count Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Last CD4 Count", new CalculationDataDefinition("Last CD4 Count", new LastCD4OrVLResultCalculation()), "", new Cd4OrVLValueAndDateConverter("value"));
        dsd.addColumn("Last CD4 Count Date", new CalculationDataDefinition("Last CD4 Count Date", new LastCD4OrVLResultCalculation()), "", new Cd4OrVLValueAndDateConverter("date"));

        dsd.addColumn("First Viral Load Result", new ObsForPersonDataDefinition("First Viral Load Result", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null), "", new ObsValueNumericConverter(1));
        dsd.addColumn("First Viral Load Result Date", new ObsForPersonDataDefinition("First Viral Load Result Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Recent Viral Load Result", new ObsForPersonDataDefinition("Recent Viral Load Result", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null), "", new ObsValueNumericConverter(1));
        dsd.addColumn("Recent Viral Load Result Date", new ObsForPersonDataDefinition("Recent Viral Load Result Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null), "", new ObsDatetimeConverter());

        dsd.addColumn("First WHO Stage", new ObsForPersonDataDefinition("First WHO Stage", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
        dsd.addColumn("First WHO Stage Date", new ObsForPersonDataDefinition("First WHO Stage Date", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Last WHO Stage", new ObsForPersonDataDefinition("Last WHO Stage", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
        dsd.addColumn("Last WHO Stage Date", new ObsForPersonDataDefinition("Last WHO Stage Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new ObsDatetimeConverter());

        dsd.addColumn("Current Regimen", new CalculationDataDefinition("Current Regimen", new CurrentArtRegimenCalculation()), "", new RegimenConverter());

        dsd.addColumn("Transfer In Date", new CalculationDataDefinition("Transfer In Date", new TransferInDateCalculation()), "", new CustomDateConverter());
        dsd.addColumn("CTX/Dapsone last, documentation date", new CalculationDataDefinition("CTX/Dapsone last, documentation date", new DateOfLastCTXCalculation()), "", new RDQACalculationResultConverter());

        EncountersForPatientDataDefinition definition = new EncountersForPatientDataDefinition();
        EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
        EncounterType hivEnrollment = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
        EncounterType consultation = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CONSULTATION);

        List<EncounterType> encounterTypes = Arrays.asList(hivConsultation, consultation, hivEnrollment);

        definition.setWhich(TimeQualifier.LAST);
        definition.setTypes(encounterTypes);
        dsd.addColumn("Last encounter date in the blue card", definition, "", new EncounterDatetimeConverter());

        dsd.addColumn("Next Appointment Date", new ObsForPersonDataDefinition("Next Appointment Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
        dsd.addColumn("Number of visits in paper bluecards", new CalculationDataDefinition("Total Visits", new VisitsForAPatientCalculation()), "", new DataConverter() {
            @Override
            public Class<?> getInputDataType() {
                return Integer.class;
            }

            @Override
            public Class<?> getDataType() {
                return Integer.class;
            }

            @Override
            public Object convert(Object input) {
                return input;
            }
        });

        dsd.addColumn("Patient checked-out", new CalculationDataDefinition("Checked Out", new PatientCheckOutStatusCalculation()), "", new RDQACalculationResultConverter());
        return dsd;
    }

    protected DataSetDefinition careAndTreatmentDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("cohortIndicator");

        String indParams = "";

        cohortDsd.addColumn("ctx", "On CTX Prophylaxis", ReportUtils.map(rdqa.patientsOnCTX(), indParams), "");
        cohortDsd.addColumn("newOncare", "New on care", ReportUtils.map(rdqa.enrolledInCare(), indParams), "");
        cohortDsd.addColumn("currentInCare", "Currently in care", ReportUtils.map(rdqa.currentInCare(), indParams), "");
        cohortDsd.addColumn("currentOnART", "Currently on ART", ReportUtils.map(rdqa.currentOnART(), indParams), "");
        cohortDsd.addColumn("cumulativeOnART", "Cumulative ever on ART", ReportUtils.map(rdqa.cumulativeOnART(), indParams), "");
        cohortDsd.addColumn("screenedForTB", "Screened for TB", ReportUtils.map(rdqa.screenedForTB(), indParams), "");
        cohortDsd.addColumn("knownPositives", "Total with known status", ReportUtils.map(rdqa.knownPositives(), indParams), "");
        cohortDsd.addColumn("sampleFrame", "Total Patients", ReportUtils.map(rdqa.sampleFrame(), indParams), "");
        cohortDsd.addColumn("sampleSize", "RDQA Patients", ReportUtils.map(rdqa.sampleSize(), indParams), "");

        return cohortDsd;
    }

}
