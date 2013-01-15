<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "patientSearchForm", [ defaultWhich: "all" ]) }

	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [
		heading: "Tasks",
		items: [
			[
				iconProvider: "kenyaemr",
				icon: "buttons/patient_add.png",
				label: "Create New Patient Record",
				extra: "Patient does not exist yet",
				href: ui.pageLink("kenyaemr", "registrationCreatePatient", [ returnUrl: ui.thisUrl() ])
			]
		]
	]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "patientList", [ id: "results", showNumResults: true, page: "intakeViewPatient", heading: "Matching Patients" ]) }
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
