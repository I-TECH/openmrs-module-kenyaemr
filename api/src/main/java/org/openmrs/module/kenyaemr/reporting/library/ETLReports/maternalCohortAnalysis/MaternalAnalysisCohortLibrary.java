/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.maternalCohortAnalysis;

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
 * Library of cohort definitions for maternal cohort analysis
 */
@Component
public class MaternalAnalysisCohortLibrary {

    @Autowired
    private DatimCohortLibrary datimCohortLibrary;
    /**
     * FIRST REVIEW: 3 Months Cohort
     */

    /**
     * Number in Maternal Cohort 12 months KP
     * @return the indicator
     */
    public CohortDefinition originalMaternalKpCohort12Months() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "where e.hiv_status = '703'\n" +
                "and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("originalMaternalKpCohort12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original Maternal KP Cohort");
        return cd;
    }
    /**
     * Number in Maternal Cohort 12 months NP
     * @return the indicator
     */
    public CohortDefinition originalMaternalNpCohort12Months() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "      and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("originalMaternalNpCohort12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original Maternal NP Cohort");
        return cd;
    }
    /**
     * Number in Maternal Cohort TI 3 months KP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalKp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    where e.hiv_status = 703 and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalKp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal KP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TI 3 months NP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalNp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "    where e.hiv_status in (664,1067) and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "    and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "              or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalNp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal NP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 3 months KP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalKp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  where  e.hiv_status =703 and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "        and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "        and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalKp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal KP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 3 months NP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalNp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalNp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal NP Cohort 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Discharged to CCC 3 months KP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalKp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalKp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal KP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 3 months NP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalNp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalNp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal NP Cohort 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Net Cohort 3 months KP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalKp3MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort12Months", ReportUtils.map(originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp3MonthsCohort",ReportUtils.map(transferInMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp3MonthsCohort",ReportUtils.map(transferOutMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort12Months AND transferInMaternalKp3MonthsCohort) AND NOT (transferOutMaternalKp3MonthsCohort OR dischargedToCCCMaternalKp3MonthsCohort)");
        return cd;
    }



    /**
     * Number in Maternal Net Cohort treatment 3 months NP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalNp3MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort12Months", ReportUtils.map(originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp3MonthsCohort",ReportUtils.map(transferInMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp3MonthsCohort",ReportUtils.map(transferOutMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort12Months AND transferInMaternalNp3MonthsCohort) AND NOT (transferOutMaternalNp3MonthsCohort OR dischargedToCCCMaternalNp3MonthsCohort)");
        return cd;
    }

    /**
     * Number in Maternal Cohort LTFU 3 months KP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalKp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalKp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal KP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort LTFU 3 months NP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalNp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalNp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal NP Cohort 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Reported dead 3 months KP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalKp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalKp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal KP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Reported dead 3 months NP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalNp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalNp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal NP Cohort 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Stopped treatment 3 months KP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalKp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date_sub(date(:endDate) , interval 10 month)\n" +
                "    left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "    where e.hiv_status =703 \n" +
                "          and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "          and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "               or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalKp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal KP Cohort 3 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Stopped treatment 3 months NP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalNp3MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date_sub(date(:endDate) , interval 10 month)\n" +
                "  left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "      and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "           or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)))\n" +
                "      and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "           or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalNp3MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal NP Cohort 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Alive and Active on treatment 3 months KP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalKp3MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort12Months", ReportUtils.map(originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp3MonthsCohort",ReportUtils.map(transferInMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp3MonthsCohort",ReportUtils.map(transferOutMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalKp3MonthsCohort",ReportUtils.map(ltfuMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalKp3MonthsCohort",ReportUtils.map(deceasedMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalKp3MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort12Months AND transferInMaternalKp3MonthsCohort) AND NOT (transferOutMaternalKp3MonthsCohort OR dischargedToCCCMaternalKp3MonthsCohort OR ltfuMaternalKp3MonthsCohort OR deceasedMaternalKp3MonthsCohort OR stoppedTreatmentMaternalKp3MonthsCohort )");
        return cd;
    }



    /**
     * Number in Maternal Cohort Alive and active treatment 3 months NP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalNp3MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort12Months", ReportUtils.map(originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp3MonthsCohort",ReportUtils.map(transferInMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp3MonthsCohort",ReportUtils.map(transferOutMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalNp3MonthsCohort",ReportUtils.map(ltfuMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalNp3MonthsCohort",ReportUtils.map(deceasedMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalNp3MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort12Months AND transferInMaternalNp3MonthsCohort) AND NOT (transferOutMaternalNp3MonthsCohort OR dischargedToCCCMaternalNp3MonthsCohort OR ltfuMaternalNp3MonthsCohort OR deceasedMaternalNp3MonthsCohort OR stoppedTreatmentMaternalNp3MonthsCohort)");
        return cd;
    }

    /**
     * Number of patients whos samples collected 3 months
     * VL sample collected within 3 months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadSamplesCollected3Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "   where le.lab_test in (1305,856) and le.date_test_requested is not null\n" +
                "     and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoSamplesCollectedWithin3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Samples Collected Within 3 Months");
        return cd;
    }

    /**
     * Number of patients whos results received 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadResultsReceived3Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test in (1305,856)  and le.test_result is not null\n" +
                " and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoReceivedWithin3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results received Within 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load samples collected 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedKp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected3Months",ReportUtils.map(patientsWithViralLoadSamplesCollected3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND patientsWithViralLoadSamplesCollected3Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load samples collected 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedNp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected3Months",ReportUtils.map(patientsWithViralLoadSamplesCollected3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND patientsWithViralLoadSamplesCollected3Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results received 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedKp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived3Months",ReportUtils.map(patientsWithViralLoadResultsReceived3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND patientsWithViralLoadResultsReceived3Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load results received 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedNp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived3Months",ReportUtils.map(patientsWithViralLoadResultsReceived3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND patientsWithViralLoadResultsReceived3Months)");
        return cd;
    }

    /**
     * Number of patients whos vl results <1000 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan10003Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <1000) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan10003Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <1000 Within 3 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results <400 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan400Month3() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <400) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan400Month3");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <400 Within 3 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results <50 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan50Month3() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <50) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan50Month3");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <50 Within 3 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000Month3() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >=1000\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000Month3");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 Within 3 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=400 with EACs 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan400WithEACsMonth3() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_enhanced_adherence ea on ea.patient_id = le.patient_id\n" +
                "where le.lab_test = 856 and le.test_result >= 400\n" +
                "  and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "  and date(ea.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan400WithEACsMonth3");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=400 with EAC Within 3 Months");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF 3 months
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFMonth3() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFMonth3");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure Within 3 Months");
        return cd;
    }


    /**
     * Number of patients whos vl result >=1000 with STF and repeat VL
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFAndRepeatVlMonth3() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFAndRepeatVlMonth3");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure and Repeat VL Within 3 Months");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure
     * VL result received within 3 months
     * @return the indicator
     */
    public CohortDefinition switchedRegimenLineAfterConfirmedSTF3Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on de.patient_id = le.patient_id\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "      and date(de.date_discontinued) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("switchedRegimenLineAfterConfirmedSTF3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number switched regimen line after confirmed treatment failure Within 3 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan10003Kp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan10003Months",ReportUtils.map(vlResultsLessThan10003Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsLessThan10003Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan10003Np3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan10003Months",ReportUtils.map(vlResultsLessThan10003Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsLessThan10003Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Kp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month3",ReportUtils.map(vlResultsLessThan400Month3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsLessThan400Month3)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Np3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month3",ReportUtils.map(vlResultsLessThan400Month3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsLessThan400Month3)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results <50 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Kp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month3",ReportUtils.map(vlResultsLessThan50Month3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsLessThan50Month3)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <50 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Np3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month3",ReportUtils.map(vlResultsLessThan50Month3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsLessThan50Month3)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=1000 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Kp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month3",ReportUtils.map(vlResultsMoreThan1000Month3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsMoreThan1000Month3)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=1000 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Np3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month3",ReportUtils.map(vlResultsMoreThan1000Month3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsMoreThan1000Month3)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsKp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth3",ReportUtils.map(vlResultsMoreThan400WithEACsMonth3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsMoreThan400WithEACsMonth3)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsNp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth3",ReportUtils.map(vlResultsMoreThan400WithEACsMonth3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsMoreThan400WithEACsMonth3)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFKp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth3",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsMoreThan1000WithSTFMonth3)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFNp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth3",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsMoreThan1000WithSTFMonth3)");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth3",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth3)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth3",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth3(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth3)");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure 3 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF3Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND switchedRegimenLineAfterConfirmedSTF3Months)");
        return cd;
    }

    /**
     * Number switched regimen line after confirmed treatment failure 3 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp3Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp3MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF3Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND switchedRegimenLineAfterConfirmedSTF3Months)");
        return cd;
    }

    /**
     * FIRST REVIEW: 6 Months Cohort
     */
    /**
     * Number in Maternal Cohort TI 6 months KP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalKp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    where e.hiv_status = 703 and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalKp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal KP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TI 6 months NP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalNp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "    where e.hiv_status in (664,1067) and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "    and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "              or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalNp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal NP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 6 months KP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalKp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  where  e.hiv_status =703 and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "        and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "        and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalKp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal KP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 6 months NP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalNp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalNp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal NP Cohort 6 months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Discharged to CCC 6 months KP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalKp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalKp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal KP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 6 months NP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalNp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalNp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal NP Cohort 6 months");
        return cd;
    }

    /**
     * Number in Maternal Net Cohort 6 months KP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalKp6MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort12Months", ReportUtils.map(originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp6MonthsCohort",ReportUtils.map(transferInMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp6MonthsCohort",ReportUtils.map(transferOutMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp6MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort12Months AND transferInMaternalKp6MonthsCohort) AND NOT (transferOutMaternalKp6MonthsCohort OR dischargedToCCCMaternalKp6MonthsCohort)");
        return cd;
    }



    /**
     * Number in Maternal Net Cohort treatment 6 months NP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalNp6MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort12Months", ReportUtils.map(originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp6MonthsCohort",ReportUtils.map(transferInMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp6MonthsCohort",ReportUtils.map(transferOutMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp6MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort12Months AND transferInMaternalNp6MonthsCohort) AND NOT (transferOutMaternalNp6MonthsCohort OR dischargedToCCCMaternalNp6MonthsCohort)");
        return cd;
    }

    /**
     * Number in Maternal Cohort LTFU 6 months KP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalKp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalKp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal KP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort LTFU 6 months NP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalNp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalNp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal NP Cohort 6 months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Reported dead 6 months KP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalKp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalKp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal KP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Reported dead 6 months NP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalNp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalNp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal NP Cohort 6 months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Stopped treatment 6 months KP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalKp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date_sub(date(:endDate) , interval 7 month)\n" +
                "    left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "    where e.hiv_status =703 \n" +
                "          and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "          and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month))\n" +
                "               or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalKp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal KP Cohort 6 months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Stopped treatment 6 months NP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalNp6MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date_sub(date(:endDate) , interval 7 month)\n" +
                "  left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "      and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month))\n" +
                "           or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)))\n" +
                "      and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "           or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalNp6MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal NP Cohort 6 months");
        return cd;
    }

    /**
     * Number in Maternal Alive and Active on treatment 6 months KP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalKp6MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort12Months", ReportUtils.map(originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp6MonthsCohort",ReportUtils.map(transferInMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp6MonthsCohort",ReportUtils.map(transferOutMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp6MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalKp6MonthsCohort",ReportUtils.map(ltfuMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalKp6MonthsCohort",ReportUtils.map(deceasedMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalKp6MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort12Months AND transferInMaternalKp6MonthsCohort) AND NOT (transferOutMaternalKp6MonthsCohort OR dischargedToCCCMaternalKp6MonthsCohort OR ltfuMaternalKp6MonthsCohort OR deceasedMaternalKp6MonthsCohort OR stoppedTreatmentMaternalKp6MonthsCohort )");
        return cd;
    }



    /**
     * Number in Maternal Cohort Alive and active treatment 6 months NP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalNp6MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort12Months", ReportUtils.map(originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp6MonthsCohort",ReportUtils.map(transferInMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp6MonthsCohort",ReportUtils.map(transferOutMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp6MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalNp6MonthsCohort",ReportUtils.map(ltfuMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalNp6MonthsCohort",ReportUtils.map(deceasedMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalNp6MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort12Months AND transferInMaternalNp6MonthsCohort) AND NOT (transferOutMaternalNp6MonthsCohort OR dischargedToCCCMaternalNp6MonthsCohort OR ltfuMaternalNp6MonthsCohort OR deceasedMaternalNp6MonthsCohort OR stoppedTreatmentMaternalNp6MonthsCohort)");
        return cd;
    }

    /**
     * Number of patients whos samples collected 6 months
     * VL sample collected within 6 months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadSamplesCollected6Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "   where le.lab_test in (1305,856) and le.date_test_requested is not null\n" +
                "     and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoSamplesCollectedWithin6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Samples Collected Within 6 months");
        return cd;
    }

    /**
     * Number of patients whos results received 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadResultsReceived6Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test in (1305,856)  and le.test_result is not null\n" +
                " and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoReceivedWithin6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results received Within 6 months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load samples collected 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedKp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected6Months",ReportUtils.map(patientsWithViralLoadSamplesCollected6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND patientsWithViralLoadSamplesCollected6Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load samples collected 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedNp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected6Months",ReportUtils.map(patientsWithViralLoadSamplesCollected6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND patientsWithViralLoadSamplesCollected6Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results received 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedKp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived6Months",ReportUtils.map(patientsWithViralLoadResultsReceived6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND patientsWithViralLoadResultsReceived6Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load results received 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedNp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived6Months",ReportUtils.map(patientsWithViralLoadResultsReceived6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND patientsWithViralLoadResultsReceived6Months)");
        return cd;
    }

    /**
     * Number of patients whos vl results <1000 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan10006Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <1000) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan10006Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <1000 Within 6 months");
        return cd;
    }

    /**
     * Number of patients whos vl results <400 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan400Month6() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <400) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan400Month6");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <400 Within 6 months");
        return cd;
    }

    /**
     * Number of patients whos vl results <50 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan50Month6() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <50) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan50Month6");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <50 Within 6 months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000Month6() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >=1000\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000Month6");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 Within 6 months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=400 with EACs 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan400WithEACsMonth6() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_enhanced_adherence ea on ea.patient_id = le.patient_id\n" +
                "where le.lab_test = 856 and le.test_result >= 400\n" +
                "  and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "  and date(ea.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan400WithEACsMonth6");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=400 with EAC Within 6 months");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF 6 months
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFMonth6() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFMonth6");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure Within 6 months");
        return cd;
    }


    /**
     * Number of patients whos vl result >=1000 with STF and repeat VL
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFAndRepeatVlMonth6() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFAndRepeatVlMonth6");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure and Repeat VL Within 6 months");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure
     * VL result received within 6 months
     * @return the indicator
     */
    public CohortDefinition switchedRegimenLineAfterConfirmedSTF6Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on de.patient_id = le.patient_id\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "      and date(de.date_discontinued) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 7 month)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("switchedRegimenLineAfterConfirmedSTF6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number switched regimen line after confirmed treatment failure Within 6 months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan1000Kp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan10006Months",ReportUtils.map(vlResultsLessThan10006Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsLessThan10006Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan1000Np6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan10006Months",ReportUtils.map(vlResultsLessThan10006Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsLessThan10006Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Kp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month6",ReportUtils.map(vlResultsLessThan400Month6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsLessThan400Month6)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Np6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month6",ReportUtils.map(vlResultsLessThan400Month6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsLessThan400Month6)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results <50 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Kp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month6",ReportUtils.map(vlResultsLessThan50Month6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsLessThan50Month6)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <50 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Np6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month6",ReportUtils.map(vlResultsLessThan50Month6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsLessThan50Month6)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=1000 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Kp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month6",ReportUtils.map(vlResultsMoreThan1000Month6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsMoreThan1000Month6)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=1000 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Np6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month6",ReportUtils.map(vlResultsMoreThan1000Month6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsMoreThan1000Month6)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsKp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth6",ReportUtils.map(vlResultsMoreThan400WithEACsMonth6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsMoreThan400WithEACsMonth6)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsNp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth6",ReportUtils.map(vlResultsMoreThan400WithEACsMonth6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsMoreThan400WithEACsMonth6)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFKp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth6",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsMoreThan1000WithSTFMonth6)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFNp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth6",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsMoreThan1000WithSTFMonth6)");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth6",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth6)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth6",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth6(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth6)");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure 6 months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF6Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp6MonthsCohort AND switchedRegimenLineAfterConfirmedSTF6Months)");
        return cd;
    }

    /**
     * Number switched regimen line after confirmed treatment failure 6 months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp6Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp6MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF6Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp6MonthsCohort AND switchedRegimenLineAfterConfirmedSTF6Months)");
        return cd;
    }


    /**
     * FIRST REVIEW: 12 Months Cohort
     */
    /**
     * Number in Maternal Cohort TI 12 Months KP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalKp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    where e.hiv_status = 703 and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalKp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal KP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TI 12 Months NP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalNp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "    where e.hiv_status in (664,1067) and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "    and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "              or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalNp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal NP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 12 Months KP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalKp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  where  e.hiv_status =703 and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "        and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "        and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalKp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal KP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 12 Months NP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalNp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalNp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal NP Cohort 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Discharged to CCC 12 Months KP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalKp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalKp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal KP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 12 Months NP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalNp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalNp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal NP Cohort 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Net Cohort 12 Months KP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalKp12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort12Months", ReportUtils.map(originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp12MonthsCohort",ReportUtils.map(transferInMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp12MonthsCohort",ReportUtils.map(transferOutMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp12MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort12Months AND transferInMaternalKp12MonthsCohort) AND NOT (transferOutMaternalKp12MonthsCohort OR dischargedToCCCMaternalKp12MonthsCohort)");
        return cd;
    }



    /**
     * Number in Maternal Net Cohort treatment 12 Months NP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalNp12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort12Months", ReportUtils.map(originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp12MonthsCohort",ReportUtils.map(transferInMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp12MonthsCohort",ReportUtils.map(transferOutMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp12MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort12Months AND transferInMaternalNp12MonthsCohort) AND NOT (transferOutMaternalNp12MonthsCohort OR dischargedToCCCMaternalNp12MonthsCohort)");
        return cd;
    }

    /**
     * Number in Maternal Cohort LTFU 12 Months KP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalKp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalKp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal KP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort LTFU 12 Months NP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalNp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalNp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal NP Cohort 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Reported dead 12 Months KP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalKp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalKp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal KP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Reported dead 12 Months NP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalNp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalNp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal NP Cohort 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Stopped treatment 12 Months KP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalKp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date(:endDate)\n" +
                "    left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "    where e.hiv_status =703 \n" +
                "          and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "          and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "               or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalKp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal KP Cohort 12 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Stopped treatment 12 Months NP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalNp12MonthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date(:endDate)\n" +
                "  left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "      and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "           or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)))\n" +
                "      and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate))\n" +
                "           or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalNp12MonthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal NP Cohort 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Alive and Active on treatment 12 Months KP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalKp12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort12Months", ReportUtils.map(originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp12MonthsCohort",ReportUtils.map(transferInMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp12MonthsCohort",ReportUtils.map(transferOutMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp12MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalKp12MonthsCohort",ReportUtils.map(ltfuMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalKp12MonthsCohort",ReportUtils.map(deceasedMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalKp12MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort12Months AND transferInMaternalKp12MonthsCohort) AND NOT (transferOutMaternalKp12MonthsCohort OR dischargedToCCCMaternalKp12MonthsCohort OR ltfuMaternalKp12MonthsCohort OR deceasedMaternalKp12MonthsCohort OR stoppedTreatmentMaternalKp12MonthsCohort )");
        return cd;
    }



    /**
     * Number in Maternal Cohort Alive and active treatment 12 Months NP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalNp12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort12Months", ReportUtils.map(originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp12MonthsCohort",ReportUtils.map(transferInMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp12MonthsCohort",ReportUtils.map(transferOutMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp12MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalNp12MonthsCohort",ReportUtils.map(ltfuMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalNp12MonthsCohort",ReportUtils.map(deceasedMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalNp12MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort12Months AND transferInMaternalNp12MonthsCohort) AND NOT (transferOutMaternalNp12MonthsCohort OR dischargedToCCCMaternalNp12MonthsCohort OR ltfuMaternalNp12MonthsCohort OR deceasedMaternalNp12MonthsCohort OR stoppedTreatmentMaternalNp12MonthsCohort)");
        return cd;
    }

    /**
     * Number of patients whos samples collected 12 Months
     * VL sample collected within 12 Months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadSamplesCollected12Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "   where le.lab_test in (1305,856) and le.date_test_requested is not null\n" +
                "     and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoSamplesCollectedWithin12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Samples Collected Within 12 Months");
        return cd;
    }

    /**
     * Number of patients whos results received 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadResultsReceived12Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test in (1305,856)  and le.test_result is not null\n" +
                " and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoReceivedWithin12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results received Within 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load samples collected 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedKp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected12Months",ReportUtils.map(patientsWithViralLoadSamplesCollected12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND patientsWithViralLoadSamplesCollected12Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load samples collected 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedNp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected12Months",ReportUtils.map(patientsWithViralLoadSamplesCollected12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND patientsWithViralLoadSamplesCollected12Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results received 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedKp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived12Months",ReportUtils.map(patientsWithViralLoadResultsReceived12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND patientsWithViralLoadResultsReceived12Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load results received 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedNp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived12Months",ReportUtils.map(patientsWithViralLoadResultsReceived12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND patientsWithViralLoadResultsReceived12Months)");
        return cd;
    }

    /**
     * Number of patients whos vl results <1000 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan100012Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <1000) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan100012Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <1000 Within 12 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results <400 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan400Month12() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <400) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan400Month12");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <400 Within 12 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results <50 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan50Month12() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <50) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan50Month12");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <50 Within 12 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000Month12() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >=1000\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000Month12");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 Within 12 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=400 with EACs 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan400WithEACsMonth12() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_enhanced_adherence ea on ea.patient_id = le.patient_id\n" +
                "where le.lab_test = 856 and le.test_result >= 400\n" +
                "  and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "  and date(ea.visit_date) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan400WithEACsMonth12");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=400 with EAC Within 12 Months");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF 12 Months
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFMonth12() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFMonth12");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure Within 12 Months");
        return cd;
    }


    /**
     * Number of patients whos vl result >=1000 with STF and repeat VL
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFAndRepeatVlMonth12() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFAndRepeatVlMonth12");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure and Repeat VL Within 12 Months");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure
     * VL result received within 12 Months
     * @return the indicator
     */
    public CohortDefinition switchedRegimenLineAfterConfirmedSTF12Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on de.patient_id = le.patient_id\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "      and date(de.date_discontinued) between date_sub(date(:startDate) , interval 1 year) and date(:endDate)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("switchedRegimenLineAfterConfirmedSTF12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number switched regimen line after confirmed treatment failure Within 12 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan1000Kp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan100012Months",ReportUtils.map(vlResultsLessThan100012Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsLessThan100012Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan1000Np12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan100012Months",ReportUtils.map(vlResultsLessThan100012Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsLessThan100012Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Kp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month12",ReportUtils.map(vlResultsLessThan400Month12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsLessThan400Month12)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Np12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month12",ReportUtils.map(vlResultsLessThan400Month12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsLessThan400Month12)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results <50 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Kp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month12",ReportUtils.map(vlResultsLessThan50Month12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsLessThan50Month12)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <50 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Np12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month12",ReportUtils.map(vlResultsLessThan50Month12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsLessThan50Month12)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=1000 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Kp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month12",ReportUtils.map(vlResultsMoreThan1000Month12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsMoreThan1000Month12)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=1000 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Np12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month12",ReportUtils.map(vlResultsMoreThan1000Month12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsMoreThan1000Month12)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsKp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth12",ReportUtils.map(vlResultsMoreThan400WithEACsMonth12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsMoreThan400WithEACsMonth12)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsNp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth12",ReportUtils.map(vlResultsMoreThan400WithEACsMonth12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsMoreThan400WithEACsMonth12)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFKp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth12",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsMoreThan1000WithSTFMonth12)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFNp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth12",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsMoreThan1000WithSTFMonth12)");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth12",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth12)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth12",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth12(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth12)");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure 12 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF12Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp12MonthsCohort AND switchedRegimenLineAfterConfirmedSTF12Months)");
        return cd;
    }

    /**
     * Number switched regimen line after confirmed treatment failure 12 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp12Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp12MonthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF12Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp12MonthsCohort AND switchedRegimenLineAfterConfirmedSTF12Months)");
        return cd;
    }





    /**
     * SECOND REVIEW
     */

    /**
     * Number in Maternal Cohort 24 months KP
     * @return the indicator
     */
    public CohortDefinition originalMaternalKpCohort24months() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "where e.hiv_status = '703'\n" +
                "and date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("originalMaternalKpCohort24months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort 24 months NP
     * @return the indicator
     */
    public CohortDefinition originalMaternalNpCohort24months() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "      and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("originalMaternalNpCohort24months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original Maternal NP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TI 24 Months KP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalKp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    where e.hiv_status = 703 and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalKp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TI 24 Months NP
     * @return the indicator
     */
    public CohortDefinition transferInMaternalNp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "    left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "    where e.hiv_status in (664,1067) and he.patient_type=160563\n" +
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "    and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "              or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferInMaternalNp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer In Maternal NP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 24 Months KP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalKp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  where  e.hiv_status =703 and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "        and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "        and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalKp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort TO 24 Months NP
     * @return the indicator
     */
    public CohortDefinition transferOutMaternalNp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='HIV' and dis.discontinuation_reason = 159492\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("transferOutMaternalNp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out Maternal NP Cohort 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Discharged to CCC 24 Months KP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalKp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalKp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 24 Months NP
     * @return the indicator
     */
    public CohortDefinition dischargedToCCCMaternalNp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160035\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("dischargedToCCCMaternalNp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discharged to CCC Maternal NP Cohort 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Net Cohort 24 Months KP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalKp24monthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort24months", ReportUtils.map(originalMaternalKpCohort24months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp24monthsCohort",ReportUtils.map(transferInMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp24monthsCohort",ReportUtils.map(transferOutMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp24monthsCohort",ReportUtils.map(dischargedToCCCMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort24months AND transferInMaternalKp24monthsCohort) AND NOT (transferOutMaternalKp24monthsCohort OR dischargedToCCCMaternalKp24monthsCohort)");
        return cd;
    }



    /**
     * Number in Maternal Net Cohort treatment 24 Months NP
     * @return the indicator
     */
    public CohortDefinition netCohortMaternalNp24monthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort24months", ReportUtils.map(originalMaternalNpCohort24months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp24monthsCohort",ReportUtils.map(transferInMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp24monthsCohort",ReportUtils.map(transferOutMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp24monthsCohort",ReportUtils.map(dischargedToCCCMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort24months AND transferInMaternalNp24monthsCohort) AND NOT (transferOutMaternalNp24monthsCohort OR dischargedToCCCMaternalNp24monthsCohort)");
        return cd;
    }

    /**
     * Number in Maternal Cohort LTFU 24 Months KP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalKp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalKp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort LTFU 24 Months NP
     * @return the indicator
     */
    public CohortDefinition ltfuMaternalNp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 5240\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ltfuMaternalNp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Interruption In treatment (LTFU) Maternal NP Cohort 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Reported dead 24 Months KP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalKp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                " inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "where  e.hiv_status =703 and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalKp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Reported dead 24 Months NP
     * @return the indicator
     */
    public CohortDefinition deceasedMaternalNp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_program_discontinuation dis on dis.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where  e.hiv_status in (664,1067) and dis.program_name='MCH Mother' and dis.discontinuation_reason = 160034\n" +
                "       and coalesce(date(dis.effective_discontinuation_date),date(dis.transfer_date),date(dis.visit_date)) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "       and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "            or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("deceasedMaternalNp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported dead Maternal NP Cohort 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Stopped treatment 24 Months KP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalKp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date(:endDate)\n" +
                "    left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "    where e.hiv_status =703 \n" +
                "          and date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "          and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate))\n" +
                "               or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)));\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalKp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal KP Cohort 24 Months");
        return cd;
    }
    /**
     * Number in Maternal Cohort Stopped treatment 24 Months NP
     * @return the indicator
     */
    public CohortDefinition stoppedTreatmentMaternalNp24monthsCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(de.date_started) <= date(:endDate)\n" +
                "  left  join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)\n" +
                "      and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate))\n" +
                "           or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)))\n" +
                "      and ((v.final_test_result ='Positive' and date(v.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year))\n" +
                "           or (ht.final_test_result ='Positive' and date(ht.visit_date) between date_sub(date(:startDate) , interval 2 year) and date_sub(date(:endDate) , interval 2 year)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("stoppedTreatmentMaternalNp24monthsCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Stopped treatment Maternal NP Cohort 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Alive and Active on treatment 24 Months KP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalKp24monthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalKpCohort24months", ReportUtils.map(originalMaternalKpCohort24months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp24monthsCohort",ReportUtils.map(transferInMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp24monthsCohort",ReportUtils.map(transferOutMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp24monthsCohort",ReportUtils.map(dischargedToCCCMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalKp24monthsCohort",ReportUtils.map(ltfuMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalKp24monthsCohort",ReportUtils.map(deceasedMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalKp24monthsCohort",ReportUtils.map(stoppedTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort24months AND transferInMaternalKp24monthsCohort) AND NOT (transferOutMaternalKp24monthsCohort OR dischargedToCCCMaternalKp24monthsCohort OR ltfuMaternalKp24monthsCohort OR deceasedMaternalKp24monthsCohort OR stoppedTreatmentMaternalKp24monthsCohort )");
        return cd;
    }



    /**
     * Number in Maternal Cohort Alive and active treatment 24 Months NP
     * @return the indicator
     */
    public CohortDefinition aliveAndActiveOnTreatmentMaternalNp24monthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("originalMaternalNpCohort24months", ReportUtils.map(originalMaternalNpCohort24months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp24monthsCohort",ReportUtils.map(transferInMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp24monthsCohort",ReportUtils.map(transferOutMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp24monthsCohort",ReportUtils.map(dischargedToCCCMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalNp24monthsCohort",ReportUtils.map(ltfuMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalNp24monthsCohort",ReportUtils.map(deceasedMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalNp24monthsCohort",ReportUtils.map(stoppedTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort24months AND transferInMaternalNp24monthsCohort) AND NOT (transferOutMaternalNp24monthsCohort OR dischargedToCCCMaternalNp24monthsCohort OR ltfuMaternalNp24monthsCohort OR deceasedMaternalNp24monthsCohort OR stoppedTreatmentMaternalNp24monthsCohort)");
        return cd;
    }

    /**
     * Number of patients whos samples collected 24 Months
     * VL sample collected within 24 Months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadSamplesCollected24Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "   where le.lab_test in (1305,856) and le.date_test_requested is not null\n" +
                "     and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoSamplesCollectedWithin24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Samples Collected Within 24 Months");
        return cd;
    }

    /**
     * Number of patients whos results received 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadResultsReceived24Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test in (1305,856)  and le.test_result is not null\n" +
                " and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWhoReceivedWithin24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results received Within 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load samples collected 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedKp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected24Months",ReportUtils.map(patientsWithViralLoadSamplesCollected24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND patientsWithViralLoadSamplesCollected24Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load samples collected 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadSamplesCollectedNp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadSamplesCollected24Months",ReportUtils.map(patientsWithViralLoadSamplesCollected24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND patientsWithViralLoadSamplesCollected24Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results received 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedKp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived24Months",ReportUtils.map(patientsWithViralLoadResultsReceived24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND patientsWithViralLoadResultsReceived24Months)");
        return cd;
    }
    /**
     * Number in Maternal Cohort Viral load results received 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithViralLoadResultsReceivedNp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsWithViralLoadResultsReceived24Months",ReportUtils.map(patientsWithViralLoadResultsReceived24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND patientsWithViralLoadResultsReceived24Months)");
        return cd;
    }

    /**
     * Number of patients whos vl results <1000 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan100024Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <1000) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan100024Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <1000 Within 24 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results <400 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan400Month24() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <400) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan400Month24");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <400 Within 24 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results <50 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsLessThan50Month24() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where (le.lab_test = 856 and le.test_result <50) or le.lab_test=1305\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsLessThan50Month24");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results <50 Within 24 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000Month24() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >=1000\n" +
                "and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000Month24");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 Within 24 Months");
        return cd;
    }

    /**
     * Number of patients whos vl results >=400 with EACs 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan400WithEACsMonth24() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_enhanced_adherence ea on ea.patient_id = le.patient_id\n" +
                "where le.lab_test = 856 and le.test_result >= 400\n" +
                "  and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "  and date(ea.visit_date) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan400WithEACsMonth24");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=400 with EAC Within 24 Months");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF 24 Months
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFMonth24() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFMonth24");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure Within 24 Months");
        return cd;
    }


    /**
     * Number of patients whos vl result >=1000 with STF and repeat VL
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition vlResultsMoreThan1000WithSTFAndRepeatVlMonth24() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("vlResultsMoreThan1000WithSTFAndRepeatVlMonth24");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Who Vl Results >=1000 with Suspected Treatment Failure and Repeat VL Within 24 Months");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure
     * VL result received within 24 Months
     * @return the indicator
     */
    public CohortDefinition switchedRegimenLineAfterConfirmedSTF24Months() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on de.patient_id = le.patient_id\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "      and date(de.date_discontinued) between date_sub(date(:startDate) , interval 2 year) and date(:endDate)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("switchedRegimenLineAfterConfirmedSTF24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number switched regimen line after confirmed treatment failure Within 24 Months");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan1000Kp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan100024Months",ReportUtils.map(vlResultsLessThan100024Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsLessThan100024Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <1000 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan1000Np24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan100024Months",ReportUtils.map(vlResultsLessThan100024Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsLessThan100024Months)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Kp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month24",ReportUtils.map(vlResultsLessThan400Month24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsLessThan400Month24)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <400 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan400Np24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan400Month24",ReportUtils.map(vlResultsLessThan400Month24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsLessThan400Month24)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results <50 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Kp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month24",ReportUtils.map(vlResultsLessThan50Month24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsLessThan50Month24)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results <50 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsLessThan50Np24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsLessThan50Month24",ReportUtils.map(vlResultsLessThan50Month24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsLessThan50Month24)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=1000 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Kp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month24",ReportUtils.map(vlResultsMoreThan1000Month24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsMoreThan1000Month24)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=1000 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000Np24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000Month24",ReportUtils.map(vlResultsMoreThan1000Month24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsMoreThan1000Month24)");
        return cd;
    }


    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsKp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth24",ReportUtils.map(vlResultsMoreThan400WithEACsMonth24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsMoreThan400WithEACsMonth24)");
        return cd;
    }

    /**
     * Number in Maternal Cohort Viral load results >=400 with EACs 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan400WithEACsNp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan400WithEACsMonth24",ReportUtils.map(vlResultsMoreThan400WithEACsMonth24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsMoreThan400WithEACsMonth24)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFKp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth24",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsMoreThan1000WithSTFMonth24)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFNp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFMonth24",ReportUtils.map(vlResultsMoreThan1000WithSTFMonth24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsMoreThan1000WithSTFMonth24)");
        return cd;
    }


    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth24",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth24)");
        return cd;
    }

    /**
     * Number of patients whos vl results >=1000 with STF and Repeat VL 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("vlResultsMoreThan1000WithSTFAndRepeatVlMonth24",ReportUtils.map(vlResultsMoreThan1000WithSTFAndRepeatVlMonth24(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND vlResultsMoreThan1000WithSTFAndRepeatVlMonth24)");
        return cd;
    }


    /**
     * Number switched regimen line after confirmed treatment failure 24 Months KP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalKp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF24Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp24monthsCohort AND switchedRegimenLineAfterConfirmedSTF24Months)");
        return cd;
    }

    /**
     * Number switched regimen line after confirmed treatment failure 24 Months NP
     * @return the indicator
     */
    public CohortDefinition maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp24Months () {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("aliveAndActiveOnTreatmentMaternalNp24monthsCohort", ReportUtils.map(aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF24Months",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp24monthsCohort AND switchedRegimenLineAfterConfirmedSTF24Months)");
        return cd;
    }


}
