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

import org.openmrs.module.kenyaemr.reporting.library.shared.mchms.MchmsCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Created by codehub on 3/14/16.
 * Cohort library
 */
@Component
public class MerCohortIndicatorLibrary {

    @Autowired
    private MerCohortLibrary merCohorts;

    @Autowired
    private MchmsCohortLibrary mchmsCohortLibrary;

    /**
     * Percentage of pregnant women with known HIV status (includes women who were tested for HIV and received their results)
     * @return CohortIndicator
     */
   public  CohortIndicator percentageOfPregnantWomenWithKnownHivStatus() {
        return cohortIndicator("percentage of pregnant women with known HIV status",
                map(mchmsCohortLibrary.testedForHivBeforeOrDuringMchms(), "onOrAfter=${endDate-3m},onOrBefore=${endDate}"),
                map(mchmsCohortLibrary.testedForHivInMchms(), "onOrAfter=${endDate-3m},onOrBefore=${endDate}"));

    }
    /**
     * Percentage of HIV-positive pregnant women who received antiretrovirals to reduce risk of mother-to-child-transmission (MTCT) during pregnancy and delivery
     * @return CohortIndicator
     */
    public CohortIndicator percentageOfHivPositivePregnantWomenWhoReceivedArtDuringPregnancyAndDelivery() {

        return cohortIndicator("Percentage of HIV-positive pregnant women who received art",
                map(merCohorts.numberOfHivPositivePregnantWomenWhoReceivedART(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
                map(mchmsCohortLibrary.testedHivPositiveInMchms(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"));

    }

    /**
     *Percentage of infants born to HIV-positive women who had a virologic HIV test done within 12 months of birth
     * @return CohortIndicator
     */
    public CohortIndicator percentageOfInfantsBornToHIVPositiveWomenWhoHadVirologicHivTestDoneWithin12MonthsOfBirth(){
        return cohortIndicator("Percentage of infants born to HIV-positive women who had a virologic HIV test done within 12 months of birth",
                map(merCohorts.numberOfInfantsWhoHadVirologicHivTestWithin12MonthsOfBirthDuringTheReportingPeriod(), "onOrAfter=${endDate-3m},onOrBefore=${endDate}"),
                map(mchmsCohortLibrary.testedHivPositiveInMchms(), "onOrAfter=${endDate-3m},onOrBefore=${endDate}"));
    }

    /**
     * Final outcomes among HIV exposed infants registered in the birth cohort
     * @return CohortIndicator
     */
    public CohortIndicator finalOutcomesAmongHivExposedInfantsRegisteredInTheBirthCohort(){
        return cohortIndicator("Final outcomes among HIV exposed infants registered in the birth cohort",
                map(merCohorts.numberOfHivExposedInfantsWithDocumentedOutcome(), "onOrAfter=${endDate-12m},onOrBefore=${endDate}"),
                map(merCohorts.numberOfHivExposedInfantsRegisteredInTheBirthCohortAtAnyTime(), "onOrAfter=${endDate-12m},onOrBefore=${endDate}"));
    }

    /**
     * Percentage of infants born to HIV-positive pregnant women who were started on Cotrimoxazole (CTX) prophylaxis within two months of birth
     * @return CohortIndicator
     */
    public CohortIndicator infantsBornToHivPositivePregnantWomenWhoStartedCtxWithin2MonthsOfBirth() {
        return cohortIndicator("Percentage of infants born to HIV-positive pregnant women who were started on Cotrimoxazole (CTX) prophylaxis within two months of birth",
                map(merCohorts.infantsBornToHivPositiveWomenStartedCtxWithin2MonthsOfBirth(), "onOrAfter=${endDate-3m},onOrBefore=${endDate}"),
                map(merCohorts.hivPositivePregnantWomenIdendifiedInTheReportingPeriod(), "onOrAfter=${endDate-3m},onOrBefore=${endDate}"));
    }
}
