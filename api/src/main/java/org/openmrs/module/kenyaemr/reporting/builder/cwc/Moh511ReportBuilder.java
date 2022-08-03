/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.cwc;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAttributeCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.Moh511CohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCAssessedDevelopmentalMilestonesDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCDewormedDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCDisabilityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCExclusiveBreastfeeding6MonthsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCFollowupDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCHeightDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCMuacDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCReferredToDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCRemarksDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCVitaminASupplementationDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCWeightCategoriesDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCWeightDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.MaternityBirthNotificationNumberDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
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
@Builds({"kenyaemr.mchcs.report.moh511"})
public class Moh511ReportBuilder extends AbstractReportBuilder {

	public static final String DATE_FORMAT = "dd/MM/yyyy";

    @SuppressWarnings("unchecked")
	@Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(moh511DataSetDefinition("cwcRegister"), "startDate=${startDate},endDate=${endDate}")
        );
    }

	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class),
				new Parameter("dateBasedReporting", "", String.class)
		);
	}
	
	protected DataSetDefinition moh511DataSetDefinition(String datasetName) {

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition(datasetName);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";


        PatientIdentifierType cwcn = MetadataUtils.existing(PatientIdentifierType.class, MchMetadata._PatientIdentifierType.CWC_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(cwcn.getName(), cwcn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Serial Number", new PersonIdDataDefinition(), "");
        //dsd.addColumn("Revisit this year", new ObsForPersonDataDefinition("Revisit this year", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REVISIT_THIS_YEAR),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("CWC Number", identifierDef, "");
        dsd.addColumn("Birth Notification Number", new MaternityBirthNotificationNumberDataDefinition(),"");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("County", new CalculationDataDefinition("County", new PersonAddressCalculation("countyDistrict")), "");
        dsd.addColumn("Sub county", new CalculationDataDefinition("Sub county", new PersonAddressCalculation("stateProvince")), "");
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "");
        dsd.addColumn("Telephone Number", new CalculationDataDefinition("Telephone Number", new PersonAttributeCalculation("Telephone contact")), "");
        dsd.addColumn("Weight", new HEICWCWeightDataDefinition(), "");
        dsd.addColumn("Weight category", new HEICWCWeightCategoriesDataDefinition(), "");
        dsd.addColumn("Height", new HEICWCHeightDataDefinition(), "");
        dsd.addColumn("Muac", new HEICWCMuacDataDefinition(), "");
        dsd.addColumn("Exclusive Breast feeding  (less than 6 months)", new HEICWCExclusiveBreastfeeding6MonthsDataDefinition(), "");
        dsd.addColumn("Vit A supplementation 6-59m", new HEICWCVitaminASupplementationDataDefinition(), "");
        dsd.addColumn("Dewormed", new HEICWCDewormedDataDefinition(), "");
        dsd.addColumn("MNPs Supplementation(6-23 children)", new HEICWCDewormedDataDefinition(), "");
        dsd.addColumn("Assessed for Developmental milestones", new HEICWCAssessedDevelopmentalMilestonesDataDefinition(), "");
       // dsd.addColumn("Danger signs", "", ""); TODO: updates on new mockups
        dsd.addColumn("Disability", new HEICWCDisabilityDataDefinition(), "");
        // dsd.addColumn("Immunization Status Up to Date",  ""); TODO:
        //dsd.addColumn("Follow up", new HEICWCFollowupDataDefinition(), "");      TODO: To be ETLed
        dsd.addColumn("Referred to", new HEICWCReferredToDataDefinition(), "");
        //dsd.addColumn("Reasons for referral", , ""); TODO: updates on new mockups
        dsd.addColumn("Remarks", new HEICWCRemarksDataDefinition(), "");




       /* dsd.addColumn("Weight in KG", new ObsForPersonDataDefinition("Weight in KG", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.WEIGHT_KG),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
       */

        Moh511CohortDefinition cd = new Moh511CohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;
    }


}
