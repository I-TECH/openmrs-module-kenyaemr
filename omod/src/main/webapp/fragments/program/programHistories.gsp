<% programs.each { programDescriptor -> %>
${ ui.includeFragment("kenyaemr", "program/programHistory", [
		patient: patient,
		program: programDescriptor.target,
		defaultEnrollmentForm: programDescriptor.defaultEnrollmentForm.target,
		defaultCompletionForm: programDescriptor.defaultCompletionForm.target,
		showClinicalData: showClinicalData
]) }
<% } %>