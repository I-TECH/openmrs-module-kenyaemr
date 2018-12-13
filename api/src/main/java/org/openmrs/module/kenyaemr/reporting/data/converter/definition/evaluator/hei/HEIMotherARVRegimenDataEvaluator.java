package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIMotherARVRegimenDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEISerialNumberDataDefinition;
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
@Handler(supports= HEIMotherARVRegimenDataDefinition.class, order=50)
public class HEIMotherARVRegimenDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "  patient_id,\n" +
                "  (case mother_drug_regimen when 792 then \"D4T/3TC/NVP\" when 160124 then \"AZT/3TC/EFV\" when 160104 then \"D4T/3TC/EFV\" when 1652 then \"3TC/NVP/AZT\"\n" +
                "   when 161361 then \"EDF/3TC/EFV\" when 104565 then \"EFV/FTC/TDF\" when 162201 then \"3TC/LPV/TDF/r\" when 817 then \"ABC/3TC/AZT\"\n" +
                "   when 162199 then \"ABC/NVP/3TC\" when 162200 then \"3TC/ABC/LPV/r\" when 162565 then \"3TC/NVP/TDF\" when 1652 then \"3TC/NVP/AZT\"\n" +
                "   when 162561 then \"3TC/AZT/LPV/r\" when 164511 then \"AZT-3TC-ATV/r\" when 164512 then \"TDF-3TC-ATV/r\" when 162560 then \"3TC/D4T/LPV/r\"\n" +
                "   when 162563 then \"3TC/ABC/EFV\" when 162562 then \"ABC/LPV/R/TDF\" when 162559 then \"ABC/DDI/LPV/r\"  else \"\" end) as mother_drug_regimen\n" +
                "from kenyaemr_etl.etl_hei_enrollment\n" +
                "GROUP BY patient_id";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}