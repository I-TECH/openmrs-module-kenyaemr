/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.cacx;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.CACXRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXCountyDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXNationalIdDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXPopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXVisitTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXScreeningResultsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXFollowUpDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXReferralDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXRemarksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.CACXHivStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.KPTypeConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.*;
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
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.cacx.report.cacxRegister"})
public class CACXRegisterReportBuilder extends AbstractReportBuilder {
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
        dsd.setName("CACXInformation");
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
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Visit Type", new CACXVisitTypeDataDefinition(), null);
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("ID Number", new CACXNationalIdDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Phone Number", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Age in years", new AgeDataDefinition(), "");

        CACXHivStatusDataDefinition cacxHivStatusDataDefinition = new CACXHivStatusDataDefinition();
        cacxHivStatusDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cacxHivStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addColumn("HIV Status", cacxHivStatusDataDefinition, paramMapping, null);

        CACXPopulationTypeDataDefinition cacxPopulationTypeDataDefinition = new CACXPopulationTypeDataDefinition();
        cacxPopulationTypeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cacxPopulationTypeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addColumn("Population Type", cacxPopulationTypeDataDefinition, paramMapping, null);

        dsd.addColumn("County of Residence", new CACXCountyDataDefinition(), null);

        CACXScreeningResultsDataDefinition cacxScreeningResultsDataDefinition = new CACXScreeningResultsDataDefinition();
        cacxScreeningResultsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cacxScreeningResultsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addColumn("Screening Modalities", cacxScreeningResultsDataDefinition, paramMapping, null);

        CACXFollowUpDateDataDefinition cacxFollowUpDateDataDefinition = new CACXFollowUpDateDataDefinition();
        cacxFollowUpDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cacxFollowUpDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addColumn("Follow Up Date", cacxFollowUpDateDataDefinition, paramMapping, null);

        CACXReferralDataDefinition cacxReferralDataDefinition = new CACXReferralDataDefinition();
        cacxReferralDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cacxReferralDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addColumn("Refferal To/From", cacxReferralDataDefinition, paramMapping, null);

        CACXRemarksDataDefinition cacxRemarksDataDefinition = new CACXRemarksDataDefinition();
        cacxRemarksDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cacxRemarksDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addColumn("Remarks", cacxRemarksDataDefinition, paramMapping, null);

        CACXRegisterCohortDefinition cd = new CACXRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;
    }
}