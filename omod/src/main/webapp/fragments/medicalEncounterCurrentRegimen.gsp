<%
	config.require("patient")
%>

<fieldset>
	<legend>Current ARV Regimen</legend>

	<a href="${ ui.pageLink("kenyaemr", "medicalEncounterArvRegimen", [ patientId: patient.id ]) }">
		<img src="${ ui.resourceLink("kenyaemr", "images/edit.png") }"/>

		<span id="current-hiv-regimen">(loading...)</span>
	</a>
</fieldset>

<script type="text/javascript">
	jq(function() {
		jq.getJSON('${ ui.actionLink("kenyaemr", "arvRegimen", "currentRegimen", [ patientId: patient.id ]) }', function(data) {
			jq('#current-hiv-regimen').html(data.longDisplay);
		});
	});
</script>