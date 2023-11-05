/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.viralSuppression;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohort definitions for viral suppression
 */
@Component
public class ViralSuppressionCohortLibrary {
    @Autowired
    private DatimCohortLibrary datimCohortLibrary;
       public CohortDefinition suppressed() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "endDate=${endDate}"));
        cd.addSearch("suppressedVLResults", ReportUtils.map(suppressedVLResults(), "endDate=${endDate}"));
        cd.setCompositionString("txcurr AND suppressedVLResults");
        return cd;
    }
    public  CohortDefinition suppressedVLResults() {
        String sqlQuery="select vl.patient_id\n" +
                "from (select b.patient_id,\n" +
                "             if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 856, mid(max(concat(b.visit_date, b.test_result)), 11),\n" +
                "                if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 1305 and\n" +
                "                   mid(max(concat(visit_date, test_result)), 11) = 1302, \"LDL\", \"\")) as vl_result\n" +
                "      from (select x.patient_id  as patient_id,\n" +
                "                   x.visit_date  as visit_date,\n" +
                "                   x.lab_test    as lab_test,\n" +
                "                   x.test_result as test_result,\n" +
                "                   urgency       as urgency\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "            group by x.patient_id, x.visit_date\n" +
                "            order by visit_date desc) b\n" +
                "      group by patient_id\n" +
                "      having max(visit_date) between\n" +
                "                 date_sub(:endDate, interval 12 MONTH) and date(:endDate)) vl\n" +
                "where vl_result < 200 or vl_result='LDL';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unsuppressedVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has unsuppressed VL Results");
        return cd;
    }
    public CohortDefinition unsuppressed() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "endDate=${endDate}"));
        cd.addSearch("unsuppressedVLResults", ReportUtils.map(unsuppressedVLResults(), "endDate=${endDate}"));
        cd.setCompositionString("txcurr AND unsuppressedVLResults");
        return cd;
    }
    public  CohortDefinition unsuppressedVLResults() {
        String sqlQuery="select a.patient_id\n" +
                "from (select b.patient_id,\n" +
                "             if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 856, mid(max(concat(b.visit_date, b.test_result)), 11),\n" +
                "                if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 1305 and\n" +
                "                   mid(max(concat(visit_date, test_result)), 11) = 1302, \"LDL\", \"\")) as vl_result\n" +
                "      from (select x.patient_id  as patient_id,\n" +
                "                   x.visit_date  as visit_date,\n" +
                "                   x.lab_test    as lab_test,\n" +
                "                   x.test_result as test_result,\n" +
                "                   urgency       as urgency\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "            group by x.patient_id, x.visit_date\n" +
                "            order by visit_date desc) b\n" +
                "      group by patient_id\n" +
                "      having max(visit_date) between\n" +
                "                 date_sub(:endDate, interval 12 MONTH) and date(:endDate)) a\n" +
                "where a.vl_result >= 200;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unsuppressedVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.setDescription("Has unsuppressed VL Results");
        return cd;
    }

    public  CohortDefinition oldVLResults() {
        String sqlQuery="select a.patient_id\n" +
                "from (select patient_id,\n" +
                "             coalesce(mid(max(concat(date(visit_date), date(date_test_requested))), 11),\n" +
                "                      max(visit_date)) as lab_test_date\n" +
                "      from kenyaemr_etl.etl_laboratory_extract\n" +
                "      where lab_test in (1305, 856)\n" +
                "      group by patient_id\n" +
                "      having lab_test_date < date_sub(:endDate, interval 12 MONTH)) a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("OldVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has old VL Results");
        return cd;
    }

    public CohortDefinition noCurrentVLResults() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "endDate=${endDate}"));
        cd.addSearch("oldVLResults", ReportUtils.map(oldVLResults(), "endDate=${endDate}"));
        cd.setCompositionString("txcurr AND oldVLResults");
        return cd;
    }
    public  CohortDefinition hasVL() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_laboratory_extract where lab_test in (1305,856)\n" +
                "                group by patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has VL Results");
        return cd;
    }
    public CohortDefinition noVLResults() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "endDate=${endDate}"));
        cd.addSearch("hasVL", ReportUtils.map(hasVL(), "endDate=${endDate}"));
        cd.setCompositionString("txcurr AND NOT hasVL");
        return cd;
    }
}
