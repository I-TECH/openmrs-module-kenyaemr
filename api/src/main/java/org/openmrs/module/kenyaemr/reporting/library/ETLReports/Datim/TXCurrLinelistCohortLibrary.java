/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLLostToFollowupCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 1/14/17.
 */

/**
 * Library of cohort definitions used specifically in Datim TX CURR linelist
 */
@Component
public class TXCurrLinelistCohortLibrary {

    @Autowired
    private DatimCohortLibrary datimCohortLibrary;
    /**
     *
     */
    public  CohortDefinition transferInReportingMonth() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY) and date(:endDate)\n" +
                "and e.patient_type = 160563;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_TI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TI Present in current report but missing in previous report");
        return cd;
    }
    /**
     *Patients transferred in within the quarter
     */
    public  CohortDefinition transferInReportingQuarter() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "and e.patient_type = 160563;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_TI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TI Present in current report but missing in previous report");
        return cd;
    }
    /**
     *Patients who returned to care in the reporting month
     */
    public  CohortDefinition reEnrollmentToHIVReportingMonth() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY) and date(:endDate)\n" +
                "and e.patient_type = 159833;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_rtt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Returned to Tx");
        return cd;
    }
    /**
     *Re-enrolled within the quarter
     */
    public  CohortDefinition reEnrollmentToHIVReportingQuarter() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "and e.patient_type = 159833;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_rtt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Returned to Tx Present in current report but missing in previous report");
        return cd;
    }
    /**
     *Accounting for clients with old enrollments but their first hiv followup happened within the reporting period/return to care but had no previous hiv followup encounter - Quarterly
     */
    public  CohortDefinition noPrevHIVFUPEncQuarterly() {
        String sqlQuery="select e.patient_id from\n" +
                "(Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e group by e.patient_id having max(e.visit_date) < DATE_SUB(date(:endDate),INTERVAL 3 MONTH))e\n" +
                "inner join (select f.patient_id from kenyaemr_etl.etl_patient_hiv_followup f where f.visit_date <= date(:endDate) group by patient_id\n" +
                " having min(f.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate))f\n" +
                " on e.patient_id = f.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_rtt_no_prev_hiv_encounter");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Returned to Tx Present in current report but missing in previous report - had no previous hiv fup encounter");
        return cd;
    }
    /**
     *Accounting for clients with old enrollments but their first hiv followup happened within the reporting period/return to care but had no previous hiv followup encounter - Monthly
     */
    public  CohortDefinition noPrevHIVFUPEncMonthly() {
        String sqlQuery="select e.patient_id from\n" +
                "(Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e group by e.patient_id having max(e.visit_date) < DATE_SUB(date(:endDate),INTERVAL 1 MONTH))e\n" +
                "inner join (select f.patient_id,DATE_SUB(date(:endDate),INTERVAL 1 MONTH) ,min(f.visit_date) from kenyaemr_etl.etl_patient_hiv_followup f where f.visit_date <= date(:endDate) group by patient_id\n" +
                "having min(f.visit_date) between DATE_SUB(date(:endDate),INTERVAL 1 MONTH) and date(:endDate))f\n" +
                "on e.patient_id = f.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_rtt_no_prev_hiv_enc");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Returned to Tx Present in current report but missing in previous report - had no previous hiv fup encounter");
        return cd;
    }
    /**
     *Newly enrolled in the reporting month
     * @return
     */
    public  CohortDefinition newlyEnrolledReportingMonth() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY) and date(:endDate)\n" +
                "and e.patient_type = 164144;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_tx_new");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly enrolled Present in current report but missing in previous report");
        return cd;
    }
     /**
     *Patients newly enrolled in the reporting quarter
     * @return
     */
    public  CohortDefinition experiencedIITPreviousReportingMonth() {
        String sqlQuery="select  e.patient_id\n" +
                "from (\n" +
                "select fup_prev_period.patient_id,\n" +
                "  max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "  mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "  max(d.visit_date) as date_discontinued,\n" +
                "  d.patient_id as disc_patient,\n" +
                "  fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT,\n" +
                "  fup_reporting_period.first_tca_after_IIT as first_tca_after_IIT\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "    join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT,min(fup_reporting_period.next_appointment_date) as first_tca_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(date(:endDate) , interval 1 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup_prev_period.patient_id\n" +
                "    join kenyaemr_etl.etl_hiv_enrollment e on fup_prev_period.patient_id=e.patient_id\n" +
                "    left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                " where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                " group by patient_id\n" +
                ") d on d.patient_id = fup_prev_period.patient_id\n" +
                "where fup_prev_period.visit_date < date_sub(date(:endDate) , interval 1 MONTH)\n" +
                "group by patient_id\n" +
                "having (\n" +
                "          (((date(prev_period_latest_tca) < date(:endDate)) and\n" +
                "            (date(prev_period_latest_vis_date) < date(prev_period_latest_tca)))) and\n" +
                "          ((date(fup_reporting_period.first_visit_after_IIT) > date(date_discontinued) and\n" +
                "            date(fup_reporting_period.first_tca_after_IIT) > date(date_discontinued)) or\n" +
                "           disc_patient is null)\n" +
                "       and timestampdiff(day, date(prev_period_latest_tca),DATE_SUB(date(:endDate),INTERVAL 1 MONTH)) > 30)\n" +
                ")e;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("iit_previous_reporting_month");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Experienced IIT Previous reporting month");
        return cd;
    }
    /**
     *Patients newly enrolled in the reporting quarter
     * @return
     */
    public  CohortDefinition newlyEnrolledReportingQuarter() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "and e.patient_type = 164144;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_tx_new");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly enrolled Present in current report but missing in previous report");
        return cd;
    }

    /**
     *TX_CURR Present in Current period but missing in previous period - Newly enrolled
     * @return
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyNewlyEnrolled() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyEnrolledReportingMonth", ReportUtils.map(newlyEnrolledReportingMonth(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly AND newlyEnrolledReportingMonth)");
        return cd;

    }
    /**
     * TX_CURR Present in Current period but missing in previous period - TI
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyTrfIn() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInReportingMonth", ReportUtils.map(transferInReportingMonth(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly AND transferInReportingMonth");
        return cd;

    }
    /**
     * TX_CURR Present in Current period but missing in previous period - Re-enrollment/return To care
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyReEnrollment() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("reEnrollmentToHIVReportingMonth", ReportUtils.map(reEnrollmentToHIVReportingMonth(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingMonth", ReportUtils.map(experiencedIITPreviousReportingMonth(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("noPrevHIVFUPEncMonthly", ReportUtils.map(noPrevHIVFUPEncMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInReportingMonth", ReportUtils.map(transferInReportingMonth(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly AND (reEnrollmentToHIVReportingMonth OR experiencedIITPreviousReportingMonth OR noPrevHIVFUPEncMonthly) AND NOT transferInReportingMonth");
        return cd;

    }

     /**
     * Patients included in the current report- quarter
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInCurrentReport() {
        String sqlQuery="select cr.patient_id\n" +
                "from\n" +
                "(select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "          greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "          greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "          greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "          d.patient_id as disc_patient,\n" +
                "          d.effective_disc_date as effective_disc_date,\n" +
                "          max(d.visit_date) as date_discontinued,\n" +
                "          de.patient_id as started_on_drugs\n" +
                "   from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "          join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "          join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "          left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "          left outer JOIN\n" +
                "            (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "             where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "             group by patient_id\n" +
                "            ) d on d.patient_id = fup.patient_id\n" +
                "   where fup.visit_date <= date(:endDate)\n" +
                "   group by patient_id\n" +
                "   having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "       (\n" +
                "           ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "             and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "           )\n" +
                "       )\n" +
                ") cr;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_current_report");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report but missing in previous report");
        return cd;
    }
    /**
     * Patients included in the previous reporting period - quarter
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousReport() {
        String sqlQuery="select pr.patient_id from  (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "     greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "     greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "     greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "     d.patient_id as disc_patient,\n" +
                "     d.effective_disc_date as effective_disc_date,\n" +
                "     max(d.visit_date) as date_discontinued,\n" +
                "     de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "     left outer JOIN\n" +
                "       (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "        where date(visit_date) <= date_sub(DATE(:endDate), interval 3 MONTH) and program_name='HIV'\n" +
                "        group by patient_id\n" +
                "       ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "  (\n" +
                "      ((date_add(date(latest_tca), interval 30 DAY)  >= date_sub(DATE(:endDate), interval 3 MONTH) or date_add(date(latest_tca), interval 30 DAY)  >= date_sub(DATE(curdate()), interval 3 MONTH))\n" +
                "         and ((date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 3 MONTH) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "        and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "      )\n" +
                "  )\n" +
                ") pr;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_previous_report");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report but missing in previous report");
        return cd;
    }
    /**
     * Patients included in the current report but not present in the previous reporting period - quarter
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentReport AND NOT txCurLinelistForPatientsPresentInPreviousReport");
        return cd;

    }
    /**
     *TX_CURR Present in Current period but missing in previous period quarterly- Newly enrolled
     * @return
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousQuarterlyNewlyEnrolledReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyEnrolledReportingQuarter", ReportUtils.map(newlyEnrolledReportingQuarter(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInReportingQuarter", ReportUtils.map(transferInReportingQuarter(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport AND (newlyEnrolledReportingQuarter AND NOT transferInReportingQuarter)");
        return cd;

    }
    /**
     * TX_CURR Present in Current period but missing in previous period quarterly- Transfer in
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportTrfIn() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInReportingQuarter", ReportUtils.map(transferInReportingQuarter(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport AND transferInReportingQuarter");
        return cd;

    }
    /**
     * TX_CURR Present in Current period but missing in previous period - Re-enrollment/return To care
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportReEnrollment() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("reEnrollmentToHIVReportingQuarter", ReportUtils.map(reEnrollmentToHIVReportingQuarter(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("noPrevHIVFUPEncQuarterly", ReportUtils.map(noPrevHIVFUPEncQuarterly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(datimCohortLibrary.experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferInReportingQuarter", ReportUtils.map(transferInReportingQuarter(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport AND (reEnrollmentToHIVReportingQuarter OR experiencedIITPreviousReportingPeriod OR noPrevHIVFUPEncQuarterly) AND NOT transferInReportingQuarter");
        return cd;

    }
/**
 * Patients present in previous period but missing in current period due to death
 */
