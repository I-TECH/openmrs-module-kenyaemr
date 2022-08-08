/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
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
	 * @see VisitWrapper#overlaps()
	 */
	@Test
	public void overlaps_shouldReturnTrueIfVisitOverlaps() {

		Patient patient8 = Context.getPatientService().getPatient(8);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		Visit visit1 = TestUtils.saveVisit(patient8, outpatient, TestUtils.date(2011, 1, 1), TestUtils.date(2011, 1, 3));
		Visit visit2 = TestUtils.saveVisit(patient8, outpatient, TestUtils.date(2011, 1, 7), TestUtils.date(2011, 1, 10));
		Visit visit3 = TestUtils.saveVisit(patient8, outpatient, TestUtils.date(2011, 1, 13), null);
		Context.flushSession();

		// Test visit in between #1 and #2
		Visit visit = new Visit();
		visit.setPatient(patient8);
		visit.setVisitType(outpatient);
		visit.setStartDatetime(TestUtils.date(2011, 1, 4));
		visit.setStopDatetime(TestUtils.date(2011, 1, 5));

		VisitWrapper wrapper = new VisitWrapper(visit);

		Assert.assertFalse(wrapper.overlaps());

		// Test overlap with #1
		visit.setStartDatetime(TestUtils.date(2011, 1, 2));
		visit.setStopDatetime(TestUtils.date(2011, 1, 4));

		Assert.assertTrue(wrapper.overlaps());

		// Test touching #1 (visit dates are inclusive so counts as overlap)
		visit.setStartDatetime(TestUtils.date(2011, 1, 3));
		visit.setStopDatetime(TestUtils.date(2011, 1, 4));

		Assert.assertTrue(wrapper.overlaps());

		// Test overlap with unstopped #3
		visit.setStartDatetime(TestUtils.date(2011, 2, 2));
		visit.setStopDatetime(TestUtils.date(2011, 2, 4));

		Assert.assertTrue(wrapper.overlaps());

		// Check overlapping itself doesn't return true
		Assert.assertFalse(new VisitWrapper(visit2).overlaps());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.wrapper.VisitWrapper#getSourceForm()
	 */
	@Test
	public void getSourceForm_shouldReturnTheSourceFormIfThereIsOne() {
		Patient patient = Context.getPatientService().getPatient(8);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);
		Visit visit = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2011, 1, 1), null);

		// Check no attribute returns null
		Assert.assertThat(new VisitWrapper(visit).getSourceForm(), is(nullValue()));

		Form ceForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.CLINICAL_ENCOUNTER);

		VisitAttribute sourceAttr = new VisitAttribute();
		sourceAttr.setAttributeType(MetadataUtils.existing(VisitAttributeType.class, CommonMetadata._VisitAttributeType.SOURCE_FORM));
		sourceAttr.setOwner(visit);
		sourceAttr.setValue(ceForm);
		visit.addAttribute(sourceAttr);

		Context.getVisitService().saveVisit(visit);

		Assert.assertThat(new VisitWrapper(visit).getSourceForm(), is(ceForm));
	}
}