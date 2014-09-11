<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Relationships", frameOnly: true ])
%>
<script type="text/javascript">
	function onVoidRelationship(relId) {
		kenyaui.openConfirmDialog({
			heading: 'Void Relationship',
			message: '${ ui.message("kenyaemr.confirmVoidRelationship") }',
			okCallback: function() { doRelationshipVoid(relId); }
		});
	}

	function doRelationshipVoid(relId) {
		ui.getFragmentActionAsJson('kenyaemr', 'emrUtils', 'voidRelationship', { relationshipId: relId, reason: 'Data entry error' }, function() {
			ui.reloadPage();
		});
	}
</script>

<% if (relationships) { %>
<div class="ke-panel-content">

	<% relationships.each { rel -> %>
	<div class="ke-stack-item">
		<button type="button" class="ke-compact" onclick="onVoidRelationship(${ rel.relationshipId })"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/void.png") }" /></button>

		<button type="button" class="ke-compact" onclick="ui.navigate('${ ui.pageLink("kenyaemr", "registration/editRelationship", [ patientId: patient.id, relationshipId: rel.relationshipId, appId: currentApp.id, returnUrl: ui.thisUrl() ]) }')">
			<img src="${ ui.resourceLink("kenyaui", "images/glyphs/edit.png") }" />
		</button>

		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(rel.type), value: rel.personLink ]) }
		<% if (rel.startDate) { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Started", value: rel.startDate ]) }
		<% } %>
		<% if (rel.endDate) { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Ended", value: rel.endDate ]) }
		<% } %>

		<div style="clear: both"></div>
	</div>
	<% } %>
</div>
<% } %>

<div class="ke-panel-footer">
	<button type="button" onclick="ui.navigate('${ ui.pageLink("kenyaemr", "registration/editRelationship", [ patientId: patient.id, appId: currentApp.id, returnUrl: ui.thisUrl() ])}')">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/add.png") }" /> Add
	</button>
</div>