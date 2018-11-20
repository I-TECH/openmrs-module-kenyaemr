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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.DewormingCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAttributeCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.library.moh510.Moh510CohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
@Builds({"kenyaemr.mchcs.report.moh511"})
public class Moh511ReportBuilder extends AbstractHybridReportBuilder{

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	
	protected Mapped<CohortDefinition> childrenEnrolledInCWCCohort() {
		CohortDefinition cd = new Moh510CohortLibrary().enrolledInCWC();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));		
		return ReportUtils.map(cd, "onOrAfter=${startDate},onOrBefore=${endDate}");
	}

    @SuppressWarnings("unchecked")
	@Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

    	Date reportingStartDate = report.getParameter("startDate") != null && !report.getParameter("startDate").equals("") ? (Date)report.getParameter("startDate").getDefaultValue(): null;
    	Date reportingEndDate = report.getParameter("endDate") != null && !report.getParameter("endDate").equals("") ? (Date)report.getParameter("endDate").getDefaultValue(): null;
    	
        PatientDataSetDefinition moh510PDSD = moh510DataSetDefinition("cwcRegister", reportingStartDate, reportingEndDate);
        moh510PDSD.addRowFilter(childrenEnrolledInCWCCohort());
        DataSetDefinition moh510DSD = moh510PDSD;

        return Arrays.asList(
                ReportUtils.map(moh510DSD, "startDate=${startDate},endDate=${endDate}")
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
	
	protected PatientDataSetDefinition moh510DataSetDefinition(String datasetName,Date reportingStartDate, Date reportingEndDate) {
		EncounterAndObsDataSetDefinition edsd = new EncounterAndObsDataSetDefinition();
		edsd.setName(datasetName);

		PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        
        PatientIdentifierType cwcn = MetadataUtils.existing(PatientIdentifierType.class, MchMetadata._PatientIdentifierType.CWC_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(cwcn.getName(), cwcn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("Serial Number", new PersonIdDataDefinition(), "");
        dsd.addColumn("Revisit this year", new ObsForPersonDataDefinition("Revisit this year", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REVISIT_THIS_YEAR),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        dsd.addColumn("CWC Number", identifierDef, "");
        dsd.addColumn("Full Name", nameDef, "");
        dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
        dsd.addColumn("Sublocation", new CalculationDataDefinition("Sublocation", new PersonAddressCalculation("sublocation")), "", new RDQACalculationResultConverter());       
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());       
        dsd.addColumn("Telephone Number", new CalculationDataDefinition("Telephone Number", new PersonAttributeCalculation("Telephone contact")), "", new RDQACalculationResultConverter());

        dsd.addColumn("Weight in KG", new ObsForPersonDataDefinition("Weight in KG", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.WEIGHT_KG),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
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
        //dsd.addColumn("Issued itn net", new ObsForPersonDataDefinition("Issued itn net", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.REVISIT_THIS_YEAR),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        //dsd.addColumn("Hiv testing", new ObsForPersonDataDefinition("Hiv testing", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.NOT_HIV_TESTED),reportingStartDate, reportingEndDate), "", new RDQACalculationResultConverter());
        
        return dsd;
    }

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		
		return childrenEnrolledInCWCCohort();
	}

}