public CohortDefinition patientsDied() {

    String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and d.discontinuation_reason = 160034 and d.visit_date <= date(:endDate);";

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsDied");
    cd.setQuery(sqlQuery);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setDescription("Patients present in previous period but missing in current period due to death");
    return cd;
}
    /**
     * Patients present in previous period but missing in current period due to Transferring out
     */
    public CohortDefinition patientsTrfOut() {

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and d.discontinuation_reason = 159492 and coalesce(effective_discontinuation_date,d.visit_date) <= date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsTrfOut");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to Transferring out");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period due to stopping treatment
     */
    public CohortDefinition patientStoppedTxPrevMonth() {

        String sqlQuery = "select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =164435 and date(dt.visit_date) between DATE_SUB(date(:endDate),INTERVAL 1 MONTH) and date(:endDate)\n" +
                "union\n" +
                "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.discontinuation_reason =164435 and date(d.visit_date) between DATE_SUB(date(:endDate),INTERVAL 1 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientStoppedTxPrevMonth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to stopping treatment");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period due to stopping treatment
     */
    public CohortDefinition patientStoppedTxPrevQuarter() {

        String sqlQuery = "select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =164435 and date(dt.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "union\n" +
                "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.discontinuation_reason =164435 and date(d.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientStoppedTxPrevQuarter");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to stopping treatment");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period - LTFU
     */
    public CohortDefinition patientsLTFU() {

        String sqlQuery = "select t.patient_id\n" +
                "from(\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "d.patient_id as disc_patient,\n" +
                "d.effective_disc_date as effective_disc_date,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "d.discontinuation_reason,\n" +
                "de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
                "left outer JOIN\n" +
                "(select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date,discontinuation_reason from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(curdate())\n" +
                "group by patient_id\n" +
                "having (\n" +
                "(timestampdiff(DAY,date(latest_tca),date(curdate())) > 30) and (((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) and d.discontinuation_reason = 5240) or d.effective_disc_date is null)\n" +
                "and ((date(latest_vis_date) > date(date_discontinued) and date(latest_tca) > date(date_discontinued) and d.discontinuation_reason = 5240) or disc_patient is null)\n" +
                ")\n" +
                ") t;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsLTFU");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period - LTFU");
        return cd;
    }
    /**
     * Patients included in the current report but not present in the previous reporting period
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousReport AND NOT txCurLinelistForPatientsPresentInCurrentReport");
        return cd;
    }

    /**
     * Patients included in the previous report but not present in the current reporting period due to death - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentDueToDeathReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDied", ReportUtils.map(patientsDied(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientsDied");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to LTFU - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentLTFUReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsLTFU", ReportUtils.map(patientsLTFU(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientsLTFU");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Transfer out - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentTrfOutReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsTrfOut", ReportUtils.map(patientsTrfOut(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientsTrfOut");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to stopping treatment - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxQuarterly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientStoppedTxPrevQuarter", ReportUtils.map(patientStoppedTxPrevQuarter(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientStoppedTxPrevQuarter");
        return cd;
    }
    /**
     * Patients included in the current reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistCurrentReportMonthly() {
        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "     select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "     greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "     greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "     greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "     d.patient_id as disc_patient,\n" +
                "     d.effective_disc_date as effective_disc_date,\n" +
                "     max(d.visit_date) as date_discontinued,\n" +
                "     de.patient_id as started_on_drugs\n" +
                "       from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "     left outer JOIN\n" +
                "       (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "     where date(visit_date) <= date(:endDate) and program_name='HIV' and patient_id\n" +
                "     group by patient_id\n" +
                "       ) d on d.patient_id = fup.patient_id\n" +
                "       where fup.visit_date <= date(:endDate)\n" +
                "       group by patient_id\n" +
                "       having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "     and\n" +
                "       (\n" +
                "       ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and\n" +
                "      ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "       and\n" +
                "       (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "     )\n" +
                "       )t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report");
        return cd;
    }
    /**
     * Patients included in the previous reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistPreviousReportMonthly() {
        String sqlQuery="select pr.patient_id from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "     greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "     greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "     greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "     d.patient_id as disc_patient,\n" +
                "     d.effective_disc_date as effective_disc_date,\n" +
                "     max(d.visit_date) as date_discontinued,\n" +
                "     de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "     left outer JOIN\n" +
                "       (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "        where date(visit_date) <= date_sub(DATE(:endDate), interval 1 MONTH) and program_name='HIV' and patient_id\n" +
                "        group by patient_id\n" +
                "       ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                " and\n" +
                "     (\n" +
                "         ((timestampdiff(DAY,date(latest_tca),date_sub(DATE(:endDate), interval 1 MONTH)) <= 30 or timestampdiff(DAY,date(latest_tca),date_sub(DATE(curdate()), interval 1 MONTH)) <= 30) and\n" +
                "          ((date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 1 MONTH) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "           and\n" +
                "         (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "         )\n" +
                ") pr;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in previous report");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Transfer out - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistCurrentReportMonthly", ReportUtils.map(txCurLinelistCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txCurLinelistPreviousReportMonthly", ReportUtils.map(txCurLinelistPreviousReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistCurrentReportMonthly AND NOT txCurLinelistPreviousReportMonthly");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistCurrentReportMonthly", ReportUtils.map(txCurLinelistCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txCurLinelistPreviousReportMonthly", ReportUtils.map(txCurLinelistPreviousReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistPreviousReportMonthly AND NOT txCurLinelistCurrentReportMonthly");
        return cd;
    }

    /**
     * Patients included in the previous report but not present in the current reporting period due to death - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToDeathMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDied", ReportUtils.map(patientsDied(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientsDied");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to LTFU - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToLTFUMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsLTFU", ReportUtils.map(patientsLTFU(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientsLTFU");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Transfer out - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToTrfOutMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsTrfOut", ReportUtils.map(patientsTrfOut(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientsTrfOut");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Stopping treatment- monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientStoppedTxPrevMonth", ReportUtils.map(patientStoppedTxPrevMonth(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientStoppedTxPrevMonth");
        return cd;
    }
}
