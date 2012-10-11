package org.openmrs.module.kenyaemr.report;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.DeclineCD4Calculation;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/20/12
 * Time: 12:59 PM
 * 
 */
@Component
public class DeclinedCD4Report extends PatientAlertListReportManager{
	
    public  DeclinedCD4Report(){
        setAlertCalculation(new DeclineCD4Calculation());
    }
    @Override
    public void addColumns(PatientDataSetDefinition dsd) {
    	Concept concept = Context.getConceptService().getConceptByUuid(MetadataConstants.CD4_CONCEPT_UUID);
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.DATE, -180);
		Date onOrBefore= calendar.getTime();
		
        super.addColumns(dsd);
        dsd.removeColumnDefinition("View");
			dsd.addColumn("Previous CD4",new ObsForPersonDataDefinition("Previous CD4", TimeQualifier.LAST, concept, onOrBefore, null),"",new DataConverter() {
				
				@Override
				public Class<?> getInputDataType() {
					return Obs.class;
				}
				
				@Override
				public Class<?> getDataType() {
					return Double.class;
				}
				
				@Override
				public Object convert(Object input) {
					return  ((Obs) input).getValueNumeric();
				}
			});
			dsd.addColumn("Current CD4",new ObsForPersonDataDefinition("Current CD4", TimeQualifier.LAST, concept,new Date(),null),"",new DataConverter() {
				
				@Override
				public Class<?> getInputDataType() {
					return Obs.class;
				}
				
				@Override
				public Class<?> getDataType() {
					return Double.class;
				}
				
				@Override
				public Object convert(Object input) {
					return  ((Obs) input).getValueNumeric();
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
			
		
    }
    
}
