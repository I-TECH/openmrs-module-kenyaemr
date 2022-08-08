/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.ipt;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.IPTRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.*;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.ipt.IPTIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
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
@Builds({"kenyaemr.ipt.report.iptRegister"})
public class IPTRegisterReportBuilder extends AbstractHybridReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private IPTIndicatorLibrary iptIndicators;

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        return allPatientsCohort();
    }

    ColumnParameters children_0_to_14 = new ColumnParameters(null, "0-14", "age=0-14");
    ColumnParameters adult_15_and_above = new ColumnParameters(null, "15+", "age=15+");
    ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

    List<ColumnParameters> iptAgeDisaggregation = Arrays.asList(children_0_to_14, adult_15_and_above, colTotal);

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new IPTRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("IPTPatients");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition iptPatients = iptDataSetDefinition();
        iptPatients.addRowFilter(allPatientsCohort());
        DataSetDefinition iptPatientsDSD = iptPatients;

        return Arrays.asList(
                ReportUtils.map(iptPatientsDSD, "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(iptRegisterSummaryDataSet(), "startDate=${startDate},endDate=${endDate}")
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

    protected PatientDataSetDefinition iptDataSetDefinition() {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition("IPTRegister");
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
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Unique Patient No", identifierDef, "");
        dsd.addColumn("Patient Number", new PersonIdDataDefinition(), "");
        dsd.addColumn("Sub County Registration", new RegistrationSubcountyDataDefinition(), "");
        dsd.addColumn("Sub County Registration Date", new SubcountyRegistrationDateDataDefinition(), "");
        dsd.addColumn("OPD or IPD and CCC Number", new OPDIPDCCCNoDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Nationality", new NationalityDataDefinition(), "");
        dsd.addColumn("Physical Address", new PhysicalAddressDataDefinition(), "");
        dsd.addColumn("Patient Phone number", new PhoneNumberDataDefinition(), "");
        dsd.addColumn("Supporter Phone number", new SupporterPhoneNumberDataDefinition(), "");
        dsd.addColumn("WeightAtStart", new WeightAtStartDataDefinition(), "");
        dsd.addColumn("Height", new HeightDataDefinition(), "");
        dsd.addColumn("BMI or Z Score or MUAC", new BMIZScoreMUACDataDefinition(), "");
        dsd.addColumn("TPT Start Date", new CalculationDataDefinition("IPT Start Date", new IPTStartDateCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Indication for TPT", new IPTIndicationDataDefinition(), "");
        dsd.addColumn("INH Dose(Mg)", new INHDataDefinition(), "");
        dsd.addColumn("VTB 6(Pyridoxine)Dose", new VTBDataDefinition(), "");
        dsd.addColumn("Treatment start date", new TreatmentStartDateDataDefinition(), "");
        dsd.addColumn("Month 1 Drug collection date", mapMonthlyDrugCollectionDateDataDefinition("Drug collection date - Month 1",0,1), defParam, null);
        dsd.addColumn("Month 2 Drug collection date", mapMonthlyDrugCollectionDateDataDefinition("Drug collection date - Month 2",1,2), defParam, null);
        dsd.addColumn("Month 3 Drug collection date", mapMonthlyDrugCollectionDateDataDefinition("Drug collection date - Month 3",2,3), defParam, null);
        dsd.addColumn("Month 4 Drug collection date", mapMonthlyDrugCollectionDateDataDefinition("Drug collection date - Month 4",3,4), defParam, null);
        dsd.addColumn("Month 5 Drug collection date", mapMonthlyDrugCollectionDateDataDefinition("Drug collection date - Month 5",4,5), defParam, null);
        dsd.addColumn("Month 6 Drug collection date", mapMonthlyDrugCollectionDateDataDefinition("Drug collection date - Month 6",5,6), defParam, null);
        dsd.addColumn("HIV Status", new HIVStatusDataDefinition(), "");
        dsd.addColumn("HIV Test Date", new HIVTestDateDataDefinition(), "");
        dsd.addColumn("Started CTX or Dapsone", new DapsoneCotrimoxazoleDataDefinition(), "");
        dsd.addColumn("Date started CTX", new CTXDapsoneStartDateDataDefinition(), "");
        dsd.addColumn("Started ART", new StartedARTDataDefinition(), "");
        dsd.addColumn("ART Start Date and Regimen", new DateStartedARTDataDefinition(), "");
        dsd.addColumn("TPT Outcome", new IPTOutcomeDataDefinition(), "");
        dsd.addColumn("TPT Outcome Date", new IPTOutcomeDateDataDefinition(), "");
        dsd.addColumn("Reasons for TPT Discontinuation", new IPTDiscontinuationReasonDataDefinition(), "");
        dsd.addColumn("M6 TB Status and Date", mapPostIPTTBStatusDateDateDataDefinition("TB status and Date - Month 6",150,210), defParam, null);
        dsd.addColumn("M12 TB Status and Date", mapPostIPTTBStatusDateDateDataDefinition("TB status and Date - Month 12",330,390), defParam, null);
        dsd.addColumn("M18 TB Status and Date", mapPostIPTTBStatusDateDateDataDefinition("TB status and Date - Month 18",510,570), defParam, null);
        dsd.addColumn("M24 TB Status and Date", mapPostIPTTBStatusDateDateDataDefinition("TB status and Date - Month 24",690,750), defParam, null);

        return dsd;
    }

    protected DataSetDefinition iptRegisterSummaryDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("cohortIndicator");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.moh731GreenCardAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "numberOnIPT", "No of Clients", ReportUtils.map(iptIndicators.numberOnIPT(), indParams), iptAgeDisaggregation, Arrays.asList("01", "02", "03"));
        cohortDsd.addColumn("plhiv", "HIV+ on IPT", ReportUtils.map(iptIndicators.plhivOnIPT(), indParams), "");
        cohortDsd.addColumn("prisoners", "Prisoners on IPT", ReportUtils.map(iptIndicators.prisonersOnIPT(), indParams), "");
        cohortDsd.addColumn("hcw", "Health Care Workers on IPT", ReportUtils.map(iptIndicators.hcwOnIPT(), indParams), "");
        cohortDsd.addColumn("childrenExposedTB", "Children Exposed to TB", ReportUtils.map(iptIndicators.childrenExposedTB(), indParams), "");
        cohortDsd.addColumn("completedIPT", "Completed IPT", ReportUtils.map(iptIndicators.completedIPT(), indParams), "");

        return cohortDsd;
    }

    private MonthlyDrugCollectionDateDataDefinition mapMonthlyDrugCollectionDateDataDefinition(String name,Integer minMonth, Integer maxMonth) {
        MonthlyDrugCollectionDateDataDefinition drugCollDateDef = new MonthlyDrugCollectionDateDataDefinition(name,minMonth,maxMonth);
        drugCollDateDef.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return drugCollDateDef;
    }

    private PostIPTTBStatusDateDataDefinition mapPostIPTTBStatusDateDateDataDefinition(String name,Integer minDays, Integer maxDays) {
        PostIPTTBStatusDateDataDefinition postIPTTBStatusDateDef = new PostIPTTBStatusDateDataDefinition(name,minDays,maxDays);
        postIPTTBStatusDateDef.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return postIPTTBStatusDateDef;
    }
}

