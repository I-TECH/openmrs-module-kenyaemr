package org.openmrs.module.kenyaemr.fragment.controller.program.ipt;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class IptOutcomeSummaryFragmentController {

	public String controller(@FragmentParam("patientProgram") PatientProgram enrollment,
			@FragmentParam(value = "encounter", required = false) Encounter encounter,
			@FragmentParam("showClinicalData") boolean showClinicalData, FragmentModel model) {

		Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();

		dataPoints.put("Completed", enrollment.getDateCompleted());

		if (showClinicalData && enrollment.getOutcome() != null) {
			dataPoints.put("Outcome", enrollment.getOutcome());
		}

		if (encounter != null) {
			EncounterWrapper wrapper = new EncounterWrapper(encounter);

			Obs outcomeObs = wrapper.firstObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_PROPHYLAXIS_OUTCOME));
			if (outcomeObs != null) {
				dataPoints.put("Outcome", outcomeObs.getValueCoded());
			}
		}

		model.put("dataPoints", dataPoints);
		return "view/dataPoints";
	}
}
