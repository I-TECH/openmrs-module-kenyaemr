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
     * Number in Maternal Cohort 12 months KP
     * @return the indicator
     */
    public CohortDefinition originalMaternalKpCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "where e.hiv_status = '703'\n" +
                "and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("originalMaternalKpCohort");
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
    public CohortDefinition originalMaternalNpCohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  left join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=e.patient_id\n" +
                "  left join kenyaemr_etl.etl_hts_test ht on ht.patient_id=e.patient_id and ht.hts_entry_point =160538\n" +
                "where e.hiv_status in (664,1067)\n" +
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "      and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year))\n" +
                "            or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)));\n" +
                "\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("originalMaternalNpCohort");
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
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
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
                "    and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "    and date(he.transfer_in_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "    and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "              or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));\n";
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
                "        and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
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
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "            or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));\n";
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
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
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
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "            or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));";
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
        cd.addSearch("originalMaternalKpCohort", ReportUtils.map(originalMaternalKpCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp3MonthsCohort",ReportUtils.map(transferInMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp3MonthsCohort",ReportUtils.map(transferOutMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort AND transferInMaternalKp3MonthsCohort) AND NOT (transferOutMaternalKp3MonthsCohort OR dischargedToCCCMaternalKp3MonthsCohort)");
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
        cd.addSearch("originalMaternalNpCohort", ReportUtils.map(originalMaternalNpCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp3MonthsCohort",ReportUtils.map(transferInMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp3MonthsCohort",ReportUtils.map(transferOutMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort AND transferInMaternalNp3MonthsCohort) AND NOT (transferOutMaternalNp3MonthsCohort OR dischargedToCCCMaternalNp3MonthsCohort)");
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
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
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
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "            or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));";
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
                "and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month);";
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
                "       and  date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "       and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "            or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));";
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
                "          and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
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
                "      and date(e.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "      and ((de.regimen_stopped = 1260 and date(de.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "           or (d.discontinuation_reason = 819 and  date(d.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)))\n" +
                "      and ((v.final_test_result = 703 and date(v.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month))\n" +
                "           or (ht.final_test_result = 703 and date(ht.visit_date) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)));\n" +
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
        cd.addSearch("originalMaternalKpCohort", ReportUtils.map(originalMaternalKpCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalKp3MonthsCohort",ReportUtils.map(transferInMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalKp3MonthsCohort",ReportUtils.map(transferOutMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalKp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalKp3MonthsCohort",ReportUtils.map(ltfuMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalKp3MonthsCohort",ReportUtils.map(deceasedMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalKp3MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalKpCohort AND transferInMaternalKp3MonthsCohort) AND NOT (transferOutMaternalKp3MonthsCohort OR dischargedToCCCMaternalKp3MonthsCohort OR ltfuMaternalKp3MonthsCohort OR deceasedMaternalKp3MonthsCohort OR stoppedTreatmentMaternalKp3MonthsCohort )");
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
        cd.addSearch("originalMaternalNpCohort", ReportUtils.map(originalMaternalNpCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInMaternalNp3MonthsCohort",ReportUtils.map(transferInMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferOutMaternalNp3MonthsCohort",ReportUtils.map(transferOutMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("dischargedToCCCMaternalNp3MonthsCohort",ReportUtils.map(dischargedToCCCMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuMaternalNp3MonthsCohort",ReportUtils.map(ltfuMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("deceasedMaternalNp3MonthsCohort",ReportUtils.map(deceasedMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTreatmentMaternalNp3MonthsCohort",ReportUtils.map(stoppedTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(originalMaternalNpCohort AND transferInMaternalNp3MonthsCohort) AND NOT (transferOutMaternalNp3MonthsCohort OR dischargedToCCCMaternalNp3MonthsCohort OR ltfuMaternalNp3MonthsCohort OR deceasedMaternalNp3MonthsCohort OR stoppedTreatmentMaternalNp3MonthsCohort)");
        return cd;
    }

    /**
     * Number of patients whos samples collected 3 months
     * VL sample collected within 3 months
     * @return the indicator
     */
    public CohortDefinition patientsWithViralLoadSamplesCollected() {
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
    public CohortDefinition patientsWithViralLoadResultsReceived() {
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
        cd.addSearch("patientsWithViralLoadSamplesCollected",ReportUtils.map(patientsWithViralLoadSamplesCollected(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND patientsWithViralLoadSamplesCollected)");
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
        cd.addSearch("patientsWithViralLoadSamplesCollected",ReportUtils.map(patientsWithViralLoadSamplesCollected(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND patientsWithViralLoadSamplesCollected)");
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
        cd.addSearch("patientsWithViralLoadResultsReceived",ReportUtils.map(patientsWithViralLoadResultsReceived(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND patientsWithViralLoadResultsReceived)");
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
        cd.addSearch("patientsWithViralLoadResultsReceived",ReportUtils.map(patientsWithViralLoadResultsReceived(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND patientsWithViralLoadResultsReceived)");
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
    public CohortDefinition switchedRegimenLineAfterConfirmedSTF() {
        String sqlQuery = "select le.patient_id from kenyaemr_etl.etl_laboratory_extract le\n" +
                "  inner join kenyaemr_etl.etl_drug_event de on de.patient_id = le.patient_id\n" +
                "  inner join openmrs.orders o on o.order_id =le.order_id\n" +
                "where le.lab_test = 856 and le.test_result >= 1000 and le.order_reason in(843,163523)\n" +
                "      and date(le.date_test_requested) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "      and date(de.date_discontinued) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 10 month)\n" +
                "having count(le.patient_id) >1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("switchedRegimenLineAfterConfirmedSTF");
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
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalKp3MonthsCohort AND switchedRegimenLineAfterConfirmedSTF)");
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
        cd.addSearch("switchedRegimenLineAfterConfirmedSTF",ReportUtils.map(switchedRegimenLineAfterConfirmedSTF(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(aliveAndActiveOnTreatmentMaternalNp3MonthsCohort AND switchedRegimenLineAfterConfirmedSTF)");
        return cd;
    }


}
