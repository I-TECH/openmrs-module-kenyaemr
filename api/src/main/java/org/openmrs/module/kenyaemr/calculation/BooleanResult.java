/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.calculation;

import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationContext;
import org.openmrs.calculation.result.SimpleResult;


/**
 *
 */
public class BooleanResult extends SimpleResult {

	/**
	 * Creates a new boolean result
	 * @param value the result value
	 * @param calculation the calculation
	 */
    public BooleanResult(Boolean value, Calculation calculation) {
	    super(value, calculation);
    }

	/**
	 * Creates a new boolean result
	 * @param value the result value
	 * @param calculation the calculation
	 * @param context the calculation context
	 */
	public BooleanResult(Boolean value, Calculation calculation, CalculationContext context) {
		super(value, calculation, context);
	}
    
    /**
     * @see org.openmrs.calculation.result.SimpleResult#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return value == null || !((Boolean) value);
    }
	
}
