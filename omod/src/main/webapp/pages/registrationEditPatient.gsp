<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<h2>Edit Patient Record</h2>

${ ui.includeFragment("kenyaemr", "registrationEditPatient", [ patient: patient, returnUrl: returnUrl ]) }
