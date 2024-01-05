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

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.MissedAppointmentsDuringPeriodCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.AppointmentTypeNotHonouredDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCaseManagerDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentDaysMissedDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentEffectiveDiscontinuationDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentInterruptionDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentLastDateBookedDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentLastTracingCommentsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentLastTracingDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentLastTracingOutcomeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentRTCDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentTracingAttemptsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentTracingFinalOutcomeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentTracingMethodsDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
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
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.common.report.missedAppointmentTrackerReport"})
public class MissedAppointmentsTrackerReportBuilder extends AbstractHybridReportBuilder {
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

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor hybridReportDescriptor, PatientDataSetDefinition patientDataSetDefinition) {
		return null;
	}

	protected DataSetDefinition datasetColumns() {
		EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
		dsd.setName("CCCDefaulterTracking");
		dsd.setDescription("Defaulter Tracking");
		dsd.addSortCriteria("No of days missed", SortCriteria.SortDirection.ASC);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		String paramMapping = "startDate=${startDate},endDate=${endDate}";

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		EncounterProviderDataDefinition providerDataDefinition = new EncounterProviderDataDefinition();
		providerDataDefinition.setSingleProvider(true);
		PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

		MissedAppointmentDateDataDefinition missedAppointmentDateDataDefinition = new MissedAppointmentDateDataDefinition();
		missedAppointmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		missedAppointmentDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		AppointmentTypeNotHonouredDataDefinition appointmentNotHonouredTypeDataDefinition = new AppointmentTypeNotHonouredDataDefinition();
		appointmentNotHonouredTypeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		appointmentNotHonouredTypeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentTracingAttemptsDataDefinition tracingAttemptsDataDefinition = new MissedAppointmentTracingAttemptsDataDefinition();
		tracingAttemptsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		tracingAttemptsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentLastTracingDateDataDefinition lastTraceDateDataDefinition = new MissedAppointmentLastTracingDateDataDefinition();
		lastTraceDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		lastTraceDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentLastTracingOutcomeDataDefinition lastTraceOutcomeDataDefinition = new MissedAppointmentLastTracingOutcomeDataDefinition();
		lastTraceOutcomeDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		lastTraceOutcomeDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentLastTracingCommentsDataDefinition lastTraceCommentsDataDefinition = new MissedAppointmentLastTracingCommentsDataDefinition();
		lastTraceCommentsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		lastTraceCommentsDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentTracingMethodsDataDefinition tracingMethods = new MissedAppointmentTracingMethodsDataDefinition();
		tracingMethods.addParameter(new Parameter("endDate", "End Date", Date.class));
		tracingMethods.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentTracingFinalOutcomeDataDefinition finalOutcome = new MissedAppointmentTracingFinalOutcomeDataDefinition();
		finalOutcome.addParameter(new Parameter("endDate", "End Date", Date.class));
		finalOutcome.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentDaysMissedDataDefinition daysMissed = new MissedAppointmentDaysMissedDataDefinition();
		daysMissed.addParameter(new Parameter("endDate", "End Date", Date.class));
		daysMissed.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentRTCDateDataDefinition rtcDate = new MissedAppointmentRTCDateDataDefinition();
		rtcDate.addParameter(new Parameter("endDate", "End Date", Date.class));
		rtcDate.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentLastDateBookedDataDefinition dateBooked = new MissedAppointmentLastDateBookedDataDefinition();
		dateBooked.addParameter(new Parameter("endDate", "End Date", Date.class));
		dateBooked.addParameter(new Parameter("startDate", "Start Date", Date.class));

		ETLCaseManagerDataDefinition etlCaseManagerDataDefinition = new ETLCaseManagerDataDefinition();
		etlCaseManagerDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		etlCaseManagerDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentInterruptionDataDefinition patientImmediateStatusAfterVisit = new MissedAppointmentInterruptionDataDefinition();
		patientImmediateStatusAfterVisit.addParameter(new Parameter("endDate", "End Date", Date.class));
		patientImmediateStatusAfterVisit.addParameter(new Parameter("startDate", "Start Date", Date.class));

		MissedAppointmentEffectiveDiscontinuationDateDataDefinition discontinuationDate = new MissedAppointmentEffectiveDiscontinuationDateDataDefinition();
		discontinuationDate.addParameter(new Parameter("endDate", "End Date", Date.class));
		discontinuationDate.addParameter(new Parameter("startDate", "Start Date", Date.class));

		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("id", new PatientIdDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
		dsd.addColumn("Unique Patient Number", identifierDef, null);
		dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
		dsd.addColumn("Date appointment given", new EncounterDatetimeDataDefinition(),"", new DateConverter(DATE_FORMAT));
		dsd.addColumn("Date Appointment missed", missedAppointmentDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Type of Appointment missed", appointmentNotHonouredTypeDataDefinition, paramMapping, null);
		dsd.addColumn("No of days missed", daysMissed, paramMapping, null);
		dsd.addColumn("Tracing attempts", tracingAttemptsDataDefinition, paramMapping, null);
		dsd.addColumn("Tracing methods", tracingMethods, paramMapping, null);
		dsd.addColumn("Last Tracing Date", lastTraceDateDataDefinition, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Last Tracing outcome", lastTraceOutcomeDataDefinition, paramMapping, null);
		dsd.addColumn("Date patient promised to come", dateBooked, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("Final outcome", finalOutcome, paramMapping, null);
		dsd.addColumn("Last Tracing comments", lastTraceCommentsDataDefinition, paramMapping, null);
		dsd.addColumn("Patient status", patientImmediateStatusAfterVisit, paramMapping, null); // before and up to the missed appointment
		dsd.addColumn("Effective Discontinuation Date", discontinuationDate, paramMapping, new DateConverter(DATE_FORMAT));
		dsd.addColumn("RTC Date", rtcDate, paramMapping, new DateConverter(DATE_FORMAT)); // first visit after missed appointment
		dsd.addColumn("Case Manager", etlCaseManagerDataDefinition, paramMapping, null);

		MissedAppointmentsDuringPeriodCohortDefinition cd = new MissedAppointmentsDuringPeriodCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));

		dsd.addRowFilter(cd, paramMapping);
		return dsd;

	}
}
