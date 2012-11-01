<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient ])
	
	ui.includeCss("kenyaemr", "kenyaemr.css");
%>

<style type="text/css">
	fieldset {
		margin-bottom: 0.6em;
	}
	
	#col1 {
		float: left;
		padding-right: 4px;
		width: 38%;
	}
	
	#col2 {
		float: left;
		padding-left: 0.5em;
		border-left: 1px black solid;
		width: 60%;
	}
	
	.active-visit {
		border: 1px black solid;
		border-top-left-radius: 0.5em;
		border-bottom-left-radius: 0.5em;
		margin-bottom: 0.6em;
		padding: 0.3em;
		position: relative;
		right: -5px;
		z-index: 1;
	}
	
	.active-visit h4 {
		margin: 0.3em;
	}
	
	.selected-visit {
		background-color: #ffffbb;
		border-right: none;
	}

	.selectable:hover {
		cursor: pointer;
		background-color: #e0e0e0;
	}
</style>

<script type="text/javascript">
	jq(function() {
		jq.getJSON('${ ui.actionLink("kenyaemr", "arvRegimen", "currentRegimen", [ patientId: patient.id ]) }', function(data) {
			jq('#current-hiv-regimen').html(data.shortDisplay);
		});
	});
</script>

<div id="col1">
	${ ui.includeFragment("kenyaemr", "patientOverallDetails", [
			patient: patient,
			visit: visit,
			activeVisits: activeVisits
		]) }
</div>

<div id="col2" <% if (visit) { %>class="selected-visit"<% } %>>

	<a href="${ ui.pageLink("kenyaemr", "medicalEncounterArvRegimen", [ patientId: patient.id ]) }">
		<img src="${ ui.resourceLink("uilibrary", "images/pencil_16.png") }"/>
		Current ARVs:
		<span id="current-hiv-regimen">(loading)</span>
	</a>

	<br/>

	<% if (!visit) { %>
		<h4>No current visit</h4>
	<% } %>
	
	<% if (visit) { %>

		${ ui.includeFragment("kenyaemr", "availableForms", [ visit: visit ]) }
		
	<% } else { %>

		${ ui.includeFragment("uilibrary", "widget/button", [
				iconProvider: "uilibrary",
				icon: "user_add_32.png",
				label: "Go to Registration",
				classes: [ "padded" ],
				extra: "to Check In",
				href: ui.pageLink("kenyaemr", "registrationViewPatient", [ patientId: patient.id ])
			]) }

	<% } %>
	
	<br/>

</div>

<% if (visit) { %>
	
	${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	
	${ ui.includeFragment("kenyaemr", "dialogSupport") }

<% } %>