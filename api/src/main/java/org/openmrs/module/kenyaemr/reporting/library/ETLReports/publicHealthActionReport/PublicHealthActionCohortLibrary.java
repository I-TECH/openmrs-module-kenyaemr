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
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
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
    Moh731CohortLibrary moh731CohortLibrary;

    /**
     * Number of HIV+ patients not linked to care
     * @return
     */
    public CohortDefinition notLinked() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t.patient_id\n" +
                "from kenyaemr_etl.etl_hts_test t\n" +
                "  left join\n" +
                "((SELECT l.patient_id\n" +
                " from kenyaemr_etl.etl_hts_referral_and_linkage l\n" +
                "   inner join kenyaemr_etl.etl_patient_demographics pt on pt.patient_id=l.patient_id and pt.voided=0\n" +
                "   inner join kenyaemr_etl.etl_hts_test t on t.patient_id=l.patient_id and t.test_type in(1,2) and t.final_test_result='Positive' and t.visit_date <=l.visit_date and t.voided=0\n" +
                " where (l.ccc_number is not null or facility_linked_to is not null)\n" +
                ")\n" +
                "union\n" +
                "( SELECT t.patient_id\n" +
                "  FROM kenyaemr_etl.etl_hts_test t\n" +
                "    INNER JOIN kenyaemr_etl.etl_patient_demographics pt ON pt.patient_id=t.patient_id AND pt.voided=0\n" +
                "    INNER JOIN kenyaemr_etl.etl_hiv_enrollment e ON e.patient_id=t.patient_id AND e.voided=0\n" +
                "  WHERE t.test_type IN (1, 2) AND t.final_test_result='Positive' AND t.voided=0\n" +
                ")) l on l.patient_id = t.patient_id\n" +
                "where t.final_test_result = 'Positive' and t.voided = 0 and t.test_type=2 and l.patient_id is null;";
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
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.exit_date is not null and e.hiv_status_at_exit is null and e.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("undocumentedHEIStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of HEIs with undocumented status");

        return cd;
    }

    /**
     * Number of patients with no current vl result
     * Valid means VL was taken <= 12 months ago and invalid means VL was taken > 12 months ago
     * @return
     */
    public CohortDefinition invalidVL() {
        String sqlQuery = "select patient_id,mid(max(concat(visit_date, if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")), \"\" )),11) as vl_result,\n" +
                "       mid(max(concat(visit_date,date_test_requested)),11) as test_date\n" +
                "from kenyaemr_etl.etl_laboratory_extract GROUP BY patient_id having timestampdiff(MONTH,date(mid(max(concat(visit_date,date_test_requested)),11)),date(:endDate)) >12";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("invalidVL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No recent VL");
        return cd;
    }

    /**
     * Number of patients with valid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     */
    public CohortDefinition unsuppressedWithValidVL() {
        String sqlQuery = "select patient_id,mid(max(concat(visit_date, if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")), \"\" )),11) as vl_result,\n" +
                "       mid(max(concat(visit_date,date_test_requested)),11) as test_date\n" +
                "from kenyaemr_etl.etl_laboratory_extract GROUP BY patient_id having timestampdiff(MONTH,date(mid(max(concat(visit_date,date_test_requested)),11)),date(:endDate)) <=12 and vl_result >= 1000;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("latestVLUnsupressed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unsuppressed VL result in their last VL");
        return cd;
    }

    /**
     * Number of patients with invalid unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     */
    public CohortDefinition unsuppressedWithinValidVL() {
        String sqlQuery = "select patient_id,mid(max(concat(visit_date, if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")), \"\" )),11) as vl_result,\n" +
                "       mid(max(concat(visit_date,date_test_requested)),11) as test_date\n" +
                "from kenyaemr_etl.etl_laboratory_extract GROUP BY patient_id having timestampdiff(MONTH,date(mid(max(concat(visit_date,date_test_requested)),11)),date(:endDate)) >12 and vl_result >= 1000;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("latestVLUnsupressed");
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
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("recentDefaulters", ReportUtils.map(moh731CohortLibrary.missedAppointment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("recentDefaulters");
        return cd;
    }

    /**
     * HEIs not linked to Mothers
     * @return
     */
    public CohortDefinition unlinkedHEI() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.parent_ccc_number is null and e.exit_date is null and e.visit_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unlinkedHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs not linked to Mothers");
        return cd;
    }

    /**
     * Mothers not linked to HEIs
     * @return
     */
    public CohortDefinition motherNotLinkedToHEI() {
        String sqlQuery = "";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("motherNotLinkedToHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers not linked to HEIs");
        return cd;
    }

    /**
     * Adolescents not in OTZ
     * @return
     */
    public CohortDefinition adolescentsNotInOTZ() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,curdate()) between 10 and  25\n" +
                "left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= curdate() and program_name='HIV'\n" +
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
                "  join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id and timestampdiff(YEAR,dm.DOB,curdate()) <= 17\n" +
                "  left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= curdate() and program_name='HIV'\n" +
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
        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c where c.baseline_hiv_status ='Unknown' and c.voided = 0";
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
        String sqlQuery = "select c.id from openmrs.kenyaemr_hiv_testing_patient_contact c where c.baseline_hiv_status ='Unknown' and c.relationship_type = 166606 and c.voided = 0";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("snsContactsUndocumentedHIVStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of SNS Contacts with undocumented HIV status");
        return cd;
    }

    /**
     * Clients without NUPI
     * @return
     */
    public CohortDefinition clientsWithoutNUPI() {
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hiv_enrollment enr\n" +
                "  join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id =enr.patient_id\n" +
                "  left join\n" +
                "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= curdate() and program_name='HIV'\n" +
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

}
