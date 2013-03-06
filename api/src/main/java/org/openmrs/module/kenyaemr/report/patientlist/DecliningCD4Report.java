/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.report.patientlist;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.cd4.DecliningCD4Calculation;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class DecliningCD4Report extends PatientListReportBuilder {

    public DecliningCD4Report() {
        setCalculation(new DecliningCD4Calculation());
    }

    @Override
    public void addColumns(PatientDataSetDefinition dsd) {
        Concept concept = Context.getConceptService().getConceptByUuid(MetadataConstants.CD4_CONCEPT_UUID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -180);
        Date onOrBefore = calendar.getTime();

        super.addColumns(dsd);
        dsd.removeColumnDefinition("View");

        dsd.addColumn("Previous CD4", new ObsForPersonDataDefinition("Previous CD4", TimeQualifier.LAST, concept, onOrBefore, null), "", new DataConverter() {
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
                return ((Obs) input).getValueNumeric();
            }
        });

        dsd.addColumn("Current CD4", new ObsForPersonDataDefinition("Current CD4", TimeQualifier.LAST, concept, new Date(), null), "", new DataConverter() {
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
                return ((Obs) input).getValueNumeric();
            }
        });

        addViewColumn(dsd);
    }
}