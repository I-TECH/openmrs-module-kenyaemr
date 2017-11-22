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
 * Library of cohort definitions used specifically in Datim Reports
 */
@Component
public class ETLDatimCohortLibrary {
     /**
     * Patients started on ART during the reporting period (last 3 months)
     * TX_New Datim indicator
     * @return
     */
    public  CohortDefinition startedOnART() {
        String sqlQuery="select net.patient_id \n" +
                "from ( \n" +
                "select e.patient_id,e.date_started, \n" +
                "e.gender,\n" +
                "e.dob,\n" +
                "d.visit_date as dis_date, \n" +
                "if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "max(fup.visit_date) as latest_vis_date\n" +
                "from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, \n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "max(if(discontinued,1,0))as alternative_regimen \n" +
                "from kenyaemr_etl.etl_drug_event e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "group by e.patient_id) e \n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "where  date(e.date_started) between date_sub(:endDate , interval 3 MONTH) and :endDate \n" +
                "group by e.patient_id \n" +
                "having TI_on_art=0\n" +
                ")net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART in the last 3 months");
        return cd;
    }

    /**
     * Patients started on ART during the reporting period (last 3 months) and are pregnant during that period
     * TX_New Datim indicator
     * @return
     */
    public  CohortDefinition startedOnARTAndPregnant() {
        String sqlQuery="select net.patient_id \n" +
                "       from ( \n" +
                "       select e.patient_id,e.date_started, \n" +
                "       e.gender,\n" +
                "       e.dob,\n" +
                "       d.visit_date as dis_date, \n" +
                "       if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "       e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "       fup.visit_date, fup.pregnancy_status,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "       max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "       max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "       max(fup.visit_date) as latest_vis_date\n" +
                "       from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, \n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "       max(if(discontinued,1,0))as alternative_regimen \n" +
                "       from kenyaemr_etl.etl_drug_event e \n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "       group by e.patient_id) e \n" +
                "       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "       where  date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) \n" +
                "       and fup.pregnancy_status =1065 and fup.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)\n" +
                "       group by e.patient_id \n" +
                "       having TI_on_art=0\n" +
                "       )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_pregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART in the last 3 months and are pregnant");
        return cd;
    }

    /**
     * Patients started on ART during the reporting period (last 3 months) and are on new or existing TB cases
     * TX_New Datim indicator
     * @return
     */
    public  CohortDefinition startedOnARTAndTBCoinfected() {
        String sqlQuery="select net.patient_id \n" +
                "       from ( \n" +
                "       select e.patient_id,e.date_started, \n" +
                "       e.gender,\n" +
                "       e.dob,\n" +
                "       d.visit_date as dis_date, \n" +
                "       if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "       e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "       fup.visit_date, fup.tb_status,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "       max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "       max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "       max(fup.visit_date) as latest_vis_date\n" +
                "       from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, \n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "       max(if(discontinued,1,0))as alternative_regimen \n" +
                "       from kenyaemr_etl.etl_drug_event e \n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "       group by e.patient_id) e \n" +
                "       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_tb_enrollment tbenr on tbenr.patient_id = e.patient_id\n" +
                "       where  date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) \n" +
                "       and ((fup.pregnancy_status =1662 and fup.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) or \n" +
                "       tbenr.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) )\n" +
                "       group by e.patient_id \n" +
                "       having TI_on_art=0\n" +
                "       )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_TB_co_infected");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART in the last 3 months and are pregnant");
        return cd;
    }
    /**
     * Patients currently on ART
     * TX_Curr Datim indicator
     * @return
     */
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
                "(((date(latest_tca) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca))) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
                ") e\n" +
                ";";

        cd.setName("TX_Curr");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currently on ART");
        return cd;
    }

    /**
     * TX_RET Denominator
     * Includes patients who started art 12 months ago irrespective of their active status
     * @return
     */
    protected CohortDefinition art12MonthCohort() {

        String sqlQuery = "  select net.patient_id " +
                "  from ( " +
                "  select e.patient_id,e.date_started, e.gender,e.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
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
                "    where  date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year) " +
                "    group by e.patient_id " +
                "    )net; ";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_Denominator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("art 12 Months Net Cohort Denominator");
        return cd;

    }

    /**
     * TX_RET Datim Indicator
     * Patients who were started on treatment 12 months ago and are still on treatment
     * @return
     */
    protected CohortDefinition onTherapyAt12Months() {

        String sqlQuery = "  select net.patient_id " +
                "  from (" +
                "  select e.patient_id,e.date_started, p.gender,p.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date, max(fup.next_appointment_date) as latest_tca" +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where  date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>:endDate or dis_date is null) and (datediff(latest_tca,:endDate)<=90))net; ";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_Numerator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Therapy At 12 Months");
        return cd;

    }

    /**
     * TX_PVLS
     * Patients with VL in the last 12 months
     * @return
     */
    protected CohortDefinition viralLoadResultsInLast12Months() {

        String sqlQuery =" select patient_id \n" +
                "from kenyaemr_etl.etl_laboratory_extract \n" +
                "where (visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate) \n" +
                "and (lab_test in (856, 1305));";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_Denominator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("VL in last 12 months");
        return cd;

    }

    /**
     * TX_PVLS Datim Indicator
     * Patients with viral suppression in last 12 months
     * @return
     */
    protected CohortDefinition viralSuppressionInLast12Months() {

        String sqlQuery = " select patient_id \n" +
                "from kenyaemr_etl.etl_laboratory_extract \n" +
                "where (visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate) \n" +
                "and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302));";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_Numerator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Therapy At 12 Months");
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

}
