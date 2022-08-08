/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.*;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DiffCareStableUnder4MonthstcaUnder15CohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DiffCareUndocumentedStabilityCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731CohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import reporting.library.shared.covid.Covid19VaccinationCohortLibrary;

import java.util.Arrays;

/**
 * Created by antony on 09/08/17.
 */
public class DashBoardCohorts {

    @Autowired
    private ETLMoh731CohortLibrary moh731Cohorts;

    private Covid19VaccinationCohortLibrary covid19VaccinationCohortLibrary = new Covid19VaccinationCohortLibrary();

    /**
     * If your use case allows it you should always use #allPatients(EvaluationContext); this method will not cache
     * results, which can significantly impact efficiency.
     *
     * @return All non-test patients in the system (with test patients excluded per the global property
     * {@link org.openmrs.module.reporting.ReportingConstants#GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION}
     */
    public static EvaluatedCohort allPatients() {
        return allPatients(null);
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return All non-test patients in the system (with test patients excluded per the global property
     * {@link org.openmrs.module.reporting.ReportingConstants#GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION}
     */
    public static EvaluatedCohort allPatients(EvaluationContext context) {
        try {
            return getService().evaluate(new AllPatientsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating allPatients", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort males(EvaluationContext context) {
        try {
            return getService().evaluate(new BuiltInCohortDefinitionLibrary().getMales(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating males", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort females(EvaluationContext context) {
        try {
            return getService().evaluate(new BuiltInCohortDefinitionLibrary().getFemales(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating females", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort patientsOnART(EvaluationContext context) {
        try {
            return getService().evaluate(new CumulativeOnARTCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating cumulative on art", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort onART(EvaluationContext context) {
        try {
            return getService().evaluate(new ETLCurrentOnARTCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating cumulative on art", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort inCare(EvaluationContext context) {
        try {
            return getService().evaluate(new ETLCurrentOnCareCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating current in care", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort newOnART(EvaluationContext context) {
        try {
            return getService().evaluate(new ETLNewOnARTCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating new on art", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort viralLoadResultsIn12Months(EvaluationContext context) {
        try {
            return getService().evaluate(new ETLPatientsWithVLInLast12MonthsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients with VL results in last 12 months", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort viralLoadSuppressionIn12Months(EvaluationContext context) {
        try {
            return getService().evaluate(new ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients with viral load suppression in 12 months", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort patientsSeen(EvaluationContext context) {
        try {
            return getService().evaluate(new AppointmentsPatientsSeenCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients with VL results in last 12 months", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort patientsScheduledToday(EvaluationContext context) {
        try {
            return getService().evaluate(new AppointmentsDailyScheduleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients with viral load suppression in 12 months", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort unscheduledAppointments(EvaluationContext context) {
        try {
            return getService().evaluate(new AppointmentsUnscheduledCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients with unscheduled visits", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort checkedInAppointments(EvaluationContext context) {
        try {
            return getService().evaluate(new AppointmentsCheckedInCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients who have been checked in", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort enrolledInHiv(EvaluationContext context) {
        try {
            ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
            cd.setPrograms(Arrays.asList(MetadataUtils.existing(Program.class, HivMetadata._Program.HIV)));
            return getService().evaluate(cd, context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients enrolled in Hiv", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort newlyEnrolledInHiv(EvaluationContext context) {
        try {
            return getService().evaluate(new ETLNewHivEnrollmentCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating patients newly enrolled in Hiv", e);
        }
    }
    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalTested(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSClientsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total HTS tested", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalPositive(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSPositiveResultsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating HTS positive Results", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalLinked(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSLinkedClientsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating HTS linked clients", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalTestedFamily(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSFamilyContactsTestedCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total HTS family contacts tested", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalTestedIDU(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSIDUContactsTestedCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total HTS IDU contacts tested", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsUnknownStatusFamily(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSFamilyContactsUknownStatusCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total Unknown HIV status family contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsUnknownStatusPartner(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSPartnerContactsUknownStatusCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total Unknown HIV status Partner contacts", e);
        }
    }
    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsUnknownStatusIDU(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSIDUContactsUknownStatusCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total Unknown HIV status IDU contacts", e);
        }
    }
    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalTestedPartner(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSPartnerContactsTestedCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total HTS Partners contacts tested", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalPositivePartner(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSPositivePartnerContactsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total HTS HIV Positive Partner contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalPositiveIDU(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSPositiveIDUContactsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating total HTS HIV Positive IDU contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalPositiveFamily(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSPositiveFamilyContactsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating HTS HIV positive family contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalLinkedFamily(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSLinkedFamilyContactsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating HTS linked family contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalLinkedIDU(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSLinkedIDUContactsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating HTS linked IDU contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort htsTotalLinkedPartners(EvaluationContext context) {
        try {
            return getService().evaluate(new HTSLinkedPartnerContactsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating HTS linked Partner contacts", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableOver4Monthstca(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableOver4MonthstcaCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable patients with 4+ months tca", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableUnder4Monthstca(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableUnder4MonthstcaCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable patients with under months tca", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort unstablePatientsUnder15(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareUnstableUnder15YearsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating unstable patients aged under 15 years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort unstableFemalePatients15Plus(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareUnstableFemales15PlusYearsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating unstable female patients aged 15+ years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort unstableMalePatients15Plus(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareUnstableMales15PlusYearsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating unstable male patients aged 15+ years", e);
        }
    }

    /**
 * @param context optional (used to return a cached value if possible)
 * @return
 */
public static EvaluatedCohort currentInCareOnART(EvaluationContext context) {
    try {
        return getService().evaluate(new DiffCarecurrentInCareOnARTCohortDefinition(), context);
    } catch (EvaluationException e) {
        throw new IllegalStateException("Error evaluating current in care ART patients", e);
    }
}

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort currentInCareOnARTOver15Female(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCarecurrentInCareOnARTOver15FemaleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating current in care females over 15 years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort undocumentedPatientStability(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareUndocumentedStabilityCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating undocumented patient stability", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort currentInCareOnARTOver15Male(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCarecurrentInCareOnARTOver15MaleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating current in care males over 15 years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort currentInCareOnARTUnder15(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCarecurrentInCareOnARTUnder15CohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating current in care under 15 years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableOver4MonthstcaOver15Female(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableOver4MonthstcaOver15FemaleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable female patients with over 4 months prescription aged 15+ years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableOver4MonthstcaOver15Male(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableOver4MonthstcaOver15MaleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable male patients with over 4 months prescription aged 15+ years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableUnder4MonthstcaOver15Female(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableUnder4MonthstcaOver15FemaleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable female patients with under 4 months prescription aged 15+ years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableOver4MonthstcaUnder15(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableOver4MonthstcaUnder15CohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable children patients with over 4 months prescription aged below 15 years", e);
        }
    }
    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableUnder4MonthstcaOver15Male(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableUnder4MonthstcaOver15MaleCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable male patients with under 4 months prescription aged 15+ years", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort stableUnder4MonthstcaUnder15(EvaluationContext context) {
        try {
            return getService().evaluate(new DiffCareStableUnder4MonthstcaUnder15CohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating stable children patients with under 4 months prescription aged below 15 years", e);
        }
    }

    public EvaluatedCohort fullyVaccinated(EvaluationContext context) {
        try {
            return getService().evaluate(covid19VaccinationCohortLibrary.onArtFullyVaccinated(), context);
        } catch (EvaluationException var2) {
            throw new IllegalStateException("Error evaluating fully vaccinated patients", var2);
        }
    }

    public EvaluatedCohort partiallyVaccinated(EvaluationContext context) {
        try {
            return getService().evaluate(covid19VaccinationCohortLibrary.onArtPartiallyVaccinated(), context);
        } catch (EvaluationException var2) {
            throw new IllegalStateException("Error evaluating partially vaccinated patients", var2);
        }
    }

    public EvaluatedCohort notVaccinated(EvaluationContext context) {
        try {
            return getService().evaluate(covid19VaccinationCohortLibrary.onArtNotVaccinatedCovid19(), context);
        } catch (EvaluationException var2) {
            throw new IllegalStateException("Error evaluating never vaccinated patients", var2);
        }
    }

    public EvaluatedCohort everTestedCovid19(EvaluationContext context) {
        try {
            return getService().evaluate(covid19VaccinationCohortLibrary.onArtEverInfected(), context);
        } catch (EvaluationException var2) {
            throw new IllegalStateException("Error evaluating ever tested positive for Covid-19", var2);
        }
    }

    public EvaluatedCohort everHospitalizedOfCovid19(EvaluationContext context) {
        try {
            return getService().evaluate(covid19VaccinationCohortLibrary.everHospitalised(), context);
        } catch (EvaluationException var2) {
            throw new IllegalStateException("Error evaluating ever hospitalized for Covid-19", var2);
        }
    }

    public EvaluatedCohort diedOfCovid19(EvaluationContext context) {
        try {
            return getService().evaluate(covid19VaccinationCohortLibrary.diedDueToCovid(), context);
        } catch (EvaluationException var2) {
            throw new IllegalStateException("Error evaluating patients who died of Covid-19", var2);
        }
    }

    private static CohortDefinitionService getService() {
        return Context.getService(CohortDefinitionService.class);
    }
}

