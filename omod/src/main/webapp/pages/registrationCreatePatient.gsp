<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<div id="content">
	${ ui.decorate("kenyaui", "panel", [ heading: "Create a New Patient Record" ],
			ui.includeFragment("kenyaemr", "registrationEditPatient")
	)}
</div>