<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div id="content-side"></div>
<div id="content-main">
	${
		ui.decorate("kenyaemr", "panel", [ heading: "Edit Patient Record" ],
				ui.includeFragment("kenyaemr", "registrationEditPatient", [ patient: patient, returnUrl: returnUrl ])
		)
	}
</div>