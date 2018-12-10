package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCHAARTForMotherWithin6WeeksDataDefinition;
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
 * PNC HAART for the mother <=6 weeks column
 */
@Handler(supports= PNCHAARTForMotherWithin6WeeksDataDefinition.class, order=50)
public class PNCHAARTForMotherWithin6WeeksDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select d.encounter_id,\n" +
                "                (case de.date_started when \"\" then \"No\" else \"Yes \" end)as HAART_For_Mother_At_PNC_Over_6Weeks\n" +
                "                from kenyaemr_etl.etl_drug_event de\n" +
                "                join kenyaemr_etl.etl_mchs_delivery d on d.patient_id = de.patient_id\n" +
                "                where timestampdiff(week,d.date_of_delivery,date(de.date_started))<=6;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
