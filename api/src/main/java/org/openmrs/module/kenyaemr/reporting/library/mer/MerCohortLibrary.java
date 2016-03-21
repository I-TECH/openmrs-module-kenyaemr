package org.openmrs.module.kenyaemr.reporting.library.mer;

import org.openmrs.Concept;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

}
