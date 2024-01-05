/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.art;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSTestEntryPointDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLHTSEntryPointDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLPredictionScoreDataDefinition;
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
 * Evaluates  HTS Entry point Data Definition
 */
@Handler(supports= ETLHTSEntryPointDataDefinition.class, order=50)
public class ETLHTSEntryPointDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "SELECT patient_id, (case  hts_entry_point\n" +
                "                    when 5485 then 'In Patient Department(IPD)'\n" +
                "                    when 160542 then 'Out Patient Department(OPD)'\n" +
                "                    when 162181 then 'Peadiatric Clinic'\n" +
                "                    when 160552 then 'Nutrition Clinic'\n" +
                "                    when 160538 then 'PMTCT ANC'\n" +
                "                    when 160456 then 'PMTCT MAT'\n" +
                "                    when 1623 then 'PMTCT PNC'\n" +
                "                    when 160541 then 'TB'\n" +
                "                    when 162050 then 'CCC'\n" +
                "                    when 159940 then 'VCT'\n" +
                "                    when 159938 then 'Home Based Testing'\n" +
                "                    when 159939 then 'Mobile Outreach'\n" +
                "                    when 162223 then 'VMMC'\n" +
                "                    when 160546 then 'STI Clinic'\n" +
                "                    when 160522 then 'Emergency'\n" +
                "                    when 163096 then 'Community Testing'\n" +
                "                    when 5622 then 'Other'\n" +
                "                    else ''  end ) as hts_entry_point\n" +
                "FROM kenyaemr_etl.etl_hts_eligibility_screening\n" +
                "where date(visit_date) >= date(:startDate) and date(visit_date) <= date(:endDate)\n" +
                "GROUP BY patient_id;";

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
