/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.mchms;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.CountyAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.MaternityRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KenyaEMRMaritalStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.*;
import org.openmrs.module.kenyaemr.reporting.library.pmtct.PMTCTIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
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
@Builds({"kenyaemr.mchcs.report.maternityRegister"})
public class MaternityRegisterReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Autowired
	PMTCTIndicatorLibrary pmtctMATIndicators;
	@Autowired
	private CommonDimensionLibrary commonDimensions;
	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {dsd.setName("maternityRegister");
		return allPatientsCohort();
	}

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new MaternityRegisterCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allPatients = maternityDataSetDefinition();
		allPatients.addRowFilter(allPatientsCohort());
        DataSetDefinition allPatientsDSD = allPatients;


        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "startDate=${startDate},endDate=${endDate}"),
				ReportUtils.map(maternityDataSet(), "startDate=${startDate},endDate=${endDate}")
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

	protected PatientDataSetDefinition maternityDataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition("maternityAllClients");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");

        String paramMapping = "startDate=${startDate},endDate=${endDate}";

		MaternityAdmissionNumberDataDefinition maternityAdmissionNumberDataDefinition = new MaternityAdmissionNumberDataDefinition();
		maternityAdmissionNumberDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityAdmissionNumberDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityAdmissionDateDataDefinition maternityAdmissionDateDataDefinition = new MaternityAdmissionDateDataDefinition();
		maternityAdmissionDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityAdmissionDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityNumberOfANCVisitsDataDefinition maternityNumberOfANCVisitsDataDefinition = new MaternityNumberOfANCVisitsDataDefinition();
		maternityNumberOfANCVisitsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityNumberOfANCVisitsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		KenyaEMRMaritalStatusDataDefinition kenyaEMRMaritalStatusDataDefinition = new KenyaEMRMaritalStatusDataDefinition();
		kenyaEMRMaritalStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		kenyaEMRMaritalStatusDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityANCParityDataDefinition maternityANCParityDataDefinition = new MaternityANCParityDataDefinition();
		maternityANCParityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityANCParityDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityGravidaDataDefinition maternityGravidaDataDefinition = new MaternityGravidaDataDefinition();
		maternityGravidaDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityGravidaDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityLMPDateDataDefinition maternityLMPDateDataDefinition = new MaternityLMPDateDataDefinition();
		maternityLMPDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityLMPDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityEDDUltrasoundDateDataDefinition maternityEDDUltrasoundDateDataDefinition = new MaternityEDDUltrasoundDateDataDefinition();
		maternityEDDUltrasoundDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityEDDUltrasoundDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDiagnosisDataDefinition maternityDiagnosisDataDefinition = new MaternityDiagnosisDataDefinition();
		maternityDiagnosisDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDiagnosisDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDurationOfLabourDataDefinition maternityDurationOfLabourDataDefinition = new MaternityDurationOfLabourDataDefinition();
		maternityDurationOfLabourDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDurationOfLabourDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDeliveryDateDataDefinition maternityDeliveryDateDataDefinition = new MaternityDeliveryDateDataDefinition();
		maternityDeliveryDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDeliveryDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDeliveryTimeDataDefinition maternityDeliveryTimeDataDefinition = new MaternityDeliveryTimeDataDefinition();
		maternityDeliveryTimeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDeliveryTimeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityGestationAtBirthDataDefinition maternityGestationAtBirthDataDefinition = new MaternityGestationAtBirthDataDefinition();
		maternityGestationAtBirthDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityGestationAtBirthDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDeliveryModeDataDefinition maternityDeliveryModeDataDefinition = new MaternityDeliveryModeDataDefinition();
		maternityDeliveryModeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDeliveryModeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityNumberOfBabieDeliveredDataDefinition maternityNumberOfBabiesDelivered = new MaternityNumberOfBabieDeliveredDataDefinition();
		maternityNumberOfBabiesDelivered.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityNumberOfBabiesDelivered.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityPlacentaCompleteDataDefinition maternityPlacentaCompleteDataDefinition  = new MaternityPlacentaCompleteDataDefinition();
		maternityPlacentaCompleteDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityPlacentaCompleteDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityUterotonicGivenDataDefinition maternityUterotonicGivenDataDefinition = new MaternityUterotonicGivenDataDefinition();
		maternityUterotonicGivenDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityUterotonicGivenDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityVaginalExaminationDataDefinition maternityVaginalExaminationDataDefinition = new MaternityVaginalExaminationDataDefinition();
		maternityVaginalExaminationDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityVaginalExaminationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityBloodLossDataDefinition maternityBloodLossDataDefinition = new MaternityBloodLossDataDefinition();
		maternityBloodLossDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityBloodLossDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityConditionAfterDeliveryDataDefinition maternityConditionAfterDeliveryDataDefinition  = new MaternityConditionAfterDeliveryDataDefinition();
		maternityConditionAfterDeliveryDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityConditionAfterDeliveryDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDeathAuditedDataDefinition maternityDeathAuditedDataDefinition = new MaternityDeathAuditedDataDefinition();
		maternityDeathAuditedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDeathAuditedDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityOtherDeliveryComplicationsDataDefinition maternityOtherDeliveryComplicationsDataDefinition = new MaternityOtherDeliveryComplicationsDataDefinition();
		maternityOtherDeliveryComplicationsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityOtherDeliveryComplicationsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityBabySexDataDefinition maternityBabySexDataDefinition = new MaternityBabySexDataDefinition();
		maternityBabySexDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityBabySexDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityBirthWeightDataDefinition maternityBirthWeightDataDefinition = new MaternityBirthWeightDataDefinition();
		maternityBirthWeightDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityBirthWeightDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityBabyConditionDataDefinition maternityBabyConditionDataDefinition = new MaternityBabyConditionDataDefinition();
		maternityBabyConditionDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityBabyConditionDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityInitiatedBFWithinOneHourDataDefinition maternityInitiatedBFWithinOneHourDataDefinition = new MaternityInitiatedBFWithinOneHourDataDefinition();
		maternityInitiatedBFWithinOneHourDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityInitiatedBFWithinOneHourDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityKangarooMotherCareGivenDataDefinition maternityKangarooMotherCareGivenDataDefinition = new MaternityKangarooMotherCareGivenDataDefinition();
		maternityKangarooMotherCareGivenDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityKangarooMotherCareGivenDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityTEOGivenAtBirthDataDefinition maternityTEOGivenAtBirthDataDefinition  = new MaternityTEOGivenAtBirthDataDefinition();
		maternityTEOGivenAtBirthDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityTEOGivenAtBirthDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityChlorhexidineAppliedOnCordStumpDataDefinition maternityChlorhexidineAppliedOnCordStumpDataDefinition = new MaternityChlorhexidineAppliedOnCordStumpDataDefinition();
		maternityChlorhexidineAppliedOnCordStumpDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityChlorhexidineAppliedOnCordStumpDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityBabyWithDeformityDataDefinition maternityBabyWithDeformityDataDefinition = new MaternityBabyWithDeformityDataDefinition();
		maternityBabyWithDeformityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityBabyWithDeformityDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityGivenVitaminKDataDefinition maternityGivenVitaminKDataDefinition = new MaternityGivenVitaminKDataDefinition();
		maternityGivenVitaminKDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityGivenVitaminKDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityApgarScoreDataDefinition maternityApgarScoreDataDefinition  = new MaternityApgarScoreDataDefinition();
		maternityApgarScoreDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityApgarScoreDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityVDRLRPRResultsDataDefinition maternityVDRLRPRResultsDataDefinition = new MaternityVDRLRPRResultsDataDefinition();
		maternityVDRLRPRResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityVDRLRPRResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityHIVStatusAtANCDataDefinition maternityHIVStatusAtANCDataDefinition = new MaternityHIVStatusAtANCDataDefinition();
		maternityHIVStatusAtANCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityHIVStatusAtANCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityHIVTestOneDataDefinition maternityHIVTestOneDataDefinition = new MaternityHIVTestOneDataDefinition();
		maternityHIVTestOneDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityHIVTestOneDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityHIVTestTwoDataDefinition maternityHIVTestTwoDataDefinition = new MaternityHIVTestTwoDataDefinition();
		maternityHIVTestTwoDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityHIVTestTwoDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityHIVFinalResultsDataDefinition maternityHIVFinalResultsDataDefinition = new MaternityHIVFinalResultsDataDefinition();
		maternityHIVFinalResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityHIVFinalResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityARVProphylaxisIssuedFromANCDataDefinition maternityARVProphylaxisIssuedFromANCDataDefinition = new MaternityARVProphylaxisIssuedFromANCDataDefinition();
		maternityARVProphylaxisIssuedFromANCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityARVProphylaxisIssuedFromANCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityARVProphylaxisIssuedAtMaternityDataDefinition maternityARVProphylaxisIssuedAtMaternityDataDefinition = new MaternityARVProphylaxisIssuedAtMaternityDataDefinition();
		maternityARVProphylaxisIssuedAtMaternityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityARVProphylaxisIssuedAtMaternityDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityARVProphylaxisToBabyAtMaternityDataDefinition maternityARVProphylaxisToBabyAtMaternityDataDefinition = new MaternityARVProphylaxisToBabyAtMaternityDataDefinition();
		maternityARVProphylaxisToBabyAtMaternityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityARVProphylaxisToBabyAtMaternityDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityCTXToMotherDataDefinition maternityCTXToMotherDataDefinition = new MaternityCTXToMotherDataDefinition();
		maternityCTXToMotherDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityCTXToMotherDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityVitaminADataDefinition maternityVitaminADataDefinition = new MaternityVitaminADataDefinition();
		maternityVitaminADataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityVitaminADataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityPartnerTestedForHIVDataDefinition maternityPartnerTestedForHIVDataDefinition = new MaternityPartnerTestedForHIVDataDefinition();
		maternityPartnerTestedForHIVDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityPartnerTestedForHIVDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityPartnerHIVTestResultsDataDefinition maternityPartnerHIVTestResultsDataDefinition = new MaternityPartnerHIVTestResultsDataDefinition();
		maternityPartnerHIVTestResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityPartnerHIVTestResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityCounselledOnInfantFeedingDataDefinition maternityCounselledOnInfantFeedingDataDefinition = new MaternityCounselledOnInfantFeedingDataDefinition();
		maternityCounselledOnInfantFeedingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityCounselledOnInfantFeedingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDeliveryConductedByDataDefinition maternityDeliveryConductedByDataDefinition = new MaternityDeliveryConductedByDataDefinition();
		maternityDeliveryConductedByDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDeliveryConductedByDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityBirthNotificationNumberDataDefinition maternityBirthNotificationNumberDataDefinition = new MaternityBirthNotificationNumberDataDefinition();
		maternityBirthNotificationNumberDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityBirthNotificationNumberDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityDischargeDateDataDefinition maternityDischargeDateDataDefinition = new MaternityDischargeDateDataDefinition();
		maternityDischargeDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityDischargeDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityStatusOfBabyDataDefinition maternityStatusOfBabyDataDefinition = new MaternityStatusOfBabyDataDefinition();
		maternityStatusOfBabyDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityStatusOfBabyDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityReferredFromDataDefinition maternityReferredFromDataDefinition = new MaternityReferredFromDataDefinition();
		maternityReferredFromDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityReferredFromDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityReferredToDataDefinition  maternityReferredToDataDefinition = new MaternityReferredToDataDefinition();
		maternityReferredToDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityReferredToDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityReasonForReferralDataDefinition maternityReasonForReferralDataDefinition = new MaternityReasonForReferralDataDefinition();
		maternityReasonForReferralDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityReasonForReferralDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		MaternityCommentsDataDefinition maternityCommentsDataDefinition = new MaternityCommentsDataDefinition();
		maternityCommentsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		maternityCommentsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));


		dsd.addColumn("Unique Patient No", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		// new columns
		dsd.addColumn("Admission Number", maternityAdmissionNumberDataDefinition, paramMapping);
		dsd.addColumn("Date of Admission", maternityAdmissionDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Number of ANC Visits", maternityNumberOfANCVisitsDataDefinition, "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("County of Residence", new CalculationDataDefinition("County", new CountyAddressCalculation()), "", null);
		dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
		dsd.addColumn("Marital Status", kenyaEMRMaritalStatusDataDefinition, paramMapping);
		dsd.addColumn("Parity", maternityANCParityDataDefinition, paramMapping);
		dsd.addColumn("Gravida", maternityGravidaDataDefinition, paramMapping);
		dsd.addColumn("LMP", maternityLMPDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Ultra Sound", maternityEDDUltrasoundDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Diagnosis", maternityDiagnosisDataDefinition, paramMapping);
		dsd.addColumn("Duration of Labour", maternityDurationOfLabourDataDefinition, paramMapping);
		dsd.addColumn("Delivery Date", maternityDeliveryDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Delivery Time", maternityDeliveryTimeDataDefinition, paramMapping);
		dsd.addColumn("Gestation at Birth in weeks", maternityGestationAtBirthDataDefinition, paramMapping);
		dsd.addColumn("Mode of Delivery", maternityDeliveryModeDataDefinition, paramMapping);
		dsd.addColumn("Number of babies delivered", maternityNumberOfBabiesDelivered, paramMapping);
		dsd.addColumn("Placenta Complete", maternityPlacentaCompleteDataDefinition, paramMapping);
		dsd.addColumn("Uterotonic given", maternityUterotonicGivenDataDefinition, paramMapping);
		dsd.addColumn("Vaginal Examination", maternityVaginalExaminationDataDefinition, paramMapping);
		dsd.addColumn("Blood Loss", maternityBloodLossDataDefinition, paramMapping);
		dsd.addColumn("Condition after delivery", maternityConditionAfterDeliveryDataDefinition, paramMapping);
		dsd.addColumn("Deaths Audited", maternityDeathAuditedDataDefinition, paramMapping);
		dsd.addColumn("Other Delivery Complications", maternityOtherDeliveryComplicationsDataDefinition, paramMapping);
		dsd.addColumn("Baby Sex", maternityBabySexDataDefinition, paramMapping);
		dsd.addColumn("Birth weight", maternityBirthWeightDataDefinition, paramMapping);
		dsd.addColumn("Baby Condition", maternityBabyConditionDataDefinition, paramMapping);
		dsd.addColumn("Initiated BF <1 Hr", maternityInitiatedBFWithinOneHourDataDefinition, paramMapping);
		dsd.addColumn("Kangaroo Mother care given", maternityKangarooMotherCareGivenDataDefinition, paramMapping);
		dsd.addColumn("TEO Given at Birth", maternityTEOGivenAtBirthDataDefinition, paramMapping);
		dsd.addColumn("Chlorhexidine applied on cord stump", maternityTEOGivenAtBirthDataDefinition, paramMapping);
		dsd.addColumn("Baby with deformity", maternityBabyWithDeformityDataDefinition, paramMapping);
		dsd.addColumn("Given Vitamin K", maternityGivenVitaminKDataDefinition, paramMapping);
		dsd.addColumn("APGAR Score", maternityApgarScoreDataDefinition, paramMapping);
		dsd.addColumn("VDRL/RPR Results", maternityVDRLRPRResultsDataDefinition, paramMapping);
		dsd.addColumn("HIV Status at ANC", maternityHIVStatusAtANCDataDefinition, paramMapping);
		dsd.addColumn("HIV Test One", maternityHIVTestOneDataDefinition, paramMapping);
		dsd.addColumn("HIV Test Two", maternityHIVTestTwoDataDefinition, paramMapping);
		dsd.addColumn("HIV Final Results", maternityHIVFinalResultsDataDefinition, paramMapping);
		dsd.addColumn("ARV Prophylaxis Issued from ANC", maternityARVProphylaxisIssuedFromANCDataDefinition, paramMapping);
		dsd.addColumn("ARV Prophylaxis Issued at Maternity", maternityARVProphylaxisIssuedAtMaternityDataDefinition, paramMapping);
		dsd.addColumn("ARV Prophylaxis To Baby in Maternity", maternityARVProphylaxisToBabyAtMaternityDataDefinition, paramMapping);
		dsd.addColumn("CTX To Mother", maternityCTXToMotherDataDefinition, paramMapping);
		dsd.addColumn("Vitamin A", maternityVitaminADataDefinition, paramMapping);
		dsd.addColumn("Partner Tested For HIV", maternityPartnerTestedForHIVDataDefinition, paramMapping);
		dsd.addColumn("Partner HIV Test Results", maternityPartnerHIVTestResultsDataDefinition, paramMapping);
		dsd.addColumn("Counselled on Infant Feeding", maternityCounselledOnInfantFeedingDataDefinition, paramMapping);
		dsd.addColumn("Delivery Conducted by", maternityDeliveryConductedByDataDefinition, paramMapping);
		dsd.addColumn("Birth Notification Number", maternityBirthNotificationNumberDataDefinition, paramMapping);
		dsd.addColumn("Discharged Date", maternityDischargeDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Status of Baby at Discharge", maternityStatusOfBabyDataDefinition, paramMapping);
		dsd.addColumn("Referred From", maternityReferredFromDataDefinition, paramMapping);
		dsd.addColumn("Referred To", maternityReferredToDataDefinition, paramMapping);
		dsd.addColumn("Reasons for referral", maternityReasonForReferralDataDefinition, paramMapping);
		dsd.addColumn("Comments", maternityCommentsDataDefinition, paramMapping);
        return dsd;
	}

	protected DataSetDefinition maternityDataSet() {
		CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
		cohortDsd.setName("cohortIndicator");
		cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.datimFineAgeGroups(), "onDate=${endDate}"));
		cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

		ColumnParameters colTotal = new ColumnParameters(null, "Total", "");
		ColumnParameters f10_to14 = new ColumnParameters(null, "10-14, Female", "gender=F|age=10-14");
		ColumnParameters f15_to19 = new ColumnParameters(null, "15-19, Female", "gender=F|age=15-19");
		ColumnParameters f10_to19 = new ColumnParameters(null, "10-19, Female", "gender=F|age=10-19");
		ColumnParameters f20_to24 = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");
		ColumnParameters fAbove25 = new ColumnParameters(null, "25+, Female", "gender=F|age=25+");

		List<ColumnParameters> maternityAgeDisaggregation = Arrays.asList(f10_to14, f15_to19, f20_to24, fAbove25, colTotal);
		List<ColumnParameters> maternityAdolescentsAgeDisaggregation = Arrays.asList(f10_to19, colTotal);
		String indParams = "startDate=${startDate},endDate=${endDate}";

		EmrReportingUtils.addRow(cohortDsd, "Maternity clients", "", ReportUtils.map(pmtctMATIndicators.maternityClients(), indParams), maternityAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05"));
		cohortDsd.addColumn("clientsWithAPH", "Clients With APH", ReportUtils.map(pmtctMATIndicators.clientsWithAPH(), indParams), "");
		cohortDsd.addColumn("clientsWithAPHDead", "Clients With APH (Dead)", ReportUtils.map(pmtctMATIndicators.clientsWithAPHDead(), indParams), "");
		cohortDsd.addColumn("clientsWithPPH", "Clients With PPH", ReportUtils.map(pmtctMATIndicators.clientsWithPPH(), indParams), "");
		cohortDsd.addColumn("clientsWithPPHDead", "Clients With PPH Dead", ReportUtils.map(pmtctMATIndicators.clientsWithPPHDead(), indParams), "");
		cohortDsd.addColumn("clientsWithEclampsia", "Clients With Eclampsia", ReportUtils.map(pmtctMATIndicators.clientsWithEclampsia(), indParams), "");
		cohortDsd.addColumn("clientsWithEclampsiaDead", "Clients With Eclampsia (Dead)", ReportUtils.map(pmtctMATIndicators.clientsWithEclampsiaDead(), indParams), "");
		cohortDsd.addColumn("clientsWithRapturedUterus", "Clients With Raptured Uterus", ReportUtils.map(pmtctMATIndicators.clientsWithRapturedUterus(), indParams), "");
		cohortDsd.addColumn("clientsWithRapturedUterusDead", "Clients With Raptured Uterus (Dead)", ReportUtils.map(pmtctMATIndicators.clientsWithRapturedUterusDead(), indParams), "");
		cohortDsd.addColumn("clientsWithObstructedLabour", "Clients With Obstructed Labour", ReportUtils.map(pmtctMATIndicators.clientsWithObstructedLabour(), indParams), "");
		cohortDsd.addColumn("clientsWithObstructedLabourDead", "Clients With Obstructed Labour(Dead)", ReportUtils.map(pmtctMATIndicators.clientsWithObstructedLabourDead(), indParams), "");
		cohortDsd.addColumn("clientsWithSepsis", "Clients With Sepsis", ReportUtils.map(pmtctMATIndicators.clientsWithSepsis(), indParams), "");
		cohortDsd.addColumn("clientsWithSepsisDead", "Clients With Sepsis(Dead)", ReportUtils.map(pmtctMATIndicators.clientsWithSepsisDead(), indParams), "");
		cohortDsd.addColumn("clientsAlive", "Clients Alive", ReportUtils.map(pmtctMATIndicators.clientsAlive(), indParams), "");
		cohortDsd.addColumn("clientsDead", "Clients Dead", ReportUtils.map(pmtctMATIndicators.clientsDead(), indParams), "");
		cohortDsd.addColumn("preTermBabies", "Pre-term Babies", ReportUtils.map(pmtctMATIndicators.preTermBabies(), indParams), "");
		cohortDsd.addColumn("underWeightBabies", "Underweight Babies", ReportUtils.map(pmtctMATIndicators.underWeightBabies(), indParams), "");
		cohortDsd.addColumn("liveBirths", "Live Births", ReportUtils.map(pmtctMATIndicators.liveBirths(), indParams), "");
		cohortDsd.addColumn("stillBirths", "Still Births", ReportUtils.map(pmtctMATIndicators.stillBirths(), indParams), "");
		cohortDsd.addColumn("initialTestAtMaternity", "Initial Test At Maternity", ReportUtils.map(pmtctMATIndicators.initialTestAtMaternity(), indParams), "");
		cohortDsd.addColumn("positiveResultsAtMaternity", "HIV Positive Results at Maternity", ReportUtils.map(pmtctMATIndicators.positiveResultsAtMaternity(), indParams), "");
		cohortDsd.addColumn("hivPositiveDeliveries", "HIV Positive Deliveries", ReportUtils.map(pmtctMATIndicators.hivPositiveDeliveries(), indParams), "");
		cohortDsd.addColumn("adolescentsNewHivPositiveAtMaternity", "HIV Positive new Adolescents at Maternity", ReportUtils.map(pmtctMATIndicators.adolescentsNewHivPositiveAtMaternity(), indParams), "");
		cohortDsd.addColumn("startedHAARTMaternity", "Started HAART at Maternity", ReportUtils.map(pmtctMATIndicators.startedHAARTMaternity(), indParams), "");
		cohortDsd.addColumn("infantARVProphylaxisMaternity", "Given Infant ARV Prophylaxis Maternity", ReportUtils.map(pmtctMATIndicators.infantARVProphylaxisMaternity(), indParams), "");
		cohortDsd.addColumn("normalDeliveries", "Normal Deliveries", ReportUtils.map(pmtctMATIndicators.normalDeliveries(), indParams), "");
		cohortDsd.addColumn("caesareanSections", "Caesarean Sections", ReportUtils.map(pmtctMATIndicators.caesareanSections(), indParams), "");
		cohortDsd.addColumn("breechDeliveries", "Breech Deliveries", ReportUtils.map(pmtctMATIndicators.breechDeliveries(), indParams), "");
		cohortDsd.addColumn("assistedVaginalDeliveries", "Assisted Vaginal Deliveries", ReportUtils.map(pmtctMATIndicators.assistedVaginalDeliveries(), indParams), "");
		cohortDsd.addColumn("uterotonicGiven", "Uterotonic Given", ReportUtils.map(pmtctMATIndicators.uterotonicGiven(), indParams), "");
		cohortDsd.addColumn("carbetocin", "Carbetocin", ReportUtils.map(pmtctMATIndicators.carbetocin(), indParams), "");
		cohortDsd.addColumn("oxytocin", "Oxytocin", ReportUtils.map(pmtctMATIndicators.oxytocin(), indParams), "");
		cohortDsd.addColumn("deformity", "Deformity", ReportUtils.map(pmtctMATIndicators.deformity(), indParams), "");
		cohortDsd.addColumn("maceratedStillbirth", "Macerated Stillbirth", ReportUtils.map(pmtctMATIndicators.maceratedStillbirth(), indParams), "");
		cohortDsd.addColumn("apgar", "Apgar", ReportUtils.map(pmtctMATIndicators.lowApgar(), indParams), "");
		cohortDsd.addColumn("noOfBabiesDischargedAlive", "No. of babies discharged alive", ReportUtils.map(pmtctMATIndicators.noOfBabiesDischargedAlive(), indParams), "");
		cohortDsd.addColumn("earlyNeonatalDeaths", "Early Neonatal deaths (0-7days)", ReportUtils.map(pmtctMATIndicators.earlyNeonatalDeaths(), indParams), "");
		cohortDsd.addColumn("lateNeonatalDeaths", "Late Neonatal deaths (8-28days)", ReportUtils.map(pmtctMATIndicators.lateNeonatalDeaths(), indParams), "");
		cohortDsd.addColumn("initialTestLD", "Initial test at L&D", ReportUtils.map(pmtctMATIndicators.initialTestLD(), indParams), "");
		cohortDsd.addColumn("positiveResultsLD", "Positive Results L&D", ReportUtils.map(pmtctMATIndicators.positiveResultsLD(), indParams), "");
		cohortDsd.addColumn("noHIVpositiveDeliveries", "No. HIV positive deliveries", ReportUtils.map(pmtctMATIndicators.noHIVpositiveDeliveries(), indParams), "");
		cohortDsd.addColumn("deaths10to14Years", "Maternal deaths 10-14Years", ReportUtils.map(pmtctMATIndicators.deaths10to14Years(), indParams), "");
		cohortDsd.addColumn("deaths15to19Years", "Maternal deaths 15-19Years", ReportUtils.map(pmtctMATIndicators.deaths15to19Years(), indParams), "");
		cohortDsd.addColumn("deaths20toplus", "Maternal deaths 20 years plus", ReportUtils.map(pmtctMATIndicators.deaths20toplus(), indParams), "");
		cohortDsd.addColumn("deathAudited", "Maternal death audited", ReportUtils.map(pmtctMATIndicators.deathAudited(), indParams), "");
		cohortDsd.addColumn("appliedChlorhexidine", "Babies applied chlorhexidine for cord care", ReportUtils.map(pmtctMATIndicators.appliedChlorhexidine(), indParams), "");
		cohortDsd.addColumn("givenTetracycline", "Babies given tetracycline at birth", ReportUtils.map(pmtctMATIndicators.givenTetracycline(), indParams), "");
		cohortDsd.addColumn("infantsIntiatiedOnBreastfeeding", "Infants intiatied on breastfeeding within 1 hour after birth", ReportUtils.map(pmtctMATIndicators.infantsIntiatiedOnBreastfeeding(), indParams), "");
		cohortDsd.addColumn("vitaminK", "Vitamin K given ", ReportUtils.map(pmtctMATIndicators.vitaminK(), indParams), "");
		EmrReportingUtils.addRow(cohortDsd, "HIV Positive adolescents at Maternity", "", ReportUtils.map(pmtctMATIndicators.hivPositiveResultAtPNC(), indParams), maternityAdolescentsAgeDisaggregation, Arrays.asList("01", "02"));
		EmrReportingUtils.addRow(cohortDsd, "Started HAART (Adolescents)", "", ReportUtils.map(pmtctMATIndicators.startedHAARTPNC(), indParams), maternityAdolescentsAgeDisaggregation, Arrays.asList("01", "02"));

		return cohortDsd;
	}
}

