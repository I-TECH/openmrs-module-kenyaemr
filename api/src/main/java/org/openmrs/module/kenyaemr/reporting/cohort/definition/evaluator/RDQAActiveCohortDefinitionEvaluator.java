/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.RDQAMetadata;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQAActiveCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Evaluator for active patients eligible for RDQA
 */
@Handler(supports = {RDQAActiveCohortDefinition.class})
public class RDQAActiveCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		RDQAActiveCohortDefinition definition = (RDQAActiveCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry = "select distinct FLOOR(1 + (RAND() * 999999)) as index_no, active.patient_id\n" +
				"from\n" +
				"(\n" +
				"select  e.patient_id\n" +
				" from ( \n" +
				" select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
				" max(fup.visit_date) as latest_vis_date,\n" +
				" mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
				"   max(d.visit_date) as date_discontinued,\n" +
				"   d.patient_id as disc_patient \n" +
				" from kenyaemr_etl.etl_patient_hiv_followup fup \n" +
				" join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id \n" +
				" join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id \n" +
				" left outer JOIN\n" +
				"   (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
				"   where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
				"   group by patient_id \n" +
				"   ) d on d.patient_id = fup.patient_id \n" +
				" where fup.visit_date <= date(:endDate) \n" +
				" group by patient_id \n" +
				" having ((date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or \n" +
				" (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
				" ) e\n" +
				" ) active ";

		Map<String, Object> m = new HashMap<String, Object>();
		Calendar calendar = Calendar.getInstance();
		int thisMonth = calendar.get(calendar.MONTH);

		Map<String, Date> dateMap = EmrReportingUtils.getReportDates(thisMonth - 1);
		Date startDate = dateMap.get("startDate");
		Date endDate = dateMap.get("endDate");
		m.put("startDate", startDate);
		m.put("endDate", endDate);
		TreeMap<Double, Integer> dataMapFromSQL = (TreeMap<Double, Integer>) makePatientDataMapFromSQL(qry, m);

		if (dataMapFromSQL !=null){
			Integer allPatients = dataMapFromSQL.size();
			SampleSizeConfiguration conf = getSampleConfiguration();
			Integer requiredPatients = getSampleSize(allPatients, conf);

			int i =0;
			for (Double rand : dataMapFromSQL.keySet()){
				if (i < requiredPatients){
					newCohort.addMember(dataMapFromSQL.get(rand));
					i++;
				}
				else {
					break;
				}
			}
		}
        return new EvaluatedCohort(newCohort, definition, context);
    }

	private Integer getSampleSize(Integer totalPatients, SampleSizeConfiguration configuration){

		Integer lowestBoundary = configuration.getFirst();
		Map<Integer, Integer> upperMostBoundary = configuration.getLast();
		Map<String, Integer> otherLevels = configuration.getMiddleLevels();
		Integer size = 0;

		Integer upperKey = new ArrayList<Integer>(upperMostBoundary.keySet()).get(0);
		Integer upperValue = upperMostBoundary.get(upperKey);

		if (totalPatients <= lowestBoundary){
			size = totalPatients;
		} else if (totalPatients >= upperKey){
			size = upperValue;
		}
		else {
			size = processMiddleLevels(totalPatients, otherLevels);
		}
		return size;
	}

	private Integer processMiddleLevels(Integer val, Map<String, Integer> otherLevels){
		Integer size = 0;
		for (String key : otherLevels.keySet()){
			String rawKey[] = key.split("-");
			Integer lower = Integer.valueOf(rawKey[0]);
			Integer upper = Integer.valueOf(rawKey[1]);
			Integer sampleSize = otherLevels.get(key);

			if (lower !=null && upper !=null){
				if (val >= lower && val <= upper){
					size = sampleSize;
					break;
				}
			}
		}
		return size;
	}

	private SampleSizeConfiguration getSampleConfiguration(){


		String sampleSizeConf = Context.getAdministrationService().getGlobalProperty(RDQAMetadata.RDQA_DEFAULT_SAMPLE_CONFIGURATION);

		if (sampleSizeConf == null)
			return new SampleSizeConfiguration();

		String confArray[] = sampleSizeConf.split(",");

		SampleSizeConfiguration configuration = new SampleSizeConfiguration();
		Map<String, Integer> middleLevels = new HashMap<String, Integer>();

		for (int i = 0; i < confArray.length; i++){
			int len = confArray.length-1;
			if (i ==0) {
				configuration.setFirst(Integer.parseInt(confArray[0]));
			} else if (i == len) {
				String lastItemArr[] = confArray[len].split(":");
				Map<Integer, Integer> lastItem = new HashMap<Integer, Integer>();

				Integer key = Integer.valueOf(lastItemArr[0]);
				Integer value = Integer.valueOf(lastItemArr[1]);
				lastItem.put(key, value);
				configuration.setLast(lastItem);
			} else{

				String itemArr[] = confArray[i].split(":");
				String key = itemArr[0];
				Integer value = Integer.valueOf(itemArr[1]);

				middleLevels.put(key, value);
			}
		}
		configuration.setMiddleLevels(middleLevels);
		return configuration;
	}

	class SampleSizeConfiguration{
		private Integer first;
		private Map<Integer, Integer> last;
		private Map<String, Integer> middleLevels;

		public Integer getFirst() {
			return first;
		}

		public void setFirst(Integer first) {
			this.first = first;
		}

		public Map<Integer, Integer> getLast() {
			return last;
		}

		public void setLast(Map<Integer, Integer> last) {
			this.last = last;
		}

		public Map<String, Integer> getMiddleLevels() {
			return middleLevels;
		}

		public void setMiddleLevels(Map<String, Integer> middleLevels) {
			this.middleLevels = middleLevels;
		}
	}

	//======================================= data extraction methods =============================================

	protected Map<Double, Integer> makePatientDataMapFromSQL(String sql, Map<String, Object> substitutions) {
		List<Object> data = Context.getService(KenyaEmrService.class).executeSqlQuery(sql, substitutions);

		return makePatientDataMap(data);
	}

	protected Map<Double, Integer> makePatientDataMap(List<Object> data) {
		Map<Double, Integer> dataTreeMap = new TreeMap<Double, Integer>();
		for (Object o : data) {
			Object[] parts = (Object[]) o;
			if (parts.length == 2) {
				Double rand = (Double) parts[0];
				Integer pid = (Integer) parts[1];
				dataTreeMap.put(rand, pid);
			}
		}

		return dataTreeMap;
	}
}
