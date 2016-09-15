package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;

public class ScheduledVisitOnDayCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		Date date = (Date) parameterValues.get("date");
		if (date == null) {
			date = new Date();
		}

		Date startOfDay = DateUtil.getStartOfDay(date);
		Date endOfDay = DateUtil.getEndOfDay(date);
		Concept returnVisitDate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
		EncounterType tbFollowup = MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_CONSULTATION);

		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setTimeModifier(TimeModifier.ANY);
		cd.setQuestion(returnVisitDate);
		cd.setOperator1(RangeComparator.GREATER_EQUAL);
		cd.setValue1(startOfDay);
		cd.setOperator2(RangeComparator.LESS_EQUAL);
		cd.setValue2(endOfDay);
		cd.setEncounterTypeList(Collections.singletonList(tbFollowup));

		EvaluatedCohort withScheduledVisit = CalculationUtils.evaluateWithReporting(cd, cohort, null, context);

		CalculationResultMap ret = new CalculationResultMap();
		
		for (Integer ptId : cohort) {
			ret.put(ptId, new BooleanResult(withScheduledVisit.contains(ptId), this));
		}
		
		return ret;

	}

}
