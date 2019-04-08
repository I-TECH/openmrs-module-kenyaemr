/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by codehub on 9/7/15.
 */
public class CurrentArtRegimenCohortAnalysisCalculation extends BaseEmrCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        CalculationResultMap ret = new CalculationResultMap();

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }
        Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
        CalculationResultMap currentARVDrugOrders = activeDrugOrders(arvs, cohort, context);

        for (Integer ptId : cohort) {
            ListResult patientDrugOrders = (ListResult) currentARVDrugOrders.get(ptId);

            if (patientDrugOrders != null) {
                RegimenOrder regimen = new RegimenOrder(new HashSet<DrugOrder>(CalculationUtils.<DrugOrder>extractResultValues(patientDrugOrders)));
                ret.put(ptId, new SimpleResult(regimen, this, context));
            }
            else {
                ret.put(ptId, null);
            }
        }
        return ret;

    }
}
