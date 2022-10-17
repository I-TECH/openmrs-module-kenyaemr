/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.sgbv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.sgbv.SGBVRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.ActiveInProgramConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.HTSMaritalStatusConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KenyaEMRMaritalStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInOvcDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.EPCDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.EmotionalIPVDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.HivTestDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.P3DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.PEPDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.PRCDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.PhysicalIPVDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.PtnSTITreatmentDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.SexualIPVDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.TcaDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.TraumaDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
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
@Builds({"kenyaemr.sgbv.report.sgbvRegister"})
public class SGBVRegisterReportBuilder extends AbstractReportBuilder {
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
        dsd.setName("SGBVRegister");
        dsd.setDescription("SGBV Visit information");
        dsd.addSortCriteria("Visit Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierCcc = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        PatientIdentifierType clinic_no = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.PATIENT_CLINIC_NUMBER);
        DataDefinition identifierClinicNo = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        String paramMapping = "startDate=${startDate},endDate=${endDate}";

        ActiveInOvcDataDefinition activeInOvcDataDefinition = new ActiveInOvcDataDefinition();
        activeInOvcDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        PatientIdentifierType nationalId = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nationalId.getName(), nationalId), identifierFormatter);

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("ID Number", identifierDef, "");
        dsd.addColumn("Patient Clinic Number", identifierClinicNo, "");
        dsd.addColumn("CCC No", identifierCcc, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Phone Number", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Marital Status", new KenyaEMRMaritalStatusDataDefinition(), null,new HTSMaritalStatusConverter());
        dsd.addColumn("Active in OVC", activeInOvcDataDefinition,"endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("Emotional IPV", new EmotionalIPVDataDefinition(), "");
        dsd.addColumn("Physical IPV", new PhysicalIPVDataDefinition(), "");
        dsd.addColumn("Sexual IPV", new SexualIPVDataDefinition(), "");
        dsd.addColumn("PRC Date", new PRCDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("P3 Date", new P3DataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("HIV Test", new HivTestDataDefinition(), "");
        dsd.addColumn("Given ECP", new EPCDataDefinition(), "");
        dsd.addColumn("PEP Given", new PEPDataDefinition(), "");//
        dsd.addColumn("STI", new PtnSTITreatmentDataDefinition(), "");
        dsd.addColumn("Trauma", new TraumaDataDefinition(), "");
        dsd.addColumn("TCA", new TcaDateDataDefinition(), "", new DateConverter(DATE_FORMAT));

        SGBVRegisterCohortDefinition cd = new SGBVRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);


        return dsd;
    }
}