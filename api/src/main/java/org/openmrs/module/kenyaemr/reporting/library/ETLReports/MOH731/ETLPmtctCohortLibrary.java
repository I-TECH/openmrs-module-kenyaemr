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

    public CohortDefinition testedForHivInMchmsTotal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (e.hiv_test_date between :startDate and :endDate) ;";

        cd.setName("testedForHivInMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers tested For Hiv In Mch Program");

        return cd;
    }

    public CohortDefinition testedHivPositiveInMchmsTotal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery =  " select distinct patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                "    where (hiv_status=703 and e.hiv_test_date between :startDate and :endDate) ;";

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
                " where  timestampdiff(month,d.dob,:endDate)>=6 and e.infant_feeding=5526 and \n" +
                "  date(e.visit_date) between :startDate and :endDate ;" ;

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
                " where  timestampdiff(month,d.dob,:endDate)>=6 and e.infant_feeding=1595 and \n" +
                "  date(e.visit_date) between :startDate and :endDate ;" ;
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
                " where  timestampdiff(month,d.dob,:endDate)>=6 and e.infant_feeding=6046 and \n" +
                "  date(e.visit_date) between :startDate and :endDate ;" ;
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
                " where timestampdiff(month,d.dob,:endDate)>=6 and e.infant_feeding in (1595,6046,5526)  and \n " +
                "  date(e.visit_date) between :startDate and :endDate ;" ;
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
                "  date(e.visit_date) between :startDate and :endDate ;" ;

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
                "  date(e.visit_date) between :startDate and :endDate ;" ;

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
                "  date(e.visit_date) between :startDate and :endDate ;" ; //this concept unknown 1067 is missing on the hei enrollment form and should be corrected

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
                "  date(e.visit_date) between :startDate and :endDate ;" ; //this concept unknown 1067 is missing on the hei enrollment form and should be corrected

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
                "where date(hiv_test_date) between :startDate and :endDate and \n" +
                "(anc.patient_id is not null);";

        cd.setName("Tested For Hiv Antenatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested For Hiv Antenatal");

        return cd;
    }

    public CohortDefinition testedForHivInMchmsDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct ld.patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between :startDate and :endDate and \n" +
                "(ld.patient_id is not null and anc.patient_id is null);";

        cd.setName("Tested For Hiv Delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested For Hiv Delivery");

        return cd;
    }

    public CohortDefinition testedForHivInMchmsPostnatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct panc.patient_id " +
                " from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between :startDate and :endDate and \n" +
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
                "    where (e.visit_date between :startDate and :endDate) " +
                "    and hiv_status=703 and e.visit_date>hiv_test_date;";

        cd.setName("testedHivPositiveBeforeMchms");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("tested Hiv Positive Before enrollment in Mchms");

        return cd;
    }

    public CohortDefinition testedHivPositiveInMchmsAntenatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct anc.patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between :startDate and :endDate and \n" +
                "(anc.patient_id is not null) and hiv_status=703;";

        cd.setName("Tested Hiv Postive at Antenatal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Hiv Postive at Antenatal");

        return cd;
    }

    public CohortDefinition testedHivPositiveInMchmsDelivery(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct ld.patient_id " +
                "    from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between :startDate and :endDate and \n" +
                "(ld.patient_id is not null and anc.patient_id is null) and hiv_status=703;";

        cd.setName("Tested Hiv Hive at Delivery");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Hiv Positive at Delivery");

        return cd;
    }

    public CohortDefinition testedHivPositiveInMchmsPostnatal(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery ="  select distinct panc.patient_id " +
                " from kenyaemr_etl.etl_mch_enrollment e " +
                " left outer join kenyaemr_etl.etl_mch_antenatal_visit anc on anc.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_mch_postnatal_visit panc on panc.patient_id=e.patient_id\n" +
                "where date(hiv_test_date) between :startDate and :endDate and \n" +
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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate) between 9 and 12; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate)<=12; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate)<=2; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate) between 3 and 8; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate) between 9 and 12; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate)<=2; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate) between 3 and 8; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate) between 9 and 12; ";

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
                "    (e.visit_date between :startDate and :endDate) and timestampdiff(month,p.dob,:endDate)<=12; ";

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
                "    where date(e.visit_date) between :startDate and :endDate " +
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
                "    where date(e.visit_date) between :startDate and :endDate " +
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
                "    where date(e.visit_date) between :startDate and :endDate " +
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
                "    where (e.visit_date between :startDate and :endDate) and ((hiv_status=703 and partner_hiv_status=664) or (hiv_status=664 and partner_hiv_status=703));";

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
                "    where (partner_hiv_status is not null and e.visit_date between :startDate and :endDate);";

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
                "    where (e.visit_date between :startDate and :endDate) " +
                "    and hiv_status=703 and visit_date>hiv_test_date;";

        cd.setName("hivTestingKnownPositiveTotal");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hiv Testing Known Positive Total");

        return cd;
    }
}
