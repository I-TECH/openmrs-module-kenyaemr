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
import org.openmrs.module.kenyaemr.calculation.library.mchcs.ParentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAttributeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.VaccinationDateCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ConceptNamesDataConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.CustomDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.library.moh510.Moh510CohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
@Builds({"kenyaemr.mchcs.report.moh510"})
public class Moh510ReportBuilder extends AbstractHybridReportBuilder{
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	public Moh510ReportBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	protected Mapped<CohortDefinition> childrenEnrolledInCWCCohort() {
		CohortDefinition cd = new Moh510CohortLibrary().enrolledInCWC();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));		
		return ReportUtils.map(cd, "onOrAfter=${startDate},onOrBefore=${endDate}");
	}

    @SuppressWarnings("unchecked")
	@Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition moh510PDSD = moh510DataSetDefinition("immunizationRegister");
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
				new Parameter("yearBasedReporting", "", String.class)
		);
	}
	
	protected PatientDataSetDefinition moh510DataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, MchMetadata._PatientIdentifierType.CWC_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("Serial Number", new PersonIdDataDefinition(), "");
        dsd.addColumn("CWC Number", identifierDef, "");
        dsd.addColumn("Childs Name", nameDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Date first seen", new ObsForPersonDataDefinition("Date first seen", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.DATE_FIRST_SEEN), null, null), "", new ObsValueDatetimeConverter());
        dsd.addColumn("Fathers full name", new CalculationDataDefinition("Father's full name", new ParentCalculation("Father")), "", new RDQACalculationResultConverter());       
        dsd.addColumn("Mothers full name", new CalculationDataDefinition("Mother's full name", new ParentCalculation("Mother")), "", new RDQACalculationResultConverter());       
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());       
        dsd.addColumn("Telephone Number", new CalculationDataDefinition("Telephone Number", new PersonAttributeCalculation("Telephone contact")), "", new RDQACalculationResultConverter());

        dsd.addColumn("BCG", new ObsForPersonDataDefinition("BCG", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.BACILLE_CAMILE_GUERIN_VACCINATION), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Polio birth Dose", new CalculationDataDefinition("Polio birth Dose", new VaccinationDateCalculation(Dictionary.POLIO_VACCINATION_ORAL, 0)), "", new ObsDatetimeConverter());
        dsd.addColumn("OPV 1", new CalculationDataDefinition("OPV 1", new VaccinationDateCalculation(Dictionary.POLIO_VACCINATION_ORAL, 1)), "", new CustomDateConverter());
        dsd.addColumn("OPV 2", new CalculationDataDefinition("OPV 2", new VaccinationDateCalculation(Dictionary.POLIO_VACCINATION_ORAL, 2)), "", new CustomDateConverter());
        dsd.addColumn("OPV 3", new CalculationDataDefinition("OPV 3", new VaccinationDateCalculation(Dictionary.POLIO_VACCINATION_ORAL, 3)), "", new CustomDateConverter());
        dsd.addColumn("IPV", new ObsForPersonDataDefinition("IPV", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.POLIO_VACCINATION_INACTIVATED), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("DPT_HepB_Hib 1", new CalculationDataDefinition("DPT/Hep.B/Hib 1", new VaccinationDateCalculation(Dictionary.DIPHTHERIA_TETANUS_AND_PERTUSSIS_VACCINATION, 1)), "", new CustomDateConverter());
        dsd.addColumn("DPT_HepB_Hib 2", new CalculationDataDefinition("DPT/Hep.B/Hib 2", new VaccinationDateCalculation(Dictionary.DIPHTHERIA_TETANUS_AND_PERTUSSIS_VACCINATION, 2)), "", new CustomDateConverter());
        dsd.addColumn("DPT_HepB_Hib 3", new CalculationDataDefinition("DPT/Hep.B/Hib 3", new VaccinationDateCalculation(Dictionary.DIPHTHERIA_TETANUS_AND_PERTUSSIS_VACCINATION, 3)), "", new CustomDateConverter());
        dsd.addColumn("PCV 10(Pneumococcal) 1", new CalculationDataDefinition("PCV 10(Pneumococcal) 1", new VaccinationDateCalculation(Dictionary.PNEUMOCOCCAL_CONJUGATE_VACCINE, 1)), "", new CustomDateConverter());
        dsd.addColumn("PCV 10(Pneumococcal) 2", new CalculationDataDefinition("PCV 10(Pneumococcal) 2", new VaccinationDateCalculation(Dictionary.PNEUMOCOCCAL_CONJUGATE_VACCINE, 2)), "", new CustomDateConverter());
        dsd.addColumn("PCV 10(Pneumococcal) 3", new CalculationDataDefinition("PCV 10(Pneumococcal) 3", new VaccinationDateCalculation(Dictionary.PNEUMOCOCCAL_CONJUGATE_VACCINE, 3)), "", new CustomDateConverter());
        dsd.addColumn("ROTA 1", new CalculationDataDefinition("ROTA 1", new VaccinationDateCalculation(Dictionary.ROTA_VIRUS_VACCINE, 1)), "", new CustomDateConverter());
        dsd.addColumn("ROTA 2", new CalculationDataDefinition("ROTA 2", new VaccinationDateCalculation(Dictionary.ROTA_VIRUS_VACCINE, 2)), "", new CustomDateConverter());
        dsd.addColumn("Vitamin A", new ObsForPersonDataDefinition("Vitamin A", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.ADMINISTRATION_OF_VITAMIN_A), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Measles 1", new CalculationDataDefinition("Measles 1", new VaccinationDateCalculation(Dictionary.MEASLES_RUBELLA_VACCINE, 1)), "", new CustomDateConverter());
        dsd.addColumn("Yellow Fever", new ObsForPersonDataDefinition("Yellow Fever", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.YELLOW_FEVER_VACCINE), null, null), "", new ObsDatetimeConverter());
        dsd.addColumn("Fully Immunized Child", new ObsForPersonDataDefinition("Fully Immunized Child", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.FULLY_IMMUNIZED_CHILD), null, null), "", new ConceptNamesDataConverter());
        dsd.addColumn("Measles 2", new CalculationDataDefinition("Measles 2", new VaccinationDateCalculation(Dictionary.MEASLES_RUBELLA_VACCINE, 2)), "", new CustomDateConverter());
        
        return dsd;
    }

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		
		return childrenEnrolledInCWCCohort();
	}
}
