/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.moh711;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohort definitions used specifically in the MOH711 report
 */
@Component
public class Moh711CohortLibrary {
    @Autowired
    private ETLMoh731GreenCardCohortLibrary moh731GreenCardCohort;

    // TODO
/**
 * Latest MCH enrollment at ANC
 */
public CohortDefinition latestMCHEnrollmentAtANC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
            "left join (select d.patient_id,max(visit_date) as latest_disc from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
            "    where d.program_name = 'MCH Mother' group by d.patient_id)d on e.patient_id = d.patient_id\n" +
            "where e.service_type = 1622 and date(e.visit_date) < date(:endDate) and (date(e.visit_date) > date(d.latest_disc) or d.patient_id is null);";
    cd.setName("Latest MCH enrollment");
    cd.setQuery(sqlQuery);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setDescription("Latest MCH enrollment at ANC");
    return cd;
}
    /**
     * Latest MCH enrollment
     * @return
     */
    public CohortDefinition latestMCHEnrollment() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "left join (select d.patient_id,max(visit_date) as latest_disc from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "    where d.program_name = 'MCH Mother' group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "where date(e.visit_date) < date(:endDate) and (date(e.visit_date) > date(d.latest_disc) or d.patient_id is null);";
        cd.setName("Latest MCH enrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Latest MCH enrollment");
        return cd;
    }
    /**
     * Latest MCH enrollment at PNC
     */
    public CohortDefinition latestMCHEnrollmentAtPNC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "left join (select d.patient_id,max(visit_date) as latest_disc from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "    where d.program_name = 'MCH Mother' group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "where e.service_type = 1623 and date(e.visit_date) < date(:endDate) and (date(e.visit_date) > date(d.latest_disc) or d.patient_id is null);";
        cd.setName("Latest MCH enrollment at PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Latest MCH enrollment PNC");
        return cd;
    }
    /**
     * No.of New ANC Clients (First ANC visit)
     * @return
     */
    public CohortDefinition noOfANCClients() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("firstANCVisit", ReportUtils.map(moh731GreenCardCohort.firstANCVisitMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("firstANCVisit and latestMCHEnrollmentAtANC");
        return cd;
    }

    /**
     * No.of revisiting ANC clients
     */
    public CohortDefinition noOfANCClientsRevisits() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select d.patient_id from\n" +
                "         (select e.patient_id, max(e.visit_date) as latest_enrollment_date,av.visit_date as 1st_anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       inner join\n" +
                "        (select av.patient_id,av.visit_date as visit_date from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number != 1\n" +
                "        and av.visit_date between date(:startDate) and date(:endDate)) av on e.patient_id = av.patient_id\n" +
                "       group by e.patient_id\n" +
                "       having 1st_anc_visit between date(:startDate) and date(:endDate))d;";

        cd.setName("Revisiting ANC clients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Revisiting ANC clients");
        return cd;
    }
    /**
     *No.of Clients given IPT (1st dose)
     */
    public CohortDefinition noOfANCClientsGivenIPT1stDose() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.of Clients given IPT (1st dose)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients given IPT (1st dose)");
        return cd;
    }
    /**
     *No.of Clients given IPT (2nd dose)
     */
    public CohortDefinition noOfANCClientsGivenIPT2ndDose() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.of Clients given IPT (2nd dose)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients given IPT (2nd dose)");
        return cd;
    }
    /**
     *No.of Clients given IPT (3rd dose)
     */
    public CohortDefinition noOfANCClientsGivenIPT3rdDose() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.of Clients given IPT (3rd dose)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients given IPT (3rd dose)");
        return cd;
    }
    /**
     *No.of Clients with Hb < 11 g/dl
     */
    public CohortDefinition noOfANCClientsLowHB() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select d.patient_id from\n" +
                "(select e.patient_id, hb.visit_date as anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "inner join\n" +
                "(select hb.patient_id,hb.visit_date,hb.anc_visit_number as no from kenyaemr_etl.etl_mch_antenatal_visit hb where hb.hemoglobin < 11\n" +
                "and hb.visit_date between date(:startDate) and date(:endDate)) hb on e.patient_id = hb.patient_id\n" +
                "group by e.patient_id\n" +
                "having anc_visit between date(:startDate) and date(:endDate))d;";
        cd.setName("No.of Clients with Hb < 11 g/dl");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients with Hb < 11 g/dl");
        return cd;
    }
    /**
     *No.of Clients completed 4 Antenatal Visits
     */
    public CohortDefinition ancClientsCompleted4Visits() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       inner join kenyaemr_etl.etl_mch_antenatal_visit av on e.patient_id = av.patient_id\n" +
                "where av.visit_date between date(:startDate) and date(:endDate) and av.anc_visit_number = 4\n" +
                "group by e.patient_id;";
        cd.setName("No.of Clients completed 4 Antenatal Visits");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients completed 4 Antenatal Visits");
        return cd;
    }
    /**
     *No.LLINs distributed to under 1 year
     */
    public CohortDefinition distributedLLINsUnder1Year() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.LLINs distributed to under 1 year");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.LLINs distributed to under 1 year");
        return cd;
    }
    /**
     *No.of LLINs distributed in preventive services
     */
    public CohortDefinition distributedLLINs() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where p.long_lasting_insecticidal_net\n" +
                "between date(:startDate) and date(:endDate);";
        cd.setName("No.of LLINs distributed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of LLINs distributed");
        return cd;
    }

    /**
     * No.of LLINs distributed to ANC clients
     */
    public CohortDefinition distributedLLINsToANCClients() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("distributedLLINs", ReportUtils.map(distributedLLINs(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollment and distributedLLINs");
        return cd;
    }
    /**
     * No of clients tested for Syphillis at HTS
     * @return
     */
    public CohortDefinition testedForSyphillisHTSDualKit() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t.patient_id from kenyaemr_etl.etl_hts_test t where t.test_1_kit_name = 'Dual Kit' and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and t.syphillis_test_result in (1228,1229);";
        cd.setName("No.of clients Tested for Syphilis HTS Dual Kit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients Tested for Syphilis HTS Dual Kit");
        return cd;
    }

    /**
     * MCH Clients tested for Syphillis at HTS
     * @return
     */
    public CohortDefinition ancClientsTestedForSyphillisAtHTS() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedForSyphillisHTSDualKit", ReportUtils.map(testedForSyphillisHTSDualKit(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedForSyphillisHTSDualKit and latestMCHEnrollment");
        return cd;
    }
    /**
     * No.of clients tested Positive for Syphillis at HTS
     * @return
     */
    public CohortDefinition clientsTestedPositiveForSyphillisHTSDualKit() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t.patient_id from kenyaemr_etl.etl_hts_test t where t.test_1_kit_name = 'Dual Kit' and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and t.syphillis_test_result = 1228;";
        cd.setName("No.of clients Tested Positive for Syphilis HTS Dual Kit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients Tested Positive for Syphilis HTS Dual Kit");
        return cd;
    }

    /**
     * MCH clients tested Positive for Syphillis at HTS
     * @return
     */
    public CohortDefinition mchClientsTestedPositiveForSyphillisAtHTS() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientsTestedPositiveForSyphillisHTSDualKit", ReportUtils.map(clientsTestedPositiveForSyphillisHTSDualKit(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedForSyphillisHTSDualKit and latestMCHEnrollment");
        return cd;
    }
    /**
     * Tested for Syphilis at ANC 1
     * @return
     */
    public CohortDefinition ancClientsTestedForSyphillisANC1() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "and v.anc_visit_number = 1 and v.syphilis_test_status in (1229, 1228);";
        cd.setName("No.of clients Tested for Syphilis ANC1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients Tested for Syphilis ANC1");
        return cd;
    }

    /**
     * Tested for Syphilis after ANC 1
     * @return
     */
    public CohortDefinition ancClientsTestedForSyphillisAfterANC1() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "and v.anc_visit_number != 1 and v.syphilis_test_status in (1229, 1228);";
        cd.setName("No.of clients Tested for Syphilis after ANC 1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients Tested for Syphilis after ANC 1");
        return cd;
    }
    /**
     * Tested for Positive Syphillis during ANC
     * @return
     */
    public CohortDefinition ancClientsTestedPositiveForSyphillisAtANC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "and v.syphilis_test_status = 1228;";
        cd.setName("No.of clients Tested Positive for Syphilis at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients Tested Positive for Syphilis at ANC");
        return cd;
    }
    /**
     *No.of clients Tested for Syphilis
     */
    public CohortDefinition ancClientsTestedForSyphillis() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ancClientsTestedForSyphillisANC1", ReportUtils.map(ancClientsTestedForSyphillisANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ancClientsTestedForSyphillisAfterANC1", ReportUtils.map(ancClientsTestedForSyphillisAfterANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ancClientsTestedForSyphillisAtHTS", ReportUtils.map(ancClientsTestedForSyphillisAtHTS(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ancClientsTestedForSyphillisANC1 OR (ancClientsTestedForSyphillisAfterANC1 AND NOT ancClientsTestedForSyphillisANC1) OR (ancClientsTestedForSyphillisAtHTS AND NOT ancClientsTestedForSyphillisANC1)");
        return cd;
    }
    /**
     *No.of clients Tested Positive for Syphilis
     */
    public CohortDefinition ancClientsTestedSyphillisPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ancClientsTestedForSyphillis", ReportUtils.map(ancClientsTestedForSyphillis(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ancClientsTestedPositiveForSyphillisAtANC", ReportUtils.map(ancClientsTestedPositiveForSyphillisAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ancClientsTestedForSyphillis AND ancClientsTestedPositiveForSyphillisAtANC");
        return cd;
    }
    /**
     *Total women done breast examination
     */
    public CohortDefinition breastExaminationDone() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "inner join kenyaemr_etl.etl_mch_antenatal_visit av on e.patient_id = av.patient_id\n" +
                "where av.visit_date between date(:startDate) and date(:endDate) and av.breast_exam_done = 1065\n" +
                "group by e.patient_id;";
        cd.setName("Total women done breast examination");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total women done breast examination");
        return cd;
    }
    /**
     *No.of adolescents (10-14 years) presenting with pregnancy at 1st ANC Visit
     */
    public CohortDefinition adolescents10To14FirstANC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.of adolescents (10-14 years) presenting with pregnancy at 1st ANC Visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of adolescents (10-14 years) presenting with pregnancy at 1st ANC Visit");
        return cd;
    }
    /**
     *No.of adolescents (15-19 years) presenting with pregnancy at 1st ANC Visit
     */
    public CohortDefinition adolescents15To19FirstANC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.of adolescents (15-19 years) presenting with pregnancy at 1st ANC Visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of adolescents (15-19 years) presenting with pregnancy at 1st ANC Visit");
        return cd;
    }
    /**
     *No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit
     */
    public CohortDefinition youth20To24FirstANC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit");
        return cd;
    }
    /**
     *No.of Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)
     */
    public CohortDefinition presentingPregnancy1stANC1stTrimester() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select av.patient_id from kenyaemr_etl.etl_mch_antenatal_visit av where av.visit_date between date(:startDate)\n" +
                "and date(:endDate) and av.maturity <= 12;";
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)");
        return cd;
    }
    /**
    public CohortDefinition ancClientsIssuedWithIron() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientsIssuedWithIron", ReportUtils.map(clientsIssuedWithIron(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND clientsIssuedWithIron");
        return cd;
    }
    /**
     *No.of clients issued with Iron - preventive services
     */
    public CohortDefinition clientsIssuedWithIron() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where date(coalesce(p.iron_1,p.iron_2,p.iron_3,p.iron_4))\n" +
                "between date(:startDate) and date(:endDate);";
        cd.setName("No.of clients issued with Iron");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients issued with Iron");
        return cd;
    }
    /**
     * ANC clients issued with Iron
     * @return
     */
    public CohortDefinition ancClientsIssuedWithIron() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientsIssuedWithIron", ReportUtils.map(clientsIssuedWithIron(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND clientsIssuedWithIron");
        return cd;
    }
    /**
     *No.of clients issued with Folic - preventive services
     */
    public CohortDefinition clientsIssuedWithFolic() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where date(coalesce(p.folate_1,p.folate_2,p.folate_3,p.folate_4))\n" +
                "between date(:startDate) and date(:endDate);";
        cd.setName("No.of clients issued with Folic");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients issued with Folic");
        return cd;
    }
    /**
     * ANC clients issued with Folic acid
     * @return
     */
    public CohortDefinition ancClientsIssuedWithFolic() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientsIssuedWithFolic", ReportUtils.map(clientsIssuedWithFolic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND clientsIssuedWithFolic");
        return cd;
    }
    /**
     *No.of clients issued with Combined Ferrous Folate - preventive services
     */
    public CohortDefinition clientsIssuedWithFerrousFolic() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where date(coalesce(p.folate_iron_1,p.folate_iron_2,p.folate_iron_3,p.folate_iron_4))\n" +
                "between date(:startDate) and date(:endDate);";
        cd.setName("No.of clients issued with Combined Ferrous Folate");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients issued with Combined Ferrous Folate");
        return cd;
    }
    /**
     * ANC clients issued with combined Ferrous Folate
     * @return
     */
    public CohortDefinition ancClientsIssuedWithFerrousFolic() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientsIssuedWithFerrousFolic", ReportUtils.map(clientsIssuedWithFerrousFolic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND clientsIssuedWithFerrousFolic");
        return cd;
    }
    /**
     *No.of pregnant women presenting in ANC with complication associated with FGM
     */
    public CohortDefinition ancClientsWithFGMRelatedComplications() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "";
        cd.setName("ancClientsWithFGMRelatedComplications");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of pregnant women presenting in ANC with complication associated with FGM");
        return cd;
    }

    /**
     * Screened for CACX through common form
     * @return
     */
    public CohortDefinition cacxScreenedCommon() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "and s.screening_method in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test');";
        cd.setName("Screened for CACX through common form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of clients issued with Combined Ferrous Folate");
        return cd;
    }
    /**
     * Screened for CACX during ANC visit
     * @return
     */
    public CohortDefinition cacxScreenedAtANC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "and cacx_screening_method in (885,162816,164977);";
        cd.setName("Screened for CACX during ANC visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for CACX during ANC visit");
        return cd;
    }
    /**
     * Screened For cancer during ANC at anc visit or Cacx form
     * @return
     */
    public CohortDefinition cacxScreened() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("cacxScreenedAtANC", ReportUtils.map(cacxScreenedAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("cacxScreenedCommon", ReportUtils.map(cacxScreenedCommon(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND (cacxScreenedAtANC OR cacxScreenedCommon)");
        return cd;
    }
    /**
     * No.Screened for Cervical Cancer using specified method- screening form
     * @return
     */
    public CohortDefinition cacxScreenedMethodSCForm(String conceptName) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "       and s.screening_method = '"+conceptName+"';";
        cd.setName("No.Screened for Pap smear Cacx screening form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.Screened for Pap smear");
        return cd;
    }
    /**
     * No.Screened for cervical cancer using specified method - ANC visit form
     * @return
     */
    public CohortDefinition cacxScreenedMethodANC(Integer conceptId) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "and v.cacx_screening_method = "+conceptId+";";
        cd.setName("No.Screened for Pap smear ANC visit form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.Screened for Pap smear ANC visit form");
        return cd;
    }
    /**
     * ANC clients Screened for cervical Cancer using specified method through ANC form aor CACX screening form
     * @return
     */
    public CohortDefinition cacxScreenedWithMethodAtANC(String conceptName, Integer conceptId) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("cacxScreenedMethodSCForm", ReportUtils.map(cacxScreenedMethodSCForm(conceptName), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("cacxScreenedMethodANC", ReportUtils.map(cacxScreenedMethodANC(conceptId), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND (cacxScreenedMethodSCForm OR cacxScreenedMethodANC)");
        return cd;
    }
    public CohortDefinition viaViliPositiveCacxScreening(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('VIA','VILI') and s.screening_result in ('Positive') ;";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }
    public CohortDefinition viaViliPositiveANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
        " and v.cacx_screening_method  in (162816,164977) and v.cacx_screening in (703);";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }

    public CohortDefinition viaViliPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("viaViliPositiveCacxScreening", ReportUtils.map(viaViliPositiveCacxScreening(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("viaViliPositiveANC", ReportUtils.map(viaViliPositiveANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND (viaViliPositiveCacxScreening OR viaViliPositiveANC)");
        return cd;
    }


    public CohortDefinition hpvPositiveCacxScreening(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method='HPV Test' and s.screening_result='Positive';";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }
    public CohortDefinition hpvPositiveANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and v.cacx_screening_method =159895 and v.cacx_screening=703;";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }
    public CohortDefinition hpvPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("hpvPositiveCacxScreening", ReportUtils.map(hpvPositiveCacxScreening(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hpvPositiveANC", ReportUtils.map(hpvPositiveANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND (hpvPositiveCacxScreening OR hpvPositiveANC)");
        return cd;
    }

    public CohortDefinition suspiciousCancerLessionsCACXScreening(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test') and s.screening_result ='Suspicious for cancer';";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("");

        return cd;
    }
    public CohortDefinition suspiciousCancerLessionsANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and v.cacx_screening_method  in (885,162816,164977) and v.cacx_screening =159395;";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }
    public CohortDefinition suspiciousCancerLessions() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("suspiciousCancerLessionsCACXScreening", ReportUtils.map(suspiciousCancerLessionsCACXScreening(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("suspiciousCancerLessionsANC", ReportUtils.map(suspiciousCancerLessionsANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC and (suspiciousCancerLessionsCACXScreening OR suspiciousCancerLessionsANC)");
        return cd;
    }

    public CohortDefinition treatedUsingCyrotherapy(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test') and s.screening_result='Positive' and s.treatment_method in ('Cryotherapy performed (single Visit)','Cryotherapy performed','Cryotherapy postponed');";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }

    public CohortDefinition treatedUsingLEEP(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test') and s.screening_result='Positive' and s.treatment_method = 'LEEP performed';";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }
    /**
     * Enrolled in HIV
     * @return
     */
    public CohortDefinition enrolledInHIV(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e\n" +
                "left join (select d.patient_id,coalesce(date(max(effective_discontinuation_date)),max(visit_date))\n" +
                "    as latest_disc from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "        where d.program_name = 'HIV' group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "where date(e.visit_date) < date(:endDate) and (date(e.visit_date) > date(d.latest_disc) or d.patient_id is null);";
        cd.setName("Enrolled in HIV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Enrolled in HIV");

        return cd;
    }
    /**
     * Has Positive test result in HTS
     * @return
     */
    public CohortDefinition htsPositive(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select t.patient_id from kenyaemr_etl.etl_hts_test t where t.final_test_result = 'Positive' and test_type = 2;";
        cd.setName("Has Positive test result in HTS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has Positive test result in HTS");

        return cd;
    }
    /**
     * Has Positive result in MCH Enrollment
     * @return
     */
    public CohortDefinition mchPositive(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="Select e.patient_id from kenyaemr_etl.etl_mch_enrollment e where e.hiv_status = 703;";
        cd.setName("Has Positive result in MCH Enrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has Positive result in MCH Enrollment");

        return cd;
    }
    /**
     * Has Positive result in ANC
     * @return
     */
    public CohortDefinition ancPositive(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="Select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.final_test_result = 'Positive';";
        cd.setName("Has Positive result in ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has Positive result in ANC");

        return cd;
    }
    /**
     * Tested HIV positive at Labour and Delivery
     * @return
     */
    public CohortDefinition labourAndDeliveryPositive(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="Select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.final_test_result = 'Positive' " +
                "and coalesce(date(d.date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate);";
        cd.setName("Has Positive result in L&D");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Has Positive result at L&D");

        return cd;
    }
    /**
     * Screened for CACX and HIV Positive
     * @return
     */
    public CohortDefinition cacxScreenedAndHIVPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("cacxScreened", ReportUtils.map(cacxScreened(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("enrolledInHIV", ReportUtils.map(enrolledInHIV(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("htsPositive", ReportUtils.map(htsPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("mchPositive", ReportUtils.map(mchPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ancPositive", ReportUtils.map(ancPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("cacxScreened AND (enrolledInHIV OR htsPositive OR mchPositive OR ancPositive)");
        return cd;
    }
    public CohortDefinition noOfRevisitingPNCClients() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("pncReVisitMchms", ReportUtils.map(pncReVisitMchms(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtPNC", ReportUtils.map(latestMCHEnrollmentAtPNC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtPNC and pncReVisitMchms");
        return cd;
    }
    /**
     * First PNC visits
     * @return
     */
    public CohortDefinition firstPNCVisitMchms(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv\n" +
                "where pv.pnc_visit_no = 1 and pv.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("First/new PNC Visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("First/new PNC visit");

        return cd;
    }
    /**
     * PNC Revisits
     * @return
     */
    public CohortDefinition pncReVisitMchms(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv\n" +
                "where (pv.pnc_visit_no > 1 or pv.pnc_visit_no is null) and pv.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("PNC re-visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PNC re-visit");

        return cd;
    }
    /**
     *New PNC visits
     * @return
     */
    public CohortDefinition noOfFirstPNCClients() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("firstPNCVisitMchms", ReportUtils.map(firstPNCVisitMchms(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtPNC", ReportUtils.map(latestMCHEnrollmentAtPNC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtPNC and firstPNCVisitMchms");
        return cd;
    }
    /**
     *PNC re-visits
     * @return
     */
/*    public CohortDefinition noOfRevisitingPNCClients() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("pncReVisitMchms", ReportUtils.map(pncReVisitMchms(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtPNC", ReportUtils.map(latestMCHEnrollmentAtPNC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtPNC and pncReVisitMchms");
        return cd;
    }*/
    /**
     * No.of Fistula cases during PNC
     * @return
     */
    public CohortDefinition noOfFistulaCasesPNC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv\n" +
                "where pv.fistula_screening in (127847,49) and pv.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("No.of Fistula cases during PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Fistula cases during PNC");

        return cd;
    }
    /**
     * No.referred from Community for PNC services
     * @return
     */
    public CohortDefinition noReferredFromCommunityForPNC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv\n" +
                "where pv.referred_from = 163488 and pv.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("No.referred from Community for PNC services");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.referred from Community for PNC services");

        return cd;
    }
    /**
     * Delivery method
     * @param mode
     * @return
     */
    public CohortDefinition deliveryMethod(Integer mode){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.mode_of_delivery = "+mode+";";
        cd.setName("Delivery method");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Delivery method");

        return cd;
    }
    /**
     * Live Births
     * @return
     */
    public CohortDefinition liveBirths(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=151849;";
        cd.setName("Live Births");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Live Births");

        return cd;
    }
    /**
     * No.of Low birth weight Babies (below 2500 grams)
     * @return
     */
    public CohortDefinition lowBirthWeight(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.birth_weight < 2.5;";
        cd.setName("No.of Low birth weight Babies (below 2500 grams)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Low birth weight Babies (below 2500 grams)");

        return cd;
    }

    /**
     * No.of births with deformities
     * @return
     */
    public CohortDefinition deformities(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.birth_with_deformity=155871;";
        cd.setName("No.of births with deformoties");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of births with deformoties");

        return cd;
    }

    /**
     * No.of neonates given Vit "K"
     * @return
     */
    public CohortDefinition givenVitaminK(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =";";
        cd.setName("No.of neonates given  Vit K");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of neonates given Vit K");

        return cd;
    }
    /**
     * No.of babies applied chlorhexidine for cord care
     * @return
     */
    public CohortDefinition chlorhexidineForCordCaregiven(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="";
        cd.setName("No.of babies applied chlorhexidine for cord care");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of babies applied chlorhexidine for cord care");

        return cd;
    }

    /**
     * No of neonates 0 -28 days put on Continous Positive Airway Pressure(CPAP)
     * @return
     */
    public CohortDefinition continousPositiveAirwayPressureAt0To28Days(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="";
        cd.setName("No of neonates 0 -28 days put on Continous Positive Airway Pressure(CPAP)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No of neonates 0 -28 days put on Continous Positive Airway Pressure(CPAP)");

        return cd;
    }
    /**
     * No.of babies given tetracycline at birth
     * @return
     */
    public CohortDefinition givenTetracyclineAtBirth(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.teo_given=84893;";
        cd.setName("No.of babies given tetracycline at birth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of babies given tetracycline at birth");

        return cd;
    }
    /**
     * Pre-Term babies
     * @return
     */
    public CohortDefinition preTermBabies(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.duration_of_pregnancy < 32;";
        cd.setName("Pre-Term babies");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pre-Term babies");

        return cd;
    }

    /**
     * No.of babies discharged alive
     * @return
     */
    public CohortDefinition dischargedAlive(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select ds.patient_id from kenyaemr_etl.etl_mchs_discharge ds where ds.visit_date between date(:startDate) and date(:endDate) and ds.baby_status=163016;";
        cd.setName("No.of babies discharged alive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of babies discharged alive");

        return cd;
    }
    /**
     * No.of Infants intiatied on breastfeeding within 1 hour after birth
     * @return
     */
    public CohortDefinition initiatedBFWithinOneHour(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.bf_within_one_hour=1065;";
        cd.setName("No.of babies discharged alive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of babies discharged alive");

        return cd;
    }

    /**
     * Deliveries within period
     * @return
     */
    public CohortDefinition deliveriesWithinPeriod(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="Select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where\n" +
                "coalesce(date(d.date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate);";
        cd.setName("Deliveries within period");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Deliveries within period");

        return cd;
    }
    /**
     * Total Deliveries from HIV+ mother
     * @return
     */
    public CohortDefinition deliveryFromHIVPosMother(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("enrolledInHIV", ReportUtils.map(enrolledInHIV(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("htsPositive", ReportUtils.map(htsPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("labourAndDeliveryPositive", ReportUtils.map(labourAndDeliveryPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("mchPositive", ReportUtils.map(mchPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deliveriesWithinPeriod", ReportUtils.map(deliveriesWithinPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(latestMCHEnrollment AND deliveriesWithinPeriod) AND (enrolledInHIV OR htsPositive OR labourAndDeliveryPositive OR mchPositive)");
        return cd;
    }
    /**
     * Perinatal Deaths - Fresh still birth
     */
    public CohortDefinition perinatalFreshStillBirth(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=159916;";
        cd.setName("Perinatal Deaths - Fresh still birth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Perinatal Deaths - Fresh still birth");

        return cd;
    }
    /**
     * Perinatal Deaths - Macerated still birth
     * @return
     */
    public CohortDefinition perinatalMaceratedStillBirth(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.baby_condition=135436;";
        cd.setName("Perinatal Deaths - Macerated still birth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Perinatal Deaths - Macerated still birth");

        return cd;
    }
    /**
     * Perinatal Deaths - Death 0-28 days
     * @return
     */
    public CohortDefinition perinatalDeathWithin0To28Days(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select a.patient_id from\n" +
                "                         (select d.patient_id,\n" +
                "       coalesce(mid(max(concat(date(d.visit_date), date(d.date_of_delivery))), 11), max(d.visit_date)) as delivery_date,\n" +
                "       mid(max(concat(date(d.visit_date), d.baby_condition)), 11) as baby_birth_condition,discharge_date,\n" +
                "       baby_status_at_discharge,pnc_perinatal_death_recorded,baby_status_at_pnc,pnc_delivery_date,pv.patient_id as baby_died_pnc\n" +
                "from kenyaemr_etl.etl_mchs_delivery d\n" +
                "         inner join (select ds.patient_id,\n" +
                "                            coalesce(mid(max(concat(date(ds.visit_date), date(ds.discharge_date))), 11),max(ds.visit_date)) as discharge_date,\n" +
                "                            mid(max(concat(date(ds.visit_date), ds.baby_status)), 11) as baby_status_at_discharge\n" +
                "                     from kenyaemr_etl.etl_mchs_discharge ds group by ds.patient_id)ds on d.patient_id = ds.patient_id\n" +
                "left join (select pv.patient_id,\n" +
                "                  min(pv.visit_date) as pnc_perinatal_death_recorded,\n" +
                "                  mid(min(concat(date(pv.visit_date), pv.condition_of_baby)), 11) as baby_status_at_pnc,\n" +
                "                  mid(min(concat(date(pv.visit_date), pv.delivery_date)), 11) as pnc_delivery_date\n" +
                "           from kenyaemr_etl.etl_mch_postnatal_visit pv where pv.condition_of_baby = 160034\n" +
                "group by pv.patient_id)pv on d.patient_id = pv.patient_id\n" +
                " where (d.visit_date between date(:startDate) and date(:endDate) or pnc_delivery_date between date(:startDate) and date(:endDate))\n" +
                "group by d.patient_id\n" +
                "having (discharge_date >= delivery_date and baby_birth_condition = 151849\n" +
                "   and ds.baby_status_at_discharge = 160432) or baby_died_pnc is not null\n" +
                "and (timestampdiff(DAY,delivery_date,discharge_date) between 0 and 7\n" +
                "         or timestampdiff(DAY,pnc_delivery_date,pnc_perinatal_death_recorded) between 0 and 28))a;";
        cd.setName("Perinatal Deaths - Death 0-28 days");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Perinatal Deaths - Death 0-28 days");

        return cd;
    }
    /**
     *Perinatal Deaths - Death 0-7 days
     * @return
     */
    public CohortDefinition perinatalDeathWithin0To7Days(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select a.patient_id from\n" +
                "                         (select d.patient_id,\n" +
                "       coalesce(mid(max(concat(date(d.visit_date), date(d.date_of_delivery))), 11), max(d.visit_date)) as delivery_date,\n" +
                "       mid(max(concat(date(d.visit_date), d.baby_condition)), 11) as baby_birth_condition,discharge_date,\n" +
                "       baby_status_at_discharge,pnc_perinatal_death_recorded,baby_status_at_pnc,pnc_delivery_date,pv.patient_id as baby_died_pnc\n" +
                "from kenyaemr_etl.etl_mchs_delivery d\n" +
                "         inner join (select ds.patient_id,\n" +
                "                            coalesce(mid(max(concat(date(ds.visit_date), date(ds.discharge_date))), 11),max(ds.visit_date)) as discharge_date,\n" +
                "                            mid(max(concat(date(ds.visit_date), ds.baby_status)), 11) as baby_status_at_discharge\n" +
                "                     from kenyaemr_etl.etl_mchs_discharge ds group by ds.patient_id)ds on d.patient_id = ds.patient_id\n" +
                "left join (select pv.patient_id,\n" +
                "                  min(pv.visit_date) as pnc_perinatal_death_recorded,\n" +
                "                  mid(min(concat(date(pv.visit_date), pv.condition_of_baby)), 11) as baby_status_at_pnc,\n" +
                "                  mid(min(concat(date(pv.visit_date), pv.delivery_date)), 11) as pnc_delivery_date\n" +
                "           from kenyaemr_etl.etl_mch_postnatal_visit pv where pv.condition_of_baby = 160034\n" +
                "group by pv.patient_id)pv on d.patient_id = pv.patient_id\n" +
                " where (d.visit_date between date(:startDate) and date(:endDate) or pnc_delivery_date between date(:startDate) and date(:endDate))\n" +
                "group by d.patient_id\n" +
                "having (discharge_date >= delivery_date and baby_birth_condition = 151849\n" +
                "   and ds.baby_status_at_discharge = 160432) or baby_died_pnc is not null\n" +
                "and (timestampdiff(DAY,delivery_date,discharge_date) between 0 and 7\n" +
                "         or timestampdiff(DAY,pnc_delivery_date,pnc_perinatal_death_recorded) between 0 and 7))a;";
        cd.setName("Perinatal Deaths - Death 0-7 days");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Perinatal Deaths - Death 0-7 days");

        return cd;
    }
    /**
     * Maternal death during delivery
     * @return
     */
    public CohortDefinition maternalDeathDuringDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate)\n" +
                "and d.condition_of_mother = 134612;";
        cd.setName("Maternal death during delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Maternal death during delivery");

        return cd;
    }
    /**
     * Maternal death at MCH
     * @return
     */
    public CohortDefinition maternalDeathAtMCH(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select ds.patient_id from kenyaemr_etl.etl_patient_program_discontinuation ds where ds.visit_date between date(:startDate) and date(:endDate) " +
                "and ds.program_name='MCH Mother' and ds.discontinuation_reason=160034;";
        cd.setName("Maternal death at MCH");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Maternal death at MCH");

        return cd;
    }
    /**
     * Maternal deaths
     * @return
     */
    public CohortDefinition maternalDeath(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("maternalDeathDuringDelivery", ReportUtils.map(maternalDeathDuringDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("maternalDeathAtMCH", ReportUtils.map(maternalDeathAtMCH(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollment AND (maternalDeathDuringDelivery OR maternalDeathAtMCH)");
        return cd;
    }
    /**
     * Maternal deaths audited within 7 days
     * @return
     */
    public CohortDefinition maternalDeathAuditedWithin7Days(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate)\n" +
                "      and d.condition_of_mother = 134612 and d.maternal_death_audited = 1065;";
        cd.setName("Maternal deaths audited within 7 days");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Maternal deaths audited within 7 days");

        return cd;
    }
    /**
     * Ante Partum Haemorrhage(APH)
     * @param motherCondition
     * @return
     */
    public CohortDefinition antePartumHaemorrhage(Integer motherCondition){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=228 and condition_of_mother = "+motherCondition+";";
        cd.setName("Ante Partum Haemorrhage(APH)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Ante Partum Haemorrhage(APH)");

        return cd;
    }
    /**
     * Post Partum Haemorrhage(PPH)
     * @param motherCondition
     * @return
     */
    public CohortDefinition postPartumHaemorrhage(Integer motherCondition){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=230 and condition_of_mother = "+motherCondition+";";
        cd.setName("Post Partum Haemorrhage(PPH)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Post Partum Haemorrhage(PPH)");

        return cd;
    }
    /**
     * Eclampsia
     * @param motherCondition
     * @return
     */
    public CohortDefinition eclampsia(Integer motherCondition){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=118744 and condition_of_mother = "+motherCondition+";";
        cd.setName("Eclampsia");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Eclampsia");

        return cd;
    }
    /**
     * Ruptured Uterus
      * @param motherCondition
     * @return
     */
    public CohortDefinition rupturedUterus(Integer motherCondition){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=113195 and condition_of_mother = "+motherCondition+";";
        cd.setName("Ruptured Uterus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Ruptured Uterus");

        return cd;
    }
    /**
     *  Obstructed Labour
     * @param motherCondition
     * @return
     */
    public CohortDefinition obstructedLabour(Integer motherCondition){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=115036 and condition_of_mother = "+motherCondition+";";
        cd.setName("Obstructed Labour");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Obstructed Labour");

        return cd;
    }
    /**
     * Sepsis
     * @param motherCondition
     * @return
     */
    public CohortDefinition sepsis(Integer motherCondition){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where d.visit_date between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=130 and condition_of_mother = "+motherCondition+";";
        cd.setName("Sepsis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Sepsis");

        return cd;
    }

}