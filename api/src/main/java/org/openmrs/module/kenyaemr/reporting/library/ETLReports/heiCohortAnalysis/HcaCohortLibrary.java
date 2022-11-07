/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.heiCohortAnalysis;

        import org.openmrs.module.kenyacore.report.ReportUtils;
        import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
        import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
        import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
        import org.openmrs.module.reporting.evaluation.parameter.Parameter;
        import org.springframework.stereotype.Component;

        import java.util.Date;

@Component
public class HcaCohortLibrary {

    /**
     * FIRST REVIEW: 12 Months Cohort composition
     */

    /**
     * Number in HEI Cohort 12 months
     * @return the indicator
     */
    public CohortDefinition heiCohort12Months() {
        String sqlQuery = "SELECT he.patient_id FROM kenyaemr_etl.etl_hei_enrollment he\n" +
                "INNER JOIN kenyaemr_etl.etl_patient_demographics pd ON he.patient_id = pd.patient_id\n" +
                "WHERE DATE(pd.DOB) between date_sub(date(:startDate),interval 1 YEAR) and  date_sub(date(:endDate),interval 1 YEAR);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("heiCohort12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI Cohort 12 Months");
        return cd;
    }
    /**
     * SECOND REVIEW: 24 Months Cohort composition
     */

    /**
     * Number in HEI Cohort 24 months
     * @return the indicator
     */
    public CohortDefinition heiCohort24Months() {
        String sqlQuery = "SELECT he.patient_id FROM kenyaemr_etl.etl_hei_enrollment he\n" +
                "INNER JOIN kenyaemr_etl.etl_patient_demographics pd ON he.patient_id = pd.patient_id\n" +
                "WHERE DATE(pd.DOB) between date_sub(date(:startDate),interval 2 YEAR) and  date_sub(date(:endDate),interval 2 YEAR);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("heiCohort24Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI Cohort 24 Months");
        return cd;
    }

    /*HEI with mothers who received PMTCT ARV
    *Composition for 12 Months cohorts
     */

    public CohortDefinition mothersReceivedPMTCTARV() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "SELECT patient_id FROM kenyaemr_etl.etl_hei_enrollment where mother_on_art_at_infant_enrollment IS NOT NULL;";
        cd.setName("mothersReceivedPMTCTARVs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEIs with mothers who received PMTCT ARVs");

