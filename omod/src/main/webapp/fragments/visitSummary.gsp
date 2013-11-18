<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Visit Summary", frameOnly: true ])

	def nonVoidedEncounters = visit.encounters.findAll { !it.voided }
%>
<script type="text/javascript">
	function ke_deleteVisit(visitId) {
		kenyaui.openConfirmDialog({
			heading: 'Visit',
			message: '${ ui.message("kenyaemr.confirmVoidVisit") }',
			okCallback: function() {
				ui.getFragmentActionAsJson('kenyaemr', 'emrUtils', 'voidVisit', { visitId: visitId, reason: 'Data entry error' }, function() {
					ui.reloadPage();
				});
			}
		});
	}
</script>

<div class="ke-panel-content">
	<% if (visit.voided) { %>
	<div class="ke-warning" style="margin-bottom: 5px">This visit has been voided</div>
	<% } %>

	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Type", value: visit.visitType ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Location", value: visit.location ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "When", value: kenyaui.formatVisitDates(visit) ]) }
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Entry", value: sourceForm ?: "Registration" ]) }
</div>

<% if (allowVoid && !visit.voided) { %>
<div class="ke-panel-controls" style="text-align: center">
	<% if (!nonVoidedEncounters) { %>
	<button type="button" onclick="ke_deleteVisit(${ visit.id })">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/void.png") }" /> Delete visit
	</button>
	<% } else { %>
	<em>To delete this visit, please delete all encounters first.</em>
	<% } %>
</div>
<% } %>