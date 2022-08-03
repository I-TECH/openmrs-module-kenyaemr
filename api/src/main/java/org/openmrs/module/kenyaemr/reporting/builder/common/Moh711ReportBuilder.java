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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.moh711.Moh711IndicatorLibrary;
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

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

/**
 * MOH 711 report
 */
@Component
@Builds({"kenyaemr.common.report.moh711"})
public class Moh711ReportBuilder extends AbstractReportBuilder {

    protected static final Log log = LogFactory.getLog(Moh711ReportBuilder.class);

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private Moh711IndicatorLibrary moh711Indicators;

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
     */
    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    String indParams = "startDate=${startDate},endDate=${endDate}";

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
     */
    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(createANCPMTCTDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createCacxScreeningDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createPNCDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    ColumnParameters f10_14 = new ColumnParameters(null, "10-14 years", "gender=F|age=10-14");
    ColumnParameters f15_19 = new ColumnParameters(null, "15-19 years", "gender=F|age=15-19");
    ColumnParameters f20_24 = new ColumnParameters(null, "20-24 years", "gender=F|age=20-24");

    ColumnParameters fUnder25 = new ColumnParameters(null, "<25 years", "gender=F|age=<25");
    ColumnParameters f25_49 = new ColumnParameters(null, "25-49 years", "gender=F|age=25-49");
    ColumnParameters f50AndAbove = new ColumnParameters(null, "50+ years", "gender=F|age=>=50");

    List<ColumnParameters> ancDisaggregations = Arrays.asList(f10_14, f15_19, f20_24);
    List<ColumnParameters> cacxScreeningDisaggregations = Arrays.asList(fUnder25, f25_49, f50AndAbove);

    /**
     * A. ANC / PMCT
     * Creates ANC/PMTCT dataset
     */
    private DataSetDefinition createANCPMTCTDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("ANC_PMTCT");
        dsd.setDescription("ANC PMTCT");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", map(commonDimensions.datimFineAgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", map(commonDimensions.gender()));

        dsd.addColumn("New ANC Clients", "", ReportUtils.map(moh711Indicators.noOfNewANCClients(), indParams), "");
        dsd.addColumn("Revisiting ANC Clients", "", ReportUtils.map(moh711Indicators.noOfANCClientsRevisits(), indParams), "");
	/*dsd.addColumn("Clients given IPT (1st dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT1stDose(), indParams), "");
	dsd.addColumn("Clients given IPT (2nd dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT2ndDose(), indParams), "");
	dsd.addColumn("Clients given IPT (3rd dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT3rdDose(), indParams), "");*/
        dsd.addColumn("Clients with Hb less than 11 g per dl", "", ReportUtils.map(moh711Indicators.noOfANCClientsLowHB(), indParams), "");
        dsd.addColumn("Clients completed 4 Antenatal Visits", "", ReportUtils.map(moh711Indicators.ancClientsCompleted4Visits(), indParams), "");
        //dsd.addColumn("LLINs distributed to under 1 year", "", ReportUtils.map(moh711Indicators.distributedLLINsUnder1Year(), indParams), "");
        dsd.addColumn("LLINs distributed to ANC clients", "", ReportUtils.map(moh711Indicators.distributedLLINsToANCClients(), indParams), "");

        dsd.addColumn("clients tested for Syphilis", "", ReportUtils.map(moh711Indicators.ancClientsTestedForSyphillis(), indParams), "");
        dsd.addColumn("clients tested Positive for Syphilis", "", ReportUtils.map(moh711Indicators.ancClientsTestedSyphillisPositive(), indParams), "");
        dsd.addColumn("Total women done breast examination", "", ReportUtils.map(moh711Indicators.breastExaminationDone(), indParams), "");
        EmrReportingUtils.addRow(dsd, "ANC1", "Presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Indicators.noOfNewANCClients(), indParams), ancDisaggregations, Arrays.asList("01", "02", "03"));
        dsd.addColumn("Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)", "", ReportUtils.map(moh711Indicators.presentingPregnancy1stANC1stTrimester(), indParams), "");
        dsd.addColumn("Clients issued with Iron", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithIron(), indParams), "");
        dsd.addColumn("Clients issued with Folic", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithFolic(), indParams), "");
        dsd.addColumn("Clients issued with Combined Ferrous Folate", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithFerrousFolic(), indParams), "");
        //dsd.addColumn("Pregnant women presenting in ANC with complication associated with FGM", "", ReportUtils.map(moh711Indicators.ancClientsWithFGMRelatedComplications(), indParams), "");

        return dsd;
    }

    /**
     * G. Cervical Cancer Screening Dataset
     * @return the data set
     */
    private DataSetDefinition createCacxScreeningDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("CACX-SCREENING");
        dsd.setDescription("CACX Screening");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        EmrReportingUtils.addRow(dsd, "ANC_CACX", "No. of Client receiving VIA /VILI /HPV VILI / HPV", ReportUtils.map(moh711Indicators.cacxScreened(), indParams), cacxScreeningDisaggregations, Arrays.asList("01", "02", "03"));
        return dsd;
    }
    /**
     *  H. Post Natal Care (PNC) Dataset
     * @return
     */
    private DataSetDefinition createPNCDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("PNC");
        dsd.setDescription("Post Natal Care (PNC)");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        dsd.addColumn("New PNC Clients", "", ReportUtils.map(moh711Indicators.noOfNewPNCClients(), indParams), "");
        dsd.addColumn("Revisiting PNC Clients", "", ReportUtils.map(moh711Indicators.noOfPNCClientsRevisits(), indParams), "");
        dsd.addColumn("Number of Cases of Fistula", "", ReportUtils.map(moh711Indicators.noOfFistulaCasesPNC(), indParams), "");
        dsd.addColumn("No Referred from the community unit for PNC", "", ReportUtils.map(moh711Indicators.noReferredFromCommunityForPNC(), indParams), "");

        return dsd;
    }
}