<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

${ ui.includeFragment("kenyaemr", "patientSearch", [page: "registrationViewPatient", defaultWhich: "all", showCreateButton: true]) }