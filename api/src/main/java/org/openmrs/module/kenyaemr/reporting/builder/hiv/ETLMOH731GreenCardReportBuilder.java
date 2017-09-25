package org.openmrs.module.kenyaemr.reporting.builder.hiv;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLPmtctIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardIndicatorLibrary;
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
 * Report builder for ETL MOH 731 for Green Card
 */
@Component
@Builds({"kenyaemr.etl.common.report.moh731GreenCard"})
public class ETLMOH731GreenCardReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private ETLPmtctIndicatorLibrary pmtctIndicators;

    @Autowired
    private ETLMoh731GreenCardIndicatorLibrary hivIndicators;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    ColumnParameters colInfants = new ColumnParameters(null, "<1", "age=<1");

    ColumnParameters maleInfants = new ColumnParameters(null, "<1, Male", "gender=M|age=<1");
    ColumnParameters femaleInfants = new ColumnParameters(null, "<1, Female", "gender=F|age=<1");

    ColumnParameters children_1_to_9 = new ColumnParameters(null, "1-9", "age=1-9");
    ColumnParameters adult_10_to_14 = new ColumnParameters(null, "1-9", "age=1-9");
    ColumnParameters adult_15_to_19 = new ColumnParameters(null, "1-9", "age=1-9");
    ColumnParameters adult_20_to_24 = new ColumnParameters(null, "1-9", "age=1-9");
    ColumnParameters adult_25_and_above = new ColumnParameters(null, "1-9", "age=1-9");

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

    ColumnParameters m_25_and_above = new ColumnParameters(null, "25+, Male", "gender=M|age=25+");
    ColumnParameters f_25_and_above = new ColumnParameters(null, "25+, Female", "gender=F|age=25+");

    ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

    List<ColumnParameters> standardDisaggregationAgeAndSex = Arrays.asList(
            colInfants, children_1_to_9,  m_10_to_14, f_10_to_14, m_15_to_19, f_15_to_19,
            m_20_to_24, f_20_to_24, m_25_and_above, f_25_and_above , colTotal);

    List<ColumnParameters> standardAgeOnlyDisaggregation = Arrays.asList(
            children_1_to_9,  adult_10_to_14, adult_15_to_19,
            adult_20_to_24, adult_25_and_above , colTotal);

    List<ColumnParameters> standardDisaggregationWithoutInfants = Arrays.asList(
            children_1_to_9,  m_10_to_14, f_10_to_14,m_15_to_19, f_15_to_19,
            m_20_to_24,f_20_to_24,m_25_and_above, f_25_and_above , colTotal);

    List<ColumnParameters> allAgeDisaggregation = Arrays.asList(
            maleInfants, femaleInfants, m_1_to_4,  f_1_to_4, m_5_to_9, f_5_to_9, m_10_to_14, f_10_to_14,m_15_to_19, f_15_to_19,
            m_20_to_24, f_20_to_24, m_25_and_above, f_25_and_above , colTotal);

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
                ReportUtils.map(pmtctDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(careAndTreatmentDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(hivTestingAndCouselingDatasetDefinition(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Creates the dataset for section #2: Prevention of Mother-to-Child Transmission
     *
     * @return the dataset
     */
    protected DataSetDefinition pmtctDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("2");
        dsd.setDescription("Prevention of Mother-to-Child Transmission");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        dsd.addColumn("HV02-01", "Testing for HIV (Antenatal)", ReportUtils.map(pmtctIndicators.testedForHivInMchmsAntenatal(), indParams), "");
        dsd.addColumn("HV02-02", "Testing for HIV (Labor and Delivery)", ReportUtils.map(pmtctIndicators.testedForHivInMchmsDelivery(), indParams), "");
        dsd.addColumn("HV02-03", "Testing for HIV (Postnatal (within 72hrs))", ReportUtils.map(pmtctIndicators.testedForHivInMchmsPostnatal(), indParams), "");
        dsd.addColumn("HV02-04", "Testing for HIV (Total (Sum HV02-01 to HV02-03))", ReportUtils.map(pmtctIndicators.testedForHivInMchms(), indParams), "");

        dsd.addColumn("HV02-05", "HIV positive results (At entry into ANC)", ReportUtils.map(pmtctIndicators.testedHivPositiveBeforeMchms(), indParams), "");
        dsd.addColumn("HV02-06", "HIV positive results (Antenatal)", ReportUtils.map(pmtctIndicators.testedHivPositiveInMchmsAntenatal(), indParams), "");
        dsd.addColumn("HV02-07", "HIV positive results (Labor and Delivery)", ReportUtils.map(pmtctIndicators.testedHivPositiveInMchmsDelivery(), indParams), "");
        dsd.addColumn("HV02-08", "HIV positive results (Postnatal (within 72hrs))", ReportUtils.map(pmtctIndicators.testedHivPositiveInMchmsPostnatal(), indParams), "");
        dsd.addColumn("HV02-09", "HIV positive results (Total (Sum HV02-05 to HV02-08))", ReportUtils.map(pmtctIndicators.testedHivPositiveInMchms(), indParams), "");

        dsd.addColumn("HV02-10", "Total with known status (Total (HV02-04 to HV02-05))", ReportUtils.map(pmtctIndicators.testedForHivBeforeOrDuringMchms(), indParams), "");

        dsd.addColumn("HV02-11", "Male partners tested - (ANC/L&D)", ReportUtils.map(pmtctIndicators.partnerTestedDuringAncOrDelivery(), indParams), "");

        dsd.addColumn("HV02-12", "Discordant Couples", ReportUtils.map(pmtctIndicators.discordantCouples(), indParams), "");

        dsd.addColumn("HV02-18", "Assessed for eligibility at 1st ANC - WHO Staging done", ReportUtils.map(pmtctIndicators.assessedForArtEligibilityWho(), indParams), "");
        dsd.addColumn("HV02-19", "Assessed for eligibility at 1st ANC - CD4", ReportUtils.map(pmtctIndicators.assessedForArtEligibilityCd4(), indParams), "");
        dsd.addColumn("HV02-20", "Assessed for Eligibility in ANC (Sum HV02-18 to HV02-19)", ReportUtils.map(pmtctIndicators.assessedForArtEligibilityTotal(), indParams), "");


        dsd.addColumn("HV02-24", "PCR within 2 months", ReportUtils.map(pmtctIndicators.pcrWithInitialIn2Months(), indParams), "");
        dsd.addColumn("HV02-25", "PCR from 3 to 8 months", ReportUtils.map(pmtctIndicators.pcrWithInitialBetween3And8MonthsOfAge(), indParams), "");
        dsd.addColumn("HV02-26", "Serology antibody test(from 9 to 12 months)", ReportUtils.map(pmtctIndicators.serologyAntBodyTestBetween9And12Months(), indParams), "");
        dsd.addColumn("HV02-27", "PCR from 9 to 12 months", ReportUtils.map(pmtctIndicators.pcrTestBetween9And12Months(), indParams), "");

        dsd.addColumn("HV02-28", "Total HEI Tested by 12 months (Total (Sum HV02-24 to HV02-26))", ReportUtils.map(pmtctIndicators.totalHeiTestedBy12Months(), indParams), "");

        dsd.addColumn("HV02-29", "Confirmed PCR Positive(Within 2 months)", ReportUtils.map(pmtctIndicators.pcrConfirmedPositive2Months(), indParams), "");
        dsd.addColumn("HV02-30", "Confirmed PCR Positive(3-8 months)", ReportUtils.map(pmtctIndicators.pcrConfirmedPositiveBetween3To8Months(), indParams), "");
        dsd.addColumn("HV02-31", "Confirmed PCR Positive(9-12 months)", ReportUtils.map(pmtctIndicators.pcrConfirmedPositiveBetween9To12Months(), indParams), "");

        dsd.addColumn("HV02-32", "Total Confirmed Positive(Total (Sum HV2-29 to HV02-31))", ReportUtils.map(pmtctIndicators.pcrTotalConfirmedPositive(), indParams), "");

        dsd.addColumn("HV02-33", "Exclusive Breastfeeding(at 6 months)", ReportUtils.map(pmtctIndicators.exclusiveBreastFeedingAtSixMonths(), indParams), "");
        dsd.addColumn("HV02-34", "Exclusive Replacement Feeding(at 6 months)", ReportUtils.map(pmtctIndicators.exclusiveReplacementFeedingAtSixMonths(), indParams), "");
        dsd.addColumn("HV02-35", "Mixed Feeding(at 6 months)", ReportUtils.map(pmtctIndicators.mixedFeedingAtSixMonths(), indParams), "");

        dsd.addColumn("HV02-36", "Total Exposed aged six Months( Total sum(HIV02-33 to HIV02-35))", ReportUtils.map(pmtctIndicators.totalExposedAgedSixMoths(), indParams), "");

        dsd.addColumn("HV02-37", "Mother on ARV treatment and breastfeeding", ReportUtils.map(pmtctIndicators.motherOnTreatmentAndBreastFeeding(), indParams), "");
        dsd.addColumn("HV02-38", "Mother on ARV treatment and Not breastfeeding", ReportUtils.map(pmtctIndicators.motherOnTreatmentAndNotBreastFeeding(), indParams), "");
        dsd.addColumn("HV02-39", "Mother on ARV treatment if breastfeeding unknown", ReportUtils.map(pmtctIndicators.motherOnTreatmentAndNotBreastFeedingUnknown(), indParams), "");

        dsd.addColumn("HV02-40", "Mother on ARV treatment (Total Sum(HIV02-37 to HIV02-39))", ReportUtils.map(pmtctIndicators.totalBreastFeedingMotherOnTreatment(), indParams), "");

        // TO DO 2.9 Infant ARV Prophylaxis(at first contact only)

        return dsd;
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
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.moh731GreenCardAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

       /* cohortDsd.addColumn("HV03-01", "HIV Exposed Infants (within 2 months)", ReportUtils.map(hivIndicators.hivExposedInfantsWithin2Months(), indParams), "");
        cohortDsd.addColumn("HV03-02", "HIV Exposed Infants (Eligible for CTX at 2 months)", ReportUtils.map(hivIndicators.hivExposedInfantsWithin2MonthsAndEligibleForCTX(), indParams), "");*/


        // 3.1 (On CTX Prophylaxis)
        //EmrReportingUtils.addRow(cohortDsd, "HV03", "On CTX/dapsone Prophylaxis", ReportUtils.map(hivIndicators.onCotrimoxazoleProphylaxis(), indParams), nonInfantColumns, Arrays.asList("03", "04", "05", "06", "07"));

        // 3.2 (Enrolled in Care)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Enrolled in care", ReportUtils.map(hivIndicators.newHivEnrollment(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        // 3.3 (Currently in Care)
       /* EmrReportingUtils.addRow(cohortDsd, "HV03", "Currently in care", ReportUtils.map(hivIndicators.currentlyInCare(), indParams), allColumns, Arrays.asList("14", "15", "16", "17", "18", "19"));

        // 3.4 (Starting ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Starting ART", ReportUtils.map(hivIndicators.startedOnArt(), indParams), allColumns, Arrays.asList("20", "21", "22", "23", "24", "25"));
        cohortDsd.addColumn("HV03-26", "Starting ART (Pregnant)", ReportUtils.map(hivIndicators.startedArtWhilePregnant(), indParams), "");
        cohortDsd.addColumn("HV03-27", "Starting ART (TB Patient)", ReportUtils.map(hivIndicators.startedArtWhileTbPatient(), indParams), "");

        // 3.5 (Revisits ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Revisits ART", ReportUtils.map(hivIndicators.revisitsArt(), indParams), allColumns, Arrays.asList("28", "29", "30", "31", "32", "33"));

        // 3.6 (Currently on ART [All])
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Currently on ART [All]", ReportUtils.map(hivIndicators.currentlyOnArt(), indParams), allColumns, Arrays.asList("34", "35", "36", "37", "38", "39"));


        // 3.7 (Cumulative Ever on ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Cumulative ever on ART", ReportUtils.map(hivIndicators.cumulativeOnArt(), indParams), nonInfantColumns, Arrays.asList("40", "41", "42", "43", "44"));

        // 3.8 (Survival and Retention on ART at 12 months)
        cohortDsd.addColumn("HV03-45", "ART Net Cohort at 12 months", ReportUtils.map(hivIndicators.art12MonthNetCohort(), indParams), "");
        cohortDsd.addColumn("HV03-46", "On original 1st Line at 12 months", ReportUtils.map(hivIndicators.onOriginalFirstLineAt12Months(), indParams), "");
        cohortDsd.addColumn("HV03-47", "On alternative 1st Line at 12 months", ReportUtils.map(hivIndicators.onAlternateFirstLineAt12Months(), indParams), "");
        cohortDsd.addColumn("HV03-48", "On 2nd Line (or higher) at 12 months ", ReportUtils.map(hivIndicators.onSecondLineAt12Months(), indParams), "");
        cohortDsd.addColumn("HV03-49", "On therapy at 12 months (Total) ", ReportUtils.map(hivIndicators.onTherapyAt12Months(), indParams), "");

        // 3.9 (Screening)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Screened for TB", ReportUtils.map(hivIndicators.screenedForTb(), indParams), nonInfantColumns, Arrays.asList("50", "51", "52", "53", "54"));

        // 3.10 (Prevention with Positives)
        cohortDsd.addColumn("HV09-04", "Modern contraceptive methods", ReportUtils.map(hivIndicators.modernContraceptivesProvided(), indParams), "");
        cohortDsd.addColumn("HV09-05", "Provided with condoms", ReportUtils.map(hivIndicators.condomsProvided(), indParams), "");

        // 3.11 (HIV Care Visits)
        cohortDsd.addColumn("HV03-70", "HIV care visits (Females 18+)", ReportUtils.map(hivIndicators.hivCareVisitsFemale18(), indParams),"");
        cohortDsd.addColumn("HV03-71", "HIV care visits (Scheduled)", ReportUtils.map(hivIndicators.hivCareVisitsScheduled(), indParams),"");
        cohortDsd.addColumn("HV03-72", "HIV care visits (Unscheduled)", ReportUtils.map(hivIndicators.hivCareVisitsUnscheduled(), indParams),"");
        cohortDsd.addColumn("HV03-73", "HIV care visits (Total)", ReportUtils.map(hivIndicators.hivCareVisitsTotal(), indParams),"");*/


        return cohortDsd;

    }

    /**
     * Creates the dataset for section #1: hiv testing and counseling
     *
     * @return the dataset
     */
    protected DataSetDefinition hivTestingAndCouselingDatasetDefinition() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("1");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.moh731GreenCardAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        String indParams = "startDate=${startDate},endDate=${endDate}";

        // 3.1 HIV testing and counseling
        EmrReportingUtils.addRow(cohortDsd, "HV01", "Tested", ReportUtils.map(hivIndicators.htsNumberTested(), indParams), standardDisaggregationWithoutInfants, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));
        cohortDsd.addColumn("1.HV01-11", "Tested Facility", ReportUtils.map(hivIndicators.htsNumberTestedAtFacility(), indParams),"");
        cohortDsd.addColumn("1.HV01-12", "Tested Community", ReportUtils.map(hivIndicators.htsNumberTestedAtCommunity(), indParams),"");
        cohortDsd.addColumn("1.HV01-13", "Tested New", ReportUtils.map(hivIndicators.htsNumberTestedNew(), indParams),"");
        cohortDsd.addColumn("1.HV01-14", "Tested Repeat", ReportUtils.map(hivIndicators.htsNumberTestedRepeat(), indParams),"");
        cohortDsd.addColumn("1.HV01-15", "Tested Couples", ReportUtils.map(hivIndicators.htsNumberTestedAsCouple(), indParams),"");
        cohortDsd.addColumn("1.HV01-16", "Tested Key Pop", ReportUtils.map(hivIndicators.htsNumberTestedKeyPopulation(), indParams),"");

        EmrReportingUtils.addRow(cohortDsd, "HV01", "Tested", ReportUtils.map(hivIndicators.htsNumberTested(), indParams), standardDisaggregationWithoutInfants, Arrays.asList("17", "18", "19", "20", "21", "22", "23", "24", "25", "26"));
        cohortDsd.addColumn("1.HV01-27", "Negative Total", ReportUtils.map(hivIndicators.htsNumberTestedNegative(), indParams),"");
        cohortDsd.addColumn("1.HV01-28", "Discordant", ReportUtils.map(hivIndicators.htsNumberTestedDiscordant(), indParams),"");
        cohortDsd.addColumn("1.HV01-29", "Positive Key Pop", ReportUtils.map(hivIndicators.htsNumberTestedKeypopPositive(), indParams),"");

        // number linked
        EmrReportingUtils.addRow(cohortDsd, "HV01", "Tested", ReportUtils.map(hivIndicators.htsNumberTestedPositiveAndLinked(), indParams), standardAgeOnlyDisaggregation, Arrays.asList("30", "31", "32", "33", "34", "35", "36"));


        return cohortDsd;

    }
}
