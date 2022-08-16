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
            "where e.service_type = 1622 and date(e.visit_date) <= date(:endDate) and (date(e.visit_date) > date(d.latest_disc) or d.patient_id is null);";
    cd.setName("Latest MCH enrollment at ANC");
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
                "where e.service_type = 1623 and date(e.visit_date) <= date(:endDate) and (date(e.visit_date) > date(d.latest_disc) or d.patient_id is null);";
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
     *No.of Clients given IPT (1st dose) SQL
     */
    public CohortDefinition noOfANCClientsGivenIPT1stDoseSQL() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where date(p.malaria_prophylaxis_1) <= date(:endDate)\n" +
                "                        and date(p.malaria_prophylaxis_1) between date(:startDate) and date(:endDate);";
        cd.setName("No.of Clients given IPT (1st dose)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients given IPT (1st dose)");
        return cd;
    }

    /**
     * No.of Clients given IPT (1st dose)
     * @return
     */
    public CohortDefinition noOfANCClientsGivenIPT1stDose() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("noOfANCClientsGivenIPT1stDoseSQL", ReportUtils.map(noOfANCClientsGivenIPT1stDoseSQL(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND noOfANCClientsGivenIPT1stDoseSQL");
        return cd;
    }
    /**
     *No.of Clients given IPT (2nd dose) SQL
     */
    public CohortDefinition noOfANCClientsGivenIPT2ndDoseSQL() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where date(p.malaria_prophylaxis_2) <= date(:endDate)\n" +
                "        and date(p.malaria_prophylaxis_2) between date(:startDate) and date(:endDate);";
        cd.setName("No.of Clients given IPT (2nd dose)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients given IPT (2nd dose)");
        return cd;
    }
    /**
     * No.of Clients given IPT (2nd dose)
     * @return
     */
    public CohortDefinition noOfANCClientsGivenIPT2ndDose() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("noOfANCClientsGivenIPT2ndDoseSQL", ReportUtils.map(noOfANCClientsGivenIPT2ndDoseSQL(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND noOfANCClientsGivenIPT2ndDoseSQL");
        return cd;
    }
    /**
     *No.of Clients given IPT (3rd dose) SQL
     */
    public CohortDefinition noOfANCClientsGivenIPT3rdDoseSQL() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_preventive_services p where date(p.malaria_prophylaxis_3) <= date(:endDate)\n" +
                "        and date(p.malaria_prophylaxis_3) between date(:startDate) and date(:endDate);";
        cd.setName("No.of Clients given IPT (3rd dose)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No.of Clients given IPT (3rd dose)");
        return cd;
    }

    /**
     * No.of Clients given IPT (3rd dose)
     * @return
     */
    public CohortDefinition noOfANCClientsGivenIPT3rdDose() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("noOfANCClientsGivenIPT3rdDoseSQL", ReportUtils.map(noOfANCClientsGivenIPT3rdDoseSQL(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC AND noOfANCClientsGivenIPT3rdDoseSQL");
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
        cd.addSearch("latestMCHEnrollmentAtANC", ReportUtils.map(latestMCHEnrollmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollmentAtANC and distributedLLINs");
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

    /**
     *No. of patients tested positive for VIA/VILI CACX screening Form
     * @return
     */
    public CohortDefinition viaViliPositiveCacxScreening(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('VIA','VILI') and s.screening_result in ('Positive') ;";
        cd.setName("No. of patients tested positive for VIA/VILI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No. of patients tested positive for VIA/VILI");

        return cd;
    }

    /**
     * No. of patients tested positive for VIA/VILI at ANC
     * @return
     */
    public CohortDefinition viaViliPositiveANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
        " and v.cacx_screening_method  in (162816,164977) and v.cacx_screening in (703);";
        cd.setName("No. of patients tested positive for VIA/VILI at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No. of patients tested positive for VIA/VILI at ANC");

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

    /**
     * Positive for CACX using HPV method using CACX screening form
     * @return
     */
    public CohortDefinition hpvPositiveCacxScreening(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method='HPV Test' and s.screening_result='Positive';";
        cd.setName("Positive for CACX using HPV method");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive for CACX using HPV method");

        return cd;
    }

    /**
     * Positive for CACX using HPV method at ANC
     * @return
     */
    public CohortDefinition hpvPositiveANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and v.cacx_screening_method =159895 and v.cacx_screening=703;";
        cd.setName("Positive for CACX using HPV method at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive for CACX using HPV method at ANC");

        return cd;
    }

    /**
     * Tested Positive for CACX using HPV method
     * @return
     */
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

    /**
     * Suspicious Cervical cancer lessions through screening form
     * @return
     */
    public CohortDefinition suspiciousCancerLessionsCACXScreening(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test') and s.screening_result ='Suspicious for cancer';";
        cd.setName("Suspicious Cervical cancer lessions through screening form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Suspicious Cervical cancer lessions through screening form");

        return cd;
    }

    /**
     * Suspicious Cervical cancer lessions at ANC
     * @return
     */
    public CohortDefinition suspiciousCancerLessionsANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and v.cacx_screening_method  in (885,162816,164977) and v.cacx_screening =159395;";
        cd.setName("Suspicious Cervical cancer lessions at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Suspicious Cervical cancer lessions at ANC");

        return cd;
    }

    /**
     * Suspicious Cervical cancer lessions
     * @return
     */
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

    /**
     * Treated for CACX uisng Cyrotherapy
     * @return
     */
    public CohortDefinition treatedUsingCyrotherapy(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test') and s.screening_result='Positive' and s.treatment_method in ('Cryotherapy performed (single Visit)','Cryotherapy performed','Cryotherapy postponed');";
        cd.setName("Treated for CACX uisng Cyrotherapy");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Treated for CACX uisng Cyrotherapy");

        return cd;
    }

    /**
     * Treated for CACX uisng LEEP
     * @return
     */
    public CohortDefinition treatedUsingLEEP(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and s.screening_method  in ('Pap Smear','VIA','VILI','Colposcopy','HPV Test') and s.screening_result='Positive' and s.treatment_method = 'LEEP performed';";
        cd.setName("Treated for CACX uisng LEEP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Treated for CACX uisng LEEP");

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
     * Mothers received PostParturm care within 48 hrs
     * @return
     */
    public CohortDefinition motherPPCWithin48hrs(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select p.patient_id from kenyaemr_etl.etl_mch_postnatal_visit p where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and timestampdiff(HOUR,date(p.delivery_date),date(p.visit_date)) <= 48 group by p.patient_id;";
        cd.setName("Mothers received PostParturm care within 48 hrs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers received PostParturm care within 48 hrs");

        return cd;
    }
    /**
     * Mothers received PostParturm care btw 3 days and 6 weeks
     * @return
     */
    public CohortDefinition motherPPCbtw3And42Days(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select p.patient_id from kenyaemr_etl.etl_mch_postnatal_visit p where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and timestampdiff(DAY,date(p.delivery_date),date(p.visit_date)) between 3 and 42 group by p.patient_id;";
        cd.setName("Mothers received PostParturm care btw 3 days and 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers received PostParturm care btw 3 days and 6 weeks");

        return cd;
    }
    /**
     * Mothers received PostParturm care after 6 weeks
     * @return
     */
    public CohortDefinition motherPPCAfter6weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select p.patient_id from kenyaemr_etl.etl_mch_postnatal_visit p where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and timestampdiff(WEEK,date(p.delivery_date),date(p.visit_date)) > 6 group by p.patient_id;";
        cd.setName("Mothers received PostParturm care after 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers received PostParturm care after 6 weeks");

        return cd;
    }
    /**
     * Babies received PostParturm care within 48 hrs
     * @return
     */
    public CohortDefinition babyPPCWithin48hrs(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_patient_demographics d inner join kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "on d.patient_id = f.patient_id inner join kenyaemr_etl.etl_hei_enrollment e on d.patient_id = e.patient_id where date(f.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and timestampdiff(HOUR,date(d.dob),date(f.visit_date)) <= 48;";
        cd.setName("Babies received PostParturm care within 48 hrs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Babies received PostParturm care within 48 hrs");

        return cd;
    }
    /**
     * Babies received PostParturm care btw 3 days and 6 weeks
     * @return
     */
    public CohortDefinition babyPPCbtw3And42Days(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_patient_demographics d inner join kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "on d.patient_id = f.patient_id inner join kenyaemr_etl.etl_hei_enrollment e on d.patient_id = e.patient_id where date(f.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                     and timestampdiff(DAY,date(d.dob),date(f.visit_date)) between 3 and 42;";
        cd.setName("Babies received PostParturm care btw 3 days and 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Babies received PostParturm care btw 3 days and 6 weeks");

        return cd;
    }
    /**
     * Babies received PostParturm care after 6 weeks
     * @return
     */
    public CohortDefinition babyPPCAfter6weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_patient_demographics d inner join kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "on d.patient_id = f.patient_id inner join kenyaemr_etl.etl_hei_enrollment e on d.patient_id = e.patient_id where date(f.visit_date) between date(:startDate) and date(:endDate)\n" +
                "     and timestampdiff(WEEK,date(d.dob),date(f.visit_date)) > 6;";
        cd.setName("Babies received PostParturm care after 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Babies received PostParturm care after 6 weeks");

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
    /**
     * Delivery method
     * @param mode
     * @return
     */
    public CohortDefinition deliveryMethod(Integer mode){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.mode_of_delivery = "+mode+";";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=151849;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.birth_weight < 2.5;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.birth_with_deformity=155871;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.teo_given=84893;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.duration_of_pregnancy < 32;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=151849 and d.bf_within_one_hour=1065;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=159916;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.baby_condition=135436;";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate)\n" +
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate)\n" +
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=228 and condition_of_mother = "+motherCondition+";";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=230 and condition_of_mother = "+motherCondition+";";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=118744 and condition_of_mother = "+motherCondition+";";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=113195 and condition_of_mother = "+motherCondition+";";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=115036 and condition_of_mother = "+motherCondition+";";
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
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where coalesce(date(date_of_delivery),date(d.visit_date)) between date(:startDate) and date(:endDate) and d.delivery_complications=1065 and d.coded_delivery_complications=130 and condition_of_mother = "+motherCondition+";";
        cd.setName("Sepsis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Sepsis");

        return cd;
    }

    /**
     * Normal Weight for Age
     * @return
     */
    public CohortDefinition normalWeightForAge(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.weight_category=1115;";
        cd.setName("Normal Weight for Age");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Normal Weight for Age");

        return cd;
    }
    /**
     * Underweight
     * @return
     */
    public CohortDefinition underWeight(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.weight_category= 123814;";
        cd.setName("Underweight");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Underweight");

        return cd;
    }
    /**
     * Severe Underweight
     * @return
     */
    public CohortDefinition severeUnderWeight(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.weight_category= 126598;";
        cd.setName("Severe Underweight");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Severe Underweight");

        return cd;
    }
    /**
     * Overweight
     * @return
     */
    public CohortDefinition overweight(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.weight_category=114413 ;";
        cd.setName("Overweight");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Overweight");

        return cd;
    }
    /**
     * Overweight
     * @return
     */
    public CohortDefinition obese(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.weight_category=115115;";
        cd.setName("Obese");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Obese");

        return cd;
    }
    /**
     * MUAC_Normal(Green)
     * @return
     */
    public CohortDefinition normalMUAC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.muac=160909;";
        cd.setName("MUAC_Normal(Green)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MUAC_Normal(Green)");

        return cd;
    }
    /**
     * MUAC_Moderate(Yellow)
     * @return
     */
    public CohortDefinition moderateMUAC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.muac=160910;";
        cd.setName("MUAC_Moderate(Yellow)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MUAC_Moderate(Yellow)");

        return cd;
    }
    /**
     * MUAC_Severe(Red)
     * @return
     */
    public CohortDefinition severeMUAC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.muac=127778;";
        cd.setName("MUAC_Severe(Red)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MUAC_Severe(Red)");

        return cd;
    }
    /**
     * Stunted
     * @return
     */
    public CohortDefinition stuntedGrowth(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.stunted=164085;";
        cd.setName("Stunted");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stunted");

        return cd;
    }
        /**
     * New_Enrollment
     * @return
     */
    public CohortDefinition newlyEnrolledMchs(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select hen.patient_id from kenyaemr_etl.etl_hei_enrollment hen where hen.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("New_Enrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New_Enrollment");

        return cd;
    }
    /**
     * Followup_type_Kwashiorkor
     * @return
     */
    public CohortDefinition kwashiorkor(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.followup_type=116474;";
        cd.setName("Followup_type_Kwashiorkor");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Followup_type_Kwashiorkor");

        return cd;
    }
    /**
     * Followup_type_Marasmus
     * @return
     */
    public CohortDefinition marasmus(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.followup_type = 132636;";
        cd.setName("Followup_type_Marasmus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Followup_type_Marasmus");

        return cd;
    }
    /**
     * Exclusive_breast_feeding
     * @return
     */
    public CohortDefinition exclusiveBreastFeeding(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.infant_feeding=5526;";
        cd.setName("Exclusive_breast_feeding");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive_breast_feeding");

        return cd;
    }
    /**
     * Dewormed
     * @return
     */
    public CohortDefinition dewormed(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.deworming_drug in(79413,70439);";
        cd.setName("Dewormed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Dewormed");

        return cd;
    }
    /**
     * MNPs_Supplementation
     * @return
     */
    public CohortDefinition mnpsSupplementation(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.MNPS_Supplementation=161649;";
        cd.setName("MNPs_Supplementation");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MNPs_Supplementation");

        return cd;
    }
    /**
     * Child Mortality
     * @return
     */
    public CohortDefinition childrenDiscontinuationReasonDied(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select ds.patient_id from kenyaemr_etl.etl_patient_program_discontinuation ds where ds.visit_date between date(:startDate) and date(:endDate) and ds.program_name=\"MCH Child HEI\" and ds.discontinuation_reason=160432 ;";
        cd.setName("Child Mortality");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Child Mortality");

        return cd;
    }
    /**
     * Children_With_Disability_Any_Form
     * @return
     */
    public CohortDefinition childrenWithDisability(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.disability=1065;";
        cd.setName("Children_With_Disability_Any_Form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Children_With_Disability_Any_Form");

        return cd;
    }
    /**
     * Children_with_delayed_developmental_milestones
     * @return
     */
    public CohortDefinition childrenWithDelayedDevelopmentalMilestones(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select he.patient_id from kenyaemr_etl.etl_hei_follow_up_visit he where he.visit_date between date(:startDate) and date(:endDate) and he.review_of_systems_developmental=6022;";
        cd.setName("Children_with_delayed_developmental_milestones");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Children_with_delayed_developmental_milestones");

        return cd;
    }
    /**
     * Total Number of people screened at ANC
     * @return
     */
    public CohortDefinition clientTbScreeningAtANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tb.patient_id from kenyaemr_etl.etl_mch_antenatal_visit tb where  tb.tb_screening  in (1660,142177,164128,1662) and tb.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("Total Number of people screened at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number of people screened at ANC");

        return cd;
    }
    /**
     * Total Number of people screened using TB form
     * @return
     */
    public CohortDefinition clientTbScreeningTBForm(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tbs.patient_id from kenyaemr_etl.etl_tb_screening tbs where tbs.visit_date between date(:startDate) and date(:endDate)\n" +
                "and tbs.resulting_tb_status  in (1660,142177,1662);";
        cd.setName("Total Number of people screened using TB form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number of people screened using TB form");

        return cd;
    }

    /**
     * Number of MCH mothers screened for TB
     * @return
     */
    public CohortDefinition clientTbScreening() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("hivFollowupEncounterWithinPeriod", ReportUtils.map(hivFollowupEncounterWithinPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientTbScreeningTBForm", ReportUtils.map(clientTbScreeningTBForm(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientTbScreeningAtANC", ReportUtils.map(clientTbScreeningAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollment AND (clientTbScreeningTBForm OR clientTbScreeningAtANC OR hivFollowupEncounterWithinPeriod)");
        return cd;
    }
    /**
     * Total Number of presumptive TB cases at ANC
     * @return
     */
    public CohortDefinition clientWithPresumptiveTbANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tb.patient_id from kenyaemr_etl.etl_mch_antenatal_visit tb where  tb.tb_screening  =142177 and tb.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("Total Number of presumptive TB cases at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number of presumptive TB cases at ANC");

        return cd;
    }
    /**
     * Total Number of presumptive TB cases (TB Form)
     * @return
     */
    public CohortDefinition clientWithPresumptiveTbTBForm(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tbs.patient_id from kenyaemr_etl.etl_tb_screening tbs where tbs.visit_date between date(:startDate) and date(:endDate)\n" +
                "       and tbs.resulting_tb_status =142177;";
        cd.setName("Total Number of presumptive TB cases (TB Form)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number of presumptive TB cases (TB Form)");

        return cd;
    }
    /**
     * Clients with presumptive TB during HIV followup
     * @return
     */
    public CohortDefinition clientWithPresumptiveTBHIVFollowup(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select f.patient_id from kenyaemr_etl.etl_patient_hiv_followup f where f.visit_date between date(:startDate) and date(:endDate)\n" +
                "and f.tb_status=142177;";
        cd.setName("Clients with presumptive TB during HIV followup");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with presumptive TB during HIV followup");

        return cd;
    }
    public CohortDefinition clientWithPresumptiveTb() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientWithPresumptiveTBHIVFollowup", ReportUtils.map(clientWithPresumptiveTBHIVFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientWithPresumptiveTbANC", ReportUtils.map(clientWithPresumptiveTbANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientWithPresumptiveTbTBForm", ReportUtils.map(clientWithPresumptiveTbTBForm(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollment AND (clientWithPresumptiveTbTBForm OR clientWithPresumptiveTbANC OR clientWithPresumptiveTBHIVFollowup)");
        return cd;
    }
    /**
     * Total Number already on TB treatment at ANC
     * @return
     */
    public CohortDefinition clientOnTbTreatmentAtANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tb.patient_id from kenyaemr_etl.etl_mch_antenatal_visit tb where  tb.tb_screening  =1662 and tb.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("Total Number already on TB treatment at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number already on TB treatment at ANC");

        return cd;
    }
    /**
     * Total Number already on TB treatment TB Form
     * @return
     */
    public CohortDefinition clientOnTbTreatmentTBForm(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tbs.patient_id from kenyaemr_etl.etl_tb_screening tbs where tbs.visit_date between date(:startDate) and date(:endDate)\n" +
                "       and tbs.resulting_tb_status  in (1660,142177,1662) and tbs.started_anti_TB=1065;";
        cd.setName("Total Number already on TB treatment TB Form");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number already on TB treatment TB Form");

        return cd;
    }

    /**
     * Total Number already on TB treatment (in HIV followup)
     * @return
     */
    public CohortDefinition clientOnTbTreatmentHIVFollowup(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select f.patient_id from kenyaemr_etl.etl_patient_hiv_followup f where f.visit_date between date(:startDate) and date(:endDate)\n" +
                "        and f.on_anti_tb_drugs=1065;";
        cd.setName("Total Number already on TB treatment (in HIV followup)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number already on TB treatment (in HIV followup)");

        return cd;
    }
    /**
     * Clients on TB Treatment
     * @return
     */
    public CohortDefinition clientOnTbTreatment() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("clientOnTbTreatmentAtANC", ReportUtils.map(clientOnTbTreatmentAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientOnTbTreatmentTBForm", ReportUtils.map(clientOnTbTreatmentTBForm(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientOnTbTreatmentHIVFollowup", ReportUtils.map(clientOnTbTreatmentHIVFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("latestMCHEnrollment AND (clientOnTbTreatmentAtANC OR clientOnTbTreatmentTBForm OR clientOnTbTreatmentHIVFollowup)");
        return cd;
    }
    /**
     * Total Number of people not screened (ANC)
     * @return
     */
    public CohortDefinition clientTbNotScreenedAtANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tb.patient_id from kenyaemr_etl.etl_mch_antenatal_visit tb where tb.tb_screening =160737 or tb.tb_screening is null and tb.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("Total Number of people not screened (ANC)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number of people not screened (ANC)");

        return cd;
    }

    /**
     * Clients with HIV followup encounter
     * @return
     */
    public CohortDefinition hivFollowupEncounterWithinPeriod(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select f.patient_id from kenyaemr_etl.etl_patient_hiv_followup f where date(f.visit_date) between date(:startDate) and date(:endDate);";
        cd.setName("Clients with HIV followup encounter");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription(" Clients with HIV followup encounter");

        return cd;
    }
    /**
     * Total Number of people not screened (TB Form)
     * @return
     */
    public CohortDefinition clientTbNotScreenedTBForm(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select tbs.patient_id from kenyaemr_etl.etl_tb_screening tbs where tbs.visit_date between date(:startDate) and date(:endDate)\n" +
                "                      and tbs.resulting_tb_status=160737 or tbs.resulting_tb_status is null group by tbs.patient_id;";
        cd.setName("Total Number of people not screened (TB Form)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Number of people not screened (TB Form)");

        return cd;
    }
    /**
     * Clients not screened for TB
     * @return
     */
    public CohortDefinition clientsNotScreenedForTB() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("hivFollowupEncounterWithinPeriod", ReportUtils.map(hivFollowupEncounterWithinPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientTbNotScreenedAtANC", ReportUtils.map(clientTbNotScreenedAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientTbNotScreenedTBForm", ReportUtils.map(clientTbNotScreenedTBForm(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("latestMCHEnrollment", ReportUtils.map(latestMCHEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(latestMCHEnrollment AND (clientTbNotScreenedTBForm OR clientTbNotScreenedAtANC)) AND NOT hivFollowupEncounterWithinPeriod");
        return cd;
    }
}