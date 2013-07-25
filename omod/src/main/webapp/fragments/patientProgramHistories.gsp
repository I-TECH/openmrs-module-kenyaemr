<% programs.each { programDescriptor -> %>
${ ui.includeFragment("kenyaemr", "patientProgramHistory", [
		patient: patient,
		program: programDescriptor.target,
		enrollmentForm: programDescriptor.enrollmentForm.target,
		discontinuationForm: programDescriptor.discontinuationForm.target,
		showClinicalData: showClinicalData
]) }
<% } %>