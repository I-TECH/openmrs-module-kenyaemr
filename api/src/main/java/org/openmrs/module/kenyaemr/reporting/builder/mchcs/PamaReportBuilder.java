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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverNextAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverStabilityStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverSuppressionStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaPairCategorizationDataDefinition;
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
		PamaCareGiverNextAppointmentDateDataDefinition pamaCareGiverNextAppointmentDateDataDefinition = new PamaCareGiverNextAppointmentDateDataDefinition();
		pamaCareGiverNextAppointmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Patient No", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		dsd.addColumn("Age at reporting", ageAtReportingDataDefinition, "endDate=${endDate}");

		dsd.addColumn("Last VL Result",  lastVlResultDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Last VL Date", lastVLDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
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
		dsd.addColumn("Caregiver Next Appointment Date", pamaCareGiverNextAppointmentDateDataDefinition, "endDate=${endDate}",new DateConverter(DATE_FORMAT));
		dsd.addColumn("Pair Categorization", new PamaPairCategorizationDataDefinition(), "");

		return dsd;
	}
}
