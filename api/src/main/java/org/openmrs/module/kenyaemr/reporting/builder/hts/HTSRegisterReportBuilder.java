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

package org.openmrs.module.kenyaemr.reporting.builder.hts;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.EverTestedForHIVDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.FinalResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HIVTestOneDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HIVTestTwoDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSDiscordanceDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSLinkageToCareDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSProviderDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSRemarksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSSelfTestDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSTBScreeningDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSTestStrategyDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.IndividualORCoupleTestDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KenyaEMRMaritalStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PatientConsentDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PatientDisabilityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.VisitDateDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.VisitDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Builds({"kenyaemr.hiv.report.htsRegister"})
public class HTSRegisterReportBuilder extends AbstractReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList();
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(datasetColumns(), "")
        );
    }

    protected DataSetDefinition datasetColumns() {
        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.setName("HTSInformation");
        dsd.setDescription("Visit information");

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
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

        dsd.addColumn("Visit Date", new VisitDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
        // new columns
        dsd.addColumn("Population Type", new PopulationTypeDataDefinition(), null);
        dsd.addColumn("everTested", new EverTestedForHIVDataDefinition(), null);
        dsd.addColumn("disability", new PatientDisabilityDataDefinition(), null);
        dsd.addColumn("consent", new PatientConsentDataDefinition(), null);
        dsd.addColumn("clientTestedAs", new IndividualORCoupleTestDataDefinition(), null);
        dsd.addColumn("testingStrategy", new HTSTestStrategyDataDefinition(), null);
        dsd.addColumn("hivTest1", new HIVTestOneDataDefinition(), null);
        dsd.addColumn("hivTest2", new HIVTestTwoDataDefinition(), null);
        dsd.addColumn("finalResult", new FinalResultDataDefinition(), null);
        dsd.addColumn("coupleDiscordant", new HTSDiscordanceDataDefinition(), null);
        dsd.addColumn("tbScreening", new HTSTBScreeningDataDefinition(), null);
        dsd.addColumn("everHadHIVSelfTest", new HTSSelfTestDataDefinition(), null);
        dsd.addColumn("provider", new HTSProviderDataDefinition(), null);
        dsd.addColumn("remarks", new HTSRemarksDataDefinition(), null);

        dsd.addRowFilter(new HTSRegisterCohortDefinition(), "");
        return dsd;

    }
}