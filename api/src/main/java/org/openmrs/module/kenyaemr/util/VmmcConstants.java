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
import org.openmrs.module.kenyaemr.metadata.VMMCMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

public class VmmcConstants {
    public static final Integer METHOD = 1651;
    public static final Integer CONVENTIONAL_METHOD = 159619;
    public static final Integer DEVICE_METHOD = 164204;

    public static final Form vmmcMedicalHistoryExaminationForm = MetadataUtils.existing(Form.class, VMMCMetadata._Form.VMMC_MEDICAL_HISTORY_EXAMINATION_FORM);
    public static final EncounterType vmmcMedicalHistoryEncType = MetadataUtils.existing(EncounterType.class, VMMCMetadata._EncounterType.VMMC_MEDICAL_HISTORY_EXAMINATION);
    public static final Form vmmcCircumcisionProcedureForm = MetadataUtils.existing(Form.class, VMMCMetadata._Form.VMMC_PROCEDURE_FORM);
    public static final EncounterType vmmcCircumcisionProcedureEncType = MetadataUtils.existing(EncounterType.class, VMMCMetadata._EncounterType.VMMC_PROCEDURE);
}
