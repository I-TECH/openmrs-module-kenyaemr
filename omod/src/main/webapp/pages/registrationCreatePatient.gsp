<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div id="content-side"></div>
<div id="content-main">
	${
		ui.decorate("kenyaui", "panel", [ heading: "Create a New Patient Record" ],
			ui.includeFragment("kenyaemr", "registrationEditPatient")
		)
	}
</div>