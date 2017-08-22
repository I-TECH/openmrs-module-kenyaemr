package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PatientDisabilityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PatientDisabilityTypeDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=PatientDisabilityTypeDataDefinition.class, order=50)
public class PatientDisabilityTypeDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select e.encounter_id, \n" +
                "(case o.value_coded \n" +
                "\twhen 1175 then \"Not applicable\" \n" +
                "\twhen 120291 then \"Deaf\" \n" +
                "\twhen 147215 then \"Blind\" \n" +
                "\twhen 151342 then \"Mentally Challenged\"\n" +
                "\twhen 1164538 then \"Physically Challenged\" \n" +
                "\twhen 5622 then \"Other\"\n" +
                "\telse \"\" end) disability_type\n" +
                "from  \n" +
                " encounter e  \n" +
                "inner join obs o on o.encounter_id = e.encounter_id and o.voided=0 \n" +
                "where o.concept_id = 162558 ";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
