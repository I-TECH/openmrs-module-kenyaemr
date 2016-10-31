package org.openmrs.module.kenyaemr.fragment.controller.program.ipt;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.ipt.LastAdherenceMeasurementCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbPatientClassificationCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class IptCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient, @FragmentParam("complete") Boolean complete,
			FragmentModel model, @SpringBean RegimenManager regimenManager) {

		Map<String, Object> calculationResults = new HashMap<String, Object>();

		CalculationResult result = EmrCalculationUtils.evaluateForPatient(LastAdherenceMeasurementCalculation.class,
				null, patient);
		calculationResults.put("lastAdherenceMeasurement", result != null ? result.getValue() : null);

		result = EmrCalculationUtils.evaluateForPatient(TbPatientClassificationCalculation.class, null, patient);
		calculationResults.put("tbPatientClassification", result != null ? result.getValue() : null);
		String message = "None";

		model.addAttribute("calculations", calculationResults);
		model.addAttribute("result", message);

	}
}
