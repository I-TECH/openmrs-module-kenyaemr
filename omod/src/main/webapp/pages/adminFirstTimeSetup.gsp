<% ui.decorateWith("standardKenyaEmrPage") %>

<% if (isSuperUser) { %>

	<style>
		.field-label, .field-content {
			font-size: 1.2em;
		}
	</style>

	<h2>Kenya EMR Configuration</h2>
	
	<div class="ui-widget">
	
		<div class="ui-widget-header">
			First-time Setup
		</div>
	
		<div class="ui-widget-content" style="padding: 1em;">
		
			${ ui.includeFragment("widget/form", [
					page: "adminFirstTimeSetup",
					submitLabel: "Save Settings",
					fields: [
						[
							label: "What facility is this installation managing?",
							formFieldName: "defaultLocation",
							class: org.openmrs.Location,
							initialValue: defaultLocation
						]
					]
				]) }
		
		</div>
	</div>

	<script>
		jq(function() {
			jq("input[type=submit]").button();
		});
	</script>

<% } else { %>

	You do not have administrative privileges. Please contact a system administrator to configure the system before it can be used.

<% } %>