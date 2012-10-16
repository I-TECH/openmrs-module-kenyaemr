package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.kenyaemr.calculation.TBPatientsCalculation;
import org.springframework.stereotype.Component;

@Component
public class TBPatientsReport extends PatientAlertListReportManager {
	public TBPatientsReport(){
		setAlertCalculation(new TBPatientsCalculation());
		
	}
	/*@Override
    public void addColumns(PatientDataSetDefinition dsd) {
		Program tbProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);
		ProgramEnrollmentsForPatientDataDefinition p=new ProgramEnrollmentsForPatientDataDefinition(tbProgram.getName());
		
		super.addColumns(dsd);
        dsd.removeColumnDefinition("View");
        dsd.addColumn("Date Treatment Started", new ProgramEnrollmentsForPatientDataDefinition(tbProgram.getName()),"",new DataConverter() {
			
			@Override
			public Class<?> getInputDataType() {
				return Program.class;
			}
			
			@Override
			public Class<?> getDataType() {
				return Date.class;
			}
			
			@Override
			public Object convert(Object input) {
				return  ((Program) input);
			}
		});
		dsd.addColumn("View", new PatientIdDataDefinition(), "", new DataConverter() {
			
			@Override
			public Class<?> getInputDataType() {
				return Integer.class;
			}
			
			@Override
			public Class<?> getDataType() {
				return String.class;
			}
			
			@Override
			public Object convert(Object input) {
				return "<a href=\"medicalChartViewPatient.page?patientId=" + input + "\">View</a>";
			}
		});
       
        }*/

}
