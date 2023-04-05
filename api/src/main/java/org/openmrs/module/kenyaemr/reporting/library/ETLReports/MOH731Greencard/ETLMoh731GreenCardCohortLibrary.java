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
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hiv_enrollment e\n" +
                "         join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id\n" +
                "where date(e.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and (e.patient_type = 164144 or (e.patient_type is null and transfer_in_date is null));";
        cd.setName("newHhivEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New HIV Enrollment");

        return cd;
    }
    public CohortDefinition kpsWithHIVFollowupVisit(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "where date(f.visit_date) <= date(:endDate)\n" +
                "  and f.population_type is not null\n" +
                "group by f.patient_id\n" +
                "having mid(max(concat(f.visit_date, f.population_type)), 11) = 164929;";
        cd.setName("kpsWithHIVFollowupVisit");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPS with HIV followup visit");

        return cd;
    }

    /**
     * KPs who were newly enrolled to care within the month
     * @return
     */
    public CohortDefinition hivEnrolledKPs() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("enrolledKPs",ReportUtils.map(enrolledKPs(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpsWithHIVFollowupVisit",ReportUtils.map(kpsWithHIVFollowupVisit(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivEnrollment", ReportUtils.map(hivEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(enrolledKPs or kpsWithHIVFollowupVisit) AND hivEnrollment");
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

    /**
     * KPs started on ART
     * @return
     */
    public CohortDefinition kpsStartedOnART() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpsWithHIVFollowupVisit",ReportUtils.map(kpsWithHIVFollowupVisit(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("enrolledKPs",ReportUtils.map(enrolledKPs(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(enrolledKPs or kpsWithHIVFollowupVisit) AND startedOnART");
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

    /**
     * Clients enrolled in KP program
     * @return
     */
    public CohortDefinition enrolledKPs() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "                         where date(c.visit_date) <= date(:endDate)\n" +
                "                         group by c.client_id having mid(max(concat(date(c.visit_date), c.key_population_type)), 11)\n" +
                "                in ('FSW','MSM','MSW','PWUD','PWID','Transgender','People in prison and other closed settings');";
        cd.setName("enrolledKPs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KP clients");
        return cd;
    }
    /**
     * KPs currently on ART
     * @return
     */
    public CohortDefinition kpsCurrentlyOnArtOnART() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpsWithHIVFollowupVisit",ReportUtils.map(kpsWithHIVFollowupVisit(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("enrolledKPs",ReportUtils.map(enrolledKPs(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(enrolledKPs or kpsWithHIVFollowupVisit) AND currentlyOnArt");
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
     * Patients screened for TB in the last visit
     * @return
     */
    public CohortDefinition screenedForTbWithinPeriod() {
// look all active in care who were screened for tb
        String sqlQuery = "select a.patient_id from\n" +
                "     (select max(tb.visit_date) as max_visit,tb.patient_id,mid(max(concat(date(tb.visit_date),ifnull(tb.resulting_tb_status,0))),11) as tb_screened,\n" +
                "mid(max(concat(date(tb.visit_date),ifnull(tb.person_present,0))),11) as person_present from kenyaemr_etl.etl_tb_screening tb\n" +
                "group by tb.patient_id)a where a.person_present = 978 and a.tb_screened in (1660,1662,142177);";
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

        String sqlQuery = "select f.patient_id\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "         join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id = f.patient_id\n" +
                "where date(f.visit_date) between date(:startDate) and date(:endDate);";
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
        cd.setCompositionString("(htsAllNumberTested");
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
        cd.setCompositionString("htsAllNumberTestedAtFacility");
        return cd;
    }

    /**
     * HIV testing cohort. includes all those who tested at the facility during the reporting period
     * facility strategies include PITC, Non Provider initiated testing, integrated vct, stand alone vct
     * Composition for htsNumberTestedAtFacility
     * @return
     */
    public CohortDefinition htsAllNumberTestedAtFacility() {
        String sqlQuery = "select patient_id\n" +
                "from kenyaemr_etl.etl_hts_test\n" +
                "WHERE test_type = 1\n" +
                "  AND setting = 'Facility'\n" +
                "  AND visit_date between date(:startDate) and date(:endDate);";
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
        cd.setCompositionString("htsAllNumberTestedAtCommunity");
        return cd;
    }

    /**
     * HIV testing cohort. includes all those who tested at the facility during the reporting period
     * Composition for htsNumberTestedAtCommunity
     * community strategies include Home based testing, mobile outreaches and other
     * @return
     */
    public CohortDefinition htsAllNumberTestedAtCommunity() {
        String sqlQuery = "select patient_id\n" +
                "FROM kenyaemr_etl.etl_hts_test\n" +
                "WHERE test_type = 1\n" +
                "  AND setting = 'Community'\n" +
                "  AND visit_date between date(:startDate) and date(:endDate);";
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
        cd.setCompositionString("htsAllNumberTestedAsCouple");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested as a couple during the reporting period
     * Composition for htsNumberTestedAsCouple     *
     * @return
     */
    protected CohortDefinition htsAllNumberTestedAsCouple() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and client_tested_as ='Couple' and date(visit_date) between date(:startDate) and date(:endDate)";
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
        cd.setCompositionString("htsAllNumberTestedKeyPopulation");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested as a Key population during the reporting period
     * Composition for htsNumberTestedKeyPopulation     *
     * @return
     */
    protected CohortDefinition htsAllNumberTestedKeyPopulation() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1 \n" +
                " and population_type ='Key Population' and visit_date between date(:startDate) and date(:endDate)";
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
        cd.setCompositionString("htsAllNumberTestedPositive");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested positive during the reporting period
     * Composition for htsNumberTestedPositive     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedPositive() {
        String sqlQuery = "select t.patient_id\n" +
                "from kenyaemr_etl.etl_hts_test t\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = t.patient_id\n" +
                "where t.voided = 0\n" +
                "  and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and t.test_type = 1\n" +
                "  and t.final_test_result = 'Positive'\n" +
                "group by t.patient_id;";
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
        cd.setCompositionString("htsAllNumberTestedNegative");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested positive during the reporting period
     * Composition for htsNumberTestedNegative     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedNegative() {
        String sqlQuery = "select t.patient_id\n" +
                "from kenyaemr_etl.etl_hts_test t\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = t.patient_id\n" +
                "where t.voided = 0\n" +
                "  and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and t.test_type = 1\n" +
                "  and t.final_test_result = 'Negative'\n" +
                "group by t.patient_id;";
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
        cd.setCompositionString("htsAllNumberTestedDiscordant");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested Discordant during the reporting period
     * Composition for htsNumberTestedDiscordant     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedDiscordant() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and couple_discordant ='Yes' and date(visit_date) between date(:startDate) and date(:endDate)";
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
        cd.setCompositionString("htsAllNumberTestedKeypopPositive");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who tested as Key Population and were Positive during the reporting period
     * Composition for htsNumberTestedKeypopPositive     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedKeypopPositive() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_hts_test where test_type =1\n" +
                " and population_type ='Key Population' and final_test_result='Positive' and date(visit_date) between date(:startDate) and date(:endDate)";
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
     * excluding pmtct tests
     * @return
     */
    protected CohortDefinition htsNumberTestedPositiveAndLinked() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("referredAndLinkedSinceThreeMonthsAgo", ReportUtils.map(referredAndLinkedSinceThreeMonthsAgo(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("enrolledToHIVAndOnDrugsSinceThreeMonthsAgo", ReportUtils.map(enrolledToHIVAndOnDrugsSinceThreeMonthsAgo(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("htsNumberTestedPositiveThreeMonthsAgo",ReportUtils.map(htsNumberTestedPositiveThreeMonthsAgo(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsNumberTestedPositiveThreeMonthsAgo AND (referredAndLinkedSinceThreeMonthsAgo or enrolledToHIVAndOnDrugsSinceThreeMonthsAgo)");
        return cd;
    }
    /**
     * Clients who were referred and linked since 3 months ago. Must be on drugs
     * @return
     */
    public CohortDefinition referredAndLinkedSinceThreeMonthsAgo() {
        String sqlQuery = "select r.patient_id\n" +
                "from kenyaemr_etl.etl_hts_referral_and_linkage r\n" +
                "where (r.ccc_number != '' or r.ccc_number IS NOT NULL)\n" +
                "  and (r.facility_linked_to != '' or r.facility_linked_to IS NOT NULL)\n" +
                "  and (r.art_start_date is not null or r.art_start_date != '')\n" +
                "  and (r.enrollment_date is not null or r.enrollment_date != '')\n" +
                "  and r.visit_date between date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
                "                                    interval 3 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("referredAndLinked");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Referred and linked since 3 months ago");
        return cd;

    }

    /**
     * Clients enrolled to HIV program since three months ago and started on drugs
     * @return
     */
    public CohortDefinition enrolledToHIVAndOnDrugsSinceThreeMonthsAgo() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hiv_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_drug_event d on e.patient_id = d.patient_id and d.program = 'HIV'\n" +
                "where date(e.visit_date) >= date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
                "                                     interval 3 MONTH)\n" +
                "  and date(d.date_started) between date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
                "                                            interval 3 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("enrolledToHIVAndOnDrugsSinceThreeMonthsAgo");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Enrolled to HIV program since three months ago and started on drugs");
        return cd;

    }
    /**
     * HIV testing cohort. includes all those who tested Positive 3 months ago
     * excluding pmtct tests        *
     * @return
     */
    protected CohortDefinition htsNumberTestedPositiveThreeMonthsAgo() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedPositiveThreeMonthsAgo", ReportUtils.map(htsAllNumberTestedPositiveThreeMonthsAgo(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(htsAllNumberTestedPositiveThreeMonthsAgo");
        return cd;
    }

    /**
     * Ever tested HIV positive
     * @return
     */
    public CohortDefinition everTestedHIVPositive() {
        String sqlQuery = "select t.patient_id from kenyaemr_etl.etl_hts_test t where t.final_test_result = 'Positive' and date(t.visit_date) <= date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Ever tested positive for HIV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Ever tested positive for HIV");
        return cd;
    }

    /**
     * Assessed for HIV risk within the period
     * @return
     */
    public CohortDefinition assessedForHIVRiskWithinPeriod() {
        String sqlQuery = "select a.patient_id from kenyaemr_etl.etl_prep_behaviour_risk_assessment a where date(a.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Assessed for HIV risk within the period");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Assessed for HIV risk within the period");
        return cd;
    }

    /**
     * Assessed for HIV risk. Excludes anyone ever tested HIV positive as of reporting period
     * @return
     */
    protected CohortDefinition numberAssessedForHIVRisk() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("assessedForHIVRiskWithinPeriod", ReportUtils.map(assessedForHIVRiskWithinPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("everTestedHIVPositive", ReportUtils.map(everTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("assessedForHIVRiskWithinPeriod AND NOT everTestedHIVPositive");
        return cd;
    }

    /**
     * Tested in PMTCT 3 months ago
     * @return
     */
    private CohortDefinition testedPmtct3MonthsAgo() {
        String sqlQuery = "select hts.patient_id\n" +
                "from kenyaemr_etl.etl_hts_test hts\n" +
                "where hts.hts_entry_point in (160538, 160456, 1623)\n" +
                "  and hts.patient_given_result = 'Yes'\n" +
                "  and hts.voided = 0\n" +
                "  and hts.visit_date\n" +
                "    between date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
                "                     interval 3 MONTH)\n" +
                "    and date_sub(date(:endDate), interval 3 MONTH);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("testedPmtct3MonthsAgo");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested in PMTCT Three Months AGO");
        return cd;
    }

    /**
     * HIV testing cohort. includes all those who tested Positive in the last 3 months
     * Composition for htsNumberTestedPositiveInLastThreeMonths     *
     * @return
     */
    public CohortDefinition htsAllNumberTestedPositiveThreeMonthsAgo() {
        String sqlQuery = "select patient_id\n" +
                "from kenyaemr_etl.etl_hts_test t\n" +
                "where test_type = 1\n" +
                "  and final_test_result = 'Positive'\n" +
                "  and t.visit_date between date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
                "                                    interval 3 MONTH)\n" +
                "    and date_sub(date(:endDate), interval 3 MONTH);";
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
     * @return
     */
    protected CohortDefinition htsNumberTestedNew() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("htsAllNumberTestedNew", ReportUtils.map(htsClientsForTheFirstTimeEver(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("htsClientsWithPreviousTestOver12MonthsAgo", ReportUtils.map(htsClientsWithPreviousTestOver12MonthsAgo(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("htsAllNumberTestedNew or htsClientsWithPreviousTestOver12MonthsAgo");
        return cd;
    }
    /**
     * HIV testing cohort. includes all those who were newly tested during the reporting period
     * Composition for htsNumberTestedNew     *
     * @return
     */
    /**
     * Clients testing for the first time in their lives
     * @return
     */
    public CohortDefinition htsClientsForTheFirstTimeEver() {
        String sqlQuery = "select patient_id\n" +
                "              from (\n" +
                "                SELECT\n" +
                "                  patient_id,\n" +
                "                  visit_date      AS test_date\n" +
                "                FROM kenyaemr_etl.etl_hts_test\n" +
                "                WHERE test_type = 1\n" +
                "                and ever_tested_for_hiv = 'No'\n" +
                "                GROUP BY patient_id\n" +
                "                       having count(patient_id) = 1\n" +
                "              ) t\n" +
                "              where date(test_date) between date(:startDate) and date(:endDate)\n" +
                "              GROUP BY patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsClientsForTheFirstTimeEver");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients testing for the first time in their lives");
        return cd;

    }

    /**
     * HTS clients testing again with previous test greater than 12 months ago
     * @return
     */
    public CohortDefinition htsClientsWithPreviousTestOver12MonthsAgo() {
        String sqlQuery = "SELECT patient_id\n" +
                "FROM kenyaemr_etl.etl_hts_test\n" +
                "WHERE test_type = 1\n" +
                "  and ever_tested_for_hiv = 'Yes'\n" +
                "  and months_since_last_test > 12\n" +
                "  and date(visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsClientsWithPreviousTestOver12MonthsAgo");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients testing for the first time in their lives");
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
        cd.addSearch("htsAllNumberTestedRepeat", ReportUtils.map(htsRepeatTestsMuxEnc(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("htsRepeatClientReported", ReportUtils.map(htsRepeatClientReported(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("htsAllNumberTestedRepeat or htsRepeatClientReported");
        return cd;
    }
    /**
     * HIV testing cohort. Clients with a repeat test within 12 months. Checking for multiple HTS encounters
     * @return
     */
    public CohortDefinition htsRepeatTestsMuxEnc() {
        String sqlQuery = "select patient_id\n" +
                "                from (\n" +
                "                       SELECT\n" +
                "                         patient_id,\n" +
                "                         max(visit_date)      AS latest_test_date,\n" +
                "                         min(visit_date) AS first_test_date\n" +
                "                       FROM kenyaemr_etl.etl_hts_test\n" +
                "                       WHERE test_type = 1\n" +
                "                       GROUP BY patient_id\n" +
                "                       having latest_test_date between date(:startDate) and date(:endDate)\n" +
                "                          and (latest_test_date > first_test_date and timestampdiff(MONTH,first_test_date,latest_test_date) <= 12)\n" +
                "                     ) t;\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsRepeatTestsMuxEnc");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Number Tested repeat");
        return cd;

    }

    /**
     * Clients who reported having had a HIV test within the last 12 months
     * @return
     */
    public CohortDefinition htsRepeatClientReported() {
        String sqlQuery = "SELECT patient_id\n" +
                "FROM kenyaemr_etl.etl_hts_test\n" +
                "WHERE test_type = 1\n" +
                "  and ever_tested_for_hiv = 'Yes'\n" +
                "  and months_since_last_test <= 12\n" +
                "  and date(visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("htsRepeatClientReported");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients who reported having had a HIV test within the last 12 months");
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
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  inner join (select mch.patient_id, max(mch.visit_date) as latest_mch_enrolment_date, mch.hiv_status as hiv_status_at_enrolment\n" +
                "              from kenyaemr_etl.etl_mch_enrollment mch group by mch.patient_id )mch on mch.patient_id = v.patient_id\n" +
                "  left join (select e.patient_id, max(e.visit_date) as latest_hiv_enrollment_date\n" +
                "             from kenyaemr_etl.etl_hiv_enrollment e where e.visit_date < date(:endDate)\n" +
                "             group by e.patient_id)e on v.patient_id = e.patient_id\n" +
                "  left join (select t.patient_id, max(t.visit_date) as latest_hiv_test_date,mid(max(concat(t.visit_date,t.final_test_result)),11) as test_result\n" +
                "             from kenyaemr_etl.etl_hts_test t where t.visit_date < date(:endDate)\n" +
                "             group by t.patient_id)t on v.patient_id = t.patient_id\n" +
                "where v.visit_date between date(:startDate) and date(:endDate) and v.anc_visit_number = 1\n" +
                "      and ((mch.latest_mch_enrolment_date > e.latest_hiv_enrollment_date or hiv_status_at_enrolment = 703 or\n" +
                "            (mch.latest_mch_enrolment_date > t.latest_hiv_test_date and t.test_result = 'Positive')))\n" +
                "group by v.patient_id";

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
        String sqlQuery ="select ld.patient_id\n" +
                "from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "         left outer join (select a.patient_id, a.visit_date\n" +
                "                          from kenyaemr_etl.etl_mch_antenatal_visit a\n" +
                "                          where a.final_test_result is not null\n" +
                "                            and date(a.visit_date) <= date(:endDate)\n" +
                "                          group by a.patient_id\n" +
                "                          having max(date(a.visit_date))) a\n" +
                "                         on ld.patient_id = a.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 160538\n" +
                "                      and date(t.visit_date) <= date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(t.visit_date, t.final_test_result)), 11) is not null) t\n" +
                "                   on ld.patient_id = t.patient_id\n" +
                "where date(ld.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and ld.final_test_result in ('Positive', 'Negative', 'Inconclusive')\n" +
                "  and a.patient_id is null and (t.patient_id is null or date(ld.visit_date) < date(t.visit_date));";

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
        String sqlQuery ="select p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "         left outer join (select a.patient_id, a.visit_date\n" +
                "                          from kenyaemr_etl.etl_mch_antenatal_visit a\n" +
                "                          where a.final_test_result is not null\n" +
                "                            and date(a.visit_date) <= date(:endDate)\n" +
                "                          group by a.patient_id\n" +
                "                          having max(date(a.visit_date))) a\n" +
                "                         on p.patient_id = a.patient_id\n" +
                "         left outer join (select ld.patient_id, ld.visit_date\n" +
                "                          from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "                          where ld.final_test_result is not null\n" +
                "                            and date(ld.visit_date) <= date(:endDate)\n" +
                "                          group by ld.patient_id\n" +
                "                          having max(date(ld.visit_date))) ld\n" +
                "                         on p.patient_id = ld.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 160538\n" +
                "                      and date(t.visit_date) <= date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(t.visit_date, t.final_test_result)), 11) is not null) t\n" +
                "                   on p.patient_id = t.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and p.final_test_result in ('Positive', 'Negative','Inconclusive')\n" +
                "  and timestampdiff(WEEK,date(p.delivery_date),date(p.visit_date)) between 0 and 6\n" +
                "  and (a.patient_id is null and ld.patient_id is null and (date(p.visit_date) < date(t.visit_date) or t.patient_id is null));";

        cd.setName("Initial Test at PNC <=6 Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initial Test at PNC <=6 Weeks");

        return cd;
    }
    /**
     *  HIV Status Total HV02-07 --Computed
     * Composed using knownPositiveAtFirstANC OR initialHIVTestInMchmsAntenatal OR testedForHivInMchmsDelivery OR  initialTestAtPNCUpto6Weeks
     *
     * @return
     */
    public CohortDefinition testedForHivInMchmsTotal() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("knownPositiveAtFirstANC",
                ReportUtils.map(knownPositiveAtFirstANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialHIVTestInMchmsAntenatal",
                ReportUtils.map(initialHIVTestInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedForHivInMchmsDelivery",
                ReportUtils.map(testedForHivInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedHivPositiveInPNCWithin6Weeks",
                ReportUtils.map(testedHivPositiveInPNCWithin6Weeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(knownPositiveAtFirstANC OR initialHIVTestInMchmsAntenatal OR testedForHivInMchmsDelivery OR testedHivPositiveInPNCWithin6Weeks ");
        return cd;
    }
    //Retesting PNC <=6 weeks HV02-08
    public CohortDefinition pncRetestUpto6Weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="select p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "         left join (select a.patient_id, a.visit_date\n" +
                "                          from kenyaemr_etl.etl_mch_antenatal_visit a\n" +
                "                          where a.final_test_result is not null\n" +
                "                            and date(a.visit_date) <= date(:endDate)\n" +
                "                          group by a.patient_id\n" +
                "                          having max(date(a.visit_date))) a\n" +
                "                         on p.patient_id = a.patient_id\n" +
                "         left outer join (select ld.patient_id, ld.visit_date\n" +
                "                          from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "                          where ld.final_test_result is not null\n" +
                "                            and date(ld.visit_date) <= date(:endDate)\n" +
                "                          group by ld.patient_id\n" +
                "                          having max(date(ld.visit_date))) ld\n" +
                "                         on p.patient_id = ld.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 160538\n" +
                "                      and date(t.visit_date) <= date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(t.visit_date, t.final_test_result)), 11) is not null) t\n" +
                "                   on p.patient_id = t.patient_id\n" +
                "where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and p.final_test_result in ('Positive', 'Negative','Inconclusive')\n" +
                "  and timestampdiff(WEEK,date(p.delivery_date),date(p.visit_date)) between 0 and 6\n" +
                "  and (a.patient_id is not null or t.patient_id is not null or ld.patient_id is not null);";

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
        String sqlQuery ="select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join (select a.patient_id, a.visit_date, a.delivery_date\n" +
                "                    from kenyaemr_etl.etl_mch_postnatal_visit a\n" +
                "                    where a.final_test_result is not null\n" +
                "                      and date(a.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                    group by a.patient_id\n" +
                "                    having max(date(a.visit_date))) p\n" +
                "                   on e.patient_id = p.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 160538\n" +
                "                      and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(date(t.visit_date), t.final_test_result)), 11) in ('Positive', 'Negative')) t\n" +
                "                   on e.patient_id = t.patient_id\n" +
                "where\n" +
                "    (timestampdiff(WEEK, date(p.delivery_date), date(p.visit_date)) > 6 and\n" +
                "     timestampdiff(MONTH, date(p.delivery_date), date(p.visit_date)) <= 6)\n" +
                "   or (timestampdiff(WEEK, date(p.delivery_date), date(t.visit_date)) > 6 and\n" +
                "       timestampdiff(MONTH, date(p.delivery_date), date(t.visit_date)) <= 6);";

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
        String sqlQuery ="select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left outer join (select ld.patient_id, ld.visit_date\n" +
                "                          from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "                          where ld.final_test_result = 'Positive'\n" +
                "                            and date(ld.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                          group by ld.patient_id\n" +
                "                          having max(date(ld.visit_date))) ld\n" +
                "                         on e.patient_id = ld.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 160456\n" +
                "                      and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Positive') t\n" +
                "                   on e.patient_id = t.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "  and (ld.patient_id is not null or t.patient_id is not null);";

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
        String sqlQuery ="select p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left outer join (select p.patient_id, p.visit_date, p.delivery_date\n" +
                "                          from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                          where p.final_test_result = 'Positive' and p.patient_given_result = 'Yes'\n" +
                "                            and date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                          group by p.patient_id\n" +
                "                          having max(date(p.visit_date))) p\n" +
                "                         on p.patient_id = e.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date,t.final_test_result\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 1623\n" +
                "                      and date(t.visit_date)  between date(:startDate) and date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Positive'\n" +
                "             and mid(max(concat(t.visit_date, t.patient_given_result)), 11) = 'Yes') t\n" +
                "                   on p.patient_id = t.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "  and (timestampdiff(WEEK,date(p.delivery_date),date(p.visit_date)) between 0 and 6\n" +
                "  or timestampdiff(WEEK,date(p.delivery_date),date(t.visit_date)) between 0 and 6);";

        cd.setName("testedHivPositiveInPNCWithin6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PNC within 6 weeks");

        return cd;
    }
    //Total HIV positive Mothers HV02-14
    public CohortDefinition totalHivPositiveMothersInMchms(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("knownPositiveAtFirstANC",
                ReportUtils.map(knownPositiveAtFirstANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialHIVTestInMchmsAntenatal",
                ReportUtils.map(initialHIVTestInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedForHivInMchmsDelivery",
                ReportUtils.map(testedForHivInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialTestAtPNCUpto6Weeks",
                ReportUtils.map(initialTestAtPNCUpto6Weeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(knownPositiveAtFirstANC OR initialHIVTestInMchmsAntenatal OR testedForHivInMchmsDelivery OR initialTestAtPNCUpto6Weeks ");
        return cd;
    }
    //   PNC >6 weeks and <=6 months   HV02-15
    public CohortDefinition totalHivPositivePNC6WeeksTo6monthsInMchms(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select p.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left outer join (select p.patient_id, p.visit_date, p.delivery_date\n" +
                "                          from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                          where p.final_test_result = 'Positive' and p.patient_given_result = 'Yes'\n" +
                "                            and date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                          group by p.patient_id\n" +
                "                          having max(date(p.visit_date))) p\n" +
                "                         on p.patient_id = e.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date,t.final_test_result\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.hts_entry_point = 1623\n" +
                "                      and date(t.visit_date)  between date(:startDate) and date(:endDate)\n" +
                "                    group by t.patient_id\n" +
                "                    having max(date(t.visit_date))\n" +
                "                       and mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Positive'\n" +
                "                       and mid(max(concat(t.visit_date, t.patient_given_result)), 11) = 'Yes') t\n" +
                "                   on p.patient_id = t.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "  and  (timestampdiff(WEEK, date(p.delivery_date), date(p.visit_date)) > 6 and\n" +
                "        timestampdiff(MONTH, date(p.delivery_date), date(p.visit_date)) <= 6)\n" +
                "   or (timestampdiff(WEEK, date(p.delivery_date), date(t.visit_date)) > 6 and\n" +
                "       timestampdiff(MONTH, date(p.delivery_date), date(t.visit_date)) <= 6);";

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
        String sqlQuery =  "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_mch_antenatal_visit v on e.patient_id = v.patient_id\n" +
                "         left join (select patient_id,\n" +
                "                           max(visit_date)                                         as last_reg_date\n" +
                "                    from kenyaemr_etl.etl_drug_event d\n" +
                "                    where program = 'HIV'\n" +
                "                      and d.date_started <= date(:endDate)\n" +
                "                    GROUP BY patient_id) d on v.patient_id = d.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and (date(e.ti_date_started_art) < date(v.visit_date) or\n" +
                "       (d.last_reg_date < date(v.visit_date)));";

        cd.setName("totalOnHAARTAtFirstANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On HAART At first ANC");

        return cd;
    }

    //  Given HAART during ANC
    public CohortDefinition givenHAARTAtANC(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "where v.haart_given = 1\n" +
                "  and date(v.visit_date) between date(:startDate) and date(:endDate);";
        cd.setName("givenHAARTAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Given HAART At ANC");

        return cd;
    }

    /**
     * Started HAART at during ANC
     * @return
     */
    public CohortDefinition startedHAARTAtANC() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("givenHAARTAtANC",ReportUtils.map(givenHAARTAtANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("totalOnHAARTAtFirstANC",ReportUtils.map(totalOnHAARTAtFirstANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("givenHAARTAtANC AND NOT totalOnHAARTAtFirstANC");
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
        String sqlQuery =  "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         inner join (select p.patient_id, p.visit_date, p.delivery_date\n" +
                "                          from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                          where p.mother_haart_given = 1065\n" +
                "                            and date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                          group by p.patient_id\n" +
                "                          having max(date(p.visit_date))) p\n" +
                "                         on p.patient_id = e.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "  and timestampdiff(WEEK,date(p.delivery_date),date(p.visit_date)) between 0 and 6;";

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
        String sqlQuery =  "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         inner join (select p.patient_id, p.visit_date, p.delivery_date\n" +
                "                     from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                     where p.mother_haart_given = 1065\n" +
                "                       and date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                     group by p.patient_id\n" +
                "                     having max(date(p.visit_date))) p\n" +
                "                    on p.patient_id = e.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "and (timestampdiff(WEEK, date(p.delivery_date), date(p.visit_date)) > 6 and\n" +
                "    timestampdiff(MONTH, date(p.delivery_date), date(p.visit_date)) <= 6);";

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
        String sqlQuery =  "select distinct mch.patient_id\n" +
                "  from kenyaemr_etl.etl_mch_enrollment mch\n" +
                "  inner join kenyaemr_etl.etl_drug_event d on d.patient_id=mch.patient_id and d.program = 'HIV'\n" +
                "  where d.date_started between date_sub(date(:startDate), INTERVAL 12 MONTH) and date_sub(date(:endDate), INTERVAL 12 MONTH);";
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
                "   from kenyaemr_etl.etl_mch_enrollment e\n" +
                "     inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id and d.program = 'HIV'\n" +
                "     left join (select disc.patient_id, max(disc.visit_date) latest_visit from kenyaemr_etl.etl_patient_program_discontinuation disc where disc.program_name='HIV') disc on e.patient_id = disc.patient_id\n" +
                "   where (disc.patient_id is null or disc.latest_visit > date(:endDate)) and d.date_started between date_sub(date(:startDate), INTERVAL 12 MONTH) and date_sub(date(:endDate), INTERVAL 12 MONTH);\n" +
                "\n";

        cd.setName("netCohortAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Net Cohort at 12 months");

        return cd;
    }

    //Syphilis Screened at ANC	 HV02-24

    public CohortDefinition syphilisScreenedAtANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on v.patient_id = e.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and v.syphilis_test_status in (1229, 1228, 1304);";

        cd.setName("syphilisScreenedAt1stANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Screened at First ANC");

        return cd;
    }

    //Syphilis Screened Positive HV02-25
    public CohortDefinition syphilisScreenedPositive(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = e.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "    and v.syphilis_test_status = 1228;";

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
        String sqlQuery =  "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e\n" +
                "  inner join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id= e.patient_id\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "      and v.syphilis_treated_status =1065;";

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
        String sqlQuery =  "select c.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit c\n" +
                "         left join (select e.patient_id, max(e.visit_date) as latest_mch_enrollment\n" +
                "                    from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                    where e.visit_date <= date(:endDate)\n" +
                "                      and e.hiv_status = 703\n" +
                "                    group by e.patient_id) e on c.patient_id = e.patient_id\n" +
                "         left join (select h.patient_id, max(h.visit_date) as latest_hiv_enrollment\n" +
                "                    from kenyaemr_etl.etl_hiv_enrollment h\n" +
                "                    where h.visit_date <= date(:endDate)\n" +
                "                    group by h.patient_id) h\n" +
                "                   on c.patient_id = h.patient_id\n" +
                "where c.anc_visit_number = 1\n" +
                "  and date(c.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  and (date(c.visit_date) > date(e.latest_mch_enrollment)\n" +
                "  or date(c.visit_date) > h.latest_hiv_enrollment)\n" +
                "  and (e.patient_id is not null\n" +
                "    or h.patient_id is not null);";

        cd.setName("firstANCKPAdolescents");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("1st ANC KP Adolescents");

        return cd;
    }

    public CohortDefinition firstHIVTestAtANCOrDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select t.patient_id from kenyaemr_etl.etl_hts_test t where date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "and t.ever_tested_for_hiv = 'No' and t.hts_entry_point in (160538,160456);";
        cd.setName("firstHIVTestAtANCOrDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at ANC OR l&d for the first time");

        return cd;
    }
    public CohortDefinition firstHIVTestAtPNCWithin6Weeks(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join\n" +
                "     (select t.patient_id, t.visit_date\n" +
                "      from kenyaemr_etl.etl_hts_test t\n" +
                "               left join (select pn.patient_id,\n" +
                "                                 max(pn.visit_date)                                    as latest_pn_visit,\n" +
                "                                 mid(max(concat(pn.visit_date, pn.delivery_date)), 11) as delivery_date\n" +
                "                          from kenyaemr_etl.etl_mch_postnatal_visit pn\n" +
                "                          where date(pn.visit_date) <= date(:endDate)) pn on t.patient_id = pn.patient_id\n" +
                "      where date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "        and t.ever_tested_for_hiv = 'No'\n" +
                "        and t.hts_entry_point = 1623\n" +
                "        and timestampdiff(WEEK, pn.delivery_date, t.visit_date) <= 6) hts on e.patient_id = hts.patient_id\n" +
                "         left join (select p.patient_id\n" +
                "                    from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                    where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                      and p.final_test_result is not null\n" +
                "                      and p.final_test_result != ''\n" +
                "                      and timestampdiff(WEEK, date(p.delivery_date), date(p.visit_date)) <= 6) p\n" +
                "                   on e.patient_id = p.patient_id\n" +
                "where hts.patient_id is not null\n" +
                "   or p.patient_id is not null;";
        cd.setName("firstHIVTestAtPNCWithin6Weeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at PNC for the first time within 6 weeks");

        return cd;
    }
    public CohortDefinition adolescentsHIVPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("firstHIVTestAtANCOrDelivery",ReportUtils.map(firstHIVTestAtANCOrDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstHIVTestAtPNCWithin6Weeks",ReportUtils.map(firstHIVTestAtPNCWithin6Weeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("firstHIVTestAtANCOrDelivery AND firstHIVTestAtPNCWithin6Weeks");
        return cd;
    }
    //Started HAART adolescents_Total	HV02-35
    public CohortDefinition adolescentsStartedOnHAART(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join (select de.patient_id\n" +
                "                    from kenyaemr_etl.etl_drug_event de\n" +
                "                             inner join (select pn.patient_id,\n" +
                "                                                mid(max(concat(pn.visit_date, pn.visit_timing_mother)), 11) as mother_visit_timing,\n" +
                "                                                max(pn.visit_date)                                          as latest_pn_visit,\n" +
                "                                                mid(max(concat(pn.visit_date, pn.delivery_date)), 11)       as delivery_date\n" +
                "                                         from kenyaemr_etl.etl_mch_postnatal_visit pn\n" +
                "                                         where date(pn.visit_date) <= date(:endDate)) pn\n" +
                "                                        on de.patient_id = pn.patient_id\n" +
                "                    where date(de.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                      and de.program = 'HIV'\n" +
                "                      and (timestampdiff(WEEK, date(pn.delivery_date), date(de.date_started)) <= 6)) de\n" +
                "                   on e.patient_id = de.patient_id\n" +
                "         left join (select c.patient_id\n" +
                "                    from kenyaemr_etl.etl_mch_antenatal_visit c\n" +
                "                    where date(c.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                      and date(c.date_given_haart) between date(:startDate) and date(:endDate)) c\n" +
                "                   on e.patient_id = c.patient_id\n" +
                "         left join (select d.patient_id, d.visit_date\n" +
                "                    from kenyaemr_etl.etl_mchs_delivery d\n" +
                "                    where date(d.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                      and d.mother_started_haart_at_maternity = 1065) d\n" +
                "                   on e.patient_id = d.patient_id\n" +
                "         left join (select p.patient_id\n" +
                "                    from kenyaemr_etl.etl_mch_postnatal_visit p\n" +
                "                    where date(p.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                      and p.mother_haart_given = 1065\n" +
                "                      and (timestampdiff(WEEK, date(p.delivery_date), date(p.visit_date)) <= 6 or\n" +
                "                           p.visit_timing_mother in (1721, 1722))) p on e.patient_id = p.patient_id\n" +
                "where de.patient_id is not null\n" +
                "   or c.patient_id is not null\n" +
                "   or d.patient_id is not null\n" +
                "   or p.patient_id is not null;";
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

    //Total given Penta 1	HV02-37
    public CohortDefinition totalGivenPenta1(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct he.patient_id\n" +
                "                from kenyaemr_etl.etl_hei_enrollment he\n" +
                "                 inner join kenyaemr_etl.etl_hei_immunization hi on hi.patient_id=he.patient_id\n" +
                "                where date(hi.visit_date) between (:startDate) and (:endDate)\n" +
                "                  and hi.PCV_10_1 = \"Yes\" ;";

        cd.setName("totalGivenPenta1");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Infants given Penta 1");

        return cd;
    }

    //Infant ARV Prophylaxis ANC HV02-39
    //We want to pick the first anc given prophylaxis
    public CohortDefinition infantArvProphylaxisANC(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select en.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment en on en.patient_id = v.patient_id\n" +
                "where (v.baby_nvp_dispensed = 80586\n" +
                "   or v.baby_azt_dispensed = 160123)\n" +
                "    and date(v.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("infantArvProphylaxisANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis at ANC");

        return cd;
    }

    //We want to pick the first ld given prophylaxis
    public CohortDefinition infantArvProphylaxisLabourAndDeliverySql(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select distinct en.patient_id from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment en on en.patient_id = ld.patient_id\n" +
                "where ld.baby_nvp_dispensed = 1  or ld.baby_azt_dispensed = 1\n" +
                "group by ld.patient_id\n" +
                "having min(date(ld.visit_date)) between date(:startDate) and date(:endDate);";
        cd.setName("infantArvProphylaxisLabourAndDelivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infant ARV Prophylaxis during Labour and Delivery");

        return cd;
    }
    //Infant ARV Prophylaxis L&D HV02-40
    //Exludes those given at ANC
    public CohortDefinition infantArvProphylaxisLabourAndDelivery(){
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("infantArvProphylaxisLabourAndDeliverySql",ReportUtils.map(infantArvProphylaxisLabourAndDeliverySql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantArvProphylaxisANC",ReportUtils.map(infantArvProphylaxisANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("infantArvProphylaxisLabourAndDeliverySql AND NOT infantArvProphylaxisANC");
        return cd;
    }

    public CohortDefinition infantARVProphylaxisGivenWithin8WeeksSql(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select en.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment en\n" +
                "         inner join kenyaemr_etl.etl_mch_postnatal_visit p on en.patient_id = p.patient_id\n" +
                "where (p.baby_nvp_dispensed = 80586 or p.baby_azt_dispensed = 160123)\n" +
                "  and TIMESTAMPDIFF(WEEK, date(p.delivery_date), date(p.visit_date)) <=\n" +
                "      8 and date(p.visit_date) between date(:startDate) and date(:endDate);";

        cd.setName("totalARVProphylaxis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total ARV Prophylaxis");

        return cd;
    }
    //Infant ARV Prophylaxis <8weeks PNC	HV02-41. Exludes those given at L&D and ANC
    //We want to pick the first pnc less than 8 weeks given prophylaxis
    public CohortDefinition infantArvProphylaxisPNCLessThan8Weeks(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("infantARVProphylaxisGivenWithin8WeeksSql",ReportUtils.map(infantARVProphylaxisGivenWithin8WeeksSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantArvProphylaxisLabourAndDelivery",ReportUtils.map(infantArvProphylaxisLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("infantARVProphylaxisGivenWithin8WeeksSql AND NOT infantArvProphylaxisLabourAndDelivery");
        return cd;
    }
    /**
     * Total Infant ARV prophylaxis: At ANC+L&D+PNC upto 8 weeks
     * @return
     */
    public CohortDefinition totalInfantARVProphylaxis() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("infantArvProphylaxisANC",ReportUtils.map(infantArvProphylaxisANC(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantArvProphylaxisLabourAndDelivery",ReportUtils.map(infantArvProphylaxisLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantArvProphylaxisPNCLessThan8Weeks",ReportUtils.map(infantArvProphylaxisPNCLessThan8Weeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("infantArvProphylaxisANC OR infantArvProphylaxisLabourAndDelivery OR infantArvProphylaxisPNCLessThan8Weeks");
        return cd;
    }

    /**
     * 12 Month cohort
     * @return
     */
    public CohortDefinition twelveMonthCohort(){
    SqlCohortDefinition cd = new SqlCohortDefinition();
    String sqlQuery =  "select d.patient_id\n" +
            "from kenyaemr_etl.etl_patient_demographics d\n" +
            "where d.dob between date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
            "                             interval 12 MONTH)\n" +
            "          and date_sub(date(:endDate), interval 12 MONTH);";
    cd.setName("twelveMonthCohort");
    cd.setQuery(sqlQuery);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setDescription("Twelve month cohort");
    return cd;
}

    /**
     * 24 month cohort
     * @return
     */
    public CohortDefinition twentyFourMonthCohort(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_demographics d\n" +
                "where d.dob between date_sub(date(DATE_SUB(date(:endDate), INTERVAL DAYOFMONTH(date(:endDate)) - 1 DAY)),\n" +
                "                             interval 24 MONTH)\n" +
                "          and date_sub(date(:endDate), interval 24 MONTH);";
        cd.setName("twentyFourMonthCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("24 month cohort");
        return cd;
    }
    public CohortDefinition heiDDSCTXStartWithin2Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "SELECT en.patient_id\n" +
                "FROM kenyaemr_etl.etl_hei_enrollment en\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics pd on en.patient_id = pd.patient_id\n" +
                "         inner join kenyaemr_etl.etl_hei_follow_up_visit hf on hf.patient_id = en.patient_id\n" +
                "where TIMESTAMPDIFF(MONTH, pd.DOB, hf.visit_date) <= 2\n" +
                "  and hf.ctx_given = 105281;";
        cd.setName("heiDDSCTSStartLessThan2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI DDS/CTS Start <=2 Months");

        return cd;
    }
    //HEI CTX/DDS Start <2 months	HV02-42
    public CohortDefinition heiDDSCTXStartLessThan2Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twelveMonthCohort",ReportUtils.map(twelveMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiDDSCTXStartWithin2Months",ReportUtils.map(heiDDSCTXStartWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twelveMonthCohort AND heiDDSCTXStartWithin2Months");
        return cd;
    }

    //Initial PCR <8 weeks	HV02-43
    public CohortDefinition initialPCRLessThan8Weeks(){

        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select hv.patient_id \n" +
                "    from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "    where hv.dna_pcr_result is not null and \n" +
                "    (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(week,p.dob,date(:endDate))<= 8; ";
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
        String sqlQuery =  "select hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "         join kenyaemr_etl.etl_patient_demographics p on p.patient_id = hv.patient_id\n" +
                "where hv.dna_pcr_result is not null\n" +
                "  and (hv.visit_date between date(:startDate) and date(:endDate))\n" +
                "  and timestampdiff(WEEK, p.dob, date(:endDate)) > 8\n" +
                "  and timestampdiff(month, p.dob, date(:endDate)) <= 12; ";
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
        String sqlQuery = "select hv.patient_id\n" +
                "                  from kenyaemr_etl.etl_hei_follow_up_visit hv \n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=hv.patient_id \n" +
                "                  where hv.dna_pcr_result is not null and\n" +
                "                  (hv.visit_date between date(:startDate) and date(:endDate)) and timestampdiff(month,p.dob,date(:endDate)) <= 12;";
        cd.setName("totalInitialPCRTestLessThan12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total Initial PCR <12 Months");

        return cd;
    }

    public CohortDefinition totalInfectedHEI(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "where hv.dna_pcr_result = 703 and date(hv.visit_date) <= date(:endDate);";
        cd.setName("totalInfectedHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total infected HEIs");
        return cd;
    }
    //Infected 24 months	HV02-46
    public CohortDefinition totalInfected24Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("totalInfectedHEI",ReportUtils.map(totalInfectedHEI(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND totalInfectedHEI");
        return cd;
    }

    public CohortDefinition totalUninfectedHEIs(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "where dna_pcr_result = 664\n" +
                "  and dna_pcr_contextual_status = 162082 and date(hv.visit_date) <= date(:endDate);";
        cd.setName("totalUninfectedHEIs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total uninfected HEIs");
        return cd;
    }
    //Uninfected 24 months	HV02-47
    public CohortDefinition totalUninfectedIn24Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("totalUninfectedHEIs",ReportUtils.map(totalUninfectedHEIs(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND totalUninfectedHEIs");
        return cd;
    }

    //Unknown Outcome 24 mths HV02-48
    public CohortDefinition unknownOutcomesIn24Months(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("exitedHEI",ReportUtils.map(exitedHEI(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("unknownStatusHEI",ReportUtils.map(unknownStatusHEI(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND exitedHEI AND unknownStatusHEI");
        return cd;
    }

    //Net Cohort HEI 24 months	HV02-49
        public CohortDefinition netCohortHeiIn24Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiTransferredOut",ReportUtils.map(heiTransferredOut(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND NOT heiTransferredOut");
        return cd;
    }
    public CohortDefinition exitedHEI(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "where program_name = 'MCH Child HEI'\n" +
                "  and d.discontinuation_reason in (5240, 159492, 160034)\n" +
                "  and date(coalesce(d.effective_discontinuation_date, d.visit_date)) <= date(:endDate);";
        cd.setName("exitedHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exited HEI");

        return cd;
    }
    public CohortDefinition unknownStatusHEI(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "where date(hv.visit_date) <= date(:endDate)\n" +
                "group by hv.patient_id\n" +
                "having group_concat(hv.dna_pcr_result) is null;";
        cd.setName("unknownStatusHEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unknown status HEI");

        return cd;
    }
    public CohortDefinition heiTransferredOut(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "where program_name = 'MCH Child HEI'\n" +
                "  and d.discontinuation_reason = 159492\n" +
                "  and date(coalesce(d.effective_discontinuation_date, d.visit_date)) <= date(:endDate);";
        cd.setName("heiTransferredOut");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs transferred out");

        return cd;
    }
    /**
     * Hei followup at 24 months with mother as primary care giver
     * @return
     */
    public CohortDefinition motherBabyPairs(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select hv.patient_id\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "where hv.primary_caregiver = 970\n" +
                "  and hv.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("motherBabyPairsIn24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mother-baby pairs in 24 Months");
        return cd;
    }
    //Mother-baby pairs 24 months	HV02-50
    public CohortDefinition motherBabyPairsIn24Months(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("motherBabyPairs",ReportUtils.map(motherBabyPairs(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND motherBabyPairs");
        return cd;
    }

    public CohortDefinition heiWithLivingMotherAtEnrollment(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select he.patient_id\n" +
                "    from kenyaemr_etl.etl_hei_enrollment he\n" +
                "    where he.mother_alive = 1;";
        cd.setName("heiWithLivingMotherAtEnrollment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI with living mother at registration");
        return cd;
    }
    //Pair net cohort 24 months	 HV02-51
    public CohortDefinition pairNetCohortIn24Months(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiWithLivingMotherAtEnrollment",ReportUtils.map(heiWithLivingMotherAtEnrollment(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiTransferredOut",ReportUtils.map(heiTransferredOut(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(twentyFourMonthCohort AND heiWithLivingMotherAtEnrollment) AND NOT heiTransferredOut");
        return cd;
    }
    public CohortDefinition exclusiveBFAt6Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id from (Select e.patient_id, f.infant_feeding, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "  and timestampdiff(month, d.dob, date(f.visit_date)) <= 6\n" +
                "group by e.patient_id\n" +
                "having find_in_set(6046, feeding) = 0\n" +
                "   and find_in_set(1595, feeding) = 0\n" +
                "   and find_in_set(5632, feeding) = 0\n" +
                "   and find_in_set(164478, feeding) = 0\n" +
                "   and find_in_set(5526, feeding) = 1)f;";
        cd.setName("exclusiveBFAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Breastfeeding at 6 months");
        return cd;
    }
    //EBF (at 6 months)	HV02-52
    public CohortDefinition exclusiveBFAt6Months12MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twelveMonthCohort",ReportUtils.map(twelveMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("exclusiveBFAt6Months",ReportUtils.map(exclusiveBFAt6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twelveMonthCohort AND exclusiveBFAt6Months");
        return cd;
    }

    //ERF (at 6 months)	HV02-53
    public CohortDefinition exclusiveRFAt6Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id from (Select e.patient_id, f.infant_feeding, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "where date(e.visit_date) <= date(:endDate)\n" +
                "  and timestampdiff(month, d.dob, date(f.visit_date)) <= 6\n" +
                "group by e.patient_id\n" +
                "having find_in_set(6046, feeding) = 0\n" +
                "   and find_in_set(1595, feeding) = 1\n" +
                "   and find_in_set(5632, feeding) = 0\n" +
                "   and find_in_set(164478, feeding) = 0\n" +
                "   and find_in_set(5526, feeding) = 0)f;";
        cd.setName("exclusiveRFAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Exclusive Replacement feeding at 6 months");
        return cd;
    }
    public CohortDefinition exclusiveRFAt6Months12MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twelveMonthCohort",ReportUtils.map(twelveMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("exclusiveRFAt6Months",ReportUtils.map(exclusiveRFAt6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twelveMonthCohort AND exclusiveRFAt6Months");
        return cd;
    }
    public CohortDefinition mixedFeedingAt6Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select f.patient_id\n" +
                "from (Select e.patient_id, f.infant_feeding, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "      from kenyaemr_etl.etl_hei_enrollment e\n" +
                "               inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "               inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "      where date(e.visit_date) <= date(:endDate)\n" +
                "        and timestampdiff(month, d.dob, date(f.visit_date)) <= 6\n" +
                "      group by e.patient_id\n" +
                "      having find_in_set(6046, feeding) = 1\n" +
                ") f;";
        cd.setName("mixedFeedingAt6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mixed feeding at 6 months");
        return cd;
    }
    //MF (at 6 months)	HV02-54
    public CohortDefinition mixedFeedingAt6Months12MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twelveMonthCohort",ReportUtils.map(twelveMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("mixedFeedingAt6Months",ReportUtils.map(mixedFeedingAt6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twelveMonthCohort AND mixedFeedingAt6Months");
        return cd;
    }
    public CohortDefinition breastFeedingAt12Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select f.patient_id\n" +
                "from (Select e.patient_id, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "      from kenyaemr_etl.etl_hei_enrollment e\n" +
                "               inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "               inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "      where date(e.visit_date) <= date(:endDate)\n" +
                "        and timestampdiff(month, d.dob, date(f.visit_date)) <= 12\n" +
                "      group by e.patient_id\n" +
                "      having (find_in_set(6046, feeding) = 1\n" +
                "          or find_in_set(5632, feeding) = 1\n" +
                "          or find_in_set(5526, feeding) = 1)\n" +
                "         and find_in_set(1595, feeding) = 0\n" +
                "         and find_in_set(164478, feeding) = 0) f;";
        cd.setName("breastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breast feeding at 12 months");

        return cd;
    }
    //BF (12 months)	HV02-55
    public CohortDefinition breastFeedingAt12Months12MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twelveMonthCohort",ReportUtils.map(twelveMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("breastFeedingAt12Months",ReportUtils.map(breastFeedingAt12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twelveMonthCohort AND breastFeedingAt12Months");
        return cd;
    }

    //Not BF (12 months)	HV02-56
    public CohortDefinition notBreastFeedingAt12Months12MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twelveMonthCohort",ReportUtils.map(twelveMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("notBreastFeedingAt12Months",ReportUtils.map(notBreastFeedingAt12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twelveMonthCohort AND notBreastFeedingAt12Months");
        return cd;
    }
    public CohortDefinition notBreastFeedingAt12Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select f.patient_id\n" +
                "from (Select e.patient_id, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "      from kenyaemr_etl.etl_hei_enrollment e\n" +
                "               inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "               inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "      where date(e.visit_date) <= date(:endDate)\n" +
                "        and timestampdiff(month, d.dob, date(f.visit_date)) <= 12\n" +
                "      group by e.patient_id\n" +
                "      having (find_in_set(1595, feeding) = 1\n" +
                "          or find_in_set(164478, feeding) = 1)\n" +
                "          and find_in_set(6046, feeding) = 0\n" +
                "          and find_in_set(5632, feeding) = 0\n" +
                "          and find_in_set(5526, feeding) = 0) f;";
        cd.setName("notBreastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not Breast feeding at 12 months");

        return cd;
    }

    public CohortDefinition breastFeedingAt18Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select f.patient_id\n" +
                "from (Select e.patient_id, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "      from kenyaemr_etl.etl_hei_enrollment e\n" +
                "               inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "               inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "      where date(e.visit_date) <= date(:endDate)\n" +
                "        and timestampdiff(month, d.dob, date(f.visit_date)) <= 18\n" +
                "      group by e.patient_id\n" +
                "      having (find_in_set(6046, feeding) = 1\n" +
                "          or find_in_set(5632, feeding) = 1\n" +
                "          or find_in_set(5526, feeding) = 1)\n" +
                "         and find_in_set(1595, feeding) = 0\n" +
                "         and find_in_set(164478, feeding) = 0) f;";
        cd.setName("breastFeedingAt18Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breast feeding at 18 months");

        return cd;
    }
    //BF (18 months)	HV02-57
    public CohortDefinition breastFeedingAt18Months24MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("breastFeedingAt18Months",ReportUtils.map(breastFeedingAt18Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND breastFeedingAt18Months");
        return cd;
    }
    public CohortDefinition notBreastFeedingAt18Months(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  "select f.patient_id\n" +
                "from (Select e.patient_id, group_concat(f.infant_feeding) as feeding, d.dob\n" +
                "      from kenyaemr_etl.etl_hei_enrollment e\n" +
                "               inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "               inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
                "      where date(e.visit_date) <= date(:endDate)\n" +
                "        and timestampdiff(month, d.dob, date(f.visit_date)) <= 18\n" +
                "      group by e.patient_id\n" +
                "      having (find_in_set(1595, feeding) = 1\n" +
                "          or find_in_set(164478, feeding) = 1)\n" +
                "          and find_in_set(6046, feeding) = 0\n" +
                "          and find_in_set(5632, feeding) = 0\n" +
                "          and find_in_set(5526, feeding) = 0) f;";
        cd.setName("notBreastFeedingAt12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Not Breast feeding at 12 months");

        return cd;
    }

    //Not BF (18 months)	HV02-58
    public CohortDefinition notBreastFeedingAt18Months24MonthCohort(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("twentyFourMonthCohort",ReportUtils.map(twentyFourMonthCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("notBreastFeedingAt18Months",ReportUtils.map(notBreastFeedingAt18Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("twentyFourMonthCohort AND notBreastFeedingAt18Months");
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
     *Number of males circumcised with severe Adverse Events post procedure
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
