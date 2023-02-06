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
import org.openmrs.module.kenyaemr.calculation.library.hiv.SubCountyAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.PNCRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.AgeAtReportingDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.*;
import org.openmrs.module.kenyaemr.reporting.library.pmtct.PMTCTIndicatorLibrary;
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
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
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
    private PMTCTIndicatorLibrary pnc;

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private PMTCTIndicatorLibrary pncIndicators;

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

        PNCRegisterNumberDataDefinition pncRegisterNumberDataDefinition = new PNCRegisterNumberDataDefinition();
        pncRegisterNumberDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncRegisterNumberDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCVisitNumberDataDefinition pncVisitNumberDataDefinition = new PNCVisitNumberDataDefinition();
        pncVisitNumberDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncVisitNumberDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCDeliveryDateDataDefinition pncDeliveryDateDataDefinition = new PNCDeliveryDateDataDefinition();
        pncDeliveryDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncDeliveryDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPlaceOfDeliveryDataDefinition pncPlaceOfDeliveryDataDefinition = new PNCPlaceOfDeliveryDataDefinition();
        pncPlaceOfDeliveryDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPlaceOfDeliveryDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCModeOfDeliveryDataDefinition pncModeOfDeliveryDataDefinition = new PNCModeOfDeliveryDataDefinition();
        pncModeOfDeliveryDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncModeOfDeliveryDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCMotherPostpartumVisitTimingDataDefinition pncMotherPostpartumVisitTimingDataDefinition = new PNCMotherPostpartumVisitTimingDataDefinition();
        pncMotherPostpartumVisitTimingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncMotherPostpartumVisitTimingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCBabyPostpartumVisitTimingDataDefinition pncBabyPostpartumVisitTimingDataDefinition = new PNCBabyPostpartumVisitTimingDataDefinition();
        pncBabyPostpartumVisitTimingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncBabyPostpartumVisitTimingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCTemperatureDataDefinition pncTemperatureDataDefinition = new PNCTemperatureDataDefinition();
        pncTemperatureDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncTemperatureDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPulseDataDefinition pncPulseDataDefinition  = new PNCPulseDataDefinition();
        pncPulseDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPulseDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCBloodPressureDataDefinition pncBloodPressureDataDefinition = new PNCBloodPressureDataDefinition();
        pncBloodPressureDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncBloodPressureDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPallorExaminationDataDefinition pncPallorExaminationDataDefinition = new PNCPallorExaminationDataDefinition();
        pncPallorExaminationDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPallorExaminationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPallorSeverityDataDefinition pncPallorSeverityDataDefinition = new PNCPallorSeverityDataDefinition();
        pncPallorSeverityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPallorSeverityDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCBreastExaminationDataDefinition pncBreastExaminationDataDefinition = new PNCBreastExaminationDataDefinition();
        pncBreastExaminationDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncBreastExaminationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCUterusExaminationDataDefinition pncUterusExaminationDataDefinition = new PNCUterusExaminationDataDefinition();
        pncUterusExaminationDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncUterusExaminationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCBreastFeedingDataDefinition pncBreastFeedingDataDefinition = new PNCBreastFeedingDataDefinition();
        pncBreastFeedingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncBreastFeedingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPPHExaminationDataDefinition pncpphExaminationDataDefinition = new PNCPPHExaminationDataDefinition();
        pncpphExaminationDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncpphExaminationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCCSectionSiteDataDefinition pnccSectionSiteDataDefinition = new PNCCSectionSiteDataDefinition();
        pnccSectionSiteDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pnccSectionSiteDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCLochiaDataDefinition pncLochiaDataDefinition = new PNCLochiaDataDefinition();
        pncLochiaDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncLochiaDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCEpisiotomyDataDefinition pncEpisiotomyDataDefinition = new PNCEpisiotomyDataDefinition();
        pncEpisiotomyDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncEpisiotomyDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCFistulaScreeningDataDefinition pncFistulaScreeningDataDefinition = new PNCFistulaScreeningDataDefinition();
        pncFistulaScreeningDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncFistulaScreeningDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCTBScreeningDataDefinition pnctbScreeningDataDefinition = new PNCTBScreeningDataDefinition();
        pnctbScreeningDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pnctbScreeningDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPriorKnownStatusDataDefinition pncPriorKnownStatusDataDefinition = new PNCPriorKnownStatusDataDefinition();
        pncPriorKnownStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPriorKnownStatusDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCTestedForHIVAtPNCDataDefinition pncTestedForHIVAtPNCDataDefinition = new PNCTestedForHIVAtPNCDataDefinition();
        pncTestedForHIVAtPNCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncTestedForHIVAtPNCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCTestOneResultsDataDefinition pncTestOneResultsDataDefinition = new PNCTestOneResultsDataDefinition();
        pncTestOneResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncTestOneResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCTestTwoResultsDataDefinition pncTestTwoResultsDataDefinition = new PNCTestTwoResultsDataDefinition();
        pncTestTwoResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncTestTwoResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCHIVResultsWithin6WeeksDataDefinition pnchivResultsWithin6WeeksDataDefinition = new PNCHIVResultsWithin6WeeksDataDefinition();
        pnchivResultsWithin6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pnchivResultsWithin6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCHIVResultsGreaterThan6WeeksDataDefinition pnchivResultsGreaterThan6WeeksDataDefinition = new PNCHIVResultsGreaterThan6WeeksDataDefinition();
        pnchivResultsGreaterThan6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pnchivResultsGreaterThan6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCNVPAndAZTForBabyWithin6WeeksDataDefinition pncnvpAndAZTForBabyWithin6WeeksDataDefinition = new PNCNVPAndAZTForBabyWithin6WeeksDataDefinition();
        pncnvpAndAZTForBabyWithin6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncnvpAndAZTForBabyWithin6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCHAARTForMotherWithin6WeeksDataDefinition pnchaartForMotherWithin6WeeksDataDefinition = new PNCHAARTForMotherWithin6WeeksDataDefinition();
        pnchaartForMotherWithin6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pnchaartForMotherWithin6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCNVPAndAZTForBabyGreaterThan6WeeksDataDefinition pncnvpAndAZTForBabyGreaterThan6WeeksDataDefinition = new PNCNVPAndAZTForBabyGreaterThan6WeeksDataDefinition();
        pncnvpAndAZTForBabyGreaterThan6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncnvpAndAZTForBabyGreaterThan6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCHAARTForMotherGreaterThan6WeeksDataDefinition pnchaartForMotherGreaterThan6WeeksDataDefinition = new PNCHAARTForMotherGreaterThan6WeeksDataDefinition();
        pnchaartForMotherGreaterThan6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pnchaartForMotherGreaterThan6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCCoupleCounselledDataDefinition pncCoupleCounselledDataDefinition = new PNCCoupleCounselledDataDefinition();
        pncCoupleCounselledDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncCoupleCounselledDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPartnerTestedHIVInPNCDataDefinition pncPartnerTestedHIVInPNCDataDefinition = new PNCPartnerTestedHIVInPNCDataDefinition();
        pncPartnerTestedHIVInPNCDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPartnerTestedHIVInPNCDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCPartnerHIVResultsDataDefinition pncPartnerHIVResultsDataDefinition = new PNCPartnerHIVResultsDataDefinition();
        pncPartnerHIVResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncPartnerHIVResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCCervicalCancerScreeningDataDefinition pncCervicalCancerScreeningDataDefinition = new PNCCervicalCancerScreeningDataDefinition();
        pncCervicalCancerScreeningDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncCervicalCancerScreeningDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCCervicalCancerScreeningMethodDataDefinition pncCervicalCancerScreeningMethodDataDefinition = new PNCCervicalCancerScreeningMethodDataDefinition();
        pncCervicalCancerScreeningMethodDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncCervicalCancerScreeningMethodDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCCervicalCancerScreeningResultsDataDefinition pncCervicalCancerScreeningResultsDataDefinition = new PNCCervicalCancerScreeningResultsDataDefinition();
        pncCervicalCancerScreeningResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncCervicalCancerScreeningResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCExerciseGivenDataDefinition pncExerciseGivenDataDefinition = new PNCExerciseGivenDataDefinition();
        pncExerciseGivenDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncExerciseGivenDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCModernFPCounsellingDataDefinition pncModernFPCounsellingDataDefinition = new PNCModernFPCounsellingDataDefinition();
        pncModernFPCounsellingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncModernFPCounsellingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCModernFPWithin6WeeksDataDefinition pncModernFPWithin6WeeksDataDefinition = new PNCModernFPWithin6WeeksDataDefinition();
        pncModernFPWithin6WeeksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncModernFPWithin6WeeksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCDiagnosisDataDefinition pncDiagnosisDataDefinition = new PNCDiagnosisDataDefinition();
        pncDiagnosisDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncDiagnosisDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCHaematinicsDataDefinition pncHaematinicsDataDefinition = new PNCHaematinicsDataDefinition();
        pncHaematinicsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncHaematinicsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCReferredFromDataDefinition pncReferredFromDataDefinition = new PNCReferredFromDataDefinition();
        pncReferredFromDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncReferredFromDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCReferredToDataDefinition pncReferredToDataDefinition = new PNCReferredToDataDefinition();
        pncReferredToDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncReferredToDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        PNCRemarksDataDefinition pncRemarksDataDefinition = new PNCRemarksDataDefinition();
        pncRemarksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        pncRemarksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        AgeAtReportingDataDefinition ageAtReportingDataDefinition = new AgeAtReportingDataDefinition();
        ageAtReportingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ageAtReportingDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Unique Patient Number", identifierDef, null);
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(), "", new DateConverter(ENC_DATE_FORMAT));
        // new columns
        dsd.addColumn("PNC Number", pncRegisterNumberDataDefinition, paramMapping);
        dsd.addColumn("Visit Number", pncVisitNumberDataDefinition,  paramMapping);
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Age", ageAtReportingDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Sub County", new CalculationDataDefinition("Subcounty", new SubCountyAddressCalculation()), "",new CalculationResultConverter());
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Delivery Date", pncDeliveryDateDataDefinition,  paramMapping, new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Place of Delivery", pncPlaceOfDeliveryDataDefinition,  paramMapping);
        dsd.addColumn("Mode of Delivery", pncModeOfDeliveryDataDefinition,  paramMapping);
        dsd.addColumn("Postpartum visit timing Mother", pncMotherPostpartumVisitTimingDataDefinition,  paramMapping);
        dsd.addColumn("Postpartum visit timing Baby", pncBabyPostpartumVisitTimingDataDefinition,  paramMapping);
        dsd.addColumn("Temperature", pncTemperatureDataDefinition,  paramMapping);
        dsd.addColumn("Pulse", pncPulseDataDefinition,  paramMapping);
        dsd.addColumn("Blood Pressure", pncBloodPressureDataDefinition,  paramMapping);
        dsd.addColumn("Pallor", pncPallorExaminationDataDefinition,  paramMapping);
        dsd.addColumn("Pallor Severity", pncPallorSeverityDataDefinition,  paramMapping);
        dsd.addColumn("Breast Examination", pncBreastExaminationDataDefinition,  paramMapping);
        dsd.addColumn("Uterus Examination", pncUterusExaminationDataDefinition,  paramMapping);
        dsd.addColumn("Breast Feeding", pncBreastFeedingDataDefinition,  paramMapping);
        dsd.addColumn("PPH Examination", pncpphExaminationDataDefinition,  paramMapping);
        dsd.addColumn("C-Section site", pnccSectionSiteDataDefinition,  paramMapping);
        dsd.addColumn("Lochia", pncLochiaDataDefinition,  paramMapping);
        dsd.addColumn("Episiotomy", pncEpisiotomyDataDefinition,  paramMapping);
        dsd.addColumn("Fistula Screening", pncFistulaScreeningDataDefinition,  paramMapping);
        dsd.addColumn("TB Screening", pnctbScreeningDataDefinition,  paramMapping);
        dsd.addColumn("Prior Known HIV Status", pncPriorKnownStatusDataDefinition,  paramMapping);
        dsd.addColumn("Tested HIV at PNC", pncTestedForHIVAtPNCDataDefinition,  paramMapping);
        dsd.addColumn("Test 1 Results", pncTestOneResultsDataDefinition,  paramMapping);
        dsd.addColumn("Test 2 Results", pncTestTwoResultsDataDefinition,  paramMapping);
        dsd.addColumn("HIV Results <=6 weeks", pnchivResultsWithin6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("HIV Results >6 weeks", pnchivResultsGreaterThan6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("NVP and AZT for Baby <=6 weeks", pncnvpAndAZTForBabyWithin6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("HAART For mother <=6 weeks", pnchaartForMotherWithin6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("NVP and AZT for Baby >6 weeks", pncnvpAndAZTForBabyGreaterThan6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("HAART For mother >6 weeks", pnchaartForMotherGreaterThan6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("Couple counselled", pncCoupleCounselledDataDefinition,  paramMapping);
        dsd.addColumn("Partner Tested for HIV in PNC", pncPartnerTestedHIVInPNCDataDefinition,  paramMapping);
        dsd.addColumn("Partner HIV Results", pncPartnerHIVResultsDataDefinition,  paramMapping);
        dsd.addColumn("Cervical Cancer Screening", pncCervicalCancerScreeningDataDefinition,  paramMapping);
        dsd.addColumn("Cervical Cancer Screening Method", pncCervicalCancerScreeningMethodDataDefinition,  paramMapping);
        dsd.addColumn("Cervical Cancer Screening Results", pncCervicalCancerScreeningResultsDataDefinition,  paramMapping);
        dsd.addColumn("Exercise Given", pncExerciseGivenDataDefinition,  paramMapping);
        dsd.addColumn("Counselled on Modern Post Partum FP", pncModernFPCounsellingDataDefinition,  paramMapping);
        dsd.addColumn("Received Modern Post Partum FP",pncModernFPWithin6WeeksDataDefinition,  paramMapping);
        dsd.addColumn("Diagnosis", pncDiagnosisDataDefinition,  paramMapping);
        dsd.addColumn("Haemanitics", pncHaematinicsDataDefinition,  paramMapping);
        dsd.addColumn("Referred From", pncReferredFromDataDefinition,  paramMapping);
        dsd.addColumn("Referred To", pncReferredToDataDefinition,  paramMapping);
        dsd.addColumn("Remarks",pncRemarksDataDefinition,  paramMapping);

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

        cohortDsd.addColumn("New PNC Visit", "", ReportUtils.map(pnc.newClientsPNC(), indParams), "");
        cohortDsd.addColumn("PNC revisits", "", ReportUtils.map(pnc.revisitsPNC(), indParams), "");
        cohortDsd.addColumn("New PNC visit within 48 hrs", "", ReportUtils.map(pnc.pncMotherNewVisitWithin48hrs(), indParams), "");
        cohortDsd.addColumn("New PNC visit between 3 days and under 6 weeks", "", ReportUtils.map(pnc.pncMotherNewVisitBetween3DaysUnder6Weeks(), indParams), "");
        cohortDsd.addColumn("New PNC visit after 6 weeks", "", ReportUtils.map(pnc.pncMotherNewVisitAfter6Weeks(), indParams), "");
        cohortDsd.addColumn("New PNC visit for babies within 48 hrs", "", ReportUtils.map(pnc.pncBabyNewVisitWithin48hrs(), indParams), "");
        cohortDsd.addColumn("New PNC visit for babies between 3 days and under 6 weeks", "", ReportUtils.map(pnc.pncBabyNewVisitBetween3DaysUnder6Weeks(), indParams), "");
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