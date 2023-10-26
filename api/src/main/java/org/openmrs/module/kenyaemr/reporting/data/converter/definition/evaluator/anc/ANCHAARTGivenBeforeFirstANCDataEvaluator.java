/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.anc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCHAARTGivenBeforeFirstANCDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates ANC HAART Given Before first ANC
 */
@Handler(supports= ANCHAARTGivenBeforeFirstANCDataDefinition.class, order=50)
public class ANCHAARTGivenBeforeFirstANCDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "       if(coalesce(d.date_started, e.ti_date_started_art) is not null, 'Yes'\n" +
                "           , 'No') as on_arv_before_first_anc\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "         inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id\n" +
                "         left join (select d.patient_id, min(d.date_started) as date_started, d.program as program\n" +
                "                    from kenyaemr_etl.etl_drug_event d\n" +
                "                    group by d.patient_id) d on d.patient_id = v.patient_id and d.program = 'HIV'\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "    and (d.date_started < e.visit_date\n" +
                "   or e.ti_date_started_art is not null);";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
