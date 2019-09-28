/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 1/14/17.
 */

/**
 * Library of cohort definitions used specifically in the MOH731 report based on ETL tables. It has incorporated green card components
 */
@Component


public class ETLMoh731GreenCardCohortLibrary {
    public CohortDefinition hivEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id " +
                "from kenyaemr_etl.etl_hiv_enrollment e " +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "where  e.entry_point <> 160563  and transfer_in_date is null " +
                "and date(e.visit_date) between date(:startDate) and date(:endDate) and (e.patient_type not in (160563, 164931, 159833) or e.patient_type is null ) " +
                ";";
        cd.setName("newHhivEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New HIV Enrollment");

        return cd;
    }

    /**
     * TODO: Review query. It takes time to execute
     * @return
     */
    public  CohortDefinition currentlyInCare() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery=" select  e.patient_id\n" +
                "from ( \n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "max(fup.visit_date) as latest_vis_date,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "  max(d.visit_date) as date_discontinued,\n" +
                "  d.patient_id as disc_patient \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id \n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id \n" +
                // ensure those discontinued are catered for
                "left outer JOIN\n" +
                "  (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" + //check if this line is necessary
                "  ) d on d.patient_id = fup.patient_id \n" +
                "where fup.visit_date <= date(:endDate) \n" +
                "group by patient_id \n" +
//                "--  we may need to filter lost to follow-up using this\n" +
                "having ((date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or \n" +
                "(((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
