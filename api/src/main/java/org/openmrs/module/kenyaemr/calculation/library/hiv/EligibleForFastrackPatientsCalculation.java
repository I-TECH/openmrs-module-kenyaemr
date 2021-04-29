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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * A calculation that returns patients who are eligible for fastrack
 * Eligibility criteria include:
 * longer follow-up intervals (depends on clinician ==>longer than 30 days)
 *
 **/
public class EligibleForFastrackPatientsCalculation extends AbstractPatientCalculation  {

    protected static final Log log = LogFactory.getLog(EligibleForFastrackPatientsCalculation.class);
    static ConceptService conceptService = Context.getConceptService();

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Concept TCAdate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
        CalculationResultMap ret = new CalculationResultMap();

        for(Integer ptId: cohort){

            Integer tcaPlus30days = 0;
            Integer tcaConcept = 5096;
            Date tcaDate = null;
            Date tcaObsDate = null;
            boolean patientInHivProgram = false;

            boolean eligible = false;
            Date currentDate =new Date();

         //With Greencard TCA more than 30 days
            Encounter lastFollowUpEncounter = EmrUtils.lastEncounter(Context.getPatientService().getPatient(ptId), Context.getEncounterService().getEncounterTypeByUuid("a0034eee-1940-4e35-847f-97537a35d05e"));   //last greencard followup form
            if (lastFollowUpEncounter != null) {
                for (Obs obs : lastFollowUpEncounter.getObs()) {
                    if (obs.getConcept().getConceptId().equals(tcaConcept)){
                         tcaDate = obs.getValueDatetime();
                         tcaObsDate = obs.getObsDatetime();
                         tcaPlus30days= daysBetween(tcaDate,tcaObsDate);
                       }
                }
            }

            if (inHivProgram.contains(ptId) && !ltfu.contains(ptId)) {
                patientInHivProgram = true;
            }
            if(patientInHivProgram && tcaDate != null && tcaObsDate != null && tcaDate.after(new Date()) && tcaPlus30days >= 30) { // exclude missed appointments
                eligible = true;
            }
            ret.put(ptId, new BooleanResult(eligible, this));
        }
        return ret;
    }
    private Integer daysBetween(Date d1, Date d2){
        DateTime date1 = new DateTime(d1.getTime());
        DateTime date2 = new DateTime(d2.getTime());
        return Math.abs(Days.daysBetween(date1, date2).getDays());
    }

}