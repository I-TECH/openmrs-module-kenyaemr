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

package org.openmrs.module.kenyaemr.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link VisitWrapper}
 */
public class VisitWrapperTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Before
	public void setup() {
		commonMetadata.install();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.wrapper.VisitWrapper#getSourceForm()
	 */
	@Test
	public void getSourceForm_shouldReturnTheSourceFormIfThereIsOne() {
		Patient patient = Context.getPatientService().getPatient(8);
		VisitType outpatient = MetadataUtils.getVisitType(CommonMetadata._VisitType.OUTPATIENT);
		Visit visit = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2011, 1, 1), null);

		// Check no attribute returns null
		Assert.assertThat(new VisitWrapper(visit).getSourceForm(), is(nullValue()));

		Form ceForm = MetadataUtils.getForm(CommonMetadata._Form.CLINICAL_ENCOUNTER);

		VisitAttribute sourceAttr = new VisitAttribute();
		sourceAttr.setAttributeType(MetadataUtils.getVisitAttributeType(CommonMetadata._VisitAttributeType.SOURCE_FORM));
		sourceAttr.setOwner(visit);
		sourceAttr.setValue(ceForm);
		visit.addAttribute(sourceAttr);

		Context.getVisitService().saveVisit(visit);

		Assert.assertThat(new VisitWrapper(visit).getSourceForm(), is(ceForm));
	}
}