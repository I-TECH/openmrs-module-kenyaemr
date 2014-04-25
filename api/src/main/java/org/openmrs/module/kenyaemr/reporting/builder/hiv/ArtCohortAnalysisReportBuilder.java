package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Builds("kenyaemr.hiv.report.artCohortAnalysis")
public class ArtCohortAnalysisReportBuilder extends AbstractCohortReportBuilder {

	@Autowired
	ArtCohortLibrary artCohortLibrary;

	@Override
	protected void addColumns(CohortReportDescriptor report, PatientDataSetDefinition dsd) {

		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

		dsd.addColumn(upn.getName(), identifierDef, "");
		dsd.addColumn("Birth date", new BirthdateDataDefinition(), "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(CohortReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		CohortDefinition cd = artCohortLibrary.netCohortMonths(12);
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("ondDate", new Date());
		return new Mapped<CohortDefinition>(cd, mappings);
	}
}
