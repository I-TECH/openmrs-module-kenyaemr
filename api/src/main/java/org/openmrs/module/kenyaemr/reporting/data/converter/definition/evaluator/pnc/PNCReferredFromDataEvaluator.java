package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCPulseDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCReferredFromDataDefinition;
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
 * PNC referred from column
 */
@Handler(supports= PNCReferredFromDataDefinition.class, order=50)
public class PNCReferredFromDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "(case v.referred_from when 1537 then \"Another Health Facility\" when 163488 then \"Community Unit\"\n" +
                "when 1175 then \"N/A\" else \"\" end) as referred_from\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v inner join kenyaemr_etl.etl_mch_enrollment e\n" +
                "on v.patient_id = e.patient_id and e.date_of_discontinuation IS NULL\n" +
                "GROUP BY v.encounter_id;\n";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
