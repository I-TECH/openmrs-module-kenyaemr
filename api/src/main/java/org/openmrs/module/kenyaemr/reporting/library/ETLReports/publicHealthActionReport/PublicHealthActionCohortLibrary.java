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
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.viralSuppression.ViralSuppressionCohortLibrary;
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
    DatimCohortLibrary datimCohortLibrary;

    @Autowired
    ViralSuppressionCohortLibrary viralSuppressionCohort;

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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV+ patients not linked to care");
        return cd;
    }

    /**
     * Number of HEIs with undocumented status
     * @return
     */
    public CohortDefinition undocumentedHEIStatus() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.exit_date is not null and e.hiv_status_at_exit is null;";
        cd.setName("undocumentedHEIStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of HEIs with undocumented status");

        return cd;
    }

    /**
     * Number of ART patients with no current vl result
     * Valid means VL was taken <= 12 months ago and invalid means VL was taken > 12 months ago
     * @return
     */
    public CohortDefinition invalidVL() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("invalidVl", ReportUtils.map(viralSuppressionCohort.noCurrentVLResults(), "endDate=${endDate}"));
        cd.setCompositionString("invalidVl");
        return cd;
    }

    /**
     * Number of patients currently on ART with valid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * @return
     */
    public CohortDefinition unsuppressedWithValidVL() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("unSupressedValidVl", ReportUtils.map(viralSuppressionCohort.unsuppressed(), "endDate=${endDate}"));
        cd.setCompositionString("unSupressedValidVl");
        return cd;
    }

    /**
     * Number of ART patients with invalid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     */
    public CohortDefinition unsuppressedWithoutValidVL() {
        String sqlQuery = "select a.patient_id as patient_id\n" +
                "              from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (\n" +
                "              select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "              greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "              greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "              greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "              d.patient_id as disc_patient,\n" +
                "              d.effective_disc_date as effective_disc_date,\n" +
                "              max(d.visit_date) as date_discontinued,\n" +
                "              de.patient_id as started_on_drugs,\n" +
                "              de.date_started\n" +
                "              from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "              join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "              join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "              left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "              left outer JOIN\n" +
                "              (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "              where fup.visit_date <= date(:endDate)\n" +
                "              group by patient_id\n" +
                "              having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "              (\n" +
                "              ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "              )\n" +
                "              ) order by date_started desc\n" +
                "              ) t\n" +
                "              inner join (\n" +
                "              select\n" +
                "                   patient_id,encounter_id,\n" +
                "                   max(visit_date) as vl_date,\n" +
                "                       if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                   mid(max(concat(visit_date,urgency)),11) as urgency\n" +
                "              from kenyaemr_etl.etl_laboratory_extract\n" +
                "              group by patient_id\n" +
                "              having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) <  date_sub(:endDate , interval 12 MONTH)\n" +
                "              )vl\n" +
                "              on t.patient_id = vl.patient_id where vl_result >= 1000)a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("allUnsuppressedWithoutCurrentVL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unsuppressed VL result in their last VL");
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
                "                                     date(date_started) <= date(:endDate)\n" +
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
                "                        (timestampdiff(DAY, date(latest_fup_tca), date(:endDate)) > 30) and\n" +
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Missed appointment");
        return cd;
    }

    /**
     * HEIs not linked to Mothers
     * @return
     */
    public CohortDefinition unlinkedHEI() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.parent_ccc_number is null and e.exit_date is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unlinkedHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs not linked to Mothers");
        return cd;
    }

    /**
     * Mothers linked to HEI
     * @return
     */
    public CohortDefinition mothersLinkedToHEI() {
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_demographics d inner join kenyaemr_etl.etl_hei_enrollment e on d.unique_patient_no = e.parent_ccc_number;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unlinkedHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers linked to HEI");
        return cd;
    }
    /**
     * Mothers in MCH with babies
     * @return
     */
    public CohortDefinition mchMothersWithBabies() {
        String sqlQuery = "select a.patient_id\n" +
                "      from (Select e.patient_id, max(e.visit_date) as latest_enrollment,disc.visit_date as disc_date,disc.patient_id as disc_patient, de.latest_delivery as latest_delivery,p.latest_visit as latest_pnc_visit\n" +
                "            from kenyaemr_etl.etl_mch_enrollment e\n" +
                "               left join (select p.patient_id, max(p.visit_date) as latest_visit\n" +
                "                          from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                          group by p.patient_id) p on e.patient_id = p.patient_id\n" +
                "               left join (select de.patient_id, max(de.visit_date) as latest_delivery\n" +
                "                          from kenyaemr_etl.etl_mchs_delivery de\n" +
                "                          group by de.patient_id) de on e.patient_id = de.patient_id\n" +
                "               left join (select patient_id,\n" +
                "                                 coalesce(max(date(effective_discontinuation_date)), date(max(visit_date))) visit_date,\n" +
                "                                 max(date(effective_discontinuation_date)) as                               effective_disc_date\n" +
                "                          from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                          where date(visit_date) <= date(current_date)\n" +
                "                            and program_name = 'MCH Mother'\n" +
                "                          group by patient_id) disc on e.patient_id = disc.patient_id\n" +
                "      group by e.patient_id\n" +
                "      having (latest_enrollment > disc_date or disc_patient is null) and (latest_delivery > latest_enrollment or latest_pnc_visit > latest_enrollment)) a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("motherNotLinkedToHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers not linked to HEIs");
        return cd;
    }

    /**
     * Mothers not liked to HEI
     * @return
     */
    public CohortDefinition motherNotLinkedToHEI() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("mchMothersWithBabies", ReportUtils.map(mchMothersWithBabies(), "endDate=${endDate}"));
        cd.addSearch("mothersLinkedToHEI", ReportUtils.map(mothersLinkedToHEI(), "endDate=${endDate}"));
        cd.setCompositionString("mchMothersWithBabies AND NOT mothersLinkedToHEI");
        return cd;
    }
    /**
     * Adolescents not in OTZ
     * @return
     */
    public CohortDefinition adolescentsNotInOTZ() {
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents not in OTZ");
        return cd;
    }

    /**
     * Children not in OVC
     * @return
     */
    public CohortDefinition childrenNotInOVC() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "  join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,date(:endDate)) <= 17\n" +
                "  left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = enr.patient_id\n" +
                "where enr.patient_id not in (select ov.patient_id from kenyaemr_etl.etl_ovc_enrolment ov) and d.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("childrenNotInOVC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
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
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients without NUPI");
        return cd;
    }

    /**
     * TX_CURR  Clients without NUPI
     * @return
     */
    public CohortDefinition txCurrclientsWithoutNUPI() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "endDate=${endDate}"));
        cd.addSearch("everEnrolledWithoutNUPI", ReportUtils.map(clientsWithoutNUPI(), "endDate=${endDate}"));
        cd.setCompositionString("txcurr AND everEnrolledWithoutNUPI");
        return cd;
    }

}
