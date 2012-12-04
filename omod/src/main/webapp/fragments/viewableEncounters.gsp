<%
	config.require("encounters")

	config.encounters.each {
		println ui.includeFragment("kenyaemr", "encounterPanel", [ encounter: it ])
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