/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.pmtct;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 1/36/23
 */

/**
 * Library of cohort definitions for PNC report
 */
@Component
public class PNCCohortLibrary {

    public  CohortDefinition pncClients() {
        String sqlQuery="SELECT pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = pv.patient_id where e.date_of_discontinuation is null and e.visit_date <= pv.visit_date and date(pv.visit_date) BETWEEN date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PNC clients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PNC clients within period");
        return cd;
    }
    public  CohortDefinition newPNCClients() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC clients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC clients within period");
        return cd;
    }
    public  CohortDefinition revisitsPNC() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no > 1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PNC revisit clients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PNC revisit clients within period");
        return cd;
    }
    public  CohortDefinition pncMotherNewVisitWithin48hrs() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 1721;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC visit for mothers within 48 hrs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC visit for mothers within 48 hrs");
        return cd;
    }
    public  CohortDefinition pncMotherNewVisitBtwn3DaysUnder6Weeks() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 1722;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC visit for mothers between 3 days and under 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC visit for mothers between 3 days and under 6 weeks");
        return cd;
    }
    public  CohortDefinition pncMotherNewVisitAfter6Weeks() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 1723;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC visit for mothers after 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC visit for mothers after 6 weeks");
        return cd;
    }
    public  CohortDefinition pncBabyNewVisitWithin48hrs() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1 and v.visit_timing_baby = 167012;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC visit for babies within 48 hrs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC visit for babies within 48 hrs");
        return cd;
    }
    public  CohortDefinition pncBabyNewVisitBtwn3DaysUnder6Weeks() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 167013;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC visit for babies between 3 days and under 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC visit for babies between 3 days and under 6 weeks");
        return cd;
    }
    public  CohortDefinition pncBabyNewVisitAfter6Weeks() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 167015;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New PNC visit for mothers after 6 weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New PNC visit for mothers after 6 weeks");
        return cd;
    }

    public  CohortDefinition initialTestsAtPNC() {
        String sqlQuery="select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join\n" +
                "     (select v.patient_id\n" +
                "      from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "      where v.test_1_result is not null\n" +
                "        and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "     on e.patient_id = v.patient_id\n" +
                "         left join\n" +
                "     (select t.patient_id\n" +
                "      from kenyaemr_etl.etl_hts_test t\n" +
                "      where t.test_type = 1\n" +
                "        and t.hts_entry_point = 1623\n" +
                "        and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "where v.patient_id is not null\n" +
                "   or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Initial test at PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial test at PNC");
        return cd;
    }

    public  CohortDefinition hivPositiveResultAtPNC() {
        String sqlQuery="select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join\n" +
                "     (select v.patient_id\n" +
                "      from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "      where v.final_test_result ='Positive'\n" +
                "        and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "     on e.patient_id = v.patient_id\n" +
                "         left join\n" +
                "     (select t.patient_id\n" +
                "      from kenyaemr_etl.etl_hts_test t\n" +
                "      where t.test_type = 1\n" +
                "        and t.hts_entry_point = 1623\n" +
                "        and t.final_test_result = 'Positive'\n" +
                "        and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "where v.patient_id is not null\n" +
                "   or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV positive result at PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV positive result at PNC");
        return cd;
    }

    public  CohortDefinition partnerTestedAtPNC() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v where v.partner_hiv_tested = 1065 and date(v.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Partners tested at PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Partners tested at PNC");
        return cd;
    }
    public  CohortDefinition startedHAARTAtPNC() {
        String sqlQuery="select v.patient_id\n" +
                "      from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "      where v.mother_haart_given = 1065\n" +
                "        and date(v.visit_date) between date(:startDate) and date(:endDate)";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Adolescents started HAART at PNC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents started HAART at PNC");
        return cd;
    }
    public  CohortDefinition infantARVProphylaxis() {
        String sqlQuery="select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where (v.baby_azt_dispensed = 160123 or v.baby_nvp_dispensed = 80586)\n" +
                "  and date(v.visit_date between date(:startDate) and date(:endDate));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Infant ARV Prophylaxis given");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis given");
        return cd;
    }
    public  CohortDefinition cacxPAP() {
        String sqlQuery="select v.patient_id from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where v.cacx_screening_method = 885 and date(v.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Screened for Cervical Cancer (PAP)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for Cervical Cancer (PAP)");
        return cd;
    }
    public  CohortDefinition cacxVIA() {
        String sqlQuery="select v.patient_id from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where v.cacx_screening_method = 162816 and date(v.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Screened for Cervical Cancer (VIA)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for Cervical Cancer (VIA)");
        return cd;
    }
    public  CohortDefinition cacxVILI() {
        String sqlQuery="select v.patient_id from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where v.cacx_screening_method = 164977 and date(v.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Screened for Cervical Cancer (VILI)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for Cervical Cancer (VILI)");
        return cd;
    }
    public  CohortDefinition cacxHPV() {
        String sqlQuery="select v.patient_id from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where v.cacx_screening_method = 159859 and date(v.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Screened for Cervical Cancer (HPV)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for Cervical Cancer (HPV)");
        return cd;
    }
    public  CohortDefinition receivedFPMethod() {
        String sqlQuery="select v.patient_id from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where v.family_planning_method in (160570,780,5279,1359,5275,136163,5278,5277,1472,190,1489)\n" +
                "  and date(v.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Received FP method");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Received FP method");
        return cd;
    }
    public  CohortDefinition hivNegativeTest1() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_1_result ='Negative'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_1_result = 'Negative'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV Negative Test-1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Negative Test-1");
        return cd;
    }
    public  CohortDefinition hivPositiveTest1() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_1_result ='Positive'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_1_result = 'Positive'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV Positive Test-1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Test-1");
        return cd;
    }
    public  CohortDefinition hivInvalidTest1() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_1_result ='Invalid'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_1_result = 'Invalid'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV Invalid Test-1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV invalid Test-1");
        return cd;
    }
    public  CohortDefinition hivWastedTest1() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_1_result ='Inconclusive'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_1_result = 'Inconclusive'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV wasted Test-1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV wasted Test-1");
        return cd;
    }
    public  CohortDefinition hivNegativeTest2() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_2_result ='Negative'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_2_result = 'Negative'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV Negative Test-2");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Negative Test-2");
        return cd;
    }
    public  CohortDefinition hivPositiveTest2() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_2_result ='Positive'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_2_result = 'Positive'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV Positive Test-2");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Test-2");
        return cd;
    }
    public  CohortDefinition hivInvalidTest2() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_2_result ='Invalid'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_2_result = 'Invalid'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV Invalid Test-2");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV invalid Test-2");
        return cd;
    }
    public  CohortDefinition hivWastedTest2() {
        String sqlQuery="select e.patient_id\n" +
                "             from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                      left join\n" +
                "                  (select v.patient_id\n" +
                "                   from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                   where v.test_2_result ='Inconclusive'\n" +
                "                     and date(v.visit_date between date(:startDate) and date(:endDate))) v\n" +
                "                  on e.patient_id = v.patient_id\n" +
                "                      left join\n" +
                "                  (select t.patient_id\n" +
                "                   from kenyaemr_etl.etl_hts_test t\n" +
                "                   where t.test_type = 1\n" +
                "                     and t.hts_entry_point = 1623\n" +
                "                     and t.test_2_result = 'Inconclusive'\n" +
                "                     and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "             where v.patient_id is not null\n" +
                "                or t.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV wasted Test-2");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV wasted Test-2");
        return cd;
    }
}
