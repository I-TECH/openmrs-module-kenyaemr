/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.VMMCRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.SubCountyAddressCalculation;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.KPTypeConverter;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ActivePatientsPopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCCircumcisionDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCHivStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCEndTimeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCStartTimeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCMethodDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCAdverseEventsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCAdverseEventsTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCAESeverityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCReferredByDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCTheatreRegisterNumDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCAssistantCadreDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCAssistantDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCCadreDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCDaysSinceSurgeryDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCFollowUpVisitDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCPostSurgeryAEDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCPostSurgeryAETypesDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCPostSurgerySeverityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCSourceDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.vmmc.VMMCSurgeonDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.PhysicalAddressDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.dataset.definition.VisitDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.common.report.VMMCRegister"})
public class VMMCRegisterReportBuilder extends AbstractHybridReportBuilder {
    public static final String ENC_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT = "dd/MM/yyyy";


    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        return allPatientsCohort();
    }

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new VMMCRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("VMMCInformation");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition vmmcPatients = vmmcDataSetDefinition();
        vmmcPatients.addRowFilter(allPatientsCohort());
        DataSetDefinition vmmcPatientsAll = vmmcPatients;

        return Arrays.asList(
                ReportUtils.map(vmmcPatientsAll, "startDate=${startDate},endDate=${endDate}")
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

    protected PatientDataSetDefinition vmmcDataSetDefinition() {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition("VMMCInformation");
        dsd.addSortCriteria("DOBAndAge", SortCriteria.SortDirection.DESC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        String defParam = "startDate=${startDate}";

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        PatientIdentifierType nationalId = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nationalId.getName(), nationalId), identifierFormatter);

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        dsd.addColumn("Date of circumcision", new VMMCCircumcisionDateDataDefinition(), "");
        dsd.addColumn("Theatre Register Number", new VMMCTheatreRegisterNumDataDefinition(), "");
        dsd.addColumn("ID Number", identifierDef, "");
        dsd.addColumn("Referred By", new VMMCReferredByDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Age in years", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Sub-County", new CalculationDataDefinition("Sub-County", new SubCountyAddressCalculation()), "", null);
        dsd.addColumn("HIV Status", new VMMCHivStatusDataDefinition(), "");
        dsd.addColumn("Method", new VMMCMethodDataDefinition(), "");
        dsd.addColumn("Start Time", new VMMCStartTimeDataDefinition(), "");
        dsd.addColumn("End Time", new VMMCEndTimeDataDefinition(), "");
        dsd.addColumn("Population Type", new ActivePatientsPopulationTypeDataDefinition(), "");
        dsd.addColumn("AE During Surgery", new VMMCAdverseEventsDataDefinition(), "");
        dsd.addColumn("Type of Adverse Event", new VMMCAdverseEventsTypeDataDefinition(), "");
        dsd.addColumn("Severity of AE", new VMMCAESeverityDataDefinition(), "");
        dsd.addColumn("Surgeon", new VMMCSurgeonDataDefinition(), "");
        dsd.addColumn("Cadre", new VMMCCadreDataDefinition(), "");
        dsd.addColumn("Assistant", new VMMCAssistantDataDefinition(), "");
        dsd.addColumn("AssistantCadre", new VMMCAssistantCadreDataDefinition(), "");
        dsd.addColumn("Follow Up Visit", new VMMCFollowUpVisitDataDefinition(), "");
        dsd.addColumn("Number of Days since surgery", new VMMCDaysSinceSurgeryDataDefinition(), "");
        dsd.addColumn("AE Post Surgery", new VMMCPostSurgeryAEDataDefinition(), "");
        dsd.addColumn("AE Type Post Surgery", new VMMCPostSurgeryAETypesDataDefinition(), "");
        dsd.addColumn("Severity Post Surgery", new VMMCPostSurgerySeverityDataDefinition(), "");
        dsd.addColumn("Source", new VMMCSourceDataDefinition(), "");

        return dsd;
    }
}