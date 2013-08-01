<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<div class="ke-page-content">
	${ ui.decorate("kenyaui", "panel", [ heading: "Edit Patient Record" ],
			ui.includeFragment("kenyaemr", "patient/editPatient", [ patient: patient, returnUrl: returnUrl ])
	)}
</div>