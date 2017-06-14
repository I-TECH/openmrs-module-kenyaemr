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
import org.openmrs.module.kenyaemr.calculation.library.NumberOfDaysLateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAttributeCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ObsValueDatetimeConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsCohortLibrary;
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
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
@Builds({"kenyaemr.mchcs.report.vaccinationAppointments"})
public class VaccinationAppointmentReportBuilder extends AbstractHybridReportBuilder{

	public VaccinationAppointmentReportBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
		return vaccinationAppointmentsInCWCCohort();

	}
	
	protected Mapped<CohortDefinition> vaccinationAppointmentsInCWCCohort() {
		CohortDefinition cd = new MchcsCohortLibrary().scheduledVisitsInCWC();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));		
		return ReportUtils.map(cd, "onOrAfter=${startDate},onOrBefore=${endDate}");
	}
	
	@Override
	protected List<Parameter> getParameters(ReportDescriptor descriptor) {
		return Arrays.asList(
				new Parameter("startDate", "Start Date", Date.class),
				new Parameter("endDate", "End Date", Date.class),
				new Parameter("dateBasedReporting", "", String.class)
		);
	}

	protected PatientDataSetDefinition vaccinationAppointmentsDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, MchMetadata._PatientIdentifierType.CWC_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("CWC Number", identifierDef, ""); 
		dsd.addColumn("Age", new AgeDataDefinition(), "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
        dsd.addColumn("Appointment Date", new ObsForPersonDataDefinition("Next Appointment Date", TimeQualifier.LAST, Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), null, null), "", new ObsValueDatetimeConverter());
        dsd.addColumn("Number of days to Appointment", new CalculationDataDefinition("Number of days to Appointment", new NumberOfDaysLateCalculation()), "", new RDQACalculationResultConverter());
        dsd.addColumn("Mothers phone Number", new CalculationDataDefinition("Mother's phone Number", new PersonAttributeCalculation("Telephone contact")), "", new RDQACalculationResultConverter());
        //dsd.addColumn("CHWs phone Number", new CalculationDataDefinition("CHW's phone Number", new PersonAttributeCalculation("Telephone contact")), "", new RDQACalculationResultConverter());
        dsd.addColumn("Village_Estate_Landmark", new CalculationDataDefinition("Village/Estate/Landmark", new PersonAddressCalculation()), "", new RDQACalculationResultConverter());       
   
        return dsd;
    }

	@SuppressWarnings("unchecked")
	@Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition vaccinationAppointmentsPDSD = vaccinationAppointmentsDataSetDefinition("vaccinationAppointments");
        vaccinationAppointmentsPDSD.addRowFilter(vaccinationAppointmentsInCWCCohort());
        DataSetDefinition vaccinationAppointmentsDSD = vaccinationAppointmentsPDSD;

        return Arrays.asList(
                ReportUtils.map(vaccinationAppointmentsDSD, "startDate=${startDate},endDate=${endDate}")
        );
    }	

}
