package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICommentsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIHIVStatusMonth24DataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PersonDataDefinition
 */
@Handler(supports= HEIHIVStatusMonth24DataDefinition.class, order=50)
public class HEIHIVStatusMonth24DataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "  f.patient_id,\n" +
                "  mid(max(concat(visit_date, (case when final_antibody_result = 664 and f.infant_feeding = 5526 then \"Uninfected_Breastfed (UBF)\"\n" +
                "       when final_antibody_result = 664 and infant_feeding = 1595 then \"Uninfected_not Breastfed (UBFn)\"\n" +
                "       when final_antibody_result = 664 and infant_feeding = \"\" then \"Uninfected_Breastfed Uknown (UBFu)\"\n" +
                "       when final_antibody_result = 703 and infant_feeding = 5526 then \"Infected_Breastfed (IBF)\"\n" +
                "       when final_antibody_result = 703 and infant_feeding = 1595 then \"Infected_not Breastfed (UBFn)\"\n" +
                "       when final_antibody_result = 703 and infant_feeding = \"\" then \"Infected_Breastfed Uknown (UBFu)\" else \"\" end))),11) as HIV_status_24_months\n" +
                "from  kenyaemr_etl.etl_hei_follow_up_visit f\n" +
                "\n" +
                "  INNER JOIN etl_patient_demographics d ON\n" +
                "  f.patient_id = d.patient_id\n" +
                "WHERE  round(DATEDIFF(f.visit_date,d.DOB)/7) BETWEEN 72 AND 96\n" +
                "GROUP BY f.patient_id";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}