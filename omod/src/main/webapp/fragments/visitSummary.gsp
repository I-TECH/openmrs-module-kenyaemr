<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Visit Summary", frameOnly: true ])
%>
<script type="text/javascript">
	function onVoidVisit(visitId) {
		kenyaui.openConfirmDialog({
			heading: 'KenyaEMR',
			message: '${ ui.message("kenyaemr.confirmVoidVisit") }',
			okCallback: function() { doVisitVoid(visitId); }
		});
	}

	function doVisitVoid(visitId) {
		ui.getFragmentActionAsJson('kenyaemr', 'emrUtils', 'voidVisit', { visitId: visitId, reason: 'Data entry error' }, function() {
			ui.reloadPage();
		});
	}
</script>

<div class="ke-panel-content">
	<% if (visit.voided) { %>
	<div class="ke-warning" style="margin-bottom: 5px">This visit has been voided</div>
	<% } %>

	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Type", value: visit.visitType ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Location", value: visit.location ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "When", value: kenyaEmrUi.formatVisitDates(visit) ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Entry", value: sourceForm ?: "Registration" ]) }
</div>

<% if (allowVoid && !visit.voided) { %>
<div class="ke-panel-controls" style="text-align: center">
	<% if (!visit.encounters) { %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			label: "Void Visit",
			extra: "If entered by mistake",
			iconProvider: "kenyaui",
			icon: "buttons/visit_void.png",
			onClick: "onVoidVisit(" + visit.id + ")"
	]) }
	<% } else { %>
	<em>To void this visit, please delete all encounters first</em>
	<% } %>
</div>
<% } %>