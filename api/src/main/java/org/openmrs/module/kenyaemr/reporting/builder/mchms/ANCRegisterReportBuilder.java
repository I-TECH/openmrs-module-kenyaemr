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
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ANCRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.*;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.*;
import org.openmrs.module.kenyaemr.reporting.library.pmtct.ANCIndicatorLibrary;
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
    private ANCIndicatorLibrary anc;

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

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);


        dsd.addColumn("id", new PatientIdDataDefinition(), "");

        dsd.addColumn("Sex", new GenderDataDefinition(), "");

        dsd.addColumn("Unique Patient Number", identifierDef, null);

        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        // new columns
        dsd.addColumn("ANC Number", new ANCNumberDataDefinition(),"");
        dsd.addColumn("Visit Number", new ANCVisitNumberDataDefinition(),"");
        dsd.addColumn("First ANC Visit", new FirstANCVisitDataDefinition(),"");
        dsd.addColumn("Number of ANC Visits", new ANCNumberOfVisitsDataDefinition(),"");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Marital Status", new KenyaEMRMaritalStatusDataDefinition(), null);
        dsd.addColumn("Parity", new ANCParityDataDefinition(),"");
        dsd.addColumn("Gravida", new ANCGravidaDataDefinition(),"");
        dsd.addColumn("LMP", new ANCLmpDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Ultra Sound", new ANCEDDUltrasoundDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Gestation", new ANCGestationDataDefinition(),"");
        dsd.addColumn("Weight", new ANCWeightDataDefinition(),"");
        dsd.addColumn("Height", new ANCHeightDataDefinition(),"");
        dsd.addColumn("Blood Pressure", new ANCBloodPressureDataDefinition(),"");
        dsd.addColumn("Breast Exam", new ANCBreastExamDoneDataDefinition(),"");
        dsd.addColumn("Counselled", new ANCCounselledDoneDataDefinition(),"");
        dsd.addColumn("Haemoglobin", new ANCHaemoglobinDataDefinition(),"");
        dsd.addColumn("VDRL Done", new ANCVDRLDoneDataDefinition(),"");
        dsd.addColumn("VDRL Results", new ANCVDRLResultsDataDefinition(),"");
        dsd.addColumn("VDRL Treated", new ANCVDRLTreatedDataDefinition(),"");
        dsd.addColumn("HIV Status preANC", new ANCHIVStatusBeforeFirstANCDataDefinition(),"");
        dsd.addColumn("HIV Test Type", new ANCHIVTestTypeDataDefinition(),"");
        dsd.addColumn("HIV Test One", new ANCHIVTestOneDataDefinition(),"");
        dsd.addColumn("HIV Test Two", new ANCHIVTestTwoDataDefinition(),"");
        dsd.addColumn("HIV Test Results", new ANCFinalTestResultsDataDefinition(),"");
        dsd.addColumn("WHO Stage", new ANCWHOStageDataDefinition(),"");
        dsd.addColumn("VL Test Results", new ANCVLTestResultsDataDefinition(),"");
        dsd.addColumn("Given HAART preANC", new ANCHAARTGivenBeforeFirstANCDataDefinition(),"");
        dsd.addColumn("Given HAART at ANC", new ANCHAARTGivenAtANCDataDefinition(),"");
        dsd.addColumn("Prophylaxis Given", new ANCProphylaxisGivenDataDefinition(),"");
        dsd.addColumn("AZT Dispensed", new ANCAZTDispensedDataDefinition(),"");
        dsd.addColumn("NVP Dispensed", new ANCNVPDispensedDataDefinition(),"");
        dsd.addColumn("TB Screening", new ANCTBScreeningResultsDataDefinition(),"");
        dsd.addColumn("CaCx Screening", new ANCCaCxScreeningResultsDataDefinition(),"");
        dsd.addColumn("Other Illnesses", new ANCOtherIllnessesDataDefinition(),"");
        dsd.addColumn("Deworming", new ANCDewormingDataDefinition(),"");
        dsd.addColumn("IPT malaria", new ANCIPTmalariaDataDefinition(),"");
        dsd.addColumn("TTT", new ANCTTTDataDefinition(),"");
        dsd.addColumn("Suppliment", new ANCSupplimentDataDefinition(),"");
        dsd.addColumn("ITN", new ANCITNDataDefinition(),"");
        dsd.addColumn("Other Illnesses", new ANCOtherIllnessesDataDefinition(),"");
        dsd.addColumn("ANC Excercises", new ANCExercisesDataDefinition(),"");
        dsd.addColumn("Partner Tested for HIV", new ANCPartnerTestedForHivDataDefinition(),"");
        dsd.addColumn("Partner HIV Status", new ANCPartnerHIVStatusDataDefinition(),"");
        dsd.addColumn("Facility Referred From", new ANCFacilityReferredFromDataDefinition(),"");
        dsd.addColumn("Facility Referred To", new ANCFacilityReferredToDataDefinition(),"");
        dsd.addColumn("Next Appointment Date", new ANCNextAppointmentDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Clinical Notes", new ANCClinicalNotesDataDefinition(),"");

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

        String indParams = "";

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