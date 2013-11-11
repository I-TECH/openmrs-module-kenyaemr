<% if (encounter) { %>
<button type="button" class="ke-compact" onclick="ui.navigate('${ ui.pageLink("kenyaemr", "editForm", [ encounterId: encounter.id, appId: currentApp.id, returnUrl: ui.thisUrl() ]) }')">
	<img src="${ ui.resourceLink("kenyaui", "images/glyphs/edit.png") }" />
</button>
<% } %>
${ ui.includeFragment(summaryFragment.provider, summaryFragment.path, [ patientProgram: enrollment, encounter: encounter, showClinicalData: showClinicalData ] )}