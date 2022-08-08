/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.ipt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients are (alive and) in the IPT program
 * Eligibility criteria include:
 * Is currently active in IPT program
 * should not include those currently in the TB program
 *
 */
public class OnIptProgramCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	protected static final Log log = LogFactory.getLog(OnIptProgramCalculation.class);

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Program iptProgram = MetadataUtils.existing(Program.class, IPTMetadata._Program.IPT);
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
		Set<Integer> inIptProgram = Filters.inProgram(iptProgram, alive, context);

		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId: cohort){

			boolean onIpt = false;
			if (!inTbProgram.contains(ptId) && inIptProgram.contains(ptId)) {
				onIpt = true;
			}

			ret.put(ptId, new BooleanResult(onIpt, this));
		}
		return ret;
	}

	@Override
	public String getFlagMessage() {
		return "On TPT";
	}

}