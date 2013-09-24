<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaemr", "patientSearchForm", [ defaultWhich: "checked-in" ]) }

	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
		heading: "Tasks",
		items: [
			[
				iconProvider: "kenyaui",
				icon: "buttons/patient_add.png",
				label: "Create New Patient Record",
				extra: "Patient does not exist yet",
				href: ui.pageLink("kenyaemr", "registration/createPatient", [ returnUrl: ui.thisUrl() ])
			]
		]
	]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/patientList", [ id: "results", pageProvider: "kenyaemr", page: "intake/intakeViewPatient", heading: "Matching Patients" ]) }
</div>

<script type="text/javascript">
	subscribe("patientSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		jq('input[name=q]').focus();
		publish('patientSearch/changed'); // Submit search on page load
	});
</script>
