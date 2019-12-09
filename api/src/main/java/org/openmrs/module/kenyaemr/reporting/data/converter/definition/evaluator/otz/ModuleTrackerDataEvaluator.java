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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.otz.ModuleTrackerDataDefinition;
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
@Handler(supports= ModuleTrackerDataDefinition.class, order=50)
public class ModuleTrackerDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select e.patient_id,concat_ws(',',if(coalesce(e.orientation,a.orientation) = 'Yes',1,null),if(coalesce(e.leadership,a.leadership) = 'Yes',2,null),if(coalesce(e.participation,a.participation) = 'Yes',3,null),if(coalesce(e.treatment_literacy,a.treatment_literacy) = 'Yes',4,null),if(coalesce(e.transition_to_adult_care,a.transition_to_adult_care) = 'Yes',5,null),if(coalesce(e.making_decision_future,a.making_decision_future) = 'Yes',6,null),if(a.srh = 'Yes',7,null),if(coalesce(e.beyond_third_ninety,a.beyond_third_ninety) = 'Yes',8,null)) as modules_completed\n" +
                "from kenyaemr_etl.etl_otz_enrollment e join kenyaemr_etl.etl_otz_activity a on e.patient_id = a.patient_id group by e.patient_id;";

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
