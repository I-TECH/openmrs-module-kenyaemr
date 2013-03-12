<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "patientSearchForm", [ defaultWhich: "all" ]) }

	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
		heading: "Tasks",
		items: [
			[ iconProvider: "kenyaui", icon: "buttons/patient_add.png", label: "Create New Patient Record", extra: "Patient does not exist yet", href: ui.pageLink("kenyaemr", "registrationCreatePatient") ]
		]
	]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "patientList", [ id: "results", showNumResults: true, page: "registrationViewPatient", heading: "Matching Patients"]) }
</div>

<script type="text/javascript">
	subscribe("patientSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		jq('input[name=q]').focus();
		// if the user goes back to this page in their history, redo the ajax query
		publish('patientSearch/changed');
	});
</script>
