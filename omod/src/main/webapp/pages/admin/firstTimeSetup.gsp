<%
	ui.decorateWith("kenyaemr", "standardPage")

	def fields = [
			[
					label: "What facility is this installation managing?",
					formFieldName: "defaultLocation",
					class: org.openmrs.Location,
					initialValue: defaultLocation
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
				initialValue: "30001",
				class: java.lang.String
		]
	}
%>
<div class="ke-page-content">

	<% if (isSuperUser) { %>

	${ ui.includeFragment("kenyaemr", "system/externalRequirements") }

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">${ ui.message("kenyaemr.admin.firstTimeSetup") }</div>

		${ ui.includeFragment("kenyaui", "widget/panelForm", [
				pageProvider: "kenyaemr",
				page: "admin/firstTimeSetup",
				submitLabel: "Save Settings",
				fields: fields
		]) }
	</div>

	<% } else { %>
	You do not have administrative privileges. Please contact a system administrator to configure the system before it can be used.
	<% } %>
</div>