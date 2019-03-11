/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EnhancedAdherenceRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.enhancedAdherence.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.hiv.report.hiv.enhancedAdherenceRegister"})
public class EnhancedAdherenceRegisterReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		return allPatientsCohort();
	}

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new EnhancedAdherenceRegisterCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("ART Preparation");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allPatients = enhancedAdherenceDataSetDefinition();
        allPatients.addRowFilter(allPatientsCohort());
		//allPatients.addRowFilter(buildCohort(descriptor));
        DataSetDefinition allPatientsDSD = allPatients;


        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "startDate=${startDate},endDate=${endDate}")
        );
    }

	@Override
	protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class),
				new Parameter("dateBasedReporting", "", String.class)
		);
	}

	protected PatientDataSetDefinition enhancedAdherenceDataSetDefinition() {

		PatientDataSetDefinition dsd = new PatientDataSetDefinition("EnhancedAdherenceRegister");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");

		dsd.addColumn("Unique Patient No", identifierDef, "");
		//		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		// new columns
		dsd.addColumn("Session number", new SessionNumberDataDefinition(), "");
		dsd.addColumn("First session date", new FirstSessionDateDataDefinition(),"");
		dsd.addColumn("Pill count", new PillCountDataDefinition(),"");
		dsd.addColumn("Arv adherence status", new ArvAdherenceDataDefinition(), "");
		dsd.addColumn("Has VL results", new HasViralLoadResultsDataDefinition(), "");
		dsd.addColumn("Has suppressed VL results", new HasSuppressedVlDataDefinition(), "");
		dsd.addColumn("Feeling about VL results", new VlResultsFeelingDataDefinition(),"");
		dsd.addColumn("Cause of high VL", new CauseOfHighVlDataDefinition(),"");
		dsd.addColumn("Patient HIV Knowledge", new PatientHivKnowledgeDataDefinition(),"");
		dsd.addColumn("Patient drugs uptake", new PatientDrugsUptakeDataDefinition(),"");
		dsd.addColumn("Patient drugs reminder tools", new PatientDrugsReminderToolsDataDefinition(),"");
		dsd.addColumn("Patient drugs uptake during travels", new PatientDrugsUptakeDuringTravelsDataDefinition(),"");
		dsd.addColumn("Patient drugs side effects response", new PatientDrugsSideEffectsResponseDataDefinition(),"");
		dsd.addColumn("Patient drugs uptake in difficult times", new PatientDrugsUptakeInDifficultTimesDataDefinition(),"");
		dsd.addColumn("Patient drugs daily uptake feeling", new PatientDrugsDailyUptakeFeelingDataDefinition(),"");
		dsd.addColumn("Patient ambitions", new PatientAmbitionsDataDefinition(),"");
		dsd.addColumn("Patient has people to talk to", new PatientHasPeopleToTalkDataDefinition(),"");
		dsd.addColumn("Patient enlisting social support", new PatientEnlistingSocialSupportDataDefinition(),"");
		dsd.addColumn("Patient income sources", new PatientIncomeSourcesDataDefinition(),"");
		dsd.addColumn("Patient challenges reaching clinic", new PatientChallengesReachingClinicDataDefinition(),"");
		dsd.addColumn("Patient treated differently", new PatientTreatedDifferentlyDataDefinition(),"");
		dsd.addColumn("Stigma hindering adherence", new StigmaHinderingAdherenceDataDefinition(),"");
		dsd.addColumn("Patient tried faith healing", new PatientTriedFaithHealingDataDefinition(),"");
		dsd.addColumn("Patient improved adherence", new PatientAdherenceImprovedDataDefinition(),"");
		dsd.addColumn("Patient missed doses", new PatientMissedDosesDataDefinition(),"");
		dsd.addColumn("Patient other referrals", new PatientOtherReferralsDataDefinition(),"");
		dsd.addColumn("Patient appointments met", new PatientAppointmentsMetDataDefinition(),"");
		dsd.addColumn("Patient referral experience", new PatientReferralExperienceDataDefinition(),"");
		dsd.addColumn("Patient home visit benefit", new PatientHomeVisitBenefitDataDefinition(),"");
		dsd.addColumn("Patient adherence plan", new PatientAdherencePlanDataDefinition(),"");
		dsd.addColumn("Patient next appointment date", new PatientNextAppointmentDateDataDefinition(),"");
		return dsd;
	}
}
