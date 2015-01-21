package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converter to get obsDatetime from an observation
 */
public class RegimenConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		SimpleResult sr = (SimpleResult) original;

		if (sr == null)
			return "NA";

		RegimenOrder ro = (RegimenOrder) sr.getValue();

		if (CollectionUtils.isEmpty(ro.getDrugOrders())) {
			return "NA";
		}
		List<String> components = new ArrayList<String>();

		for (DrugOrder o : ro.getDrugOrders()) {
			ConceptName cn = o.getConcept().getPreferredName(CoreConstants.LOCALE);
			if (cn == null) {
				cn = o.getConcept().getName(CoreConstants.LOCALE);
			}
			components.add(cn.getName());
		}

		return getRegimenName(standardRegimens(), components);

	}

	@Override
	public Class<?> getInputDataType() {
		return SimpleResult.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	private String getRegimenName(Map<String, List<String>> standardRegimens, List<String> drugs){
		if (standardRegimens.size() ==0 )
			return null;

		if (drugs.size() == 0)
			return null;
		String regimen = null;

		for (String key : standardRegimens.keySet()){
			List<String> value = standardRegimens.get(key);
				if (value.containsAll(drugs)) {
					regimen = key;
					break;
				}

		}
		return regimen;
	}

	private Map<String, List<String>> standardRegimens(){

		Map<String, List<String>> listMap = new HashMap<String, List<String>>();
		listMap.put("AZT+3TC+NVP", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "NEVIRAPINE" ));
		listMap.put("AZT+3TC+EFV", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "EFAVIRENZ" ));
		listMap.put("AZT+3TC+ABC", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "ABACAVIR" ));

		listMap.put("TDF+3TC+NVP", Arrays.asList("TENOFOVIR","LAMIVUDINE", "NEVIRAPINE" ));
		listMap.put("TDF+3TC+EFV", Arrays.asList("TENOFOVIR","LAMIVUDINE", "EFAVIRENZ" ));
		listMap.put("TDF+3TC+AZT", Arrays.asList("TENOFOVIR","LAMIVUDINE", "ZIDOVUDINE" ));

		listMap.put("d4T+3TC+NVP", Arrays.asList("STAVUDINE","LAMIVUDINE", "NEVIRAPINE" ));
		listMap.put("d4T+3TC+EFV", Arrays.asList("STAVUDINE","LAMIVUDINE", "EFAVIRENZ" ));
		listMap.put("d4T+3TC+ABC", Arrays.asList("STAVUDINE","LAMIVUDINE", "ABACAVIR" ));

		listMap.put("AZT+3TC+LPV/r", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
		listMap.put("AZT+3TC+ATV/r", Arrays.asList("ZIDOVUDINE","LAMIVUDINE", "ATAZANAVIR", "RITONAVIR" ));
		listMap.put("TDF+3TC+LPV/r", Arrays.asList("TENOFOVIR","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
		listMap.put("TDF+ABC+LPV/r", Arrays.asList("TENOFOVIR","ABACAVIR", "LOPINAVIR", "RITONAVIR" ));
		listMap.put("TDF+3TC+ATV/r", Arrays.asList("TENOFOVIR","LAMIVUDINE", "ATAZANAVIR", "RITONAVIR" ));
		listMap.put("ABC+ddI+LPV/r", Arrays.asList("ABACAVIR","DIDANOSINE", "LOPINAVIR", "RITONAVIR" ));
		listMap.put("d4T+3TC+LPV/r", Arrays.asList("STAVUDINE","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));

		listMap.put("ABC+3TC+NVP", Arrays.asList("ABACAVIR","LAMIVUDINE", "NEVIRAPINE" ));
		listMap.put("ABC+3TC+EFV", Arrays.asList("ABACAVIR","LAMIVUDINE", "EFAVIRENZ" ));
		listMap.put("ABC+3TC+AZT", Arrays.asList("ABACAVIR","LAMIVUDINE", "ZIDOVUDINE" ));

		listMap.put("ABC+3TC+LPV/r", Arrays.asList("ABACAVIR","LAMIVUDINE", "LOPINAVIR", "RITONAVIR" ));
		listMap.put("ABC+ddI+LPV/r", Arrays.asList("ABACAVIR","DIDANOSINE", "LOPINAVIR", "RITONAVIR" ));

		return listMap;

	}


}
