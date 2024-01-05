/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.cacx;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.CountyAddressCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.CACXRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.cacx.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.cancer.report.cancerRegister"})
public class CancerScreeningRegisterReportBuilder extends AbstractReportBuilder {
    public static final String ENC_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
        new Parameter("startDate", "Start Date", Date.class),
        new Parameter("endDate", "End Date", Date.class),
        new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
        ReportUtils.map(datasetColumns(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition datasetColumns() {
        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
        dsd.setName("CancerInformation");
        dsd.setDescription("Visit information");
        dsd.addSortCriteria("Visit Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String paramMapping = "startDate=${startDate},endDate=${endDate}";

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        PatientIdentifierType nationalId = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nationalId.getName(), nationalId), identifierFormatter);

        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);

        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(),"", new DateConverter(ENC_DATE_FORMAT));
        dsd.addColumn("Visit Type", new CACXVisitTypeDataDefinition(), null);
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("ID Number", identifierDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Phone Number", new PersonAttributeDataDefinition(phoneNumber), "");
        dsd.addColumn("Age in years", new AgeDataDefinition(), "");
        //dsd.addColumn("HIV Status", new CACXHivStatusDataDefinition(), null);
        dsd.addColumn("Hiv Status",new HivStatusDataDefinition(),null);
        dsd.addColumn("Population Type", new LatestPopulationTypeDataDefinition(), null);
        dsd.addColumn("County of Residence", new CalculationDataDefinition("County", new CountyAddressCalculation()), "", null);
        dsd.addColumn("Visit Type", new VisitTypeDataDefinition(), null);
        dsd.addColumn("Cause of Post Treatment Complications", new CauseOfPostTxComplicationsDataDefinition(), null);
        dsd.addColumn("Cervical Cancer", new CervicalCancerDataDefinition(), null);
        dsd.addColumn("Colposcopy Screening Method",new ColposcopyScreeningMethodDataDefinition(),null);
        dsd.addColumn("Hpv Screening Method",new HpvScreeningMethodDataDefinition(),null);
        dsd.addColumn("Pap Smear Screening Method",new PapSmearScreeningMethodDataDefinition(),null);
        dsd.addColumn("Via Vili Screening Method",new ViaViliScreeningMethodDataDefinition(),null);
        dsd.addColumn("Colposcopy Screening Result",new ColposcopyScreeningResultDataDefinition(),null);
        dsd.addColumn("Hpv Screening Result",new HpvScreeningResultDataDefinition(),null);
        dsd.addColumn("Pap Smear Screening Result",new PapSmearScreeningResultDataDefinition(),null);
        dsd.addColumn("Via Vili Screening Result",new ViaViliScreeningResultDataDefinition(),null);
        dsd.addColumn("Colposcopy Treatment Method",new ColposcopyTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Hpv Treatment Method",new HpvTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Pap Smear Treatment Method",new PapSmearTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Via Vili Treatment Method",new ViaViliTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Colorectal Cancer",new ColorectalCancerDataDefinition(),null);
        dsd.addColumn("Fecal Occult Screening Method",new FecalOccultScreeningMethodDataDefinition(),null);
        dsd.addColumn("Colonoscopy Method",new ColonoscopyMethodDataDefinition(),null);
        dsd.addColumn("Fecal Occult Screening Results",new FecalOccultScreeningResultsDataDefinition(),null);
        dsd.addColumn("Colonoscopy Method Results",new ColonoscopyMethodResultsDataDefinition(),null);
        dsd.addColumn("Fecal Occult Screening Treatment",new FecalOccultScreeningTreatmentDataDefinition(),null);
        dsd.addColumn("Colonoscopy Method Treatment",new ColonoscopyMethodTreatmentDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Cancer",new RetinoblastomaCancerDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Eua Screening Method",new RetinoblastomaEuaScreeningMethodDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Gene Method",new RetinoblastomaGeneMethodDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Eua Screening Results",new RetinoblastomaEuaScreeningResultsDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Gene Method Results",new RetinoblastomaGeneMethodResultsDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Eua Treatment",new RetinoblastomaEuaTreatmentDataDefinition(),null);
        dsd.addColumn("Retinoblastoma Gene Treatment",new RetinoblastomaGeneTreatmentDataDefinition(),null);
        dsd.addColumn("Prostate Cancer",new ProstateCancerDataDefinition(),null);
        dsd.addColumn("Digital Rectal Prostate Examination",new DigitalRectalProstateExaminationDataDefinition(),null);
        dsd.addColumn("Digital Rectal Prostate Results",new DigitalRectalProstateResultsDataDefinition(),null);
        dsd.addColumn("Digital Rectal Prostate Treatment",new DigitalRectalProstateTreatmentDataDefinition(),null);
        dsd.addColumn("Prostatic Specific Antigen Test",new ProstaticSpecificAntigenTestDataDefinition(),null);
        dsd.addColumn("Prostatic Specific Antigen Results",new ProstaticSpecificAntigenResultsDataDefinition(),null);
        dsd.addColumn("Prostatic Specific Antigen Treatment",new ProstaticSpecificAntigenTreatmentDataDefinition(),null);
        dsd.addColumn("Breast Cancer",new BreastCancerDataDefinition(),null);
        dsd.addColumn("Clinical Breast Examination Screening Method",new ClinicalBreastExaminationScreeningMethodDataDefinition(),null);
        dsd.addColumn("Ultrasound Screening Method",new UltrasoundScreeningMethodDataDefinition(),null);
        dsd.addColumn("Mammography Smear Screening Method",new MammographySmearScreeningMethodDataDefinition(),null);
        dsd.addColumn("Clinical Breast Examination Screening Result",new ClinicalBreastExaminationScreeningResultDataDefinition(),null);
        dsd.addColumn("Ultrasound Screening Result",new UltrasoundScreeningResultDataDefinition(),null);
        dsd.addColumn("Mammography Screening Result",new MammographyScreeningResultDataDefinition(),null);
        dsd.addColumn("Clinical Breast Examination Treatment Method",new ClinicalBreastExaminationTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Ultrasound Treatment Method",new UltrasoundTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Mammography Treatment Method",new MammographyTreatmentMethodDataDefinition(),null);
        dsd.addColumn("Oral Cancer",new OralCancerDataDefinition(),null);
        dsd.addColumn("Oral Cancer Visual Exam Method",new OralCancerVisualExamMethodDataDefinition(),null);
        dsd.addColumn("Oral Cancer Cytology Method",new OralCancerCytologyMethodDataDefinition(),null);
        dsd.addColumn("Oral Cancer Imaging Method",new OralCancerImagingMethodDataDefinition(),null);
        dsd.addColumn("Oral Cancer Biopsy Method",new OralCancerBiopsyMethodDataDefinition(),null);
        dsd.addColumn("Oral Cancer Visual Exam Results",new OralCancerVisualExamResultsDataDefinition(),null);
        dsd.addColumn("Oral Cancer Cytology Results",new OralCancerCytologyResultsDataDefinition(),null);
        dsd.addColumn("Oral Cancer Imaging Results",new OralCancerImagingResultsDataDefinition(),null);
        dsd.addColumn("Oral Cancer Biopsy Results",new OralCancerBiopsyResultsDataDefinition(),null);
        dsd.addColumn("Oral Cancer Visual Exam Treatment",new OralCancerVisualExamTreatmentDataDefinition(),null);
        dsd.addColumn("Oral Cancer Cytology Treatment",new OralCancerCytologyTreatmentDataDefinition(),null);
        dsd.addColumn("Oral Cancer Imaging Treatment",new OralCancerImagingTreatmentDataDefinition(),null);
        dsd.addColumn("Oral Cancer Biopsy Treatment",new OralCancerBiopsyTreatmentDataDefinition(),null);
        dsd.addColumn("Smoke Cigarattes",new SmokeCigarattesDataDefinition(),null);
        dsd.addColumn("Other Forms Tobacco",new OtherFormsTobaccoDataDefinition(),null);
        dsd.addColumn("Take Alcohol",new TakeAlcoholDataDefinition(),null);
        dsd.addColumn("Previous Treatment",new PreviousTreatmentDataDefinition(),null);
        dsd.addColumn("Previous Treatment Specify",new PreviousTreatmentSpecifyDataDefinition(),null);
        dsd.addColumn("Signs Symptoms",new SignsSymptomsDataDefinition(),null);
        dsd.addColumn("Signs Symptoms Specify",new SignsSymptomsSpecifyDataDefinition(),null);
        dsd.addColumn("Family History",new FamilyHistoryDataDefinition(),null);
        dsd.addColumn("Number Of Years Smoked",new NumberOfYearsSmokedDataDefinition(),null);
        dsd.addColumn("Number Of Cigarette Per Day",new NumberOfCigarettePerDayDataDefinition(),null);
        dsd.addColumn("Referred Out",new ReferredOutDataDefinition(),null);
        dsd.addColumn("Refferal To/From", new CACXReferralDataDefinition(), null);
        dsd.addColumn("Referral Reason",new ReferralReasonDataDefinition(),null);
        dsd.addColumn("Follow Up Date", new CACXFollowUpDateDataDefinition(), null);
        dsd.addColumn("Clinical Notes", new ClinicalNotesDataDefinition(), null);


        CACXRegisterCohortDefinition cd = new CACXRegisterCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;
    }
}