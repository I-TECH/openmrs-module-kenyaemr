/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.MOH745;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Moh745CohortLibrary {


    public Moh745CohortLibrary() {
        // TODO Auto-generated constructor stub
    }

    //Queries for Moh745
    /*Received VIA or VIA/ VILI Screening */
    public CohortDefinition receivedVIAScreeningCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where (i.screening_method = 'VIA' or i.screening_method = 'VILI') and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("VIA/VILI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Received VIA or VIA/ VILI Screening");

        return cd;
    }

    /*Received Pap smear Screening*/
    public CohortDefinition receivedPapSmearScreeningCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method = 'Pap Smear' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("PAP SMEAR");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Received Pap Smear Screening");

        return cd;
    }

    /*Received HPV Test*/
    public CohortDefinition receivedHPVTestCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method = 'HPV Test' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("HPV Test");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Received HPV Test");

        return cd;
    }

    /*Positive VIA or VIA/VILI result*/
    public CohortDefinition positiveVIAresultCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i " +
                "where (i.screening_method = 'VIA' or i.screening_method = 'VILI') and i.screening_result = 'Positive'" +
                "and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Positive VIA or VIA/VILI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive VIA or VIA/VILI Result");

        return cd;
    }

    /*Positive Cytology result*/
    public CohortDefinition positiveCytologyResultCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method = 'Pap Smear'" +
                " and i.screening_result = 'Positive' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Positive Cytology");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive Cytology Result");

        return cd;
    }

    /*Positive HPV result*/
    public CohortDefinition positiveHPVResultCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_method = 'HPV Test' " +
                "and i.screening_result = 'Positive' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Positive HPV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive HPV Result");

        return cd;
    }

    /*Suspicious cancer lesions*/
    public CohortDefinition suspiciousCancerLesionsCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = 'Positive' or i.screening_result = 'Presumed' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Suspicious Cancer Lesions");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Have Suspicious Cancer Lesions");

        return cd;
    }

    /*Treated Using Cryotherapy*/
    public CohortDefinition treatedUsingCryotherapyCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = 'Positive'" +
                " and i.treatment_method = 'Cryotherapy postponed' or i.treatment_method = 'Cryotherapy performed (single Visit)' " +
                "or i.treatment_method = 'Cryotherapy performed' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Cryotherapy Treatment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Treated Using Cryotherapy");

        return cd;
    }

    /*Treated Using Thermocoagulation*/
    public CohortDefinition treatedUsingThermocoagulationCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = 'Positive'" +
                " and i.treatment_method = 'Thermocoagulation'  and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Thermocoagulation Treatment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Treated Using Thermocoagulation");

        return cd;
    }

    /*Treated using LEEP*/
    public CohortDefinition treatedUsingLEEPCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = 'Positive'" +
                " and i.treatment_method = 'LEEP' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("LEEP Treatment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Treated using LEEP");

        return cd;
    }

    /*Other Treatment Given*/
    public CohortDefinition otherTreatmentGivenCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i where i.screening_result = 'Positive'" +
                " and i.treatment_method = 'Other' and visit_date between date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("Other Treatment");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Other Treatment Given");

        return cd;
    }

    /*HIV Positive Clients Screened*/
    public CohortDefinition HIVPositiveClientsScreenedCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i inner join kenyaemr_etl.etl_hts_test t on t.patient_id = i.patient_id where t.final_test_result = 'Positive' and date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("HIV Positive Clients Screened");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Clients Screened");

        return cd;
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortDefinition HIVPositiveClientsScreenedWithResultsCl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select i.patient_id from kenyaemr_etl.etl_cervical_cancer_screening i inner join kenyaemr_etl.etl_hts_test t on t.patient_id = i.patient_id where t.final_test_result = 'Positive' and i.screening_result = 'Positive' and date(:startDate) and date(:endDate) group by i.patient_id;";

        cd.setName("HIV Positive With Positive Screening Results");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive With Positive Screening Results");

        return cd;
    }

}
