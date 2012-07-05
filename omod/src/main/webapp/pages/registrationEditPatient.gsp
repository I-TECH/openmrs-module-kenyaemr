<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<h2>Edit Patient Record</h2>

${ ui.includeFragment("registrationEditPatient", [ patient: patient, returnUrl: returnUrl ]) }	
