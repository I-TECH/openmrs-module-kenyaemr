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
public class PMTCTCohortLibrary {

    //ANC COHORTS
    /**
     * New ANC Clients
     *
     * @return
     */
    public CohortDefinition newClientsANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "     where v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancNewClients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients registered within the reporting period");
        return cd;
    }

    /**
     * Revisits ANC Clients
     *
     * @return
     */
    public CohortDefinition revisitClientsANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id\n" +
                "      from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "      where v.visit_date between date(:startDate) AND date(:endDate)\n" +
                "      having count(v.visit_date) > 1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancClientsRevisits");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients with revisits within the reporting period");
        return cd;
    }
    /**
     * Completed 4 ANC visits
     *
     * @return
     */
    public CohortDefinition completed4AntenatalVisitsANCCohortDefinition() {
        String sqlQuery = "select  DISTINCT v.patient_id\n" +
                "         from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "           inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "           where v.visit_date between date(:startDate) AND date(:endDate)\n" +
                "          having count(v.visit_date) >= 4;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancClientsCompletedVisits");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients with 4 visits within the reporting period");
        return cd;
    }
    /**
     * ANC tested Syphylis
     *
     * @return
     */
    public CohortDefinition testedSyphilisANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id\n" +
                "     from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "     where (v.syphilis_test_status is not null or v.syphilis_test_status !=1402) and\n" +
                "          v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancClientsTestedSyphilis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients tested Syphilis within the reporting period");
        return cd;
    }
    /**
     * ANC tested Syphilis positive
     *
     * @return
     */
    public CohortDefinition syphilisPositiveANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id\n" +
                "          from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "            inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "             where v.syphilis_test_status =1228 and\n" +
                "                   v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancClientsTestedSyphilisPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients tested Positive Syphilis within the reporting period");
        return cd;
    }
    /**
     * ANC tested Syphilis treated
     *
     * @return
     */
    public CohortDefinition syphilisTreatedANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id\n" +
                "        from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "          inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "         where v.syphilis_treated_status =1065 and\n" +
                "               v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancClientsTestedSyphilisTreated");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients treated Syphilis within the reporting period");
        return cd;
    }
    /**
     * ANC  Known positive
     *
     * @return
     */
    public CohortDefinition knownPositivesFirstANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id\n" +
                "      from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "            where e.hiv_status=703 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ancClientsKnownPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANC clients known positive within the reporting period");
        return cd;
    }

    /**
     * Initial test at ANC
     *
     * @return
     */
    public CohortDefinition initialTestANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= v.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=v.patient_id\n" +
                "where v.visit_date between date(:startDate) AND date(:endDate) and\n" +
                "      e.hiv_status !=703 and\n" +
                "      ld.final_test_result is null and\n" +
                "       p.final_test_result is null and\n" +
                "       v.final_test_result is not null ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("initialTestAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial test at ANC  within the reporting period");
        return cd;
    }
    /**
     * Positive test at ANC
     *
     * @return
     */
    public CohortDefinition positiveTestANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "          inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "          where e.hiv_status !=703 and v.final_test_result ='POSITIVE' and\n" +
                "                v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("positiveTestAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive test at ANC  within the reporting period");
        return cd;
    }

    /**
     * On ARV at ANC
     *
     * @return
     */
    public CohortDefinition onARVFirstANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        inner join kenyaemr_etl.etl_drug_event d on d.patient_id=v.patient_id\n" +
                "        where d.program = 'HIV' and d.date_started < e.visit_date and\n" +
                "              v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onARVAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ARV at ANC  within the reporting period");
        return cd;
    }

    /**
     * Started HAART at ANC
     *
     * @return
     */
    public CohortDefinition startedHAARTInANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "          inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "          inner join kenyaemr_etl.etl_drug_event d on v.patient_id=d.patient_id\n" +
                "           where d.date_started >= v.visit_date and\n" +
                "                 v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startedARVAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started ARV at ANC  within the reporting period");
        return cd;
    }

    /**
     * Given AZT for Baby at ANC
     *
     * @return
     */
    public CohortDefinition aztBabyANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "   where v.baby_azt_dispensed = 160123 and\n" +
                "         v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("givenAZTAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Given AZT for Baby at ANC  within the reporting period");
        return cd;
    }

    /**
     * Given NVP for Baby at ANC
     *
     * @return
     */
    public CohortDefinition nvpBabyANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "      where v.baby_nvp_dispensed = 80586 and\n" +
                "            v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("givenNVPAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Given AZT for Baby at NVP  within the reporting period");
        return cd;
    }

    /**
     * Screened TB at ANC
     *
     * @return
     */
    public CohortDefinition screenedTbANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        where (v.tb_screening is not null or v.tb_screening !=160737) and\n" +
                "              v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("screenedTbAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened TB at ANC within the reporting period");
        return cd;
    }

    /**
     * Screened Cacx pap at ANC
     *
     * @return
     */
    public CohortDefinition screenedCaCxPapANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "         where v.cacx_screening_method =885 and\n" +
                "              v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("screenedCacxPapAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened Cacx Pap at ANC within the reporting period");
        return cd;
    }

    /**
     * Screened Cacx via at ANC
     *
     * @return
     */
    public CohortDefinition screenedCaCxViaANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "         where v.cacx_screening_method =162816 and\n" +
                "              v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("screenedCacxViaAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened Cacx Via at ANC within the reporting period");
        return cd;
    }

    /**
     * Screened Cacx vili at ANC
     *
     * @return
     */
    public CohortDefinition screenedCaCxViliANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "         where v.cacx_screening_method =164977 and\n" +
                "              v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("screenedCacxViliAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened Cacx Vili at ANC within the reporting period");
        return cd;
    }

    /**
     * Givien IPT 1 at ANC
     *
     * @return
     */
    public CohortDefinition givenIPT1ANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "          where  v.IPT_dose_given_anc = 1 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("givenIPT1AtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Given IPT 1 at ANC within the reporting period");
        return cd;
    }

    /**
     * Givien IPT 2at ANC
     *
     * @return
     */
    public CohortDefinition givenIPT2ANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "          where  v.IPT_dose_given_anc = 2 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("givenIPT2AtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Given IPT 2 at ANC within the reporting period");
        return cd;
    }

    /**
     * Givien ITN at ANC
     *
     * @return
     */
    public CohortDefinition givenITNANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        where v.bed_nets ='Yes' and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("givenITNAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Given ITN at ANC within the reporting period");
        return cd;
    }

    /**
     * Partner tested at ANC
     *
     * @return
     */
    public CohortDefinition partnerTestedANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        where v.partner_hiv_tested =1065 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("partnerTestedAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Partner Tested at ANC within the reporting period");
        return cd;
    }

    /**
     * Partner known positive at ANC
     *
     * @return
     */
    public CohortDefinition partnerKnownPositiveANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        where e.partner_hiv_status=703 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("partnerKnownPositiveAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Partner Known Positive at ANC within the reporting period");
        return cd;
    }

    /**
     * Adolescents known positive 10-19 at ANC
     *
     * @return
     */
    public CohortDefinition adolescentsKnownPositive_10_19_AtANCCohortDefinition() {
        String sqlQuery = "select distinct e.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        inner join  kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "        where e.hiv_status =703 and timestampdiff(year,d.DOB,e.visit_date) between 10 and 19\n" +
                "              and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsKnownPositive_10_19_AtANCCohortDefinition");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents Known Positive 10-19 at ANC within the reporting period");
        return cd;
    }
    /**
     * Adolescents tested positive 10-19 at ANC
     *
     * @return
     */
    public CohortDefinition adolescentsTestedPositive_10_19_AtANCCohortDefinition() {
        String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = v.patient_id\n" +
                "        where timestampdiff(year,d.DOB,v.visit_date) BETWEEN 10 AND 19 and\n" +
                "        v.final_test_result = 'Positive' and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsTestedPositive_10_19_AtANCCohortDefinition");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents Tested Positive 10-19 at ANC within the reporting period");
        return cd;
    }
    /**
     * Adolescents started ART 10-19 at ANC
     *
     * @return
     */
    public CohortDefinition adolescentsStartedHaart_10_19_AtANCCohortDefinition() {
        String sqlQuery = "select\n" +
                "     distinct v.patient_id\n" +
                "     from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "       inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id\n" +
                "       inner join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id=e.patient_id\n" +
                "     left join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "     left join kenyaemr_etl.etl_mch_postnatal_visit pn on pn.patient_id=e.patient_id\n" +
                "     WHERE d.program = 'HIV' and timestampdiff(year,dm.DOB,e.visit_date) BETWEEN 10 AND 19\n" +
                "     and d.date_started >= e.visit_date and (d.date_started < ld.visit_date or d.date_started < pn.visit_date)\n" +
                "      and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsStartedART_10_19_AtANCCohortDefinition");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents Started ART 10-19 at ANC within the reporting period");
        return cd;
    }

    public  CohortDefinition infantsIntiatiedOnBreastfeeding() {
        String sqlQuery="select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.bf_within_one_hour=1065 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Client dead");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients dead");
        return cd;
    }




    // MATERNITY COHORTS

    public  CohortDefinition maternityClients() {
        String sqlQuery="SELECT pv.patient_id from kenyaemr_etl.etl_mchs_delivery pv inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = pv.patient_id where e.date_of_discontinuation is null and e.visit_date <= pv.visit_date and date(pv.visit_date) BETWEEN date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Maternity clients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Maternity clients within period");
        return cd;
    }

    public  CohortDefinition clientsWithAPH() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_mchs_delivery where coded_delivery_complications =228 and date(visit_date)\n" +
                "between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Clients with APH");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with APH");
        return cd;
    }
    public  CohortDefinition clientsWithPPH() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_mchs_delivery where coded_delivery_complications =230 and date(visit_date)\n" +
                "between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Clients with PPH");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with PPH");
        return cd;
    }
    public  CohortDefinition clientsWithEclampsia() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_mchs_delivery where coded_delivery_complications =118744 and visit_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Clients with Eclampsia");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with Eclampsia");
        return cd;
    }

    public  CohortDefinition clientsWithRapturedUterus() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_mchs_delivery where coded_delivery_complications =113195 and date(visit_date)\n" +
                "between date(:startDate) and date(:endDate)";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Clients with raptured uterus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with raptured uterus");
        return cd;
    }

    public  CohortDefinition clientsWithObstructedLabour() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_mchs_delivery where coded_delivery_complications =115036 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Clients with obstructed labour");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with obstructed labour");
        return cd;
    }

    public  CohortDefinition clientsWithSepsis() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_mchs_delivery where coded_delivery_complications =130 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Clients with sepsis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with sepsis");
        return cd;
    }

    public  CohortDefinition clientsAlive() {
        String sqlQuery="select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.condition_of_mother=160429 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Client Alive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients Alive");
        return cd;
    }

    public  CohortDefinition clientsDead() {
        String sqlQuery="select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.condition_of_mother=134612 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Client dead");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients dead");
        return cd;
    }

       public CohortDefinition preTermBabies() {
        String sqlQuery="select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.duration_of_pregnancy < 37 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Pre-term baby");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pre-term baby");
        return cd;
    }

    public  CohortDefinition underWeightBabies() {
        String sqlQuery="select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.birth_weight >2.5 and date(visit_date)\n" +
                "    between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Under weight baby");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Under weight baby");
        return cd;
    }

    public CohortDefinition liveBirths() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.baby_condition in (151849,164815,164816) and date(visit_date)\n" +
                "        between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Under weight baby");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Under weight baby");
        return cd;
    }

    public CohortDefinition stillBirths() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.baby_condition in (135436,159916,125872) and date(visit_date)\n" +
                "        between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Still birth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Still birth");
        return cd;
    }

    public CohortDefinition initialTestAtMaternity() {
        String sqlQuery = "select ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "         left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = ld.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = ld.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id = ld.patient_id\n" +
                "where e.hiv_status != 703\n" +
                "  and v.final_test_result is null\n" +
                "  and p.final_test_result is null\n" +
                "  and ld.final_test_result is not null\n" +
                "and ld.visit_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Initial test at Maternity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial test at Maternity");
        return cd;
    }

    public CohortDefinition positiveResultsAtMaternity() {
        String sqlQuery = "select ld.patient_id\n" +
                "                              from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "                               left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=ld.patient_id\n" +
                "                               left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=ld.patient_id\n" +
                "                              where e.hiv_status !=703 and\n" +
                "                                    (v.final_test_result is null or v.final_test_result !='Positive') and\n" +
                "                                     ld.final_test_result ='Positive'\n" +
                "and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Positive results at maternity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive results at maternity");
        return cd;
    }

    public CohortDefinition hivPositiveDeliveries() {
        String sqlQuery = "select ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "         left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = ld.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = ld.patient_id\n" +
                "where ld.final_test_result = 'Positive'\n" +
                "   or hiv_status = 703\n" +
                "   or v.final_test_result = 'Positive'\n" +
                "and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HIV+ deliveries");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV positive deliveries");
        return cd;
    }

    public CohortDefinition adolescentsNewHivPositiveAtMaternity() {
        String sqlQuery = "select ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "         INNER JOIN kenyaemr_etl.etl_patient_demographics d ON\n" +
                "    d.patient_id = ld.patient_id\n" +
                "WHERE timestampdiff(year, d.DOB, ld.visit_date) BETWEEN 10 AND 19\n" +
                "          and ld.final_test_result = 'Positive' and date(ld.visit_date) between date(:startDate)\n" +
                "          and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("New HIV+ adolescents at maternity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New HIV+ adolescents at maternity");
        return cd;
    }

    public CohortDefinition startedHAARTMaternity() {
        String sqlQuery = "select ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "         inner join kenyaemr_etl.etl_drug_event d on d.patient_id = ld.patient_id\n" +
                "         left join kenyaemr_etl.etl_mch_postnatal_visit pnc on pnc.patient_id = ld.patient_id\n" +
                "where d.program = 'HIV'\n" +
                "  and d.date_started >= ld.visit_date\n" +
                "  and d.date_started < pnc.visit_date\n" +
                "  and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Started HAART at maternity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started HAART at maternity");
        return cd;
    }

    public CohortDefinition infantARVProphylaxisMaternity() {
        String sqlQuery = "select ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "         left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = ld.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id = ld.patient_id\n" +
                "where (ld.baby_nvp_dispensed = 160123 or ld.baby_azt_dispensed = 160123)\n" +
                "  and (p.baby_nvp_dispensed != 160123 or p.baby_azt_dispensed != 160123)\n" +
                "  and (v.baby_nvp_dispensed != 160123 or v.baby_azt_dispensed != 160123)\n" +
                "and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Infant ARV prophylaxis at maternity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV prophylaxis at maternity");
        return cd;
    }

    public CohortDefinition normalDeliveries() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.mode_of_delivery =1170 and date(ld.visit_date) between date(:startDate) and date(:endDate); ";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Normal delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Normal delivery");
        return cd;
    }

    public CohortDefinition caesareanSections() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.mode_of_delivery =1171 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("C-Section delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("C-Section delivery");
        return cd;
    }

    public CohortDefinition breechDeliveries() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.mode_of_delivery =1172 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Breech delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breech delivery");
        return cd;
    }

    public CohortDefinition assistedVaginalDeliveries() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.mode_of_delivery in (118159,159260) and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Assisted vaginal delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Assisted vaginal delivery");
        return cd;
    }
    public CohortDefinition uterotonicGiven() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.uterotonic_given in(81369,104590) and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Uterotonic given");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Uterotonic given");
        return cd;
    }
    public CohortDefinition carbetocin() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.uterotonic_given =104590 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Cabertocin given");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cabertocin given");
        return cd;
    }
    public CohortDefinition oxytocin() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.uterotonic_given =81369 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("oxytocin given");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("oxytocin given");
        return cd;
    }
    public CohortDefinition deformity() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.birth_with_deformity = 164122 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Deformity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Deformity");
        return cd;
    }

    public CohortDefinition lowApgar() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.apgar_score_1min = 159603 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Low Apgar");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Low Apgar");
        return cd;
    }
    public CohortDefinition deathAudited() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.maternal_death_audited = 1602 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Maternal death audited");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Maternal death audited");
        return cd;
    }
    public CohortDefinition appliedChlorhexidine() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.chlohexidine_applied_on_code_stump = 159369 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Babies applied chlorhexidine for cord care");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Babies applied chlorhexidine for cord care");
        return cd;
    }
    public CohortDefinition givenTetracycline() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.teo_given in(84893,1) and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Babies given tetracycline at birth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Babies given tetracycline at birth");
        return cd;
    }
    public CohortDefinition vitaminK() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.vitamin_K_given = 984 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Vitamin K given");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Vitamin K given");
        return cd;
    }
    public CohortDefinition maceratedStillbirth() {
        String sqlQuery = "select ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld where ld.baby_condition =135436 and date(ld.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Macerated Stillbirth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Macerated Stillbirth");
        return cd;
    }
    public CohortDefinition deaths10to14Years() {
        String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mchs_delivery v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = v.patient_id\n" +
                "        where timestampdiff(year,d.DOB,v.visit_date) BETWEEN 10 AND 14 and\n" +
                "        v.condition_of_mother = 134612 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsTestedPositive_10_19_AtANCCohortDefinition");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents Tested Positive 10-19 at ANC within the reporting period");
        return cd;
    }
    public CohortDefinition deaths15to19Years() {
        String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mchs_delivery v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = v.patient_id\n" +
                "        where timestampdiff(year,d.DOB,v.visit_date) BETWEEN 15 AND 19 and\n" +
                "        v.condition_of_mother = 134612 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsTestedPositive_10_19_AtANCCohortDefinition");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents Tested Positive 10-19 at ANC within the reporting period");
        return cd;
    }

    public CohortDefinition deaths20toplus() {
        String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mchs_delivery v\n" +
                "        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = v.patient_id\n" +
                "        where timestampdiff(year,d.DOB,v.visit_date) >= 20 and\n" +
                "        v.condition_of_mother = 134612 and v.visit_date between date(:startDate) AND date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Maternal deaths 20 years plus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Maternal deaths 20 years plus");
        return cd;
    }
    //PNC COHORTS
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
                "where v.pnc_visit_no = 1 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no > 1 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 1721 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 1722 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 1723 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no = 1 and v.visit_timing_baby = 167012 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 167013 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "where v.pnc_visit_no = 1 and v.visit_timing_mother = 167015 and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "        and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
                "     on e.patient_id = v.patient_id\n" +
                "         left join\n" +
                "     (select t.patient_id\n" +
                "      from kenyaemr_etl.etl_hts_test t\n" +
                "      where t.test_type = 1\n" +
                "        and t.hts_entry_point = 1623\n" +
                "        and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "where (v.patient_id is not null\n" +
                "   or t.patient_id is not null);";
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
                "        and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
                "     on e.patient_id = v.patient_id\n" +
                "         left join\n" +
                "     (select t.patient_id\n" +
                "      from kenyaemr_etl.etl_hts_test t\n" +
                "      where t.test_type = 1\n" +
                "        and t.hts_entry_point = 1623\n" +
                "        and t.final_test_result = 'Positive'\n" +
                "        and date(t.visit_date) between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "where (v.patient_id is not null\n" +
                "   or t.patient_id is not null);";
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
                "  and date(v.visit_date) between date(:startDate) and date(:endDate);";
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
                "                     and date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
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
