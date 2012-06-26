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

	function enterHtmlForm(htmlFormId, title) {
		showDialog({
			title: title,
			fragment: "enterHtmlForm",
			config: {
				patient: ${ patient.id },
				htmlFormId: htmlFormId
			}
		});
	}
</script>

<fieldset>
	<legend>Fill Out a Form</legend>
	
	<% if (!availableForms) { %> None <% } %>
	
	<% availableForms.each { %>
		${ ui.includeFragment("widget/button", [
			iconProvider: "uilibrary",
			icon: it.icon,
			label: it.label,
			classes: [ "padded" ],
			onClick: "enterHtmlForm(" + it.htmlFormId + ", '" + it.label + "');"
		]) }
		<br/>
	<% } %>
</fieldset>

<% if (encounters) { %>
	<fieldset>
		<legend>Already Filled Out</legend>

		<% encounters.each { %>
			${ ui.includeFragment("encounterPanel", [ encounter: it ]) }
		<% } %>
	</fieldset>
<% } %>