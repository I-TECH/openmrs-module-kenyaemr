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

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DatimCohortLibrary datimCohortLibrary;


    public CohortDefinition hivEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  e.patient_id " +
                "from kenyaemr_etl.etl_hiv_enrollment e " +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "where  (e.entry_point <> 160563 or e.entry_point is null) and transfer_in_date is null " +
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
                "having ((date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ) and (date(latest_vis_date) > date(date_discontinued) or disc_patient is null)) or \n" +
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
                "  from (\n" +
                "  select e.patient_id,e.date_started,\n" +
                "  d.visit_date as dis_date,\n" +
                "  if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "  e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "  max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "  max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "  max(enr.patient_type) as latest_patient_type,\n" +
                "  max(fup.visit_date) as latest_vis_date\n" +
                "  from (select e.patient_id, min(e.date_started) as date_started,\n" +
                "  mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "  mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "  max(if(discontinued,1,0))as alternative_regimen\n" +
                "  from kenyaemr_etl.etl_drug_event e\n" +
                "   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "  where e.program = 'HIV'\n" +
                "  group by e.patient_id) e\n" +
                "  left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "  where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                "  group by e.patient_id\n" +
                "  having TI_on_art=0 and latest_patient_type in (164144,160563,159833)\n" +
                "       )net;";
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
        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                     "  select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                     "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                     "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                     "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                     "d.patient_id as disc_patient,\n" +
                     "d.effective_disc_date as effective_disc_date,\n" +
                     "max(d.visit_date) as date_discontinued,\n" +
                     "de.patient_id as started_on_drugs\n" +
                     "  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                     "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                     "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                     "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                     "left outer JOIN\n" +
                     "  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                     "where date(visit_date) <= date(:endDate) and program_name='HIV' and patient_id\n" +
                     "group by patient_id\n" +
                     "  ) d on d.patient_id = fup.patient_id\n" +
                     "  where fup.visit_date <= date(:endDate)\n" +
                     "  group by patient_id\n" +
                     "  having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                     "and\n" +
                     "  (\n" +
                     "  ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and\n" +
                     " ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                     "  and\n" +
                     "  (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                     ")\n" +
                     "  )t";

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

    /**
     * Patients screened for TB within the reporting period
     * @return
     */
    public CohortDefinition screenedForTbWithinPeriod() {
// look all active in care who were screened for tb
        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.visit_date between date(:startDate) and date(:endDate) and tb.resulting_tb_status in (1660,1662,142177) group by tb.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbScreening");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened for TB");
        return cd;

    }

    /**
     * Patients currently on ART and screened for TB within the reporting period
     * @return
     */
    public CohortDefinition tbScreening() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("screenedForTbWithinPeriod", ReportUtils.map(screenedForTbWithinPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND screenedForTbWithinPeriod");
        return cd;
    }

    /**
     * Patients screened for TB within the reporting period and presumed to having TB
     * @return
     */
    public CohortDefinition presumedTBResult() {
        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.visit_date between date(:startDate) and date(:endDate) and tb.resulting_tb_status =142177 group by tb.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("presumedTB");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Presumed for TB");
        return cd;

    }

    /**
     * Patients current in care and presumed to having TB
     * @return
     */
    public CohortDefinition presumedTb() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("presumedTBResult", ReportUtils.map(presumedTBResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND presumedTBResult");
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
                "from (\n" +
                "select  net.patient_id as patient_id\n" +
                "from (\n" +
                "select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                " from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                " from kenyaemr_etl.etl_drug_event e\n" +
                " join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                " where e.program='HIV'\n" +
                " group by e.patient_id) e\n" +
                " left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee'\n" +
                " left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                " left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                " where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                " group by e.patient_id\n" +
                "having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) and (\n" +
                "    (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(dis_date) or dis_date is null ))  or\n" +
                "    (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_tca) >= date(latest_vis_date)) ) ) or\n" +
                "    (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and\n" +
                "    (date(latest_tca) > date(dis_date) or dis_date is null )\n" +
                "    )\n" +
                " )net ) e\n" +
                "inner join\n" +
                "(\n" +
                "select\n" +
                "       b.patient_id,\n" +
                "       max(b.visit_date) as vl_date,\n" +
                "       mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "       if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "       mid(max(concat(b.visit_date,b.urgency)),11) as urgency\n" +
                "from (select x.patient_id as patient_id,x.visit_date as visit_date,x.lab_test as lab_test, x.test_result as test_result,urgency as urgency\n" +
                "      from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856)\n" +
                "      group by x.patient_id,x.visit_date order by visit_date desc)b\n" +
                "group by patient_id\n" +
                "having max(visit_date) between\n" +
                "           date_sub(date(:startDate) , interval 12 MONTH) and date(:endDate)\n" +
                ") vl_result on vl_result.patient_id = e.patient_id\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.vl_date, vl_result.vl_result)), 11)='LDL' or mid(max(concat(vl_result.vl_date, vl_result.vl_result)), 11)<1000;";
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
                "                from (\n" +
                "                select  net.patient_id as patient_id\n" +
                "                  from ( \n" +
                "                  select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "                  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "                   from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "                   from kenyaemr_etl.etl_drug_event e \n" +
                "                   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "                   where e.program='HIV' \n" +
                "                   group by e.patient_id) e \n" +
                "                   left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' \n" +
                "                   left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "                   left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "                   where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) \n" +
                "                   group by e.patient_id\n" +
                "                  having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) and (\n" +
                "                      (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(dis_date) or dis_date is null ))  or\n" +
                "                      (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_tca) >= date(latest_vis_date)) ) ) or\n" +
                "                      (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and\n" +
                "                      (date(latest_tca) > date(dis_date) or dis_date is null )\n" +
                "                      )\n" +
                "                   )net ) e\n" +
                "                 inner join\n" +
                "                 (\n" +
                "                  select\n" +
                "                    patient_id,\n" +
                "                    visit_date,\n" +
                "                    if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, 'LDL','')) as vl_result,\n" +
                "                    urgency\n" +
                "                  from kenyaemr_etl.etl_laboratory_extract\n" +
                "                  where lab_test in (1305, 856)  and visit_date between  date_sub(date(:startDate) , interval 12 MONTH) and date(:endDate)\n" +
                "                  ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "                group by e.patient_id\n" +
                "                ;";
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
        String sqlQuery="select e.patient_id\n" +
                "from (\n" +
                "         select fup.visit_date,\n" +
                "                fup.patient_id,\n" +
                "                max(e.visit_date)                                                      as enroll_date,\n" +
                "                greatest(max(fup.visit_date), ifnull(max(d.visit_date), '0000-00-00')) as latest_vis_date,\n" +
                "                greatest(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11),\n" +
                "                         ifnull(max(d.visit_date), '0000-00-00'))                      as latest_tca,\n" +
                "                max(d.visit_date)                                                      as date_discontinued,\n" +
                "                d.effective_disc_date                                                  as effective_disc_date,\n" +
                "                d.patient_id                                                           as disc_patient,\n" +
                "                max(if(dr.is_ctx = 1, 1, if(dr.is_dapsone = 1, 1, 0)))                 as prophylaxis_given,\n" +
                "                max(if(fup.ctx_dispensed in (105281, 74250, 1065), 1, 0))              as ctx_dispensed,\n" +
                "                max(if(fup.dapsone_dispensed in (105281, 74250, 1065), 1, 0))          as dapsone_dispensed\n" +
                "         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "                  left join kenyaemr_etl.etl_pharmacy_extract dr\n" +
                "                            on date(fup.visit_date) = date(dr.visit_date) and dr.patient_id = fup.patient_id\n" +
                "                  left outer JOIN\n" +
                "              (select patient_id,\n" +
                "                      coalesce(date(effective_discontinuation_date), visit_date) visit_date,\n" +
                "                      max(date(effective_discontinuation_date)) as               effective_disc_date\n" +
                "               from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= date(:endDate)\n" +
                "                 and program_name = 'HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "         where fup.visit_date <= date(:endDate)\n" +
                "         group by fup.patient_id\n" +
                "         having (\n" +
                "                        ((ctx_dispensed = 1 or dapsone_dispensed = 1 or prophylaxis_given = 1))\n" +
                "                        AND\n" +
                "                        (((timestampdiff(DAY, date(latest_tca), date(:endDate)) <= 30 or\n" +
                "                           timestampdiff(DAY, date(latest_tca), date(curdate())) <= 30) and\n" +
                "                          ((date(d.effective_disc_date) > date(:endDate) or\n" +
                "                            date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                            and\n" +
                "                         (date(latest_vis_date) >= date(date_discontinued) or\n" +
                "                          date(latest_tca) >= date(date_discontinued) or disc_patient is null))\n" +
                "                    ))e;";

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
    /**
     * HIV testing cohort includes those who tested during the reporting period excluding pmtct clients
     * Composed using htsALLNumberTested AND NOT testedPmtct
     *
     * @return
     */
    public CohortDefinition htsNumberTested() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTested", ReportUtils.map(htsAllNumberTested(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTested AND NOT testedPmtct");
        return cd;
    }
    // HIV testing cohort. includes all those who tested during the reporting period
    public CohortDefinition htsAllNumberTested() {
        String sqlQuery = "select t.patient_id from kenyaemr_etl.etl_hts_test t inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = t.patient_id where test_type =1 and t.voided = 0 and t.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by t.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTested");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested");
        return cd;

    }
