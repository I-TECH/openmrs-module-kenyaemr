package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCHAARTForMotherGreaterThan6WeeksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCHIVResultsGreaterThan6WeeksDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * PNC HIV results >6 weeks column
 */
@Handler(supports= PNCHIVResultsGreaterThan6WeeksDataDefinition.class, order=50)
public class PNCHIVResultsGreaterThan6WeeksDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "       (case v.final_test_result when 703 then \"Positive\" when 664 then \"Negative\"\n" +
                "                                 when 1138 then \"Unknown\" else \"NA\" end) as HIV_Results_in_pnc_Within_6Weeks\n" +
                "from etl_mch_postnatal_visit v\n" +
                "       join kenyaemr_etl.etl_mchs_delivery d on d.patient_id = v.patient_id\n" +
                "where timestampdiff(week,d.date_of_delivery,date(v.visit_date))>6";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
