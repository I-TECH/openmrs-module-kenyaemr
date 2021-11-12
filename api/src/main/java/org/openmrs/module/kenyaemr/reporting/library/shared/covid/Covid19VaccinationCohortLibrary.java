/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package reporting.library.shared.covid;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Date;

/**
 * Library of cohort definitions for Covid-19 vaccinations
 */
public class Covid19VaccinationCohortLibrary {

    DatimCohortLibrary datimCohortLibrary = new DatimCohortLibrary();

    public static CohortDefinition fullyVaccinated() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment a where a.final_vaccination_status = 5585 and a.visit_date <= date(:endDate)";
        cd.setName("fullyVaccinated");
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setQuery(sqlQuery);
        cd.setDescription("fullyVaccinated");

        return cd;
    }

    public CohortDefinition partiallyVaccinated() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment group by patient_id\n"
                + "        having mid(max(concat(visit_date,final_vaccination_status)),11) = 166192\n"
                + "        and max(visit_date) <= date(:endDate);";
        cd.setName("partiallyVaccinated;");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("partiallyVaccinated");

        return cd;
    }

    public CohortDefinition covid19AssessedPatients() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from kenyaemr_etl.etl_covid19_assessment a;";
        cd.setName("covid19AssessedPatients;");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("covid19AssessedPatients");

        return cd;
    }

    public CohortDefinition everInfected() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment where ever_tested_covid_19_positive = 703 and ever_vaccinated is not null and visit_date <= date(:endDate)\n"
                + "group by patient_id;";
        cd.setName("everInfected;");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("everInfected");

        return cd;
    }

    public CohortDefinition everHospitalised() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment where hospital_admission = 1065 and visit_date <= date(:endDate);\n";
        cd.setName("everHospitalised");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("everHospitalised");

        return cd;
    }

    public CohortDefinition diedDueToCovid() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_patient_program_discontinuation where discontinuation_reason =160034 and specific_death_cause=165609\n"
                + "and coalesce(date(date_died),coalesce(date(effective_discontinuation_date),date(visit_date))) <= date(:endDate);";
        cd.setName("diedDueToCovid");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("diedDueToCovid");

        return cd;
    }

    public CohortDefinition aged18AndAbove() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_patient_demographics where timestampdiff(YEAR ,dob,date(:endDate))>= 18;\n";
        cd.setName("aged18andAbove");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("aged18andAbove");

        return cd;
    }

    public CohortDefinition firstDoseVerifiedSQl() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = " select patient_id from kenyaemr_etl.etl_covid19_assessment where first_vaccination_verified = 164134 and\n"
                + "        visit_date <= date(:endDate);";
        cd.setName("firstDose");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("firstDose");

        return cd;
    }

    public CohortDefinition secondDoseVerifiedSQL() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_covid19_assessment where second_vaccination_verified = 164134 and\n"
                + "        visit_date <= date(:endDate);";
        cd.setName("secondDose");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("secondDose");

        return cd;
    }

    public CohortDefinition boosterDoseVerifiedSQL() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = " select patient_id from kenyaemr_etl.etl_covid19_assessment where booster_dose_verified = 164134 and\n"
                + "        visit_date <= date(:endDate);";
        cd.setName("boosterDose");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("boosterDose");

        return cd;
    }

    /**
     * Patients OnArt and partially vaccinated
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtPartiallyVaccinated() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("partiallyVaccinated",
                ReportUtils.map(partiallyVaccinated(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18AndAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18AndAbove AND partiallyVaccinated");
        return cd;
    }

    /**
     * Patients On Art and not vaccinated
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtNotVaccinatedCovid19() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("partiallyVaccinated",
                ReportUtils.map(partiallyVaccinated(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("fullyVaccinated", ReportUtils.map(fullyVaccinated(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18AndAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18AndAbove AND NOT (partiallyVaccinated OR fullyVaccinated)");
        return cd;
    }

    /**
     * Patients On Art and with unknown Covid-19 vaccination status
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtUnknownVaccinationStatus() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("covid19AssessedPatients",
                ReportUtils.map(covid19AssessedPatients(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18AndAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18AndAbove AND NOT covid19AssessedPatients");
        return cd;
    }

    /**
     * Patients OnArt and fully vaccinated
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtFullyVaccinated() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("fullyVaccinated", ReportUtils.map(fullyVaccinated(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18AndAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18AndAbove AND fullyVaccinated");
        return cd;
    }

    /**
     * Patients OnArt and ever infected
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtEverInfected() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("everInfected", ReportUtils.map(everInfected(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18AndAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18AndAbove AND everInfected");
        return cd;
    }

    /**
     * Patients OnArt and ever admitted to hospital due to covid
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtEverHospitalised() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("everHospitalised", ReportUtils.map(everHospitalised(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18AndAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18AndAbove AND everHospitalised");
        return cd;
    }

    /**
     * Patients OnArt and 18 years and above
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtAged18AndAbove() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18andAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18andAbove");
        return cd;
    }

    /**
     * Patients with first dose verified
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtFirstDoseVerified() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstDoseVerified",
                ReportUtils.map(firstDoseVerifiedSQl(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18andAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18andAbove AND firstDoseVerified");
        return cd;
    }

    /**
     * Patients with second dose verified
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtSecondDoseVerified() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("secondDoseVerified",
                ReportUtils.map(secondDoseVerifiedSQL(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18andAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18andAbove AND secondDoseVerified");
        return cd;
    }

    /**
     * Patients with booster dose verified
     *
     * @return the cohort definition
     */
    public CohortDefinition onArtBoosterDoseVerified() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(datimCohortLibrary.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("boosterDoseVerified",
                ReportUtils.map(boosterDoseVerifiedSQL(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("aged18andAbove", ReportUtils.map(aged18AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND aged18andAbove AND boosterDoseVerified");
        return cd;
    }
}