/*
* HIV testing cohort includes those who tested at facility during the reporting period excluding pmtct clients
* Composed using htsAllNumberTestedAtFacility AND NOT testedPmtct
* */

    public CohortDefinition htsNumberTestedAtFacility() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedAtFacility", ReportUtils.map(htsAllNumberTestedAtFacility(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedAtFacility AND NOT testedPmtct");
        return cd;
    }

    /**
     * HIV testing cohort. includes all those who tested at the facility during the reporting period
     * facility strategies include PITC, Non Provider initiated testing, integrated vct, stand alone vct
     * Composition for htsNumberTestedAtFacility
     * @return
     */
    public CohortDefinition htsAllNumberTestedAtFacility() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test\n" +
                "      WHERE test_type =1\n" +
                "      AND coalesce(setting = 'Facility',test_strategy in (164954,164953,164163,164955))\n" +
                "      AND visit_date between :startDate and :endDate;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedAtFacility");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested At Facility");
        return cd;

    }
/*
* HIV testing cohort includes those who tested at community during the reporting period excluding pmtct clients
* community strategies include Home based testing, mobile outreaches and other
* */

    public CohortDefinition htsNumberTestedAtCommunity() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedAtCommunity", ReportUtils.map(htsAllNumberTestedAtCommunity(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedAtCommunity AND NOT testedPmtct");
        return cd;
    }

    /**
     * HIV testing cohort. includes all those who tested at the facility during the reporting period
     * Composition for htsNumberTestedAtCommunity
     * community strategies include Home based testing, mobile outreaches and other
     * @return
     */
    public CohortDefinition htsAllNumberTestedAtCommunity() {
        String sqlQuery = "select patient_id FROM kenyaemr_etl.etl_hts_test\n" +
                            "  WHERE test_type =1\n" +
                            "  AND coalesce(setting = 'Community',test_strategy in (159939,159938,5622))\n" +
                            "  AND visit_date between :startDate and :endDate;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedAtCommunity");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested At Community");
        return cd;

    }
    /**
     * HIV testing cohort. includes all those who tested as a couple during the reporting period
     * excluding pmtct tests
     * Composition for htsNumberTestedAsCouple     *
     * @return
     */
    protected CohortDefinition htsNumberTestedAsCouple() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedAsCouple", ReportUtils.map(htsAllNumberTestedAsCouple(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedAsCouple AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested as a couple during the reporting period
     * Composition for htsNumberTestedAsCouple     *
     * @return
     */
    protected CohortDefinition htsAllNumberTestedAsCouple() {
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
    /**
     * HIV testing cohort. includes all those who tested as a Key population during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedKeyPopulation() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedKeyPopulation", ReportUtils.map(htsAllNumberTestedKeyPopulation(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedKeyPopulation AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested as a Key population during the reporting period
     * Composition for htsNumberTestedKeyPopulation     *
     * @return
     */
    protected CohortDefinition htsAllNumberTestedKeyPopulation() {
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
    /**
     * HIV testing cohort. includes all those who tested Positive during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedPositive", ReportUtils.map(htsAllNumberTestedPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedPositive AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested positive during the reporting period
     * Composition for htsNumberTestedPositive     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedPositive() {
        String sqlQuery = "select t.patient_id from  kenyaemr_etl.etl_hts_test t\n" +
                "                                inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id=t.patient_id\n" +
                "                        where t.voided=0 and date(t.visit_date) between date(:startDate) and date(:endDate) and t.test_type=2 and t.final_test_result='Positive'\n" +
                "                        group by t.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Total Number Tested Positive");
        return cd;

    }
    /**
     * HIV testing cohort. includes all those who tested Negative during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedNegative", ReportUtils.map(htsAllNumberTestedNegative(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedNegative AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested positive during the reporting period
     * Composition for htsNumberTestedNegative     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedNegative() {
        String sqlQuery = "select t.patient_id from  kenyaemr_etl.etl_hts_test t\n" +
                "                                inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id=t.patient_id\n" +
                "                        where t.voided=0 and date(t.visit_date) between date(:startDate) and date(:endDate) and t.test_type=1 and t.final_test_result='Negative'\n" +
                "                        group by t.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedNegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Negative");
        return cd;

    }
    /**
     * HIV testing cohort. includes all those who tested Discordant during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedDiscordant() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedDiscordant", ReportUtils.map(htsAllNumberTestedDiscordant(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedDiscordant AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested Discordant during the reporting period
     * Composition for htsNumberTestedDiscordant     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedDiscordant() {
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
    /**
     * HIV testing cohort. includes all those who tested as Key Population and were Positive during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedKeypopPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedKeypopPositive", ReportUtils.map(htsAllNumberTestedKeypopPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedKeypopPositive AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested as Key Population and were Positive during the reporting period
     * Composition for htsNumberTestedKeypopPositive     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedKeypopPositive() {
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
    /**
     * HIV testing cohort. includes all those who tested Positive and Linked during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedPositiveAndLinked() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedPositiveAndLinked", ReportUtils.map(htsAllNumberTestedPositiveAndLinked(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedPositiveAndLinked AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested Positive and Linked during the reporting period
     * Composition for htsNumberTestedPositiveAndLinked     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedPositiveAndLinked() {
        String sqlQuery = "select distinct r.patient_id \n" +
                " from kenyaemr_etl.etl_hts_referral_and_linkage r \n" +
                "  inner join kenyaemr_etl.etl_hts_test t on r.patient_id = t.patient_id and t.final_test_result = 'Positive' \n" +
                " where (r.ccc_number !='' or r.ccc_number IS NOT NULL) and (r.facility_linked_to !='' or r.facility_linked_to IS NOT NULL) \n" +
                "      and t.visit_date between date_sub(:endDate , interval 3 MONTH) and  :endDate ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsNumberTestedPositiveAndLinked");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested Positive and Linked");
        return cd;

    }
    /**
     * HIV testing cohort. includes all those who tested Positive in the last 3 months
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedPositiveInLastThreeMonths() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedPositiveInLastThreeMonths", ReportUtils.map(htsAllNumberTestedPositiveInLastThreeMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedPositiveInLastThreeMonths AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested Positive in the last 3 months
     * Composition for htsNumberTestedPositiveInLastThreeMonths     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedPositiveInLastThreeMonths() {
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
    /**
     * HIV testing cohort. includes all those who were newly tested during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedNew() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedNew", ReportUtils.map(htsAllNumberTestedNew(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedNew AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who who were newly tested during the reporting period
     * Composition for htsNumberTestedNew     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedNew() {
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
    /**
     * HIV testing cohort. Repeat tests during the reporting period
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedRepeat() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedRepeat", ReportUtils.map(htsAllNumberTestedRepeat(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPmtct",
                ReportUtils.map(datimCohortLibrary.testedPmtct(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedRepeat AND NOT testedPmtct");
        return cd;
    }
    /**
     * HIV testing cohort. All repeat tests during the reporting period
     * Composition for htsNumberTestedRepeat     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedRepeat() {
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
                "SELECT\n" +
                "      fup.visit_date,\n" +
                "      fup.patient_id,\n" +
                "      min(e.visit_date)  AS enroll_date,\n" +
                "      max(fup.visit_date) AS latest_vis_date,\n" +
                "      mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11) AS latest_tca,\n" +
                "      disc.patient_id  AS disc_patient,\n" +
                "disc.disc_date as date_disc,\n" +
                "      de.date_started  AS date_started_drugs,\n" +
                "      de.patient_id  AS on_drugs\n" +
                "FROM kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "      JOIN kenyaemr_etl.etl_hiv_enrollment e ON fup.patient_id = e.patient_id\n" +
                "      LEFT OUTER JOIN (select de.patient_id, de.date_started as date_started from kenyaemr_etl.etl_drug_event de group by de.patient_id)de ON e.patient_id = de.patient_id\n" +
                "      LEFT OUTER JOIN\n" +
                "         (select disc.patient_id,max(disc.visit_date) as disc_date from kenyaemr_etl.etl_patient_program_discontinuation disc where\n" +
                "             program_name='HIV' group by patient_id) disc ON disc.patient_id = fup.patient_id\n" +
                "GROUP BY fup.patient_id\n" +
                "HAVING (\n" +
                "          (date(latest_tca) > date(:endDate)\n" +
                "             AND (date(latest_tca) > date(date_disc) OR disc_patient IS NULL))\n" +
                "            OR (\n" +
                "              (\n" +
                "                  (date(latest_tca) BETWEEN date(:startDate) AND date(:endDate)) OR (date(latest_vis_date) BETWEEN date(:startDate) AND date(:endDate))) AND\n" +
                "           (latest_tca > date(disc_date) OR disc_patient IS NULL))) and on_drugs is null\n" +
                ") pre_art;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("preARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Those in care, not started on ARVs");
        return cd;

    }
    /**
     * patients in HIV program assessed for nutrition
     */
    public CohortDefinition assessedForNutritionInHIV() {
        String sqlQuery = "select  e.patient_id\n" +
                "                from (\n" +
                "                select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                     greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                     greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                     greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                     max(d.visit_date) as date_discontinued,\n" +
                "                     d.patient_id as disc_patient,\n" +
                "                     d.effective_disc_date as effective_disc_date,\n" +
                "                     de.patient_id as started_on_drugs,\n" +
                "                     mid(max(concat(fup.visit_date, fup.nutritional_status)), 11) nutrition_status\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                     left outer JOIN\n" +
                "                       (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                        where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                        group by patient_id\n" +
                "                       ) d on d.patient_id = fup.patient_id\n" +
                "                where fup.visit_date <= date(:endDate)\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> '') and\n" +
                "                     ((((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                         and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null))) and\n" +
                "                    nutrition_status is not null\n" +
                "                )e where e.latest_vis_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("assessedForNutrition");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients in HIV program and assessed for nutrition");
        return cd;
    }
    /**
     * patients in HIV program assessed for nutrition and found malnourished
     */
    public CohortDefinition malnourishedInHIV() {
        String sqlQuery = "select  e.patient_id\n" +
                "           from (\n" +
                "                select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                       greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                       greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                       greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                       max(d.visit_date) as date_discontinued,\n" +
                "                       d.patient_id as disc_patient,\n" +
                "                       d.effective_disc_date as effective_disc_date,\n" +
                "                       de.patient_id as started_on_drugs,\n" +
                "                       mid(max(concat(fup.visit_date, fup.nutritional_status)), 11) nutrition_status\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                       left outer JOIN\n" +
                "                         (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                          group by patient_id\n" +
                "                         ) d on d.patient_id = fup.patient_id\n" +
                "                where fup.visit_date <= date(:endDate)\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> '') and\n" +
                "                       ((((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                           and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null))) and nutrition_status in (163302,163303)\n" +
                "                )e where e.latest_vis_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("malnourishedInHIV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients in HIV program and are malnourished");
        return cd;
    }
    /**
     * started on ipt
     * looks for patients started on ipt during a given period
     */
    public CohortDefinition startedOnIPT() {
        String sqlQuery = "select patient_id \n" +
                "from kenyaemr_etl.etl_ipt_initiation \n" +
                "where visit_date between date(:startDate) and date(:endDate) and voided=0 " +
                " ;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startedOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients started on TPT");
        return cd;

    }

    /**
     * those started on ipt 12 months ago and have successfully completed
     */
    public CohortDefinition completedIPT12Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id \n" +
                "from kenyaemr_etl.etl_patient_program \n" +
                "where program='TPT' and date_completed between date(:startDate) and date(:endDate) and date_enrolled  between DATE_SUB(date(:startDate), INTERVAL 1 YEAR) and DATE_SUB(date(:endDate), INTERVAL 1 YEAR) and outcome=1267 " +
                ";";
        cd.setName("completedIPT12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TPT 12 months cohort who have completed");

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
                "where  date(f.visit_date) between date(:startDate) and date(:endDate) and f.cacx_screening in(703, 664, 1118) " +
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
        String sqlQuery ="select d.patient_id from\n" +
                "         (select e.patient_id, max(e.visit_date) as latest_enrollment_date,av.visit_date as 1st_anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       inner join\n" +
                "        (select av.patient_id,av.visit_date as visit_date from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number = 1\n" +
                "        and av.visit_date between date(:startDate) and date(:endDate)) av on e.patient_id = av.patient_id\n" +
                "       group by e.patient_id\n" +
                "       having 1st_anc_visit between date(:startDate) and date(:endDate))d;";

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
                " from kenyaemr_etl.etl_mchs_delivery ld\n" +
                " left outer join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= ld.patient_id\n" +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= ld.patient_id\n" +
                " where (date(ld.visit_date) between date(:startDate) and date(:endDate))\n" +
                " and (ld.final_test_result=\"Positive\" or e.hiv_status = 703 or v.final_test_result =\"Positive\") ;";

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
        String sqlQuery = "select a.patient_id\n" +
                "from (select mch.patient_id, mch.latest_mch_enrolment_date, mch.service_type, e.latest_hiv_enrollment_date,mch.hiv_status_at_enrolment,t.test_result,t.latest_hiv_test_date\n" +
                "from (select mch.patient_id,\n" +
                "     max(mch.visit_date)                                       latest_mch_enrolment_date,\n" +
                "     mid(max(concat(mch.visit_date, mch.service_type)), 11) as service_type,\n" +
                "     mch.hiv_status as hiv_status_at_enrolment\n" +
                "from kenyaemr_etl.etl_mch_enrollment mch where mch.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by mch.patient_id)mch\n" +
                "left join (select e.patient_id, max(e.visit_date) as latest_hiv_enrollment_date\n" +
                "           from kenyaemr_etl.etl_hiv_enrollment e where e.visit_date < date(:endDate)\n" +
                "           group by e.patient_id)e on mch.patient_id = e.patient_id\n" +
                "left join (select t.patient_id, max(t.visit_date) as latest_hiv_test_date,mid(max(concat(t.visit_date,t.final_test_result)),11) as test_result\n" +
                "          from kenyaemr_etl.etl_hts_test t where t.visit_date < date(:endDate)\n" +
                "          group by t.patient_id)t on mch.patient_id = t.patient_id\n" +
                "group by mch.patient_id\n" +
                "having mch.service_type = 1622\n" +
                "and (mch.latest_mch_enrolment_date > e.latest_hiv_enrollment_date or hiv_status_at_enrolment = 703 or (mch.latest_mch_enrolment_date > t.latest_hiv_test_date\n" +
                "and t.test_result = 'Positive')))a;";

        cd.setName("Known Positive at First ANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Known Positive at First ANC");

        return cd;
    }
    /**
     * Clients withInitial test at ANC 1 HV02-04     *
     * Composed using negativePMTCTANC1 OR positivePMTCTANC1
     *
     * @return
     */
    public CohortDefinition initialHIVTestInMchmsAntenatal() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("negativePMTCTANC1",
                ReportUtils.map(datimCohortLibrary.negativePMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positivePMTCTANC1",
                ReportUtils.map(datimCohortLibrary.positivePMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(negativePMTCTANC1 OR positivePMTCTANC1");
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
        String sqlQuery =  "select distinct v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "inner join kenyaemr_etl.etl_drug_event d on d.patient_id=v.patient_id\n" +
                "where d.program = 'HIV' and date(v.visit_date) BETWEEN date(:startDate) and date(:endDate)\n" +
                "and d.date_started < v.visit_date and v.anc_visit_number =1;";

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
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "where (e.hiv_status=703 and  e.visit_date between date(:startDate) and date(:endDate))";

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

    //Infant ARV Prophylaxis ANC HV02-39
    public CohortDefinition infantArvProphylaxisANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select t.patient_id from\n" +
                "(select en.patient_id as patient_id,\n" +
                "       ifnull(date(v.visit_date),'0000-00-00') as anc_prophylaxis_date,\n" +
                "       ifnull(date(ld.visit_date),'0000-00-00') as ld_prophylaxis_date,\n" +
                "       ifnull(date(p.visit_date),'0000-00-00') as pnc_prophylaxis_date,\n" +
                "       COALESCE(date(v.visit_date),date(ld.visit_date),date(p.visit_date)) as earliest_prophylaxis_date\n" +
                "from kenyaemr_etl.etl_mch_enrollment en\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mch_antenatal_visit\n" +
                "      where baby_nvp_dispensed = 160123 or baby_azt_dispensed = 160123)  v on v.patient_id = en.patient_id\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mchs_delivery\n" +
                "            where baby_nvp_dispensed = 1 or baby_azt_dispensed = 1) ld on ld.patient_id = en.patient_id\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mch_postnatal_visit\n" +
                "         where baby_nvp_dispensed = 160123 or baby_azt_dispensed = 160123) p on p.patient_id = en.patient_id\n" +
                "where  date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "Group by anc_prophylaxis_date,patient_id\n" +
                "Having anc_prophylaxis_date = earliest_prophylaxis_date) t;\n";

        cd.setName("infantArvProphylaxisANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis at ANC");

        return cd;
    }

    //Infant ARV Prophylaxis L&D	HV02-40
    public CohortDefinition infantArvProphylaxisLabourAndDelivery(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select t.patient_id from\n" +
                "  (select en.patient_id as patient_id,\n" +
                "       ifnull(date(v.visit_date),'0000-00-00') as anc_prophylaxis_date,\n" +
                "       ifnull(date(ld.visit_date),'0000-00-00') as ld_prophylaxis_date,\n" +
                "       ifnull(date(p.visit_date),'0000-00-00') as pnc_prophylaxis_date,\n" +
                "       COALESCE(date(v.visit_date),date(ld.visit_date),date(p.visit_date)) as earliest_prophylaxis_date\n" +
                "from kenyaemr_etl.etl_mch_enrollment en\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mch_antenatal_visit\n" +
                "  where baby_nvp_dispensed = 160123 or baby_azt_dispensed = 160123)  v on v.patient_id = en.patient_id\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mchs_delivery\n" +
                "  where baby_nvp_dispensed = 1 or baby_azt_dispensed = 1) ld on ld.patient_id = en.patient_id\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mch_postnatal_visit\n" +
                "  where baby_nvp_dispensed = 160123 or baby_azt_dispensed = 160123) p on p.patient_id = en.patient_id\n" +
                "where  date(ld.visit_date) between date(:startDate) and date(:endDate)\n" +
                "Group by ld_prophylaxis_date,patient_id\n" +
                "Having ld_prophylaxis_date = earliest_prophylaxis_date) t ;";

        cd.setName("infantArvProphylaxisLabourAndDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis during Labour and Delivery");

        return cd;
    }

    //Infant ARV Prophylaxis <8weeks PNC	HV02-41
    public CohortDefinition infantArvProphylaxisPNCLessThan8Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select t.patient_id from\n" +
                "  (select en.patient_id as patient_id,\n" +
                "       ifnull(date(v.visit_date),'0000-00-00') as anc_prophylaxis_date,\n" +
                "       ifnull(date(ld.visit_date),'0000-00-00') as ld_prophylaxis_date,\n" +
                "       ifnull(date(p.visit_date),'0000-00-00') as pnc_prophylaxis_date,\n" +
                "       COALESCE(date(v.visit_date),date(ld.visit_date),date(p.visit_date)) as earliest_prophylaxis_date\n" +
                "from kenyaemr_etl.etl_mch_enrollment en\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mch_antenatal_visit\n" +
                "  where baby_nvp_dispensed = 160123 or baby_azt_dispensed = 160123)  v on v.patient_id = en.patient_id\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mchs_delivery\n" +
                "  where baby_nvp_dispensed = 1 or baby_azt_dispensed = 1) ld on ld.patient_id = en.patient_id\n" +
                "  left join (select patient_id, visit_date, baby_nvp_dispensed, baby_azt_dispensed from kenyaemr_etl.etl_mch_postnatal_visit\n" +
                "  where baby_nvp_dispensed = 160123 or baby_azt_dispensed = 160123) p on p.patient_id = en.patient_id\n" +
                "where  date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "       and round(DATEDIFF(ld.visit_date,DATE(:endDate))/7) <=8\n" +
                "Group by pnc_prophylaxis_date,patient_id\n" +
                "Having pnc_prophylaxis_date = earliest_prophylaxis_date) t ;";

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
    /**
     * VMMC
     * HV04-13
     *Number of males circumcised with moderate Adverse Events during procedutre
     * @return
     */
    public CohortDefinition circumcisedWithModerateAEDuringProcedure() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_vmmc_enrolment e\n" +
                "  inner join kenyaemr_etl.etl_vmmc_circumcision_procedure c on e.patient_id = c.patient_id\n" +
                "  inner join kenyaemr_etl.etl_adverse_events a on e.patient_id = a.patient_id and a.form ='vmmc-procedure'\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and a.severity = 1499;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_MODERATE_AE_DURING");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised with moderate AE during procedure");
        return cd;
    }
    /**
     * VMMC
     * HV04-14
     *Number of males circumcised with severe Adverse Events during procedutre
     * @return
     */
    public CohortDefinition circumcisedWithSevereAEDuringProcedure() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_vmmc_enrolment e\n" +
                "  inner join kenyaemr_etl.etl_vmmc_circumcision_procedure c on e.patient_id = c.patient_id\n" +
                "  inner join kenyaemr_etl.etl_adverse_events a on e.patient_id = a.patient_id and a.form ='vmmc-procedure'\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and a.severity = 1500;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_SEVERE_AE_DURING");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised with severe AE during procedure");
        return cd;
    }
    /**
     * VMMC
     * HV04-15
     *Number of males circumcised with moderate Adverse Events post procedutre
     * @return
     */
    public CohortDefinition circumcisedWithModerateAEPostProcedure() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_vmmc_enrolment e\n" +
                "  inner join kenyaemr_etl.etl_vmmc_circumcision_procedure c on e.patient_id = c.patient_id\n" +
                "  inner join kenyaemr_etl.etl_adverse_events a on e.patient_id = a.patient_id and a.form ='vmmc-followup'\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and a.severity = 1499;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_MODERATE_AE_POST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised with moderate AE post procedure");
        return cd;
    }
    /**
     * VMMC
     * HV04-16
     *Number of males circumcised with severe Adverse Events post procedutre
     * @return
     */
    public CohortDefinition circumcisedWithSevereAEPostProcedure() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_vmmc_enrolment e\n" +
                "  inner join kenyaemr_etl.etl_vmmc_circumcision_procedure c on e.patient_id = c.patient_id\n" +
                "  inner join kenyaemr_etl.etl_adverse_events a on e.patient_id = a.patient_id and a.form ='vmmc-followup'\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and a.severity = 1500;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_SEVERE_AE_POST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised with severe AE post procedure");
        return cd;
    }
}
