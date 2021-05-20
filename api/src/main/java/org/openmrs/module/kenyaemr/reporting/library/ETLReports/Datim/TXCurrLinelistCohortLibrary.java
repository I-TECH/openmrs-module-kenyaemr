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

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
     /**
     * Patients included in the current report but not present in the previous reporting period
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport() {
        String sqlQuery="select cr.patient_id\n" +
                "from\n" +
                "     (select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                 greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                 greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                 greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                 d.patient_id as disc_patient,\n" +
                "                 d.effective_disc_date as effective_disc_date,\n" +
                "                 max(d.visit_date) as date_discontinued,\n" +
                "                 de.patient_id as started_on_drugs\n" +
                "          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                 left outer JOIN\n" +
                "                   (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                    where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                    group by patient_id\n" +
                "                   ) d on d.patient_id = fup.patient_id\n" +
                "          where fup.visit_date <= date(:endDate)\n" +
                "          group by patient_id\n" +
                "          having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "              (\n" +
                "                  ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                    and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                  )\n" +
                "              )\n" +
                "     ) cr\n" +
                "       left join\n" +
                "         (\n" +
                "         select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                d.patient_id as disc_patient,\n" +
                "                d.effective_disc_date as effective_disc_date,\n" +
                "                max(d.visit_date) as date_discontinued,\n" +
                "                de.patient_id as started_on_drugs\n" +
                "         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "                left outer JOIN\n" +
                "                  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                   where date(visit_date) <= date_sub(DATE(:endDate), interval 3 MONTH) and program_name='HIV'\n" +
                "                   group by patient_id\n" +
                "                  ) d on d.patient_id = fup.patient_id\n" +
                "         where fup.visit_date <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "         group by patient_id\n" +
                "         having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "             (\n" +
                "                 ((date_add(date(latest_tca), interval 30 DAY)  >= date_sub(DATE(:endDate), interval 3 MONTH) or date_add(date(latest_tca), interval 30 DAY)  >= date_sub(DATE(curdate()), interval 3 MONTH))\n" +
                "                    and ((date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 3 MONTH) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                   and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                 )\n" +
                "             )\n" +
                "         ) pr on pr.patient_id=cr.patient_id\n" +
                "where pr.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report but missing in previous report");
        return cd;
    }

    /**
     * Patients included in the current report but not present in the previous reporting period
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport() {
        String sqlQuery="select pr.patient_id\n" +
                "from\n" +
                "(select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "      max(fup.visit_date) as latest_vis_date,\n" +
                "      mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "      max(d.visit_date) as date_discontinued,\n" +
                "      d.patient_id as disc_patient,\n" +
                "    de.patient_id as started_on_drugs\n" +
                "  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and date(date_started) <= date(:endDate)\n" +
                "  left outer JOIN\n" +
                "  (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = fup.patient_id\n" +
                "  where fup.visit_date <= date(:endDate)\n" +
                "  group by patient_id\n" +
                "  having (started_on_drugs is not null and started_on_drugs <> \"\") and ( \n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "  )\n" +
                ") cr \n" +
                "right join \n" +
                "(\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "      max(fup.visit_date) as latest_vis_date,\n" +
                "      mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "      max(d.visit_date) as date_discontinued,\n" +
                "      d.patient_id as disc_patient,\n" +
                "    de.patient_id as started_on_drugs\n" +
                "  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and date(date_started) <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "  left outer JOIN\n" +
                "  (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date_sub(DATE(:endDate), interval 3 MONTH) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = fup.patient_id\n" +
                "  where fup.visit_date <= date_sub(DATE(:endDate), interval 3 MONTH)\n" +
                "  group by patient_id\n" +
                "  having (started_on_drugs is not null and started_on_drugs <> \"\") and ( \n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(DATE(:endDate), interval 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(DATE(:endDate), interval 3 MONTH) ))\n" +
                "  )\n" +
                ") pr on pr.patient_id=cr.patient_id\n" +
                "where cr.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_current");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report but missing in current report");
        return cd;
    }


    /**
     * Patients included in the current report but not present in the previous reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly() {
        String sqlQuery="select cr.patient_id\n" +
                "from\n" +
                "     (select t.patient_id\n" +
                "           from(\n" +
                "                select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                d.patient_id as disc_patient,\n" +
                "                d.effective_disc_date as effective_disc_date,\n" +
                "                max(d.visit_date) as date_discontinued,\n" +
                "                de.patient_id as started_on_drugs\n" +
                "                  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                left outer JOIN\n" +
                "                  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV' and patient_id\n" +
                "                group by patient_id\n" +
                "                  ) d on d.patient_id = fup.patient_id\n" +
                "                  where fup.visit_date <= date(:endDate)\n" +
                "                  group by patient_id\n" +
                "                  having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "                and\n" +
                "                  (\n" +
                "                  ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and\n" +
                "                 ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                  and\n" +
                "                  (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                )\n" +
                "                  )t\n" +
                "     ) cr\n" +
                "       left join\n" +
                "         (\n" +
                "         select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                d.patient_id as disc_patient,\n" +
                "                d.effective_disc_date as effective_disc_date,\n" +
                "                max(d.visit_date) as date_discontinued,\n" +
                "                de.patient_id as started_on_drugs\n" +
                "         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "                left outer JOIN\n" +
                "                  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                   where date(visit_date) <= date_sub(DATE(:endDate), interval 1 MONTH) and program_name='HIV' and patient_id\n" +
                "                   group by patient_id\n" +
                "                  ) d on d.patient_id = fup.patient_id\n" +
                "         where fup.visit_date <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "         group by patient_id\n" +
                "         having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "            and\n" +
                "                (\n" +
                "                    ((timestampdiff(DAY,date(latest_tca),date_sub(DATE(:endDate), interval 1 MONTH)) <= 30 or timestampdiff(DAY,date(latest_tca),date_sub(DATE(curdate()), interval 1 MONTH)) <= 30) and\n" +
                "                     ((date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 1 MONTH) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                      and\n" +
                "                    (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                    )\n" +
                "         ) pr on pr.patient_id=cr.patient_id\n" +
                "where pr.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_previous");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report but missing in previous report");
        return cd;
    }

    /**
     * Patients included in the current report but not present in the previous reporting period - monthly indicator
     * @return
     */
    public  CohortDefinition txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly() {
        String sqlQuery="select pr.patient_id\n" +
                "from\n" +
                "     (select t.patient_id\n" +
                "      from(\n" +
                "          select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                 greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                 greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                 greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                 d.patient_id as disc_patient,\n" +
                "                 d.effective_disc_date as effective_disc_date,\n" +
                "                 max(d.visit_date) as date_discontinued,\n" +
                "                 de.patient_id as started_on_drugs\n" +
                "          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                 left outer JOIN\n" +
                "                   (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                    where date(visit_date) <= date(:endDate) and program_name='HIV' and patient_id\n" +
                "                    group by patient_id\n" +
                "                   ) d on d.patient_id = fup.patient_id\n" +
                "          where fup.visit_date <= date(:endDate)\n" +
                "          group by patient_id\n" +
                "          having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "             and\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and\n" +
                "                      ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and\n" +
                "                     (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "          )t\n" +
                "     ) cr\n" +
                "       right join\n" +
                "         (\n" +
                "         select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                d.patient_id as disc_patient,\n" +
                "                d.effective_disc_date as effective_disc_date,\n" +
                "                max(d.visit_date) as date_discontinued,\n" +
                "                de.patient_id as started_on_drugs\n" +
                "         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "                left outer JOIN\n" +
                "                  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                   where date(visit_date) <= date_sub(DATE(:endDate), interval 1 MONTH) and program_name='HIV' and patient_id\n" +
                "                   group by patient_id\n" +
                "                  ) d on d.patient_id = fup.patient_id\n" +
                "         where fup.visit_date <= date_sub(DATE(:endDate), interval 1 MONTH)\n" +
                "         group by patient_id\n" +
                "         having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "            and\n" +
                "                (\n" +
                "                    ((timestampdiff(DAY,date(latest_tca),date_sub(DATE(:endDate), interval 1 MONTH)) <= 30 or timestampdiff(DAY,date(latest_tca),date_sub(DATE(curdate()), interval 1 MONTH)) <= 30) and\n" +
                "                     ((date(d.effective_disc_date) > date_sub(DATE(:endDate), interval 1 MONTH) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                      and\n" +
                "                    (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                    )\n" +
                "         ) pr on pr.patient_id=cr.patient_id\n" +
                "where cr.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Curr_Missing_in_current");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Present in current report but missing in current report");
        return cd;
    }

}
