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
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.PNCRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.AgeAtReportingDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.RegistrationSubcountyDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.*;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.pmtct.PNCIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
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
@Builds({"kenyaemr.mchms.report.pncRegister"})
public class PNCRegisterReportBuilder extends AbstractReportBuilder {
    public static final String ENC_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private PNCIndicatorLibrary pnc;

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private PNCIndicatorLibrary pncIndicators;

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
                ReportUtils.map(pncRegisterAggregateDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition datasetColumns() {
        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.setName("pncRegister");
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

        AgeAtReportingDataDefinition ageAtReportingDataDefinition = new AgeAtReportingDataDefinition();
        ageAtReportingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Unique Patient Number", identifierDef, null);
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(), "", new DateConverter(ENC_DATE_FORMAT));
        // new columns
        dsd.addColumn("PNC Number", new PNCRegisterNumberDataDefinition(), "");
        dsd.addColumn("Visit Number", new PNCVisitNumberDataDefinition(), "");
        // dsd.addColumn("Admission Number", new PNCNumberDataDefinition(),"");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Age", ageAtReportingDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Sub County", new RegistrationSubcountyDataDefinition(), "");
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        //dsd.addColumn("Date of Birth", new PNCDateOfBirthAndAgeDataDefinition(),"");
        //dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        // dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Delivery Date", new PNCDeliveryDateDataDefinition(), "", new DateConverter(ENC_DATE_FORMAT));
        // dsd.addColumn("Marital Status", new KenyaEMRMaritalStatusDataDefinition(), null);

        dsd.addColumn("Place of Delivery", new PNCPlaceOfDeliveryDataDefinition(), "");
        dsd.addColumn("Mode of Delivery", new PNCModeOfDeliveryDataDefinition(), "");
        dsd.addColumn("Postpartum visit timing Mother", new PNCMotherPostpartumVisitTimingDataDefinition(), "");
        dsd.addColumn("Postpartum visit timing Baby", new PNCBabyPostpartumVisitTimingDataDefinition(), "");
        dsd.addColumn("Temperature", new PNCTemperatureDataDefinition(), "");
        dsd.addColumn("Pulse", new PNCPulseDataDefinition(), "");
        dsd.addColumn("Blood Pressure", new PNCBloodPressureDataDefinition(), "");
        dsd.addColumn("Pallor", new PNCPallorExaminationDataDefinition(), "");
        dsd.addColumn("Pallor Severity", new PNCPallorSeverityDataDefinition(), "");
        dsd.addColumn("Breast Examination", new PNCBreastExaminationDataDefinition(), "");
        dsd.addColumn("Uterus Examination", new PNCUterusExaminationDataDefinition(), "");
        dsd.addColumn("Breast Feeding", new PNCBreastFeedingDataDefinition(), "");
        dsd.addColumn("PPH Examination", new PNCPPHExaminationDataDefinition(), "");
        dsd.addColumn("C-Section site", new PNCCSectionSiteDataDefinition(), "");
        dsd.addColumn("Lochia", new PNCLochiaDataDefinition(), "");
        dsd.addColumn("Episiotomy", new PNCEpisiotomyDataDefinition(), "");
        dsd.addColumn("Fistula Screening", new PNCFistulaScreeningDataDefinition(), "");
        dsd.addColumn("TB Screening", new PNCTBScreeningDataDefinition(), "");
        //dsd.addColumn("Infections", new PNCInfectionsDataDefinition(),"");
        dsd.addColumn("Prior Known HIV Status", new PNCPriorKnownStatusDataDefinition(), "");
        dsd.addColumn("Tested HIV at PNC", new PNCTestedForHIVAtPNCDataDefinition(), "");
        dsd.addColumn("Test 1 Results", new PNCTestOneResultsDataDefinition(), "");
        dsd.addColumn("Test 2 Results", new PNCTestTwoResultsDataDefinition(), "");
        dsd.addColumn("HIV Results <=6 weeks", new PNCHIVResultsWithin6WeeksDataDefinition(), "");
        dsd.addColumn("HIV Results >6 weeks", new PNCHIVResultsGreaterThan6WeeksDataDefinition(), "");
        dsd.addColumn("NVP and AZT for Baby <=6 weeks", new PNCNVPAndAZTForBabyWithin6WeeksDataDefinition(), "");
        dsd.addColumn("HAART For mother <=6 weeks", new PNCHAARTForMotherWithin6WeeksDataDefinition(), "");
        dsd.addColumn("NVP and AZT for Baby >6 weeks", new PNCNVPAndAZTForBabyGreaterThan6WeeksDataDefinition(), "");
        dsd.addColumn("HAART For mother >6 weeks", new PNCHAARTForMotherGreaterThan6WeeksDataDefinition(), "");
        dsd.addColumn("Couple counselled", new PNCCoupleCounselledDataDefinition(), "");
        dsd.addColumn("Partner Tested for HIV in PNC", new PNCPartnerTestedHIVInPNCDataDefinition(), "");
        dsd.addColumn("Partner HIV Results", new PNCPartnerHIVResultsDataDefinition(), "");
        dsd.addColumn("Cervical Cancer Screening", new PNCCervicalCancerScreeningDataDefinition(), "");
        dsd.addColumn("Cervical Cancer Screening Method", new PNCCervicalCancerScreeningMethodDataDefinition(), "");
        dsd.addColumn("Cervical Cancer Screening Results", new PNCCervicalCancerScreeningResultsDataDefinition(), "");
        dsd.addColumn("Exercise Given", new PNCExerciseGivenDataDefinition(), "");
        dsd.addColumn("Counselled on Modern Post Partum FP", new PNCModernFPWithin6WeeksDataDefinition(), "");
        dsd.addColumn("Received Modern Post Partum FP", new PNCModernFPWithin6WeeksDataDefinition(), "");
        dsd.addColumn("Diagnosis", new PNCDiagnosisDataDefinition(), "");
        dsd.addColumn("Haemanitics", new PNCHaematinicsDataDefinition(), "");
        dsd.addColumn("Referred From", new PNCReferredFromDataDefinition(), "");
        dsd.addColumn("Referred To", new PNCReferredToDataDefinition(), "");
        dsd.addColumn("Remarks", new PNCRemarksDataDefinition(), "");
        PNCRegisterCohortDefinition cd = new PNCRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));

        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;

    }

    protected DataSetDefinition pncRegisterAggregateDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("pncIndicators");
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

        List<ColumnParameters>  pncAgeDisaggregation = Arrays.asList(f10_to14, f15_to19, f20_to24, fAbove25,colTotal);
        List<ColumnParameters>  pncAdolescentsAgeDisaggregation = Arrays.asList(f10_to19,colTotal);

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "PNC clients", "", ReportUtils.map(pncIndicators.pncClients(), indParams), pncAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05"));

        cohortDsd.addColumn("New PNC Visit", "", ReportUtils.map(pnc.newClientsPNC(), ""), "");
        cohortDsd.addColumn("PNC revisits", "", ReportUtils.map(pnc.revisitsPNC(), ""), "");
        cohortDsd.addColumn("New PNC visit within 48 hrs", "", ReportUtils.map(pnc.pncMotherNewVisitWithin48hrs(), ""), "");
        cohortDsd.addColumn("New PNC visit between 3 days and under 6 weeks", "", ReportUtils.map(pnc.pncMotherNewVisitBetween3DaysUnder6Weeks(), ""), "");
        cohortDsd.addColumn("New PNC visit after 6 weeks", "", ReportUtils.map(pnc.pncMotherNewVisitAfter6Weeks(), ""), "");
        cohortDsd.addColumn("New PNC visit for babies within 48 hrs", "", ReportUtils.map(pnc.pncBabyNewVisitWithin48hrs(), ""), "");
        cohortDsd.addColumn("New PNC visit for babies between 3 days and under 6 weeks", "", ReportUtils.map(pnc.pncBabyNewVisitBetween3DaysUnder6Weeks(), ""), "");
        cohortDsd.addColumn("New PNC visit for babies after 6 weeks", "", ReportUtils.map(pnc.pncBabyNewVisitAfter6Weeks(), indParams), "");
        cohortDsd.addColumn("Initial test at PNC", "", ReportUtils.map(pnc.initialTestsAtPNC(), indParams), "");
        cohortDsd.addColumn("HIV Positive result at PNC", "", ReportUtils.map(pnc.hivPositiveResultAtPNC(), indParams), "");
        cohortDsd.addColumn("Partner tested at PNC", "", ReportUtils.map(pnc.partnerTestedAtPNC(), indParams), "");
        EmrReportingUtils.addRow(cohortDsd, "HIV Positive adolescents at PNC", "", ReportUtils.map(pncIndicators.hivPositiveResultAtPNC(), indParams), pncAdolescentsAgeDisaggregation, Arrays.asList("01", "02"));
        EmrReportingUtils.addRow(cohortDsd, "Started HAART (Adolescents)", "", ReportUtils.map(pncIndicators.startedHAARTPNC(), indParams), pncAdolescentsAgeDisaggregation, Arrays.asList("01", "02"));
        cohortDsd.addColumn("Started HAART at PNC", "", ReportUtils.map(pnc.startedHAARTPNC(), indParams), "");
        cohortDsd.addColumn("Infant ARV Prophylaxis", "", ReportUtils.map(pnc.infantARVProphylaxis(), indParams), "");
        cohortDsd.addColumn("Screened for Cervical Cancer (PAP)", "", ReportUtils.map(pnc.cacxPAP(), indParams), "");
        cohortDsd.addColumn("Screened for Cervical Cancer (VIA)", "", ReportUtils.map(pnc.cacxVIA(), indParams), "");
        cohortDsd.addColumn("Screened for Cervical Cancer (VILI)", "", ReportUtils.map(pnc.cacxVILI(), indParams), "");
        cohortDsd.addColumn("Screened for Cervical Cancer (HPV)", "", ReportUtils.map(pnc.cacxHPV(), indParams), "");
        cohortDsd.addColumn("Received an FP", "", ReportUtils.map(pnc.receivedFPMethod(), indParams), "");
        //Test 1 results
        cohortDsd.addColumn("HIV Negative Test_1", "", ReportUtils.map(pnc.hivNegativeTest1(), indParams), "");
        cohortDsd.addColumn("HIV Positive Test_1", "", ReportUtils.map(pnc.hivPositiveTest1(), indParams), "");
        cohortDsd.addColumn("HIV Invalid Test_1", "", ReportUtils.map(pnc.hivInvalidTest1(), indParams), "");
        cohortDsd.addColumn("HIV wasted Test_1", "", ReportUtils.map(pnc.hivWastedTest1(), indParams), "");
        //Test 2 results
        cohortDsd.addColumn("HIV Negative Test_2", "", ReportUtils.map(pnc.hivNegativeTest2(), indParams), "");
        cohortDsd.addColumn("HIV Positive Test_2", "", ReportUtils.map(pnc.hivPositiveTest2(), indParams), "");
        cohortDsd.addColumn("HIV Invalid Test_2", "", ReportUtils.map(pnc.hivInvalidTest2(), indParams), "");
        cohortDsd.addColumn("HIV wasted Test_2", "", ReportUtils.map(pnc.hivWastedTest2(), indParams), "");

        return cohortDsd;
    }
}