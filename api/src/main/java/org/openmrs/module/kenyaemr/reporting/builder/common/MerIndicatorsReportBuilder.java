package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.mer.MerCohortIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivIndicatorLibrary;
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
 * Created by codehub on 2/23/16.
 */
@Component
@Builds({"kenyaemr.common.report.mer-indicators"})
public class MerIndicatorsReportBuilder extends AbstractReportBuilder {

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private HivIndicatorLibrary hivIndicators;

    @Autowired
    private MerCohortIndicatorLibrary merCohortIndicators;




    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(merDataSetLevel1(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(merDataSetLevel2(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Create a data set for the mer indicator level 1
     * @return dataset
     */
    protected DataSetDefinition merDataSetLevel1() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Mer level 1 indicators");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        cohortDsd.addColumn("PMTCT_STAT", "Pregnant women with known HIV status", ReportUtils.map(merCohortIndicators.percentageOfPregnantWomenWithKnownHivStatus(), indParams), "");
        cohortDsd.addColumn("PMTCT_ARV", "HIV+ pregnant women who received antiretrovirals to reduce risk of MTCT during pregnancy and delivery", ReportUtils.map(merCohortIndicators.percentageOfPregnantWomenWithKnownHivStatus(), indParams), "");
        cohortDsd.addColumn("PMTCT_EID", "Infants born to HIV-positive women who had a virologic HIV test done within 12 months of birth", ReportUtils.map(merCohortIndicators.percentageOfInfantsBornToHIVPositiveWomenWhoHadVirologicHivTestDoneWithin12MonthsOfBirth(), indParams), "");
        cohortDsd.addColumn("PMTCT_FO", "Final outcomes among HIV exposed infants registered in the birth cohort", ReportUtils.map(merCohortIndicators.finalOutcomesAmongHivExposedInfantsRegisteredInTheBirthCohort(), indParams), "");

        return cohortDsd;

    }

    /**
     * Create a data set definition for level 2 indicators
     * @return DataSetDefinition
     */
    protected DataSetDefinition merDataSetLevel2() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Mer level 2 indicators");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        cohortDsd.addColumn("PMTCT_CTX", "Infants born to HIV-positive pregnant women who were started on CTX prophylaxis within two months of birth", ReportUtils.map(merCohortIndicators.infantsBornToHivPositivePregnantWomenWhoStartedCtxWithin2MonthsOfBirth(), indParams), "");

        return cohortDsd;
    }
}