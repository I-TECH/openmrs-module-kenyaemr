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

package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.StoppedARTCalculation;
import org.openmrs.module.kenyaemr.calculation.library.DeceasedPatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.*;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentARTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.FacilityNameCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferInCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.MflCodeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadListCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.CurrentTbStatusCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.ScreenedForTbAndDiagnosedCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.*;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.hiv.report.artCohortAnalysis.data.on.ART.cohorts"})
public class PreArtandArtClientsReportBuilder extends AbstractHybridReportBuilder {

	@Autowired
	private HivCohortLibrary hivCohortLibrary;

    /**
     *
     * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
     */
    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

	/**
	 *
	 * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(org.openmrs.module.kenyacore.report.CohortReportDescriptor, org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition)
	 */
	@Override
	protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        ProgramEnrollmentsForPatientDataDefinition hivProgramEnrollment = new ProgramEnrollmentsForPatientDataDefinition();
        hivProgramEnrollment.setWhichEnrollment(TimeQualifier.LAST);
        hivProgramEnrollment.setProgram(hivProgram);


		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.setName("preArtArtClients");
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Facility name", new CalculationDataDefinition("Facility Name", new FacilityNameCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("MFL code", new CalculationDataDefinition("MFL Code", new MflCodeCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Unique Identifier", identifierDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter());
		dsd.addColumn("Marital Status", new ObsForPersonDataDefinition("Marital Status", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CIVIL_STATUS), null, null), "", new CustomDataConverter());
		dsd.addColumn("County", new CalculationDataDefinition("ARV Start Date", new CountyAddressCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Sub County/District", new CalculationDataDefinition("ARV Start Date", new SubCountyAddressCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date of Diagnosis", new ObsForPersonDataDefinition("Date of Diagnosis", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), null, null), "", new DateOfHivDiagnosisConverter());
        dsd.addColumn("Date of enrollment to care", hivProgramEnrollment, "", new DateOfLastEnrollmentConverter());
		dsd.addColumn("Transfer in (TI)", new CalculationDataDefinition("Transfer in (TI)", new IsTransferInCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date Transferred in", new CalculationDataDefinition("Date Transferred in", new TransferInDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Current IPT status", new CalculationDataDefinition("Current IPT status", new CurrentIPTStatusCalculation()), "", new CurrentIPTStatusConverter("status"));
		dsd.addColumn("IPT start date", new CalculationDataDefinition("IPT start date", new IPTStartDateCalculation()), "", new CurrentIPTStatusConverter("startDate"));
		dsd.addColumn("Current TB status", new CalculationDataDefinition("Current TB status", new CurrentTbStatusCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date of TB diagnosis", new CalculationDataDefinition("Date of TB diagnosis", new ScreenedForTbAndDiagnosedCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Anti TB start date", new ObsForPersonDataDefinition("Anti TB start date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), null, null), "", new ObsDateConverter());
		dsd.addColumn("Date medically eligible for ART", new CalculationDataDefinition("Date medically eligible for ART", new DateMedicallyEligibleForARTCalculation()), "", new MedicallyEligibleConverter("date"));
		dsd.addColumn("Original/Initial ART regimen", new CalculationDataDefinition("Original/Initial ART regimen", new InitialArtRegimenCalculation()), "", new RegimenConverter());
		dsd.addColumn("Initial ART start Date", new CalculationDataDefinition("Initial ART start Date", new InitialArtStartDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Reason for ART initiation", new CalculationDataDefinition("Reason for ART initiation", new DateMedicallyEligibleForARTCalculation()), "", new MedicallyEligibleConverter("reason"));
		dsd.addColumn("Current ART regimen", new CalculationDataDefinition("Current ART regimen", new CurrentArtRegimenCalculation()), "", new RegimenConverter());
		dsd.addColumn("Start date for Current regimen", new CalculationDataDefinition("Start date for Current regimen", new CurrentARTStartDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date of Last Visit", new CalculationDataDefinition("Date of Last Visit", new DateLastSeenWithNoEncountersCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Initial CD4 count", new ObsForPersonDataDefinition("Initial CD4 count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsNumericConverter());
		dsd.addColumn("Date of initial CD4 Count", new ObsForPersonDataDefinition("Date of initial CD4 Count", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDateConverter());
		dsd.addColumn("Last documented CD4 count", new ObsForPersonDataDefinition("Last documented CD4 count", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsNumericConverter());
		dsd.addColumn("Date of last CD4 count", new ObsForPersonDataDefinition("Date of last CD4 count", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CD4_COUNT), null, null), "", new ObsDateConverter());
		dsd.addColumn("Latest VL", new ObsForPersonDataDefinition("Latest VL", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null), "", new ObsNumericConverter());
		dsd.addColumn("Latest VL Date", new ObsForPersonDataDefinition("Latest VL Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), null, null), "", new ObsDateConverter());
		dsd.addColumn("2nd last VL", new CalculationDataDefinition("2nd last VL", new ViralLoadListCalculation()), "", new CalculationMapResultsConverter("value", 2));
		dsd.addColumn("2nd last VL Date", new CalculationDataDefinition("2nd last VL Date", new ViralLoadListCalculation()), "", new CalculationMapResultsConverter("date", 2));
		dsd.addColumn("3rd Last vL", new CalculationDataDefinition("3rd Last vL", new ViralLoadListCalculation()), "", new CalculationMapResultsConverter("value", 3));
		dsd.addColumn("3rd last VL Date", new CalculationDataDefinition("3rd last VL Date", new ViralLoadListCalculation()), "", new CalculationMapResultsConverter("date", 3));
		dsd.addColumn("Last appointment date", new ObsForPersonDataDefinition("Last appointment date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsDateConverter());
		dsd.addColumn("Alive and on Follow-up", new CalculationDataDefinition("Alive and on Follow-up", new AliveAndOnFollowUpCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Stopped (Interrupted)", new CalculationDataDefinition("Stopped (Interrupted)",new StoppedARTCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date stopped", new CalculationDataDefinition("Date stopped", new StoppedARTDateCalculation()), "", new ArtStoppedConverter());
		dsd.addColumn("Transferred out", new CalculationDataDefinition("Transferred out", new IsTransferOutCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date Transferred out", new CalculationDataDefinition("Date Transferred out", new TransferOutDateCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Defaulted", new CalculationDataDefinition("Defaulted", new MissedLastAppointmentCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Lost to Follow-up", new CalculationDataDefinition("Lost to Follow-up", new LostToFollowUpCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date classified as LTFU", new CalculationDataDefinition("Date classified as LTFU", new DateClassifiedLTFUCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Died", new CalculationDataDefinition("Died", new DeceasedPatientsCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date reported dead", new CalculationDataDefinition("Date reported dead", new DateOfDeathCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Documented pregnancies", new CalculationDataDefinition("Documented pregnancies", new PregnancyAndEDDCalculation()), "", new PregnancyEddConverter("status"));
        dsd.addColumn("EDD of pregnancies", new CalculationDataDefinition("EDD of pregnancies", new PregnancyAndEDDCalculation()), "", new PregnancyEddConverter("date"));
	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		CohortDefinition cd = hivCohortLibrary.enrolled();
        cd.setName("preArtArtClients");
		return ReportUtils.map(cd, "enrolledOnOrBefore=${endDate}");
	}

}
