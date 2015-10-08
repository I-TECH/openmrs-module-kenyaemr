package org.openmrs.module.kenyaemr.reporting.builder.tb;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

/**
 * Created by codehub on 10/8/15.
 */
@Component
@Builds({"kenyaemr.tb.report.needsSputumTest"})
public class NeedsSputumTestReportBuilder extends CalculationReportBuilder {

    @Override
    protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierDefUpn = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), new IdentifierConverter());

        PatientIdentifierType districtNo = MetadataUtils.existing(PatientIdentifierType.class, TbMetadata._PatientIdentifierType.DISTRICT_REG_NUMBER);
        DataDefinition identifierDefDn = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), districtNo), new IdentifierConverter());


        addStandardColumns(report, dsd);
        dsd.addColumn("Unique Patient Number", identifierDefUpn, "");
        dsd.addColumn("District Registration Number", identifierDefDn, "");
    }
}
