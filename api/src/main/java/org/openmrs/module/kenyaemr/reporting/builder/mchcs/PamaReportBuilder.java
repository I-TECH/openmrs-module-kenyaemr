/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.mchcs;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HEIRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.PamaReportCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.AgeAtReportingDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLNextAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLStabilityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLSuppressionStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAndDOBDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAtDisbandingPairMonth24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAtTestConfirmatoryDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAtTestInMonths12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAtTestInMonths18_24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAtTestInMonthsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIAgeAtTestInWeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICommentsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIDNA2PCRTestTypeMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIDNA3PCRTestTypeMonth12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIEnrollmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIEntryPointDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptions10WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptions14WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptions6_8WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptionsConfirmatoryDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptionsMonth12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptionsMonth15DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptionsMonth18_24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptionsMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFeedingOptionsMonth9DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFinalAntiBodyResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFinalAntiBodySampleDateMonth18_24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFirstPCRResultsCollectionDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIFirstPCRSampleDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTX10WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTX14WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTX6_8WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTXConfirmatoryDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTXMonth12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTXMonth15DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTXMonth18_24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTXMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIGivenNVPCTXMonth9DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIHIVStatusMonth24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIHIVStatusOfPairMonth24DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIIdDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIInfantProphylaxisDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEILinkageToCareCCCNoDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIMotherFacilityAndCCCNumberDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIMotherOnARVDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIMothersNameAndTelephoneDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRConfirmatoryResultCollectionDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRConfirmatoryResultDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRConfirmatoryResultsStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRConfirmatorySampleDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRConfirmatoryTestTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRResultsCollectionDateMonth12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRResultsCollectionDateMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRResultsStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRResultsStatusMonth12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRResultsStatusMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRSampleDateMonth12DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRSampleDateMonth6DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRTestTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIRelationToInfantDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEISerialNumberDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverLastVLDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverLastVLDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverLastVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverNameDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverStabilityStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverSuppressionStatusDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.mchcs.report.pamaReport"})
public class PamaReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		return allPatientsCohort();
	}

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new PamaReportCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("Pama report");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allPatients = pamaDataSetDefinition();
        allPatients.addRowFilter(allPatientsCohort());
		//allPatients.addRowFilter(buildCohort(descriptor));
        DataSetDefinition allPatientsDSD = allPatients;


        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "startDate=${startDate},endDate=${endDate}")
        );
    }

	@Override
	protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class),
				new Parameter("dateBasedReporting", "", String.class)
		);
	}

	protected PatientDataSetDefinition pamaDataSetDefinition() {

		PatientDataSetDefinition dsd = new PatientDataSetDefinition("PamaReport");
		dsd.addSortCriteria("DOBAndAge", SortCriteria.SortDirection.DESC);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		AgeAtReportingDataDefinition ageAtReportingDataDefinition = new AgeAtReportingDataDefinition();
		ageAtReportingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		ETLLastVLResultDataDefinition lastVlResultDataDefinition = new ETLLastVLResultDataDefinition();
		lastVlResultDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		ETLLastVLDateDataDefinition lastVLDateDataDefinition = new ETLLastVLDateDataDefinition();
		lastVLDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		ETLStabilityDataDefinition stabilityDataDefinition = new ETLStabilityDataDefinition();
		stabilityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		ETLLastVisitDateDataDefinition lastVisitDateDataDefinition = new ETLLastVisitDateDataDefinition();
		lastVisitDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		ETLNextAppointmentDateDataDefinition nextAppointmentDateDataDefinition = new ETLNextAppointmentDateDataDefinition();
		nextAppointmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		ETLSuppressionStatusDataDefinition suppressionStatusDataDefinition = new ETLSuppressionStatusDataDefinition();
		suppressionStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		PamaCareGiverLastVLDataDefinition pamaCareGiverLastVLDataDefinition = new PamaCareGiverLastVLDataDefinition();
		pamaCareGiverLastVLDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		PamaCareGiverLastVLDateDataDefinition pamaCareGiverLastVLDateDataDefinition = new PamaCareGiverLastVLDateDataDefinition();
		pamaCareGiverLastVLDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		PamaCareGiverSuppressionStatusDataDefinition pamaCareGiverSuppressionStatusDataDefinition = new PamaCareGiverSuppressionStatusDataDefinition();
		pamaCareGiverSuppressionStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		PamaCareGiverStabilityStatusDataDefinition pamaCareGiverStabilityStatusDataDefinition = new PamaCareGiverStabilityStatusDataDefinition();
		pamaCareGiverStabilityStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		PamaCareGiverLastVisitDateDataDefinition pamaCareGiverLastVisitDateDataDefinition = new PamaCareGiverLastVisitDateDataDefinition();
		pamaCareGiverLastVisitDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Patient No", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		dsd.addColumn("Age at reporting", ageAtReportingDataDefinition, "endDate=${endDate}");

		dsd.addColumn("Last VL Result",  lastVlResultDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Last VL Date", lastVLDateDataDefinition, "", new DateConverter(DATE_FORMAT));
		dsd.addColumn("Suppression Status", suppressionStatusDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Stability Status", stabilityDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Last Visit Date", lastVisitDateDataDefinition,"endDate=${endDate}", new DateConverter(DATE_FORMAT));
		dsd.addColumn("Next Appointment Date", nextAppointmentDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));

		dsd.addColumn("Care giver name", new PamaCareGiverNameDataDefinition(), "");
		dsd.addColumn("Care Giver HIV Status", new PamaCareGiverStatusDataDefinition(), "");
		dsd.addColumn("Caregiver Last Viral Load Results", pamaCareGiverLastVLDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Caregiver Last Viral Load Date", pamaCareGiverLastVLDateDataDefinition, "endDate=${endDate}",new DateConverter(DATE_FORMAT));
		dsd.addColumn("Caregiver Suppression Status", pamaCareGiverSuppressionStatusDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Caregiver Stability Status", pamaCareGiverStabilityStatusDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Caregiver Last Visit Date", pamaCareGiverLastVisitDateDataDefinition, "endDate=${endDate}",new DateConverter(DATE_FORMAT));


		dsd.addColumn("Serial Number", new HEISerialNumberDataDefinition(),"");
		dsd.addColumn("Enrollment Date", new HEIEnrollmentDateDataDefinition(),"");
		dsd.addColumn("HEI Id", new HEIIdDataDefinition(),"");
		dsd.addColumn("Entry Point", new HEIEntryPointDataDefinition(),"");
		dsd.addColumn("Infant Relation", new HEIRelationToInfantDataDefinition(),"");
		dsd.addColumn("Mothers Name and Phone", new HEIMothersNameAndTelephoneDataDefinition(),"");
//		dsd.addColumn("Mothers Name", new HEIMotherNameDataDefinition(),"");
//		dsd.addColumn("Mothers Telephone", new HEIMotherPhoneDataDefinition(),"");
		dsd.addColumn("Mothers Facility and CCC", new HEIMotherFacilityAndCCCNumberDataDefinition(),"");
//		dsd.addColumn("Mothers Facility", new HEIFacilityEnrolledDataDefinition(),"");
//		dsd.addColumn("Mothers CCC No.", new HEIMotherCCCNumberDataDefinition(),"");
		dsd.addColumn("Mothers PMTCT ARV", new HEIMotherOnARVDataDefinition(),"");
		dsd.addColumn("Infant Prophylaxis", new HEIInfantProphylaxisDataDefinition(),"");
		dsd.addColumn("Age at Test 0-6 wks", new HEIAgeAtTestInWeeksDataDefinition(),"");
		dsd.addColumn("Test Type 0-6 wks", new HEIPCRTestTypeDataDefinition(),"");
		dsd.addColumn("Sample Date 0-6 wks", new HEIFirstPCRSampleDateDataDefinition(),"");
		dsd.addColumn("Results Date 0-6 wks", new HEIFirstPCRResultsCollectionDateDataDefinition(),"");
		dsd.addColumn("Results Status 0-6 wks", new HEIPCRResultsStatusDataDefinition(),"");
		dsd.addColumn("Feeding option 6-8 wks", new HEIFeedingOptions6_8WeeksDataDefinition(),"");
		dsd.addColumn("Given NVPCTX 6-8 wks", new HEIGivenNVPCTX6_8WeeksDataDefinition(),"");
//		dsd.addColumn("Given NVP 6-8 wks", new HEIGivenNVP6_8WeeksDataDefinition(),"");
//		dsd.addColumn("Given CTX 6-8 wks", new HEIGivenCTX6_8WeeksDataDefinition(),"");
		dsd.addColumn("Feeding option 10 wks", new HEIFeedingOptions10WeeksDataDefinition(),"");
		dsd.addColumn("Given NVPCTX 10 wks", new HEIGivenNVPCTX10WeeksDataDefinition(),"");
//		dsd.addColumn("Given NVP 10 wks", new HEIGivenNVP10WeeksDataDefinition(),"");
//		dsd.addColumn("Given CTX 10 wks", new HEIGivenCTX10WeeksDataDefinition(),"");
		dsd.addColumn("Feeding option 14 wks", new HEIFeedingOptions14WeeksDataDefinition(),"");
		dsd.addColumn("Given NVPCTX 14 wks", new HEIGivenNVPCTX14WeeksDataDefinition(),"");
//		dsd.addColumn("Given NVP 14 wks", new HEIGivenNVP14WeeksDataDefinition(),"");
//		dsd.addColumn("Given CTX 14 wks", new HEIGivenCTX14WeeksDataDefinition(),"");
		dsd.addColumn("Feeding option 6 months", new HEIFeedingOptionsMonth6DataDefinition(),"");
		dsd.addColumn("Given NVPCTX 6 months", new HEIGivenNVPCTXMonth6DataDefinition(),"");
//		dsd.addColumn("Given NVP 6 months", new HEIGivenNVPMonth6DataDefinition(),"");
//		dsd.addColumn("Given CTX 6 months", new HEIGivenCTXMonth6DataDefinition(),"");
		dsd.addColumn("Age at Test 6 months", new HEIAgeAtTestInMonthsDataDefinition(),"");
		dsd.addColumn("Test Type 6 months", new HEIDNA2PCRTestTypeMonth6DataDefinition(),"");
		dsd.addColumn("Sample Date 6 months", new HEIPCRSampleDateMonth6DataDefinition(),"");
		dsd.addColumn("Results Date 6 months", new HEIPCRResultsCollectionDateMonth6DataDefinition(),"");
		dsd.addColumn("Results Status 6 months", new HEIPCRResultsStatusMonth6DataDefinition(),"");
		dsd.addColumn("Feeding option 9 months", new HEIFeedingOptionsMonth9DataDefinition(),"");
		dsd.addColumn("Given NVPCTX 9 months", new HEIGivenNVPCTXMonth9DataDefinition(),"");
//		dsd.addColumn("Given NVP 9 months", new HEIGivenNVPMonth9DataDefinition(),"");
//		dsd.addColumn("Given CTX 9 months", new HEIGivenCTXMonth9DataDefinition(),"");
		dsd.addColumn("Feeding option 12 months", new HEIFeedingOptionsMonth12DataDefinition(),"");
		dsd.addColumn("Given NVPCTX 12 months", new HEIGivenNVPCTXMonth12DataDefinition(),"");
//		dsd.addColumn("Given NVP 12 months", new HEIGivenNVPMonth12DataDefinition(),"");
//		dsd.addColumn("Given CTX 12 months", new HEIGivenCTXMonth12DataDefinition(),"");
		dsd.addColumn("Age at Test 12 months", new HEIAgeAtTestInMonths12DataDefinition(),"");
		dsd.addColumn("Test Type 12 months", new HEIDNA3PCRTestTypeMonth12DataDefinition(),"");
		dsd.addColumn("Sample Date 12 months", new HEIPCRSampleDateMonth12DataDefinition(),"");
		dsd.addColumn("Results Date 12 months", new HEIPCRResultsCollectionDateMonth12DataDefinition(),"");
		dsd.addColumn("Results Status 12 months", new HEIPCRResultsStatusMonth12DataDefinition(),"");
		dsd.addColumn("Feeding option 15 months", new HEIFeedingOptionsMonth15DataDefinition(),"");
		dsd.addColumn("Given NVPCTX 15 months", new HEIGivenNVPCTXMonth15DataDefinition(),"");
//		dsd.addColumn("Given NVP 15 months", new HEIGivenNVPMonth15DataDefinition(),"");
//		dsd.addColumn("Given CTX 15 months", new HEIGivenCTXMonth15DataDefinition(),"");
		dsd.addColumn("Feeding option Confirmatory", new HEIFeedingOptionsConfirmatoryDataDefinition(),"");
		dsd.addColumn("Given NVPCTX Confirmatory", new HEIGivenNVPCTXConfirmatoryDataDefinition(),"");
//		dsd.addColumn("Given NVP Confirmatory", new HEIGivenNVPConfirmatoryDataDefinition(),"");
//		dsd.addColumn("Given CTX Confirmatory", new HEIGivenCTXConfirmatoryDataDefinition(),"");
		dsd.addColumn("Age at Test Confirmatory", new HEIAgeAtTestConfirmatoryDataDefinition(),"");
		dsd.addColumn("Test Type Confirmatory", new HEIPCRConfirmatoryTestTypeDataDefinition(),"");
		dsd.addColumn("Sample Date Confirmatory", new HEIPCRConfirmatorySampleDateDataDefinition(),"");
		dsd.addColumn("Results Date Confirmatory", new HEIPCRConfirmatoryResultDateDataDefinition(),"");
		dsd.addColumn("Results Collection Date Confirmatory", new HEIPCRConfirmatoryResultCollectionDateDataDefinition(),"");
		dsd.addColumn("Results Status confirmatory", new HEIPCRConfirmatoryResultsStatusDataDefinition(),"");
		dsd.addColumn("Feeding options 18-24 months", new HEIFeedingOptionsMonth18_24DataDefinition(),"");
		dsd.addColumn("Given NVPCTX 18-24 months", new HEIGivenNVPCTXMonth18_24DataDefinition(),"");
//		dsd.addColumn("Given NVP 18-24 months", new HEIGivenNVPMonth18_24DataDefinition(),"");
//		dsd.addColumn("Given CTX 18-24 months", new HEIGivenCTXMonth18_24DataDefinition(),"");
		dsd.addColumn("Antibody Test Date at 18-24 months", new HEIFinalAntiBodySampleDateMonth18_24DataDefinition(),"");
		dsd.addColumn("Age in Months ", new HEIAgeAtTestInMonths18_24DataDefinition(),"");
		dsd.addColumn("Antibody Test results at 18-24 months", new HEIFinalAntiBodyResultDataDefinition(),"");
		dsd.addColumn("HIV Status at 24 months", new HEIHIVStatusMonth24DataDefinition(),"");
		dsd.addColumn("Pair Status at 24 months", new HEIHIVStatusOfPairMonth24DataDefinition(),"");
		dsd.addColumn("Age at Disbanding pair", new HEIAgeAtDisbandingPairMonth24DataDefinition(),"");
		dsd.addColumn("HEI CCC Number", new HEILinkageToCareCCCNoDataDefinition(),"");
		dsd.addColumn("Comments", new HEICommentsDataDefinition(),"");

		return dsd;
	}
}
