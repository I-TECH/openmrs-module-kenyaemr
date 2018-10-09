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
        String sqlQuery="select  net.patient_id \n" +
                "from ( \n" +
                "select e.patient_id,e.date_started, \n" +
                "d.visit_date as dis_date, \n" +
                "if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "max(fup.visit_date) as latest_vis_date\n" +
                "from (select e.patient_id, min(e.date_started) as date_started, \n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "max(if(discontinued,1,0))as alternative_regimen \n" +
                "from kenyaemr_etl.etl_drug_event e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "group by e.patient_id) e \n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "where  date(e.date_started) between date(:startDate) and date(:endDate) \n" +
                "group by e.patient_id \n" +
                "having TI_on_art=0\n" +
                ")net;";
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
                "  de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
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
        String sqlQuery=" select  e.patient_id " +
                "from ( " +
                "select fup.visit_date,fup.patient_id," +
                "min(e.visit_date) as enroll_date, " +
                "max(fup.visit_date) as latest_vis_date, " +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca " +
                "from kenyaemr_etl.etl_patient_hiv_followup fup " +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id " +
                "join kenyaemr_etl.etl_hiv_enrollment e  on fup.patient_id=e.patient_id " +
                "where (fup.visit_date between date_sub(date(:startDate), interval 3 month) and date(:endDate)) " +
                "group by patient_id " +
                "having (latest_tca>date(:endDate) or \n" +
                "(latest_tca between date(:startDate) and date(:endDate) and latest_vis_date between date(:startDate) and date(:endDate)) )\n" +
                ") e " +
                "where e.patient_id not in (select patient_id from kenyaemr_etl.etl_patient_program_discontinuation \n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV' \n" +
                "group by patient_id \n" +
                "having if(e.latest_tca>max(visit_date),1,0)=0) \n" +
                "and e.patient_id in (select patient_id\n" +
                "from (select e.patient_id,min(e.date_started) as date_started,\n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "max(if(discontinued,1,0))as alternative_regimen\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "group by e.patient_id) e\n" +
                "where  date(e.date_started)<date(:startDate)) \n";

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
                "group by e.patient_id) e \n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
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
                "where date_started between date(:startDate) and date(:endDate)) started_art on  " +
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
                "where date_started between date(:startDate) and date(:endDate)) started_art on  \n" +
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

        String sqlQuery = "  select  net.patient_id " +
                "  from ( " +
                "  select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "   e.regimen, e.regimen_line, e.alternative_regimen, mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, "+
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date" +
                "    from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, " +
                "    mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, " +
                "    mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, " +
                "    max(if(discontinued,1,0))as alternative_regimen " +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>date(:endDate) or dis_date is null) )net; ";
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
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
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
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
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
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
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

        String sqlQuery = "  select  net.patient_id " +
                "  from (" +
                "  select e.patient_id,e.date_started,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date, max(fup.next_appointment_date) as latest_tca" +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>date(:endDate) or dis_date is null) and (datediff(latest_tca,date(:endDate))<=90))net; ";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onTherapyAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Therapy At 12 Months");
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
                "  SELECT\n" +
                "    fup.visit_date,\n" +
                "    fup.patient_id,\n" +
                "    p.dob,\n" +
                "    p.Gender,\n" +
                "    min(e.visit_date)  AS enroll_date,\n" +
                "    max(fup.visit_date) AS latest_vis_date,\n" +
                "    mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11) AS latest_tca,\n" +
                "    max(d.visit_date)     AS date_discontinued,\n" +
                "    d.patient_id  AS disc_patient,\n" +
                "    de.patient_id  AS started_on_drugs\n" +
                "  FROM kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "    JOIN kenyaemr_etl.etl_patient_demographics p ON p.patient_id = fup.patient_id\n" +
                "    JOIN kenyaemr_etl.etl_hiv_enrollment e ON fup.patient_id = e.patient_id\n" +
                "    LEFT OUTER JOIN kenyaemr_etl.etl_drug_event de ON e.patient_id = de.patient_id AND date(date_started) <= date(:endDate)\n" +
                "    LEFT OUTER JOIN\n" +
                "    (SELECT\n" +
                "       patient_id,\n" +
                "       visit_date\n" +
                "     FROM kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "     WHERE date(visit_date) <= date(:endDate) AND program_name = 'HIV'\n" +
                "     GROUP BY patient_id\n" +
                "    ) d ON d.patient_id = fup.patient_id\n" +
                "  WHERE date(fup.visit_date) <= date(:endDate)\n" +
                "  GROUP BY patient_id\n" +
                "  HAVING (\n" +
                "    (date(latest_tca) > date(:endDate) AND (date(latest_tca) > date(date_discontinued) OR disc_patient IS NULL)) OR\n" +
                "    (((date(latest_tca) BETWEEN date(:startDate) AND date(:endDate)) OR (date(latest_vis_date) BETWEEN date(:startDate) AND date(:endDate))) AND\n" +
                "     (latest_tca > date_discontinued OR disc_patient IS NULL)))\n" +
                ") active\n" +
                "where started_on_drugs = '' or started_on_drugs is null\n" +
                " ;";
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
        String sqlQuery = "select  e.patient_id \n" +
                "from kenyaemr_etl.etl_tb_enrollment e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.date_started < e.visit_date\n" +
                "where  date(e.visit_date) between :startDate and :endDate " +
                ";";
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
        String sqlQuery = "select  e.patient_id \n" +
                "from kenyaemr_etl.etl_tb_enrollment e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.date_started between :startDate and :endDate\n" +
                "where  date(e.visit_date) between :startDate and :endDate " +
                ";";
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
        String sqlQuery = "select  e.patient_id \n" +
                "from kenyaemr_etl.etl_tb_enrollment e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.date_started <= :endDate\n" +
                "where  date(e.visit_date) between :startDate and :endDate " +
                ";";
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
                "   ld.final_test_result=\"Positive\" or hiv_status = 703 or v.final_test_result =\"Positive\" ;";

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
                "                    from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                     where date(visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "                (e.patient_id is not null) and hiv_status=703;";

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
                "                   where date(v.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "                         e.hiv_status =1402 and\n" +
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
                "where date(ld.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      e.hiv_status =1402 and\n" +
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
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "  left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      round(DATEDIFF(ld.visit_date,:endDate)/7) <=6 and\n" +
                "      e.hiv_status =1402 and\n" +
                "      v.final_test_result is null and\n" +
                "      ld.final_test_result is null and\n" +
                "      p.final_test_result is not null ;";

        cd.setName("Initial Test at PNC <=6 Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at PNC <=6 Weeks");

        return cd;
    }
    //Known HIV Status Total HV02-07
    public CohortDefinition testedForHivInMchmsTotal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (e.hiv_test_date between date(:startDate) and date(:endDate)) ;";
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
        String sqlQuery ="select distinct pnc.patient_id " +
                " from kenyaemr_etl.etl_mch_postnatal_visit pnc \n" +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=pnc.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=pnc.patient_id\n" +
                "where date(hiv_test_date) between date(:startDate) and date(:endDate) and \n" +
                "pnc.patient_id is not null and pnc.final_test_result is not null and anc.final_test_result is not null and ld.final_test_result is not null\n"+
                "and round(endDate,DATEDIFF(ld.visit_date)/7) <=6";

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
        String sqlQuery ="select distinct pnc.patient_id " +
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc " +
                "left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=pnc.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=pnc.patient_id\n" +
                "where date(pnc.visit_date) between date(:startDate) and date(:endDate) and \n" +
                "(pnc.patient_id is not null and anc.patient_id is null and ld.patient_id is null) and (round(DATEDIFF(endDate,ld.visit_date)/7) >6) AND (round(DATEDIFF(endDate,ld.visit_date)/7)<=24) ";

        cd.setName("pncTest6WeeksUpto6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PNC Test between 6 weeks and 6 Months");

        return cd;
    }
    //Known Positive before 1st ANC HV02-10
   /* public CohortDefinition knownPositiveAtFirstANC(){
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
    //Known Positive at ANC HV02-11
    public CohortDefinition testedHivPositiveInMchmsAntenatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =" select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      (e.hiv_status !=703) and\n" +
                "      v.final_test_result =\"Positive\";";

        cd.setName("Tested Hiv Postive at Antenatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Hiv Postive at Antenatal");

        return cd;
    }

//HIV Positive during Labour and Delivery HV02-12

    public CohortDefinition positiveHIVResultsAtLabourAndDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "  left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=ld.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=ld.patient_id\n" +
                "where date(ld.visit_date) between date(:startDate) and date(:endDate)) and\n" +
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
                "where date(visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      round(DATEDIFF(p.visit_date,endDate)/7) <=6 and\n" +
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
        String sqlQuery =  " select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
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
        String sqlQuery =  " select distinct p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "  left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      ((round(DATEDIFF(p.visit_date,endDate)/7) >=6) AND (round(DATEDIFF(ld.visit_date,endDate)/7) <=24)) and\n" +
                "      (e.hiv_status !=703) and\n" +
                "      (v.final_test_result is null or v.final_test_result !=\"Positive\") and\n" +
                "      (ld.final_test_result is null or ld.final_test_result !=\"Positive\") and\n" +
                "      p.final_test_result =\"Positive\";";

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
        String sqlQuery =  "select distinct anc.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit a \n" +
                "inner join kenyaemr_etl.etl_drug_event d on anc.patient_id=d.patient_id \n"+
                "where date(anc.visit_date) BETWEEN date(:startDate) and date(:endDate) and \n" +
                "anc_visit_number = 1 and d.date_started < anc.visit_date";

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
                "                from kenyaemr_etl.etl_mch_antenatal_visit a \n" +
                "                inner join kenyaemr_etl.etl_drug_event d on a.patient_id=d.patient_id\n" +
                "                where date(a.visit_date) between date(:startDate) and date(:endDate)  \n" +
                "                and d.date_started >= a.visit_date;";

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
        String sqlQuery =  "select distinct d.patient_id \n"+
                "       from kenyaemr_etl.etl_mch_delivery d \n"+
                "                where date(d.visit_date) between date(:startDate) and date(:endDate)  \n" +
                "                and d.haart_given_at_delivery is not null;";


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
        String sqlQuery =  "select distinct pnc.patient_id \n"+
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc \n"+
                "where date(pnc.visit_date) between date(:startDate) and date(:endDate)\n"+
                "and round(DATEDIFF(endDate,pnc.haart_start_date)/7) <= 6";

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
        String sqlQuery =  "select distinct pnc.patient_id \n"+
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc \n"+
                "where date(pnc.visit_date) between date(:startDate) and date(:endDate)\n"+
                "and (round(DATEDIFF(endDate,pnc.haart_start_date)/7)>6) and (round(DATEDIFF(endDate,pnc.haart_start_date)/7)<=24)";

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
        String sqlQuery =  "select distinct pnc.patient_id \n"+
                "from kenyaemr_etl.etl_mch_postnatal_visit pnc \n"+
                "where date(pnc.visit_date) between date(:startDate) and date(:endDate)\n"+
                "and round(DATEDIFF(endDate,pnc.haart_start_date)/7) >=48";

        cd.setName("onHAARTUpto12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total on HAART upto 12 months");

        return cd;
    }

    //Net Cohort_12 months	HV02-23
    public CohortDefinition netCohortAt12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("netCohortAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Net Cohort at 12 months");

        return cd;
    }

    //Syphilis Screened 1st ANC	 HV02-24

    public CohortDefinition syphilisScreenedAt1stANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on v.patient_id = e.patient_id\n" +
                "where anc_visit_number = 1 and date(v.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "v.syphilis_test_status is not null or v.syphilis_test_status !=1402;";

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
        String sqlQuery =  "select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on v.patient_id = e.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "        v.syphilis_test_status =1228;";

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
        String sqlQuery =  "select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment e on v.patient_id = e.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      v.syphilis_treated_status =1065;";

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
                "  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=p.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= p.patient_id\n" +
                "where date(visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      (ld.final_test_result=\"Positive\" or e.hiv_status = 703 or v.final_test_result =\"Positive\") and\n" +
                "      (round(DATEDIFF(p.visit_date,endDate)/7) <=6) and\n" +
                "      p.family_planning_status=965;";

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
                "where date(p.visit_date) between date(:startDate) and date(:endDate)) and\n" +
                "      (ld.final_test_result=\"Positive\" or e.hiv_status = 703 or v.final_test_result =\"Positive\") and\n" +
                "      (round(DATEDIFF(ld.visit_date,\"2018-10-10\")/7) <=6);";

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
        String sqlQuery =  "select distinct patient_id \n" +
                "    from kenyaemr_etl.etl_mch_enrollment e \n" +
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
        String sqlQuery =  "select distinct anc.patient_id \n " +
                " from kenyaemr_etl.etl_mch_antenatal_visit anc \n" +
                "left join kenyaemr_etl.etl_mch_enrollment e on anc.patient_id=e.patient_id\n" +
                "left join kenyaemr_etl.etl_mchs_delivery ld on anc.patient_id=ld.patient_id\n" +
                "left join kenyaemr_etl.etl_mch_postnatal_visit pnc on anc.patient_id=pnc.patient_id\n" +
                "where date(anc.visit_date) between date(:startDate) and date(:endDate) and \n" +
                "and anc.partner_hiv_tested is not null and \n"+
                "(anc.patient_id is not null and ld.patient_id is null and pnc.patient_id is null);";



        cd.setName("initialTestAtANCForMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at ANC for Males");

        return cd;
    }

    //Initial test at PNC Male	HV02-31
    public CohortDefinition initialTestAtPNCForMale(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =   "select distinct pnc.patient_id \n " +
                " from kenyaemr_etl.etl_mch_postnatal_visit pnc \n" +
                " left join kenyaemr_etl.etl_mch_enrollment e on pnc.patient_id=e.patient_id\n" +
                "left join kenyaemr_etl.etl_mchs_delivery ld on pnc.patient_id=ld.patient_id\n" +
                "left join kenyaemr_etl.etl_mch_antenatal_visit anc on pnc.patient_id=anc.patient_id\n" +
                "where date(pnc.visit_date) between date(:startDate) and date(:endDate) and \n" +
                "and anc.partner_hiv_tested is not null and \n"+
                "(pnc.patient_id is not null and ld.patient_id is null and anc.patient_id is null);";

        cd.setName("initialTestAtPNCForMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at PNC for Males");

        return cd;
    }
    //Total Known Status Male	HV02-32
    public CohortDefinition totalKnownHIVStatusMale(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalKnownHIVStatusMale");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total males with known HIV Status");

        return cd;
    }


    public CohortDefinition firstANCKPAdolescents(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("firstANCKPAdolescents");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("1st ANC KP Adolescents");

        return cd;
    }

    public CohortDefinition adolescentsHIVPositive(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("adolescentsHIVPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Adolescents");

        return cd;
    }

    public CohortDefinition adolescentsStartedOnHAART(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("adolescentsStartedOnHAART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Adolescents started on HAART");

        return cd;
    }


    public CohortDefinition knownExposureAtPenta1(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("knownExposureAtPenta1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Known HIV Exposure at Penta 1");

        return cd;
    }

    public CohortDefinition totalDueForPenta1(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalDueForPenta1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Infants due for Penta 1");

        return cd;
    }

    public CohortDefinition infantArvProphylaxisANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("infantArvProphylaxisANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis at ANC");

        return cd;
    }

    public CohortDefinition infantArvProphylaxisLabourAndDelivery(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("infantArvProphylaxisLabourAndDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis during Labour and Delivery");

        return cd;
    }

    public CohortDefinition infantArvProphylaxisPNCLessThan8Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("infantArvProphylaxisPNCLessThan8Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis PNC <8 weeks");

        return cd;
    }

    public CohortDefinition totalARVProphylaxis(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalARVProphylaxis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total ARV Prophylaxis");

        return cd;
    }

    public CohortDefinition heiDDSCTXStartLessThan2Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("heiDDSCTSStartLessThan2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI DDS/CTS Start <2 Months");

        return cd;
    }

    public CohortDefinition initialPCRLessThan8Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("initialPCRLessThan8Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial PCR <8 weeks");

        return cd;
    }

    public CohortDefinition initialPCROver8WeeksTo12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("initialPCROver8WeeksTo12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial PCR >8 weeks to 12 Months");

        return cd;
    }


    public CohortDefinition totalInitialPCRTestLessThan12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalInitialPCRTestLessThan12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Initial PCR <12 Months");

        return cd;
    }

    public CohortDefinition totalInfected24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalInfected24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total infected in 24 Months");

        return cd;
    }
    public CohortDefinition totalUninfectedIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("totalUninfectedIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total uninfected in 24 Months");

        return cd;
    }

    public CohortDefinition unknownOutcomesIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("unknownOutcomesIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Unknown outcomes in 24 Months");

        return cd;
    }

    public CohortDefinition netCohortHeiIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("netCohortHeiIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Net cohort HEI in 24 Months");

        return cd;
    }

    public CohortDefinition motherBabyPairsIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("motherBabyPairsIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mother-baby pairs in 24 Months");

        return cd;
    }

    public CohortDefinition pairNetCohortIn24Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("pairNetCohortIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pair net cohort in 24 Months");

        return cd;
    }

    public CohortDefinition exclusiveBFAt6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("exclusiveBFAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Breastfeeding at 6 months");

        return cd;
    }

    public CohortDefinition exclusiveRFAt6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("exclusiveRFAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Replacement feeding at 6 months");

        return cd;
    }
    public CohortDefinition mixedFeedingAt6Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("mixedFeedingAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mixed feeding at 6 months");

        return cd;
    }
    public CohortDefinition breastFeedingAt12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("breastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breast feeding at 12 months");

        return cd;
    }

    public CohortDefinition notBreastFeedingAt12Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("notBreastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not Breast feeding at 12 months");

        return cd;
    }
    public CohortDefinition breastFeedingAt18Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("breastFeedingAt18Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breast feeding at 18 months");

        return cd;
    }

    public CohortDefinition notBreastFeedingAt18Months(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  ";";

        cd.setName("notBreastFeedingAt18Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not Breastfeeding at 18 months");

        return cd;
    }



//    public CohortDefinition testedForHivInMchmsTotal(){
//        SqlCohortDefinition cd = new SqlCohortDefinition();
//        String sqlQuery =  " select distinct patient_id " +
//                "    from kenyaemr_etl.etl_mch_enrollment e " +
//                "    where (e.hiv_test_date between date(:startDate) and date(:endDate)) ;";
//
//        cd.setName("testedForHivInMchms");
//        cd.setQuery(sqlQuery);
//        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
//        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
//        cd.setDescription("Mothers tested For Hiv In Mch Program");
//
//        return cd;
//    }

    public CohortDefinition testedHivPositiveInMchmsTotal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (hiv_status=703 and e.hiv_test_date between date(:startDate) and date(:endDate)) ;";

        cd.setName("testedHivPositeInMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers tested Hiv Positive In Mch Program");

        return cd;
    }



    public CohortDefinition exclusiveBreastFeedingAtSixMonths(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id  " +
                " from kenyaemr_etl.etl_hei_follow_up_visit e   " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  timestampdiff(month,d.dob,date(:endDate))>=6 and e.infant_feeding=5526 and \n" +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ;

        cd.setName("exclusiveBreastFeedingAtSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive BreastFeeding At Six Months");

        return cd;
    }

    public CohortDefinition exclusiveReplacementFeedingAtSixMonths(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id  " +
                " from kenyaemr_etl.etl_hei_follow_up_visit e   " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  timestampdiff(month,d.dob,date(:endDate))>=6 and e.infant_feeding=1595 and \n" +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ;
        cd.setName("exclusiveReplacementFeedingAtSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Replacement Feeding At Six Months");

        return cd;
    }

    public CohortDefinition mixedFeedingAtSixMonths(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id  " +
                " from kenyaemr_etl.etl_hei_follow_up_visit e   " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  timestampdiff(month,d.dob,date(:endDate))>=6 and e.infant_feeding=6046 and \n" +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ;
        cd.setName("mixedFeedingAtSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mixed Feeding At Six Months");

        return cd;
    }

    public CohortDefinition totalExposedAgedSixMonths(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id  " +
                " from kenyaemr_etl.etl_hei_follow_up_visit e   " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where timestampdiff(month,d.dob,date(:endDate))>=6 and e.infant_feeding in (1595,6046,5526)  and \n " +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ;
        cd.setName("totalExposedAgedSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("total Exposed Aged Six Months");

        return cd;
    }

    public CohortDefinition motherOnTreatmentAndBreastFeeding(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id " +
                " from kenyaemr_etl.etl_hei_enrollment e  " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  mother_breastfeeding=1065 and \n " +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ;

        cd.setName("motherOnTreatmentAndBreastFeeding");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("mother On Treatment And BreastFeeding");

        return cd;
    }

    public CohortDefinition motherOnTreatmentAndNotBreastFeeding(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id " +
                " from kenyaemr_etl.etl_hei_enrollment e  " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  mother_breastfeeding=1066 and \n " +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ;

        cd.setName("motherOnTreatmentAndNotBreastFeeding");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("mother On Treatment And Not BreastFeeding");

        return cd;
    }

    public CohortDefinition motherOnTreatmentAndNotBreastFeedingUnknown(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id " +
                " from kenyaemr_etl.etl_hei_enrollment e  " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  mother_breastfeeding=1067 and \n " +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ; //this concept unknown 1067 is missing on the hei enrollment form and should be corrected

        cd.setName("motherOnTreatmentAndNotBreastFeedingUnknown");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("mother On Treatment And Not BreastFeeding Unknown");

        return cd;
    }

    public CohortDefinition totalBreastFeedingMotherOnTreatment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  Select distinct e.patient_id " +
                " from kenyaemr_etl.etl_hei_enrollment e  " +
                " join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id " +
                " where  mother_breastfeeding is not null and \n " +
                "  date(e.visit_date) between date(:startDate) and date(:endDate) ;" ; //this concept unknown 1067 is missing on the hei enrollment form and should be corrected

        cd.setName("totalBreastFeedingMotherOnTreatment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("total BreastFeeding Mother On Treatment");

        return cd;
    }

    public CohortDefinition testedForHivInMchmsAntenatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct anc.patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between date(:startDate) and date(:endDate) and \n" +
                "(anc.patient_id is not null);";

        cd.setName("Tested For Hiv Antenatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested For Hiv Antenatal");

        return cd;
    }

//
//public CohortDefinition testedForHivInMchmsDelivery(){
//        SqlCohortDefinition cd = new SqlCohortDefinition();
//        String sqlQuery ="  select distinct ld.patient_id " +
//                "    from kenyaemr_etl.etl_mch_enrollment e " +
//                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
//                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
//                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
//                "where date(hiv_test_date) between date(:startDate) and date(:endDate) and \n" +
//                "(ld.patient_id is not null and anc.patient_id is null);";
//
//        cd.setName("Tested For Hiv Delivery");
//        cd.setQuery(sqlQuery);
//        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
//        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
//        cd.setDescription("Tested For Hiv Delivery");
//
//        return cd;
//    }


    public CohortDefinition testedForHivInMchmsPostnatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct panc.patient_id " +
                " from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between date(:startDate) and date(:endDate) and \n" +
                "(panc.patient_id is not null and anc.patient_id is null and ld.patient_id is null);";

        cd.setName("Tested For Hiv Postnatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested For Hiv Postnatal");

        return cd;
    }

    public CohortDefinition testedHivPositiveBeforeMchms(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id " +
                "    where (e.visit_date between date(:startDate) and date(:endDate)) " +
                "    and hiv_status=703 and e.visit_date>hiv_test_date;";

        cd.setName("testedHivPositiveBeforeMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("tested Hiv Positive Before enrollment in Mchms");

        return cd;
    }


// public CohortDefinition testedHivPositiveInMchmsAntenatal(){
//        SqlCohortDefinition cd = new SqlCohortDefinition();
//        String sqlQuery ="  select distinct anc.patient_id " +
//                "    from kenyaemr_etl.etl_mch_enrollment e " +
//                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
//                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
//                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
//                "where date(hiv_test_date) between date(:startDate) and date(:endDate) and \n" +
//                "(anc.patient_id is not null) and hiv_status=703;";
//
//        cd.setName("Tested Hiv Postive at Antenatal");
//        cd.setQuery(sqlQuery);
//        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
//        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
//        cd.setDescription("Tested Hiv Postive at Antenatal");
//
//        return cd;
//    }



    public CohortDefinition testedHivPositiveInMchmsPostnatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct panc.patient_id " +
                " from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between date(:startDate) and date(:endDate) and \n" +
                "(panc.patient_id is not null and anc.patient_id is null and ld.patient_id is null) and " +
                " hiv_status=703;";

        cd.setName("Tested Hiv Postive at Postnatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Hiv Positive Postnatal");

        return cd;
    }

    public CohortDefinition serologyAntBodyTestBetween9And12Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where (first_antibody_result is not null or final_antibody_result is not null) and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) between 9 and 12; ";

        cd.setName("serologyAntBodyTestBetween9And12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("serology Anti Body Test Between 9 And 12 Months");

        return cd;
    }

    public CohortDefinition totalHeitestedBy12Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result is not null and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=12; ";

        cd.setName("totalHeitestedBy12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("total Hei tested By 12 Months");

        return cd;
    }


    public CohortDefinition pcrWithInitialIn2Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result is not null and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=2; ";

        cd.setName("pcrWithInitialIn2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv infant testing - pcr Initial In 2 Months");

        return cd;
    }


    public CohortDefinition pcrWithInitialBetween3And8MonthsOfAge(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select  distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result is not null and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) between 3 and 8; ";

        cd.setName("pcrWithInitialBetween3And8MonthsOfAge");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv infant testing - pcr Initial between 3 and 8 Months");

        return cd;
    }

    public CohortDefinition pcrTestBetween9And12MonthsAge(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result is not null and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) between 9 and 12; ";

        cd.setName("pcrTestBetween9And12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv infant testing - pcr Initial between 9 and 12 Months");

        return cd;
    }
    //confirmed pcr
    public CohortDefinition pcrConfirmedPositive2Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result=1301 and dna_pcr_contextual_status=162082 and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=2; ";

        cd.setName("pcrConfirmedPositiveIn2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv infant testing - pcr Confirmed Positive In 2 Months");

        return cd;
    }

    public CohortDefinition pcrConfirmedPositiveBetween3To8Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select  distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result=1301 and dna_pcr_contextual_status=162082 and" +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) between 3 and 8; ";

        cd.setName("pcrConfirmedPositiveBetween3To8Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv infant testing - pcr confirmed Positive between 3 and 8 Months");

        return cd;
    }

    public CohortDefinition pcrConfirmedPositiveBetween9To12Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result = 1301 and dna_pcr_contextual_status=162082 and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) between 9 and 12; ";

        cd.setName("pcrConfirmedPositiveBetween9And12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv infant testing - pcr Confirmed Positive between 9 and 12 Months");

        return cd;
    }

    public CohortDefinition totalHeiConfirmedPositiveBy12Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select distinct e.patient_id " +
                "    from kenyaemr_etl.etl_hei_follow_up_visit e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    where dna_pcr_result= 1301 and dna_pcr_contextual_status=162082 and " +
                "    (e.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate))<=12; ";

        cd.setName("totalConfirmedPositiveBy12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("total Hei Confirmed Positive By 12 Months");

        return cd;
    }

    public CohortDefinition assessedForArtEligibilityTotal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = " select distinct m.patient_id " +
                "    from kenyaemr_etl.etl_mch_antenatal_visit m " +
                "   join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = m.patient_id \n"+
                "    where date(e.visit_date) between date(:startDate) and date(:endDate) " +
                "    and who_stage is not null or cd4 is not null;";

        cd.setName("mchEligibilityAssessment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("mch Eligibility Assessment");

        return cd;
    }

    public CohortDefinition assessedForArtEligibilityWho(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "  Select distinct m.patient_id " +
                "    from kenyaemr_etl.etl_mch_antenatal_visit m " +
                "   join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = m.patient_id \n"+
                "    where date(e.visit_date) between date(:startDate) and date(:endDate) " +
                "    and who_stage is not null ;";

        cd.setName("mchEligibilityAssessmentWho");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("mch Eligibility Assessment - WHO Stage");

        return cd;
    }

    public CohortDefinition assessedForArtEligibilityCd4(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "  Select distinct m.patient_id " +
                "    from kenyaemr_etl.etl_mch_antenatal_visit m " +
                "   join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = m.patient_id \n"+
                "    where date(e.visit_date) between date(:startDate) and date(:endDate) " +
                "    and cd4 is not null ;";

        cd.setName("mchEligibilityAssessmentCd4");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("mch Eligibility Assessment through Cd4");

        return cd;
    }

    public CohortDefinition discordantCouples(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = " select patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (e.visit_date between date(:startDate) and date(:endDate)) and ((hiv_status=703 and partner_hiv_status=664) or (hiv_status=664 and partner_hiv_status=703));";

        cd.setName("discordantCouples");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Discordant Couples");

        return cd;
    }

    public CohortDefinition partnerTestedDuringAncOrDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "   select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (partner_hiv_status is not null and e.visit_date between date(:startDate) and date(:endDate));";

        cd.setName("partnerTestedDuringAncOrDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("partner Tested During Anc Or Delivery");

        return cd;
    }

    public CohortDefinition mchKnownPositiveTotal(){
        //testedForHivBeforeOrDuringMchms
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (e.visit_date between date(:startDate) and date(:endDate)) " +
                "    and hiv_status=703 and visit_date>hiv_test_date;";

        cd.setName("hivTestingKnownPositiveTotal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Testing Known Positive Total");

        return cd;
    }
}
