package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Returns concept names
 */
public class PatientEntryPointDataConverter implements DataConverter {

	private Log log = LogFactory.getLog(getClass());

	public PatientEntryPointDataConverter() {}

	/**
	 * @should return a pre-established labels for patient entry point
	 */
	@Override
	public Object convert(Object original) {



		Obs o = (Obs) original;

		if (o == null)
			return "Missing";

		Concept answer = o.getValueCoded();

		if (answer == null)
			return "Missing";

		String label = "Missing";
		switch (answer.getId()){
			case 160539:
				label = "VCT";
				break;
			case 160538:
				label = "PMTCT";
				break;
			case 160537:
				label = "IPD-P";
				break;
			case 160536:
				label = "IPD-A";
				break;
			case 160542:
				label = "OPD";
				break;
			case 160541:
				label = "TB";
				break;
			case 160543:
				label = "CBO";
				break;
			case 160544:
				label = "MCH-Child";
				break;
			case 160546:
				label = "STI";
				break;
			case 160548:
				label = "IDU";
				break;
			case 159937:
				label = "MCH";
				break;
			case 162223:
				label = "VMMC";
				break;
			case 160563:
				label = "TI";
				break;
			default:
				label = "OTHER";

		}

		return label;
	}

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}



}
