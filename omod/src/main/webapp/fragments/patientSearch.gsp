<%
	config.require("page") // the page to go to when you select a patient

	// config supports defaultWhich (defaults to "checked-in", also supports "all")
	
	def defaultWhich = config.defaultWhich ?: "checked-in"
%>

<style type="text/css">
	#search {
		float: left;
	}
	#create-patient-button {
		float: right;
	}
</style>

<fieldset id="search">
	<legend>
		Find a Patient
	</legend>
	
	<%= ui.includeFragment("uilibrary", "widget/form", [
			id: "patientSearch",
			fields: [
				[ label: "Which patients", fragment: "widget/radioButtons", formFieldName: "which",
					selected: defaultWhich,
					separator: "&nbsp;&nbsp;",
					options: [
						[ label: "All", value: "all" ],
						[ label: "Only Checked In", value: "checked-in" ]
					],
					onChange: """function() { publish('patientSearch/changed'); }"""
				],
				[ label: "ID or Name", formFieldName: "q", class: java.lang.String ],
				[ label: "Age", formFieldName: "age", class: java.lang.Integer ]
			],
			fragment: "patientSearch",
			fragmentProvider: "kenyaemr",
			action: "search",
			submitOnEvent: "patientSearch/changed",
			resetOnSubmit: false,
			successEvent: "patientSearch/results"
		]) %>
</fieldset>

<% if (config.showCreateButton) {
	print ui.includeFragment("uilibrary", "widget/button", [
			id: "create-patient-button",
			iconProvider: "kenyaemr",
			icon: "patient_add.png",
			label: "Create New Patient Record",
			extra: "Patient does not exist yet",
			href: ui.pageLink("kenyaemr", "registrationCreatePatient") ])
}
%>

<div style="clear: right"></div>

<fieldset style="border: none">
${ ui.includeFragment("kenyaemr", "patientList", [
	id: "results",
	showNumResults: true,
	page: config.page
]) }
</fieldset>

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