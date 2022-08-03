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
     * No. of revisiting ANC clients
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
     * No. of clients tested Positive for Syphillis at HTS
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
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_mch_antenatal_visit av on e.patient_id = av.patient_id\n" +
                "         left join (select d.patient_id,\n" +
                "                           max(visit_date) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'MCH Mother'\n" +
                "                    group by d.patient_id\n" +
                ") d on e.patient_id = d.patient_id\n" +
                "where (date(e.visit_date) > date(d.latest_disc)\n" +
                "    or d.patient_id is null)\n" +
                "  and av.visit_date between date(:startDate) and date(:endDate)\n" +
                "  and e.lmp is not null\n" +
                "  and av.anc_visit_number = 1\n" +
                "  and FLOOR(DATEDIFF(date(e.visit_date), date(e.lmp)) / 7) <= 12;";
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)");
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
}