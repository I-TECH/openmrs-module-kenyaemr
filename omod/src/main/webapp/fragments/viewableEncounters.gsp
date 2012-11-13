<%
	config.require("encounters")
%>
<style type="text/css">
	.encounter-panel {
		border-top: 2px dotted #d1d0c9;
		cursor: pointer;
		padding: 2px;
	}

	.encounter-panel:first-of-type {
		border-top: 0;
	}

	.encounter-panel:hover {
		background-color: white;
	}
</style>

<script type="text/javascript">
	jq(function() {
		jq('.encounter-panel').click(function(event) {
			var encId = jq(this).find('input[name=encounterId]').val();
			var title = jq(this).find('input[name=title]').val();
			publish('showHtmlForm/showEncounter', { encounterId: encId, editButtonLabel: 'Edit', deleteButtonLabel: 'Delete' });
			showDivAsDialog('#showHtmlForm', title);
			return false;
		});
	});
</script>

<% config.encounters.each { %>
	${ ui.includeFragment("kenyaemr", "encounterPanel", [ encounter: it ]) }
<% } %>