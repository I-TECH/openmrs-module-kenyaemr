package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.AppointmentsCheckedInCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.AppointmentsDailyScheduleCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.AppointmentsUnscheduledCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.CumulativeOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLCurrentOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLCurrentOnCareCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLNewHivEnrollmentCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLNewOnARTCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLPatientsWithVLInLast12MonthsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSLinkedClientsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSPositiveResultsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731CohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by antony on 09/08/17.
 */
public class DashBoardCohorts {

    @Autowired
    private ETLMoh731CohortLibrary moh731Cohorts;

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
            return getService().evaluate(new AppointmentsCheckedInCohortDefinition(), context);
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
    private static CohortDefinitionService getService() {
        return Context.getService(CohortDefinitionService.class);
    }
}