//                "-- drop missd completely\n" +
                ") e\n" ;


        cd.setName("currentlyInCare");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currently In Care");

        return cd;
    }

    public  CohortDefinition startedOnART() {
        String sqlQuery="select  net.patient_id\n" +
                "from (\n" +
                "select e.patient_id,e.date_started,\n" +
                "d.visit_date as dis_date,\n" +
                "if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "max(fup.visit_date) as latest_vis_date\n" +
                "from (select e.patient_id, min(e.date_started) as date_started,\n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "max(if(discontinued,1,0))as alternative_regimen\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                " join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "where e.program = 'HIV'\n" +
                "group by e.patient_id) e\n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having TI_on_art=0\n" +
                "     )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART");
        return cd;
    }

    public CohortDefinition currentlyOnArt() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery=" select  e.patient_id\n" +
                "from (\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "    max(fup.visit_date) as latest_vis_date,\n" +
                "    mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "    max(d.visit_date) as date_discontinued,\n" +
                "    d.patient_id as disc_patient,\n" +
                "  de.patient_id as started_on_drugs,\n" +
                "  de.program as hiv_program\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where de.program = 'HIV' and fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "(date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "(((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
                ") e\n" +
                ";";

        cd.setName("currentlyOnArt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currently on ART");
        return cd;
    }

    public CohortDefinition revisitsArt() {
        String sqlQuery="select  e.patient_id\n" +
                "from (\n" +
                "select fup.visit_date,fup.patient_id,\n" +
                "min(e.visit_date) as enroll_date,\n" +
                "max(fup.visit_date) as latest_vis_date,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e  on fup.patient_id=e.patient_id\n" +
                "where (fup.visit_date between date_sub(date(:startDate), interval 3 month) and date(:endDate))\n" +
                "group by patient_id\n" +
                "having (latest_tca>date(:endDate) or\n" +
                "(latest_tca between date(:startDate) and date(:endDate) and latest_vis_date between date(:startDate) and date(:endDate)) )\n" +
                ") e\n" +
                "where e.patient_id not in (select patient_id from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                "having if(e.latest_tca>max(visit_date),1,0)=0)\n" +
                "and e.patient_id in (select patient_id\n" +
                "from (select e.patient_id,min(e.date_started) as date_started,\n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "max(if(discontinued,1,0))as alternative_regimen\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "where e.program = 'HIV'\n" +
                "group by e.patient_id) e\n" +
                "where date(e.date_started)<date(:startDate));";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("revisitsArt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Revisits on ART");
        return cd;
    }

    public CohortDefinition cummulativeOnArt() {
        String sqlQuery=" select  net.patient_id \n" +
                "from (\n" +
                "select e.patient_id,e.date_started,min(enr.visit_date) as enrollment_date,\n" +
                "e.regimen, \n" +
                "e.regimen_line,\n" +
                "e.alternative_regimen,\n" +
                "d.visit_date as dis_date,\n" +
                "max(if(d.visit_date is not null, 1, 0)) as TOut, \n" +
                "max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "enr.transfer_in_date,max(fup.visit_date) as latest_vis_date, \n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca \n" +
                "from (select e.patient_id,min(e.date_started) as date_started, \n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "max(if(discontinued,1,0))as alternative_regimen \n" +
                "from kenyaemr_etl.etl_drug_event e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "where e.program = 'HIV' \n" +
                "group by e.patient_id) e \n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' \n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "group by e.patient_id \n" +
                "having  (TI_on_art =0 and date_started<=date(:endDate) ) )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("cummulativeOnArt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Cummulative ever on ART");
        return cd;
    }

    /**
     * TODO: review this query. it is a killer
     * @return
     */
    public CohortDefinition startingARTPregnant() {

        String sqlQuery = " select  fup.patient_id " +
                "from kenyaemr_etl.etl_patient_hiv_followup fup " +
                "join (select patient_id from kenyaemr_etl.etl_drug_event e " +
                "where e.program = 'HIV' and e.date_started between date(:startDate) and date(:endDate)) started_art on  " +
                "started_art.patient_id = fup.patient_id " +
                "where fup.pregnancy_status =1065 " +
                "and fup.visit_date between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingARTPregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women Started on ART");
        return cd;

    }

    public CohortDefinition startingARTWhileTbPatient() {

        String sqlQuery = "select  fup.patient_id \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup \n" +
                "join (select patient_id from kenyaemr_etl.etl_drug_event e \n" +
                "where e.program = 'HIV' and date_started between date(:startDate) and date(:endDate)) started_art on  \n" +
                "started_art.patient_id = fup.patient_id \n" +
                "join kenyaemr_etl.etl_tb_enrollment tb on tb.patient_id=fup.patient_id\n" +
                "where fup.visit_date between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingARTonTb");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tb positive Started on ART");
        return cd;

    }

    public CohortDefinition tbScreening() {
// look all active in care who were screened for tb
        String sqlQuery = " select  e.patient_id\n" +
                "from (\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "  max(fup.visit_date) as latest_vis_date,\n" +
                "  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "  max(d.visit_date) as date_discontinued,\n" +
                "  d.patient_id as disc_patient,\n" +
                "  mid(max(concat(fup.visit_date, tb.visit_date)), 11) screened_using_icf,\n" +
                "mid(max(concat(fup.visit_date, fup.tb_status)), 11) screened_using_consultation\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id and date(fup.visit_date) = date(tb.visit_date)\n" +
                "left outer JOIN\n" +
                "  (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (\n" +
                "  (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (screened_using_icf is not null or screened_using_consultation in(1660, 142177, 160737 ))) or\n" +
                "(((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (screened_using_icf is not null or screened_using_consultation in(1660, 142177, 160737 ))) )\n" +
                ") e";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbScreening");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for TB");
        return cd;

    }

    public CohortDefinition condomsProvided() {

        String sqlQuery = " select  e.patient_id " +
                "from kenyaemr_etl.etl_patient_hiv_followup e " +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "where date(e.visit_date) between date(:startDate) and date(:endDate) and  condom_provided=1065 ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("pwpCondom_provided");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("pwp - Condoms Provided");
        return cd;
    }

    public CohortDefinition modernContraceptivesProvided() {

        String sqlQuery = " select  e.patient_id " +
                "from kenyaemr_etl.etl_patient_hiv_followup e " +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "where  date(e.visit_date) between date(:startDate) and date(:endDate) and (family_planning_method is not null and family_planning_method<>190) ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("pwpModernContraceptivesProvided");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PWP - Modern Contraceptives Provided");
        return cd;
    }



    public CohortDefinition art12MonthNetCohort() {

        String sqlQuery = "  select  net.patient_id\n" +
                "  from ( \n" +
                "  select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "   from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "   from kenyaemr_etl.etl_drug_event e \n" +
                "   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "   where e.program='HIV' \n" +
                "   group by e.patient_id) e \n" +
                "   left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' \n" +
                "   left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "   left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "   where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) \n" +
                "   group by e.patient_id \n" +
                "   having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) \n" +
                "   )net; ";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("art12MonthNetCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("art 12 Months Net Cohort");
        return cd;

    }

    public CohortDefinition onOriginalFirstLineAt12Months() {

        String sqlQuery = "  select  net.patient_id " +
                "  from ( " +
                "  select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "   e.regimen, e.regimen_line, e.alternative_regimen, mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, "+
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date" +
                "    from (select e.patient_id,min(e.date_started) as date_started, " +
                "    mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, " +
                "    mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, " +
                "    max(if(discontinued,1,0))as alternative_regimen " +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where e.program='HIV' " +
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>date(:endDate) or dis_date is null) and (regimen_line='1st Line' and alternative_regimen=0) )net; ";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onOriginalFirstLineAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Original FirstLine At 12 Months");
        return cd;

    }

    public CohortDefinition onAlternateFirstLineAt12Months() {

        String sqlQuery = "  select  net.patient_id " +
                "  from ( " +
                "  select e.patient_id,e.date_started,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "   e.regimen, e.regimen_line, e.alternative_regimen, mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, "+
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date" +
                "    from (select e.patient_id,min(e.date_started) as date_started, " +
                "    mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, " +
                "    mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, " +
                "    max(if(discontinued,1,0))as alternative_regimen " +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where e.program='HIV' " +
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>date(:endDate) or dis_date is null) and (regimen_line='1st Line' and alternative_regimen=1) )net; ";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onAlternateFirstLineAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Alternate First Line At 12 Months");
        return cd;

    }

    public CohortDefinition onSecondLineAt12Months() {

        String sqlQuery = "  select  net.patient_id " +
                "  from ( " +
                "  select e.patient_id,e.date_started,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "   e.regimen, e.regimen_line, e.alternative_regimen, mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, "+
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date" +
                "    from (select e.patient_id,min(e.date_started) as date_started, " +
                "    mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, " +
                "    mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, " +
                "    max(if(discontinued,1,0))as alternative_regimen " +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where e.program='HIV' " +
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>date(:endDate) or dis_date is null) and (regimen_line='2nd Line') )net; ";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onSecondLineAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Second Line At 12Months");
        return cd;

    }

    public CohortDefinition onTherapyAt12Months() {

        String sqlQuery = "    select  net.patient_id\n" +
                "  from ( \n" +
                "  select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                " mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                " if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "   from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "   from kenyaemr_etl.etl_drug_event e \n" +
                "   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "   where e.program='HIV' \n" +
                "   group by e.patient_id) e \n" +
                "   left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' \n" +
                "   left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "   left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "   where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) \n" +
                "   group by e.patient_id \n" +
                "   having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) and (\n" +
                "        (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(dis_date) or dis_date is null ))  or \n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_tca) >= date(latest_vis_date)) ) ) or \n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and \n" +
                "         (date(latest_tca) > date(dis_date) or dis_date is null )\n" +
                "        )\n" +
                "   )net; ";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onTherapyAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Therapy At 12 Months");
        return cd;

    }

    /*Patients with Suppressed  VL within last 12 Months*/
    public CohortDefinition patientsWithSuppressedVlLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "        (\n" +
                "         select\n" +
                "                patient_id,\n" +
                "                visit_date,\n" +
                "                if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                urgency\n" +
                "         from kenyaemr_etl.etl_laboratory_extract\n" +
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "       left JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWithSuppressedVlLast12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Suppressed VL within last 12 Months");
        return cd;
    }

    /*Patients with VL results within last 12 Months*/
    public CohortDefinition patientsWithVLResultsLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "        (\n" +
                "         select\n" +
                "                patient_id,\n" +
                "                visit_date,\n" +
                "                if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                urgency\n" +
                "         from kenyaemr_etl.etl_laboratory_extract\n" +
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "       left JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsWithVLResultsLast12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with VL results within last 12 Months");
        return cd;
    }

    public CohortDefinition hivCareVisitsFemale18() {

        String sqlQuery = "select  e.patient_id " +
                "from kenyaemr_etl.etl_patient_hiv_followup e " +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "where timestampdiff(year,p.dob,date(:endDate))>=18 and p.gender='F' and date(e.visit_date) between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivCareVisitsFemales18");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Care Visits Female 18 and above");
        return cd;

    }

    public CohortDefinition hivCareVisitsScheduled() {

        String sqlQuery = " select  patient_id \n" +
                "from (\n" +
                "select f1.patient_id,max(f1.visit_date) as visit_date, max(f2.next_appointment_date) as next_appointment_date \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup f1\n" +
                "join kenyaemr_etl.etl_patient_hiv_followup f2 on f1.visit_date>f2.visit_date\n" +
                "and f1.patient_id=f2.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "where date(f1.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by f1.patient_id, f1.visit_date)vis where visit_date=next_appointment_date";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivCareVisitsScheduled");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Care Visits Scheduled");
        return cd;

    }

    public CohortDefinition hivCareVisitsUnscheduled() {

        String sqlQuery = " select  patient_id \n" +
                "from (\n" +
                "select f1.patient_id,max(f1.visit_date) as visit_date, max(f2.next_appointment_date) as next_appointment_date \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup f1\n" +
                "join kenyaemr_etl.etl_patient_hiv_followup f2 on f1.visit_date>f2.visit_date\n" +
                "and f1.patient_id=f2.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "where date(f1.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by f1.patient_id, f1.visit_date)vis where visit_date<>next_appointment_date";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivCareVisitsUnscheduled");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Care Visits Unscheduled");
        return cd;

    }

    public CohortDefinition hivCareVisitsTotal() {

        String sqlQuery = " select  patient_id \n" +
                "from (\n" +
                "select f1.patient_id,max(f1.visit_date) as visit_date, max(f2.next_appointment_date) as next_appointment_date \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup f1\n" +
                "join kenyaemr_etl.etl_patient_hiv_followup f2 on f1.visit_date>f2.visit_date\n" +
                "and f1.patient_id=f2.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=f1.patient_id\n" +
                "where date(f1.visit_date) between date(:startDate) and date(:endDate)\n" +
                "group by f1.patient_id, f1.visit_date)vis";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivCareVisitsTotal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Care Visits Total");
        return cd;

    }

    public CohortDefinition inHivProgramAndOnCtxProphylaxis() {
        String sqlQuery=" select  e.patient_id\n" +
                "from (\n" +
                "select fup.visit_date,fup.patient_id,\n" +
                "  max(fup.visit_date) as latest_vis_date,\n" +
                "  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "  max(d.visit_date) as date_discontinued,\n" +
                "  d.patient_id as disc_patient,\n" +
                "  max(if(dr.is_ctx=1, 1, if(dr.is_dapsone =1, 1, 0))) as prophylaxis_given, \n" +
                "max(if(fup.ctx_dispensed in (105281,74250,1065), 1, 0)) as ctx_dispensed,\n" +
                "max(if(fup.dapsone_dispensed in (105281,74250,1065), 1, 0)) as dapsone_dispensed\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left join kenyaemr_etl.etl_pharmacy_extract dr on date(fup.visit_date) = date(dr.visit_date) and dr.patient_id = fup.patient_id \n" +
                "left outer JOIN\n" +
                "  (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (\n" +
                "  (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (ctx_dispensed = 1 or dapsone_dispensed=1 or prophylaxis_given = 1 )) or\n" +
                "(((latest_tca between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (ctx_dispensed = 1 or dapsone_dispensed=1 or prophylaxis_given = 1 )) )\n" +
                ") e" +
                "; ";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("inHivProgramAndOnCtxProphylaxis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("In Hiv Program And On Ctx Prophylaxis");
        return cd;
    }
    protected CohortDefinition hivExposedInfantsWithin2Months() {
        String sqlQuery = " select  e.patient_id " +
                "    from kenyaemr_etl.etl_hei_enrollment e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where  child_exposed=822 and timestampdiff(month,p.dob,:endDate)<=2 and date(e.visit_date) between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivExposedInfantsWithin2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Exposed Infants Within 2 Months");
        return cd;
    }

    protected CohortDefinition hivExposedInfantsWithin2MonthsAndEligibleForCTX() {
        String sqlQuery = " select  e.patient_id " +
                "    from kenyaemr_etl.etl_hei_enrollment e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where  child_exposed=822 and timestampdiff(month,p.dob,:endDate)<=2 and date(e.visit_date) between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivExposedInfantsWithin2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Exposed Infants Within 2 Months");
        return cd;
    }

    /**
     * HTS Cohort Definitions
     */

    // HIV testing cohort. includes those who tested during the reporting period
    public CohortDefinition htsNumberTested() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1 and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTested");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested");
        return cd;

    }

    // facility strategies include PITC, Non Provider initiated testing, integrated vct, stand alone vct
    public CohortDefinition htsNumberTestedAtFacility() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test WHERE test_type =1\n" +
                " and test_strategy in ('Integrated VCT Center','Non Provider Initiated Testing','Provider Initiated Testing(PITC)', 'Stand Alone VCT Center')" +
                " and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedAtFacility");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested At Facility");
        return cd;

    }

    // community strategies include Home based testing, mobile outreaches and other
    public CohortDefinition htsNumberTestedAtCommunity() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test WHERE test_type =1 \n" +
                " and test_strategy in ('Mobile Outreach HTS','Home Based Testing','Other')" +
                " and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedAtCommunity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested At Community");
        return cd;

    }

    protected CohortDefinition htsNumberTestedAsCouple() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and client_tested_as ='Couple' and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedAsCouple");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested As a Couple");
        return cd;

    }

    protected CohortDefinition htsNumberTestedKeyPopulation() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1 \n" +
                " and population_type ='Key Population' and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedKeyPopulation");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested As a Key population");
        return cd;

    }

    public CohortDefinition htsNumberTestedPositive() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and final_test_result ='Positive' and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Positive");
        return cd;

    }

    public CohortDefinition htsNumberTestedNegative() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and final_test_result ='Negative' and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedNegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Negative");
        return cd;

    }

    public CohortDefinition htsNumberTestedDiscordant() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and couple_discordant ='Yes' and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedDiscordant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Discordant");
        return cd;

    }

    public CohortDefinition htsNumberTestedKeypopPositive() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and population_type ='Key Population' and final_test_result='Positive' and visit_date between :startDate and :endDate";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedKeypopPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Key population");
        return cd;

    }

    public CohortDefinition htsNumberTestedPositiveAndLinked() {
        String sqlQuery = "select r.patient_id \n" +
                " from kenyaemr_etl.etl_hts_referral_and_linkage r \n" +
                "  inner join kenyaemr_etl.etl_hts_test t on r.patient_id = t.patient_id and t.final_test_result = 'Positive' \n" +
                " where (r.ccc_number !=''  and r.ccc_number IS NOT NULL) and (r.facility_linked_to !='' and r.facility_linked_to IS NOT NULL) \n" +
                "      and t.visit_date between date_sub(:endDate , interval 3 MONTH) and  :endDate ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedPositiveAndLinked");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Positive and Linked");
        return cd;

    }

    // number tested in the last 3 months
    public CohortDefinition htsNumberTestedPositiveInLastThreeMonths() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test t where test_type in (1, 2) and voided = 0 \n" +
                " and final_test_result ='Positive' and t.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedPositiveInLastThreeMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Positive in The Last Three Months");
        return cd;

    }

    // new tests during the reporting period
    public CohortDefinition htsNumberTestedNew() {
        String sqlQuery = "select patient_id\n" +
                "from (\n" +
                "  SELECT\n" +
                "    patient_id,\n" +
                "    visit_date      AS test_date,\n" +
                "    min(visit_date) AS first_test_date,\n" +
                "    max(visit_date) AS last_test_date\n" +
                "  FROM kenyaemr_etl.etl_hts_test\n" +
                "  WHERE test_type = 1\n" +
                "  GROUP BY patient_id\n" +
                ") t\n" +
                "where test_date between :startDate and :endDate and first_test_date between :startDate and :endDate\n" +
                "GROUP BY patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedNew");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested New");
        return cd;

    }

    // repeat tests during the reporting period
    public CohortDefinition htsNumberTestedRepeat() {
        String sqlQuery = "select patient_id\n" +
                "from (\n" +
                "       SELECT\n" +
                "         patient_id,\n" +
                "         visit_date      AS test_date,\n" +
                "         min(visit_date) AS first_test_date,\n" +
                "         max(visit_date) AS last_test_date\n" +
                "       FROM kenyaemr_etl.etl_hts_test\n" +
                "       WHERE test_type = 1\n" +
                "       GROUP BY patient_id\n" +
                "     ) t\n" +
                "where test_date between :startDate and :endDate and first_test_date < :startDate\n" +
                "GROUP BY patient_id\n" +
                "having if(count(distinct test_date) > 1, 1, 0) = 1;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedRepeat");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested repeat");
        return cd;

    }

    /**
     * pre-art cohort
     * looks for those qualify to be current in care but haven't started on arvs
     */
    public CohortDefinition preArtCohort() {
        String sqlQuery = "select patient_id from (\n" +
                "  SELECT \n" +
                "    fup.visit_date, \n" +
                "    fup.patient_id, \n" +
                "    p.dob, \n" +
                "    p.Gender, \n" +
                "    min(e.visit_date)  AS enroll_date, \n" +
                "    max(fup.visit_date) AS latest_vis_date, \n" +
                "    mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11) AS latest_tca, \n" +
                "    max(d.visit_date)     AS date_discontinued, \n" +
                "    d.patient_id  AS disc_patient, \n" +
                "    de.patient_id  AS started_on_drugs \n" +
                "  FROM kenyaemr_etl.etl_patient_hiv_followup fup \n" +
                "    JOIN kenyaemr_etl.etl_patient_demographics p ON p.patient_id = fup.patient_id \n" +
                "    JOIN kenyaemr_etl.etl_hiv_enrollment e ON fup.patient_id = e.patient_id \n" +
                "    LEFT OUTER JOIN kenyaemr_etl.etl_drug_event de ON e.patient_id = de.patient_id AND date(date_started) <= date(:endDate) \n" +
                "    LEFT OUTER JOIN \n" +
                "    (SELECT \n" +
                "       patient_id, \n" +
                "       visit_date \n" +
                "     FROM kenyaemr_etl.etl_patient_program_discontinuation \n" +
                "     WHERE date(visit_date) <= date(:endDate) AND program_name = 'HIV' \n" +
                "     GROUP BY patient_id \n" +
                "    ) d ON d.patient_id = fup.patient_id \n" +
                "  WHERE de.program = 'HIV' and date(fup.visit_date) <= date(:endDate) \n" +
                "  GROUP BY patient_id \n" +
                "  HAVING ( \n" +
                "    (date(latest_tca) > date(:endDate) AND (date(latest_tca) > date(date_discontinued) OR disc_patient IS NULL)) OR \n" +
                "    (((date(latest_tca) BETWEEN date(:startDate) AND date(:endDate)) OR (date(latest_vis_date) BETWEEN date(:startDate) AND date(:endDate))) AND \n" +
                "     (latest_tca > date_discontinued OR disc_patient IS NULL))) \n" +
                ") active \n" +
                "where started_on_drugs = '' or started_on_drugs is null ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("preARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Those in care, not started on ARVs");
        return cd;

    }

    /**
     * started on ipt
     * looks for patients started on ipt during a given period
     */
    public CohortDefinition startedOnIPT() {
        String sqlQuery = "select patient_id \n" +
                "from kenyaemr_etl.etl_ipt_screening \n" +
                "where visit_date between :startDate and :endDate and ipt_started=1065 " +
                " ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startedOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients started on IPT");
        return cd;

    }

    /**
     * those started on ipt 12 months ago and have successfully completed
     */
    public CohortDefinition completedIPT12Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id \n" +
                "from kenyaemr_etl.etl_ipt_follow_up f\n" +
                "inner join kenyaemr_etl.etl_ipt_screening s on s.visit_date = DATE_SUB(date(:endDate), INTERVAL 1 YEAR) and s.ipt_started=1065 and f.patient_id = s.patient_id\n" +
                "where f.visit_date between DATE_SUB(date(:endDate), INTERVAL 1 YEAR) and date(:endDate) and f.outcome=1267 " +
                ";";
        cd.setName("completedIPT12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("IPT 12 months cohort who have completed");

        return cd;
    }

    /**
     * newly enrolled tb patients
     * @return
     */
    public CohortDefinition tbEnrollment() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id " +
                " from kenyaemr_etl.etl_tb_enrollment e " +
                " join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                " where date(e.visit_date) between :startDate and :endDate " +
                ";";
        cd.setName("newTBEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB Enrollment");

        return cd;
    }

    /**
     * new tb cases who are known positive
     * @return
     */
    public CohortDefinition tbNewKnownPositive() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id \n" +
                "from kenyaemr_etl.etl_tb_enrollment e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "inner join kenyaemr_etl.etl_hiv_enrollment he on he.patient_id=e.patient_id and he.visit_date > e.visit_date\n" +
                "where  date(e.visit_date) between :startDate and :endDate " +
                ";";
        cd.setName("newTBKnownPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB HIV Known Positive");

        return cd;
    }

    /**
     * new tb cases who took hiv test
     * @return
     */
    public CohortDefinition tbTestedForHIV() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id \n" +
                "from kenyaemr_etl.etl_tb_enrollment e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "inner join kenyaemr_etl.etl_hts_test h on h.patient_id=e.patient_id and h.visit_date between :startDate and :endDate\n" +
                "where  date(e.visit_date) between :startDate and :endDate " +
                ";";
        cd.setName("newTBKnownPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB HIV Known Positive");

        return cd;
    }

    /**
     * new tb cases who tested hiv positive
     * @return
     */
    public CohortDefinition tbNewTestedHIVPositive() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id \n" +
                "from kenyaemr_etl.etl_tb_enrollment e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "inner join kenyaemr_etl.etl_hts_test h on h.patient_id=e.patient_id and h.visit_date between :startDate and :endDate and h.final_test_result=\"Positive\"\n" +
                "where  date(e.visit_date) between :startDate and :endDate " +
                ";";
        cd.setName("newTBTestedHIVPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB cases tested HIV Positive");

        return cd;
    }

    /**
     * new tb cases already on HAART at diagnosis
     * @return
     */
    public CohortDefinition tbNewAlreadyOnHAART() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id\n" +
                "from kenyaemr_etl.etl_tb_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.date_started < e.visit_date\n" +
                "where d.program = 'HIV' and date(e.visit_date) between :startDate and :endDate ;";
        cd.setName("tbNewAlreadyOnHAART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB cases already on HAART");

        return cd;
    }

    /**
     * TB newly diagnosed started on HAART within a period
     * @return
     */
    public CohortDefinition tbNewStartingHAART() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id\n" +
                "from kenyaemr_etl.etl_tb_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.date_started between :startDate and :endDate\n" +
                "where d.program = 'HIV' and date(e.visit_date) between :startDate and :endDate;";
        cd.setName("tbNewStartingHAART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB cases started on HAART");

        return cd;
    }

    /**
     * total of tb patients receiving HAART
     * @return
     */
    public CohortDefinition tbTotalOnHAART() {
            SqlCohortDefinition cd = new SqlCohortDefinition();
            String sqlQuery = "select  e.patient_id\n" +
                    "from kenyaemr_etl.etl_tb_enrollment e\n" +
                    "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                    "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.date_started <= :endDate\n" +
                    "where d.program = 'HIV' and date(e.visit_date) between :startDate and :endDate;";
        cd.setName("tbNewTotalOnHAART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New TB cases on HAART");

        return cd;
    }

    public CohortDefinition screenedForCaCx() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup f \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=f.patient_id \n" +
                "where  date(f.visit_date) between :startDate and :endDate and f.cacx_screening in(703, 664, 1118) " +
                ";";
        cd.setName("screenedforCaCx");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened for CaCx");

        return cd;
    }

    //Updated PMTCT
    //First ANC visit  HV02-01
    public CohortDefinition firstANCVisitMchmsAntenatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =" select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "inner join kenyaemr_etl.etl_mch_enrollment e on v.patient_id = e.patient_id and e.date_of_discontinuation IS NULL\n" +
                "where anc_visit_number = 1 and date(v.visit_date)  between date(:startDate) and date(:endDate);";

        cd.setName("First ANC Visit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested For Hiv Antenatal");

        return cd;
    }

    // Delivery for HIV Positive mothers HV02-02
    public CohortDefinition deliveryFromHIVPositiveMothers(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "   left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= ld.patient_id\n" +
                "   left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= ld.patient_id\n" +
                "   where (date(ld.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "   ld.final_test_result=\"Positive\" or e.hiv_status = 703 or v.final_test_result =\"Positive\" ;";

        cd.setName("Delivery from HIV Positive Mothers");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Delivery from HIV Positive Mothers");

        return cd;
    }
    // Known Positive at 1st ANC HV02-03
    public CohortDefinition knownPositiveAtFirstANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id\n" +
                "   from kenyaemr_etl.etl_mch_enrollment e\n" +
                "   where date(visit_date) between date(:startDate) and date(:endDate) and\n" +
                "   (e.patient_id is not null) and hiv_status=703;";

        cd.setName("Known Positive at First ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Known Positive at First ANC");

        return cd;
    }
    //    Initial test at ANC  HV02-04
    public CohortDefinition initialHIVTestInMchmsAntenatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct v.patient_id\n" +
                "         from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "                     left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
                "                     left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= v.patient_id\n" +
                "                     left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=ld.patient_id\n" +
                "                   where date(v.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "                         e.hiv_status !=703 and\n" +
                "                         ld.final_test_result is null and\n" +
                "                         p.final_test_result is null and\n" +
                "                         v.final_test_result is not null ;";

        cd.setName("Initial HIV Test at ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial HIV Test at ANC");

        return cd;
    }
    //    Initial test at Labour and Delivery  HV02-05
    public CohortDefinition testedForHivInMchmsDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "   left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=ld.patient_id\n" +
                "   left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=ld.patient_id\n" +
                "   left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=ld.patient_id\n" +
                "     where date(ld.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "      e.hiv_status !=703 and\n" +
                "      v.final_test_result is null and\n" +
                "      p.final_test_result is null and\n" +
                "      ld.final_test_result is not null ;";

        cd.setName("Initial Test at Labour and Delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at Labour and Delivery");

        return cd;
    }
    //Initial Test at PNC <=6 Weeks HV02-06
    public CohortDefinition initialTestAtPNCUpto6Weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct p.patient_id\n" +
                "                from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                 left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=p.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "                where date(p.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "                      round(DATEDIFF(ld.date_of_delivery,:endDate)/7) <=6 and\n" +
                "                     e.hiv_status !=703 and\n" +
                "                     v.final_test_result is null and\n" +
                "                     ld.final_test_result is null and\n" +
                "                    p.final_test_result is not null ;";

        cd.setName("Initial Test at PNC <=6 Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at PNC <=6 Weeks");

        return cd;
    }
    //Known HIV Status Total HV02-07 --Computed
    public CohortDefinition testedForHivInMchmsTotal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id\n" +
                "    from kenyaemr_etl.etl_mch_enrollment e\n " +
                "    where e.hiv_test_date between date(:startDate) and date(:endDate) ;";
        cd.setName("testedForHivInMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers tested For Hiv In Mch Program");

        return cd;
    }
    //Retesting PNC <=6 weeks HV02-08
    public CohortDefinition pncRetestUpto6Weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct pnc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc\n" +
                "       left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=pnc.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=pnc.patient_id\n" +
                "where date(pnc.visit_date) between (:startDate) and (:endDate) and\n" +
                "      pnc.final_test_result is not null and anc.final_test_result =664 and ld.final_test_result = 664\n" +
                "  and round(DATEDIFF(:endDate,ld.date_of_delivery)/7) between 1 and 6;";

        cd.setName("pncRetestUpto6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PNC Retest within 6 weeks");

        return cd;
    }
    //Tested PNC >6 weeks and <= 6 months HV02-09
    public CohortDefinition pncTestBtwn6WeeksAnd6Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct pnc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc\n" +
                "       left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=pnc.patient_id\n" +
                "    left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=pnc.patient_id\n" +
                "    where date(pnc.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "    pnc.patient_id is not null and anc.patient_id =664 and ld.patient_id=664 and\n" +
                "    round(DATEDIFF(:endDate,ld.date_of_delivery))/7 between 6 and 24;";

        cd.setName("pncTest6WeeksUpto6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PNC Test between 6 weeks and 6 Months");

        return cd;
    }
    //Known Positive before 1st ANC HV02-10
    /*public CohortDefinition knownPositiveAtFirstANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id " +
                "    where (e.visit_date between date(:startDate) and date(:endDate)) " +
                "    and hiv_status=703 and e.visit_date>hiv_test_date;";

        cd.setName("knownHIVPositiveBeforeFirstAnc");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Hiv Positive Before First ANC visit");

        return cd;
    }*/
    //Tested HIV Positive at ANC HV02-11
    public CohortDefinition testedHivPositiveInMchmsAntenatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "       left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "    (e.hiv_status !=703) and\n" +
                "    v.final_test_result =\"POSITIVE\";";

        cd.setName("Tested Hiv Postive at Antenatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Hiv Postive at Antenatal");

        return cd;
    }

