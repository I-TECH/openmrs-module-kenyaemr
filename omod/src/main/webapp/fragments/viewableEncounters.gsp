<%
	config.require("encounters")
%>
<style>
	.encounter-panel {
		border: 1px #e0e0e0 solid;
		cursor: pointer;
		margin: 2px 0px;
	}

	.encounter-panel:hover {
		background-color: white;
	}
</style>

<script>
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
	${ ui.includeFragment("encounterPanel", [ encounter: it ]) }
<% } %>