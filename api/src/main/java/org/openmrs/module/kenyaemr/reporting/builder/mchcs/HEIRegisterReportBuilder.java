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

package org.openmrs.module.kenyaemr.reporting.builder.mchcs;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HEIRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
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
@Builds({"kenyaemr.mchcs.report.heiRegister"})
public class HEIRegisterReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		return allPatientsCohort();
	}

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new HEIRegisterCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("HEI All Patients");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allPatients = heiDataSetDefinition();
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

	protected PatientDataSetDefinition heiDataSetDefinition() {

		PatientDataSetDefinition dsd = new PatientDataSetDefinition("HEI Data set definition");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Patient No", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));

		// new columns
		dsd.addColumn("Serial Number", new HEISerialNumberDataDefinition(),"");
		dsd.addColumn("Enrollment Date", new HEIEnrollmentDateDataDefinition(),"");
		dsd.addColumn("HEI Id", new HEIIdDataDefinition(),"");
		dsd.addColumn("Entry Point", new HEIEntryPointDataDefinition(),"");
		dsd.addColumn("Infant Relation", new HEIRelationToInfantDataDefinition(),"");
		dsd.addColumn("Mothers Name", new HEIMotherNameDataDefinition(),"");
		dsd.addColumn("Mothers Telephone", new HEIMotherPhoneDataDefinition(),"");
		dsd.addColumn("Mothers Facility", new HEIFacilityEnrolledDataDefinition(),"");
		dsd.addColumn("Mothers CCC No.", new HEIMotherCCCNumberDataDefinition(),"");
		dsd.addColumn("Mothers PMTCT ARV", new HEIMotherOnARVDataDefinition(),"");
		dsd.addColumn("Infant Prophylaxis", new HEIInfantProphylaxisDataDefinition(),"");
		dsd.addColumn("Age at Test 0-6 wks", new HEIAgeAtTestInWeeksDataDefinition(),"");
		dsd.addColumn("Test Type 0-6 wks", new HEIPCRTestTypeDataDefinition(),"");
		dsd.addColumn("Sample Date 0-6 wks", new HEIFirstPCRSampleDateDataDefinition(),"");
		dsd.addColumn("Results Date 0-6 wks", new HEIFirstPCRResultsCollectionDateDataDefinition(),"");
		dsd.addColumn("Results Status 0-6 wks", new HEIPCRResultsStatusDataDefinition(),"");
		dsd.addColumn("Feeding option 6-8 wks", new HEIFeedingOptions6_8WeeksDataDefinition(),"");
		dsd.addColumn("Given NVP 6-8 wks", new HEIGivenNVP6_8WeeksDataDefinition(),"");
		dsd.addColumn("Given CTX 6-8 wks", new HEIGivenCTX6_8WeeksDataDefinition(),"");
		dsd.addColumn("Feeding option 10 wks", new HEIFeedingOptions10WeeksDataDefinition(),"");
		dsd.addColumn("Given NVP 10 wks", new HEIGivenNVP10WeeksDataDefinition(),"");
		dsd.addColumn("Given CTX 10 wks", new HEIGivenCTX10WeeksDataDefinition(),"");
		dsd.addColumn("Feeding option 14 wks", new HEIFeedingOptions14WeeksDataDefinition(),"");
		dsd.addColumn("Given NVP 14 wks", new HEIGivenNVP14WeeksDataDefinition(),"");
		dsd.addColumn("Given CTX 14 wks", new HEIGivenCTX14WeeksDataDefinition(),"");
		dsd.addColumn("Feeding option 6 months", new HEIFeedingOptionsMonth6DataDefinition(),"");
		dsd.addColumn("Given NVP 6 months", new HEIGivenNVPMonth6DataDefinition(),"");
		dsd.addColumn("Given CTX 6 months", new HEIGivenCTXMonth6DataDefinition(),"");
		dsd.addColumn("Age at Test 6 months", new HEIAgeAtTestInMonthsDataDefinition(),"");
		dsd.addColumn("Test Type 6 months", new HEIDNA2PCRTestTypeMonth6DataDefinition(),"");
		dsd.addColumn("Sample Date 6 months", new HEIPCRSampleDateMonth6DataDefinition(),"");
		dsd.addColumn("Results Date 6 months", new HEIPCRResultsCollectionDateMonth6DataDefinition(),"");
		dsd.addColumn("Results Status 6 months", new HEIPCRResultsStatusMonth6DataDefinition(),"");
		dsd.addColumn("Feeding option 9 months", new HEIFeedingOptionsMonth9DataDefinition(),"");
		dsd.addColumn("Given NVP 9 months", new HEIGivenNVPMonth9DataDefinition(),"");
		dsd.addColumn("Given CTX 9 months", new HEIGivenCTXMonth9DataDefinition(),"");
		dsd.addColumn("Feeding option 12 months", new HEIFeedingOptionsMonth12DataDefinition(),"");
		dsd.addColumn("Given NVP 12 months", new HEIGivenNVPMonth12DataDefinition(),"");
		dsd.addColumn("Given CTX 12 months", new HEIGivenCTXMonth12DataDefinition(),"");
		dsd.addColumn("Age at Test 12 months", new HEIAgeAtTestInMonths12DataDefinition(),"");
		dsd.addColumn("Test Type 12 months", new HEIDNA3PCRTestTypeMonth12DataDefinition(),"");
		dsd.addColumn("Sample Date 12 months", new HEIPCRSampleDateMonth12DataDefinition(),"");
		dsd.addColumn("Results Date 12 months", new HEIPCRResultsCollectionDateMonth12DataDefinition(),"");
		dsd.addColumn("Results Status 12 months", new HEIPCRResultsStatusMonth12DataDefinition(),"");
		dsd.addColumn("Feeding option 15 months", new HEIFeedingOptionsMonth15DataDefinition(),"");
		dsd.addColumn("Given NVP 15 months", new HEIGivenNVPMonth15DataDefinition(),"");
		dsd.addColumn("Given CTX 15 months", new HEIGivenCTXMonth15DataDefinition(),"");
		dsd.addColumn("Feeding option Confirmatory", new HEIFeedingOptionsConfirmatoryDataDefinition(),"");
		dsd.addColumn("Given NVP Confirmatory", new HEIGivenNVPConfirmatoryDataDefinition(),"");
		dsd.addColumn("Given CTX Confirmatory", new HEIGivenCTXConfirmatoryDataDefinition(),"");
		dsd.addColumn("Age at Test Confirmatory", new HEIAgeAtTestConfirmatoryDataDefinition(),"");
		dsd.addColumn("Test Type Confirmatory", new HEIPCRConfirmatoryTestTypeDataDefinition(),"");
		dsd.addColumn("Sample Date Confirmatory", new HEIPCRConfirmatorySampleDateDataDefinition(),"");
		dsd.addColumn("Results Date Confirmatory", new HEIPCRConfirmatoryResultDateDataDefinition(),"");
		dsd.addColumn("Results Collection Date Confirmatory", new HEIPCRConfirmatoryResultCollectionDateDataDefinition(),"");
		dsd.addColumn("Results Status confirmatory", new HEIPCRConfirmatoryResultsStatusDataDefinition(),"");
		dsd.addColumn("Feeding options 18-24 months", new HEIFeedingOptionsMonth18_24DataDefinition(),"");
		dsd.addColumn("Given NVP 18-24 months", new HEIGivenNVPMonth18_24DataDefinition(),"");
		dsd.addColumn("Given CTX 18-24 months", new HEIGivenCTXMonth18_24DataDefinition(),"");
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
