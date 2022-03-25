/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.vmmc;


import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.vmmc.VMMCAdverseEventsIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Report builder for VMMC Adverse events
 */
@Component
@Builds({"kenyaemr.vmmc.report.vmmcadverseevents"})
public class VMMCAdverseEventsReportBuilder extends AbstractReportBuilder {

    @Autowired
    private VMMCAdverseEventsIndicatorLibrary vmmcAdverseEventsIndicatorLibrary;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String VMMC_PROCEDURE_FORM = "vmmc-procedure";
    public static final String VMMC_CLIENT_FOLLOWUP_FORM = "vmmc-followup";
    public static final Integer PAIN = 114403;
    public static final Integer BLEEDING = 147241;
    public static final Integer ANAESTHETIC_REACTION = 135693;
    public static final Integer EXCESSIVE_SKIN_REMOVED = 110094;
    public static final Integer DAMAGE_TO_PENIS = 156911;
    public static final Integer APPEARANCE_PROBLEMS = 152045;
    public static final Integer HEMATOMA_OR_SWELLING = 156567;
    public static final Integer INFECTION_OR_SWELLING = 139510;
    public static final Integer DIFFICULTY_OR_PAIN_URINATING = 118771;
    public static final Integer WOUND_DISRUPTION = 163799;
    public static final Integer SEVERE_AE = 1500;
    public static final Integer MODERATE_AE = 1499;
    public static final Integer MILD_AE = 1498;

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(new Parameter("endDate", "End Date", Date.class),
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(ReportUtils.map(vmmcProcedureDataset(), "startDate=${startDate},endDate=${endDate}"),  ReportUtils.map(vmmcClientFollowupDataset(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition vmmcProcedureDataset() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("1");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.setDescription("Adverse events during VMMC procedure");
        cohortDsd.addColumn("Mild pain", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(PAIN,MILD_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Moderate pain", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(PAIN,MODERATE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Severe pain", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(PAIN,SEVERE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Mild bleeding", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(BLEEDING,MILD_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Moderate bleeding", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(BLEEDING,MODERATE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Severe bleeding", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(BLEEDING,SEVERE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Mild Anaesthetic Reaction", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(ANAESTHETIC_REACTION,MILD_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Anaesthetic Reaction", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(ANAESTHETIC_REACTION,MODERATE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Severe Anaesthetic Reaction", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(ANAESTHETIC_REACTION,SEVERE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Mild Excessive skin removed", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(EXCESSIVE_SKIN_REMOVED,MILD_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Excessive skin removed", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(EXCESSIVE_SKIN_REMOVED,MODERATE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Severe Excessive skin removed", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(EXCESSIVE_SKIN_REMOVED,SEVERE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Mild Damage to the penis", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(DAMAGE_TO_PENIS,MILD_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Damage to the penis", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(DAMAGE_TO_PENIS,MODERATE_AE,VMMC_PROCEDURE_FORM), indParams), "");
        cohortDsd.addColumn("Severe Damage to the penis", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(DAMAGE_TO_PENIS,SEVERE_AE,VMMC_PROCEDURE_FORM), indParams), "");

        return cohortDsd;

    }

    protected DataSetDefinition vmmcClientFollowupDataset() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("2");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.setDescription("VMMC adverse events during client followup");
        cohortDsd.addColumn("Mild pain", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(PAIN,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate pain", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(PAIN,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe pain", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(PAIN,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Mild bleeding", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(BLEEDING,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate bleeding", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(BLEEDING,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe bleeding", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(BLEEDING,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Mild Problems with appearance", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(APPEARANCE_PROBLEMS,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Problems with appearance", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(APPEARANCE_PROBLEMS,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe Problems with appearance", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(APPEARANCE_PROBLEMS,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Mild Hematoma or Swelling", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(HEMATOMA_OR_SWELLING,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Hematoma or Swelling", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(HEMATOMA_OR_SWELLING,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe Hematoma or Swelling", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(HEMATOMA_OR_SWELLING,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Mild Infection or Swelling", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(INFECTION_OR_SWELLING,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Infection or Swelling", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(INFECTION_OR_SWELLING,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe Infection or Swelling", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(INFECTION_OR_SWELLING,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Mild Difficulty or pain when urinating", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(DIFFICULTY_OR_PAIN_URINATING,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Difficulty or pain when urinating", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(DIFFICULTY_OR_PAIN_URINATING,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe Difficulty or pain when urinating", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(DIFFICULTY_OR_PAIN_URINATING,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Mild Wound disruption", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(WOUND_DISRUPTION,MILD_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Moderate Wound disruption", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(WOUND_DISRUPTION,MODERATE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");
        cohortDsd.addColumn("Severe Wound disruption", "", ReportUtils.map(vmmcAdverseEventsIndicatorLibrary.getClientsWithVMMCAdverseEvent(WOUND_DISRUPTION,SEVERE_AE,VMMC_CLIENT_FOLLOWUP_FORM), indParams), "");

        return cohortDsd;

    }
}
