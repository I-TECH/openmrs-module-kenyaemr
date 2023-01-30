/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.pmtct;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohort definitions for ANC Register
 */
@Component
public class ANCCohortLibrary {
	
	@Autowired
	private ANCIndicatorLibrary ancIndicatorLibrary;


	/**
	 * New ANC Clients
	 * 
	 * @return
	 */
	public CohortDefinition newClientsANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id\n" +
				"from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"     where v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancNewClients");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients registered within the reporting period");
		return cd;
	}

	/**
	 * Revisits ANC Clients
	 *
	 * @return
	 */
	public CohortDefinition revisitClientsANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id\n" +
				"      from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"      where v.visit_date between date(:startDate) AND date(:endDate)\n" +
				"      having count(v.visit_date) > 1;";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancClientsRevisits");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients with revisits within the reporting period");
		return cd;
	}
	/**
	 * Completed 4 ANC visits
	 *
	 * @return
	 */
	public CohortDefinition completed4AntenatalVisitsANCCohortDefinition() {
		String sqlQuery = "select  DISTINCT v.patient_id\n" +
				"         from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"           inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"           where v.visit_date between date(:startDate) AND date(:endDate)\n" +
				"          having count(v.visit_date) >= 4;";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancClientsCompletedVisits");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients with 4 visits within the reporting period");
		return cd;
	}
	/**
	 * ANC tested Syphylis
	 *
	 * @return
	 */
	public CohortDefinition testedSyphilisANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id\n" +
				"     from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"     where (v.syphilis_test_status is not null or v.syphilis_test_status !=1402) and\n" +
				"          v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancClientsTestedSyphilis");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients tested Syphilis within the reporting period");
		return cd;
	}
	/**
	 * ANC tested Syphilis positive
	 *
	 * @return
	 */
	public CohortDefinition syphilisPositiveANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id\n" +
				"          from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"            inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"             where v.syphilis_test_status =1228 and\n" +
				"                   v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancClientsTestedSyphilisPositive");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients tested Positive Syphilis within the reporting period");
		return cd;
	}
	/**
	 * ANC tested Syphilis treated
	 *
	 * @return
	 */
	public CohortDefinition syphilisTreatedANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id\n" +
				"        from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"          inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"         where v.syphilis_treated_status =1065 and\n" +
				"               v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancClientsTestedSyphilisTreated");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients treated Syphilis within the reporting period");
		return cd;
	}
	/**
	 * ANC  Known positive
	 *
	 * @return
	 */
	public CohortDefinition knownPositivesFirstANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id\n" +
				"      from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"            where e.hiv_status=703 and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("ancClientsKnownPositive");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("ANC clients known positive within the reporting period");
		return cd;
	}

	/**
	 * Initial test at ANC
	 *
	 * @return
	 */
	public CohortDefinition initialTestANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id=v.patient_id\n" +
				"  left outer join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id= v.patient_id\n" +
				"  left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=v.patient_id\n" +
				"where v.visit_date between date(:startDate) AND date(:endDate) and\n" +
				"      e.hiv_status !=703 and\n" +
				"      ld.final_test_result is null and\n" +
				"       p.final_test_result is null and\n" +
				"       v.final_test_result is not null ;";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("initialTestAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Initial test at ANC  within the reporting period");
		return cd;
	}
	/**
	 * Positive test at ANC
	 *
	 * @return
	 */
	public CohortDefinition positiveTestANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"          inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"          where e.hiv_status !=703 and v.final_test_result ='POSITIVE' and\n" +
				"                v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("positiveTestAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Positive test at ANC  within the reporting period");
		return cd;
	}

	/**
	 * On ARV at ANC
	 *
	 * @return
	 */
	public CohortDefinition onARVFirstANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        inner join kenyaemr_etl.etl_drug_event d on d.patient_id=v.patient_id\n" +
				"        where d.program = 'HIV' and d.date_started < e.visit_date and\n" +
				"              v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("onARVAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("On ARV at ANC  within the reporting period");
		return cd;
	}

	/**
	 * Started HAART at ANC
	 *
	 * @return
	 */
	public CohortDefinition startedHAARTInANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"          inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"          inner join kenyaemr_etl.etl_drug_event d on v.patient_id=d.patient_id\n" +
				"           where d.date_started >= v.visit_date and\n" +
				"                 v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("startedARVAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Started ARV at ANC  within the reporting period");
		return cd;
	}

	/**
	 * Given AZT for Baby at ANC
	 *
	 * @return
	 */
	public CohortDefinition aztBabyANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"   where v.baby_azt_dispensed = 160123 and\n" +
				"         v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("givenAZTAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given AZT for Baby at ANC  within the reporting period");
		return cd;
	}

	/**
	 * Given NVP for Baby at ANC
	 *
	 * @return
	 */
	public CohortDefinition nvpBabyANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"      where v.baby_nvp_dispensed = 80586 and\n" +
				"            v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("givenNVPAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given AZT for Baby at NVP  within the reporting period");
		return cd;
	}

	/**
	 * Screened TB at ANC
	 *
	 * @return
	 */
	public CohortDefinition screenedTbANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        where (v.tb_screening is not null or v.tb_screening !=160737) and\n" +
				"              v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("screenedTbAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Screened TB at ANC within the reporting period");
		return cd;
	}

	/**
	 * Screened Cacx pap at ANC
	 *
	 * @return
	 */
	public CohortDefinition screenedCaCxPapANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"         where v.cacx_screening_method =885 and\n" +
				"              v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("screenedCacxPapAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Screened Cacx Pap at ANC within the reporting period");
		return cd;
	}

	/**
	 * Screened Cacx via at ANC
	 *
	 * @return
	 */
	public CohortDefinition screenedCaCxViaANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"         where v.cacx_screening_method =162816 and\n" +
				"              v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("screenedCacxViaAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Screened Cacx Via at ANC within the reporting period");
		return cd;
	}

	/**
	 * Screened Cacx vili at ANC
	 *
	 * @return
	 */
	public CohortDefinition screenedCaCxViliANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"         where v.cacx_screening_method =164977 and\n" +
				"              v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("screenedCacxViliAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Screened Cacx Vili at ANC within the reporting period");
		return cd;
	}

	/**
	 * Givien IPT 1 at ANC
	 *
	 * @return
	 */
	public CohortDefinition givenIPT1ANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"          where  v.IPT_dose_given_anc = 1 and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("givenIPT1AtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given IPT 1 at ANC within the reporting period");
		return cd;
	}

	/**
	 * Givien IPT 2at ANC
	 *
	 * @return
	 */
	public CohortDefinition givenIPT2ANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"  inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"          where  v.IPT_dose_given_anc = 2 and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("givenIPT2AtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given IPT 2 at ANC within the reporting period");
		return cd;
	}

	/**
	 * Givien ITN at ANC
	 *
	 * @return
	 */
	public CohortDefinition givenITNANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        where v.bed_nets ='Yes' and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("givenITNAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Given ITN at ANC within the reporting period");
		return cd;
	}

	/**
	 * Partner tested at ANC
	 *
	 * @return
	 */
	public CohortDefinition partnerTestedANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        where v.partner_hiv_tested =1065 and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("partnerTestedAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Partner Tested at ANC within the reporting period");
		return cd;
	}

	/**
	 * Partner known positive at ANC
	 *
	 * @return
	 */
	public CohortDefinition partnerKnownPositiveANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        where e.partner_hiv_status=703 and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("partnerKnownPositiveAtANC");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Partner Known Positive at ANC within the reporting period");
		return cd;
	}

	/**
	 * Adolescents known positive 10-19 at ANC
	 *
	 * @return
	 */
	public CohortDefinition adolescentsKnownPositive_10_19_AtANCCohortDefinition() {
		String sqlQuery = "select distinct e.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        inner join  kenyaemr_etl.etl_patient_demographics d on d.patient_id = e.patient_id\n" +
				"        where e.hiv_status =703 and timestampdiff(year,d.DOB,e.visit_date) between 10 and 19\n" +
				"              and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("adolescentsKnownPositive_10_19_AtANCCohortDefinition");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Adolescents Known Positive 10-19 at ANC within the reporting period");
		return cd;
	}
	/**
	 * Adolescents tested positive 10-19 at ANC
	 *
	 * @return
	 */
	public CohortDefinition adolescentsTestedPositive_10_19_AtANCCohortDefinition() {
		String sqlQuery = "select distinct v.patient_id  from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"        inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"        inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = v.patient_id\n" +
				"        where timestampdiff(year,d.DOB,v.visit_date) BETWEEN 10 AND 19 and\n" +
				"        v.final_test_result = 'Positive' and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("adolescentsTestedPositive_10_19_AtANCCohortDefinition");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Adolescents Tested Positive 10-19 at ANC within the reporting period");
		return cd;
	}
	/**
	 * Adolescents started ART 10-19 at ANC
	 *
	 * @return
	 */
	public CohortDefinition adolescentsStartedHaart_10_19_AtANCCohortDefinition() {
		String sqlQuery = "select\n" +
				"     distinct v.patient_id\n" +
				"     from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
				"       inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id= v.patient_id\n" +
				"       inner join kenyaemr_etl.etl_drug_event d on d.patient_id=e.patient_id\n" +
				"       inner join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id=e.patient_id\n" +
				"     left join kenyaemr_etl.etl_mchs_delivery ld on ld.patient_id=e.patient_id\n" +
				"     left join kenyaemr_etl.etl_mch_postnatal_visit pn on pn.patient_id=e.patient_id\n" +
				"     WHERE d.program = 'HIV' and timestampdiff(year,dm.DOB,e.visit_date) BETWEEN 10 AND 19\n" +
				"     and d.date_started >= e.visit_date and (d.date_started < ld.visit_date or d.date_started < pn.visit_date)\n" +
				"      and v.visit_date between date(:startDate) AND date(:endDate);";
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("adolescentsStartedART_10_19_AtANCCohortDefinition");
		cd.setQuery(sqlQuery);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Adolescents Started ART 10-19 at ANC within the reporting period");
		return cd;
	}

}
