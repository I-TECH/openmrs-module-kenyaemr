/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSPredictionCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.MFLCodeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLFirstHIVTestDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLFirstHIVTestResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLPredictionCategoryDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLPredictionScoreDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;


/**
 * Report builder for ETL MOH 731
 */
@Component
@Builds({"kenyaemr.etl.common.report.htsprediction"})
public class HTSPredictionReportBuilder extends AbstractHybridReportBuilder {
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
        protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {
        }

        @Override
        protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
                return null;
        }


        protected Mapped<CohortDefinition> allPatientsCohort() {
                CohortDefinition cd = new HTSPredictionCohortDefinition();
                cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                cd.addParameter(new Parameter("endDate", "End Date", Date.class));
                cd.setName("Screened Patients");
                return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
        }

        @Override
        protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

                PatientDataSetDefinition allVisits = screenedPatientsDataSetDefinition("screenedPatients");
                allVisits.addRowFilter(allPatientsCohort());
                DataSetDefinition allPatientsDSD = allVisits;

                return Arrays.asList(
                        ReportUtils.map(allPatientsDSD, "startDate=${startDate},endDate=${endDate}")
                );
        }

        protected PatientDataSetDefinition screenedPatientsDataSetDefinition(String datasetName) {

                PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
                dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
                String defParam = "startDate=${startDate},endDate=${endDate}";

                PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
                DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), new IdentifierConverter());
                PatientIdentifierType nupi = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
                DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
                DataDefinition nupiDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nupi.getName(), nupi), identifierFormatter);

                ETLPredictionScoreDataDefinition predictionScoreDataDefinition = new ETLPredictionScoreDataDefinition();
                predictionScoreDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
                predictionScoreDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
                ETLPredictionCategoryDataDefinition predictionCategoryDataDefinition = new ETLPredictionCategoryDataDefinition();
                predictionCategoryDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
                predictionCategoryDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
                ETLFirstHIVTestResultDataDefinition firstHIVTestResultDataDefinition = new ETLFirstHIVTestResultDataDefinition();
                firstHIVTestResultDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
                firstHIVTestResultDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
                ETLFirstHIVTestDateDataDefinition firstHIVTestDateDataDefinition = new ETLFirstHIVTestDateDataDefinition();
                firstHIVTestDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
                firstHIVTestDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

                DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
                DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
                dsd.addColumn("MFL Code", new MFLCodeDataDefinition(), "");
                dsd.addColumn("id", new PersonIdDataDefinition(), "");
                dsd.addColumn("Name", nameDef, "");
                dsd.addColumn("CCC No", identifierDef, "");
                dsd.addColumn("NUPI", nupiDef, "");
                dsd.addColumn("Sex", new GenderDataDefinition(), "", null);
                dsd.addColumn("UPN", identifierDef, "");
                dsd.addColumn("ML Prediction Score", predictionScoreDataDefinition, "startDate=${startDate},endDate=${endDate}");
                dsd.addColumn("ML Prediction Category", predictionCategoryDataDefinition, "startDate=${startDate},endDate=${endDate}");
                dsd.addColumn("Date Of Test", firstHIVTestDateDataDefinition, "startDate=${startDate},endDate=${endDate}", new DateConverter(DATE_FORMAT));
                dsd.addColumn("Actual Test Results", firstHIVTestResultDataDefinition, "startDate=${startDate},endDate=${endDate}");
                return dsd;
        }
}
