/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.openmrs.*;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether a child at 6 week and above has had PCR test
 */
public class NeedsPcrTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(NeedsPcrTestCalculation.class);

    /**
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return flagMsg.toString();
    }

    StringBuilder flagMsg = new StringBuilder("");

    /**
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        // Get all patients who are alive and in MCH-CS program
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inMchcsProgram = Filters.inProgram(mchcsProgram, alive, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        // Get whether the child is HIV Exposed
        CalculationResultMap lastChildHivStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), cohort, context);
        CalculationResultMap lastPcrTestQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
        Set<Integer> pendingDNARapidTestResults = CalculationUtils.patientsThatPass(calculate(new PendingDNAPCRRapidTestResultCalculation(), cohort, context));

        Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
        Concept PCR_6_WEEKS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
        Concept PCR_6_MONTHS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE);
        Concept PCR_12_MONTHS = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);

        CalculationResultMap ret = new CalculationResultMap();

        OrderService orderService = Context.getOrderService();

        for (Integer ptId : cohort) {

            boolean needsPcr = false;
            Order order = new Order();
            // Check if a patient is alive and is in MCHCS program
            if (inMchcsProgram.contains(ptId) && !pendingDNARapidTestResults.contains(ptId) && !inHivProgram.contains(ptId)) {

                Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);
                Obs pcrTestObsQual = EmrCalculationUtils.obsResultForPatient(lastPcrTestQualitative, ptId);

                if (pcrTestObsQual != null && pcrTestObsQual.getOrder() != null) {
                    Integer orderId = pcrTestObsQual.getOrder().getOrderId();
                    order = orderService.getOrder(orderId);
                }

                //get birth date of this patient
                Person person = Context.getPersonService().getPerson(ptId);

                if (hivStatusObs != null && pcrTestObsQual == null && hivStatusObs.getValueCoded().equals(hivExposed) && getAgeInWeeks(person.getBirthdate(), context.getNow()) >= 6) {
                    needsPcr = true;
                    flagMsg.append("Due for week-6 PCR test");
                } else if (hivStatusObs != null && pcrTestObsQual != null && order != null && hivStatusObs.getValueCoded().equals(hivExposed) && pcrTestObsQual.getValueCoded() == NEGATIVE && !order.getOrderReason().equals(PCR_6_MONTHS) && order.getOrderReason().equals(PCR_6_WEEKS) && getAgeInMonths(person.getBirthdate(), context.getNow()) >= 6) {
                    needsPcr = true;
                    flagMsg.append("Due for month-6 PCR test");
                } else if (hivStatusObs != null && pcrTestObsQual != null && order != null && (hivStatusObs.getValueCoded().equals(hivExposed)) && pcrTestObsQual.getValueCoded() == NEGATIVE && !order.getOrderReason().equals(PCR_12_MONTHS) && order.getOrderReason().equals(PCR_6_MONTHS) && getAgeInMonths(person.getBirthdate(), context.getNow()) >= 12) {
                    needsPcr = true;
                    flagMsg.append("Due for month-12 PCR test");
                }

            }

            ret.put(ptId, new BooleanResult(needsPcr, this, context));
        }

        return ret;
    }

    public static Integer getAgeInWeeks(Date birtDate, Date context) {
        DateTime d1 = new DateTime(birtDate.getTime());
        DateTime d2 = new DateTime(context.getTime());
        return Weeks.weeksBetween(d1, d2).getWeeks();
    }

    Integer getAgeInMonths(Date birtDate, Date context) {
        DateTime d1 = new DateTime(birtDate.getTime());
        DateTime d2 = new DateTime(context.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}