/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.report.patientlist;

import org.openmrs.module.kenyaemr.calculation.tb.TbInProgramCalculation;
import org.springframework.stereotype.Component;

/**
 * TB patients report
 */
@Component
public class TBPatientsReport extends PatientListReportManager {

    public TBPatientsReport() {
        setCalculation(new TbInProgramCalculation());
    }

	/*@Override
    public void addColumns(PatientDataSetDefinition dsd) {
		Program tbProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);
		ProgramEnrollmentsForPatientDataDefinition p=new ProgramEnrollmentsForPatientDataDefinition(tbProgram.getShortMessage());
		
		super.addColumns(dsd);
        dsd.removeColumnDefinition("View");
        dsd.addColumn("Date Treatment Started", new ProgramEnrollmentsForPatientDataDefinition(tbProgram.getShortMessage()),"",new DataConverter() {
			
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