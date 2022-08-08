/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.ovc;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.*;
import org.openmrs.module.kenyaemr.calculation.library.ovc.ImplementingPartnerSupportingOVCCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.OVCMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.*;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ovc.PatientsOnOVCCohortDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Builds({"kenyaemr.ovc.report.currentlyEnrolledInOVC"})
public class PatientsOnOVCReportBuilder extends AbstractHybridReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";


    @Override
    protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {
    }

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        return null;
    }


    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new PatientsOnOVCCohortDefinition();
        cd.setName("Patients Currently on OVC");
        return ReportUtils.map(cd, "");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allVisits = patientsOnOVCDataSetDefinition("patientsOnOvc");
        allVisits.addRowFilter(allPatientsCohort());
        DataSetDefinition allPatientsDSD = allVisits;

        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "")
        );
    }

    protected PatientDataSetDefinition patientsOnOVCDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, OVCMetadata._PatientIdentifierType.CPIMS_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);
        PatientIdentifierType cccNo = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition cccNoIdentifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(cccNo.getName(), cccNo), identifierFormatter);



        DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("CCC No", cccNoIdentifierDef, "");
        dsd.addColumn("CPMIS No", identifierDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", null);
        dsd.addColumn("Age", new AgeDataDefinition(), "", null);
        dsd.addColumn("Current Regimen", new CalculationDataDefinition("Current Regimen", new CurrentArtRegimenCalculation()), "", null);
        dsd.addColumn("Last VL Result", new CalculationDataDefinition("Recent Viral Load Result", new ViralLoadResultCalculation("last")), "", new RDQASimpleObjectRegimenConverter("data"));
        dsd.addColumn("Last VL Date", new CalculationDataDefinition("Recent Viral Load Result Date", new ViralLoadResultCalculation("last")), "", new RDQASimpleObjectRegimenConverter("date"));
        dsd.addColumn("Partner Supporting", new CalculationDataDefinition("Partner Supporting", new ImplementingPartnerSupportingOVCCalculation()), "", null);

        return dsd;
    }
}
