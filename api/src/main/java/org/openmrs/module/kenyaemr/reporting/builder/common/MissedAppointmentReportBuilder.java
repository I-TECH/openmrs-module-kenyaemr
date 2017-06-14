package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.NumberOfDaysLateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.PatientProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.TelephoneNumberCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

/**
 * Created by codehub on 10/7/15.
 * Use to list patients who missed appointment
 */
@Component
@Builds({"kenyaemr.common.report.missedAppointment"})
public class MissedAppointmentReportBuilder extends CalculationReportBuilder {

    @Override
    protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), new IdentifierConverter());

        addStandardColumns(report, dsd);
        dsd.addColumn("UPN", identifierDef, "");
        dsd.addColumn("Appointment date", new CalculationDataDefinition("Appointment date", new LastReturnVisitDateCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Number of days late", new CalculationDataDefinition("Number of days late", new NumberOfDaysLateCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Phone number", new CalculationDataDefinition("Phone number", new TelephoneNumberCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Program", new CalculationDataDefinition("Program", new PatientProgramCalculation()), "", new CalculationResultConverter());

    }
}
