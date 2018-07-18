/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.reporting.query.patientContact.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.visit.VisitDataUtil;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.AllVisitQuery;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.openmrs.module.reporting.query.visit.evaluator.VisitQueryEvaluator;

/**
 * The logic that evaluates a {@link AllVisitQuery} and produces an {@link Query}
 */
@Handler(supports=AllVisitQuery.class)
public class AllVisitQueryEvaluator implements VisitQueryEvaluator {

    /**
     * @see VisitQueryEvaluator#evaluate(Definition, EvaluationContext)
     * @should return all of the visit ids for all patients in the defined query
     * @should filter results by patient and visit given an VisitEvaluationContext
     * @should filter results by patient given an EvaluationContext
     */
    public VisitQueryResult evaluate(VisitQuery definition, EvaluationContext context) throws EvaluationException {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        VisitQueryResult queryResult = new VisitQueryResult(definition, context);
        queryResult.setMemberIds(VisitDataUtil.getVisitIdsForContext(context, false));
        return queryResult;
    }
}
