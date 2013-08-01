<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<div class="ke-page-content">
	${ ui.decorate("kenyaui", "panel", [ heading: "Create a New Patient Record" ],
			ui.includeFragment("kenyaemr", "patient/editPatient")
	)}
</div>