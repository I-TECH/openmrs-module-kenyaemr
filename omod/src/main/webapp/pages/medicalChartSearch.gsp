<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

${ ui.includeFragment("kenyaemr", "patientSearch", [ page: "medicalChartViewPatient", defaultWhich: "all", showCreateButton: true ]) }