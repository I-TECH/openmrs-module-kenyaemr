/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.ipt;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HEIRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
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
@Builds({"kenyaemr.ipt.report.iptRegister"})
public class IPTRegisterReportBuilder extends AbstractHybridReportBuilder {
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		return allPatientsCohort();
	}

    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new HEIRegisterCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("IPTPatients");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition iptPatients = iptDataSetDefinition();
        iptPatients.addRowFilter(allPatientsCohort());
        DataSetDefinition iptPatientsDSD = iptPatients;


        return Arrays.asList(
                ReportUtils.map(iptPatientsDSD, "startDate=${startDate},endDate=${endDate}")
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

	protected PatientDataSetDefinition iptDataSetDefinition() {

		PatientDataSetDefinition dsd = new PatientDataSetDefinition("IPTRegister");
		dsd.addSortCriteria("DOBAndAge", SortCriteria.SortDirection.DESC);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);
		DataDefinition addressDef = new ConvertedPersonDataDefinition("identifier", new PreferredAddressDataDefinition(), identifierFormatter);

		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");

		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("Serial Number", new PersonIdDataDefinition(), "");
		dsd.addColumn("Sub County Registration", new RegistrationSubcountyDataDefinition(), "");
		dsd.addColumn("Sub County Registration Date", new RegistrationSubcountyDataDefinition(), "");
		dsd.addColumn("OPD or IPD and CCC Number", new OPDIPDCCCNoDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("Nationality", new NationalityDataDefinition(), "");
		dsd.addColumn("Physical Address", new PhysicalAddressDataDefinition(), "");
		dsd.addColumn("Patient Phone number", new PhoneNumberDataDefinition(), "");
		dsd.addColumn("Supporter Phone number", new SupporterPhoneNumberDataDefinition(), "");
		dsd.addColumn("WeightAtStart", new WeightAtStartDataDefinition(), "");
		dsd.addColumn("Height", new HeightDataDefinition(), "");
		dsd.addColumn("BMI or Z Score or MUAC", new BMIZScoreMUACDataDefinition(), "");
		dsd.addColumn("INH Dose(Mg)", new INHDataDefinition(), "");
		dsd.addColumn("VTB 6(Pyridoxine)Dose", new VTBDataDefinition(), "");
		dsd.addColumn("Treatment start date", new TreatmentStartDateDataDefinition(),"");
		dsd.addColumn("Month 1 drug collection date", new Month1DrugCollectionDateDataDefinition(),"");
		dsd.addColumn("Month 2 drug collection date", new Month2DrugCollectionDateDataDefinition(),"");
		dsd.addColumn("Month 3 drug collection date", new Month3DrugCollectionDateDataDefinition(),"");
		dsd.addColumn("Month 4 drug collection date", new Month4DrugCollectionDateDataDefinition(),"");
		dsd.addColumn("Month 5 drug collection date", new Month5DrugCollectionDateDataDefinition(),"");
		dsd.addColumn("Month 6 drug collection date", new Month6DrugCollectionDateDataDefinition(),"");
		dsd.addColumn("HIV Status", new HIVStatusDataDefinition(),"");
		dsd.addColumn("Started Cotrimoxazole preventive Therapy or Dapsone", new DapsoneCotrimoxazoleDataDefinition(),"");
		dsd.addColumn("Started ART", new StartedARTDataDefinition(),"");
		dsd.addColumn("IPT Outcome", new IPTOutcomeDataDefinition(),"");
		dsd.addColumn("Reasons for IPT Discontinuation", new IPTDiscontinuationReasonDataDefinition(),"");
		dsd.addColumn("M6 TB Status and Date", new Month6TBStatusDateDataDefinition(),"");
		dsd.addColumn("M12 TB Status and Date", new Month12TBStatusDateDataDefinition(),"");
		dsd.addColumn("M18 TB Status and Date", new Month18TBStatusDateDataDefinition(),"");
		dsd.addColumn("M24 TB Status and Date", new Month24TBStatusDateDataDefinition(),"");
		dsd.addColumn("Remarks", new IPTRemarksDataDefinition(),"");

		return dsd;
	}
}
