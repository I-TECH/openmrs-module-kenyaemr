/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.NumberOfDaysLateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.TelephoneNumberCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.PatientProgramEnrollmentCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.EncounterDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.PatientProgramEnrollmentConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLLTFUWithDateRangeCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLLostToFollowupCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ScheduledAppointmentCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultDateYYMMDDConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.AppointmentDaysMissedDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HonouredAppointmentDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PatientDisabilityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLNextAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.BookingDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.LastDefaulterTracingDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.ReturnToCareDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.IPTIndicationDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Refactored by patryllus on 12/1/21.
 */
@Component
@Builds({"kenyaemr.hiv.report.lostToFollowUp"})
public class ETLLtfuReportBuilder extends AbstractHybridReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    String paramMapping = "startDate=${startDate},endDate=${endDate}";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    /**
     *
     * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(org.openmrs.module.kenyacore.report.CohortReportDescriptor, PatientDataSetDefinition)
     */
    @Override
    protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addSortCriteria("Number of days late", SortCriteria.SortDirection.ASC);
        dsd.setName("lostToFollowup");
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Unique Patient No", identifierDef, "");
        dsd.addColumn("Age", new AgeDataDefinition(), "", new DataConverter[0]);
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new DataConverter[0]);
        dsd.addColumn("Phone number", new CalculationDataDefinition("Phone number", new TelephoneNumberCalculation()), "", new DataConverter[]{new CalculationResultConverter()});
        dsd.addColumn("Last Visit Date", new ETLLastVisitDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Last Appointment Date", new ETLNextAppointmentDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Last Tracing Date", new LastDefaulterTracingDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Return to Care Date", new BookingDateDataDefinition(),"", new DateConverter(DATE_FORMAT));

        AppointmentDaysMissedDataDefinition daysMissed = new AppointmentDaysMissedDataDefinition();
        daysMissed.addParameter(new Parameter("startDate", "Start Date", Date.class));
        daysMissed.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addColumn("Number of days late", daysMissed, paramMapping);
        dsd.addColumn("Program", new CalculationDataDefinition("Program", new PatientProgramEnrollmentCalculation()), "", new PatientProgramEnrollmentConverter());
      }

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        CohortDefinition cd = new ETLLTFUWithDateRangeCohortDefinition();
        cd.setName("Lost to Followup on Date");
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return ReportUtils.map(cd, paramMapping);
    }
}
