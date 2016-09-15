package org.openmrs.module.kenyaemr.fragment.controller.program.ipt;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.wrapper.Enrollment;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Patient program enrollment fragment
 */
public class IptEnrollmentSummaryFragmentController {

	public String controller(@FragmentParam("patientProgram") PatientProgram patientProgram,
			@FragmentParam(value = "encounter", required = false) Encounter encounter,
			@FragmentParam("showClinicalData") boolean showClinicalData, FragmentModel model) {

		Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();
		dataPoints.put("Initiated", patientProgram.getDateEnrolled());

		Enrollment enrollment = new Enrollment(patientProgram);

		Obs o = enrollment.firstObs(Dictionary.getConcept(Dictionary.INDICATION_FOR_TB_PROPHYLAXIS));
		if (o != null) {
			dataPoints.put("Indication for IPT", o.getValueCoded());
		}

		model.put("dataPoints", dataPoints);
		return "view/dataPoints";
	}
}
