<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<div id="content">
	${ ui.decorate("kenyaui", "panel", [ heading: "Edit Patient Record" ],
			ui.includeFragment("kenyaemr", "registrationEditPatient", [ patient: patient, returnUrl: returnUrl ])
	)}
</div>