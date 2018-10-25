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

package org.openmrs.module.kenyaemr.reporting.builder.mchms;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HEIRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.MaternityRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.*;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
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
@Builds({"kenyaemr.mchcs.report.maternityRegister"})
public class MaternityRegisterReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		return allPatientsCohort();
	}

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new MaternityRegisterCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("Maternity All Clients");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allPatients = maternityDataSetDefinition();
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

	protected PatientDataSetDefinition maternityDataSetDefinition() {

		PatientDataSetDefinition dsd = new PatientDataSetDefinition("HEI Data set definition");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Patient No", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));

		// new columns
		dsd.addColumn("Admission Number", new MaternityAdmissionNumberDataDefinition(),"");
		dsd.addColumn("Date of Admission", new MaternityAdmissionDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
		dsd.addColumn("Number of ANC Visits", new MaternityNumberOfANCVisitsDataDefinition(),"");
		dsd.addColumn("Parity", new MaternityANCParityDataDefinition(),"");
		dsd.addColumn("Gravida", new MaternityGravidaDataDefinition(),"");
		dsd.addColumn("LMP", new MaternityLMPDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
		dsd.addColumn("Ultra Sound", new MaternityEDDUltrasoundDateDataDefinition(),"", new DateConverter(DATE_FORMAT));
		dsd.addColumn("Diagnosis", new MaternityDiagnosisDataDefinition(),"");
		dsd.addColumn("Duration of Labour", new MaternityDurationOfLabourDataDefinition(),"");
		dsd.addColumn("Delivery Date", new MaternityDeliveryDateDataDefinition(),"",new DateConverter(DATE_FORMAT));
		dsd.addColumn("Delivery Time", new MaternityDeliveryTimeDataDefinition(),"");
		dsd.addColumn("Gestation at Birth in weeks", new MaternityGestationAtBirthDataDefinition(),"");
		dsd.addColumn("Blood Loss", new MaternityBloodLossDataDefinition(),"");
		dsd.addColumn("Placenta Complete", new MaternityPlacentaCompleteDataDefinition(),"");
		dsd.addColumn("Blood loss", new MaternityBloodLossDataDefinition(),"");
		dsd.addColumn("Condition after delivery", new MaternityConditionAfterDeliveryDataDefinition(),"");
		dsd.addColumn("Deaths Audited", new MaternityDeathAuditedDataDefinition(),"");
		dsd.addColumn("Other Delivery Complications", new MaternityOtherDeliveryComplicationsDataDefinition(),"");
		dsd.addColumn("Baby Sex", new MaternityBabySexDataDefinition(),"");
		dsd.addColumn("Birth weight", new MaternityBirthWeightDataDefinition(),"");
		dsd.addColumn("Baby Condition", new MaternityBabyConditionDataDefinition(),"");
		dsd.addColumn("Initiated BF <1 Hr", new MaternityInitiatedBFWithinOneHourDataDefinition(),"");
		dsd.addColumn("TEO Given at Birth", new MaternityTEOGivenAtBirthDataDefinition(),"");
		dsd.addColumn("Baby with deformity", new MaternityBabyWithDeformityDataDefinition(),"");
		dsd.addColumn("APGAR Score", new MaternityApgarScoreDataDefinition(),"");
		dsd.addColumn("VDRL/RPR Results", new MaternityVDRLRPRResultsDataDefinition(),"");
		dsd.addColumn("HIV Status at ANC", new MaternityHIVStatusAtANCDataDefinition(),"");
		//dsd.addColumn("HIV Test One", new MaternityHIVTestOneDataDefinition(),"");
		//dsd.addColumn("HIV Test Two", new MaternityHIVTestTwoDataDefinition(),"");
		dsd.addColumn("HIV Final Results", new MaternityHIVFinalResultsDataDefinition(),"");
		dsd.addColumn("ARV Prophylaxis Issued from ANC", new MaternityARVProphylaxisIssuedFromANCDataDefinition(),"");
		dsd.addColumn("ARV Prophylaxis Issued at Maternity", new MaternityARVProphylaxisIssuedAtMaternityDataDefinition(),"");
		dsd.addColumn("ARV Prophylaxis To Baby in Maternity", new MaternityARVProphylaxisToBabyAtMaternityDataDefinition(),"");
		dsd.addColumn("CTX To Mother", new MaternityCTXToMotherDataDefinition(),"");
		dsd.addColumn("Vitamin A", new MaternityVitaminADataDefinition(),"");
		dsd.addColumn("Partner Tested For HIV", new MaternityPartnerTestedForHIVDataDefinition(),"");
		dsd.addColumn("Partner HIV Test Results", new MaternityPartnerHIVTestResultsDataDefinition(),"");
		dsd.addColumn("Counselled on Infant Feeding", new MaternityCounselledOnInfantFeedingDataDefinition(),"");
		dsd.addColumn("Delivery Conducted by", new MaternityDeliveryConductedByDataDefinition(),"");
		dsd.addColumn("Birth Notification Number", new MaternityBirthNotificationNumberDataDefinition(),"");
		dsd.addColumn("Discharged Date", new MaternityDischargeDateDataDefinition(),"");
		dsd.addColumn("Status of Baby at Discharge", new MaternityStatusOfBabyDataDefinition(),"");
		dsd.addColumn("Referred From", new MaternityReferredFromDataDefinition(),"");
		dsd.addColumn("Referred To", new MaternityReferredToDataDefinition(),"");
		dsd.addColumn("Comments", new MaternityCommentsDataDefinition(),"");

		return dsd;
	}
}
