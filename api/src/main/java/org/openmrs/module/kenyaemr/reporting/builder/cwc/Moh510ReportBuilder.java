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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.ParentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAttributeCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ConceptNamesDataConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.Moh510CohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.cwc.DateOfFullImmunizationDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.cwc.DateOfVaccineDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.cwc.DateOfVitaminADataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
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
@Builds({"kenyaemr.mchcs.report.moh510"})
public class Moh510ReportBuilder extends AbstractReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	public Moh510ReportBuilder() {
		// TODO Auto-generated constructor stub
	}

    @SuppressWarnings("unchecked")
	@Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        return Arrays.asList(
                ReportUtils.map(moh510DataSetDefinition("immunizationRegister"), "startDate=${startDate},endDate=${endDate}")
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
	
	protected DataSetDefinition moh510DataSetDefinition(String datasetName) {

        String paramMapping = "startDate=${startDate},endDate=${endDate}";

        EncounterDataSetDefinition dsd = new EncounterDataSetDefinition(datasetName);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, MchMetadata._PatientIdentifierType.CWC_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Visit Date", new EncounterDatetimeDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Serial Number", new PersonIdDataDefinition(), "");
        dsd.addColumn("CWC Number", identifierDef, "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Date first seen", new ObsForPersonDataDefinition("Date first seen", TimeQualifier.FIRST, Dictionary.getConcept(Dictionary.DATE_FIRST_SEEN), null, null), "", new ObsValueDatetimeConverter());
        dsd.addColumn("Mothers full name", new CalculationDataDefinition("Mother's full name", new ParentCalculation("Mother")), "", new RDQACalculationResultConverter());
        dsd.addColumn("Fathers full name", new CalculationDataDefinition("Father's full name", new ParentCalculation("Father")), "", new RDQACalculationResultConverter());
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Telephone Number", new CalculationDataDefinition("Telephone Number", new PersonAttributeCalculation("Telephone contact")), "", new RDQACalculationResultConverter());

        dsd.addColumn("BCG", new DateOfVaccineDataDefinition("BCG", "BCG"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Polio birth Dose", new DateOfVaccineDataDefinition("Polio birth Dose", "OPV_birth"), "",  new DateConverter(DATE_FORMAT));
        dsd.addColumn("OPV 1", new DateOfVaccineDataDefinition("OPV 1", "OPV_1"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("OPV 2", new DateOfVaccineDataDefinition("OPV 2", "OPV_2"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("OPV 3", new DateOfVaccineDataDefinition("OPV 3", "OPV_3"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("IPV", new DateOfVaccineDataDefinition("IPV", "IPV"), "",new DateConverter(DATE_FORMAT));
        dsd.addColumn("DPT_HepB_Hib 1", new DateOfVaccineDataDefinition("DPT/Hep.B/Hib 1", "DPT_Hep_B_Hib_1"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("DPT_HepB_Hib 2", new DateOfVaccineDataDefinition("DPT/Hep.B/Hib 2", "DPT_Hep_B_Hib_2"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("DPT_HepB_Hib 3", new DateOfVaccineDataDefinition("DPT/Hep.B/Hib 3", "DPT_Hep_B_Hib_3"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("PCV 10(Pneumococcal) 1", new DateOfVaccineDataDefinition("PCV 10(Pneumococcal) 1", "PCV_10_1"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("PCV 10(Pneumococcal) 2", new DateOfVaccineDataDefinition("PCV 10(Pneumococcal) 2", "PCV_10_2"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("PCV 10(Pneumococcal) 3", new DateOfVaccineDataDefinition("PCV 10(Pneumococcal) 3", "PCV_10_3"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("ROTA 1", new DateOfVaccineDataDefinition("ROTA 1", "ROTA_1"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("ROTA 2", new DateOfVaccineDataDefinition("ROTA 2", "ROTA_2"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Vitamin A", new DateOfVitaminADataDefinition("Vitamin A"), "", null);
        dsd.addColumn("Measles 1", new DateOfVaccineDataDefinition("Measles 1", "Measles_rubella_1"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Yellow Fever", new DateOfVaccineDataDefinition("Yellow Fever", "Yellow_fever"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Fully Immunized Child", new DateOfFullImmunizationDataDefinition("Fully Immunized Child"), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Measles 2", new DateOfVaccineDataDefinition("Measles 2", "Measles_rubella_2"), "", new DateConverter(DATE_FORMAT));
        Moh510CohortDefinition cd = new Moh510CohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addRowFilter(cd, paramMapping);
        return dsd;
    }

}
