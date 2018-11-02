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
@Builds({"kenyaemr.mchms.report.ancRegister"})
public class ANCRegisterReportBuilder extends AbstractReportBuilder {
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

        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Marital Status", new KenyaEMRMaritalStatusDataDefinition(), null);
        dsd.addColumn("Unique Patient Number", identifierDef, null);

        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        // new columns
        dsd.addColumn("Visit Number", new ANCVisitNumberDataDefinition(),"");
        dsd.addColumn("First ANC Visit", new FirstANCVisitDataDefinition(),"");
        dsd.addColumn("Number of ANC Visits", new ANCNumberOfVisitsDataDefinition(),"");
        dsd.addColumn("Parity", new ANCParityDataDefinition(),"");
        dsd.addColumn("Gravida", new ANCGravidaDataDefinition(),"");
        dsd.addColumn("Height", new ANCHeightDataDefinition(),"");
        dsd.addColumn("LMP", new ANCLmpDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Gestation", new ANCGestationDataDefinition(),"");
        dsd.addColumn("Ultra Sound", new ANCEDDUltrasoundDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Weight", new ANCWeightDataDefinition(),"");
        dsd.addColumn("Blood Pressure", new ANCBloodPressureDataDefinition(),"");
        dsd.addColumn("Breast Exam", new ANCBreastExamDoneDataDefinition(),"");
        dsd.addColumn("Counselled", new ANCCounselledDoneDataDefinition(),"");
        dsd.addColumn("VDRL Done", new ANCVDRLDoneDataDefinition(),"");
        dsd.addColumn("VDRL Results", new ANCVDRLResultsDataDefinition(),"");
        dsd.addColumn("VDRL Treated", new ANCVDRLTreatedDataDefinition(),"");
        dsd.addColumn("HIV Status before First ANC", new ANCHIVStatusBeforeFirstANCDataDefinition(),"");
        dsd.addColumn("HIV Test One", new ANCHIVTestOneDataDefinition(),"");
        dsd.addColumn("HIV Test Two", new ANCHIVTestTwoDataDefinition(),"");
        dsd.addColumn("HIV Test Results", new ANCFinalTestResultsDataDefinition(),"");
        dsd.addColumn("WHO Stage", new ANCWHOStageDataDefinition(),"");
        dsd.addColumn("NVP Dispensed", new ANCNVPDispensedDataDefinition(),"");
        dsd.addColumn("TB Screening", new ANCTBScreeningResultsDataDefinition(),"");
        dsd.addColumn("CaCx Screening", new ANCCaCxScreeningResultsDataDefinition(),"");
        dsd.addColumn("Other Illnesses", new ANCOtherIllnessesDataDefinition(),"");
        dsd.addColumn("ANC Excercises", new ANCExercisesDataDefinition(),"");
        dsd.addColumn("Partner Tested for HIV", new ANCPartnerTestedForHivDataDefinition(),"");
        dsd.addColumn("Partner HIV Status", new ANCPartnerHIVStatusDataDefinition(),"");
        dsd.addColumn("Facility Referred From", new ANCFacilityReferredFromDataDefinition(),"");
        dsd.addColumn("Facility Referred To", new ANCFacilityReferredToDataDefinition(),"");
        dsd.addColumn("Next Appointment Date", new ANCNextAppointmentDateDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Clinical Notes", new ANCClinicalNotesDataDefinition(),"");
        dsd.addColumn("WHO Stage", new ANCWHOStageDataDefinition(),"");
        dsd.addColumn("Haemoglobin", new ANCHaemoglobinDataDefinition(),"");
        dsd.addColumn("Prophylaxis Given", new ANCProphylaxisGivenDataDefinition(),"");
        dsd.addColumn("NVP Dispensed", new ANCNVPDispensedDataDefinition(),"");
        dsd.addColumn("AZT Dispensed", new ANCAZTDispensedDataDefinition(),"");
        dsd.addColumn("VL Test Results", new ANCVLTestResultsDataDefinition(),"");
        dsd.addColumn("Deworming", new ANCDewormingDataDefinition(),"");
        dsd.addColumn("IPT malaria", new ANCIPTmalariaDataDefinition(),"");
        dsd.addColumn("TTT", new ANCTTTDataDefinition(),"");
        dsd.addColumn("Suppliment", new ANCSupplimentDataDefinition(),"");
        dsd.addColumn("ITN", new ANCITNDataDefinition(),"");



        ANCRegisterCohortDefinition cd = new ANCRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;

    }
}