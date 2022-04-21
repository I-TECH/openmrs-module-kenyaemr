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
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AllPatientsCalculation;
import org.openmrs.module.kenyaemr.reporting.data.converter.NationalIdentifiersTypeConverter;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

/**
 * Created by cynthia on 21/4/22.
 */
@Component
@Builds({"kenyaemr.common.report.allPatients"})
public class AllPatientsReportBuilder extends CalculationReportBuilder {

    @Override
    protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {

        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        PatientIdentifierType passport = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.PASSPORT_NUMBER);
        DataDefinition passportDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(passport.getName(), passport), identifierFormatter);

        PatientIdentifierType national_id = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
        DataDefinition nationalIdDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(national_id.getName(), national_id), identifierFormatter);

        PatientIdentifierType huduma_number = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.HUDUMA_NUMBER);
        DataDefinition hudumaNumberDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(huduma_number.getName(), huduma_number), identifierFormatter);

        PatientIdentifierType birth_certificate_number = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.BIRTH_CERTIFICATE_NUMBER);
        DataDefinition birthCertificateNumberDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(birth_certificate_number.getName(), birth_certificate_number), identifierFormatter);

        PatientIdentifierType alien_id_number = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.ALIEN_ID_NUMBER);
        DataDefinition alienIdNumberDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(alien_id_number.getName(), alien_id_number), identifierFormatter);

        PatientIdentifierType driving_licence = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.DRIVING_LICENSE);
        DataDefinition drivingLicenceDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(driving_licence.getName(), driving_licence), identifierFormatter);

        addStandardColumns(report, dsd);

        dsd.addColumn("UPN", identifierDef, "", new NationalIdentifiersTypeConverter());
        dsd.addColumn("National Id", nationalIdDef, "", new NationalIdentifiersTypeConverter());
        dsd.addColumn("Huduma Number", hudumaNumberDef, "", new NationalIdentifiersTypeConverter());
        dsd.addColumn("Passport Number", passportDef, "", new NationalIdentifiersTypeConverter());
        dsd.addColumn("Birth Certificate Number", birthCertificateNumberDef, "", new NationalIdentifiersTypeConverter());
        dsd.addColumn("Driving Licence", drivingLicenceDef, "", new NationalIdentifiersTypeConverter());
        dsd.addColumn("Alien Id Number", alienIdNumberDef, "", new NationalIdentifiersTypeConverter());

    }
}
