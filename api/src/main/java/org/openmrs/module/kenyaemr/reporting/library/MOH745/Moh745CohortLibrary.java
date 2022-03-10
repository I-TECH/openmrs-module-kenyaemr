/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.MOH745;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class Moh745CohortLibrary {

    protected static final Log log = LogFactory.getLog(Moh745CohortLibrary.class);


    public CohortDefinition patientVisitType(String visitType) {

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.visit_type = '"+visitType+"' and  i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientVisitType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Patients Visit Type");
        return cd;
    }

    public CohortDefinition patientScreeningMethod(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        log.warn("sql query executing" +sqlQuery + " here");
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Screening Method");
        return cd;
    }

    public CohortDefinition receivedScreeningCl(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientScreeningMethod", ReportUtils.map(patientScreeningMethod(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientScreeningMethod AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientPositiveScreening(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientPositiveScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Positive Screening Method");
        return cd;
    }

    public CohortDefinition receivedPositiveScreeningCl(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveScreening", ReportUtils.map(patientPositiveScreening(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveScreening AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientScreeningResult(String result) {

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = '"+result+"' and i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientScreeningResult");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Screening Result");
        return cd;
    }

    public CohortDefinition suspiciousScreeningCl(String result, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedResult", ReportUtils.map(patientScreeningResult(result), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedResult AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientTreatment(String[] treatmentMethod) {

        String val = StringUtils.join(treatmentMethod,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where " +
                " i.treatment_method IN ('"+val+"') and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientTreatmentMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Treatment Method");
        return cd;
    }

    public CohortDefinition treatmentMethodCl(String[] treatmentMethod, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveScreening", ReportUtils.map(patientTreatment(treatmentMethod), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveScreening AND patientVisitType");
        return cd;
    }

    /*HIV Positive Clients Screened*/
    public CohortDefinition HIVPositiveClientsScreenedCl(String visitType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i left join kenyaemr_etl.etl_hiv_enrollment h on" +
                " h.patient_id = i.patient_id left join kenyaemr_etl.etl_hts_test t on t.patient_id = i.patient_id where t.final_test_result = 'Positive'" +
                " and i.visit_type = '"+visitType+"' and i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("HIV Positive Clients Screened");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Clients Screened");

        return cd;
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortDefinition HIVPositiveClientsScreenedWithResultsCl(String visitType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i left join kenyaemr_etl.etl_hiv_enrollment h on h.patient_id = i.patient_id " +
                "left join kenyaemr_etl.etl_hts_test t on t.patient_id = i.patient_id where t.final_test_result = 'Positive' and i.screening_result = 'Positive' and " +
                "i.visit_type = '"+visitType+"' and i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("HIV Positive With Positive Screening Results");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive With Positive Screening Results");

        return cd;
    }

}
