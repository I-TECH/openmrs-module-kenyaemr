package org.openmrs.module.kenyaemr.reporting.data.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.kenyaemr.reporting.data.definition.CountyAddressDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.definition.AddressObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluator for CountyAddressDataDefinition
 */
@Handler(supports =CountyAddressDataDefinition.class, order = 50)
public class PersonCountyAddressDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		CountyAddressDataDefinition def = (CountyAddressDataDefinition) definition;
		EvaluatedPersonData c = new EvaluatedPersonData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().isEmpty()) {
			return c;
		}

		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

		StringBuilder hql = new StringBuilder();
		hql.append("select person.id, country, countyDistrict");
		hql.append(" from PersonAddress");
		hql.append(" where");
		hql.append("   person.id in (:patientIds)");

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("patientIds", context.getBaseCohort());

		List<Object> queryResult = qs.executeHqlQuery(hql.toString(), m);


		for (Object o : queryResult) {
			Object[] parts = (Object[]) o;
			if (parts.length == 3) {
				Integer pId = (Integer) parts[0];
				String country = (String) parts[1];
				String countyDistrict = (String) parts[2];
				c.addData(pId, new AddressObject(country, countyDistrict));
			}
		}

		return c;
	}

}
