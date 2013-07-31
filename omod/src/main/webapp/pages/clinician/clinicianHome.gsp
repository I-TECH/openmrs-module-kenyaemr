<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "patientSearchForm", [ defaultWhich: "all" ]) }
</div>

<div id="content-main">
	${ ui.includeFragment("kenyaemr", "patient/patientList", [ id: "results", showNumResults: true, page: "chart/chartViewPatient", heading: "Matching Patients"]) }
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