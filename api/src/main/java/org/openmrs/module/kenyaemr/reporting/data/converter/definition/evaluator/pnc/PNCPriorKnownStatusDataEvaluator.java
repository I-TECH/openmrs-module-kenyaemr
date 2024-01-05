/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCPriorKnownStatusDataDefinition;
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
 * PNC prior Known status column
 */
@Handler(supports= PNCPriorKnownStatusDataDefinition.class, order=50)
public class PNCPriorKnownStatusDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select a.enc_id,\n" +
                "       if(a.mother_hiv_status != 'Known Positive', a.pnv_status,a.mother_hiv_status) as status\n" +
                "from (select e.mother_hiv_status, v.pnv_status, e.patient_id,v.enc_id\n" +
                "      from (select e.patient_id,\n" +
                "                   (case e.hiv_status\n" +
                "                        when 1067 then 'Unknown'\n" +
                "                        when 664 then 'Negative'\n" +
                "                        when 703 then 'Known Positive'\n" +
                "                       end) as mother_hiv_status\n" +
                "            from kenyaemr_etl.etl_mch_enrollment e\n" +
                "            where date(e.visit_date) <= date(:endDate)) e\n" +
                "               inner join (select v.patient_id,v.encounter_id as enc_id,\n" +
                "                                  case v.mother_hiv_status\n" +
                "                                      when 1067 then 'Unknown'\n" +
                "                                      when 703 then 'Positive'\n" +
                "                                      when 664 then 'Negative' end as pnv_status\n" +
                "                           from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "                           where date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
                "                          on v.patient_id = e.patient_id) a;";

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
