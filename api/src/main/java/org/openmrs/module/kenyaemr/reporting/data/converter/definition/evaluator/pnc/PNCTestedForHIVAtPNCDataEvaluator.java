package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCTemperatureDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCTestedForHIVAtPNCDataDefinition;
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
 * PNC tested for HIV at PNC column
 */
@Handler(supports= PNCTestedForHIVAtPNCDataDefinition.class, order=50)
public class PNCTestedForHIVAtPNCDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "       (case  when (test_1_result is not null and test_2_result is null and v.final_test_result is null)\n" +
                "                  then \"Initial\" when (test_1_result is not null and test_2_result is not null ) then \"Repeat\"\n" +
                "           when (test_1_result is null and test_2_result is null and final_test_result is null) then \"Not Done\" else \"NA\" end) as Tested_for_hiv_at_pnc\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v inner join kenyaemr_etl.etl_mch_enrollment e\n" +
                "         on v.patient_id = e.patient_id and e.date_of_discontinuation IS NULL\n" +
                "GROUP BY v.encounter_id;\n";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