        return cd;
    }

    /*HEI with mothers who received PMTCT ARV 12 Months Cohort
     *Composition  heiCohort12Months + mothersReceivedPMTCTARV
     * @return the indicator
     */
    public CohortDefinition mothersReceivedARV12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("mothersReceivedPMTCTARV",ReportUtils.map(mothersReceivedPMTCTARV(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND mothersReceivedPMTCTARV");
        return cd;
    }

    /*HEI who received ARVs at 0-6 weeks
    *Composition for 12 Months cohorts
     */
    public CohortDefinition infantReceivedARVsWithinSixWeeks() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "SELECT he.patient_id FROM kenyaemr_etl.etl_hei_enrollment he\n" +
                "INNER JOIN kenyaemr_etl.etl_patient_demographics pd ON he.patient_id = pd.patient_id\n" +
                "WHERE he.infant_prophylaxis in (80586,1652,162326,160123,78643,1652,1107)\n" +
                "AND TIMESTAMPDIFF(WEEK,pd.DOB,he.visit_date) BETWEEN 0 AND 6;";

        cd.setName("nfantReceivedARVsSixWeeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants who received ARVs at 0-6 weeks");

        return cd;
    }

    /*HEI who received ARVs at 0-6 weeks 12 Months Cohort
     *Composition  heiCohort12Months + infantReceivedARVsWithinSixWeeks
     * @return the indicator
     */
    public CohortDefinition infantReceivedARVsWithinSixWeeks12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantReceivedARVsWithinSixWeeks",ReportUtils.map(infantReceivedARVsWithinSixWeeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND infantReceivedARVsWithinSixWeeks");
        return cd;
    }
    /*HEI tested with PCR at age 6-8 weeks and results available
       *Composition for 12 Months cohorts
        */
    public CohortDefinition heiPCRTestSixWeeks() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                " inner join kenyaemr_etl.etl_patient_demographics pd ON hf.patient_id = pd.patient_id\n" +
                " where  hf.dna_pcr_result is not null and TIMESTAMPDIFF(WEEK,pd.DOB,hf.dna_pcr_sample_date) BETWEEN 0 AND 8\n" +
                " group by hf.patient_id\n";

        cd.setName("heiPCRTestSixWeeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI tested with PCR at age 6-8 weeks and results available");

        return cd;
    }
    /*HEI tested with PCR at age 6-8 weeks and results available 12 Months Cohort
    *Composition  heiCohort12Months + heiPCRTestSixWeeks
    * @return the indicator
    */
    public CohortDefinition heiPCRTestedSixWeeks12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiPCRTestSixWeeks",ReportUtils.map(heiPCRTestSixWeeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiPCRTestSixWeeks");
        return cd;
    }
    /*HEI tested POSITIVE with PCR at age 6-8 weeks and results available
       *Composition for 12 Months cohorts
        */
    public CohortDefinition heiPCRTestedPositiveSixWeeks() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "inner join kenyaemr_etl.etl_patient_demographics pd ON hf.patient_id = pd.patient_id\n" +
                "where hf.dna_pcr_result = 703 and TIMESTAMPDIFF(WEEK,pd.DOB,hf.dna_pcr_sample_date) BETWEEN 0 AND 8\n" +
                "group by hf.patient_id;\n;";

        cd.setName("heiPositiveSixWeeks");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI tested positive by first PCR at age 6-8 weeks");

        return cd;
    }

    /*HEI tested PCR Positive  at age 6-8 weeks  12 Months Cohort
    *Composition  heiCohort12Months + heiPCRTestedPositiveSixWeeks
    * @return the indicator
    */
    public CohortDefinition heiPCRTestedPositiveSixWeeks12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiPCRTestedPositiveSixWeeks",ReportUtils.map(heiPCRTestedPositiveSixWeeks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiPCRTestedPositiveSixWeeks");
        return cd;
    }
    /*HEI tested with PCR Initial within 12 months
       *Composition for 12 Months cohorts
        */
    public CohortDefinition heiInitialPCRTestWithinTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                " where TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) between 0 and  12\n" +
                "and hf.dna_pcr_result is not null and dna_pcr_contextual_status =162080\n" +
                "group by hf.patient_id;";

        cd.setName("heiInitialPCRTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI tested with Initial PCR and results available between 0 and 12 months");

        return cd;
    }

    /*HEI with Initial PCR test within 12 months 12 Months Cohort
   *Composition  heiCohort12Months + heiInitialPCRTestWithinTwelveMonths
   * @return the indicator
   */
    public CohortDefinition heiInitialPCRTestWithinTwelveMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiInitialPCRTestWithinTwelveMonths",ReportUtils.map(heiInitialPCRTestWithinTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiInitialPCRTestWithinTwelveMonths");
        return cd;
    }
    /*Eligible HEI with repeat PCR done at 6 months and results available
         *Composition for 12 Months cohorts
          */
    public CohortDefinition heiEligibleRepeatPCRSixMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd ON hf.patient_id = pd.patient_id\n" +
                "where TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) = 6\n" +
                "      and hf.dna_pcr_result is not null  and dna_pcr_contextual_status =162081\n" +
                "group by hf.patient_id;\n";

        cd.setName("heiEligibleRepeatPCRSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Eligible HEI  with repeat PCR done at 6 months and results available");

        return cd;
    }


    /*Eligible HEI with repeat PCR done at 6 months and results available 12 Months Cohort
   *Composition  heiCohort12Months + heiEligibleRepeatPCRSixMonths
   * @return the indicator
   */
    public CohortDefinition heiEligibleRepeatPCRSixMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiEligibleRepeatPCRSixMonths",ReportUtils.map(heiEligibleRepeatPCRSixMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiEligibleRepeatPCRSixMonths");
        return cd;
    }
    /*HEI tested positive by PCR between 0 and 12 months both Initial or repeat
          *Composition for 12 Months cohorts
           */
    public CohortDefinition heiPCRTestedPositivePCR12Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "where TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) between 0 and 12\n" +
                "         and dna_pcr_contextual_status in (162080,162081) and hf.dna_pcr_result = 703\n" +
                "group by hf.patient_id;";
        cd.setName("heiPositivePCR12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("7. HEI tested positive by PCR between 0 and 12 months");

        return cd;
    }
    /*HEI tested positive by PCR between 0 and 12 months both Initial or repeat 12 Months Cohort
      *Composition  heiCohort12Months + heiPCRTestedPositivePCR12Months
      * @return the indicator
      */
    public CohortDefinition heiPCRTestedPositivePCR12Months12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiPCRTestedPositivePCR12Months",ReportUtils.map(heiPCRTestedPositivePCR12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiPCRTestedPositivePCR12Months");
        return cd;
    }
           /*HEI who were Exclusively Breastfed within 6 months among HEI assessed
             *Composition for 12 Months cohorts
              */
    public CohortDefinition heiExclusiveBFWithinSixMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd ON hf.patient_id = pd.patient_id\n" +
                "where TIMESTAMPDIFF(MONTH,pd.DOB,hf.visit_date) <= 6\n" +
                "group by hf.patient_id\n" +
                "having mid(max(concat(hf.visit_date,hf.infant_feeding)),11)=5526;";

        cd.setName("heiExclusiveBFSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI who were Exclusively Breastfed at 6 months among HEI assessed");

        return cd;
    }
        /*HEI who were Exclusively Breastfed within 6 months 12 Months Cohort
          *Composition  heiCohort12Months + heiExclusiveBFWithinSixMonths
          * @return the indicator
          */
    public CohortDefinition heiExclusiveBFWithinSixMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiExclusiveBFWithinSixMonths",ReportUtils.map(heiExclusiveBFWithinSixMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiExclusiveBFWithinSixMonths");
        return cd;
    }
         /*HEI PCR Tested between 0 and 12 months linked to CCC
            *Composition for 12 Months cohorts
             */
    public CohortDefinition heiPCRTestedPositiveAt12MonthsLinkedToCCC() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select  hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "  inner join kenyaemr_etl.etl_hiv_enrollment e on (hf.patient_id = e.patient_id)\n" +
                "where TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) <= 12\n" +
                "      and hf.dna_pcr_result = 703 and pd.unique_patient_no is not null\n" +
                "group by hf.patient_id;";
        cd.setName("infantLinkedToCCCTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV positive infants identified between 0 and 12 months linked to CCC");

        return cd;
    }
    /*HEI PCR Tested between 0 and 12 months linked to CCC 12 Months Cohort
            *Composition  heiCohort12Months + heiPCRTestedPositiveAt12MonthsLinkedToCCC
            * @return the indicator
            */
    public CohortDefinition heiPCRTestedPositiveAt12MonthsLinkedToCCC12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiPCRTestedPositiveAt12MonthsLinkedToCCC",ReportUtils.map(heiPCRTestedPositiveAt12MonthsLinkedToCCC(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiPCRTestedPositiveAt12MonthsLinkedToCCC");
        return cd;
    }

    /*HIV Positive infants with baseline VL done and results available between 0 and 12 months
          *Composition for 12 Months cohorts
           */
    public CohortDefinition infantBaselineDocumented12MonthsVLResults() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select enr.patient_id from kenyaemr_etl.etl_hei_enrollment enr\n" +
                "   inner join kenyaemr_etl.etl_patient_demographics pd on enr.patient_id = pd.patient_id\n" +
                "   inner join(select x.patient_id, max(x.visit_date) as latest_vl_date, mid(max(concat(x.visit_date,if(x.lab_test = 856, x.test_result, if(x.lab_test=1305 and x.test_result = 1302, 'LDL','')))),11) as latest_vl_result\n" +
                "             from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305, 856) group by x.patient_id) vl on enr.patient_id= vl.patient_id\n" +
                "      where (vl.latest_vl_result is not null or vl.latest_vl_result !='')\n" +
                "               and TIMESTAMPDIFF(MONTH,pd.DOB,vl.latest_vl_date) between 0 and 12\n" +
                "      group by enr.patient_id;";
        cd.setName("infantBaselineVLwithResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive infants with baseline VL done and results available");

        return cd;
    }

    /*HIV Positive infants with baseline VL done and results available between 0 and 12 months : 12 Months Cohort
            *Composition  heiCohort12Months + infantBaselineDocumentedVLResults
            * @return the indicator
            */
    public CohortDefinition heiWithBaselineDocumented12MonthsVLResults12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantBaselineDocumented12MonthsVLResults",ReportUtils.map(infantBaselineDocumented12MonthsVLResults(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND infantBaselineDocumented12MonthsVLResults");
        return cd;
    }

        /*Active in follow-up 12 months
          *Composition for 12 Months cohorts
           */
    public CohortDefinition heiActiveFollowupTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t1.patient_id from kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "    inner join kenyaemr_etl.etl_patient_demographics pd on (t1.patient_id = pd.patient_id)\n" +
                "    left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and\n" +
                "    (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 12)))\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation hd on (t1.patient_id = hd.patient_id and (hd.discontinuation_reason = 160034 and\n" +
                "    (hd.date_died is null or TIMESTAMPDIFF(MONTH,pd.DOB,hd.date_died) > 12)))\n" +
                "    where DATE_ADD(pd.DOB, INTERVAL 12 MONTH) <= t1.next_appointment_date\n" +
                "    and ho.patient_id is null and hd.patient_id is null\n" +
                "    and (t1.dna_pcr_result is null or t1.dna_pcr_result != 703) GROUP BY t1.patient_id;";

        cd.setName("activeFollowupTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Active in follow-up");

        return cd;
    }
        /*Active in follow-up 12 months: 12 Months Cohort
            *Composition  heiCohort12Months + heiActiveFollowupTwelveMonths
            * @return the indicator
            */
    public CohortDefinition heiActiveFollowupAtTwelveMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiActiveFollowupTwelveMonths",ReportUtils.map(heiActiveFollowupTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiActiveFollowupTwelveMonths");
        return cd;
    }
    /*Identified as positive between 0 and 12 months
           *Composition for 12 Months cohorts
            */
    public CohortDefinition heiTestedPositiveWithinTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "where hf.dna_pcr_result = 703 and TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) <= 12\n" +
                "group by hf.patient_id;";

        cd.setName("positiveTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Identified as positive between 0 and 12 months");

        return cd;
    }
    /*Identified as positive between 0 and 12 months: 12 Months Cohort
               *Composition  heiCohort12Months + heiTestedPositiveWithinTwelveMonths
               * @return the indicator
               */
    public CohortDefinition heiTestedPositiveWithinTwelveMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiTestedPositiveWithinTwelveMonths",ReportUtils.map(heiTestedPositiveWithinTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiTestedPositiveWithinTwelveMonths");
        return cd;
    }

    /*Transferred out between 0 and 12 months
           *Composition for 12 Months cohorts
            */
    public CohortDefinition heiTransferredOutWithinTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "        inner join kenyaemr_etl.etl_patient_program_discontinuation ho on (hf.patient_id = ho.patient_id and ho.discontinuation_reason = 159492)\n" +
                "  where TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) <= 12\n" +
                "    group by hf.patient_id;\n";

        cd.setName("transferredOutTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transferred out between 0 and 12 mon");

        return cd;
    }

    /*Transferred out between 0 and 12 months: 12 Months Cohort
              *Composition  heiCohort12Months + heiTransferredOutWithinTwelveMonths
              * @return the indicator
              */
    public CohortDefinition heiTransferredOutWithinTwelveMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiTransferredOutWithinTwelveMonths",ReportUtils.map(heiTransferredOutWithinTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiTransferredOutWithinTwelveMonths");
        return cd;
    }
            /*Missing 12 months visit
              *Composition for 12 Months cohorts
                */
    public CohortDefinition heiMissingVisitTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "SELECT t1.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "           INNER JOIN kenyaemr_etl.etl_patient_demographics pd ON (t1.patient_id = pd.patient_id)\n" +
                "           LEFT JOIN (SELECT MAX(next_appointment_date) AS latest_tca, t4.patient_id FROM\n" +
                "           kenyaemr_etl.etl_hei_follow_up_visit t4\n" +
                "           left join kenyaemr_etl.etl_patient_demographics t5 using(patient_id)\n" +
                "           where DATE_ADD(t5.DOB, INTERVAL 12 MONTH) <= t4.next_appointment_date\n" +
                "       GROUP BY t4.patient_id) latest_tca ON t1.patient_id = latest_tca.patient_id\n" +
                "       left join kenyaemr_etl.etl_hei_follow_up_visit t2 on (latest_tca.patient_id = t2.patient_id and latest_tca.latest_tca = t2.next_appointment_date)\n" +
                "       left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and \n" +
                "       (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 12)))\n" +
                "       left join kenyaemr_etl.etl_patient_program_discontinuation hd on (t1.patient_id = hd.patient_id and (hd.discontinuation_reason = 160034 and \n" +
                "       (hd.date_died is null or TIMESTAMPDIFF(MONTH,pd.DOB,hd.date_died) > 12)))\n" +
                "   WHERE latest_tca.patient_id is null and ho.patient_id is null and hd.patient_id is null\n" +
                "   and (t2.dna_pcr_result is null or t2.dna_pcr_result != 703) GROUP BY t1.patient_id;";

        cd.setName("missingVisit12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Missing 12 month follow-up visit");

        return cd;
    }

          /*Missing 12 months visit: 12 Months Cohort
             *Composition  heiCohort12Months + heiMissingVisitTwelveMonths
             * @return the indicator
             */
    public CohortDefinition heiMissingVisitTwelveMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiMissingVisitTwelveMonths",ReportUtils.map(heiMissingVisitTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiMissingVisitTwelveMonths");
        return cd;
    }
               /*Died between 0 and 12 months
                 *Composition for 12 Months cohorts
                   */
    public CohortDefinition heiDiedWithinTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "        inner join kenyaemr_etl.etl_patient_program_discontinuation ho on hf.patient_id = ho.patient_id and ho.discontinuation_reason = 160034\n" +
                "   where TIMESTAMPDIFF(MONTH,pd.DOB,ho.date_died) <= 12";

        cd.setName("diedTwelveonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Died between 0 and 12 months");

        return cd;
    }

    /*Died between 0 and 12 months: 12 Months Cohort
         *Composition  heiCohort12Months + heiDiedWithinTwelveMonths
         * @return the indicator
         */
    public CohortDefinition heiDiedWithinTwelveMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiDiedWithinTwelveMonths",ReportUtils.map(heiDiedWithinTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiDiedWithinTwelveMonths");
        return cd;
    }
                /*HEI tested with Third DNA PCR Test at 12 months and results available
                     *Composition for 24 Months cohorts
                       */
    public CohortDefinition heiEligibleRepeatPCRTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "    where TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) = 12\n" +
                "    and hf.dna_pcr_result is not null\n" +
                "    group by hf.patient_id;";

        cd.setName("heiEligibleRepeatPCRTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI eligible with repeat PCR done at 12 months");

        return cd;
    }

    /*HEI tested with Third DNA PCR Test at 12 months and results available: 24 Months Cohort
         *Composition  heiCohort24Months + heiDiedWithinTwelveMonths
         * @return the indicator
         */
    public CohortDefinition heiEligibleRepeatPCRTwelveMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiEligibleRepeatPCRTwelveMonths",ReportUtils.map(heiEligibleRepeatPCRTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiEligibleRepeatPCRTwelveMonths");
        return cd;
    }
         /*HEI tested positive by PCR at 12
             *Composition for 24 Months cohorts
             * @return the indicator
             */
    public CohortDefinition heiTestedPositiveTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics pd ON hf.patient_id = pd.patient_id\n" +
                "    where dna_pcr_contextual_status in (162081)\n" +
                "      and dna_pcr_result = 703 and TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) = 12\n" +
                "      group by hf.patient_id;";

        cd.setName("heiTestedPositiveTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI tested positive by PCR at 12");

        return cd;
    }

    /*HEI repeat tested positive by PCR at 12: 24 Months Cohort
           *Composition  heiCohort24Months + heiTestedPositiveTwelveMonths
           * @return the indicator
           */
    public CohortDefinition heiTestedPositiveTwelveMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiTestedPositiveTwelveMonths",ReportUtils.map(heiTestedPositiveTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiTestedPositiveTwelveMonths");
        return cd;
    }
     /*HEI tested positive by confirmatory PCR between 12 and 18 months
             *Composition for 24 Months cohorts
             * @return the indicator
             */
    public CohortDefinition heiPositiveConfirmatoryPCRTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                " where dna_pcr_contextual_status in (162082)\n" +
                "     and dna_pcr_result = 703 AND TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) between 12 and 18\n" +
                "    group by hf.patient_id;";

        cd.setName("heiPositiveConfirmatoryPCRTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI tested positive by confirmatory PCR between 12 and 18 months");

        return cd;
    }
    /*HEI tested positive by confirmatory PCR between 12 and 18 months: 24 Months Cohort
               *Composition  heiCohort24Months + heiPositiveConfirmatoryPCRTwelveMonths
               * @return the indicator
               */
    public CohortDefinition heiPositiveConfirmatoryPCRTwelveMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiPositiveConfirmatoryPCRTwelveMonths",ReportUtils.map(heiPositiveConfirmatoryPCRTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiPositiveConfirmatoryPCRTwelveMonths");
        return cd;
    }
    /*HEI tested by AB test at >= 18 months and results are available
             *Composition for 24 Months cohorts
             * @return the indicator
             */
    public CohortDefinition heiEligibleABEigteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "  where final_antibody_result is not null and TIMESTAMPDIFF(MONTH,pd.DOB,hf.final_antibody_sample_date) >= 18\n" +
                "  group by hf.patient_id;";

        cd.setName("heiEligibleABEigteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI eligible tested by AB test at >= 18 months and results are available");

        return cd;
    }
    /*HEI tested by AB test at >= 18 months and results are available: 24 Months Cohort
                   *Composition  heiCohort24Months + heiEligibleABEigteenMonths
                   * @return the indicator
                   */
    public CohortDefinition heiEligibleABEigteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiEligibleABEigteenMonths",ReportUtils.map(heiEligibleABEigteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiEligibleABEigteenMonths");
        return cd;
    }
        /*HEI Tested positive and linked to CCC
                 *Composition for 24 Months cohorts
                 * @return the indicator
                 */
    public CohortDefinition heiTestedPositiveLinkedtoCCCTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "        inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "        inner join kenyaemr_etl.etl_hiv_enrollment e on hf.patient_id = e.patient_id\n" +
                "    where (final_antibody_result = 703 and TIMESTAMPDIFF(MONTH,pd.DOB,hf.final_antibody_sample_date) between 12 and 18)\n" +
                "          or (dna_pcr_result = 703 and TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) between 12 and 18)\n" +
                "          and pd.unique_patient_no is not null\n" +
                "    group by hf.patient_id;";

        cd.setName("infantLinkedtoCCCTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV positive infants linked to CCC among those testing positive between 12 and 18 months");

        return cd;
    }

    /*HEI Tested positive and linked to CCC: 24 Months Cohort
                  *Composition  heiCohort24Months + heiTestedPositiveLinkedtoCCCTwelveMonths
                  * @return the indicator
                  */
    public CohortDefinition heiTestedPositiveLinkedtoCCCTwelveMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiTestedPositiveLinkedtoCCCTwelveMonths",ReportUtils.map(heiTestedPositiveLinkedtoCCCTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiTestedPositiveLinkedtoCCCTwelveMonths");
        return cd;
    }

    /*HEI AB negative at 18 months
                     *Composition for 24 Months cohorts
                     * @return the indicator
                     */
    public CohortDefinition heiABNegativeEighteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "          inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "    where final_antibody_result = 664\n" +
                "         and TIMESTAMPDIFF(MONTH,pd.DOB,hf.visit_date) BETWEEN 18 AND 23 group by hf.patient_id;";

        cd.setName("ABNegativeEighteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("AB negative at 18 months");

        return cd;
    }
    /*HEI AB negative at 18 months: 24 Months Cohort
                     *Composition  heiCohort24Months + heiABNegativeEighteenMonthsCL
                     * @return the indicator
                     */
    public CohortDefinition heiABNegativeEighteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiABNegativeEighteenMonths",ReportUtils.map(heiABNegativeEighteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiABNegativeEighteenMonths");
        return cd;
    }
    /*Active at 18 months but no AB test done
                    *Composition for 24 Months cohorts
                    * @return the indicator
                    */
    public CohortDefinition activeHeiNoABTestEighteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "SELECT t1.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd ON t1.patient_id = pd.patient_id\n" +
                "      left join (select patient_id,final_antibody_result, first_antibody_result_date from kenyaemr_etl.etl_hei_follow_up_visit t1 where \n" +
                "      final_antibody_result is not null group by t1.patient_id) ab_test on (ab_test.patient_id = t1.patient_id)\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and \n" +
                "  (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 9)))\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation hd on (t1.patient_id = hd.patient_id and (hd.discontinuation_reason = 160034 and \n" +
                "  (hd.date_died is null or TIMESTAMPDIFF(MONTH,pd.DOB,hd.date_died) > 9)))\n" +
                "  WHERE date(ab_test.first_antibody_result_date) <=  DATE_ADD(pd.DOB, INTERVAL 18 MONTH)\n" +
                "  and ho.patient_id is null and hd.patient_id is null\n" +
                "  and ab_test.patient_id is not null  GROUP BY t1.patient_id;";

        cd.setName("activeNoABTestEighteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Active at 18 months but no AB test done");

        return cd;
    }
    /*Active at 18 months but no AB test done: 24 Months Cohort
                        *Composition  heiCohort24Months + activeHeiNoABTestEighteenMonths
                        * @return the indicator
                        */
    public CohortDefinition activeHeiNoABTestEighteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("activeHeiNoABTestEighteenMonths",ReportUtils.map(activeHeiNoABTestEighteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND activeHeiNoABTestEighteenMonths");
        return cd;
    }

    /*Identified as positive between 0 and 18 months
                    *Composition for 24 Months cohorts
                    * @return the indicator
                    */
    public CohortDefinition positiveIdentifiedEighteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "where (dna_pcr_result = 703 and TIMESTAMPDIFF(MONTH,pd.DOB,hf.visit_date) between 18 and 23) or\n" +
                "      (final_antibody_result = 703 and TIMESTAMPDIFF(MONTH,pd.DOB,hf.visit_date) between 18 and 23)\n" +
                "group by hf.patient_id;";

        cd.setName("positiveIdentifiedEighteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Identified as positive between 0 and 18 months");

        return cd;
    }
    /*Identified as positive between 0 and 18 months: 24 Months Cohort
                            *Composition  heiCohort24Months + positiveIdentifiedEighteenMonths
                            * @return the indicator
                            */
    public CohortDefinition positiveIdentifiedEighteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveIdentifiedEighteenMonths",ReportUtils.map(positiveIdentifiedEighteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND positiveIdentifiedEighteenMonths");
        return cd;
    }

    /*Transferred out between 0 and 18 months
                  *Composition for 24 Months cohorts
                  * @return the indicator
                  */
    public CohortDefinition transferredOutEighteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "      inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "      inner join kenyaemr_etl.etl_patient_program_discontinuation ho on (hf.patient_id = ho.patient_id and ho.discontinuation_reason = 159492)\n" +
                "      where TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) <= 18\n" +
                "      group by hf.patient_id;";

        cd.setName("transferredOutEighteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transferred out between 0 and 18 months");

        return cd;
    }
    /*Transferred out between 0 and 18 months: 24 Months Cohort
                           *Composition  heiCohort24Months + positiveIdentifiedEighteenMonths
                           * @return the indicator
                           */
    public CohortDefinition transferredOutEighteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferredOutEighteenMonths",ReportUtils.map(transferredOutEighteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND transferredOutEighteenMonths");
        return cd;
    }
    /*LTFU between 0 and 18 months
                     *Composition for 24 Months cohorts
                     * @return the indicator
                     */
    public CohortDefinition ltfuEighteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "SELECT t1.patient_id FROM kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "      INNER JOIN kenyaemr_etl.etl_patient_demographics pd ON (t1.patient_id = pd.patient_id)\n" +
                "          LEFT JOIN(SELECT MAX(next_appointment_date) AS latest_tca, t4.patient_id\n" +
                "      FROM kenyaemr_etl.etl_hei_follow_up_visit t4\n" +
                "          left join kenyaemr_etl.etl_patient_demographics t5 using(patient_id)\n" +
                "          where DATE_ADD(t5.DOB, INTERVAL 18 MONTH) <= t4.next_appointment_date\n" +
                "      GROUP BY t4.patient_id) latest_tca ON t1.patient_id = latest_tca.patient_id\n" +
                "      left join kenyaemr_etl.etl_hei_follow_up_visit t2 on (latest_tca.patient_id = t2.patient_id and latest_tca.latest_tca = t2.next_appointment_date)\n" +
                "      left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and \n" +
                "      (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 9)))\n" +
                "      left join kenyaemr_etl.etl_patient_program_discontinuation hd on (t1.patient_id = hd.patient_id and (hd.discontinuation_reason = 160034 and \n" +
                "      (hd.date_died is null or TIMESTAMPDIFF(MONTH,pd.DOB,hd.date_died) > 9)))\n" +
                "  WHERE latest_tca.patient_id is null and ho.patient_id is null and hd.patient_id is null\n" +
                "  and (t2.dna_pcr_result is null or t2.dna_pcr_result != 703) GROUP BY t1.patient_id;";

        cd.setName("ltfuEighteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Lost to Follow-Up between 0 and 18 months");

        return cd;
    }
             /*Lost to Follow-Up between 0 and 18 months : 24 Months Cohort
                              *Composition  heiCohort24Months + ltfuEighteenMonths
                              * @return the indicator
                              */
    public CohortDefinition ltfuEighteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ltfuEighteenMonths",ReportUtils.map(ltfuEighteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND ltfuEighteenMonths");
        return cd;
    }
    /*Died between 0 and 18 months
                     *Composition for 24 Months cohorts
                     * @return the indicator
                     */
    public CohortDefinition diedEighteenMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from  kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "   inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "   inner join kenyaemr_etl.etl_patient_program_discontinuation ho on (hf.patient_id = ho.patient_id and ho.discontinuation_reason = 160034)\n" +
                "   where ho.patient_id is not null and TIMESTAMPDIFF(MONTH,pd.DOB,ho.date_died) <= 18\n" +
                "   group by hf.patient_id;";

        cd.setName("diedEighteenMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Died between 0 and 18 months");

        return cd;
    }
    /*Died between 0 and 18 months : 24 Months Cohort
                              *Composition  heiCohort24Months + ltfuEighteenMonths
                              * @return the indicator
                              */
    public CohortDefinition diedEighteenMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("diedEighteenMonths",ReportUtils.map(diedEighteenMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND diedEighteenMonths");
        return cd;
    }

    /*HEI in registered cohort LESS  HEI positive, transferred out, or dead before 6 months of age
        * #Denominator
        *Composition for 12 Months cohorts
         */
    public CohortDefinition activeHeiLessPositiveToDiedSixMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t1.patient_id from kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on (t1.patient_id = pd.patient_id)\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and\n" +
                "                                                                                                       (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 6)))\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation hd on (t1.patient_id = hd.patient_id and (hd.discontinuation_reason = 160034 and\n" +
                "                                                                                                       (hd.date_died is null or TIMESTAMPDIFF(MONTH,pd.DOB,hd.date_died) > 6)))\n" +
                "where DATE_ADD(pd.DOB, INTERVAL 6 MONTH) <= t1.next_appointment_date\n" +
                "      and ho.patient_id is null and hd.patient_id is null\n" +
                "      and (t1.dna_pcr_result is null or t1.dna_pcr_result != 703) GROUP BY t1.patient_id;";

        cd.setName("heiActiveSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI cohort LESS  HEI positive, transferred out or dead before 6 months");

        return cd;
    }

    /*HEI in registered cohort LESS  HEI positive, transferred out, or dead before 6 months of age : 12 Months Cohort
        *Composition  heiCohort12Months + activeHeiLessPositiveToDiedSixMonths
                             *   @return the indicator
                             */
    public CohortDefinition activeHeiLessPositiveToDiedSixMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("activeHeiLessPositiveToDiedSixMonths",ReportUtils.map(activeHeiLessPositiveToDiedSixMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND activeHeiLessPositiveToDiedSixMonths");
        return cd;
    }

    /* HEI who had feeding status assessed (EBF, ERF, or MF) at 6 months
        * #Denominator
        *Composition for 12 Months cohorts
         */
    public CohortDefinition heiWithDocumentedFeedingMethodSixMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on hf.patient_id = pd.patient_id\n" +
                "where TIMESTAMPDIFF(month, date(pd.DOB),date(hf.visit_date)) <= 6\n" +
                "      and infant_feeding is not null";

        cd.setName("heiEligibleRepeatPCRSixMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI who had feeding status assessed  at 6 months");

        return cd;
    }

    /*HEI who had feeding status assessed (EBF, ERF, or MF) at 6 months : 12 Months Cohort
        *Composition  heiCohort12Months + heiWithDocumentedFeedingMethodSixMonths
                             *   @return the indicator
                             */
    public CohortDefinition heiWithDocumentedFeedingMethodSixMonths12MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort12Months", ReportUtils.map(heiCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiWithDocumentedFeedingMethodSixMonths",ReportUtils.map(heiWithDocumentedFeedingMethodSixMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort12Months AND heiWithDocumentedFeedingMethodSixMonths");
        return cd;
    }
    /*HEI in registered cohort LESS  HEI positive, transferred out, or dead before 12 months of age
           * #Denominator
           *Composition for 24 Months cohorts
            */
    public CohortDefinition activeHeiLessPositiveToDiedTwelveMonths() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t1.patient_id from kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on (t1.patient_id = pd.patient_id)\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and\n" +
                "                                                                                                       (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 12)))\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation hd on (t1.patient_id = hd.patient_id and (hd.discontinuation_reason = 160034 and\n" +
                "                                                                                                       (hd.date_died is null or TIMESTAMPDIFF(MONTH,pd.DOB,hd.date_died) > 12)))\n" +
                "where DATE_ADD(pd.DOB, INTERVAL 12 MONTH) <= t1.next_appointment_date\n" +
                "      and ho.patient_id is null and hd.patient_id is null\n" +
                "      and (t1.dna_pcr_result is null or t1.dna_pcr_result != 703) GROUP BY t1.patient_id;";

        cd.setName("heiActiveTwelveMonths");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI cohort LESS  HEI positive, transferred out or dead before 12 months");

        return cd;
    }

    /*HEI in registered cohort LESS  HEI positive, transferred out, or dead before 12 months of age : 24 Months Cohort
        *Composition  heiCohort24Months + activeHeiLessPositiveToDiedTwelveMonths
                             *   @return the indicator
                             */
    public CohortDefinition activeHeiLessPositiveToDiedTwelveMonths24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("activeHeiLessPositiveToDiedTwelveMonths",ReportUtils.map(activeHeiLessPositiveToDiedTwelveMonths(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND activeHeiLessPositiveToDiedTwelveMonths");
        return cd;
    }
    /*HEI AB test positive between 12 and 18 months who had a confirmatory DNA PCR
              * #Denominator
              *Composition for 24 Months cohorts
               */
    public CohortDefinition heiWithABTestPositiveAndPCRBetween12And18Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select hf.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hf\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on (hf.patient_id = pd.patient_id)\n" +
                "where dna_pcr_contextual_status in (162082)\n" +
                "      and first_antibody_result is not null and TIMESTAMPDIFF(MONTH,pd.DOB,hf.dna_pcr_sample_date) between 12 and 18\n" +
                "group by hf.patient_id";

        cd.setName("heiABPositiveWithPCR");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI AB test positive between 12 and 18 months who had a confirmatory DNA PCR");

        return cd;
    }

    /*HEI AB test positive between 12 and 18 months who had a confirmatory DNA PCR : 24 Months Cohort
        *Composition  heiCohort24Months + heiWithABTestPositiveAndPCRBetween12And18Months
                             *   @return the indicator
                             */
    public CohortDefinition heiWithABTestPositiveAndPCRBetween12And18Months24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiWithABTestPositiveAndPCRBetween12And18Months",ReportUtils.map(heiWithABTestPositiveAndPCRBetween12And18Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiWithABTestPositiveAndPCRBetween12And18Months");
        return cd;
    }

    /* HEI tested by AB >= 18 months
      * PLUSActive 18
      * PLUS   Loss to follow-up
             * #Denominator
             *Composition for 24 Months cohorts
              */
    public CohortDefinition heiWithABTestActiveAndLTFU() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select t1.patient_id from kenyaemr_etl.etl_hei_follow_up_visit t1\n" +
                "  inner join kenyaemr_etl.etl_patient_demographics pd on (t1.patient_id = pd.patient_id)\n" +
                "  left join kenyaemr_etl.etl_patient_program_discontinuation ho on (t1.patient_id = ho.patient_id and (ho.discontinuation_reason = 159492 and\n" +
                "                                                                     (ho.transfer_date is null or TIMESTAMPDIFF(MONTH,pd.DOB,ho.transfer_date) > 12)))\n" +
                "where DATE_ADD(pd.DOB, INTERVAL 18 MONTH) <= t1.next_appointment_date\n" +
                "      or ho.patient_id is not null\n" +
                "      and first_antibody_result is not null\n" +
                "group by t1.patient_id;";

        cd.setName("heiABActiveORLTFU");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HEI AB Active with LTFU");

        return cd;
    }

    /*HEI AB test active or ltfu : 24 Months Cohort
        *Composition  heiCohort24Months + heiWithABTestPositiveAndPCRBetween12And18Months
                             *   @return the indicator
                             */
    public CohortDefinition heiWithABTestActiveAndLTFU24MonthsCohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("heiCohort24Months", ReportUtils.map(heiCohort24Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiWithABTestActiveAndLTFU",ReportUtils.map(heiWithABTestActiveAndLTFU(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("heiCohort24Months AND heiWithABTestActiveAndLTFU");
        return cd;
    }
}