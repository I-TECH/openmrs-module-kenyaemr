/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv.art;

import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfLastViralLoadCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LowDetectableViralLoadCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by codehub on 10/28/15.
 * This report documents all possible elements associated with viral load
 */
@Component
@Builds({"kenyaemr.hiv.report.needsViralLoad"})
public class NeedsViralLoadTestReportBuilder extends CalculationReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    @Override
    protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), new IdentifierConverter());

        addStandardColumns(report, dsd);
        dsd.addColumn("UPN", identifierDef, "");
        dsd.addColumn("Last viral load", new ObsForPersonDataDefinition("Last viral load", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null ), "", new DataConverter() {
            @Override
            public Class<?> getInputDataType() {
                return Obs.class;
            }

            @Override
            public Class<?> getDataType() {
                return Double.class;
            }

            @Override
            public Object convert(Object input) {
                if(input == null){
                    return null;
                }
                Double value = ((Obs) input).getValueNumeric();
                if(value != null) {
                    return  value+ "copies/ml";
                }
                return null ;
            }
        });
        dsd.addColumn("Date of last viral load", new CalculationDataDefinition("Date of last viral load", new DateOfLastViralLoadCalculation()), "", new DateArtStartDateConverter());

        /*dsd.addColumn("Date of last viral load", new ObsForPersonDataDefinition("Date of last viral load", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null ), "", new DataConverter() {
            @Override
            public Class<?> getInputDataType() {
                return Obs.class;
            }

            @Override
            public Class<?> getDataType() {
                return Date.class;
            }

            @Override
            public Object convert(Object input) {
                if(input == null){
                    return null;
                }
                Date date = ((Obs) input).getObsDatetime();
                if(date != null) {
                    return  formatDate(date);
                }
                return null ;
            }
        });*/
        dsd.addColumn("LDL", new CalculationDataDefinition("LDL", new LowDetectableViralLoadCalculation()), "", new DataConverter() {
            @Override
            public Class<?> getInputDataType() {
                return CalculationResult.class;
            }

            @Override
            public Class<?> getDataType() {
                return String.class;
            }

            @Override
            public Object convert(Object input) {
                if(input == null){
                    return null;
                }
                String result =  ((CalculationResult) input).getValue().toString();
                if(!result.isEmpty()) {
                    return "Yes";
                }
                return null;
            }
        });
        dsd.addColumn("Date of ldl", new ObsForPersonDataDefinition("Date of ldl", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), null, null ), "", new DataConverter() {
            @Override
            public Class<?> getInputDataType() {
                return Obs.class;
            }

            @Override
            public Class<?> getDataType() {
                return Date.class;
            }

            @Override
            public Object convert(Object input) {
                if(input == null){
                    return null;
                }
                Date date = ((Obs) input).getObsDatetime();
                if(date != null) {
                    return  formatDate(date);
                }
                return null ;
            }
        });


    }
    private String formatDate(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return date == null?"":dateFormatter.format(date);
    }
}
