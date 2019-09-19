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

import org.openmrs.Concept;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IsTransferInAndHasDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.*;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfFirstCTXCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.HeightAtArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.WeightAtArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.*;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.MedicallyEligibleConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HEIRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.*;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.HTSDiscordanceDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.TBScreeningAtLastVisitDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.EDCandANCNumberPreg1DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.EDCandANCNumberPreg2DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.EDCandANCNumberPreg3DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.*;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by codehub on 11/06/15.
 */
@Component
@Builds({"kenyaemr.hiv.report.art.cohort.analysis.art.6","kenyaemr.hiv.report.art.cohort.analysis.art.12","kenyaemr.hiv.report.art.cohort.analysis.art.24","kenyaemr.hiv.report.art.cohort.analysis.art.36","kenyaemr.hiv.report.art.cohort.analysis.art.48","kenyaemr.hiv.report.art.cohort.analysis.art.60"})
public class ArtCohortAnalysisReportBuilder extends AbstractHybridReportBuilder {

    @Autowired
    private ArtCohortLibrary artCohortLibrary;

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    /**
     *
     * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#getParameters(ReportDescriptor)
     */
    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        Integer period = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CohortDefinition cd = artCohortLibrary.netCohortMonthsBetweenDatesGivenMonths(period);
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

    /**
     *
     * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(CohortReportDescriptor, PatientDataSetDefinition)
     */
    @Override
    protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        PatientIdentifierType tbRegNo = MetadataUtils.existing(PatientIdentifierType.class, TbMetadata._PatientIdentifierType.DISTRICT_REG_NUMBER);
        DataDefinition identifierDef_upn = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), new IdentifierConverter());
        DataDefinition identifierDef_tb = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), tbRegNo), new IdentifierConverter());
        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);
        Concept startIptConcept = Dictionary.getConcept("1265AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept startTbRxDateConcept = Dictionary.getConcept("1113AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept weightConcept = Dictionary.getConcept(Dictionary.WEIGHT_KG);

        dsd.setName("artCohortRegister");
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("ART Start Date", new CalculationDataDefinition("ART Start Date", new DateARV1Calculation()), "", new CalculationResultConverter());
        dsd.addColumn("UPN", identifierDef_upn, "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter());
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Telephone No", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Population Type", new PopulationTypeArtDataDefinition(), "");
        dsd.addColumn("coupleDiscordant", new HTSDiscordanceArtDataDefinition(), "");
        dsd.addColumn("First WHO Stage", new ObsForPersonDataDefinition("First WHO Stage", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), null, null), "", new WHOStageDataConverter());
        dsd.addColumn("Latest CD4", currentCd4Count(report), "onDate=${endDate}", new CurrentCd4Converter("value"));
        dsd.addColumn("Height at Art Start", new CalculationDataDefinition("Height at Art Start", new HeightAtArtStartDateCalculation()), "", new HeightConverter());
        dsd.addColumn("Weight at Art Start", new CalculationDataDefinition("Weight at Art Start", new WeightAtArtStartDateCalculation()), "", new WeightConverter());
        dsd.addColumn("CTX Start Date", new CalculationDataDefinition("Weight at Art Start", new DateOfFirstCTXCalculation()), "", new ArtCohortStartMonthYearDateConverter());
        dsd.addColumn("IPT Start Date", new ObsForPersonDataDefinition("IPT Start Date", TimeQualifier.FIRST, startIptConcept, null, null), "", new ObsMonthYearConverter());
        dsd.addColumn("TBRx Start Date", new ObsForPersonDataDefinition("TB Treatment Start Date", TimeQualifier.FIRST, startTbRxDateConcept, null, null), "", new ObsMonthYearConverter());
        dsd.addColumn("TB Reg", identifierDef_tb, "");
        dsd.addColumn("Pregnancy_1", new EDCandANCNumberPreg1DataDefinition(),"");
        dsd.addColumn("Pregnancy_2", new EDCandANCNumberPreg2DataDefinition(),"");
        dsd.addColumn("Pregnancy_3", new EDCandANCNumberPreg3DataDefinition(),"");
        dsd.addColumn("Initial ART Regimen", new CalculationDataDefinition("Initial ART Regimen", new InitialArtRegimenCalculation()), "", null);
        dsd.addColumn("ART First Sub", new ARTFirstSubstitutionDataDefinition(),"");
        dsd.addColumn("ART Second Sub", new ARTSecondSubstitutionDataDefinition(),"");
        dsd.addColumn("ART First Switch", new ARTFirstSwitchDataDefinition(),"");
        dsd.addColumn("ART Second Switch", new ARTSecondSwitchDataDefinition(),"");
        dsd.addColumn("Weight", new ObsForPersonDataDefinition("Weight", TimeQualifier.LAST, weightConcept, null, null), "", new ObsValueNumericConverter());
        dsd.addColumn("Recent Viral Load Result", new CalculationDataDefinition("Recent Viral Load Result", new ViralLoadResultCalculation("last")), "", new RDQASimpleObjectRegimenConverter("data"));
        dsd.addColumn("Recent Viral Load Result Date", new CalculationDataDefinition("Recent Viral Load Result Date", new ViralLoadResultCalculation("last")), "", new RDQASimpleObjectRegimenConverter("date"));
        dsd.addColumn("TB screening outcome", new TBScreeningAtLastVisitDataDefinition(), "", new TBScreeningConverter("outcome"));
        dsd.addColumn("ART Outcomes", patientOutComes(report), "onDate=${endDate}", new CalculationResultConverter());

    }

    private DataDefinition patientOutComes(HybridReportDescriptor descriptor) {
        int months = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CalculationDataDefinition cd = new CalculationDataDefinition("outcomes", new PatientArtOutComeCalculation());
        cd.addCalculationParameter("outcomePeriod", months);
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;

    }

    private DataDefinition currentCd4Count(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("currentCd4", new LastCd4Calculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }



}
