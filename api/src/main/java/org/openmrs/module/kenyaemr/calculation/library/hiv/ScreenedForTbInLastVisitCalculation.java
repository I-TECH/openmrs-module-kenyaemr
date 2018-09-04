/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.report.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 06/08/15.
 * Calculates if a patient was screened for tb during their last visit
 */
public class ScreenedForTbInLastVisitCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
        Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
        Concept diseaseDiagnosed = Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED);
        Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);

        VisitsForPatientDataDefinition definition = new VisitsForPatientDataDefinition();
        definition.setStartedOnOrBefore(context.getNow());
        definition.setStartedOnOrAfter((DateUtil.adjustDate(context.getNow(), -3, DurationUnit.MONTHS)));

        //put this visits into a map
        CalculationResultMap data = CalculationUtils.evaluateWithReporting(definition, cohort, params, null, context);

        for (Integer ptId : cohort) {
            boolean isScreened = false;
            //get all visits for this patient
            ListResult result = (ListResult) data.get(ptId);
            List<Visit> visits = CalculationUtils.extractResultValues(result);
            if (visits.size() > 1) {
                //we want the previouse visit
                Visit previouseVisit = visits.get(visits.size() - 2);
                    if (previouseVisit.getEncounters() != null && previouseVisit.getEncounters().size() > 0) {
                        for (Encounter enc : previouseVisit.getEncounters()) {
                                for (Obs obs : enc.getAllObs()) {
                                    if (obs.getConcept().equals(tbDiseaseStatus) && (obs.getValueCoded().equals(diseaseSuspected) || obs.getValueCoded().equals(diseaseDiagnosed) || obs.getValueCoded().equals(noSignsOrSymptoms))) {
                                        isScreened = true;
                                        break;
                                    }
                                }
                        }
                    }
            }
            ret.put(ptId, new BooleanResult(isScreened, this));
        }
        return ret;
    }
}
