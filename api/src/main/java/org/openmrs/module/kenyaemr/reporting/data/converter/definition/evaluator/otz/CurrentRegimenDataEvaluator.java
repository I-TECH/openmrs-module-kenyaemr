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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.otz.CurrentARTRegimenDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.otz.CurrentRegimenLineDataDefinition;
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
@Handler(supports= CurrentARTRegimenDataDefinition.class, order=50)
public class CurrentRegimenDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select  otzenr.patient_id,net.art_start_date_regimen as art_start_date_regimen from kenyaemr_etl.etl_otz_enrollment otzenr\n" +
                "                                                                                      left outer join\n" +
                "                                                                                        (\n" +
                "                                                                                        select e.patient_id,\n" +
                "                                                                                               if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "                                                                                               if(max(enr.date_started_art_at_transferring_facility) is not null,(mid(max(concat(enr.date_started_art_at_transferring_facility,e.latest_regimen)),11)),concat_ws('\\r\\n',mid(max(concat(e.latest_regimen_date,e.latest_regimen)),11),e.latest_regimen_date)) as art_start_date_regimen\n" +
                "                                                                                                                                                                from (select e.patient_id,max(e.date_started) as latest_regimen_date,\n" +
                "                                                                                                       mid(max(concat(e.visit_date,e.regimen)),11) as latest_regimen\n" +
                "                                                                                              from kenyaemr_etl.etl_drug_event e\n" +
                "                                                                                                     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                                                                                              group by e.patient_id) e\n" +
                "                                                                                               left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "                                                                                               left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "                                                                                               left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "                                                                                        group by e.patient_id\n" +
                "                                                                                        having TOut = 0\n" +
                "                                                                                        )net on otzenr.patient_id = net.patient_id;";

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
