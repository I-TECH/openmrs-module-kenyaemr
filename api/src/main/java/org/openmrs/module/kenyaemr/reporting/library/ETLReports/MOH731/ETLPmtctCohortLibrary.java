package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 1/17/17.
 */
@Component
public class ETLPmtctCohortLibrary {
    //Updated:
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
                "      e.hiv_status !=703 and\n" +
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
                "      e.hiv_status !=703 and\n" +
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
                "      e.hiv_status !=703 and\n" +
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
        String sqlQuery =  "select distinct ld.patient_id\\n\" +\n" +
                "                \"from kenyaemr_etl.etl_mchs_delivery ld\\n\" +\n" +
                "                \"  inner join kenyaemr_etl.etl_drug_event d on d.patient_id=ld.patient_id\\n\" +\n" +
                "                \"where date(ld.visit_date) between date(:startDate) and date(:endDate)\\n\" +\n" +
                "                \"      and d.date_started >= ld.visit_date;";


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
