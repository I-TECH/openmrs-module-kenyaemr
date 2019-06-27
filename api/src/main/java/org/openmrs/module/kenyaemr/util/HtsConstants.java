/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.util;

import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

public class HtsConstants {
    public static final Integer HTS_FINAL_TEST_CONCEPT_ID = 159427;
    public static final Integer HTS_POSITIVE_RESULT_CONCEPT_ID = 703;
    public static final Integer HTS_NEGATIVE_RESULT_CONCEPT_ID = 664;
    public static final Integer HTSLINKAGE_QUESTION_CONCEPT_ID = 159811;
    public static final Integer SUCCESSFUL_LINKAGE_CONCEPT_ID = 1065;

    public static final Form htsInitialForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_INITIAL_TEST);
    public static final Form htsRetestForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.HTS_CONFIRMATORY_TEST);
    public static final Form htsLinkageForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.REFERRAL_AND_LINKAGE);
    public static final EncounterType htsEncType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.HTS);
}
