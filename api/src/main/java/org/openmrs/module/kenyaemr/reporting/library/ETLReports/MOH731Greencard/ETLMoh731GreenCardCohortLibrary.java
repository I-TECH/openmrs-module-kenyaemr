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
                "and date(e.visit_date) between date(:startDate) and date(:endDate) " +
                ";";
        cd.setName("newHhivEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New HIV Enrollment");

        return cd;
    }

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
                "(((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca))) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
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
                "(((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca))) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
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
                "(((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca))) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (screened_using_icf is not null or screened_using_consultation in(1660, 142177, 160737 ))) )\n" +
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
                "(((latest_tca between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca) )) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (ctx_dispensed = 1 or dapsone_dispensed=1 or prophylaxis_given = 1 )) )\n" +
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
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test t where test_type =1\n" +
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
                "    LEFT OUTER JOIN kenyaemr_etl.etl_drug_event de ON e.patient_id = de.patient_id AND date(date_started) <= :endDate\n" +
                "    LEFT OUTER JOIN\n" +
                "    (SELECT\n" +
                "       patient_id,\n" +
                "       visit_date\n" +
                "     FROM kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "     WHERE date(visit_date) <= :endDate AND program_name = 'HIV'\n" +
                "     GROUP BY patient_id\n" +
                "    ) d ON d.patient_id = fup.patient_id\n" +
                "  WHERE fup.visit_date <= :endDate\n" +
                "  GROUP BY patient_id\n" +
                "  HAVING (\n" +
                "    (latest_tca > :endDate AND (latest_tca > date_discontinued OR disc_patient IS NULL)) OR\n" +
                "    (((latest_tca BETWEEN :startDate AND :endDate) OR (latest_vis_date BETWEEN :startDate AND :endDate)) AND\n" +
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

}
