/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.common;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

/**
 * Library of common dimension definitions
 */
@Component
public class CommonDimensionLibrary {

    @Autowired
    private CommonCohortLibrary commonCohortLibrary;

    /**
     * Gender dimension
     * @return the dimension
     */
    public CohortDefinitionDimension gender() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("gender");
        dim.addCohortDefinition("M", map(commonCohortLibrary.males()));
        dim.addCohortDefinition("F", map(commonCohortLibrary.females()));
        return dim;
    }

    /**
     * Dimension of age using the 3 standard age groups
     * @return the dimension
     */
    public CohortDefinitionDimension standardAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("age groups (<1, <15, 15+)");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<1", map(commonCohortLibrary.agedAtMost(0), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("<15", map(commonCohortLibrary.agedAtMost(14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15+", map(commonCohortLibrary.agedAtLeast(15), "effectiveDate=${onDate}"));
        return dim;
    }

    /**
     * Dimension of age between
     * @return Dimension
     */
    public CohortDefinitionDimension artRegisterAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();

        dim.setName("fine age between(<9, btw 10 and 19, 25+");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<9", map(commonCohortLibrary.agedAtMost(0), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("9-19", map(commonCohortLibrary.agedAtLeastAgedAtMost(9, 19), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("20+", map(commonCohortLibrary.agedAtLeast(20), "effectiveDate=${onDate}"));
         return dim;
    }

    /**
     * Dimension of age between
     * @return Dimension
     */
    public CohortDefinitionDimension datimFineAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("fine age between(<1, btw 1 and 9, btw 10 and 14, btw 15 and 19, btw 20 and 24, btw 25 and 49, 50+");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<1", map(commonCohortLibrary.agedAtMost(0), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("<10", map(commonCohortLibrary.agedAtMost(9), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("1-4", map(commonCohortLibrary.agedAtLeastAgedAtMost(1, 4), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("5-9", map(commonCohortLibrary.agedAtLeastAgedAtMost(5, 9), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("1-9", map(commonCohortLibrary.agedAtLeastAgedAtMost(1, 9), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("10-14", map(commonCohortLibrary.agedAtLeastAgedAtMost(10, 14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15-19", map(commonCohortLibrary.agedAtLeastAgedAtMost(15, 19), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("20-24", map(commonCohortLibrary.agedAtLeastAgedAtMost(20, 24), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("25-49", map(commonCohortLibrary.agedAtLeastAgedAtMost(25, 49), "effectiveDate=${onDate}"));
        // new age disaggregations
        dim.addCohortDefinition("25-29", map(commonCohortLibrary.agedAtLeastAgedAtMost(25, 29), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("30-34", map(commonCohortLibrary.agedAtLeastAgedAtMost(30, 34), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("35-39", map(commonCohortLibrary.agedAtLeastAgedAtMost(35, 39), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("40-44", map(commonCohortLibrary.agedAtLeastAgedAtMost(40, 44), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("45-49", map(commonCohortLibrary.agedAtLeastAgedAtMost(45, 49), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("40-49", map(commonCohortLibrary.agedAtLeastAgedAtMost(40, 49), "effectiveDate=${onDate}"));
        // previous one
        dim.addCohortDefinition("50+", map(commonCohortLibrary.agedAtLeast(50), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("25+", map(commonCohortLibrary.agedAtLeast(25), "effectiveDate=${onDate}"));
        //mer2.6
        dim.addCohortDefinition("50-54", map(commonCohortLibrary.agedAtLeastAgedAtMost(50, 54), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("55-59", map(commonCohortLibrary.agedAtLeastAgedAtMost(55, 59), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("60-64", map(commonCohortLibrary.agedAtLeastAgedAtMost(60, 64), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("65+", map(commonCohortLibrary.agedAtLeast(65), "effectiveDate=${onDate}"));
        //Age group in months
        dim.addCohortDefinition("0-2", map(commonCohortLibrary.agedAtLeastAgedAtMostInMonths(0, 2), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("2-12", map(commonCohortLibrary.agedAtLeastAgedAtMostInMonths(2, 12), "effectiveDate=${onDate}"));

        dim.addCohortDefinition("<15", map(commonCohortLibrary.agedAtMost(14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15+", map(commonCohortLibrary.agedAtLeast(15), "effectiveDate=${onDate}"));

        return dim;
    }

    /**
     * Dimension of age between
     * @return Dimension
     */
    public CohortDefinitionDimension moh731GreenCardAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("fine age between(<1, btw 1 and 9, btw 10 and 14, btw 15 and 19, btw 20 and 24, 25+");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<1", map(commonCohortLibrary.agedAtMost(0), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("1-9", map(commonCohortLibrary.agedAtLeastAgedAtMost(1, 9), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("0-9", map(commonCohortLibrary.agedAtLeastAgedAtMost(0, 9), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("0-14", map(commonCohortLibrary.agedAtMost(14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15+", map(commonCohortLibrary.agedAtLeast(15), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("10-14", map(commonCohortLibrary.agedAtLeastAgedAtMost(10, 14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15-19", map(commonCohortLibrary.agedAtLeastAgedAtMost(15, 19), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("20-24", map(commonCohortLibrary.agedAtLeastAgedAtMost(20, 24), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("25+", map(commonCohortLibrary.agedAtLeast(25), "effectiveDate=${onDate}"));
        //25-49 and 50+ added for KDoD
        dim.addCohortDefinition("25-49", map(commonCohortLibrary.agedAtLeastAgedAtMost(25, 49), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("50+", map(commonCohortLibrary.agedAtLeast(50), "effectiveDate=${onDate}"));

        return dim;
    }

    /**
     * Dimension of age between
     * @return Dimension
     */
    public CohortDefinitionDimension otzAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("fine age between(<1, btw 1 and 9, btw 10 and 14, btw 15 and 19, btw 20 and 24, 25+");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("10-19", map(commonCohortLibrary.agedAtLeastAgedAtMost(10, 19), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("20-24", map(commonCohortLibrary.agedAtLeastAgedAtMost(20, 24), "effectiveDate=${onDate}"));

        return dim;
    }


    /**
     * Dimension of age between
     * @return Dimension
     */
    public CohortDefinitionDimension moh710AgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("Fine age between(<1,>=1)");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<1", map(commonCohortLibrary.agedAtMost(0), "effectiveDate=${onDate}"));
        dim.addCohortDefinition(">=1", map(commonCohortLibrary.agedAtLeast(1), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("18-24", map(commonCohortLibrary.agedAtLeastAgedAtMostInMonths(18, 24), "effectiveDate=${onDate}"));
        dim.addCohortDefinition(">2", map(commonCohortLibrary.agedAtLeast(2), "effectiveDate=${onDate}"));
        return dim;
    }

    /**
     * Dimension of age using the 2 standard age groups. <15 and 15+ years
     * @return the dimension
     */
    public CohortDefinitionDimension diffCareAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("age groups (<15, 15+)");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<15", map(commonCohortLibrary.agedAtMost(14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15+", map(commonCohortLibrary.agedAtLeast(15), "effectiveDate=${onDate}"));
        return dim;
    }
    /**
     * Cadre dimension
     * @return the dimension
     */
    public CohortDefinitionDimension cadre() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("cadre");
        dim.addCohortDefinition("T", map(commonCohortLibrary.kDoDTroupesPatients()));
        dim.addCohortDefinition("C", map(commonCohortLibrary.kDoDCiviliansPatients()));
        return dim;
    }

    /**
     * Dimension of age using the 2 standard age groups
     * @return the dimension
     */
    public CohortDefinitionDimension contactAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("age groups (<15, 15+)");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<15", map(commonCohortLibrary.contactAgedAtMost(14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15+", map(commonCohortLibrary.contactAgedAtLeast(15), "effectiveDate=${onDate}"));

        return dim;
    }

    /**
     * Dimension of age between
     * @return Dimension
     */
    public CohortDefinitionDimension contactsFineAgeGroups() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("fine age between(<1, btw 1 and 4,btw 5 and 9, btw 10 and 14, btw 15 and 19, btw 20 and 24, btw 25 and 29, btw 30 and 34, btw 35 and 39, btw 40 and 44, btw 45 and 49, 50+");
        dim.addParameter(new Parameter("onDate", "Date", Date.class));
        dim.addCohortDefinition("<1", map(commonCohortLibrary.contactAgedAtMost(0), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("1-4", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(1,4), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("5-9", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(5,9), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("10-14", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(10,14), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("15-19", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(15,19), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("20-24", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(20,24), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("25-29", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(25,29), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("30-34", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(30,34), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("35-39", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(35,39), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("40-44", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(40,44), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("45-49", map(commonCohortLibrary.contactAgedAtLeastAgedAtMost(45,49), "effectiveDate=${onDate}"));
        dim.addCohortDefinition("50+", map(commonCohortLibrary.contactAgedAtLeast(50), "effectiveDate=${onDate}"));

        return dim;
    }

    /**
     * Gender dimension for patient contact
     * @return the dimension
     */
    public CohortDefinitionDimension contactGender() {
        CohortDefinitionDimension dim = new CohortDefinitionDimension();
        dim.setName("contactGender");
        dim.addCohortDefinition("M", map(commonCohortLibrary.malePatientContacts()));
        dim.addCohortDefinition("F", map(commonCohortLibrary.femalePatientContacts()));
        return dim;
    }

}