/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.publicHealthActionReport;

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
 * Library of cohort definitions for public health action
 */
@Component
public class PublicHealthActionCohortLibrary {

    @Autowired
    private DatimCohortLibrary datimCohortLibrary;

    /**
     * Clients currently on ART with no current VL results- This Query is the same as one in Viral suppression report but uses current date instead of end date.
     * Should be updated when the counterpart in viral suppression report is updated
     * @return
     */
    public CohortDefinition noRecentVLResults() {
        String sqlQuery = "select vl.patient_id\n" +
                "from (select b.patient_id,\n" +
                "             max(b.visit_date)                                                                    as vl_date,\n" +
                "             date_sub(date(:endDate), interval 12 MONTH),\n" +
                "             mid(max(concat(b.visit_date, b.lab_test)), 11)                                       as lab_test,\n" +
                "             if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 856, mid(max(concat(b.visit_date, b.test_result)), 11),\n" +
                "                if(\n" +
                "                                mid(max(concat(b.visit_date, b.lab_test)), 11) = 1305 and\n" +
                "                                mid(max(concat(visit_date, test_result)), 11) = 1302, 'LDL', '')) as vl_result,\n" +
                "             mid(max(concat(b.visit_date, b.urgency)), 11)                                        as urgency\n" +
                "      from (select x.patient_id  as patient_id,\n" +
                "                   x.visit_date  as visit_date,\n" +
                "                   x.lab_test    as lab_test,\n" +
                "                   x.test_result as test_result,\n" +
                "                   urgency       as urgency\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "            group by x.patient_id, x.visit_date\n" +
                "            order by visit_date desc) b\n" +
                "      group by patient_id\n" +
                "      having max(visit_date) < date_sub(date(:endDate), interval 12 MONTH)) vl;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("noRecentVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ANo recent VL results");
        return cd;
    }
    public CohortDefinition noCurrentVLResults() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("noRecentVLResults", ReportUtils.map(noRecentVLResults(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txcurr AND noRecentVLResults");
        return cd;
    }

    /**
     * Number of HIV+ patients not linked to care
     * @return
     */
    public CohortDefinition notLinked() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t.patient_id from kenyaemr_etl.etl_hts_test t left join\n" +
                "   (select l.patient_id, l.ccc_number from kenyaemr_etl.etl_hts_referral_and_linkage l where date(l.visit_date) <= date(:endDate) group by l.patient_id) l on t.patient_id = l.patient_id\n" +
                "left join (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where date(e.visit_date) <= date(:endDate))e on e.patient_id = t.patient_id\n" +
                "where t.final_test_result='Positive' and t.test_type = 2 and date(t.visit_date) between date(:startDate) and date(:endDate) and l.ccc_number is null and e.patient_id is null;";
        cd.setName("notLinked");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV+ patients not linked to care");
        return cd;
    }

    /**
     * Number of HEIs aged between 6 and 24 weeks
     * @return
     */
    public CohortDefinition allHEIsAgedBetween6And24Weeks() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "and timestampdiff(WEEK,d.dob,date(:endDate)) between 6 and 96;";
        cd.setName("allHEIsAgedBetween6And24Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of HEIs aged between 6 and 24 weeks");
        return cd;
    }

