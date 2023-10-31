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
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.CountyAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.SubCountyAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ANCRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KenyaEMRMaritalStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.*;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.AgeAtReportingDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.library.pmtct.PMTCTIndicatorLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.mchms.report.ancRegister"})
public class ANCRegisterReportBuilder extends AbstractReportBuilder {
    public static final String ENC_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private PMTCTIndicatorLibrary anc;

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(datasetColumns(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(ancRegisterAggregateDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition datasetColumns() {
        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.setName("ancRegister");
        dsd.setDescription("Visit information");
        dsd.addSortCriteria("Visit Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String paramMapping = "startDate=${startDate},endDate=${endDate}";

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        ANCVisitNumberDataDefinition ancVisitNumberDataDefinition = new ANCVisitNumberDataDefinition();
        ancVisitNumberDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancVisitNumberDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCMUACDataDefinition ancMUACDataDefinition = new ANCMUACDataDefinition();
        ancMUACDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancMUACDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHeightDataDefinition ancHeightDataDefinition = new ANCHeightDataDefinition();
        ancHeightDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHeightDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCWeightDataDefinition ancWeightDataDefinition = new ANCWeightDataDefinition();
        ancWeightDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancWeightDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCBloodPressureDataDefinition ancBloodPressureDataDefinition = new ANCBloodPressureDataDefinition();
        ancBloodPressureDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancBloodPressureDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCBreastExamDoneDataDefinition ancBreastExamDoneDataDefinition = new ANCBreastExamDoneDataDefinition();
        ancBreastExamDoneDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancBreastExamDoneDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCFGMDoneDataDefinition ancFGMDoneDataDefinition = new ANCFGMDoneDataDefinition();
        ancFGMDoneDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancFGMDoneDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCFGMComplicationsDataDefinition ancFGMComplicationsDataDefinition = new ANCFGMComplicationsDataDefinition();
        ancFGMComplicationsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancFGMComplicationsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHepatitisBScreenedDefinition ancHepatitisBScreenedDefinition = new ANCHepatitisBScreenedDefinition();
        ancHepatitisBScreenedDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHepatitisBScreenedDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHepatitisBTreatedDefinition ancHepatitisBTreatedDefinition = new ANCHepatitisBTreatedDefinition();
        ancHepatitisBTreatedDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHepatitisBTreatedDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHaemoglobinDataDefinition ancHaemoglobinDataDefinition = new ANCHaemoglobinDataDefinition();
        ancHaemoglobinDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHaemoglobinDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCDiabetesTestingDefinition ancDiabetesTestingDefinition = new ANCDiabetesTestingDefinition();
        ancDiabetesTestingDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancDiabetesTestingDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCUrinalysisDataDefinition ancUrinalysisDataDefinition = new ANCUrinalysisDataDefinition();
        ancUrinalysisDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancUrinalysisDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCVDRLDoneDataDefinition ancVDRLDoneDataDefinition = new ANCVDRLDoneDataDefinition();
        ancVDRLDoneDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancVDRLDoneDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCVDRLResultsDataDefinition ancVDRLResultsDataDefinition = new ANCVDRLResultsDataDefinition();
        ancVDRLResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancVDRLResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCVDRLTreatedDataDefinition ancVDRLTreatedDataDefinition = new ANCVDRLTreatedDataDefinition();
        ancVDRLTreatedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancVDRLTreatedDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHIVStatusBeforeFirstANCDataDefinition ancHIVStatusBeforeFirstANCDataDefinition = new ANCHIVStatusBeforeFirstANCDataDefinition();
        ancHIVStatusBeforeFirstANCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHIVStatusBeforeFirstANCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHIVTestTypeDataDefinition ancHIVTestTypeDataDefinition = new ANCHIVTestTypeDataDefinition();
        ancHIVTestTypeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHIVTestTypeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHIVTestOneDataDefinition ancHIVTestOneDataDefinition = new ANCHIVTestOneDataDefinition();
        ancHIVTestOneDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHIVTestOneDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHIVTestTwoDataDefinition ancHIVTestTwoDataDefinition = new ANCHIVTestTwoDataDefinition();
        ancHIVTestTwoDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHIVTestTwoDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCFinalTestResultsDataDefinition ancFinalTestResultsDataDefinition = new ANCFinalTestResultsDataDefinition();
        ancFinalTestResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancFinalTestResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCWHOStageDataDefinition ancWHOStageDataDefinition = new ANCWHOStageDataDefinition();
        ancWHOStageDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancWHOStageDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ETLLastVLResultDataDefinition ancVLTestResultsDataDefinition = new ETLLastVLResultDataDefinition();
        ancVLTestResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancVLTestResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHAARTGivenBeforeFirstANCDataDefinition ancHAARTGivenBeforeFirstANCDataDefinition = new ANCHAARTGivenBeforeFirstANCDataDefinition();
        ancHAARTGivenBeforeFirstANCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHAARTGivenBeforeFirstANCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCHAARTGivenAtANCDataDefinition ancHAARTGivenAtANCDataDefinition = new ANCHAARTGivenAtANCDataDefinition();
        ancHAARTGivenAtANCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancHAARTGivenAtANCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCProphylaxisGivenDataDefinition ancProphylaxisGivenDataDefinition = new ANCProphylaxisGivenDataDefinition();
        ancProphylaxisGivenDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancProphylaxisGivenDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCAZTDispensedDataDefinition ancAZTDispensedDataDefinition = new ANCAZTDispensedDataDefinition();
        ancAZTDispensedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancAZTDispensedDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCNVPDispensedDataDefinition ancNVPDispensedDataDefinition = new ANCNVPDispensedDataDefinition();
        ancNVPDispensedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancNVPDispensedDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCTBScreeningResultsDataDefinition ancTBScreeningResultsDataDefinition = new ANCTBScreeningResultsDataDefinition();
        ancTBScreeningResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancTBScreeningResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCOtherIllnessesDataDefinition ancOtherIllnessesDataDefinition = new ANCOtherIllnessesDataDefinition();
        ancOtherIllnessesDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancOtherIllnessesDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCDewormingDataDefinition ancDewormingDataDefinition = new ANCDewormingDataDefinition();
        ancDewormingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancDewormingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCIPTmalariaDataDefinition ancIPTmalariaDataDefinition = new ANCIPTmalariaDataDefinition();
        ancIPTmalariaDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancIPTmalariaDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCTTTDataDefinition ancTTTDataDefinition = new ANCTTTDataDefinition();
        ancTTTDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancTTTDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCSupplimentDataDefinition ancSupplimentDataDefinition = new ANCSupplimentDataDefinition();
        ancSupplimentDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancSupplimentDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCITNDataDefinition ancITNDataDefinition = new ANCITNDataDefinition();
        ancITNDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancITNDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCPartnerTestedForHivDataDefinition ancPartnerTestedForHivDataDefinition = new ANCPartnerTestedForHivDataDefinition();
        ancPartnerTestedForHivDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancPartnerTestedForHivDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCPartnerHIVStatusDataDefinition ancPartnerHIVStatusDataDefinition = new ANCPartnerHIVStatusDataDefinition();
        ancPartnerHIVStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancPartnerHIVStatusDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCFacilityReferredFromDataDefinition ancFacilityReferredFromDataDefinition = new ANCFacilityReferredFromDataDefinition();
        ancFacilityReferredFromDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancFacilityReferredFromDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCFacilityReferredToDataDefinition ancFacilityReferredToDataDefinition = new ANCFacilityReferredToDataDefinition();
        ancFacilityReferredToDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancFacilityReferredToDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCClinicalNotesDataDefinition ancClinicalNotesDataDefinition = new ANCClinicalNotesDataDefinition();
        ancClinicalNotesDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancClinicalNotesDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ANCPPFPCounseledDataDefinition ancPPFPCounseledDataDefinition = new ANCPPFPCounseledDataDefinition();
        ancPPFPCounseledDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ancPPFPCounseledDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

        AgeAtReportingDataDefinition ageAtReportingDataDefinition = new AgeAtReportingDataDefinition();
        ageAtReportingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ageAtReportingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);


        dsd.addColumn("id", new PatientIdDataDefinition(), "");

        dsd.addColumn("Sex", new GenderDataDefinition(), "");

        dsd.addColumn("Unique Patient Number", identifierDef, null);
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        // new columns
        dsd.addColumn("ANC Number", new ANCNumberDataDefinition(),"");
        dsd.addColumn("Visit Number", ancVisitNumberDataDefinition, paramMapping);
       // dsd.addColumn("First ANC Visit", new FirstANCVisitDataDefinition(),""); Dropped
        dsd.addColumn("Number of ANC Visits", new ANCNumberOfVisitsDataDefinition(),"");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("County",new CalculationDataDefinition("County", new CountyAddressCalculation()), "",new CalculationResultConverter());
        dsd.addColumn("Sub County", new CalculationDataDefinition("Subcounty", new SubCountyAddressCalculation()), "",new CalculationResultConverter());
        dsd.addColumn("Village", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "",new RDQACalculationResultConverter());
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Age",  ageAtReportingDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Marital Status", new KenyaEMRMaritalStatusDataDefinition(), null);
        dsd.addColumn("Parity", new ANCParityDataDefinition(),"");
        dsd.addColumn("Gravida", new ANCGravidaDataDefinition(),"");
        dsd.addColumn("LMP", new ANCLmpDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Ultra Sound", new ANCEDDUltrasoundDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Gestation", new ANCGestationDataDefinition(),"");

        dsd.addColumn("MUAC", ancMUACDataDefinition, paramMapping);
        dsd.addColumn("Height", ancHeightDataDefinition, paramMapping);
        dsd.addColumn("Weight", ancWeightDataDefinition, paramMapping);
        dsd.addColumn("Blood Pressure", ancBloodPressureDataDefinition, paramMapping);
        dsd.addColumn("Breast Exam", ancBreastExamDoneDataDefinition, paramMapping);
        dsd.addColumn("FGM Done", ancFGMDoneDataDefinition, paramMapping);
        dsd.addColumn("FGM Complications", ancFGMComplicationsDataDefinition, paramMapping);
        dsd.addColumn("Hepatitis B Screened", ancHepatitisBScreenedDefinition, paramMapping);
        dsd.addColumn("Hepatitis B Treated", ancHepatitisBTreatedDefinition, paramMapping);
        //dsd.addColumn("Counselled", new ANCCounselledDoneDataDefinition(),""); --Dropped
        dsd.addColumn("Haemoglobin", ancHaemoglobinDataDefinition, paramMapping);
        dsd.addColumn("Diabetes testing", ancDiabetesTestingDefinition, paramMapping);
        dsd.addColumn("Urinalysis", ancUrinalysisDataDefinition, paramMapping);
        dsd.addColumn("VDRL Done", ancVDRLDoneDataDefinition, paramMapping);
        dsd.addColumn("VDRL ", ancVDRLResultsDataDefinition, paramMapping);
        dsd.addColumn("VDRL Results", ancVDRLResultsDataDefinition, paramMapping);
        dsd.addColumn("VDRL Treated", ancVDRLTreatedDataDefinition,paramMapping);
        dsd.addColumn("HIV Status preANC", ancHIVStatusBeforeFirstANCDataDefinition, paramMapping);
        dsd.addColumn("HIV Test Type", ancHIVTestTypeDataDefinition, paramMapping);
        dsd.addColumn("HIV Test One", ancHIVTestOneDataDefinition, paramMapping);
        dsd.addColumn("HIV Test Two", ancHIVTestTwoDataDefinition, paramMapping);
        dsd.addColumn("HIV Test Results", ancFinalTestResultsDataDefinition, paramMapping);
        dsd.addColumn("WHO Stage", ancWHOStageDataDefinition, paramMapping);
        dsd.addColumn("VL Test Results", ancVLTestResultsDataDefinition, paramMapping);
        dsd.addColumn("Given HAART preANC", ancHAARTGivenBeforeFirstANCDataDefinition, paramMapping);
        dsd.addColumn("Given HAART at ANC", ancHAARTGivenAtANCDataDefinition, paramMapping);
        dsd.addColumn("Prophylaxis Given", ancProphylaxisGivenDataDefinition, paramMapping);
        dsd.addColumn("AZT Dispensed", ancAZTDispensedDataDefinition, paramMapping);
        dsd.addColumn("NVP Dispensed", ancNVPDispensedDataDefinition, paramMapping);
        dsd.addColumn("TB Screening", ancTBScreeningResultsDataDefinition, paramMapping);
       // dsd.addColumn("CaCx Screening", new ANCCaCxScreeningResultsDataDefinition(),""); d-ropped
        dsd.addColumn("Other Illnesses", ancOtherIllnessesDataDefinition, paramMapping);
        dsd.addColumn("Deworming", ancDewormingDataDefinition, paramMapping);
        dsd.addColumn("IPT Dose 1-7", ancIPTmalariaDataDefinition, paramMapping);
        dsd.addColumn("TTT Given", ancTTTDataDefinition, paramMapping);
        dsd.addColumn("Suppliment", ancSupplimentDataDefinition, paramMapping);
        dsd.addColumn("ITN", ancITNDataDefinition, paramMapping);
        //dsd.addColumn("ANC Excercises", new ANCExercisesDataDefinition(),""); --dropped
        dsd.addColumn("Partner Tested for HIV", ancPartnerTestedForHivDataDefinition, paramMapping);
        dsd.addColumn("Partner HIV Status", ancPartnerHIVStatusDataDefinition, paramMapping);
        dsd.addColumn("PPFP Counselling", ancPPFPCounseledDataDefinition, paramMapping);
        dsd.addColumn("Facility Referred From", ancFacilityReferredFromDataDefinition, paramMapping);
        dsd.addColumn("Facility Referred To", ancFacilityReferredToDataDefinition, paramMapping);
        //dsd.addColumn("Next Appointment Date", new ANCNextAppointmentDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT)); --Dropped
        dsd.addColumn("Clinical Notes", ancClinicalNotesDataDefinition, paramMapping);

        ANCRegisterCohortDefinition cd = new ANCRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;

    }
    protected DataSetDefinition ancRegisterAggregateDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("cohortIndicator");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        cohortDsd.addColumn("newClients", "New Clients", ReportUtils.map(anc.newClientsANC(), indParams), "");
        cohortDsd.addColumn("revisits", "Revisit Clients", ReportUtils.map(anc.revisitsANC(), indParams), "");
        cohortDsd.addColumn("fourthANC", "Fourth ANC visit", ReportUtils.map(anc.completed4AntenatalVisits(), indParams), "");
        cohortDsd.addColumn("syphilisTested", "Syphilis Tested", ReportUtils.map(anc.testedSyphilisANC(), indParams), "");
        cohortDsd.addColumn("syphilisPositive", "Syphilis Positive", ReportUtils.map(anc.positiveSyphilisANC(), indParams), "");
        cohortDsd.addColumn("syphilisTreated", "Treated Syphilis", ReportUtils.map(anc.treatedSyphilisANC(), indParams), "");
        cohortDsd.addColumn("knownPositives", "Known positive", ReportUtils.map(anc.knownPositivesFirstANC(), indParams), "");
        cohortDsd.addColumn("initialTest", "Initial Test ANC", ReportUtils.map(anc.initialTestANC(), indParams), "");
        cohortDsd.addColumn("positiveAtANC", "Tested Positive ANC", ReportUtils.map(anc.positiveTestANC(), indParams), "");
        cohortDsd.addColumn("onARV", "On ARV at First ANC", ReportUtils.map(anc.onARVatFirstANC(), indParams), "");
        cohortDsd.addColumn("startedHaart", "Started Haart at ANC", ReportUtils.map(anc.startedHAARTInANC(), indParams), "");
        cohortDsd.addColumn("babyAZT", "AZT For Baby", ReportUtils.map(anc.aztBabyGivenAtANC(), indParams), "");
        cohortDsd.addColumn("babyNVP", "NVP For Baby", ReportUtils.map(anc.nvpBabyGivenAtANC(), indParams), "");
        cohortDsd.addColumn("screenedForTB", "Screened for TB", ReportUtils.map(anc.screenedForTBAtANC(), indParams), "");
        cohortDsd.addColumn("screenedCaCxPap", "Screened CaCx PAP", ReportUtils.map(anc.screenedForCaCxPAPAtANC(), indParams), "");
        cohortDsd.addColumn("screenedCaCxVIA", "Screened CaCx VIA", ReportUtils.map(anc.screenedForCaCxVIAAtANC(), indParams), "");
        cohortDsd.addColumn("screenedCaCxVili", "Screened CaCx Vili", ReportUtils.map(anc.screenedForCaCxViliAtANC(), indParams), "");
        cohortDsd.addColumn("givenIPT1", "Given IPT 1", ReportUtils.map(anc.givenIPT1AtANC(), indParams), "");
        cohortDsd.addColumn("givenIPT2", "Given IPT 2", ReportUtils.map(anc.givenIPT2AtANC(), indParams), "");
        cohortDsd.addColumn("givenITN", "Given ITN", ReportUtils.map(anc.givenITNAtANC(), indParams), "");
        cohortDsd.addColumn("partnerTested", "Partner Tested", ReportUtils.map(anc.partnerTestedAtANC(), indParams), "");
        cohortDsd.addColumn("partnerKnownPositive", "Partner Known Positive", ReportUtils.map(anc.partnerKnownPositiveAtANC(), indParams), "");
        cohortDsd.addColumn("adolescentsKnownPositive", "Adolescents Known Positive", ReportUtils.map(anc.adolescentsKnownPositive_10_19_AtANC(), indParams), "");
        cohortDsd.addColumn("adolescentsTestedPositive", "Adolescents Tested Positive", ReportUtils.map(anc.adolescentsTestedPositive_10_19_AtANC(), indParams), "");
        cohortDsd.addColumn("adolescentsStartedHaartAnc", "Adolescents Started Haart ANC", ReportUtils.map(anc.adolescentsStartedHaart_10_19_AtANC(), indParams), "");

        return cohortDsd;
    }


}