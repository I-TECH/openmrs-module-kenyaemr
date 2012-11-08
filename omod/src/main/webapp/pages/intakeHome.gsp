<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

${ ui.includeFragment("kenyaemr", "patientSearch", [ page: "intakeViewPatient", showCreateButton: true ]) }