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
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.springframework.stereotype.Component;

/**
 * Library of cohort definitions for public health action
 */
@Component
public class PublicHealthActionCohortLibrary {

    /**
     * Clients currently on ART - This Query is the same as one datim but uses current date instead of end date.
     * Should be updated when the counterpart in datim is updated
     * @return
     */
    public CohortDefinition currentlyOnArt() {
        String sqlQuery = "select t.patient_id\n" +
                "            from(\n" +
                "                select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                       greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                       greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                       greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                       d.patient_id as disc_patient,\n" +
                "                       d.effective_disc_date as effective_disc_date,\n" +
                "                       max(d.visit_date) as date_discontinued,\n" +
                "                       de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
                "                       left outer JOIN\n" +
                "                         (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                          where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "                          group by patient_id\n" +
                "                         ) d on d.patient_id = fup.patient_id\n" +
                "                where fup.visit_date <= date(curdate())\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                    (\n" +
                "                        ((timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                          and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                        )\n" +
                "                    )\n" +
                "                ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("currentlyOnArt");
        cd.setQuery(sqlQuery);
        cd.setDescription("Clients currently on ART");
        return cd;
    }

    /**
     * Clients currently on ART with no current VL results- This Query is the same as one in Viral suppression report but uses current date instead of end date.
     * Should be updated when the counterpart in viral suppression report is updated
     * @return
     */
    public CohortDefinition noCurrentVLResults() {
        String sqlQuery = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.lab_test,vl.vl_result,vl.urgency from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "   greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "   greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "   greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "   d.patient_id as disc_patient,\n" +
                "   d.effective_disc_date as effective_disc_date,\n" +
                "   max(d.visit_date) as date_discontinued,\n" +
                "   de.patient_id as started_on_drugs,\n" +
                "   de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "   join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "   left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
                "   left outer JOIN\n" +
                " (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                " ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(curdate())\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "(\n" +
                "((timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "  and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                ")\n" +
                ") order by date_started desc\n" +
                ") t\n" +
                "  inner join (\n" +
                " select\n" +
                "   b.patient_id,\n" +
                "   max(b.visit_date) as vl_date,\n" +
                "   date_sub(curdate() , interval 12 MONTH),\n" +
                "   mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "   if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \\\"LDL\\\",\\\"\\\")) as vl_result,\n" +
                "   mid(max(concat(b.visit_date,b.urgency)),11) as urgency\n" +
                "   from (select x.patient_id as patient_id,x.visit_date as visit_date,x.lab_test as lab_test, x.test_result as test_result,urgency as urgency\n" +
                "   from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856)\n" +
                "   group by x.patient_id,x.visit_date order by visit_date desc)b\n" +
                " group by patient_id\n" +
                " having max(visit_date) <\n" +
                "date_sub(curdate() , interval 12 MONTH)\n" +
                " )vl\n" +
                "on t.patient_id = vl.patient_id)a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("noCurrentVLResults");
        cd.setQuery(sqlQuery);

        cd.setDescription("ART clients with no current VL results");
        return cd;
    }

