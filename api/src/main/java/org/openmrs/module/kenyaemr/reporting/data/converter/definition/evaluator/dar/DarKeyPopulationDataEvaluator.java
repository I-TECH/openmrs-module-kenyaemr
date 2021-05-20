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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarKeyPopulationDataDefinition;
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
 * Evaluates DarKeyPopulationDataDefinition to produce a VisitData
 */
@Handler(supports= DarKeyPopulationDataDefinition.class, order=50)
public class DarKeyPopulationDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        DarKeyPopulationDataDefinition def = (DarKeyPopulationDataDefinition) definition;
        String section = def.getSection();
        String qry = "";

        if (section.equals("Enrollment")) {
            qry = "SELECT e.patient_id, 'X' kp\n" +
                    "FROM kenyaemr_etl.etl_hiv_enrollment e\n" +
                    "         inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0 \n" +
                    "         inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = e.patient_id and  date(f.visit_date) = date(:startDate) and f.voided = 0 and f.population_type=164929 \n" +
                    "where e.entry_point <> 160563  and e.transfer_in_date is null and (e.patient_type not in (160563, 164931, 159833) or e.patient_type is null ) and date(e.visit_date) = date(:startDate) and e.voided = 0 ";
        } else if (section.equals("Starting ART")) {
            qry = "select patient_id, 'X' as kp\n" +
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
                    " inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = e.patient_id and  date(f.visit_date) = date(:startDate) and f.voided = 0 and f.population_type=164929 \n" +
                    " where date(e.date_started) = date(:startDate) \n" +
                    " group by e.patient_id\n" +
                    " having TI_on_art=0) a ";

        } else if (section.equals("On ART")) {
            qry = "SELECT f.patient_id, if(f2.patient_id is not null,\n" +
                    "    TIMESTAMPDIFF(MONTH , date(f.visit_date), date(f.next_appointment_date)), 'R' ) as kp\n" +
                    "FROM kenyaemr_etl.etl_patient_hiv_followup f\n" +
                    "         inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = f.patient_id and  p.voided = 0 \n" +
                    "         inner join kenyaemr_etl.etl_drug_event d on d.patient_id = f.patient_id  and  date(d.date_started) <= date(:startDate)  and ifnull(d.voided,0)= 0\n" +
                    "         left join kenyaemr_etl.etl_patient_hiv_followup f2 on f.patient_id = f2.patient_id  and  date(f.visit_date) = date(f2.next_appointment_date) and f2.voided = 0\n" +
                    "where date(f.visit_date) = date(:startDate) and f.voided = 0 and f.population_type=164929";
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