//Tested HIV Positive during Labour and Delivery HV02-12

    public CohortDefinition positiveHIVResultsAtLabourAndDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "  left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=ld.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=ld.patient_id\n" +
                "where date(ld.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "      (e.hiv_status !=703) and\n" +
                "      (v.final_test_result is null or v.final_test_result !=\"Positive\") and\n" +
                "      ld.final_test_result =\"Positive\";";

        cd.setName("HIV Positive results during Labour and Delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive results during Labour and Delivery");

        return cd;
    }
    // HIV positive results PNC <=6 weeks) HV02-13
    public CohortDefinition testedHivPositiveInPNCWithin6Weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "  left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "      round(DATEDIFF(ld.date_of_delivery,:endDate)/7) <=6 and\n" +
                "      (e.hiv_status !=703) and\n" +
                "      (v.final_test_result is null or v.final_test_result !=\"Positive\") and\n" +
                "      (ld.final_test_result is null or ld.final_test_result !=\"Positive\") and\n" +
                "      p.final_test_result =\"Positive\";";

        cd.setName("testedHivPositiveInPNCWithin6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PNC within 6 weeks");

        return cd;
    }
    //Total HIV positive Mothers HV02-14
    public CohortDefinition totalHivPositiveMothersInMchms(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id\n " +
                "    from kenyaemr_etl.etl_mch_enrollment e\n " +
                "    where (hiv_status=703 and e.hiv_test_date between date(:startDate) and date(:endDate)) ;";

        cd.setName("totalHivPositiveInMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers tested Hiv Positive In Mch Program");

        return cd;
    }
    //   PNC >6 weeks and <=6 months   HV02-15
    public CohortDefinition totalHivPositivePNC6WeeksTo6monthsInMchms(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "            left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=p.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "    (round(DATEDIFF(p.visit_date,:endDate)/7) between 6 and 24) and\n" +
                "    (e.hiv_status !=703) and\n" +
                "    (v.final_test_result is null or v.final_test_result !=\"Positive\") and\n" +
                "    (ld.final_test_result is null or ld.final_test_result !=\"Positive\") and\n" +
                "    p.final_test_result =\"Positive\";";

        cd.setName("totalHivPositivePNC6WeeksTo6monthsInMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers tested Hiv Positive in PNC Between 7 weeks and 6 months");

        return cd;
    }
    //On HAART at 1st ANC  HV02-16
    public CohortDefinition totalOnHAARTAtFirstANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id\n" +
                "where d.program = 'HIV' and date(e.visit_date) BETWEEN date(:startDate) and date(:endDate)\n" +
                "and d.date_started < e.visit_date;\n";

        cd.setName("totalOnHAARTAtFirstANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On HAART At first ANC");

        return cd;
    }

    //  Start HAART during ANC  HV02-17
    public CohortDefinition startedHAARTAtANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct a.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit a\n" +
                "inner join kenyaemr_etl.etl_drug_event d on a.patient_id=d.patient_id\n" +
                "where d.program = 'HIV' and date(a.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and d.date_started >= a.visit_date;";

        cd.setName("totalStartedOnHAARTAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started HAART At ANC");

        return cd;
    }

    //Start HAART During Labour and Delivery HV02-18
    public CohortDefinition totalStartedHAARTAtLabourAndDelivery(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=ld.patient_id\n" +
                "where d.program = 'HIV' and date(ld.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and d.date_started >= ld.visit_date;";

        cd.setName("totalStartedHAARTAtLabourAndDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started HAART At L&D");

        return cd;
    }

    //Started HAART upto 6 weeks HV02-19
    public CohortDefinition totalStartedHAARTAtPNCUpto6Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct pnc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=pnc.patient_id\n" +
                "where d.program = 'HIV' and date(pnc.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and round(DATEDIFF(pnc.visit_date,d.date_started)/7) between 0 and 6 ;";

        cd.setName("totalStartedHAARTAtPNCUpto6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started HAART At PNC within 6 weeks");

        return cd;
    }

    //Total maternal HAART HV02-20
    public CohortDefinition totalMaternalHAART(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalMaternalHAART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total maternal HAART");

        return cd;
    }
    //Start HAART_PNC >6 wks to 6 mths	HV02-21
    public CohortDefinition totalStartedOnHAARTBtw7WeeksAnd6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct pnc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=pnc.patient_id\n" +
                "where d.program = 'HIV' and date(pnc.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and round(DATEDIFF(pnc.visit_date,d.date_started)/7) between 7 and 24 ;";

        cd.setName("totalStartedOnHAARTBtw7WeeksAnd6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total started on HAART between 7 weeks and 6 months");

        return cd;
    }
    //On maternal HAART_12 mths	HV02-22
    public CohortDefinition onHAARTUpto12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct pnc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=pnc.patient_id\n" +
                "where d.program = 'HIV' and date(pnc.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and round(DATEDIFF(pnc.visit_date,d.date_started)/7) >=48;";

        cd.setName("onHAARTUpto12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total on HAART upto 12 months");

        return cd;
    }

    //MCH Net Cohort_12 months	HV02-23
    public CohortDefinition netCohortAt12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= e.patient_id\n" +
                "where d.program = 'HIV' and date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "      and round(DATEDIFF(ld.visit_date,:endDate)/7) <= 12;";

        cd.setName("netCohortAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Net Cohort at 12 months");

        return cd;
    }

    //Syphilis Screened at ANC	 HV02-24

    public CohortDefinition syphilisScreenedAt1stANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct v.patient_id\n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "                inner join kenyaemr_etl.etl_mch_enrollment e on v.patient_id = e.patient_id\n" +
                "                where anc_visit_number = 1 and (date(v.visit_date) between date(:startDate) and date(:endDate))\n" +
                "                and v.syphilis_test_status in (1229,1228,1304);";

        cd.setName("syphilisScreenedAt1stANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened at First ANC");

        return cd;
    }

    //Syphilis Screened Positive	HV02-25
    public CohortDefinition syphilisScreenedPositive(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= e.patient_id\n" +
                "where date(v.visit_date)  between date(:startDate) and date(:endDate)\n" +
                "      and v.syphilis_test_status =1228 or e.serology =1228;";

        cd.setName("syphilisScreenedPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Syphilis Screened Positive");

        return cd;
    }

    //Syphilis Treated	HV02-26
    public CohortDefinition treatedForSyphilis(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= e.patient_id\n" +
                "where date(v.visit_date)  between date(:startDate) and date(:endDate)\n" +
                "      and v.syphilis_treated_status =1065;;";

        cd.setName("treatedForSyphilis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Treated for Syphilis");

        return cd;
    }

    //HIV+ On Modern FP at 6 weeks	HV02-27
    public CohortDefinition HIVPositiveOnModernFPUpto6Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = p.patient_id\n" +
                "inner join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "(ld.final_test_result=\"Positive\" or e.hiv_status = 703 or p.final_test_result =\"Positive\") and\n" +
                "(round(DATEDIFF(ld.date_of_delivery,:endDate)/7) <=6) and\n" +
                "p.family_planning_status=965;";

        cd.setName("HIVPositiveOnModernFPUpto6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive on Modern FP at 6 weeks");

        return cd;
    }

    //HIV+ PNC Visits at 6 weeks	HV02-28
    public CohortDefinition HIVPositivePNCVisitsAt6Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "      (ld.final_test_result=\"Positive\" or e.hiv_status = 703 or v.final_test_result =\"Positive\") and\n" +
                "      (round(DATEDIFF(ld.date_of_delivery,:endDate)/7) <=6);";

        cd.setName("HIVPositivePNCVisitsAt6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive PNC Visits at 6 weeks");

        return cd;
    }

    //Known Positive Status 1st Contact	HV02-29
    public CohortDefinition knownHIVPositive1stContact(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct patient_id\n" +
                "    from kenyaemr_etl.etl_mch_enrollment e\n" +
                "    where (e.partner_hiv_status=703 and  e.visit_date between date(:startDate) and date(:endDate))";

        cd.setName("knownHIVPositive1stContact");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive at First Contact");

        return cd;
    }

    //Initial test at ANC Male	HV02-30
    public CohortDefinition initialTestAtANCForMale(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct anc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit anc\n" +
                "where date(anc.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and anc.partner_hiv_tested =1065;";
        cd.setName("initialTestAtANCForMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at ANC for Males");

        return cd;
    }

    //Initial test at Delivery Male	HV02-30
    public CohortDefinition initialTestAtDeliveryForMale(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "where date(ld.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and ld.partner_hiv_tested =1065;";
        cd.setName("initialTestAtDeliveryForMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at Delivery for Males");

        return cd;
    }

    //Initial test at PNC Male	HV02-31
    public CohortDefinition initialTestAtPNCForMale(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =   "select distinct pnc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc\n" +
                "where date(pnc.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and pnc.partner_hiv_tested =1065;";

        cd.setName("initialTestAtPNCForMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at PNC for Males");

        return cd;
    }
    //Total Known Status Male	HV02-32
  /*  public CohortDefinition totalKnownHIVStatusMale(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalKnownHIVStatusMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total males with known HIV Status");

        return cd;
    }*/

    //1st ANC KP adolescents (10-19)	HV02-33
    public CohortDefinition firstANCKPAdolescents(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "\n" +
                "select distinct e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "join  kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where e.hiv_status =703\n" +
                "and date(e.visit_date) between (:startDate) and (:endDate)\n" +
                "and  timestampdiff(year,d.dob,date(:endDate)) between 10 and 19;";

        cd.setName("firstANCKPAdolescents");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("1st ANC KP Adolescents");

        return cd;
    }

    //Positive result Adolescents_Total	HV02-34
    public CohortDefinition adolescentsHIVPositive(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "left join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id = e.patient_id\n" +
                "left join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id = e.patient_id\n" +
                "left join kenyaemr_etl.etl_mch_postnatal_visit pnc on pnc.patient_id = e.patient_id\n" +
                "where anc.final_test_result = 703\n" +
                "or ld.final_test_result = 703\n" +
                "or pnc.final_test_result = 703\n" +
                "and date(anc.visit_date) between (:startDate) and (:endDate)\n" +
                "and  timestampdiff(year,d.dob,date(:endDate)) between 10 and 19\n" +
                "and (round(DATEDIFF(ld.visit_date,:endDate)/7) <=6);\n";

        cd.setName("adolescentsHIVPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Adolescents");

        return cd;
    }

    //Started HAART adolescents_Total	HV02-35
    public CohortDefinition adolescentsStartedOnHAART(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct e.patient_id from kenyaemr_etl.etl_mch_enrollment e \n" +
                "join kenyaemr_etl.etl_drug_event de on de.patient_id = e.patient_id\n" +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                " where de.program = 'HIV' and de.date_started >= e.visit_date and\n" +
                " timestampdiff(year,d.dob,date(:endDate)) between 10 and 19\n" +
                " and date(e.visit_date) between (:startDate) and (:endDate);";

        cd.setName("adolescentsStartedOnHAART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents started on HAART");

        return cd;
    }

    //Known Exposure at Penta 1	HV02-36
 public CohortDefinition knownExposureAtPenta1(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct he.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment he\n" +
                "  inner join kenyaemr_etl.etl_hei_immunization hi on hi.patient_id=he.patient_id\n" +
                "where date(hi.visit_date) between (:startDate) and (:endDate)\n" +
                "      and he.child_exposed != 1067 AND\n" +
                "      hi.PCV_10_1 = \"Yes\" ;";

        cd.setName("knownExposureAtPenta1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Known HIV Exposure at Penta 1");

        return cd;
    }

    //Total due for Penta 1	HV02-37
    public CohortDefinition totalDueForPenta1(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct he.patient_id\n" +
                "                from kenyaemr_etl.etl_hei_enrollment he\n" +
                "                 inner join kenyaemr_etl.etl_hei_immunization hi on hi.patient_id=he.patient_id\n" +
                "                where date(hi.visit_date) between (:startDate) and (:endDate)\n" +
                "                  and hi.PCV_10_1 = \"Yes\" ;";

        cd.setName("totalDueForPenta1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Infants due for Penta 1");

        return cd;
    }

    //Infant ARV Prophylaxis ANC HV02-38
    public CohortDefinition infantArvProphylaxisANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=v.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=v.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "      and (v.baby_nvp_dispensed = 160123 or v.baby_azt_dispensed = 160123) and\n" +
                "      (p.baby_nvp_dispensed != 160123 or p.baby_azt_dispensed != 160123) and\n" +
                "      (ld.baby_nvp_dispensed != 160123 or ld.baby_azt_dispensed != 160123);";

        cd.setName("infantArvProphylaxisANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis at ANC");

        return cd;
    }

    //Infant ARV Prophylaxis L&D	HV02-39
    public CohortDefinition infantArvProphylaxisLabourAndDelivery(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=ld.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=ld.patient_id\n" +
                "where date(ld.visit_date) between date(:startDate) and date(:endDate)\n" +
                "      and (ld.baby_nvp_dispensed = 160123 or ld.baby_azt_dispensed = 160123) and\n" +
                "      (p.baby_nvp_dispensed != 160123 or p.baby_azt_dispensed != 160123) and\n" +
                "      (v.baby_nvp_dispensed != 160123 or v.baby_azt_dispensed != 160123);";

        cd.setName("infantArvProphylaxisLabourAndDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis during Labour and Delivery");

        return cd;
    }

    //Infant ARV Prophylaxis <8weeks PNC	HV02-40
    public CohortDefinition infantArvProphylaxisPNCLessThan8Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct p.patient_id\n" +
                "                from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "               where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                and (p.baby_nvp_dispensed = 160123 or p.baby_azt_dispensed = 160123) and\n" +
                "                      (v.baby_nvp_dispensed != 160123 or v.baby_azt_dispensed != 160123) and\n" +
                "                      (ld.baby_nvp_dispensed != 160123 or ld.baby_azt_dispensed != 160123)\n" +
                "                and round(DATEDIFF(ld.visit_date,DATE(:endDate))/7) <=8;";

        cd.setName("infantArvProphylaxisPNCLessThan8Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis PNC <8 weeks");

        return cd;
    }

    //Total ARV Prophylaxis Total	HV02-41
  /*  public CohortDefinition totalARVProphylaxis(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalARVProphylaxis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total ARV Prophylaxis");

        return cd;
    }*/

    //HEI CTX/DDS Start <2 months	HV02-42
    public CohortDefinition heiDDSCTXStartLessThan2Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "SELECT distinct hv.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "where date(hv.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and hv.ctx_given = 105281\n" +
                "and round(DATEDIFF(DATE(hv.visit_date),DATE(:endDate))/7) <=8;";

        cd.setName("heiDDSCTSStartLessThan2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI DDS/CTS Start <2 Months");

        return cd;
    }

    //Initial PCR <8 weeks	HV02-43
    public CohortDefinition initialPCRLessThan8Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct hv.patient_id \n" +
                "    from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "    where hv.dna_pcr_result is not null and dna_pcr_contextual_status=162080 and \n" +
                "    (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=2; ";

        cd.setName("initialPCRLessThan8Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial PCR <8 weeks");

        return cd;
    }

    //Initial PCR >8weeks - 12 months	HV02-44
    public CohortDefinition initialPCROver8WeeksTo12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct hv.patient_id \n" +
                "    from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "    where hv.dna_pcr_result is not null and dna_pcr_contextual_status=162080 and \n" +
                "    (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) between 9 and 12; ";

        cd.setName("initialPCROver8WeeksTo12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial PCR >8 weeks to 12 Months");

        return cd;
    }

    //Initial PCR Test <12 mths Total	HV02-45
    public CohortDefinition totalInitialPCRTestLessThan12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct hv.patient_id \n" +
                "    from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "    where hv.dna_pcr_result is not null and dna_pcr_contextual_status=162080 and \n" +
                "    (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<12; ";

        cd.setName("totalInitialPCRTestLessThan12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Initial PCR <12 Months");

        return cd;
    }

    //Infected 24 months	HV02-46
    public CohortDefinition totalInfected24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct hv.patient_id \n" +
                "    from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "    where dna_pcr_result= 703 and dna_pcr_contextual_status=162082 and \n" +
                "    (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=24; ";

        cd.setName("totalInfected24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total infected in 24 Months");

        return cd;
    }

    //Uninfected 24 months	HV02-47
    public CohortDefinition totalUninfectedIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct hv.patient_id \n" +
                "    from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "    where dna_pcr_result= 664 and dna_pcr_contextual_status=162082 and \n" +
                "    (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=24; ";

        cd.setName("totalUninfectedIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total uninfected in 24 Months");

        return cd;
    }

    //Unknown Outcome 24 mths HV02-48
    public CohortDefinition unknownOutcomesIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id\n" +
                "where hv.dna_pcr_result= 1138 or hv.dna_pcr_result= 1304 and hv.dna_pcr_contextual_status=162082 and\n" +
                "(hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=24;";

        cd.setName("unknownOutcomesIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Unknown outcomes in 24 Months");

        return cd;
    }

    //Net Cohort HEI 24 months	HV02-49
    public CohortDefinition netCohortHeiIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select he.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment he\n" +
                " join  kenyaemr_etl.etl_patient_demographics d on d.patient_id = he.patient_id\n" +
                "where transfer_in = 1066\n" +
                "and timestampdiff(month,d.dob,date(:endDate))>=24;";

        cd.setName("netCohortHeiIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Net cohort HEI in 24 Months");

        return cd;
    }

    //Mother-baby pairs 24 months	HV02-50
    public CohortDefinition motherBabyPairsIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct he.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment he\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id=he.patient_id\n" +
                "where he.infant_mother_link is not null and\n" +
                "he.visit_date between date(:startDate) and date(:endDate) and timestampdiff(month,d.dob,date(:endDate))<=24;";

        cd.setName("motherBabyPairsIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mother-baby pairs in 24 Months");

        return cd;
    }

    //Pair net cohort 24 months	 HV02-51
    public CohortDefinition pairNetCohortIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select he.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment he\n" +
                "join  kenyaemr_etl.etl_patient_demographics d on d.patient_id = he.patient_id\n" +
                "where  he.mother_alive=1 and transfer_in = 1066\n" +
                "and timestampdiff(month,d.dob,date(:endDate))>=24;";

        cd.setName("pairNetCohortIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pair net cohort in 24 Months");

        return cd;
    }

    //EBF (at 6 months)	HV02-52
    public CohortDefinition exclusiveBFAt6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hv.patient_id\n" +
                "where  timestampdiff(month,d.dob,date(:endDate))>=6 and hv.infant_feeding=5526 and\n" +
                "date(hv.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("exclusiveBFAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Breastfeeding at 6 months");

        return cd;
    }

    //ERF (at 6 months)	HV02-53
    public CohortDefinition exclusiveRFAt6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hv.patient_id\n" +
                "where  timestampdiff(month,d.dob,date(:endDate))>=6 and hv.infant_feeding=1595 and\n" +
                "date(hv.visit_date) between date(:startDate) and date(:endDate) ;";

        cd.setName("exclusiveRFAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Replacement feeding at 6 months");

        return cd;
    }

    //MF (at 6 months)	HV02-54
    public CohortDefinition mixedFeedingAt6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hv.patient_id\n" +
                "where  timestampdiff(month,d.dob,date(:endDate))>=6 and hv.infant_feeding=6046 and\n" +
                "date(hv.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("mixedFeedingAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mixed feeding at 6 months");

        return cd;
    }

    //BF (12 months)	HV02-55
    public CohortDefinition breastFeedingAt12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct e.patient_id \n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where  e.mother_breastfeeding=1065 and \n" +
                "timestampdiff(month,d.dob,date(:endDate))>=12 and\n" +
                "date(e.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("breastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breast feeding at 12 months");

        return cd;
    }

    //Not BF (12 months)	HV02-56
    public CohortDefinition notBreastFeedingAt12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct e.patient_id \n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where  e.mother_breastfeeding=1066 and \n" +
                "timestampdiff(month,d.dob,date(:endDate))>=12 and\n" +
                "date(e.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("notBreastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not Breast feeding at 12 months");

        return cd;
    }

    //BF (18 months)	HV02-57
    public CohortDefinition breastFeedingAt18Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct e.patient_id \n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where  e.mother_breastfeeding=1065 and \n" +
                "timestampdiff(month,d.dob,date(:endDate))>=18 and\n" +
                "date(e.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("breastFeedingAt18Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breast feeding at 18 months");

        return cd;
    }

    //Not BF (18 months)	HV02-58
    public CohortDefinition notBreastFeedingAt18Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "Select distinct e.patient_id \n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where  e.mother_breastfeeding=1066 and \n" +
                "timestampdiff(month,d.dob,date(:endDate))>=18 and\n" +
                "date(e.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("notBreastFeedingAt18Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not Breastfeeding at 18 months");

        return cd;
    }

}
