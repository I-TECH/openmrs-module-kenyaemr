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

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i\n" +
                "          where coalesce(i.via_vili_screening_method,i.hpv_screening_method,i.pap_smear_screening_method,i.colposcopy_screening_method)\n" +
                "           IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Screening Method");
        return cd;
    }

       public CohortDefinition patientScreeningVia(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i\n" +
                "where i.via_vili_screening_method IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientViaScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer VIA Screening Method");
        return cd;
    }

    public CohortDefinition patientScreenedByVIA(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientViaScreeningMethod", ReportUtils.map(patientScreeningVia(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientViaScreeningMethod AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientScreeningPap(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i\n" +
                "where i.pap_smear_screening_method IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientPapScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Pap Screening Method");
        return cd;
    }

    public CohortDefinition patientScreenedByPapSmear(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientPapScreeningMethod", ReportUtils.map(patientScreeningPap(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientPapScreeningMethod AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientScreeningHpv(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i\n" +
                "where i.hpv_screening_method IN ('"+val+"') and i.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientHpvScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Hpv Screening Method");
        return cd;
    }

    public CohortDefinition patientScreenedByHpv(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientHpvScreeningMethod", ReportUtils.map(patientScreeningHpv(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientHpvScreeningMethod AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientPositiveScreeningVia(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.via_vili_screening_method IN ('"+val+"')\n" +
                "  and i.via_vili_screening_result = 'Positive' and  i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientPositiveViaScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Positive Via Screening Method");
        return cd;
    }

    public CohortDefinition patientPositiveScreenedVia(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveViaScreening", ReportUtils.map(patientPositiveScreeningVia(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveViaScreening AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientPositiveScreeningColposcopy(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.colposcopy_screening_method IN ('"+val+"')\n" +
                "  and i.colposcopy_screening_result = 'Positive' and  i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientPositiveViaScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Positive Colposcopy Screening Method");
        return cd;
    }

    public CohortDefinition patientPositiveScreenedColposcopy(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveColposcopyScreening", ReportUtils.map(patientPositiveScreeningColposcopy(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveColposcopyScreening AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientPositiveScreeningHpv(String[] indicatorVal) {

        String val = StringUtils.join(indicatorVal,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.hpv_screening_method IN ('"+val+"')\n" +
                "  and i.hpv_screening_result = 'Positive' and  i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientPositiveHpvScreeningMethod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Positive Hpv Screening Method");
        return cd;
    }

    public CohortDefinition patientPositiveScreenedHpv(String[] indicatorVal, String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedPositiveHpvScreening", ReportUtils.map(patientPositiveScreeningHpv(indicatorVal), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedPositiveHpvScreening AND patientVisitType");
        return cd;
    }


    public CohortDefinition patientScreeningResult() {

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where\n" +
                "  coalesce(i.via_vili_screening_result,i.hpv_screening_result,i.pap_smear_screening_result,i.colposcopy_screening_result)  in ('Presumed','Suspicious for Cancer','Low grade lesion') \n" +
                "  and i.visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientScreeningResult");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cervical Cancer Screening Suspicious Result");
        return cd;
    }

    public CohortDefinition suspiciousScreeningCl(String visitType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("receivedResult", ReportUtils.map(patientScreeningResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientVisitType", ReportUtils.map(patientVisitType(visitType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("receivedResult AND patientVisitType");
        return cd;
    }

    public CohortDefinition patientTreatment(String[] treatmentMethod) {

        String val = StringUtils.join(treatmentMethod,"','");

        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where\n" +
                "  coalesce(i.colposcopy_treatment_method,i.hpv_treatment_method,i.pap_smear_treatment_method,i.via_vili_treatment_method) IN ('"+val+"')\n" +
                "  and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";
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
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i left join (select h.patient_id from kenyaemr_etl.etl_hiv_enrollment h " +
                "where h.visit_date <= date(:endDate)) h on h.patient_id = i.patient_id left join (select t.patient_id from kenyaemr_etl.etl_hts_test t where " +
                "t.final_test_result = 'Positive' and t.test_type = 2 and t.visit_date <= date(:endDate)) t on t.patient_id = i.patient_id where" +
                " i.visit_type = '"+visitType+"' and (h.patient_id is not null or t.patient_id is not null) and i.visit_date between date(:startDate) and date(:endDate)" +
                "group by i.patient_id;";

        cd.setName("HIV Positive Clients Screened");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Clients Screened");

        return cd;
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortDefinition HIVPositiveClientsScreenedWithPositiveResultsCl(String visitType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i left join (select h.patient_id from kenyaemr_etl.etl_hiv_enrollment h " +
                "where h.visit_date <= date(:endDate)) h on h.patient_id = i.patient_id left join (select t.patient_id from kenyaemr_etl.etl_hts_test t where " +
                "t.final_test_result = 'Positive' and t.test_type = 2 and t.visit_date <= date(:endDate)) t on t.patient_id = i.patient_id where i.visit_type = '"+visitType+"' " +
                " and coalesce(i.via_vili_screening_result,i.hpv_screening_result,i.pap_smear_screening_result,i.colposcopy_screening_result) = 'Positive' and (h.patient_id is not null or t.patient_id is not null) and  i.visit_date between date(:startDate) and " +
                "date(:endDate) group by i.patient_id;";

        cd.setName("HIV Positive With Positive Screening Results");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive With Positive Screening Results");

        return cd;
    }

}
