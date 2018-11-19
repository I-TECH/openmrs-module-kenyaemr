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
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EncounterWrapper}
 */
public class EncounterWrapperTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
	}

	/**
	 * @see EncounterWrapper#firstObs(org.openmrs.Concept)
	 */
	@Test
	public void firstObs_shouldFindFirstObsWithConcept() {
		Encounter e = new Encounter();
		EncounterWrapper wrapper = new EncounterWrapper(e);

		// Test empty encounter
		Assert.assertNull(wrapper.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Add obs to encounter
		Obs obs0 = new Obs();
		obs0.setConcept(Dictionary.getConcept(Dictionary.CD4_PERCENT));
		obs0.setValueNumeric(50.0);
		e.addObs(obs0);
		Obs obs1 = new Obs();
		obs1.setConcept(Dictionary.getConcept(Dictionary.CD4_COUNT));
		obs1.setValueNumeric(123.0);
		e.addObs(obs1);

		Assert.assertEquals(new Double(123.0), wrapper.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT)).getValueNumeric());
	}

	/**
	 * @see EncounterWrapper#allObs(org.openmrs.Concept)
	 */
	@Test
	public void allObs_shouldFindAllObsWithConcept() {
		Encounter e = new Encounter();
		EncounterWrapper wrapper = new EncounterWrapper(e);

		// Test empty encounter
		Assert.assertEquals(new ArrayList<Obs>(), wrapper.allObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY)));

		// Add 2 obs to encounter
		Obs obs0 = new Obs();
		obs0.setConcept(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY));
		obs0.setValueCoded(Dictionary.getConcept(Dictionary.NEVIRAPINE));
		e.addObs(obs0);
		Obs obs1 = new Obs();
		obs1.setConcept(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY));
		obs1.setValueCoded(Dictionary.getConcept(Dictionary.ZIDOVUDINE));
		e.addObs(obs1);

		Assert.assertEquals(2, wrapper.allObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY)).size());
	}

	/**
	 * @see EncounterWrapper#getProvider()
	 */
	@Test
	public void getProvider_shouldGetFirstProviderWithUnknownRole() {
		Provider provider1 = Context.getProviderService().getProvider(1);
		Encounter enc3 = Context.getEncounterService().getEncounter(3);
		EncounterWrapper wrapped = new EncounterWrapper(enc3);

		Assert.assertThat(wrapped.getProvider(), is(provider1));

		// Check empty encounter with no provider
		Assert.assertThat(new EncounterWrapper(new Encounter()).getProvider(), nullValue());
	}

	/**
	 * @see EncounterWrapper#setProvider(org.openmrs.Provider)
	 */
	@Test
	public void setProvider_shouldSetUniqueProviderWithUnknownRole() {
		EncounterRole unknownRole = MetadataUtils.existing(EncounterRole.class, EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
		Provider provider1 = Context.getProviderService().getProvider(1);
		Encounter enc = new Encounter();
		EncounterWrapper wrapped = new EncounterWrapper(enc);

		wrapped.setProvider(provider1);

		Assert.assertThat(enc.getProvidersByRole(unknownRole), contains(provider1));

		// Check setting again
		wrapped.setProvider(provider1);

		Assert.assertThat(enc.getProvidersByRole(unknownRole), contains(provider1));

		// Check setting to null
		wrapped.setProvider(null);

		Assert.assertThat(enc.getProvidersByRole(unknownRole), empty());
	}
}