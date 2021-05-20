/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.viralSuppression;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohort definitions for viral suppression
 */
@Component
public class ViralSuppressionCohortLibrary {
    public CohortDefinition suppressed(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "       greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "       greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "       greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "       d.patient_id as disc_patient,\n" +
                "       d.effective_disc_date as effective_disc_date,\n" +
                "       max(d.visit_date) as date_discontinued,\n" +
                "       de.patient_id as started_on_drugs,\n" +
                "       de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "       left outer JOIN\n" +
                "         (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "    (\n" +
                "        ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "          and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "        )\n" +
                "    ) order by date_started desc\n" +
                ") t\n" +
                "  inner join (\n" +
                "             select\n" +
                "   patient_id,encounter_id,\n" +
                "   max(visit_date) as vl_date,\n" +
                "   date_sub(:endDate , interval 12 MONTH),\n" +
                "   if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "   mid(max(concat(visit_date,urgency)),11) as urgency\n" +
                "             from kenyaemr_etl.etl_laboratory_extract\n" +
                "             group by patient_id\n" +
                "             having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "             )vl\n" +
                "    on t.patient_id = vl.patient_id  where (vl_result < 1000 or vl_result='LDL'))a;";
        cd.setName("suppressed");
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setQuery(sqlQuery);
        cd.setDescription("Suppressed");

        return cd;
    }

    public  CohortDefinition unsuppressed() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "       greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "       greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "       greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "       d.patient_id as disc_patient,\n" +
                "       d.effective_disc_date as effective_disc_date,\n" +
                "       max(d.visit_date) as date_discontinued,\n" +
                "       de.patient_id as started_on_drugs,\n" +
                "       de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "       left outer JOIN\n" +
                "         (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "    (\n" +
                "        ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "          and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "        )\n" +
                "    ) order by date_started desc\n" +
                ") t\n" +
                "  inner join (\n" +
                "             select\n" +
                "   patient_id,encounter_id,\n" +
                "   max(visit_date) as vl_date,\n" +
                "   date_sub(:endDate , interval 12 MONTH),\n" +
                "   if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "   mid(max(concat(visit_date,urgency)),11) as urgency\n" +
                "             from kenyaemr_etl.etl_laboratory_extract\n" +
                "             group by patient_id\n" +
                "             having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "             )vl\n" +
                "    on t.patient_id = vl.patient_id where vl_result >= 1000)a;" ;


        cd.setName("unsuppressed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unsuppressed");

        return cd;
    }

    public  CohortDefinition noCurrentVLResults() {
        String sqlQuery="select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "d.patient_id as disc_patient,\n" +
                "d.effective_disc_date as effective_disc_date,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "de.patient_id as started_on_drugs,\n" +
                "de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "(\n" +
                "((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                ")\n" +
                ") order by date_started desc\n" +
                ") t\n" +
                "inner join (\n" +
                "select\n" +
                "     patient_id,encounter_id,\n" +
                "     max(visit_date) as vl_date,\n" +
                "         if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "     mid(max(concat(visit_date,urgency)),11) as urgency\n" +
                "from kenyaemr_etl.etl_laboratory_extract\n" +
                "group by patient_id\n" +
                "having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) <  date_sub(:endDate , interval 12 MONTH)\n" +
                ")vl\n" +
                "on t.patient_id = vl.patient_id)a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("noCurrentVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No current VL Results");
        return cd;
    }

    public  CohortDefinition noVLResults() {
        String sqlQuery="select a.patient_id as patient_id\n" +
                "from(select t.patient_id from (\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "d.patient_id as disc_patient,\n" +
                "d.effective_disc_date as effective_disc_date,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "de.patient_id as started_on_drugs,\n" +
                "de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                " where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                " group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "(\n" +
                "((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                " and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                ")\n" +
                ") order by date_started desc\n" +
                ") t\n" +
                "left join (\n" +
                "    select patient_id from kenyaemr_etl.etl_laboratory_extract\n" +
                "    group by patient_id\n" +
                "    having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) < date(:endDate)\n" +
                "    )vl\n" +
                "on t.patient_id = vl.patient_id\n" +
                "    where vl.patient_id is null)a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("noVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("No VL Results");
        return cd;
    }

}
