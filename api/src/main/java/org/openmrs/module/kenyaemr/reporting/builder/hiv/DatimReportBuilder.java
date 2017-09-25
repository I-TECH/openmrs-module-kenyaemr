package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim.ETLDatimIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Report builder for Datim report
 */
@Component
@Builds({"kenyaemr.etl.common.report.datim"})
public class DatimReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private ETLDatimIndicatorLibrary datimIndicators;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

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
                ReportUtils.map(careAndTreatmentDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }



    /**
     * Creates the dataset for section #3: Care and Treatment
     *
     * @return the dataset
     */
    protected DataSetDefinition careAndTreatmentDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("3");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.datimFineAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        ColumnParameters colInfants = new ColumnParameters(null, "<1", "age=<1");

        ColumnParameters maleInfants = new ColumnParameters(null, "<1, Male", "gender=M|age=<1");
        ColumnParameters femaleInfants = new ColumnParameters(null, "<1, Female", "gender=F|age=<1");

        ColumnParameters children_1_to_9 = new ColumnParameters(null, "1-9", "age=1-9");

        ColumnParameters m_1_to_4 = new ColumnParameters(null, "1-4, Male", "gender=M|age=1-4");
        ColumnParameters f_1_to_4 = new ColumnParameters(null, "1-4, Female", "gender=F|age=1-4");

        ColumnParameters m_5_to_9 = new ColumnParameters(null, "5-9, Male", "gender=M|age=5-9");
        ColumnParameters f_5_to_9 = new ColumnParameters(null, "5-9, Female", "gender=F|age=5-9");

        ColumnParameters m_10_to_14 = new ColumnParameters(null, "10-14, Male", "gender=M|age=10-14");
        ColumnParameters f_10_to_14 = new ColumnParameters(null, "10-14, Female", "gender=F|age=10-14");

        ColumnParameters m_15_to_19 = new ColumnParameters(null, "15-19, Male", "gender=M|age=15-19");
        ColumnParameters f_15_to_19 = new ColumnParameters(null, "15-19, Female", "gender=F|age=15-19");

        ColumnParameters m_20_to_24 = new ColumnParameters(null, "20-24, Male", "gender=M|age=20-24");
        ColumnParameters f_20_to_24 = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");

        ColumnParameters m_25_to_49 = new ColumnParameters(null, "25-49, Male", "gender=M|age=25-49");
        ColumnParameters f_25_to_49 = new ColumnParameters(null, "25-49, Female", "gender=F|age=25-49");

        ColumnParameters m_50_and_above = new ColumnParameters(null, "50+, Male", "gender=M|age=50+");
        ColumnParameters f_50_and_above = new ColumnParameters(null, "50+, Female", "gender=F|age=50+");

        ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

        List<ColumnParameters> adultDisaggregation = Arrays.asList(
                colInfants, children_1_to_9,  f_10_to_14, m_10_to_14,f_15_to_19, m_15_to_19,
                f_20_to_24,m_20_to_24,f_25_to_49, m_25_to_49,f_50_and_above,m_50_and_above , colTotal);

        List<ColumnParameters> allAgeDisaggregation = Arrays.asList(
                maleInfants,femaleInfants, m_1_to_4,  f_1_to_4, m_5_to_9, f_5_to_9, f_10_to_14, m_10_to_14,f_15_to_19, m_15_to_19,
                f_20_to_24,m_20_to_24,f_25_to_49, m_25_to_49,f_50_and_above,m_50_and_above , colTotal);

        String indParams = "startDate=${startDate},endDate=${endDate}";
        String endDateParams = "endDate=${endDate}";

        // 3.1 (On CTX Prophylaxis)
        EmrReportingUtils.addRow(cohortDsd, "TX_New", "Started on Art", ReportUtils.map(datimIndicators.startedOnArt(), indParams), adultDisaggregation, Arrays.asList("03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15"));

        EmrReportingUtils.addRow(cohortDsd, "TX_Curr", "Currently on Art", ReportUtils.map(datimIndicators.currentlyOnArt(), indParams), adultDisaggregation, Arrays.asList("17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29"));

        // 3.2 (Enrolled in Care)
        EmrReportingUtils.addRow(cohortDsd, "TX_RET_Numerator", "Retention at 12 months", ReportUtils.map(datimIndicators.onTherapyAt12Months(), indParams), allAgeDisaggregation, Arrays.asList("30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45","46"));

        // 3.3 (Currently in Care)
        EmrReportingUtils.addRow(cohortDsd, "TX_RET_Denominator", "12 months cohort", ReportUtils.map(datimIndicators.art12MonthCohort(), indParams), allAgeDisaggregation, Arrays.asList("47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61","62","63"));

        // 3.4 (Starting ART)
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Numerator", "Viral Suppression", ReportUtils.map(datimIndicators.patientsWithViralLoadSuppression(), endDateParams), allAgeDisaggregation, Arrays.asList("64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77","78","79","80"));

        // 3.5 (Revisits ART)
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Denominator", "Patients with VL in 12 months", ReportUtils.map(datimIndicators.patientsWithVLResults(), endDateParams), allAgeDisaggregation, Arrays.asList("81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93","94","95","96","97"));
        return cohortDsd;

    }
}
