package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Program;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.InProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.OnCtxWithinDurationCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQAPatientsOnCTXCohortDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Evaluator for patients eligible for RDQAPatientsOnCTXCohortDefinition
 */
@Handler(supports = {RDQAPatientsOnCTXCohortDefinition.class})
public class RDQAPatientsOnCTXCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        RDQAPatientsOnCTXCohortDefinition definition = (RDQAPatientsOnCTXCohortDefinition) cohortDefinition;

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationCohortDefinition inHiv = new CalculationCohortDefinition(new InProgramCalculation());
        inHiv.setName("in " + hivProgram.getName() + " on date");
        inHiv.addParameter(new Parameter("onDate", "On Date", Date.class));
        inHiv.addCalculationParameter("program", hivProgram);

        //----------------------- ctx -------------------
        CodedObsCohortDefinition onCtx = new CodedObsCohortDefinition();
        onCtx.setName("on CTX prophylaxis");
        onCtx.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
        onCtx.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        onCtx.setTimeModifier(PatientSetService.TimeModifier.LAST);
        onCtx.setQuestion(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED));
        onCtx.setValueList(Arrays.asList(Dictionary.getConcept(Dictionary.YES)));
        onCtx.setOperator(SetComparator.IN);

        /**
         * Patients who were dispensed the given medications between ${onOrAfter} and ${onOrBefore}
         * @param concepts the drug concepts
         * @return the cohort definition
         */
        CodedObsCohortDefinition medDsp = new CodedObsCohortDefinition();
        medDsp.setName("dispensed medication between");
        medDsp.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
        medDsp.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        medDsp.setTimeModifier(PatientSetService.TimeModifier.ANY);
        medDsp.setQuestion(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS));
        medDsp.setValueList(Arrays.asList(Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM), Dictionary.getConcept(Dictionary.DAPSONE)));
        medDsp.setOperator(SetComparator.IN);

        /**
         * Patients who are on ctx on ${onDate}
         * @return the cohort definition
         */
        CalculationCohortDefinition onCtxDuration = new CalculationCohortDefinition(new OnCtxWithinDurationCalculation());
        onCtxDuration.setName("On CTX on date");
        onCtxDuration.addParameter(new Parameter("onDate", "On Date", Date.class));

        //---------------------------

        CompositionCohortDefinition onCtxProphylaxis = new CompositionCohortDefinition();
        onCtxProphylaxis.setName("Having CTX either dispensed");
        onCtxProphylaxis.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
        onCtxProphylaxis.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        onCtxProphylaxis.addSearch("onCtx", ReportUtils.map(onCtx, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        onCtxProphylaxis.addSearch("onMedCtx", ReportUtils.map(medDsp,"onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        onCtxProphylaxis.setCompositionString("onCtx OR onMedCtx");


        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.setName("in HIV program and on CTX prophylaxis");
        cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        cd.addSearch("inProgram", ReportUtils.map(inHiv, "onDate=${onOrBefore}"));
        cd.addSearch("onCtxProphylaxis", ReportUtils.map(onCtxProphylaxis, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
        cd.setCompositionString("inProgram AND onCtxProphylaxis");

        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        now.set(2002,1,1);
        Date startDate = now.getTime();

        context.addParameterValue("onOrAfter", startDate);
        context.addParameterValue("onOrBefore", today);

        Cohort patientsOnCtx = Context.getService(CohortDefinitionService.class).evaluate(cd, context);


        return new EvaluatedCohort(patientsOnCtx, definition, context);
    }


}