    /**
     * Clients currently on ART with unsuppressed VL results- This Query is the same as one in Viral suppression report but uses current date instead of end date.
     * Should be updated when the counterpart in viral suppression report is updated
     * @return
     */
    public CohortDefinition unSuppressed() {
        String sqlQuery = "select a.patient_id as patient_id\n" +
                "            from(select t.patient_id,vl.vl_date,vl.lab_test,vl.vl_result,vl.urgency from (\n" +
                "            select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                   greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                   greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                   greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                   d.patient_id as disc_patient,\n" +
                "                   d.effective_disc_date as effective_disc_date,\n" +
                "                   max(d.visit_date) as date_discontinued,\n" +
                "                   de.patient_id as started_on_drugs,\n" +
                "                   de.date_started\n" +
                "            from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                   join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                   left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
                "                   left outer JOIN\n" +
                "                     (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                      where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "                      group by patient_id\n" +
                "                     ) d on d.patient_id = fup.patient_id\n" +
                "            where fup.visit_date <= date(curdate())\n" +
                "            group by patient_id\n" +
                "            having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                (\n" +
                "                    ((timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                      and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                    )\n" +
                "                ) order by date_started desc\n" +
                "            ) t\n" +
                "              inner join (\n" +
                "                         select\n" +
                "               b.patient_id,\n" +
                "               max(b.visit_date) as vl_date,\n" +
                "               date_sub(curdate() , interval 12 MONTH),\n" +
                "               mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "               if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "               mid(max(concat(b.visit_date,b.urgency)),11) as urgency\n" +
                "                               from (select x.patient_id as patient_id,x.visit_date as visit_date,x.lab_test as lab_test, x.test_result as test_result,urgency as urgency\n" +
                "                               from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856)\n" +
                "                               group by x.patient_id,x.visit_date order by visit_date desc)b\n" +
                "                         group by patient_id\n" +
                "                         having max(visit_date) between\n" +
                "            date_sub(curdate() , interval 12 MONTH) and date(curdate())\n" +
                "                         )vl\n" +
                "                on t.patient_id = vl.patient_id where vl_result >= 1000)a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unSuppressed");
        cd.setQuery(sqlQuery);
        cd.setDescription("Clients currently on ART with unSuppressed VL");
        return cd;
    }

    /**
     * Number of HIV+ patients not linked to care
     * @return
     */
    public CohortDefinition notLinked() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t.patient_id from kenyaemr_etl.etl_hts_test t inner join\n" +
                "    (select l.patient_id, l.ccc_number from kenyaemr_etl.etl_hts_referral_and_linkage l group by l.patient_id) l on t.patient_id = l.patient_id\n" +
                "left join (select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e)e on e.patient_id = t.patient_id\n" +
                "where t.final_test_result='Positive' and t.test_type = 2 and l.ccc_number is null and e.patient_id is null;";
        cd.setName("notLinked");
        cd.setQuery(sqlQuery);
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
                "and timestampdiff(WEEK,d.dob,curdate()) between 6 and 96;";
        cd.setName("allHEIsAgedBetween6And24Weeks");
        cd.setQuery(sqlQuery);
        cd.setDescription("Number of HEIs aged between 6 and 24 weeks");
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
        cd.setDescription("Number of HEIs with a HIV test result");
        return cd;
    }
    /**
     * HEIs aged between 6 and 24 weeks with no documented HIV result
     * @return
     */
    public CohortDefinition undocumentedHEIStatus() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addSearch("allHEIsAgedBetween6And24Weeks", ReportUtils.map(allHEIsAgedBetween6And24Weeks(), ""));
        cd.addSearch("allHEIsWithAHIVTestResult", ReportUtils.map(allHEIsWithAHIVTestResult(), ""));
        cd.setCompositionString("allHEIsAgedBetween6And24Weeks AND NOT allHEIsWithAHIVTestResult");
        return cd;
    }
    /**
     * Number of ART patients with no current vl result
     * Valid means VL was taken <= 12 months ago and invalid means VL was taken > 12 months ago
     * @return
     */
    public CohortDefinition invalidVL() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addSearch("invalidVl", ReportUtils.map(noCurrentVLResults(), ""));
        cd.setCompositionString("invalidVl");
        return cd;
    }

    /**
     * Number of patients currently on ART with valid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * @return
     */
    public CohortDefinition unsuppressedWithValidVL() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addSearch("unSupressedValidVl", ReportUtils.map(unSuppressed(), ""));
        cd.setCompositionString("unSupressedValidVl");
        return cd;
    }

    /**
     * Number of ART patients with invalid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     */
    public CohortDefinition unsuppressedWithoutValidVL() {
        String query = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.lab_test,vl.vl_result,vl.urgency from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                " greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                " greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                " greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                " d.patient_id as disc_patient,\n" +
                " d.effective_disc_date as effective_disc_date,\n" +
                " max(d.visit_date) as date_discontinued,\n" +
                " de.patient_id as started_on_drugs,\n" +
                " de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                " join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                " join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                " left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
                " left outer JOIN\n" +
                "   (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "    where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "    group by patient_id\n" +
                "   ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(curdate())\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "(\n" +
                "  ((timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "    and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "  )\n" +
                ") order by date_started desc\n" +
                ") t\n" +
                "inner join (\n" +
                "       select\n" +
                "b.patient_id,\n" +
                "max(b.visit_date) as vl_date,\n" +
                "date_sub(curdate() , interval 12 MONTH),\n" +
                "mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "mid(max(concat(b.visit_date,b.urgency)),11) as urgency\n" +
                "             from (select x.patient_id as patient_id,x.visit_date as visit_date,x.lab_test as lab_test, x.test_result as test_result,urgency as urgency\n" +
                "             from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856)\n" +
                "             group by x.patient_id,x.visit_date order by visit_date desc)b\n" +
                "       group by patient_id\n" +
                "       having max(visit_date) <\n" +
                "date_sub(curdate() , interval 12 MONTH)\n" +
                "       )vl\n" +
                "on t.patient_id = vl.patient_id where vl_result >= 1000)a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unsuppressedWithoutValidVL");
        cd.setQuery(query);
        cd.setDescription("Unsuppressed without current VL");
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
                "                         ifnull(max(d.visit_date), '0000-00-00'))               as latest_tca, timestampdiff(DAY, date(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11)), date(curdate())) 'DAYS MISSED',\n" +
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
                "               where date(visit_date) <= date(curdate())\n" +
                "                 and program_name = 'HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "         where fup.visit_date <= date(curdate())\n" +
                "         group by patient_id\n" +
                "         having (\n" +
                "                        (timestampdiff(DAY, date(latest_fup_tca), date(curdate())) > 30) and\n" +
                "                        (\n" +
                "                                (date(enroll_date) >= date(d.visit_date) and\n" +
                "                                 date(max_fup_vis_date) >= date(d.visit_date) and\n" +
                "                                 date(latest_fup_tca) > date(d.visit_date))\n" +
                "                                or disc_patient is null\n" +
                "                              )\n" +
                "                    )\n" +
                "     ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("undocumentedLTFU");
        cd.setQuery(sqlQuery);
        cd.setDescription("Undocumented LTFU patients");
        return cd;
    }

    /**
     * Number of patients who missed HIV appointments/Recent defaulters
     * @return
     */
    public CohortDefinition recentDefaulters() {
        String queryString = "select t.patient_id\n" +
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
                "(timestampdiff(DAY,date(latest_tca),date(curdate())) between 1 and 30) and ((date(d.effective_disc_date) > date(curdate()) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null)\n" +
                "and (date(latest_vis_date) > date(date_discontinued) and date(latest_tca) > date(date_discontinued) or disc_patient is null)\n" +
                ")\n" +
                ") t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("recentDefaulters");
        cd.setQuery(queryString);
        cd.setDescription("Missed appointment");
        return cd;
    }

    /**
     * HEIs not linked to mothers
     * @return
     */
    public CohortDefinition unlinkedHEI() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addSearch("allHEIsUnder24MonthsOld", ReportUtils.map(allHEIsUnder24MonthsOld(), ""));
        cd.addSearch("allHEIsLinkedToMothers", ReportUtils.map(allHEIsLinkedToMothers(), ""));
        cd.addSearch("allHEIsLinkedToMothersInHEIEnrolment", ReportUtils.map(allHEIsLinkedToMothersInHEIEnrolment(), ""));
        cd.setCompositionString("allHEIsUnder24MonthsOld AND NOT (allHEIsLinkedToMothers OR allHEIsLinkedToMothersInHEIEnrolment) ");
        return cd;
    }
    /**
     * HEIs under 24 months old
     * @return
     */
    public CohortDefinition allHEIsUnder24MonthsOld() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id and timestampdiff(MONTH,d.dob,curdate()) <= 24;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allHEIsUnder24MonthsOld");
        cd.setQuery(sqlQuery);
        cd.setDescription("HEIs under 24 months old");
        return cd;
    }

    /**
     * All HEIs linked to mothers in HEI enrolment
     * @return
     */
    public CohortDefinition allHEIsLinkedToMothersInHEIEnrolment() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics d on d.unique_patient_no = e.parent_ccc and d.gender = 'F'\n" +
                "where e.parent_ccc is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allHEIsLinkedToMothersInHEIEnrolment");
        cd.setQuery(sqlQuery);
        cd.setDescription("HEIs linked to mothers in HEI enrolment");
        return cd;
    }
    /**
     * HEIs linked to Mothers
     * @return
     */
    public CohortDefinition allHEIsLinkedToMothers() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e\n" +
                "                             inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "                             left join openmrs.relationship r on e.patient_id = r.person_b\n" +
                "                          inner join (select d.patient_id, d.gender from kenyaemr_etl.etl_patient_demographics d where d.gender = 'F')m on m.patient_id = r.person_a                          --   inner join openmrs.relationship r1 on d.patient_id = r1.person_a and d.gender = 'F'\n" +
                "inner join relationship_type t on r.relationship = t.relationship_type_id and t.uuid='8d91a210-c2cc-11de-8d13-0010c6dffd0f';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allHEIsLinkedToMothers");
        cd.setQuery(sqlQuery);
        cd.setDescription("HEIs linked to Mothers");
        return cd;
    }

    /**
     * Adolescents not in OTZ
     * @return
     */
    public CohortDefinition adolescentsNotInOTZ() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,date(curdate())) between 10 and  19\n" +
                "left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "   group by patient_id\n" +
                "   ) d on d.patient_id = enr.patient_id\n" +
                "where enr.patient_id not in (select ot.patient_id from kenyaemr_etl.etl_otz_enrollment ot) and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adolescentsNotInOTZ");
        cd.setQuery(sqlQuery);
        cd.setDescription("Adolescents not in OTZ");
        return cd;
    }

    /**
     * Children not in OVC
     * @return
     */
    public CohortDefinition childrenNotInOVC() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "  join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,date(curdate())) <= 17\n" +
                "  left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = enr.patient_id\n" +
                "where enr.patient_id not in (select ov.patient_id from kenyaemr_etl.etl_ovc_enrolment ov) and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("childrenNotInOVC");
        cd.setQuery(sqlQuery);
        cd.setDescription("Children not in OVC");
        return cd;
    }

    /**
     * Contacts with undocumented HIV status
     * @return
     */
    public CohortDefinition contactsUndocumentedHIVStatus() {
        String sqlQuery = "select pc.id from openmrs.kenyaemr_hiv_testing_patient_contact pc\n" +
                "           where pc.baseline_hiv_status =\"Unknown\" and pc.voided = 0 and\n" +
                "                 pc.patient_id not in (select ht.patient_id from kenyaemr_etl.etl_hts_test ht);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("contactsUndocumentedHIVStatus");
        cd.setQuery(sqlQuery);
        cd.setDescription("Contacts with undocumented HIV status");
        return cd;
    }

    /**
     * Number of SNS Contacts with undocumented HIV status
     */
    public CohortDefinition snsContactsUndocumentedHIVStatus() {
        String sqlQuery = "select pc.id from openmrs.kenyaemr_hiv_testing_patient_contact pc\n" +
                "    where pc.baseline_hiv_status =\"Unknown\" and pc.relationship_type = 166606 and pc.voided = 0 and\n" +
                "      pc.patient_id not in (select ht.patient_id from kenyaemr_etl.etl_hts_test ht);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("snsContactsUndocumentedHIVStatus");
        cd.setQuery(sqlQuery);
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
                "  where date(visit_date) <= date(curdate()) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = enr.patient_id\n" +
                "where dm.national_unique_patient_identifier is null and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("clientsWithoutNUPI");
        cd.setQuery(sqlQuery);
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
        cd.setDescription("Clients with NUPI");
        return cd;
    }

    /**
     * TX_CURR  Clients without NUPI
     * @return
     */
    public CohortDefinition txCurrclientsWithoutNUPI() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), ""));
        cd.addSearch("clientsWithNUPI", ReportUtils.map(clientsWithNUPI(), ""));
        cd.setCompositionString("txcurr AND NOT clientsWithNUPI");
        return cd;
    }

}
