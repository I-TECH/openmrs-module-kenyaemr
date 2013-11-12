<div class="ke-panel-content" style="max-height: 500px; overflow: scroll">
	<script type="text/javascript">
		function onEncounterEdit() {
			ui.navigate('kenyaemr', 'editForm', { appId: '${ currentApp.id }', encounterId: ${ encounter.id }, returnUrl: '${ currentUrl }' });
		}
		function onEncounterDelete(encounterId) {
			if (confirm('Are you sure you want to delete this encounter?')) {
				ui.getFragmentActionAsJson('kenyaemr', 'form/formUtils', 'deleteEncounter', { appId: '${ currentApp.id }', encounterId: encounterId }, function() {
					ui.reloadPage();
				});
			}
		}
	</script>

	${ ui.includeFragment("kenyaemr", "form/viewHtmlForm", [ encounter: encounter ]) }
</div>
<div class="ke-panel-footer">
	<button type="button" onclick="onEncounterEdit(${ encounter.id })">Edit</button>
	<button type="button" onclick="onEncounterDelete(${ encounter.id })">Delete</button>
	<button type="button" onclick="kenyaui.closeDialog()">Close</button>
</div>