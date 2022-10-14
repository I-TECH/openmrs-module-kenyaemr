package org.openmrs.module.kenyaemr.reporting.builder.sgbv;
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.TelephoneNumberCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.sgbv.SGBVLineListCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLNextAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.SgbvLastScreeningDateDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.common.report.sgbvlinelist"})
public class SGBVLineListReportBuilder extends AbstractHybridReportBuilder {
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
    protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {
    }

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        return null;
    }


    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new SGBVLineListCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("SGBV Patients Linelist");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allVisits = sgbvPatientsLinelistDataSetDefinition("sgbvLinelistPatients");
        allVisits.addRowFilter(allPatientsCohort());
        DataSetDefinition allPatientsDSD = allVisits;

        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected PatientDataSetDefinition sgbvPatientsLinelistDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);
        dsd.addSortCriteria("DOBAndAge", SortCriteria.SortDirection.DESC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String defParam = "startDate=${startDate},endDate=${endDate}";
        ETLNextAppointmentDateDataDefinition nextAppointmentDateDataDefinition = new ETLNextAppointmentDateDataDefinition();
        nextAppointmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        SgbvLastScreeningDateDataDefinition sgbvLastScreeningDateDataDefinition = new SgbvLastScreeningDateDataDefinition();
        sgbvLastScreeningDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("CCC No", identifierDef, "");
        dsd.addColumn("Phone number", new CalculationDataDefinition("Phone number", new TelephoneNumberCalculation()), "", new DataConverter[]{new CalculationResultConverter()});
        dsd.addColumn("Last Screening Date", sgbvLastScreeningDateDataDefinition,"endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Next Appointment Date", nextAppointmentDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));

        SGBVLineListCohortDefinition cd = new SGBVLineListCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, defParam);
        return dsd;
    }
}
