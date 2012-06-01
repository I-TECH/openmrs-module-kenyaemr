<%
	ui.decorateWith("standardAppPage")
%>
<style>
	#search {
		float: left;
	}
</style>

<fieldset id="search">
	<legend>
		Find a Patient
	</legend>
	
	${ ui.includeFragment("widget/form", [
			id: "patientSearch",
			fields: [
				[ label: "ID or Name", formFieldName: "q", class: java.lang.String ],
				[ label: "Age", formFieldName: "age", class: java.lang.Integer ]
			],
			fragment: "patientSearch",
			action: "search",
			submitOnEvent: "patientSearch/changed",
			resetOnSubmit: false,
			successEvent: "patientSearch/results"
		] )}
</fieldset>

<fieldset style="border: none">
	${ ui.includeFragment("patientList", [
		id: "results",
		showNumResults: true,
		page: "medicalChartViewPatient"
	]) }
</fieldset>

<script>
	subscribe("patientSearch/results", function(event, data) {
		publish("results/show", data);
	});
	jq(function() {
		// if the user goes back to this page in their history, redo the ajax query
		publish('patientSearch/changed');
	});
</script>