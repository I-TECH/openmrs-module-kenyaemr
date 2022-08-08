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
     * Patients transferred in within the reporting month
     */
    public  CohortDefinition transferInReportingMonth() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where e.patient_type = 160563 and\n" +
                "                coalesce(e.transfer_in_date,e.visit_date) between DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_TI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TI Present in current report but missing in previous report");
        return cd;
    }
    /**
     *Patients transferred in within the quarter
     */
    public  CohortDefinition transferInReportingQuarter() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "coalesce(e.transfer_in_date,e.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "and e.patient_type = 160563;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous_TI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TI Present in current report but missing in previous report");
        return cd;
    }
    /**
     *Patients who returned to care in the reporting month through re-enrollment
     */
    public  CohortDefinition reEnrollmentToHIVReportingMonth() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY) and date(:endDate)\n" +
                "and e.patient_type = 159833;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("reEnrollmentToHIVReportingMonth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Returned to Tx");
        return cd;
    }
    /**
     *Patients who returned to care through re-enrollment within the quarter
     */
    public  CohortDefinition reEnrollmentToHIVReportingQuarter() {
        String sqlQuery="Select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where\n" +
                "e.visit_date between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "and e.patient_type = 159833;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("reEnrollmentToHIVReportingQuarter");
        cd.setQuery(sqlQuery);
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Returned to Tx Present in current report but missing in previous report - had no previous hiv fup encounter");
        return cd;
    }
    /**
     *Newly on ART in the reporting month
     * @return
     */
    public  CohortDefinition newlyOnARTMonthly() {
        String sqlQuery="select e.patient_id from (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e group by e.patient_id having mid(max(concat(date(e.visit_date),e.patient_type)),11) = 164144)e\n" +
                "                    inner join (select d.patient_id,min(d.date_started) as date_started from kenyaemr_etl.etl_drug_event d where d.program= 'HIV'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "              where\n" +
                "                   d.date_started between DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newlyOnARTMonthly");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly on ART in the reporting month");
        return cd;
    }
    /**
     *Patients discontinued with a future effective discontinuation date
     * @return
     */
    public  CohortDefinition discWithFutureEffectiveDate() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where program_name='HIV' and date(visit_date) <= date(:endDate)\n" +
                "                  and date(effective_discontinuation_date) > date(:endDate)\n" +
                "                group by patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("discInReportMonthWithFutureEffectiveDate");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discontinued with a future effective discontinuation date");
        return cd;
    }
    /**
     * Discontinued within the month with a future re-enrollment
     * @return
     */
    public  CohortDefinition discInReportMonthWithFutureReenrollment() {
        String sqlQuery="select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "left join (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where date(e.visit_date) > date(:endDate)) e on d.patient_id = e.patient_id\n" +
                "where d.program_name='HIV' and date(d.visit_date) <= date(:endDate)\n" +
                "  and date(effective_discontinuation_date) between date_sub(DATE(:endDate), interval 1 MONTH) and date(:endDate)\n" +
                "and e.patient_id is not null\n" +
                "group by patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("discInReportMonthWithFutureReenrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discontinued within the month with a future re-enrollment");
        return cd;
    }
    /**
     * Discontinued within the quarter with a future re-enrollment
     * @return
     */
    public  CohortDefinition discInReportQuarterWithFutureReenrollment() {
        String sqlQuery="select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "        left join (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where date(e.visit_date) > date(:endDate)) e on d.patient_id = e.patient_id\n" +
                "        where d.program_name='HIV' and date(d.visit_date) <= date(:endDate)\n" +
                "          and date(effective_discontinuation_date) between DATE_SUB(DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY), interval 2 MONTH) and date(:endDate)\n" +
                "        and e.patient_id is not null\n" +
                "        group by patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("discInReportQuarterWithFutureReenrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discontinued within the quarter with a future re-enrollment");
        return cd;
    }
    /**
     * Discontinued within the past month with a future re-enrollment
     * @return
     */
    public  CohortDefinition discInPastMonthWithFutureReenrollment() {
        String sqlQuery="select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "left join (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where date(e.visit_date) > date_sub(DATE(:endDate), interval 1 MONTH)) e on d.patient_id = e.patient_id\n" +
                "where d.program_name='HIV' and date(d.visit_date) <= date(:endDate)\n" +
                "  and date(effective_discontinuation_date) between date_sub(DATE(:endDate), interval 2 MONTH) and date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "and e.patient_id is not null\n" +
                "group by patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("discInPastMonthWithFutureReenrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discontinued within the past month with a future re-enrollment");
        return cd;
    }
    /**
     * Discontinued within the past quarter with a future re-enrollment
     * @return
     */
    public  CohortDefinition discInPastQuarterWithFutureReenrollment() {
        String sqlQuery="select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "left join (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where date(e.visit_date) > date_sub(DATE(:endDate), interval 3 MONTH)) e on d.patient_id = e.patient_id\n" +
                "where d.program_name='HIV' and date(d.visit_date) <= date(:endDate)\n" +
                "  and date(effective_discontinuation_date) between date_sub(DATE(:endDate), interval 6 MONTH) and date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "and e.patient_id is not null\n" +
                "group by patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("discInPastQuarterWithFutureReenrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discontinued within the past quarter with a future re-enrollment");
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Experienced IIT Previous reporting month");
        return cd;
    }
    /**
     *Patients newly on ART in the reporting quarter
     * @return
     */
    public  CohortDefinition newlyARTReportingQuarter() {
        String sqlQuery="select e.patient_id from (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e group by e.patient_id having mid(max(concat(date(e.visit_date),e.patient_type)),11) = 164144)e\n" +
                "              inner join (select d.patient_id,min(d.date_started) as date_started from kenyaemr_etl.etl_drug_event d where d.program= 'HIV'\n" +
                "                       group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "              where\n" +
                "              d.date_started between DATE_SUB(DATE_SUB(date(:endDate),INTERVAL DAYOFMONTH(date(:endDate))-1 DAY), interval 2 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newlyARTReportingQuarter");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients newly on ART in the reporting quarter");
        return cd;
    }

    /**
     *TX_CURR Present in Current period but missing in previous period - Newly on ART
     * @return
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyNewlyEnrolled() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("newlyOnARTMonthly", ReportUtils.map(newlyOnARTMonthly(), "endDate=${endDate}"));
        cd.setCompositionString("(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly AND newlyOnARTMonthly)");
        return cd;

    }
    /**
     * TX_CURR Present in Current period but missing in previous period - TI
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyTrfIn() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("transferInReportingMonth", ReportUtils.map(transferInReportingMonth(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly AND transferInReportingMonth");
        return cd;

    }
    /**
     * TX_CURR Present in Current period but missing in previous period - Re-enrollment/return To care
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyReEnrollment() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("reEnrollmentToHIVReportingMonth", ReportUtils.map(reEnrollmentToHIVReportingMonth(), "endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingMonth", ReportUtils.map(experiencedIITPreviousReportingMonth(), "endDate=${endDate}"));
        cd.addSearch("noPrevHIVFUPEncMonthly", ReportUtils.map(noPrevHIVFUPEncMonthly(), "endDate=${endDate}"));
        cd.addSearch("transferInReportingMonth", ReportUtils.map(transferInReportingMonth(), "endDate=${endDate}"));
        cd.addSearch("discWithFutureEffectiveDate", ReportUtils.map(discWithFutureEffectiveDate(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly AND (reEnrollmentToHIVReportingMonth OR experiencedIITPreviousReportingMonth OR noPrevHIVFUPEncMonthly OR discWithFutureEffectiveDate) AND NOT transferInReportingMonth");
        return cd;
    }
     /**
     * Patients included in the current report- quarter
     * @return
     */
    public  CohortDefinition txCurLinelistCurrentQuarter() {
        String sqlQuery="select t.patient_id\n" +
                "              from(\n" +
                "                  select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                         greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                         greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                         greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                         d.patient_id as disc_patient,\n" +
                "                         d.effective_disc_date as effective_disc_date,\n" +
                "                         max(d.visit_date) as date_discontinued,\n" +
                "                         de.patient_id as started_on_drugs\n" +
                "                  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                         join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                         join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                         left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                         left outer JOIN\n" +
                "                           (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                            where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                            group by patient_id\n" +
                "                           ) d on d.patient_id = fup.patient_id\n" +
                "                  where fup.visit_date <= date(:endDate)\n" +
                "                  group by patient_id\n" +
                "                  having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                      (\n" +
                "                          ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                            and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                          )\n" +
                "                      )\n" +
                "                  ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_current_report");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TX Curr current quarter");
        return cd;
    }
    /**
     * Patients included in the previous reporting period - quarter
     * @return
     */
    public  CohortDefinition txCurInPreviousQuarter() {
        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "        select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "               greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "               greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "               greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "               d.patient_id as disc_patient,\n" +
                "               d.effective_disc_date as effective_disc_date,\n" +
                "               max(d.visit_date) as date_discontinued,\n" +
                "               de.patient_id as started_on_drugs\n" +
                "        from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "                 left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date_sub(DATE(:endDate), interval 3 MONTH) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "        where fup.visit_date <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "        group by patient_id\n" +
                "        having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "            (\n" +
                "                    ((timestampdiff(DAY,date(latest_tca),date_sub(DATE(:endDate), interval 3 MONTH)) <= 30) and ((date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 3 MONTH) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                    and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                )\n" +
                "            )\n" +
                "    ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_previous_report");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TX Curr previous quarter");
        return cd;
    }

    /**
     * Patients included in the current report but not present in the previous reporting period - quarter
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistCurrentQuarter", ReportUtils.map(txCurLinelistCurrentQuarter(), "endDate=${endDate}"));
        cd.addSearch("discInReportQuarterWithFutureReenrollment", ReportUtils.map(discInReportQuarterWithFutureReenrollment(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistCurrentQuarter AND NOT discInReportQuarterWithFutureReenrollment");
        return cd;
    }
    /**
     * Patients included in the current report but not present in the previous reporting period - quarter
     */
    public CohortDefinition txCurLinelistForPatientsPresentInPreviousReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurInPreviousQuarter", ReportUtils.map(txCurInPreviousQuarter(), "endDate=${endDate}"));
        cd.addSearch("discInPastQuarterWithFutureReenrollment", ReportUtils.map(discInPastQuarterWithFutureReenrollment(), "endDate=${endDate}"));
        cd.setCompositionString("txCurInPreviousQuarter AND NOT discInPastQuarterWithFutureReenrollment");
        return cd;
    }
    /**
     * Patients included in the current report but not present in the previous reporting period - quarter
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentReport(), "endDate=${endDate}"));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousReport(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentReport AND NOT txCurLinelistForPatientsPresentInPreviousReport");
        return cd;
    }
    /**
     *TX_CURR Present in Current period but missing in previous period quarterly- Newly enrolled
     * @return
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousQuarterlyNewlyEnrolledReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "endDate=${endDate}"));
        cd.addSearch("newlyARTReportingQuarter", ReportUtils.map(newlyARTReportingQuarter(), "endDate=${endDate}"));
        cd.addSearch("transferInReportingQuarter", ReportUtils.map(transferInReportingQuarter(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport AND (newlyARTReportingQuarter AND NOT transferInReportingQuarter)");
        return cd;
    }
    /**
     * TX_CURR Present in Current period but missing in previous period quarterly- Transfer in
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportTrfIn() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "endDate=${endDate}"));
        cd.addSearch("transferInReportingQuarter", ReportUtils.map(transferInReportingQuarter(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport AND transferInReportingQuarter");
        return cd;
    }
    /**
     * TX_CURR Present in Current period but missing in previous period - Re-enrollment/return To care
     */
    public CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportReEnrollment() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "endDate=${endDate}"));
        cd.addSearch("reEnrollmentToHIVReportingQuarter", ReportUtils.map(reEnrollmentToHIVReportingQuarter(), "endDate=${endDate}"));
        cd.addSearch("noPrevHIVFUPEncQuarterly", ReportUtils.map(noPrevHIVFUPEncQuarterly(), "endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(datimCohortLibrary.experiencedIITPreviousReportingPeriod(), "endDate=${endDate}"));
        cd.addSearch("transferInReportingQuarter", ReportUtils.map(transferInReportingQuarter(), "endDate=${endDate}"));
        cd.addSearch("discWithFutureEffectiveDate", ReportUtils.map(discWithFutureEffectiveDate(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport AND (reEnrollmentToHIVReportingQuarter OR experiencedIITPreviousReportingPeriod OR noPrevHIVFUPEncQuarterly OR discWithFutureEffectiveDate) AND NOT transferInReportingQuarter");
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
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setDescription("Patients present in previous period but missing in current period due to death");
    return cd;
}
    /**
     * Patients present in previous period but missing in current period due to Transferring out
     */
    public CohortDefinition patientsTrfOut() {
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and d.discontinuation_reason = 159492\n" +
                "                    and coalesce(effective_discontinuation_date,d.visit_date) between DATE_SUB(date(:endDate),INTERVAL 1 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsTrfOut");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to Transferring out");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period due to Transferring out within the quarter
     */
    public CohortDefinition patientsTrfOutQuarter() {
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and d.discontinuation_reason = 159492\n" +
                "                    and coalesce(effective_discontinuation_date,d.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsTrfOutQuarter");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to Transferring out within the quarter");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period due to stopping treatment
     */
    public CohortDefinition patientStoppedTxWithinMonth() {
        String sqlQuery = "select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =164435 and date(dt.visit_date) between DATE_SUB(date(:endDate),INTERVAL 1 MONTH) and date(:endDate)\n" +
                "union\n" +
                "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.discontinuation_reason =164349 and date(d.visit_date) between DATE_SUB(date(:endDate),INTERVAL 1 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientStoppedTxWithinMonth");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to stopping treatment");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period due to stopping treatment
     */
    public CohortDefinition patientStoppedTxWithinQuarter() {
        String sqlQuery = "select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =164435 and date(dt.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate)\n" +
                "union\n" +
                "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.discontinuation_reason =164435 and date(d.visit_date) between DATE_SUB(date(:endDate),INTERVAL 3 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientStoppedTxWithinQuarter");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period due to stopping treatment");
        return cd;
    }
    /**
     * Patients present in previous period but missing in current period - LTFU
     */
    public CohortDefinition patientsLTFU() {
        String sqlQuery = "select t.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,\n" +
                "            date(d.visit_date),\n" +
                "            fup.patient_id,\n" +
                "            max(e.visit_date)                                               as enroll_date,\n" +
                "            greatest(max(e.visit_date),\n" +
                "                     ifnull(max(date(e.transfer_in_date)), '0000-00-00'))   as latest_enrolment_date,\n" +
                "            greatest(max(fup.visit_date),\n" +
                "                     ifnull(max(d.visit_date), '0000-00-00'))               as latest_vis_date,\n" +
                "            max(fup.visit_date)                                             as max_fup_vis_date,\n" +
                "            greatest(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11),\n" +
                "                     ifnull(max(d.visit_date), '0000-00-00'))               as latest_tca, timestampdiff(DAY, date(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11)), date(:endDate)) 'DAYS MISSED',\n" +
                "            mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11) as latest_fup_tca,\n" +
                "            d.patient_id                                                    as disc_patient,\n" +
                "            d.effective_disc_date                                           as effective_disc_date,\n" +
                "            d.visit_date                                                    as date_discontinued,\n" +
                "            d.discontinuation_reason,\n" +
                "            de.patient_id                                                   as started_on_drugs\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "              join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "              join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "              left outer join kenyaemr_etl.etl_drug_event de\n" +
                "                              on e.patient_id = de.patient_id and de.program = 'HIV' and\n" +
                "                                 date(date_started) <= date(curdate())\n" +
                "              left outer JOIN\n" +
                "          (select patient_id,\n" +
                "                  coalesce(max(date(effective_discontinuation_date)), max(date(visit_date))) as visit_date,\n" +
                "                  max(date(effective_discontinuation_date))                                  as effective_disc_date,\n" +
                "                  discontinuation_reason\n" +
                "           from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "           where date(visit_date) <= date(:endDate)\n" +
                "             and program_name = 'HIV'\n" +
                "           group by patient_id\n" +
                "          ) d on d.patient_id = fup.patient_id\n" +
                "     where fup.visit_date <= date(:endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                             date(max_fup_vis_date) <= date(d.effective_disc_date) and\n" +
                "                             date(latest_fup_tca) < date(d.effective_disc_date)\n" +
                "                            and d.discontinuation_reason = 5240\n" +
                "                )\n" +
                " ) t;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsLTFU");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients present in previous period but missing in current period - LTFU");
        return cd;
    }

    /**
     * Patients present in previous period but missing in current period - Undocumented LTFU
     */
    public CohortDefinition patientsUndocumentedLTFU() {
        String sqlQuery = "select t.patient_id\n" +
                "from (\n" +
                "         select fup.visit_date,\n" +
                "                date(d.visit_date),\n" +
                "                fup.patient_id,\n" +
                "                max(e.visit_date)                                               as enroll_date,\n" +
                "                greatest(max(e.visit_date),\n" +
                "                         ifnull(max(date(e.transfer_in_date)), '0000-00-00'))   as latest_enrolment_date,\n" +
                "                greatest(max(fup.visit_date),\n" +
                "                         ifnull(max(d.visit_date), '0000-00-00'))               as latest_vis_date,\n" +
                "                max(fup.visit_date)                                             as max_fup_vis_date,\n" +
                "                greatest(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11),\n" +
                "                         ifnull(max(d.visit_date), '0000-00-00'))               as latest_tca, timestampdiff(DAY, date(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11)), date(:endDate)) 'DAYS MISSED',\n" +
                "                mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11) as latest_fup_tca,\n" +
                "                d.patient_id                                                    as disc_patient,\n" +
                "                d.effective_disc_date                                           as effective_disc_date,\n" +
                "                d.visit_date                                                    as date_discontinued,\n" +
                "                d.discontinuation_reason,\n" +
                "                de.patient_id                                                   as started_on_drugs\n" +
                "         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de\n" +
                "                                  on e.patient_id = de.patient_id and de.program = 'HIV' and\n" +
                "                                     date(date_started) <= date(curdate())\n" +
                "                  left outer JOIN\n" +
                "              (select patient_id,\n" +
                "                      coalesce(max(date(effective_discontinuation_date)), max(date(visit_date))) as visit_date,\n" +
                "                      max(date(effective_discontinuation_date))                                  as effective_disc_date,\n" +
                "                      discontinuation_reason\n" +
                "               from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= date(:endDate)\n" +
                "                 and program_name = 'HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "         where fup.visit_date <= date(:endDate)\n" +
                "         group by patient_id\n" +
                "         having (\n" +
                "\n" +
                "                        (timestampdiff(DAY, date(latest_fup_tca), date(:endDate)) > 30) and\n" +
                "                        (\n" +
                "                                (date(enroll_date) >= date(d.visit_date) and\n" +
                "                                 date(max_fup_vis_date) >= date(d.visit_date) and\n" +
                "                                 date(latest_fup_tca) > date(d.visit_date))\n" +
                "                                or disc_patient is null\n" +
                "                                or (d.discontinuation_reason not in (159492,160034,5240,819,164349) or d.discontinuation_reason is null))\n" +
                "                    )\n" +
                "     ) t;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsUndocumentedLTFU");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients undocumented LTFU");
        return cd;
    }
    /**
     * Patients included in the current report but not present in the previous reporting period
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInCurrentReport(), "endDate=${endDate}"));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousReport(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousReport AND NOT txCurLinelistForPatientsPresentInCurrentReport");
        return cd;
    }

    /**
     * Patients included in the previous report but not present in the current reporting period due to death - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentDueToDeathReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "endDate=${endDate}"));
        cd.addSearch("patientsDied", ReportUtils.map(patientsDied(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientsDied");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to LTFU - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentLTFUReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "endDate=${endDate}"));
        cd.addSearch("patientsLTFU", ReportUtils.map(patientsLTFU(), "endDate=${endDate}"));
        cd.addSearch("patientsUndocumentedLTFU", ReportUtils.map(patientsUndocumentedLTFU(), "endDate=${endDate}"));
        cd.addSearch("patientsTrfOutQuarter", ReportUtils.map(patientsTrfOutQuarter(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND ((patientsLTFU OR patientsUndocumentedLTFU) AND NOT patientsTrfOutQuarter)");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Transfer out - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentTrfOutReport() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "endDate=${endDate}"));
        cd.addSearch("patientsTrfOutQuarter", ReportUtils.map(patientsTrfOutQuarter(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientsTrfOutQuarter");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to stopping treatment - Quarterly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxQuarterly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "endDate=${endDate}"));
        cd.addSearch("patientStoppedTxPrevQuarter", ReportUtils.map(patientStoppedTxWithinQuarter(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport AND patientStoppedTxPrevQuarter");
        return cd;
    }
    /**
     * Patients included in the current reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistCurrentMonthly() {
        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "        select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "               greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "               greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "               greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "               d.patient_id as disc_patient,\n" +
                "               d.effective_disc_date as effective_disc_date,\n" +
                "               max(d.visit_date) as date_discontinued,\n" +
                "               de.patient_id as started_on_drugs\n" +
                "        from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                 left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV' and patient_id\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "        where fup.visit_date <= date(:endDate)\n" +
                "        group by patient_id\n" +
                "        having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "           and\n" +
                "            (\n" +
                "                    ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30) and\n" +
                "                     (date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date) or d.effective_disc_date is null))\n" +
                "                    and\n" +
                "                    (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                )\n" +
                "    )t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("txCurLinelistCurrentReportMonthly");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report");
        return cd;
    }
    /**
     * Patients included in the previous reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurPrevReportMonthly() {
        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "        select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "               greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "               greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "               greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "               d.patient_id as disc_patient,\n" +
                "               d.effective_disc_date as effective_disc_date,\n" +
                "               max(d.visit_date) as date_discontinued,\n" +
                "               de.patient_id as started_on_drugs\n" +
                "        from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "                 left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date_sub(DATE(:endDate), interval 1 MONTH) and program_name='HIV' and patient_id\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "        where fup.visit_date <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "        group by patient_id\n" +
                "        having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "           and\n" +
                "            (\n" +
                "                    ((timestampdiff(DAY,date(latest_tca),date_sub(DATE(:endDate), interval 1 MONTH)) <= 30) and\n" +
                "                     (date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 1 MONTH) or date(enroll_date) > date(d.effective_disc_date) or d.effective_disc_date is null))\n" +
                "                    and\n" +
                "                    (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                )\n" +
                "    )t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in previous report");
        return cd;
    }
    /**
     * Tx curr current month
     * @return
     */
    public  CohortDefinition txCurLinelistMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistCurrentMonthly", ReportUtils.map(txCurLinelistCurrentMonthly(), "endDate=${endDate}"));
        cd.addSearch("discInReportMonthWithFutureReenrollment", ReportUtils.map(discInReportMonthWithFutureReenrollment(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistCurrentMonthly AND NOT discInReportMonthWithFutureReenrollment");
        return cd;
    }

    /**
     * TX curr previous month
     * @return
     */
    public  CohortDefinition txCurLinelistMonthlyPrev() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurPrevReportMonthly", ReportUtils.map(txCurPrevReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("discInPastMonthWithFutureReenrollment", ReportUtils.map(discInPastMonthWithFutureReenrollment(), "endDate=${endDate}"));
        cd.setCompositionString("txCurPrevReportMonthly AND NOT discInPastMonthWithFutureReenrollment");
        return cd;
    }
    /**
     * Patients included in the current report but not present in the previous reporting period- Monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistMonthly", ReportUtils.map(txCurLinelistMonthly(), "endDate=${endDate}"));
        cd.addSearch("txCurLinelistMonthlyPrev", ReportUtils.map(txCurLinelistMonthlyPrev(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistMonthly AND NOT txCurLinelistMonthlyPrev");
        return cd;
    }

    /**
     * Patients included in the previous report but not present in the current reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistMonthlyPrev", ReportUtils.map(txCurLinelistMonthlyPrev(), "endDate=${endDate}"));
        cd.addSearch("txCurLinelistMonthly", ReportUtils.map(txCurLinelistMonthly(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistMonthlyPrev AND NOT txCurLinelistMonthly");
        return cd;
    }

    /**
     * Patients included in the previous report but not present in the current reporting period due to death - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToDeathMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("patientsDied", ReportUtils.map(patientsDied(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientsDied");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to LTFU - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToLTFUMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("patientsLTFU", ReportUtils.map(patientsLTFU(), "endDate=${endDate}"));
        cd.addSearch("patientsUndocumentedLTFU", ReportUtils.map(patientsUndocumentedLTFU(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND (patientsLTFU OR patientsUndocumentedLTFU)");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Transfer out - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToTrfOutMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("patientsTrfOut", ReportUtils.map(patientsTrfOut(), "endDate=${endDate}"));
        cd.addSearch("patientsLTFU", ReportUtils.map(patientsLTFU(), "endDate=${endDate}"));
        cd.addSearch("patientsUndocumentedLTFU", ReportUtils.map(patientsUndocumentedLTFU(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientsTrfOut AND NOT (patientsLTFU OR patientsUndocumentedLTFU)");
        return cd;
    }
    /**
     * Patients included in the previous report but not present in the current reporting period due to Stopping treatment- monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxMonthly() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly", ReportUtils.map(txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "endDate=${endDate}"));
        cd.addSearch("patientStoppedTxPrevMonth", ReportUtils.map(patientStoppedTxWithinMonth(), "endDate=${endDate}"));
        cd.setCompositionString("txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly AND patientStoppedTxPrevMonth");
        return cd;
    }
}
