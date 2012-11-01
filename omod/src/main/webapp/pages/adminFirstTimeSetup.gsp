<% ui.decorateWith("kenyaemr", "standardKenyaEmrPage") %>

<% if (isSuperUser) { %>

	<style type="text/css">
		.field-label {
			font-size: 1.2em;
		}
		.field-content {
			padding: 0.3em;
		}
	</style>

	<h2>Kenya EMR Configuration</h2>
	
	<div class="ui-widget">
	
		<div class="ui-widget-header">
			First-time Setup
		</div>
	
		<div class="ui-widget-content" style="padding: 1em;">
		<%
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
			${ ui.includeFragment("uilibrary", "widget/form", [
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