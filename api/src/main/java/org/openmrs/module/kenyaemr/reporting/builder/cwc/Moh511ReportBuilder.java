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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.Moh510CohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.Moh511CohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.maternity.MaternityBirthNotificationNumberDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
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
        PatientIdentifierType bnn = MetadataUtils.existing(PatientIdentifierType.class, MchMetadata._PatientIdentifierType.B);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(cwcn.getName(), cwcn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("Serial Number", new PersonIdDataDefinition(), "");
        //dsd.addColumn("Revisit this year", new ObsForPersonDataDefinition("Revisit this year", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REVISIT_THIS_YEAR),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("CWC Number", identifierDef, "");
        dsd.addColumn("Birth Notification Number", new MaternityBirthNotificationNumberDataDefinition(),"");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("County", new CalculationDataDefinition("County", new PersonAddressCalculation("countyDistrict")), "", new RDQACalculationResultConverter());
        dsd.addColumn("Sub county", new CalculationDataDefinition("Sub county", new PersonAddressCalculation("stateProvince")), "", new RDQACalculationResultConverter());
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Telephone Number", new CalculationDataDefinition("Telephone Number", new PersonAttributeCalculation("Telephone contact")), "", new RDQACalculationResultConverter());
        dsd.addColumn("Weight in KG", new ObsForPersonDataDefinition("Weight in KG", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.WEIGHT_KG),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());

       /* dsd.addColumn("Weight in KG", new ObsForPersonDataDefinition("Weight in KG", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.WEIGHT_KG),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Weight category", new ObsForPersonDataDefinition("Weight category", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.WEIGHT_FOR_AGE_STATUS),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("height/length in cm", new ObsForPersonDataDefinition("height/length in cm", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.HEIGHT_CM),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Stunted", new ObsForPersonDataDefinition("Stunted", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.NUTRITIONAL_STUNTING),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("MUAC", new ObsForPersonDataDefinition("MUAC", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.MUAC),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Vit A supplementation 6-59m", new ObsForPersonDataDefinition("Vit A supplementation 6-59m", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.VITAMIN_A),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Dewormed", new CalculationDataDefinition("Dewormed", new DewormingCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Supplemented with mnps 6-23 month", new ObsForPersonDataDefinition("Supplemented with mnps 6-23 month", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.MICRONUTRIENT_SUPPORT),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Exclusive breastfeeding 0-6months", new ObsForPersonDataDefinition("Exclusive breastfeeding 0-6months", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Any disability", new ObsForPersonDataDefinition("Any disability", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.DISABILITY),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Type of follow up", new ObsForPersonDataDefinition("Type of follow up", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.MALNUTRITION_TYPE),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Counseled on", new ObsForPersonDataDefinition("Counseled on", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.COUNSELING_ORDERS),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Referrals from", new ObsForPersonDataDefinition("Referrals from", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REFERRED_FROM),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("Referrals to", new ObsForPersonDataDefinition("Referrals to", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REFERRED_TO),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        *///dsd.addColumn("Issued itn net", new ObsForPersonDataDefinition("Issued itn net", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REVISIT_THIS_YEAR),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        //dsd.addColumn("Hiv testing", new ObsForPersonDataDefinition("Hiv testing", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.NOT_HIV_TESTED),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());

        Moh511CohortDefinition cd = new Moh511CohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;
    }


}
