/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.nupi;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLArtStartDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCurrentRegLineDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCurrentRegimenDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLFirstRegimenDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLNextAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLStabilityDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;

/**
 * Created by codehub on 10/7/15.
 */
@Component
@Builds({ "kenyaemr.nupi.report.allPatientsMissingNationalUniquePatientIdentifier" })
public class MissingNUPIIdentifierReportBuilder extends CalculationReportBuilder {
	
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	
	@Override
	protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {
		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class,
		    HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		PatientIdentifierType nupi = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
		PersonAttributeType networkError = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.VERIFICATION_MESSAGE_WITH_NATIONAL_REGISTRY);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition CCCidentifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(
		        upn.getName(), upn), new IdentifierConverter());
		DataDefinition nupiDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nupi.getName(), nupi), identifierFormatter);
		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		DataConverter ageFormatter = new ObjectFormatter("{age}");
        DataDefinition ageDef = new ConvertedPersonDataDefinition("age", new AgeDataDefinition(), ageFormatter);
		DataDefinition networkErrorDef = new ConvertedPersonDataDefinition("error", new PersonAttributeDataDefinition(networkError.getName(), networkError));

		dsd.removeColumnDefinition("upn");
		addStandardColumns(report, dsd);
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Age", ageDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("ERROR", networkErrorDef, "");
	}
}
