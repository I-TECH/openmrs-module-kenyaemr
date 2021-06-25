/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.otz;

import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 12/02/19.
 */

/**
 * Library of cohort definitions used specifically in the OZT report based on ETL tables.
 */
@Component


public class ETLOtzCohortLibrary {


    public CohortDefinition otzEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "where (e.transfer_in =\"Yes\" or e.transfer_in =\"No\")\n" +
                "and date(e.visit_date) between date(:startDate) and date(:endDate);";
        cd.setName("newOtzEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New OTZ Enrollment");

        return cd;
    }

    public CohortDefinition attendedSupportGroup(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct a.patient_id\n" +
                "  from kenyaemr_etl.etl_otz_activity a\n" +
                "  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=a.patient_id and p.voided=0\n" +
                "  where a.attended_support_group =\"Yes\" and a.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("AttendedSupportGroup");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Attended Support Group");

        return cd;
    }

    public CohortDefinition transferredOut(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select dis.patient_id\n" +
                "                      from kenyaemr_etl.etl_patient_program_discontinuation dis\n" +
                "       join kenyaemr_etl.etl_otz_enrollment en on en.patient_id = dis.patient_id\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id = dis.patient_id and p.voided = 0\n" +
                "where dis.discontinuation_reason = 159492\n" +
                "  and dis.visit_date between :startDate and :endDate\n" +
                "  and en.visit_date between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "  and dis.program_name = 'OTZ' ;";
        cd.setName("TransferOut");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transfer Out");

        return cd;
    }

    public CohortDefinition otzLostToFollowup(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select dis.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation dis\n" +
                "       join kenyaemr_etl.etl_otz_enrollment en on en.patient_id = dis.patient_id\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id = dis.patient_id and p.voided = 0\n" +
                "where dis.discontinuation_reason = 5240\n" +
                "  and en.visit_date between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "  and dis.visit_date between :startDate and :endDate\n" +
                "  and dis.program_name = 'OTZ';";
        cd.setName("Lftu");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Lost to followup");

        return cd;
    }

    public CohortDefinition exitedPostOtz(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select e.patient_id,\n" +
                "             max(e.visit_date) as latest_enr,\n" +
                "             min(e.visit_date) as first_enr,\n" +
                "             d.patient_id      as disc_patient,\n" +
                "             d.latest_disc,\n" +
                "             p.dob             as dob,\n" +
                "             timestampdiff(YEAR, p.DOB, date(:endDate)),\n" +
                "             date_sub(:endDate, interval :month MONTH)\n" +
                "      from kenyaemr_etl.etl_otz_enrollment e\n" +
                "             inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "             inner join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                         from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                         where d.program_name = 'OTZ'\n" +
                "                         group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "      group by e.patient_id\n" +
                "      having timestampdiff(YEAR, dob, date(:endDate)) between 20 and 24\n" +
                "         and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "         and d.latest_disc between :startDate and :endDate)a;";
        cd.setName("ExitedPostOtz");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exited Post OTZ");

        return cd;
    }

    public CohortDefinition numberOfAdolescentsInotzProgram(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "from kenyaemr_etl.etl_otz_enrollment e\n" +
                "       inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "       left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                  from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                  where d.program_name = 'OTZ'\n" +
                "                  group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "group by e.patient_id\n" +
                "having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "   and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "and (disc_patient is null or latest_enr > d.latest_disc))a;";
        cd.setName("Adolescents");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of adolescents 20-24 in OTZ");

        return cd;
    }
    public CohortDefinition reportedDead(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select dis.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation dis\n" +
                "       join kenyaemr_etl.etl_otz_enrollment en on en.patient_id = dis.patient_id\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id = dis.patient_id and p.voided = 0\n" +
                "where (dis.discontinuation_reason = 160034 or p.dead = 1)\n" +
                "  and en.visit_date between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "  and dis.visit_date between :startDate and :endDate\n" +
                "  and dis.program_name = 'OTZ';";
        cd.setName("Dead");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Reported Dead");

        return cd;
    }

    // transitioned to adult care aged 20-24
    public CohortDefinition transitionedToAdultCare(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select dis.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation dis\n" +
                "       join kenyaemr_etl.etl_otz_enrollment en on en.patient_id = dis.patient_id\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id = dis.patient_id and p.voided = 0\n" +
                "where dis.discontinuation_reason = 165363\n" +
                "  and en.visit_date between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "  and dis.visit_date between :startDate and :endDate\n" +
                "  and dis.program_name = 'OTZ';";
        cd.setName("TransitionToAdultCare");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transition to Adult Care");

        return cd;
    }

    public CohortDefinition optedOutOfOtz(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select dis.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation dis\n" +
                "      join kenyaemr_etl.etl_otz_enrollment en on en.patient_id = dis.patient_id\n" +
                "      join kenyaemr_etl.etl_patient_demographics p on p.patient_id = dis.patient_id and p.voided = 0\n" +
                "where dis.discontinuation_reason = 159836\n" +
                " and en.visit_date between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "and dis.visit_date between :startDate and :endDate\n" +
                " and dis.program_name = 'OTZ';";
        cd.setName("OptedOut");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Opt Out");

        return cd;
    }

    //# of adolescents in OTZ who kept  (came on/before the scheduled date) their clinic appointments
    public CohortDefinition honoredAppointments(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct patient_id\n" +
                "            from (\n" +
                "            select f1.patient_id,max(f1.visit_date) as visit_date, max(f2.next_appointment_date) as next_appointment_date\n" +
                "            from kenyaemr_etl.etl_patient_hiv_followup f1\n" +
                "            join kenyaemr_etl.etl_patient_hiv_followup f2 on f1.visit_date>f2.visit_date\n" +
                "            and f1.patient_id=f2.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "            join kenyaemr_etl.etl_otz_enrollment ot on ot.patient_id=f1.patient_id\n" +
                "            where date(f1.visit_date) between date(:startDate) and date(:endDate)\n" +
                "              and f1.visit_date<=f2.next_appointment_date and f1.visit_date>=ot.visit_date\n" +
                "            group by f1.patient_id, f1.visit_date)vis;";
        cd.setName("HonoredAppointments");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Honored Appointments");

        return cd;
    }

    public CohortDefinition adherenceGreaterThan90(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  patient_id\n" +
                "       from (\n" +
                "       select f1.patient_id,f1.arv_adherence\n" +
                "       from kenyaemr_etl.etl_patient_hiv_followup f1\n" +
                "              join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "              join kenyaemr_etl.etl_otz_enrollment otz on otz.patient_id=f1.patient_id\n" +
                "              join kenyaemr_etl.etl_patient_demographics p on p.patient_id=f1.patient_id and p.voided=0\n" +
                "         where date(f1.visit_date) <= date(:endDate) and f1.arv_adherence=159405\n" +
                "         group by f1.patient_id)vis ;";
        cd.setName("AdherenceGood");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adherence > 95%");

        return cd;
    }

    public CohortDefinition bookedForAppointmentInTheMonth(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  patient_id\n" +
                "           from (\n" +
                "           select f1.patient_id,max(f1.visit_date) as visit_date, max(f1.next_appointment_date) as next_appointment_date\n" +
                "           from kenyaemr_etl.etl_patient_hiv_followup f1\n" +
                "           join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "           join kenyaemr_etl.etl_otz_enrollment otz on otz.patient_id=f1.patient_id\n" +
                "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=f1.patient_id and p.voided=0\n" +
                "             where date(f1.next_appointment_date) between date(:startDate) and date(:endDate) and f1.next_appointment_date>otz.visit_date\n" +
                "           group by f1.patient_id, f1.visit_date)vis where visit_date<next_appointment_date;\n";
        cd.setName("BookedAppointments");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Booked Appointments for the month");

        return cd;
    }
    public CohortDefinition patientWithVLResultWithinLast6MonthsAtEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "  inner join( select patient_id,visit_date,\n" +
                "                if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                urgency\n" +
                "              from kenyaemr_etl.etl_laboratory_extract\n" +
                "              where lab_test in (1305, 856)\n" +
                "              and visit_date between  date_sub(:endDate , interval 6 MONTH) and date(:endDate)) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id;\n";
        cd.setName("VLLast6months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Vl Results within last 6 months");

        return cd;
    }

    public CohortDefinition patientWithVLlessThan1000AtEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "  inner join( select patient_id,visit_date,\n" +
                "     if(lab_test = 856, test_result, \"\") as vl_result,\n" +
                "     urgency\n" +
                "    from kenyaemr_etl.etl_laboratory_extract\n" +
                "    where lab_test = 856\n" +
                "    and visit_date between  date_sub(:endDate , interval 6 MONTH) and date(:endDate)) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having  mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";
        cd.setName("VLLessThan1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("VL less than 1000 at enrollment");

        return cd;
    }

    public CohortDefinition patientWithVLlessThan400AtEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "  inner join( select patient_id,visit_date,\n" +
                "                if(lab_test = 856, test_result, \"\") as vl_result,\n" +
                "                urgency\n" +
                "              from kenyaemr_etl.etl_laboratory_extract\n" +
                "              where lab_test = 856\n" +
                "                    and visit_date between  date_sub(:endDate , interval 6 MONTH) and date(:endDate)) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having  mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<400;";
        cd.setName("VLLessThan400");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("VL less than 400 at enrollment");

        return cd;
    }

    public CohortDefinition patientWithLDLAtEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "  inner join( select patient_id,visit_date,\n" +
                "                if(lab_test=1305 and test_result = 1302, \"LDL\",\"\") as vl_result,\n" +
                "                urgency\n" +
                "              from kenyaemr_etl.etl_laboratory_extract\n" +
                "              where lab_test = 1305\n" +
                "                    and visit_date between  date_sub(:endDate , interval 6 MONTH) and date(:endDate)) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" ;";
        cd.setName("LDLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Those VL results as LDL");

        return cd;
    }

    public CohortDefinition patientWithValidVL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select c.patient_id,vl.vl_date from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "  from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "         left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'OTZ'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "  group by e.patient_id\n" +
                "  having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "     and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "     and (disc_patient is null or latest_enr > d.latest_disc))c\n" +
                "inner join\n" +
                " (select\n" +
                "                          b.patient_id,\n" +
                "                          max(b.visit_date) as vl_date,\n" +
                "                          date_sub(:endDate , interval 12 MONTH),\n" +
                "                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                                              mid(max(concat(b.visit_date, b.urgency)), 11)\n" +
                "                                                                              as urgency\n" +
                "                                                                              from (select x.patient_id  as patient_id,\n" +
                "                                                                                           x.visit_date  as visit_date,\n" +
                "                                                                                           x.lab_test    as lab_test,\n" +
                "                                                                                           x.test_result as test_result,\n" +
                "                                                                                           urgency       as urgency\n" +
                "                                                                                    from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                                                                    where x.lab_test in (1305, 856)\n" +
                "                                                                                    group by x.patient_id, x.visit_date\n" +
                "                                                                                    order by visit_date desc)b\n" +
                "                                                                              group by patient_id\n" +
                "                                                                              having max(visit_date) between\n" +
                "                                                                                  date_sub(:endDate, interval 12 MONTH) and date(:endDate))vl\n" +
                "                             on c.patient_id = vl.patient_id)a;";
        cd.setName("ValidVL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL");

        return cd;
    }

    public CohortDefinition patient20To24WithValidVL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select c.patient_id,vl.vl_date from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "  from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "         left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'OTZ'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "  group by e.patient_id\n" +
                "  having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "     and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "     and (disc_patient is null or latest_enr > d.latest_disc))c\n" +
                "inner join\n" +
                " (select\n" +
                "                          b.patient_id,\n" +
                "                          max(b.visit_date) as vl_date,\n" +
                "                          date_sub(:endDate , interval 12 MONTH),\n" +
                "                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                                              mid(max(concat(b.visit_date, b.urgency)), 11)\n" +
                "                                                                              as urgency\n" +
                "                                                                              from (select x.patient_id  as patient_id,\n" +
                "                                                                                           x.visit_date  as visit_date,\n" +
                "                                                                                           x.lab_test    as lab_test,\n" +
                "                                                                                           x.test_result as test_result,\n" +
                "                                                                                           urgency       as urgency\n" +
                "                                                                                    from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                                                                    where x.lab_test in (1305, 856)\n" +
                "                                                                                    group by x.patient_id, x.visit_date\n" +
                "                                                                                    order by visit_date desc)b\n" +
                "                                                                              group by patient_id\n" +
                "                                                                              having max(visit_date) between\n" +
                "                                                                                  date_sub(:endDate, interval 12 MONTH) and date(:endDate))vl\n" +
                "                             on c.patient_id = vl.patient_id)a;";
        cd.setName("ValidVL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient aged 20 - 24 with Valid VL");

        return cd;
    }

    public CohortDefinition patient20To24WithValidVLLess400(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select c.patient_id,vl.vl_date from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "  from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "         left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'OTZ'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "  group by e.patient_id\n" +
                "  having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "     and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "     and (disc_patient is null or latest_enr > d.latest_disc))c\n" +
                "inner join\n" +
                " (select\n" +
                "                          b.patient_id,\n" +
                "                          max(b.visit_date) as vl_date,\n" +
                "                          date_sub(:endDate , interval 12 MONTH),\n" +
                "                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                                              mid(max(concat(b.visit_date, b.urgency)), 11)\n" +
                "                                                                              as urgency\n" +
                "                                                                              from (select x.patient_id  as patient_id,\n" +
                "                                                                                           x.visit_date  as visit_date,\n" +
                "                                                                                           x.lab_test    as lab_test,\n" +
                "                                                                                           x.test_result as test_result,\n" +
                "                                                                                           urgency       as urgency\n" +
                "                                                                                    from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                                                                    where x.lab_test in (1305, 856)\n" +
                "                                                                                    group by x.patient_id, x.visit_date\n" +
                "                                                                                    order by visit_date desc)b\n" +
                "                                                                              group by patient_id\n" +
                "                                                                              having max(visit_date) between\n" +
                "                                                                                  date_sub(:endDate, interval 12 MONTH) and date(:endDate))vl\n" +
                "                             on c.patient_id = vl.patient_id where vl.vl_result < 400 or vl_result = 'LDL')a;";
        cd.setName("ValidVLLess400");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL < 400");

        return cd;
    }

    public CohortDefinition patient20To24WithValidVLLess1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select c.patient_id,vl.vl_date from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "  from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "         left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'OTZ'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "  group by e.patient_id\n" +
                "  having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "     and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "     and (disc_patient is null or latest_enr > d.latest_disc))c\n" +
                "inner join\n" +
                " (select\n" +
                "                          b.patient_id,\n" +
                "                          max(b.visit_date) as vl_date,\n" +
                "                          date_sub(:endDate , interval 12 MONTH),\n" +
                "                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                                              mid(max(concat(b.visit_date, b.urgency)), 11)\n" +
                "                                                                              as urgency\n" +
                "                                                                              from (select x.patient_id  as patient_id,\n" +
                "                                                                                           x.visit_date  as visit_date,\n" +
                "                                                                                           x.lab_test    as lab_test,\n" +
                "                                                                                           x.test_result as test_result,\n" +
                "                                                                                           urgency       as urgency\n" +
                "                                                                                    from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                                                                    where x.lab_test in (1305, 856)\n" +
                "                                                                                    group by x.patient_id, x.visit_date\n" +
                "                                                                                    order by visit_date desc)b\n" +
                "                                                                              group by patient_id\n" +
                "                                                                              having max(visit_date) between\n" +
                "                                                                                  date_sub(:endDate, interval 12 MONTH) and date(:endDate))vl\n" +
                "                             on c.patient_id = vl.patient_id where vl.vl_result < 1000 or vl_result = 'LDL')a;";
        cd.setName("ValidVLLess1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL < 1000");

        return cd;
    }

    public CohortDefinition patientWithValidVLLess400(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select c.patient_id,vl.vl_date from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "  from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "         left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'OTZ'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "  group by e.patient_id\n" +
                "  having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "     and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "     and (disc_patient is null or latest_enr > d.latest_disc))c\n" +
                "inner join\n" +
                " (select\n" +
                "                          b.patient_id,\n" +
                "                          max(b.visit_date) as vl_date,\n" +
                "                          date_sub(:endDate , interval 12 MONTH),\n" +
                "                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                                              mid(max(concat(b.visit_date, b.urgency)), 11)\n" +
                "                                                                              as urgency\n" +
                "                                                                              from (select x.patient_id  as patient_id,\n" +
                "                                                                                           x.visit_date  as visit_date,\n" +
                "                                                                                           x.lab_test    as lab_test,\n" +
                "                                                                                           x.test_result as test_result,\n" +
                "                                                                                           urgency       as urgency\n" +
                "                                                                                    from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                                                                    where x.lab_test in (1305, 856)\n" +
                "                                                                                    group by x.patient_id, x.visit_date\n" +
                "                                                                                    order by visit_date desc)b\n" +
                "                                                                              group by patient_id\n" +
                "                                                                              having max(visit_date) between\n" +
                "                                                                                  date_sub(:endDate, interval 12 MONTH) and date(:endDate))vl\n" +
                "                             on c.patient_id = vl.patient_id where vl.vl_result < 400 or vl_result = 'LDL')a;";
        cd.setName("ValidVLLess400");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL < 400");

        return cd;
    }

    public CohortDefinition patientWithValidVLLess1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select c.patient_id,vl.vl_date from (select e.patient_id, max(e.visit_date) as latest_enr,min(e.visit_date) as first_enr, d.patient_id as disc_patient,d.latest_disc,p.dob as dob,timestampdiff(YEAR,p.DOB,date(:endDate)),date_sub(:endDate , interval :month MONTH)\n" +
                "  from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id\n" +
                "         left join (select d.patient_id, date(max(d.visit_date)) as latest_disc\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'OTZ'\n" +
                "                    group by d.patient_id)d on e.patient_id = d.patient_id\n" +
                "  group by e.patient_id\n" +
                "  having timestampdiff(YEAR,dob,date(:endDate)) between 20 and 24\n" +
                "     and latest_enr between date_sub(:startDate, interval :month MONTH) and date_sub(:endDate, interval :month MONTH)\n" +
                "     and (disc_patient is null or latest_enr > d.latest_disc))c\n" +
                "inner join\n" +
                " (select\n" +
                "                          b.patient_id,\n" +
                "                          max(b.visit_date) as vl_date,\n" +
                "                          date_sub(:endDate , interval 12 MONTH),\n" +
                "                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                                              mid(max(concat(b.visit_date, b.urgency)), 11)\n" +
                "                                                                              as urgency\n" +
                "                                                                              from (select x.patient_id  as patient_id,\n" +
                "                                                                                           x.visit_date  as visit_date,\n" +
                "                                                                                           x.lab_test    as lab_test,\n" +
                "                                                                                           x.test_result as test_result,\n" +
                "                                                                                           urgency       as urgency\n" +
                "                                                                                    from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                                                                    where x.lab_test in (1305, 856)\n" +
                "                                                                                    group by x.patient_id, x.visit_date\n" +
                "                                                                                    order by visit_date desc)b\n" +
                "                                                                              group by patient_id\n" +
                "                                                                              having max(visit_date) between\n" +
                "                                                                                  date_sub(:endDate, interval 12 MONTH) and date(:endDate))vl\n" +
                "                             on c.patient_id = vl.patient_id where vl.vl_result < 1000 or vl_result = 'LDL')a;";
        cd.setName("ValidVLLess1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL < 1000");

        return cd;
    }

    public CohortDefinition patientWithValidVLGreaterThan1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "                inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                inner join( select patient_id,visit_date,\n" +
                "                   if(lab_test = 856, test_result, \"\") as vl_result,\n" +
                "   urgency\n" +
                "  from kenyaemr_etl.etl_laboratory_extract\n" +
                "  where lab_test = 856\n" +
                "  and visit_date between  date_sub(:endDate , interval 6 MONTH) and date(:endDate)) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having  mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)>1000;";
        cd.setName("ValidVLMore1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL > 1000");

        return cd;
    }

    public CohortDefinition patientWithValidVLasLDL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id from kenyaemr_etl.etl_otz_enrollment e\n" +
                "                inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                inner join( select patient_id,visit_date,\n" +
                "                              if(lab_test=1305 and test_result = 1302, \"LDL\",\"\") as vl_result,\n" +
                "              urgency\n" +
                "            from kenyaemr_etl.etl_laboratory_extract\n" +
                "            where lab_test = 1305\n" +
                "                  and visit_date between  date_sub(:endDate , interval 6 MONTH) and date(:endDate)) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\";";
        cd.setName("ValidVLASLDL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with Valid VL as LDL");

        return cd;
    }


    public CohortDefinition patientEligibleForRoutineVL(int month){
        SqlCohortDefinition cd =  new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "(\n" +
                "select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "      (select a.patient_id,\n" +
                "              a.date_started_art,\n" +
                "              mch.patient_id                                               as inMCH,\n" +
                "              mch.latest_mch_date                                          as latest_mch_date,\n" +
                "              mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "              left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "              left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "              a.dob                                                        as dob\n" +
                "       from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob\n" +
                "             from kenyaemr_etl.etl_otz_enrollment e\n" +
                "                    inner join kenyaemr_etl.etl_patient_demographics p\n" +
                "                      on e.patient_id = p.patient_id and p.voided = 0\n" +
                "                    inner join kenyaemr_etl.etl_drug_event d\n" +
                "                      on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "             group by e.patient_id\n" +
                "             having timestampdiff(MONTH,min(d.date_started),:endDate ) = "+month+") a\n" +
                "              left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "                               left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "                         group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval "+month+" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "              left join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test in (856, 1305)\n" +
                "              left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test in (856, 1305)\n" +
                "       group by a.patient_id) o\n" +
                ") e where\n" +
                "(\n" +
                "(e.lastVL is null and  e.inMCH is null)\n" +
                "or  e.lastVL is null and  e.inMCH is not null and e.latest_mch_date >= e.date_started_art\n" +
                "or  e.lastVL is not null  and (lastVL < 1000 or lastVL=1302) and (timestampdiff(YEAR,e.dob,:endDate))<25 and  timestampdiff(MONTH,e.lastVLDate, :endDate) >= 6\n" +
                "or  e.lastVL is not null  and (lastVL < 1000 or lastVL=1302) and (timestampdiff(YEAR,e.dob,:endDate))>25 and  timestampdiff(MONTH,e.lastVLDate, :endDate) >= 12\n" +
                "or  e.lastVL is not null  and (lastVL > 1000 and timestampdiff(MONTH,e.lastVLDate, :endDate) >= 3\n" +
                "or  e.lastVL is not null and (lastVL < 1000 or lastVL=1302) and e.inMCH is not null and  timestampdiff(MONTH,e.lastVLDate, :endDate) >= 6\n" +
                "));";
        cd.setName("due for VL Test");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    public CohortDefinition patientSamplesTakenForRoutineVL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (\n" +
                "    select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "      (select a.patient_id,\n" +
                "         a.date_started_art,\n" +
                "         mch.patient_id                                               as inMCH,\n" +
                "         mch.latest_mch_date                                          as latest_mch_date,\n" +
                "         mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "         left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "         left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "         a.dob                                                        as dob,\n" +
                "         encounter\n" +
                "       from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob,o.encounter_id as encounter from kenyaemr_etl.etl_otz_enrollment e\n" +
                "               inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id and p.voided = 0\n" +
                "               inner join kenyaemr_etl.etl_drug_event d on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "               inner join openmrs.encounter enc on e.patient_id=enc.patient_id\n" +
                "               inner join openmrs.orders o on o.encounter_id =  enc.encounter_id\n" +
                "       where o.order_type_id = 3 and o.order_reason=161236 and o.date_activated  between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "             group by e.patient_id\n" +
                "             having timestampdiff(MONTH,min(d.date_started),:endDate ) = \"+month+\") a\n" +
                "           left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "           left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "         group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval \"+month+\" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "         left join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test in (856, 1305)\n" +
                "         left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test in (856, 1305)\n" +
                "       group by a.patient_id) o\n" +
                "  ) e\n" +
                "where\n" +
                "  (\n" +
                "    (e.lastVL is null and  e.inMCH is null)\n" +
                "    or  e.lastVL is null and  e.inMCH is not null and e.latest_mch_date >= e.date_started_art\n" +
                "    or  e.lastVL is not null  and (lastVL < 1000 or lastVL=1302) and (timestampdiff(YEAR,e.dob,:endDate))<25 and  timestampdiff(MONTH,e.lastVLDate, :endDate) >= 6\n" +
                "    or  e.lastVL is not null  and (lastVL < 1000 or lastVL=1302) and (timestampdiff(YEAR,e.dob,:endDate))>25 and  timestampdiff(MONTH,e.lastVLDate, :endDate) >= 12\n" +
                "    or  e.lastVL is not null  and (lastVL > 1000 and timestampdiff(MONTH,e.lastVLDate, :endDate) >= 3\n" +
                "    or  e.lastVL is not null and (lastVL < 1000 or lastVL=1302) and e.inMCH is not null and  timestampdiff(MONTH,e.lastVLDate, :endDate) >= 6\n" +
                "    ));";
        cd.setName("SamplesTakenForRoutineVL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient samples taken for routine vl");

        return cd;
    }

    public CohortDefinition patientWithRoutineFollowupVL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "                from\n" +
                "                (\n" +
                "                  select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "                    (select a.patient_id,\n" +
                "                       a.date_started_art,\n" +
                "                       mch.patient_id                                               as inMCH,\n" +
                "                       mch.latest_mch_date                                          as latest_mch_date,\n" +
                "                       mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "                       left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "                       left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "                       a.dob                                                        as dob,\n" +
                "                       encounter\n" +
                "                     from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob,o.encounter_id as encounter from kenyaemr_etl.etl_otz_enrollment e\n" +
                "                             inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id and p.voided = 0\n" +
                "                             inner join kenyaemr_etl.etl_drug_event d on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "                             inner join openmrs.encounter enc on e.patient_id=enc.patient_id\n" +
                "                             inner join openmrs.orders o on o.encounter_id =  enc.encounter_id\n" +
                "                     where o.order_type_id = 3 and o.order_reason=161236 and o.date_activated  between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "                           group by e.patient_id\n" +
                "                           having timestampdiff(MONTH,min(d.date_started),:endDate ) = \"+month+\") a\n" +
                "         left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "         left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "       group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval \"+month+\" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "       inner join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test in (856, 1305)\n" +
                "       left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test in (856, 1305)\n" +
                "     group by a.patient_id) o\n" +
                ") e\n" +
                "where  e.lastVL is not null  and e.lastVLDate between  date_sub(:endDate , interval :month MONTH) and date(:endDate);";
        cd.setName("PatientWithRoutineFollowupVL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with routine followup vl end of reporting period");

        return cd;
    }

    public CohortDefinition patientWithRoutineFollowupVLGreaterThan1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (\n" +
                "    select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "      (select a.patient_id,\n" +
                "         a.date_started_art,\n" +
                "         mch.patient_id                                               as inMCH,\n" +
                "         mch.latest_mch_date                                          as latest_mch_date,\n" +
                "         mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "         left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "         left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "         a.dob                                                        as dob,\n" +
                "         encounter\n" +
                "       from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob,o.encounter_id as encounter from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id and p.voided = 0\n" +
                "         inner join kenyaemr_etl.etl_drug_event d on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "         inner join openmrs.encounter enc on e.patient_id=enc.patient_id\n" +
                "         inner join openmrs.orders o on o.encounter_id =  enc.encounter_id\n" +
                "       where o.order_type_id = 3 and o.order_reason=161236 and o.date_activated  between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "       group by e.patient_id\n" +
                "       having timestampdiff(MONTH,min(d.date_started),:endDate ) = \"+month+\") a\n" +
                "         left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "           left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "         group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval \"+month+\" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "         inner join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test = 856\n" +
                "         left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test = 856\n" +
                "       group by a.patient_id) o\n" +
                "  ) e\n" +
                "where e.lastVL  >=1000  and  e.lastVLDate between  date_sub(:endDate , interval :month MONTH) and date(:endDate);";
        cd.setName("PatientWithRoutineFollowupVLMore1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with routine followup vl greater than 1000 copies/ml");

        return cd;
    }

    public CohortDefinition patientWithRoutineFollowupVLLessThan1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (\n" +
                "    select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "      (select a.patient_id,\n" +
                "         a.date_started_art,\n" +
                "         mch.patient_id                                               as inMCH,\n" +
                "         mch.latest_mch_date                                          as latest_mch_date,\n" +
                "         mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "         left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "         left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "         a.dob                                                        as dob,\n" +
                "         encounter\n" +
                "       from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob,o.encounter_id as encounter from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id and p.voided = 0\n" +
                "         inner join kenyaemr_etl.etl_drug_event d on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "         inner join openmrs.encounter enc on e.patient_id=enc.patient_id\n" +
                "         inner join openmrs.orders o on o.encounter_id =  enc.encounter_id\n" +
                "       where o.order_type_id = 3 and o.order_reason=161236 and o.date_activated  between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "       group by e.patient_id\n" +
                "       having timestampdiff(MONTH,min(d.date_started),:endDate ) = \"+month+\") a\n" +
                "         left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "           left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "         group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval \"+month+\" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "         inner join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test = 856\n" +
                "         left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test = 856\n" +
                "       group by a.patient_id) o\n" +
                "  ) e\n" +
                "where e.lastVL  <1000  and  e.lastVLDate between  date_sub(:endDate , interval :month MONTH) and date(:endDate);";
        cd.setName("PatientWithRoutineFollowupVLLess1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with routine followup vl less than 1000 copies/ml");

        return cd;
    }

    public CohortDefinition patientWithRoutineFollowupVLLessThan400(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (\n" +
                "    select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "      (select a.patient_id,\n" +
                "         a.date_started_art,\n" +
                "         mch.patient_id                                               as inMCH,\n" +
                "         mch.latest_mch_date                                          as latest_mch_date,\n" +
                "         mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "         left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "         left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "         a.dob                                                        as dob,\n" +
                "         encounter\n" +
                "       from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob,o.encounter_id as encounter from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id and p.voided = 0\n" +
                "         inner join kenyaemr_etl.etl_drug_event d on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "         inner join openmrs.encounter enc on e.patient_id=enc.patient_id\n" +
                "         inner join openmrs.orders o on o.encounter_id =  enc.encounter_id\n" +
                "       where o.order_type_id = 3 and o.order_reason=161236 and o.date_activated  between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "       group by e.patient_id\n" +
                "       having timestampdiff(MONTH,min(d.date_started),:endDate ) = \"+month+\") a\n" +
                "         left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "           left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "         group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval \"+month+\" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "         inner join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test = 856\n" +
                "         left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test = 856\n" +
                "       group by a.patient_id) o\n" +
                "  ) e\n" +
                "where e.lastVL  <400  and  e.lastVLDate between  date_sub(:endDate , interval :month MONTH) and date(:endDate);";
        cd.setName("PatientWithRoutineFollowupVLLess400");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with routine followup vl less than 400 copies/ml");

        return cd;
    }

    public CohortDefinition patientWithRoutineVLResultsLDL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (\n" +
                "    select patient_id, date_started_art, inMCH, latest_mch_date, lastVLDate, lastVLDateWithinPeriod, dob,timestampdiff(YEAR,dob,:endDate) as age, if(lastVL !=null and (lastVL<1000 or lastVL=1302), 'Suppressed', if(lastVL !=null and lastVL>=1000, 'Unsuppressed', null)) lastVLWithinPeriod,lastVL  from\n" +
                "      (select a.patient_id,\n" +
                "         a.date_started_art,\n" +
                "         mch.patient_id                                               as inMCH,\n" +
                "         mch.latest_mch_date                                          as latest_mch_date,\n" +
                "         mid(max(concat(l.visit_date, l.test_result)), 11)            as lastVL,\n" +
                "         left(max(concat(l.visit_date, l.test_result)), 10)           as lastVLDateWithinPeriod,\n" +
                "         left(max(concat(l_ever.visit_date, l_ever.test_result)), 10) as lastVLDate,\n" +
                "         a.dob                                                        as dob,\n" +
                "         encounter\n" +
                "       from (select e.patient_id, min(date_started) as date_started_art, p.DOB as dob,o.encounter_id as encounter from kenyaemr_etl.etl_otz_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics p on e.patient_id = p.patient_id and p.voided = 0\n" +
                "         inner join kenyaemr_etl.etl_drug_event d on d.patient_id = e.patient_id and ifnull(d.voided, 0) = 0\n" +
                "         inner join openmrs.encounter enc on e.patient_id=enc.patient_id\n" +
                "         inner join openmrs.orders o on o.encounter_id =  enc.encounter_id\n" +
                "       where o.order_type_id = 3 and o.order_reason=161236 and o.date_activated  between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "       group by e.patient_id\n" +
                "       having timestampdiff(MONTH,min(d.date_started),:endDate ) = \"+month+\") a\n" +
                "         left join (select mch.patient_id,di.patient_id as disc_patient,max(date(mch.visit_date)) as latest_mch_date,max(date(di.visit_date)) as disc_date,di.program_name from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "           left join kenyaemr_etl.etl_patient_program_discontinuation di on mch.patient_id = di.patient_id\n" +
                "         group by mch.patient_id having ((latest_mch_date > disc_date and di.program_name = 'MCH Mother') or di.patient_id is null) and latest_mch_date between date_sub(:endDate, interval \"+month+\" month) and :endDate) mch on mch.patient_id = a.patient_id\n" +
                "         inner join kenyaemr_etl.etl_laboratory_extract l on l.patient_id = a.patient_id and l.lab_test = 1305\n" +
                "         left join kenyaemr_etl.etl_laboratory_extract l_ever on l_ever.patient_id = a.patient_id and l_ever.lab_test = 1305\n" +
                "       group by a.patient_id) o\n" +
                "  ) e\n" +
                "where e.lastVL =\"LDL\"  and  e.lastVLDate between  date_sub(:endDate , interval :month MONTH) and date(:endDate);";
        cd.setName("PatientWithRoutineVLResultsLDL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with routine vl results reported as LDL");

        return cd;
    }

    public CohortDefinition patientWithRepeatVLLessThan1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_otz_enrollment e\n" +
                "       inner join\n" +
                "     (\n" +
                "       select\n" +
                "         patient_id,\n" +
                "         visit_date,\n" +
                "         if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "         urgency,order_id\n" +
                "       from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test in (1305, 856)  and visit_date between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "     ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "     inner join orders o on o.order_id =vl_result.order_id\n" +
                "     where vl_result.order_id is not null and o.order_reason in(843,163523)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";
        cd.setName("PatientWithRepeatVLLessThan1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with repeat vl less than 1000 copies/ml");

        return cd;
    }

    public CohortDefinition patientWithRepeatVLMoreThan1000(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_otz_enrollment e\n" +
                "       inner join\n" +
                "     (\n" +
                "       select\n" +
                "         patient_id,\n" +
                "         visit_date,\n" +
                "         if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "         urgency,order_id\n" +
                "       from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test in (1305, 856)  and visit_date between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "     ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "     inner join orders o on o.order_id =vl_result.order_id\n" +
                "     where vl_result.order_id is not null and o.order_reason in(843,163523)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)>=1000;";
        cd.setName("PatientWithRepeatVLMoreThan1000");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with repeat vl more than 1000 copies/ml");

        return cd;
    }

    public CohortDefinition patientWithRepeatVLLessThan400(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_otz_enrollment e\n" +
                "       inner join\n" +
                "     (\n" +
                "       select\n" +
                "         patient_id,\n" +
                "         visit_date,\n" +
                "         if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "         urgency,order_id\n" +
                "       from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test in (1305, 856)  and visit_date between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "     ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "     inner join orders o on o.order_id =vl_result.order_id\n" +
                "     where vl_result.order_id is not null and o.order_reason in(843,163523)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<400;";
        cd.setName("PatientWithRepeatVLLessThan400");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with repeat vl less than 400 copies/ml");

        return cd;
    }

    public CohortDefinition patientWithRepeatVLResults(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct e.patient_id,vl_result\n" +
                "from kenyaemr_etl.etl_otz_enrollment e\n" +
                "       inner join\n" +
                "     (\n" +
                "       select\n" +
                "         patient_id,\n" +
                "         visit_date,\n" +
                "         if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "         urgency,order_id\n" +
                "       from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "     ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "     inner join orders o on o.order_id =vl_result.order_id\n" +
                "     where vl_result.order_id is not null and o.order_reasonin(843,163523)\n" +
                "group by e.patient_id\n";
        cd.setName("PatientWithRepeatVLResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with repeat vl Results");

        return cd;
    }

    public CohortDefinition patientWithRepeatVLasLDL(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_otz_enrollment e\n" +
                "       inner join\n" +
                "     (\n" +
                "       select\n" +
                "         patient_id,\n" +
                "         visit_date,\n" +
                "         if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "         urgency,order_id\n" +
                "       from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test in (1305, 856)  and visit_date between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "     ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "     inner join orders o on o.order_id =vl_result.order_id\n" +
                "     where vl_result.order_id is not null and o.order_reason in(843,163523)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\"\n";
        cd.setName("PatientWithRepeatVLasLDL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with repeat vl as LDL");

        return cd;
    }

    public CohortDefinition patientSwitchToSecondLineART(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct patient_id\n" +
                "from (\n" +
                "       select f1.patient_id,max(f1.visit_date) as visit_date\n" +
                "       from kenyaemr_etl.etl_drug_event f1\n" +
                "              join kenyaemr_etl.etl_drug_event f2 on f1.visit_date>f2.visit_date\n" +
                "         and f1.patient_id=f2.patient_id\n" +
                "               join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "               join kenyaemr_etl.etl_otz_enrollment ot on ot.patient_id=f1.patient_id\n" +
                "       where date(f1.visit_date) between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "         and f1.regimen_line=\"Second line\" and f2.regimen_line=\"First line\" and f1.discontinued is null\n" +
                "\n" +
                "       group by f1.patient_id, f1.visit_date)vis;";
        cd.setName("PatientSwitchToSecondLine");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient switch to second line");

        return cd;
    }

    public CohortDefinition patientSwitchToThirdLineART(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select distinct patient_id\n" +
                "from (\n" +
                "       select f1.patient_id,max(f1.visit_date) as visit_date\n" +
                "       from kenyaemr_etl.etl_drug_event f1\n" +
                "              join kenyaemr_etl.etl_drug_event f2 on f1.visit_date>f2.visit_date\n" +
                "         and f1.patient_id=f2.patient_id\n" +
                "              join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "              join kenyaemr_etl.etl_otz_enrollment ot on ot.patient_id=f1.patient_id\n" +
                "       where date(f1.visit_date) between date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "         and f1.regimen_line=\"Third line\" and f2.regimen_line=\"Second line\" and f1.discontinued is null\n" +
                "\n" +
                "       group by f1.patient_id, f1.visit_date)vis;";
        cd.setName("PatientSwitchToThirdLineART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient switch to third line");

        return cd;
    }

    public CohortDefinition patientWithVLMoreThan1000AtEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "     (\n" +
                "       select\n" +
                "         patient_id,\n" +
                "         visit_date,\n" +
                "         if(lab_test = 856, test_result, \"\") as vl_result,\n" +
                "         urgency\n" +
                "       from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test = 856 and visit_date between  date_sub(:endDate , interval :month MONTH) and date(:endDate)\n" +
                "     ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "       left JOIN\n" +
                "     (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "      where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "      group by patient_id\n" +
                "     ) d on d.patient_id = e.patient_id\n" +
                "       inner JOIN(select patient_id, visit_date from kenyaemr_etl.etl_otz_enrollment\n" +
                "                  where date(visit_date) <= date(:endDate)\n" +
                "                  group by patient_id) ot on ot.patient_id = e.patient_id\n" +
                "where e.program = 'HIV' and vl_result.visit_date <= ot.visit_date\n" +
                "group by e.patient_id\n" +
                "having  mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)>1000;";
        cd.setName("patientWithVLMoreThan1000AtEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patient with VL results more 1000 copies/ml at enrollment");

        return cd;
    }


    public CohortDefinition otzMembersWhoCompletedAllModules(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_otz_enrollment e join  (select patient_id, min(a.visit_date) as firstVisitDate,\n" +
                "   group_concat(\n" +
                "       concat_ws(',',if(a.orientation = 'Yes',1,null),if(a.leadership = 'Yes',2,null),if(a.participation = 'Yes',3,null),\n" +
                "                 if(a.treatment_literacy = 'Yes',4,null),if(a.transition_to_adult_care = 'Yes',5,null),\n" +
                "                 if(a.making_decision_future = 'Yes',6,null),if(a.srh = 'Yes',7,null),if(a.beyond_third_ninety = 'Yes',8,null)\n" +
                "         )) as modules_completed\n" +
                "        from kenyaemr_etl.etl_otz_activity a group by patient_id)a on e.patient_id = a.patient_id\n" +
                "where length(a.modules_completed)>=15\n" +
                "  and firstVisitDate between date_sub(:endDate , interval :month MONTH) and date(:endDate);";
        cd.setName("OtzMembersWhoCompletedAllModules");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of OTZ members 10-19 yrs who completed 8 modules");

        return cd;
    }






}
