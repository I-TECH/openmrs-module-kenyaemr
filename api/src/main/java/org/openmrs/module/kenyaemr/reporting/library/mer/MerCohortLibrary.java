/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.mer;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by codehub on 3/14/16.
 * file that contain all libraries for mer
 */
@Component
public class MerCohortLibrary {

    @Autowired
    private HivCohortLibrary hivCohorts;

    @Autowired
    private CommonCohortLibrary commonCohorts;

    @Autowired
    private ArtCohortLibrary artCohorts;

    @Autowired
    private MchcsCohortLibrary mchcsCohorts;

    /**
     * Number of HIV-positive pregnant women who received antiretrovirals (ARVs) to reduce risk of mother-to-child-transmission during pregnancy
     * @return CohortDefinition
     */
    public CohortDefinition numberOfHivPositivePregnantWomenWhoReceivedART(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName("pregnant and on ART");
        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addSearch("startedArtAndPregnant", ReportUtils.map(artCohorts.startedArtWhilePregnant(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("onrtAndPregnant", ReportUtils.map(artCohorts.onArtAndPregnant(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.setCompositionString("startedArtAndPregnant OR onrtAndPregnant");

        return cd;
    }

    /**
     * Number of infants who had a virologic HIV test within 12 months of birth during the reporting period
     * @return CohortDefinition
     */
    public CohortDefinition numberOfInfantsWhoHadVirologicHivTestWithin12MonthsOfBirthDuringTheReportingPeriod(){
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName("Infants count with 12 months of birth");
        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
        cd.addSearch("pcrInitialWithin2Months", ReportUtils.map(mchcsCohorts.pcrInitialWithin2Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("pcrInitialBetween3To8Months", ReportUtils.map(mchcsCohorts.pcrInitialBetween3To8Months(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("pcrBetween9And12MonthsAge", ReportUtils.map(mchcsCohorts.pcrBetween9And12MonthsAge(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));

        cd.setCompositionString("pcrInitialWithin2Months OR pcrInitialBetween3To8Months OR pcrBetween9And12MonthsAge");
        return cd;

    }

    /**
     * Number of HIV-exposed infants with a documented outcome by 18 months of age (collection of 18 month outcomes is recommended at 24 months of age)
     * @return CohortDefinition
     */
    public CohortDefinition numberOfHivExposedInfantsWithDocumentedOutcome() {

        AgeCohortDefinition ageCohortDefinition = new AgeCohortDefinition();
        ageCohortDefinition.setName("ageAt24");
        ageCohortDefinition.setMaxAge(24);
        ageCohortDefinition.setMaxAgeUnit(DurationUnit.MONTHS);
        ageCohortDefinition.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName("Number of HIV-exposed infants with a documented outcome by 18 months of age");
        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
        cd.addSearch("hivExposed", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV)),  "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("hasOutcome", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION)),  "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("age", ReportUtils.map(ageCohortDefinition, "effectiveDate=${onOrBefore}"));
        cd.setCompositionString("hivExposed AND hasOutcome AND age");
        return cd;
    }

    /**
     * Number of HIV-exposed infants registered in the birth cohort at any time between 0 and 18 months of age (including transfers-ins)
     * @return CohortDefinition
     */
    public CohortDefinition numberOfHivExposedInfantsRegisteredInTheBirthCohortAtAnyTime() {
        AgeCohortDefinition ageCohortDefinition = new AgeCohortDefinition();
        ageCohortDefinition.setName("ageAt24");
        ageCohortDefinition.setMaxAge(24);
        ageCohortDefinition.setMaxAgeUnit(DurationUnit.MONTHS);
        ageCohortDefinition.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName("Number of HIV-exposed infants registered in the birth cohort at any time between 0 and 18 months of age");
        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
        cd.addSearch("hivExposed", ReportUtils.map(commonCohorts.hasObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV)),  "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("age", ReportUtils.map(ageCohortDefinition, "effectiveDate=${onOrBefore}"));
        cd.setCompositionString("hivExposed AND age");
        return cd;
    }

    /**
     * Number of infants born to HIV-positive women who were started on CTX prophylaxis within two months of birth at USG supported sites within the reporting period
     * @return CohortDefinition
     */
    public CohortDefinition infantsBornToHivPositiveWomenStartedCtxWithin2MonthsOfBirth() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();

        EncounterCohortDefinition enc = new EncounterCohortDefinition();
        enc.setName("has delivery encounter");
        enc.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        enc.addParameter(new Parameter("onOrBefore", "Beforer date", Date.class));
        enc.setEncounterTypeList(Arrays.asList(MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION)));
        enc.setFormList(Arrays.asList(MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY)));

        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
        cd.addSearch("hivPositive", ReportUtils.map(hivCohorts.enrolled(), "enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}"));
        cd.addSearch("onCtx", ReportUtils.map(hivCohorts.onCtxProphylaxis(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.addSearch("delivered", ReportUtils.map(enc, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.setCompositionString("hivPositive AND onCtx AND delivered");
        return cd;
    }

    /**
     * Number of HIV-positive pregnant women identified in the reporting period (including known HIV-positives at entry)
     * @return CohortDefinition
     */
    public CohortDefinition hivPositivePregnantWomenIdendifiedInTheReportingPeriod() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();

        CalculationCohortDefinition calc = new CalculationCohortDefinition(new IsPregnantCalculation());
        calc.setName("pregnant");
        calc.addParameter(new Parameter("onDate", "On Date", Date.class));

        cd.addParameter(new Parameter("onOrAfter", "After date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
        cd.addSearch("hivPositive", ReportUtils.map(hivCohorts.enrolled(), "enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}"));
        cd.addSearch("pregnant", ReportUtils.map(calc, "onDate=${onOrBefore}"));
        cd.setCompositionString("hivPositive AND pregnant");
        return cd;

    }

}
