<%
	ui.decorateWith("standardKenyaEmrPage")
%>

${ ui.includeFragment("checkedInPatientSearch", [ page: "medicalEncounterViewPatient" ]) }