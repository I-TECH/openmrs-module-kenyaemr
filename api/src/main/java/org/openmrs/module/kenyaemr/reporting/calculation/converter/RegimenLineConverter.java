package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversion of regimens into line
 */
public class RegimenLineConverter implements DataConverter {
    @Override
    public Object convert(Object o) {
        String regimen = (String) new RegimenConverter().convert(o);
        if(regimen == null || regimen.isEmpty() || regimen.equals("NA")) {
            return "NA";
        }
        else if(firstLineRegimen().contains(regimen)){
            return "1st";
        }
        else if(secondLineRegimen().contains(regimen)) {
            return "2nd";
        }
        else {
            return "Other";
        }

    }

    @Override
    public Class<?> getInputDataType() {
        return String.class;
    }

    @Override
    public Class<?> getDataType() {
        return String.class;
    }


    private List<String> firstLineRegimen() {
        List<String> regimens = new ArrayList<String>();
        regimens.add("AZT+3TC+NVP");
        regimens.add("AZT+3TC+EFV");
        regimens.add("TDF+3TC+NVP");
        regimens.add("TDF+3TC+EFV");
        regimens.add("d4T+3TC+NVP");
        regimens.add("d4T+3TC+EFV");
        regimens.add("ABC+3TC+NVP");
        regimens.add("ABC+3TC+EFV");
        regimens.add("ABC+3TC+AZT");
        return regimens;
    }

    private List<String> secondLineRegimen() {
        List<String> regimens = new ArrayList<String>();
        regimens.add("AZT+3TC+LPV/r");
        regimens.add("AZT+ddI+LPV/r");
        regimens.add("AZT+3TC+ABC");
        regimens.add("TDF+3TC+LPV/r");
        regimens.add("TDF+3TC+ABC");
        regimens.add("TDF+3TC+AZT");
        regimens.add("TDF+ABC+LPV/r");
        regimens.add("TDF+AZT+LPV/r");
        regimens.add("ABC+ddI+LPV/r");
        regimens.add("d4T+3TC+LPV/r");
        regimens.add("d4T+3TC+ABC");
        regimens.add("AZT+ABC+LPV/r");
        regimens.add("d4T+ABC+LPV/r");
        regimens.add("ABC+3TC+LPV/r");
        regimens.add("AZT+3TC+ATV/r");
        regimens.add("ABC+3TC+DRV/r");
        regimens.add("AZT+3TC+DRV/r");
        regimens.add("TDF+3TC+ATV/r");
        return regimens;
    }
}