    /**
     * Children aged between 6 and 24 weeks enrolled in HIV
     * @return
     */
    public CohortDefinition childrenBetween6And24WeeksInHIVProgram() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = " select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "    where date(visit_date) <= date(:endDate) and timestampdiff(WEEK,d.dob,date(:endDate)) between 6 and 96;";
        cd.setName("childrenBetween6And24WeeksInHIVProgram");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of HEIs aged between 6 and 24 weeks in HIV program");
        return cd;
    }
    /**
     * Number of HEIs with a HIV test result
     * @return
     */
    public CohortDefinition allHEIsWithAHIVTestResult() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_hei_follow_up_visit v where v.dna_pcr_result is not null or v.first_antibody_result is not null or\n" +
                "v.final_antibody_result is not null group by v.patient_id;";
        cd.setName("allHEIsWithAHIVTestResult");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of HEIs with a HIV test result");
        return cd;
    }

    /**
     * HEIs aged between 6 and 24 weeks with no documented HIV result
     * @return
     */
    public CohortDefinition undocumentedHEIStatus() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("allHEIsAgedBetween6And24Weeks", ReportUtils.map(allHEIsAgedBetween6And24Weeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("allHEIsWithAHIVTestResult", ReportUtils.map(allHEIsWithAHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("childrenBetween6And24WeeksInHIVProgram", ReportUtils.map(childrenBetween6And24WeeksInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(allHEIsAgedBetween6And24Weeks AND NOT allHEIsWithAHIVTestResult) AND NOT childrenBetween6And24WeeksInHIVProgram");
        return cd;
    }

    /**
     * Number of ART patients with no current vl result
     * Valid means VL was taken <= 12 months ago and invalid means VL was taken > 12 months ago
     * @return
     */
    public CohortDefinition invalidVL() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("invalidVl", ReportUtils.map(noCurrentVLResults(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("invalidVl");
        return cd;
    }

    /**
     * Number of ART patients with invalid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     */

    public CohortDefinition invalidUnsuppressedVL() {
        String query = "select vl.patient_id\n" +
                "from (select b.patient_id,\n" +
                "             max(b.visit_date)                                                       as vl_date,\n" +
                "             date_sub(date(:endDate), interval 12 MONTH),\n" +
                "             mid(max(concat(b.visit_date, b.lab_test)), 11)                          as lab_test,\n" +
                "             if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 856, mid(max(concat(b.visit_date, b.test_result)), 11),\n" +
                "                if(mid(max(concat(b.visit_date, b.lab_test)), 11) = 1305 and\n" +
                "                   mid(max(concat(visit_date, test_result)), 11) = 1302, \"LDL\", \"\")) as vl_result,\n" +
                "             mid(max(concat(b.visit_date, b.urgency)), 11)                           as urgency\n" +
                "      from (select x.patient_id  as patient_id,\n" +
                "                   x.visit_date  as visit_date,\n" +
                "                   x.lab_test    as lab_test,\n" +
                "                   x.test_result as test_result,\n" +
                "                   urgency       as urgency\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "            group by x.patient_id, x.visit_date\n" +
                "            order by visit_date desc) b\n" +
                "      group by patient_id\n" +
                "      having max(visit_date) <\n" +
                "             date_sub(date(:endDate), interval 12 MONTH)) vl\n" +
                "where vl_result >= 200;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unsuppressedWithoutValidVL");
        cd.setQuery(query);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unsuppressed without current VL");
        return cd;
    }
    public CohortDefinition unsuppressedWithoutValidVL() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("invalidUnsuppressedVL", ReportUtils.map(invalidUnsuppressedVL(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr and invalidUnsuppressedVL");
        return cd;
    }
    /**
     * Number of undocumented LTFU patients
     * @return
     */
    public CohortDefinition undocumentedLTFU() {
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
                "                        (timestampdiff(DAY, date(latest_fup_tca), date(:startDate)) <= 30) and\n" +
                "                        (timestampdiff(DAY, date(latest_fup_tca), date(:endDate)) > 30) and\n" +
                "                        (\n" +
                "                                (date(enroll_date) >= date(d.visit_date) and\n" +
                "                                 date(max_fup_vis_date) >= date(d.visit_date) and\n" +
                "                                 date(latest_fup_tca) > date(d.visit_date))\n" +
                "                                or disc_patient is null)\n" +
                "                    )\n" +
                "     ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("undocumentedLTFU");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Undocumented LTFU patients");
        return cd;
    }

    /**
     * Number of patients who missed HIV appointments/Recent defaulters
     * @return
     */
    public CohortDefinition recentDefaulters() {
        String queryString = "select t.patient_id \n" +
                "                from( \n" +
                "                select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date, \n" +
                "                greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date, \n" +
                "                greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date, \n" +
                "                greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                       greatest(mid(max(concat(fup.visit_date, fup.refill_date)), 11),\n" +
                "                                ifnull(max(d.visit_date), '0000-00-00'))                                                        as refill_tca,\n" +
                "                d.patient_id as disc_patient, \n" +
                "                d.effective_disc_date as effective_disc_date, \n" +
                "                max(d.visit_date) as date_discontinued, \n" +
                "                d.discontinuation_reason, \n" +
                "                de.patient_id as started_on_drugs,\n" +
                "                       k.latest_fast_track_visit_date\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                         left join (select k.patient_id, max(k.visit_date) as latest_fast_track_visit_date\n" +
                "                                    from kenyaemr_etl.etl_art_fast_track k\n" +
                "                                    group by k.patient_id) k on fup.patient_id = k.patient_id\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id \n" +
                "                join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id \n" +
                "                left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate) \n" +
                "                left outer JOIN \n" +
                "                (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date,discontinuation_reason from kenyaemr_etl.etl_patient_program_discontinuation \n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV' \n" +
                "                group by patient_id \n" +
                "                ) d on d.patient_id = fup.patient_id \n" +
                "                where fup.visit_date <= date(:endDate) \n" +
                "                group by patient_id \n" +
                "                having (((timestampdiff(DAY, date(refill_tca), date(:endDate)) between 1 and 30 and\n" +
                "                         refill_tca > latest_vis_date and\n" +
                "                         (latest_fast_track_visit_date is null or latest_fast_track_visit_date > date(:endDate))) or\n" +
                "                (timestampdiff(DAY,date(latest_tca),date(:endDate)) between 1 and 30)) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null)\n" +
                "                and (date(latest_vis_date) > date(date_discontinued) and date(latest_tca) > date(date_discontinued) or disc_patient is null) \n" +
                "                ) \n" +
                "                ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("recentDefaulters");
        cd.setQuery(queryString);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Missed appointment");
        return cd;
    }

    /**
     * HEIs not linked to mothers
     * @return
     */
    public CohortDefinition unlinkedHEI() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("allHEIsUnder24MonthsOld", ReportUtils.map(allHEIsUnder24MonthsOld(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("allHEIsLinkedToMothers", ReportUtils.map(allHEIsLinkedToMothers(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("allHEIsLinkedToMothersInHEIEnrolment", ReportUtils.map(allHEIsLinkedToMothersInHEIEnrolment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("allHEIsUnder24MonthsOld AND NOT (allHEIsLinkedToMothers OR allHEIsLinkedToMothersInHEIEnrolment) ");
        return cd;
    }

    /**
     * HEIs who have missed HIV Tests
     * @return
     */
    public CohortDefinition heiMissedHIVTests() {
        String sqlQuery = "select e.patient_id\n" +
                "         from kenyaemr_etl.etl_hei_enrollment e\n" +
                "                inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "             left join kenyaemr_etl.etl_hiv_enrollment hiv on e.patient_id = hiv.patient_id\n" +
                "                left join (select p.patient_id from kenyaemr_etl.etl_patient_program_discontinuation p where p.program_name = 'MCH Child HEI') p\n" +
                "                          on e.patient_id = p.patient_id\n" +
                "                left join (select x.patient_id week6pcr, x.test_result as week6results\n" +
                "                           from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                           where x.lab_test = 1030\n" +
                "                             and x.order_reason = 1040) a on e.patient_id = a.week6pcr\n" +
                "                left join (select x.patient_id month6pcr, x.test_result as month6results\n" +
                "                           from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                           where x.lab_test = 1030\n" +
                "                             and x.order_reason = 1326) b on e.patient_id = b.month6pcr\n" +
                "                left join (select x.patient_id month12pcr, x.test_result as month12results\n" +
                "                           from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                           where x.lab_test = 1030\n" +
                "                             and x.order_reason = 844) c on e.patient_id = c.month12pcr\n" +
                "                left join (select x.patient_id month18AB, x.test_result as month18results\n" +
                "                           from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                           where x.lab_test = 163722\n" +
                "                             and x.order_reason = 164860) f on e.patient_id = f.month18AB\n" +
                "                left join (select f.patient_id as ceased_bf_hei,\n" +
                "                                  left(max(concat(date(f.visit_date), f.infant_feeding)), 10) as bf_date,\n" +
                "                                  mid(max(concat(date(f.visit_date), f.infant_feeding)), 11)  as latest_infant_feeding,\n" +
                "                                  x.patient_id                                                as cessation_bf_tested_hei\n" +
                "                           from kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "                                    left join (select x.patient_id\n" +
                "                                               from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                               where x.lab_test = 163722\n" +
                "                                                 and x.order_reason = 164460) x\n" +
                "                                              on x.patient_id = f.patient_id\n" +
                "                           group by f.patient_id) g\n" +
                "                          on e.patient_id = g.ceased_bf_hei\n" +
                "                left join (select x0.patient_id positive_hei,\n" +
                "                                  x0.visit_date date_tested_postive,\n" +
                "                                  x.confirmed_hei,\n" +
                "                                  x.confirmatoryresults,\n" +
                "                                  x.confirmatory_results\n" +
                "                           from kenyaemr_etl.etl_laboratory_extract x0\n" +
                "                                    left join\n" +
                "                                (select x.patient_id             confirmed_hei,\n" +
                "                                        x.date_test_requested as confirmatory_results,\n" +
                "                                        x.test_result         as confirmatoryresults\n" +
                "                                 from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                 where x.lab_test = 163722\n" +
                "                                   and x.order_reason = 164860) x on x0.patient_id = x.confirmed_hei\n" +
                "                           where x0.lab_test in (1030, 163722)\n" +
                "                             and x0.order_reason in (1040, 1326, 844, 164860)\n" +
                "                             and x0.test_result = 703) i on e.patient_id = g.ceased_bf_hei\n" +
                "         where timestampdiff(MONTH,date(d.DOB),date(current_date)) <= 24\n" +
                "         and ((timestampdiff(WEEK, date(d.dob), date(current_date)) >= 6 and a.week6pcr is null) or\n" +
                "              (timestampdiff(MONTH, date(d.dob), date(current_date)) >= 6 and b.month6pcr is null) or\n" +
                "              (timestampdiff(MONTH, date(d.dob), date(current_date)) >= 12 and c.month12pcr is null)\n" +
                "           or (timestampdiff(MONTH, date(d.dob), date(current_date)) >= 18 and f.month18AB is null) or\n" +
                "              (timestampdiff(WEEK, date(g.bf_date), date(current_date)) >= 6 and g.latest_infant_feeding = 164478 and\n" +
                "               g.cessation_bf_tested_hei is null)\n" +
                "           or (i.positive_hei is not null and i.confirmed_hei is null))\n" +
                "         and p.patient_id is null\n" +
                "         and hiv.patient_id is null\n" +
                "         group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("heiMissedHIVTests");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs who have missed HIV Tests");
        return cd;
    }
    /**
     * HEIs under 24 months old
     * @return
     */
    public CohortDefinition allHEIsUnder24MonthsOld() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id and timestampdiff(MONTH,d.dob,date(:endDate)) <= 24;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allHEIsUnder24MonthsOld");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs under 24 months old");
        return cd;
    }

    /**
     * All HEIs linked to mothers in HEI enrolment
     * @return
     */
    public CohortDefinition allHEIsLinkedToMothersInHEIEnrolment() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics d on d.unique_patient_no = e.parent_ccc_number and d.gender = 'F'\n" +
                "where e.parent_ccc_number is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allHEIsLinkedToMothersInHEIEnrolment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs linked to mothers in HEI enrolment");
        return cd;
    }

    /**
     * HEIs linked to Mothers
     * @return
     */
    public CohortDefinition allHEIsLinkedToMothers() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "         left join relationship r on e.patient_id = r.person_b\n" +
                "         inner join (select d.patient_id, d.gender from kenyaemr_etl.etl_patient_demographics d where d.gender = 'F') m\n" +
                "                    on m.patient_id = r.person_a\n" +
                "         inner join relationship_type t\n" +
                "                    on r.relationship = t.relationship_type_id and t.uuid = '8d91a210-c2cc-11de-8d13-0010c6dffd0f';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allHEIsLinkedToMothers");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs linked to Mothers");
        return cd;
    }

    /**
     * Adolescents not in OTZ
     * @return
     */
    public CohortDefinition adolescentsNotInOTZSql() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,date(:endDate)) between 10 and  19\n" +
                "left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "   group by patient_id\n" +
                "   ) d on d.patient_id = enr.patient_id\n" +
                "where enr.patient_id not in (select ot.patient_id from kenyaemr_etl.etl_otz_enrollment ot) and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsNotInOTZ");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents not in OTZ");
        return cd;
    }

    /**
     * Current on ART adolescents not enrolled in OTZ
     * @return
     */
    public CohortDefinition adolescentsNotInOTZ() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("adolescentsNotInOTZSql", ReportUtils.map(adolescentsNotInOTZSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnART", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnART AND adolescentsNotInOTZSql");
        return cd;
    }
    /**
     * Children and adolescents living with HIV not in OVC
     * @return
     */
    public CohortDefinition calhivNotInOVCSql() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "  join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,date(:endDate)) <= 17\n" +
                "  left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = enr.patient_id\n" +
                "where enr.patient_id not in (select ov.patient_id from kenyaemr_etl.etl_ovc_enrolment ov) and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("calhivNotInOVC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CALHIV not in OVC");
        return cd;
    }

    /**
     * Current on ART CALHIV not in OVC
     * @return
     */
    public CohortDefinition calhivNotInOVC() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("calhivNotInOVCSql", ReportUtils.map(calhivNotInOVCSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnART", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnART AND calhivNotInOVCSql");
        return cd;
    }
    /**
     * Patients on DTG regimen
     * @return
     */
    public CohortDefinition patientsOnDTGRegimen() {
        String sqlQuery = "select de.patient_id from kenyaemr_etl.etl_drug_event de where de.program='HIV' and de.discontinued is null\n" +
                "group by de.patient_id having mid(max(concat(de.visit_date,de.regimen)),11) like ('%DTG%');";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsOnDTGRegimen");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on DTG");
        return cd;
    }
    /**
     * Not eligible for DTG regimen.
     * Criteria: Less than 1 month old and weight less than 3 kgs
     */
    public CohortDefinition ineligibleForDTGRegimen() {
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_demographics d left join (select t.patient_id,mid(max(concat(t.visit_date,t.weight)),11) as weight from kenyaemr_etl.etl_patient_triage t\n" +
                "                where date(t.visit_date) <= date(:endDate) group by t.patient_id)t on d.patient_id = t.patient_id\n" +
                "              where date(d.dob) <= date(:endDate) and timestampdiff(DAY, date(d.DOB), date(:endDate)) < 30 and (t.patient_id is null or t.weight < 3)\n" +
                "              group by d.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ineligibleForDTGRegimen");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not eligible for DTG");
        return cd;
    }
    /**
     * Children and adolescents living with HIV
     * @return
     */
    public CohortDefinition calhivCohort() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "                            join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(MONTH,dm.DOB,date(:endDate)) >= 1 and timestampdiff(YEAR,dm.DOB,date(:endDate)) <= 14\n" +
                "                            left join\n" +
                "                            (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                            where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                            group by patient_id\n" +
                "                            ) d on d.patient_id = enr.patient_id\n" +
                "                          where d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("calhivCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Children and adolescents living with HIV");
        return cd;
    }
    /**
     * Children and adolescents living with HIV not on DTG.
     * Must be current on ART
     * @return
     */
    public CohortDefinition calhivNotOnDTGRegimen() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentlyOnART", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("calhivCohort", ReportUtils.map(calhivCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsOnDTGRegimen", ReportUtils.map(patientsOnDTGRegimen(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ineligibleForDTGRegimen", ReportUtils.map(ineligibleForDTGRegimen(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnART AND (calhivCohort AND NOT patientsOnDTGRegimen) AND NOT ineligibleForDTGRegimen");
        return cd;
    }
    /**
     * Sexual Contacts with undocumented HIV status - Includes spouse, sexual partner, co-wife
     * @return
     */
    public CohortDefinition contactsUndocumentedHIVStatus() {
        String sqlQuery = "select pc.id\n" +
                "from openmrs.kenyaemr_hiv_testing_patient_contact pc\n" +
                "         inner join openmrs.patient p on p.patient_id = pc.patient_related_to and p.voided = 0\n" +
                "left join (select ht.patient_id, mid(max(concat(date(ht.visit_date), ht.final_test_result)), 11) as hiv_status\n" +
                " from kenyaemr_etl.etl_hts_test ht\n" +
                " group by ht.patient_id\n" +
                " having hiv_status in ('Negative', 'Positive'))\n" +
                "ht on ht.patient_id = pc.patient_id\n" +
                "             where (pc.baseline_hiv_status is null or pc.baseline_hiv_status in ('Unknown','1067')) and pc.relationship_type in (162221,163565,5617) and date(pc.date_created) <= date(:endDate)\n" +
                "               and pc.voided = 0 and ht.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("contactsUndocumentedHIVStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Contacts with undocumented HIV status");
        return cd;
    }
    /**
     * Children contacts with undocumented HIV Status
     * @return
     */
    public CohortDefinition childrenContactsUndocumentedHIVStatus() {
        String sqlQuery = "select c.id\n" +
                "from (select pc.id,\n" +
                "             pc.patient_id,\n" +
                "             pc.patient_related_to,\n" +
                "             h.patient_id  as hei,\n" +
                "             pc.baseline_hiv_status,\n" +
                "             pc.relationship_type,\n" +
                "             pc.date_created,\n" +
                "             pc.voided,\n" +
                "             ht.patient_id as tested_contact\n" +
                "      from openmrs.kenyaemr_hiv_testing_patient_contact pc\n" +
                "               inner join openmrs.patient p on p.patient_id = pc.patient_related_to and p.voided = 0\n" +
                "               left join (select ht.patient_id,\n" +
                "                                 mid(max(concat(date(ht.visit_date), ht.final_test_result)), 11) as hiv_status\n" +
                "                          from kenyaemr_etl.etl_hts_test ht\n" +
                "                          group by ht.patient_id\n" +
                "                          having hiv_status in ('Negative', 'Positive')) ht on ht.patient_id = pc.patient_id\n" +
                "               left join (select e.patient_id, e.hiv_status_at_exit\n" +
                "                          from kenyaemr_etl.etl_hei_enrollment e\n" +
                "                                   inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "                              and timestampdiff(WEEK, d.dob, date(:endDate)) between 6 and 96) h\n" +
                "                         on pc.patient_id = h.patient_id\n" +
                "     ) c\n" +
                "where\n" +
                "  date(c.date_created) <= date(:endDate)\n" +
                "  and c.hei is null\n" +
                "  and (c.baseline_hiv_status is null or c.baseline_hiv_status in ('Unknown','1067'))\n" +
                "  and c.relationship_type = 1528\n" +
                "  and c.voided = 0\n" +
                "  and c.tested_contact is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("contactsUndocumentedHIVStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Contacts with undocumented HIV status");
        return cd;
    }

    /**
     * Number of SNS Contacts with undocumented HIV status
     */
    public CohortDefinition snsContactsUndocumentedHIVStatus() {
        String sqlQuery = "select pc.id\n" +
                "from kenyaemr_hiv_testing_patient_contact pc\n" +
                "         inner join patient p on p.patient_id = pc.patient_related_to and p.voided = 0\n" +
                "left join (select ht.patient_id, mid(max(concat(date(ht.visit_date), ht.final_test_result)), 11) as hiv_status\n" +
                " from kenyaemr_etl.etl_hts_test ht\n" +
                " group by ht.patient_id\n" +
                " having hiv_status in ('Negative', 'Positive'))\n" +
                "ht on ht.patient_id = pc.patient_id\n" +
                "             where (pc.baseline_hiv_status is null or pc.baseline_hiv_status in ('Unknown','1067')) and pc.relationship_type = 166606 and date(pc.date_created) <= date(:endDate)\n" +
                "               and pc.voided = 0 and ht.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("snsContactsUndocumentedHIVStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of SNS Contacts with undocumented HIV status");
        return cd;
    }

    /**
     * Ever enrolled Clients without NUPI
     * @return
     */
    public CohortDefinition clientsWithoutNUPI() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "  join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id\n" +
                "  left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = enr.patient_id\n" +
                "where dm.national_unique_patient_identifier is null and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("clientsWithoutNUPI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients without NUPI");
        return cd;
    }

    /**
     * Ever enrolled Clients NUPI
     * @return
     */
    public CohortDefinition clientsWithNUPI() {
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_demographics d where d.national_unique_patient_identifier is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("clientsWithNUPI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with NUPI");
        return cd;
    }

    /**
     * TX_CURR  Clients without NUPI
     * @return
     */
    public CohortDefinition txCurrclientsWithoutNUPI() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("clientsWithNUPI", ReportUtils.map(clientsWithNUPI(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND NOT clientsWithNUPI");
        return cd;
    }

    /**
     * Number of patients who died withing the past 3 months
     * @return
     */
    public CohortDefinition numberOfDeaths() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where program_name='HIV' and discontinuation_reason = 160034\n" +
                "                  and coalesce(date(date_died),date(effective_discontinuation_date),date(visit_date)) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("numberOfDeaths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients who died");
        return cd;
    }

    public CohortDefinition partiallyVaccinated() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment group by patient_id\n"
                + "        having mid(max(concat(visit_date,final_vaccination_status)),11) = 166192\n"
                + "        and max(visit_date) <= date(CURRENT_DATE());";
        cd.setName("partiallyVaccinated;");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("partiallyVaccinated");

        return cd;
    }


    public static CohortDefinition fullyVaccinated() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment a where a.final_vaccination_status = 5585 and a.visit_date <= date(CURRENT_DATE());";
        cd.setName("fullyVaccinated");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("fullyVaccinated");
        return cd;
    }

    public CohortDefinition covid19AssessedPatients() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from kenyaemr_etl.etl_covid19_assessment a;";
        cd.setName("covid19AssessedPatients;");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("covid19AssessedPatients");
        return cd;
    }

    // Covid vaccine age for now is 15+ , this can be adjusted accordingly depending on the recommended age limits
    public CohortDefinition covidVaccineAgeCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_patient_demographics where timestampdiff(YEAR ,dob,date(CURRENT_DATE()))>= 15;";
        cd.setName("covidVaccineAgeCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("covidVaccineAgeCohort");
        return cd;
    }

    /**
     * Clients on ART nad not vaccinated for Covid-19
     * @return
     */
    public CohortDefinition notVaccinatedForCovid19() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("partiallyVaccinated",
                ReportUtils.map(partiallyVaccinated(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("fullyVaccinated", ReportUtils.map(fullyVaccinated(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("covidVaccineAgeCohort", ReportUtils.map(covidVaccineAgeCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND covidVaccineAgeCohort AND NOT (partiallyVaccinated OR fullyVaccinated)");
        return cd;
    }

    /**
     * Clients on ART nad not vaccinated for Covid-19
     * @return
     */
    public CohortDefinition notAssessedForCovid19() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("covid19AssessedPatients",
                ReportUtils.map(covid19AssessedPatients(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("covidVaccineAgeCohort", ReportUtils.map(covidVaccineAgeCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND covidVaccineAgeCohort AND NOT covid19AssessedPatients");
        return cd;
    }
}
