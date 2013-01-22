<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Completed Forms" ])

	config.require("visit")
%>

<%
if (encounters) {
	encounters.each {
		println ui.includeFragment("kenyaemr", "encounterPanel", [ encounter: it ])
	}
} else {
	println "<i>None</i>"
}
 %>

<script type="text/javascript">
	jq(function() {
		jq('.encounter-item').click(function(event) {
			var encId = jq(this).find('input[name=encounterId]').val();
			var title = jq(this).find('input[name=title]').val();
			publish('showHtmlForm/showEncounter', { encounterId: encId, editButtonLabel: 'Edit', deleteButtonLabel: 'Delete' });
			showDivAsDialog('#showHtmlForm', title);
			return false;
		});
	});
</script>