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
import org.openmrs.module.kenyaemr.calculation.library.cohort.analysis.ARTYearsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.analysis.ArtLineYearsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.analysis.CurrentArtLineCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.analysis.PreARTYearsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.cohort.analysis.RegimenYearsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.*;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.*;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.ScreenedForTbAndDiagnosedCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.builder.tb.TBStartDateCalculation;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.ArtStoppedConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationMapResultsConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CurrentIPTStatusConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CurrentTBStatusConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CustomDataConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.DateOfHivDiagnosisConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.DateOfLastEnrollmentConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.EarliestCd4FollowingArtInitiationConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.LatestCd4PriorToArtInitiationConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.LostToFollowUpConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.MedicallyEligibleConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.ObsDateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.ObsNumericConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.PregnancyEddConverter;
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
import org.openmrs.module.reporting.data.person.definition.*;
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




		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.setName("preArtArtClients");
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Unique Identifier", identifierDef, "");
		dsd.addColumn("Facility name", new CalculationDataDefinition("Facility Name", new FacilityNameCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("MFL code", new CalculationDataDefinition("MFL Code", new MflCodeCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter());
		dsd.addColumn("Marital Status", new ObsForPersonDataDefinition("Marital Status", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.CIVIL_STATUS), null, null), "", new CustomDataConverter());
		dsd.addColumn("County", new CalculationDataDefinition("ARV Start Date", new CountyAddressCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Sub County", new CalculationDataDefinition("Sub County", new SubCountyAddressCalculation()), "", new CalculationResultConverter());
		dsd.addColumn("Date of Diagnosis", dateOfDiagnosis(), "onOrBefore=${endDate}", new DateOfHivDiagnosisConverter());
        dsd.addColumn("Date of enrollment", hivProgramEnrollment(),  "enrolledOnOrBefore=${endDate}", new DateOfLastEnrollmentConverter());
		dsd.addColumn("Ti", transferIn(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Date Ti", dateTransferredIn(), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Current IPT status", iptStatus(), "endDate=${endDate}", new CurrentIPTStatusConverter("status"));
        dsd.addColumn("IPT start date", iptStatus(), "endDate=${endDate}", new CurrentIPTStatusConverter("startDate"));
		dsd.addColumn("Current TB status", tbStatus(), "endDate=${endDate}", new CurrentTBStatusConverter("status"));
		dsd.addColumn("Date of TB diagnosis", dateOfTbDiagnosis(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Anti TB start date", tbStatus(), "endDate=${endDate}",  new  CurrentTBStatusConverter("startDate"));
		dsd.addColumn("Date medically eligible for ART", dateMedicallyEligibleForART(), "endDate=${endDate}", new MedicallyEligibleConverter("date"));
        dsd.addColumn("Lcd4artInitiation",  latestCd4PriorToArtInitiation(), "endDate=${endDate}", new LatestCd4PriorToArtInitiationConverter("value"));
        dsd.addColumn("Dcd4artInitiation",  latestCd4PriorToArtInitiation(), "endDate=${endDate}", new LatestCd4PriorToArtInitiationConverter("date"));
		dsd.addColumn("Original/Initial ART regimen", InitialARTRegimen(), "endDate=${endDate}", new RegimenConverter());
        dsd.addColumn("Original/Initial ART line", artLine(0), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Initial ART start Date", initialArtStartDate(), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Earliest CD4", earliestCd4FollowingArtInitiation(), "endDate=${endDate}", new EarliestCd4FollowingArtInitiationConverter("value"));
        dsd.addColumn("Date of earliest CD4", earliestCd4FollowingArtInitiation(), "endDate=${endDate}", new EarliestCd4FollowingArtInitiationConverter("date"));
		dsd.addColumn("Reason for ART initiation",  dateMedicallyEligibleForART(), "endDate=${endDate}", new MedicallyEligibleConverter("reason"));
		dsd.addColumn("Current ART regimen", currentArtRegimen(), "endDate=${endDate}", new RegimenConverter());
        dsd.addColumn("Current ART line", currentArtLine(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Start date for Current regimen", startDateForCurrentRegimen(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Date of Last Visit", dateOfLastVisit(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Initial CD4 count", initialCD4Count(),"onOrBefore=${endDate}", new ObsNumericConverter());
        dsd.addColumn("Date of initial CD4 Count",  initialCD4Count(),"onOrBefore=${endDate}", new ObsDateConverter());
		dsd.addColumn("Last documented CD4 count", lastCD4Count(), "onOrBefore=${endDate}", new ObsNumericConverter());
        dsd.addColumn("Date of last CD4 count", lastCD4Count(), "onOrBefore=${endDate}", new ObsDateConverter());
		dsd.addColumn("Latest VL", latestVl(),  "onOrBefore=${endDate}", new ObsNumericConverter());
		dsd.addColumn("Latest VL Date", latestVl(),  "onOrBefore=${endDate}", new ObsDateConverter());
		dsd.addColumn("2nd last VL", viralLoad(), "endDate=${endDate}", new CalculationMapResultsConverter("value", 2));
		dsd.addColumn("2nd last VL Date", viralLoad(), "endDate=${endDate}", new CalculationMapResultsConverter("date", 2));
		dsd.addColumn("3rd Last VL", viralLoad(), "endDate=${endDate}", new CalculationMapResultsConverter("value", 3));
		dsd.addColumn("3rd last VL Date", viralLoad(), "endDate=${endDate}", new CalculationMapResultsConverter("date", 3));
		dsd.addColumn("Last appointment date",  returnVisitDate(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Alive and on Follow-up", aliveAndOnFollowUp(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Stopped", stoppedArt(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Date stopped", stoppedArtDate(), "endDate=${endDate}", new ArtStoppedConverter());
        dsd.addColumn("Transferred out", transferOut(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Date Transferred out", transferOutDate(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Defaulted", defaulted(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Lost", classifiedLTFU(), "endDate=${endDate}", new LostToFollowUpConverter("status"));
		dsd.addColumn("Date LTFU", classifiedLTFU(), "endDate=${endDate}", new LostToFollowUpConverter("lostDate"));
		dsd.addColumn("Died", died(), "endDate=${endDate}", new CalculationResultConverter());
		dsd.addColumn("Date reported dead", dateReportedDate(), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Documented pregnancies", new CalculationDataDefinition("Documented pregnancies", new PregnancyAndEDDCalculation()), "", new PregnancyEddConverter("status"));
        dsd.addColumn("EDD of pregnancies", new CalculationDataDefinition("EDD of pregnancies", new PregnancyAndEDDCalculation()), "", new PregnancyEddConverter("date"));
        //additional columns added recently*/
        //pre art patients
        dsd.addColumn("pre-ART status 1yr", preArtStatus(0), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("pre-ART status 2yr", preArtStatus(1), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("pre-ART status 3yr", preArtStatus(2), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("pre-ART status 4yr", preArtStatus(3), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("pre-ART status 5yr", preArtStatus(4), "endDate=${endDate}", new CalculationResultConverter());

       //art patients
        dsd.addColumn("ART status 1yr", artStatus(0), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ART status 2yr", artStatus(1), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ART status 3yr", artStatus(2), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ART status 4yr", artStatus(3), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ART status 5yr", artStatus(4), "endDate=${endDate}", new CalculationResultConverter());

        dsd.addColumn("regimen 1yr", regimen(0), "endDate=${endDate}", new RegimenConverter());
        /*dsd.addColumn("regimen 2yr", regimen(1), "endDate=${endDate}", new RegimenConverter());
        dsd.addColumn("regimen 3yr", regimen(2), "endDate=${endDate}", new RegimenConverter());
        dsd.addColumn("regimen 4yr", regimen(3), "endDate=${endDate}", new RegimenConverter());
        dsd.addColumn("regimen 5yr", regimen(4), "endDate=${endDate}", new RegimenConverter());*/

        //ARTline
        dsd.addColumn("ARTline 1yr", artLine(0), "endDate=${endDate}", new CalculationResultConverter());
        /*dsd.addColumn("ARTline 2yr", artLine(1), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ARTline 3yr", artLine(2), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ARTline 4yr", artLine(3), "endDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ARTline 5yr", artLine(4), "endDate=${endDate}", new CalculationResultConverter());*/


	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		CohortDefinition cd = hivCohortLibrary.enrolled();
        cd.setName("preArtArtClients");
		return ReportUtils.map(cd, "enrolledOnOrAfter=${endDate-84m},enrolledOnOrBefore=${endDate}");
	}

    private DataDefinition earliestCd4FollowingArtInitiation() {
        CalculationDataDefinition cd = new CalculationDataDefinition("earliest cd4", new EarliestCd4FollowingArtInitiationCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }
    private DataDefinition latestCd4PriorToArtInitiation() {
        CalculationDataDefinition cd = new CalculationDataDefinition("latest cd4 prior to art", new LatestCd4PriorToArtInitiationCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition currentPregnancyStatusAtARTEnrolment() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Pregnancy status", new CurrentPregnancyStatusAtARTEnrolmentCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition dateReportedDate() {
        CalculationDataDefinition cd = new CalculationDataDefinition("date date", new DateOfDeathCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition died() {
        CalculationDataDefinition cd = new CalculationDataDefinition("deceased", new DeceasedPatientsCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition classifiedLTFU() {
        CalculationDataDefinition cd = new CalculationDataDefinition("LTFU", new DateClassifiedLTFUCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition defaulted() {
        CalculationDataDefinition cd = new CalculationDataDefinition("defaulted", new MissedLastAppointmentCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }
    private DataDefinition transferOutDate() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Transfer Out", new TransferOutDateCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition transferOut() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Transfer Out", new IsTransferOutCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition latestVl() {
        ObsForPersonDataDefinition obs = new ObsForPersonDataDefinition();
        obs.setWhich(TimeQualifier.LAST);
        obs.setQuestion(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD));
        obs.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        return obs;
    }

    private DataDefinition lastCD4Count() {
        ObsForPersonDataDefinition obs = new ObsForPersonDataDefinition();
        obs.setWhich(TimeQualifier.LAST);
        obs.setQuestion(Dictionary.getConcept(Dictionary.CD4_COUNT));
        obs.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        return obs;
    }

    private DataDefinition initialCD4Count() {
        ObsForPersonDataDefinition obs = new ObsForPersonDataDefinition();
        obs.setWhich(TimeQualifier.FIRST);
        obs.setQuestion(Dictionary.getConcept(Dictionary.CD4_COUNT));
        obs.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        return obs;
    }

    private DataDefinition tbStatus() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Anti TB start date", new TBStartDateCalculation());
        cd.setName("Anti TB start date");
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition iptStatus() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Current IPT status", new CurrentIPTStatusCalculation());
        cd.setName("Current IPT status");
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition hivProgramEnrollment() {
        Program hivEnrollmentProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        ProgramEnrollmentsForPatientDataDefinition hivEnrollment = new ProgramEnrollmentsForPatientDataDefinition();
        hivEnrollment.setWhichEnrollment(TimeQualifier.FIRST);
        hivEnrollment.setProgram(hivEnrollmentProgram);
        hivEnrollment.addParameter(new Parameter("enrolledOnOrBefore", "Before Date", Date.class));

        return hivEnrollment;
    }

    private DataDefinition dateOfDiagnosis() {
        ObsForPersonDataDefinition obs = new ObsForPersonDataDefinition();
        obs.setWhich(TimeQualifier.LAST);
        obs.setQuestion(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS));
        obs.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
        return obs;
    }

    private DataDefinition transferIn() {

        CalculationDataDefinition cd = new CalculationDataDefinition("Transfer in", new IsTransferInCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }
    private DataDefinition startDateForCurrentRegimen() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Start date for Current regimen", new CurrentARTStartDateCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition dateTransferredIn() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Transfer in date", new TransferInDateCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition dateOfTbDiagnosis() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Date of TB diagnosis", new ScreenedForTbAndDiagnosedCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition dateMedicallyEligibleForART() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Date medically eligible for ART", new DateMedicallyEligibleForARTCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition InitialARTRegimen() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Original/Initial ART regimen", new InitialArtRegimenCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition initialArtStartDate() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Initial ART start Date", new InitialArtStartDateCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition currentArtRegimen() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Current ART regimen", new CurrentArtRegimenCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition dateOfLastVisit() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Date of Last Visit", new DateLastSeenWithNoEncountersCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition viralLoad() {
        CalculationDataDefinition cd = new CalculationDataDefinition("viral load", new ViralLoadListCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition aliveAndOnFollowUp() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Alive and on Follow-up", new AliveAndOnFollowUpCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition stoppedArt() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Stopped", new StoppedARTCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition stoppedArtDate() {
        CalculationDataDefinition cd = new CalculationDataDefinition("Stopped date", new StoppedARTDateCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

    private DataDefinition returnVisitDate() {
        CalculationDataDefinition cd = new CalculationDataDefinition("return date", new LastReturnVisitDateCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }
//additional indicators added recently
    private DataDefinition preArtStatus(int years) {
        CalculationDataDefinition cd = new CalculationDataDefinition("pre-ART status", new PreARTYearsCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addCalculationParameter("years", years);
        return cd;
    }

    private DataDefinition artStatus(int years) {
        CalculationDataDefinition cd = new CalculationDataDefinition("ART status", new ARTYearsCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addCalculationParameter("years", years);
        return cd;
    }

    private DataDefinition regimen(int years) {
        CalculationDataDefinition cd = new CalculationDataDefinition("regimen", new RegimenYearsCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addCalculationParameter("years", years);
        return cd;
    }

    private DataDefinition artLine(int years) {
        CalculationDataDefinition cd = new CalculationDataDefinition("artLine", new ArtLineYearsCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addCalculationParameter("years", years);
        return cd;
    }

    private DataDefinition currentArtLine() {
        CalculationDataDefinition cd = new CalculationDataDefinition("currentArtLine", new CurrentArtLineCalculation());
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cd;
    }

}
