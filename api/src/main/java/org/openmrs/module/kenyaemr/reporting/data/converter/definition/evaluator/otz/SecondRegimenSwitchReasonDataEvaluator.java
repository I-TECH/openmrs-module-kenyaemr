/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.otz;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.otz.SecondRegimenSwitchDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.otz.SecondRegimenSwitchReasonDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates a OnARTDataDefinition
 */
@Handler(supports= SecondRegimenSwitchReasonDataDefinition.class, order=50)
public class SecondRegimenSwitchReasonDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select t.patient_id,t.reason\n" +
                "from (select t.*,\n" +
                "             (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                        if(@v := patient_id, 1, 1)\n" +
                "                 )\n" +
                "                 ) as rn\n" +
                "      from (select t.patient_id,t.date_started,t.regimen,t.regimen_line,max(e.visit_date) as latest_otz_enr,\n" +
                "                   coalesce((case t.reason_discontinued\n" +
                "                                               when 102 then 'Drug toxicity'\n" +
                "                                               when 160561 then 'New drug available'\n" +
                "                                               when 160567 then 'New diagnosis of Tuberculosis'\n" +
                "                                               when 160569 then 'Virologic failure'\n" +
                "                                               when 159598 then 'Non-compliance with treatment or therapy'\n" +
                "                                               when 1754 then 'Medications unavailable'\n" +
                "                                               when 1434 then 'Currently pregnant'\n" +
                "                                               when 1253 then 'Completed PMTCT'\n" +
                "                                               when 843 then 'Regimen failure'\n" +
                "                                               when 5622 then 'Other'\n" +
                "                                               else '' end), t.reason_discontinued_other) as reason from kenyaemr_etl.etl_otz_enrollment e\n" +
                "                                                            left join kenyaemr_etl.etl_drug_event t on e.patient_id = t.patient_id where t.program = 'HIV'\n" +
                "            group by t.date_started,e.patient_id\n" +
                "            having t.date_started >= latest_otz_enr) t cross join\n" +
                "               (select @v := -1, @rn := 0) params\n" +
                "      order by t.patient_id, t.date_started asc\n" +
                "     ) t\n" +
                "where rn=2;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
