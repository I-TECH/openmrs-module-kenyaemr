<%
	config.require("patient")
	config.require("editable")
%>

<fieldset>
	<legend>Current ARV Regimen</legend>

	<div id="current-arv-regimen" class="loading"></div>
</fieldset>

<script type="text/javascript">
	jq(function() {
		jq.getJSON('${ ui.actionLink("kenyaemr", "arvRegimen", "currentRegimen", [ patientId: patient.id ]) }', function(data) {
			var html = "";
			<% if (config.editable) { %>
			html += '<a href="${ ui.pageLink("kenyaemr", "medicalEncounterArvRegimen", [ patientId: patient.id ]) }">';
			html += '<img src="${ ui.resourceLink("kenyaemr", "images/edit.png") }"/> ';
			<% } %>
			html += data.longDisplay;
			<% if (config.editable) { %>
			html += '</a>';
			<% } %>

			jq('#current-arv-regimen').removeClass('loading').html(html);
		});
	});
</script>