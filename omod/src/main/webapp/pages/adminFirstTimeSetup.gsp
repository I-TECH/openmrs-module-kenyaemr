<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")

	def fields = [
			[
					label: "What facility is this installation managing?",
					formFieldName: "defaultLocation",
					class: org.openmrs.Location,
					initialValue: defaultLocation,
					fieldFragment: "field/org.openmrs.Location.kenyaemr"
			]
	]

	if (mrnIdentifierSource) {
		fields << [
				label: "OpenMRS ID Generator",
				value: "Already configured"
		]
	} else {
		fields << [
				label: "(OpenMRS ID Generator) Base for first ID Number",
				formFieldName: "mrnIdentifierSourceStart",
				initialValue: "3",
				class: java.lang.String
		]
	}

	if (hivIdentifierSource) {
		fields << [
				label: "HIV Unique Patient Number Generator",
				value: "Already configured"
		]
	} else {
		fields << [
				label: "(HIV Unique Patient Number Generator) First ID Number",
				formFieldName: "hivIdentifierSourceStart",
				initialValue: "00001",
				class: java.lang.String
		]
	}
%>

<style type="text/css">
.field-label {
	font-size: 1.2em;
}
.field-content {
	padding: 0.3em;
}
</style>
<div id="content">
	<% if (isSuperUser) { %>
	<div class="panel-frame">
		<div class="panel-heading">First-time Setup</div>
		<div class="panel-content">
			${ ui.includeFragment("uilibrary", "widget/form", [
					pageProvider: "kenyaemr",
					page: "adminFirstTimeSetup",
					submitLabel: "Save Settings",
					fields: fields
			]) }
		</div>
	</div>

	<script type="text/javascript">
		jq(function() {
			jq("input[type=submit]").button();
		});
	</script>

	<% } else { %>
	You do not have administrative privileges. Please contact a system administrator to configure the system before it can be used.
	<% } %>
</div>