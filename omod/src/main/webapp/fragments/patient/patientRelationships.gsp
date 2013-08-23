<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Relationships", frameOnly: true ])
%>
<div class="ke-panel-content">
	<script type="text/javascript">
		function onVoidRelationship(relId) {
			kenyaui.openConfirmDialog({
				heading: 'KenyaEMR',
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
	<% relationships.each { rel -> %>
	<div class="ke-stack-item">
		${ ui.includeFragment("kenyaui", "widget/buttonlet", [ type: "void",
				onClick: "onVoidRelationship(" + rel.relationshipId + ")"
		]) }
		${ ui.includeFragment("kenyaui", "widget/buttonlet", [ type: "edit",
				href: ui.pageLink("kenyaemr", "registration/editRelationship", [ patientId: patient.id, relationshipId: rel.relationshipId, appId: currentApp.id, returnUrl: ui.thisUrl() ])
		]) }

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
<div class="ke-panel-footer">
	${ ui.includeFragment("kenyaui", "widget/buttonlet", [ type: "add", label: "Add",
			href: ui.pageLink("kenyaemr", "registration/editRelationship", [ patientId: patient.id, appId: currentApp.id, returnUrl: ui.thisUrl() ])
	]) }
</div>