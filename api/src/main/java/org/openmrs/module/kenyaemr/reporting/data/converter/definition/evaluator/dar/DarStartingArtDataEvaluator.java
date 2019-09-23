/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.dar;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarStartingArtDataDefinition;
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
 * Evaluates DarStartingArtDataDefinition to produce a VisitData
 */
@Handler(supports= DarStartingArtDataDefinition.class, order=50)
public class DarStartingArtDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        DarStartingArtDataDefinition def = (DarStartingArtDataDefinition) definition;
        Integer minAge = def.getMinAge();
        Integer maxAge = def.getMaxAge();
        String sex = def.getSex();
        String qry = "";
        if (sex != null) {
            qry = "select patient_id, 'X' as newOnArt\n" +
                    "from \n" +
                    "(select e.patient_id,\n" +
                    "        e.date_started,\n" +
                    "        p.DOB as DOB,\n" +
                    " max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art\n" +
                    " from\n" +
                    "      (select e.patient_id, min(e.date_started) as date_started\n" +
                    "        from kenyaemr_etl.etl_drug_event e\n" +
                    "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                    "        where e.program = 'HIV'\n" +
                    "        group by e.patient_id) e\n" +
                    " inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                    " inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0 and p.Gender = ':sex'\n" +
                    " inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = e.patient_id and  date(f.visit_date) = date(:startDate) and f.voided = 0\n" +
                    " where date(e.date_started) = date(:startDate) \n" +
                    " group by e.patient_id\n" +
                    " having TI_on_art=0) a ";
        } else {
            qry = "select patient_id, 'X' as newOnArt\n" +
                    "from \n" +
                    "(select e.patient_id,\n" +
                    "        e.date_started,\n" +
                    "        p.DOB as DOB,\n" +
                    " max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art\n" +
                    " from\n" +
                    "      (select e.patient_id, min(e.date_started) as date_started\n" +
                    "        from kenyaemr_etl.etl_drug_event e\n" +
                    "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                    "        where e.program = 'HIV'\n" +
                    "        group by e.patient_id) e\n" +
                    " inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                    " inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0 \n" +
                    " inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = e.patient_id and  date(f.visit_date) = date(:startDate) and f.voided = 0\n" +
                    " where date(e.date_started) = date(:startDate) \n" +
                    " group by e.patient_id\n" +
                    " having TI_on_art=0) a ";
        }
        String ageConditionString = "";
        if (minAge != null && maxAge != null) {
            ageConditionString = " WHERE  TIMESTAMPDIFF(YEAR, date(DOB), date(:startDate)) between :minAge and :maxAge ";
        } else if (minAge != null) {
            ageConditionString = " WHERE TIMESTAMPDIFF(YEAR, date(DOB), date(:startDate)) >= :minAge ";
        } else if (maxAge != null) {
            ageConditionString = " WHERE TIMESTAMPDIFF(YEAR, date(DOB), date(:startDate)) <= :maxAge ";
        }
        qry = qry.concat(ageConditionString);

        if (maxAge != null) {
            qry = qry.replace(":maxAge", maxAge.toString());
        }

        if (minAge != null) {
            qry = qry.replace(":minAge", minAge.toString());
        }

        if (sex != null) {
            qry = qry.replace(":sex", sex);
        }
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Date startDate = (Date)context.getParameterValue("startDate");
        queryBuilder.addParameter("startDate", startDate);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}