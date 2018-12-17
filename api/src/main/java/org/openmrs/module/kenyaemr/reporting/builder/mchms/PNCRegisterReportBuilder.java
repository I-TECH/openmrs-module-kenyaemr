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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.PNCRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.*;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.*;
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
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.mchms.report.pncRegister"})
public class PNCRegisterReportBuilder extends AbstractReportBuilder {
    public static final String ENC_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

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
                ReportUtils.map(datasetColumns(), "startDate=${startDate},endDate=${endDate}")
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

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
       // dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        //dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Marital Status", new KenyaEMRMaritalStatusDataDefinition(), null);
        dsd.addColumn("Unique Patient Number", identifierDef, null);

        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        // new columns
        dsd.addColumn("Visit Number", new PNCVisitNumberDataDefinition(),"");
        dsd.addColumn("Date of Birth", new PNCDateOfBirthAndAgeDataDefinition(),"");
        dsd.addColumn("Admission Number", new PNCAdmissionNumberDataDefinition(),"");
        dsd.addColumn("Breast Examination", new PNCBreastExaminationDataDefinition(),"");
        dsd.addColumn("Breast Feeding", new PNCBreastFeedingDataDefinition(),"");
        dsd.addColumn("Cervical Cancer Screening", new PNCCervicalCancerScreeningMethodDataDefinition(),"");
        dsd.addColumn("Cervical Cancer Screening Results", new PNCCervicalCancerScreeningResultsDataDefinition(),"");
        dsd.addColumn("C-Section site", new PNCCSectionSiteDataDefinition(),"");
        dsd.addColumn("Delivery Date", new PNCDeliveryDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Diagnosis", new PNCDiagnosisDataDefinition(),"");
        dsd.addColumn("Episiotomy", new PNCEpisiotomyDataDefinition(),"");
        dsd.addColumn("Exercise Given", new PNCExerciseGivenDataDefinition(),"");
        dsd.addColumn("Fistula Screening", new PNCFistulaScreeningDataDefinition(),"");
        dsd.addColumn("HAART For mother >6 weeks", new PNCHAARTForMotherGreaterThan6WeeksDataDefinition(),"");
        dsd.addColumn("HAART For mother <=6 weeks", new PNCHAARTForMotherWithin6WeeksDataDefinition(),"");
        dsd.addColumn("Haemanitics", new PNCHaematinicsDataDefinition(),"");
        dsd.addColumn("HIV Results >6 weeks", new PNCHIVResultsGreaterThan6WeeksDataDefinition(),"");
        dsd.addColumn("HIV Results <=6 weeks", new PNCHIVResultsWithin6WeeksDataDefinition(),"");
        dsd.addColumn("Infections", new PNCInfectionsDataDefinition(),"");
        dsd.addColumn("Lochia", new PNCLochiaDataDefinition(),"");
        dsd.addColumn("Mode of Delivery", new PNCModeOfDeliveryDataDefinition(),"");
        dsd.addColumn("Modern FP <=6 weeks", new PNCModernFPWithin6WeeksDataDefinition(),"");
        dsd.addColumn("NVP and AZT for Baby <=6 weeks", new PNCNVPAndAZTForBabyWithin6WeeksDataDefinition(),"");
        dsd.addColumn("NVP and AZT for Baby >6 weeks", new PNCNVPAndAZTForBabyGreaterThan6WeeksDataDefinition(),"");
        dsd.addColumn("Pallor", new PNCPallorExaminationDataDefinition(),"");
        dsd.addColumn("Partner Tested for HIV in PNC", new PNCPartnerTestedHIVInPNCDataDefinition(),"");
        dsd.addColumn("Partner HIV Results", new PNCPartnerHIVResultsDataDefinition(),"");
        dsd.addColumn("Place of Delivery", new PNCPlaceOfDeliveryDataDefinition(),"");
        dsd.addColumn("Postpartum visit timing", new PNCPostpartumVisitTimingDataDefinition(),"");
        dsd.addColumn("PPH Examination", new PNCPPHExaminationDataDefinition(),"");
        dsd.addColumn("Prior Known HIV Status", new PNCPriorKnownStatusDataDefinition(),"");
        dsd.addColumn("Pulse", new PNCPulseDataDefinition(),"");
        dsd.addColumn("Blood Pressure", new PNCBloodPressureDataDefinition(),"");
        dsd.addColumn("Referred From", new PNCReferredFromDataDefinition(),"");
        dsd.addColumn("Referred To", new PNCReferredToDataDefinition(),"");
        dsd.addColumn("Register Number", new PNCRegisterNumberDataDefinition(),"");
        dsd.addColumn("Remarks", new PNCRemarksDataDefinition(),"");
        dsd.addColumn("Temperature", new PNCTemperatureDataDefinition(),"");
        dsd.addColumn("Tested HIV at PNC", new PNCTestedForHIVAtPNCDataDefinition(),"");
        dsd.addColumn("Test 1 Results", new PNCTestOneResultsDataDefinition(),"");
        dsd.addColumn("Test 2 Results", new PNCTestTwoResultsDataDefinition(),"");
        dsd.addColumn("Uterus Examination", new PNCUterusExaminationDataDefinition(),"");

        PNCRegisterCohortDefinition cd = new PNCRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));

        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;

    }
}