/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.otz;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.OTZRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.otz.*;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.otz.report.otzRegister"})
public class OTZRegisterReportBuilder extends AbstractHybridReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        return allPatientsCohort();
    }

        protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new OTZRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("OTZPatients");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition otzPatients = otzDataSetDefinition();
        otzPatients.addRowFilter(allPatientsCohort());
        DataSetDefinition otzPatientsDSD = otzPatients;

        return Arrays.asList(
                ReportUtils.map(otzPatientsDSD, "startDate=${startDate},endDate=${endDate}")
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

    protected PatientDataSetDefinition otzDataSetDefinition() {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition("OTZRegister");
        dsd.addSortCriteria("DOBAndAge", SortCriteria.SortDirection.DESC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        String defParam = "startDate=${startDate}";

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");

        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("Enrolment date",new OTZEnrolmentDateDataDefinition(),"");
        dsd.addColumn("Date of Birth and Age", new DateOfBirthAgeDataDefinition(),"");
        //dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Unique Patient No", identifierDef, "");
        dsd.addColumn("Art Start Date and Start Regimen", new ARTStartDateRegimenDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("VL Results and Test date", new VLTestDataDefinition(), "");
        dsd.addColumn("VL done within 12 Months", new VLTestWithin12MonthsDataDefinition(), "");
        dsd.addColumn("Current ART Regimen", new CurrentARTRegimenDataDefinition(), "");
        dsd.addColumn("Current ART Regimen Line", new CurrentRegimenLineDataDefinition(), "");
        dsd.addColumn("1st Regimen Switch", new FirstRegimenSwitchDataDefinition(), "");
        dsd.addColumn("1st Regimen Switch Date", new FirstRegimenSwitchDateDataDefinition(), "");
        dsd.addColumn("1st Regimen Switch Reason", new FirstRegimenSwitchReasonDataDefinition(), "");
        dsd.addColumn("2nd Regimen Switch", new SecondRegimenSwitchDataDefinition(), "");
        dsd.addColumn("2nd Regimen Switch Date", new SecondRegimenSwitchDateDataDefinition(), "");
        dsd.addColumn("2nd Regimen Switch Reason", new SecondRegimenSwitchReasonDataDefinition(), "");
        dsd.addColumn("3rd Regimen Switch", new ThirdRegimenSwitchDataDefinition(), "");
        dsd.addColumn("3rd Regimen Switch Date", new ThirdRegimenSwitchDateDataDefinition(), "");
        dsd.addColumn("3rd Regimen Switch Reason", new ThirdRegimenSwitchReasonDataDefinition(), "");
        dsd.addColumn("4th Regimen Switch", new FourthRegimenSwitchDataDefinition(), "");
        dsd.addColumn("4th Regimen Switch Date", new FourthRegimenSwitchDateDataDefinition(), "");
        dsd.addColumn("4th Regimen Switch Reason", new FourthRegimenSwitchReasonDataDefinition(), "");
        dsd.addColumn("First VL post OTZ enrolment", new FirstVLPostOTZEnrolmentDataDefinition(), "");
        dsd.addColumn("Second VL post OTZ enrolment", new SecondVLPostOTZEnrolmentDataDefinition(), "");
        dsd.addColumn("Third VL post OTZ enrolment", new ThirdVLPostOTZEnrolmentDataDefinition(), "");
        dsd.addColumn("Fourth VL post OTZ enrolment", new FourthVLPostOTZEnrolmentDataDefinition(), "");
        dsd.addColumn("Fifth VL post OTZ enrolment", new FifthVLPostOTZEnrolmentDataDefinition(), "");
        dsd.addColumn("Sixth VL post OTZ enrolment", new SixthVLPostOTZEnrolmentDataDefinition(), "");
        dsd.addColumn("Transition", new TransitionDataDefinition(), "");
        dsd.addColumn("Module Completed", new ModuleTrackerDataDefinition(), "");
        dsd.addColumn("Remarks", new OTZRemarksDataDefinition(), "");

        return dsd;
    }

}

