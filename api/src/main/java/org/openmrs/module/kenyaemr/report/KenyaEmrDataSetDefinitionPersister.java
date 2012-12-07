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

package org.openmrs.module.kenyaemr.report;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Each ReportManager's DSDs are exposed as "${reportmanagerclassname}:${dsdName}"
 */
@Handler(supports=DataSetDefinition.class)
public class KenyaEmrDataSetDefinitionPersister implements DataSetDefinitionPersister {

    @Autowired
    KenyaEmrService service;

    @Override
    public DataSetDefinition getDefinition(Integer integer) {
        // don't allow fetching by PK (we have none)
        return null;
    }

    @Override
    public DataSetDefinition getDefinitionByUuid(String uniqueName) {
        ManagerAndDsdName managerAndDsdName = new ManagerAndDsdName(uniqueName);
        ReportManager reportManager = service.getReportManager(managerAndDsdName.getManagerClassname());
        return toDataSetDefinition(reportManager, managerAndDsdName.getDsdName());
    }

    @Override
    public List<DataSetDefinition> getAllDefinitions(boolean b) {
        List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();
        for (ReportManager reportManager : service.getReportManagersByTag(null)) {
            ReportDefinition reportDefinition = reportManager.getReportDefinition();
            if (reportDefinition == null || reportDefinition.getDataSetDefinitions() == null) {
                continue;
            }
            for (String dsdName : reportDefinition.getDataSetDefinitions().keySet()) {
                ret.add(toDataSetDefinition(reportManager, dsdName));
            }
        }
        return ret;
    }

    @Override
    public int getNumberOfDefinitions(boolean b) {
        return getAllDefinitions(b).size();
    }

    @Override
    public List<DataSetDefinition> getDefinitions(String s, boolean b) {
        // don't allow searching by string
        return Collections.emptyList();
    }

    @Override
    public DataSetDefinition saveDefinition(DataSetDefinition dataSetDefinition) {
        throw new IllegalArgumentException("Not Allowed");
    }

    @Override
    public void purgeDefinition(DataSetDefinition dataSetDefinition) {
        throw new IllegalArgumentException("Not Allowed");
    }

    private DataSetDefinition toDataSetDefinition(ReportManager manager, String dsdName) {
        Mapped<? extends DataSetDefinition> mapped = manager.getReportDefinition().getDataSetDefinitions().get(dsdName);
        DataSetDefinition dataSetDefinition = mapped.getParameterizable();
        // since the ReportManager always creates these on the fly, they have arbitrary UUIDs, so we set a known one.
        dataSetDefinition.setUuid(new ManagerAndDsdName(manager, dsdName).toString());
        return dataSetDefinition;
    }

    class ManagerAndDsdName {
        private String managerClassname;
        private String dsdName;

        public ManagerAndDsdName(ReportManager manager, String dsdName) {
            this.managerClassname = manager.getClass().getName();
            this.dsdName = dsdName;
        }

        public ManagerAndDsdName(String s) {
            String[] split = s.split(":");
            managerClassname = split[0].replaceAll("-", ".");
            dsdName = split[1];
        }

        public String getManagerClassname() {
            return managerClassname;
        }

        public String getDsdName() {
            return dsdName;
        }

        @Override
        public String toString() {
            return managerClassname.replaceAll("\\.", "-") + ":" + dsdName;
        }
    }

}
