<%
	ui.decorateWith("standardKenyaEmrPage", [ patient: patient ])
%>
<style>
	#col1, #col2 {
		float: left;
		height: 100%;
	}
	
	#col1 {
		width: 32%;
		margin-right: 0.5em;
	}
	
	#col2 {
		width: 66%;		
	}
		
	fieldset {
		margin-bottom: 1em;
	}
	
	.link-button {
		border: 1px black solid;
		margin-bottom: 0.5em;
		padding: 0.3em;
		border-radius: 0.3em;
	}
	
	.selected {
		background-color: #ffffaa;
	}
	
	.link-button .label {
		font-weight: bold;
	}
	
	.link-button .description {
		font-size: 0.6em;
	}
</style>

<script>
jq(function() {
	ui.applyAlternatingClasses('table.decorated tbody', 'alternate-shading-even', 'alternate-shading-odd');
});
</script>

<div id="col1">

	<div class="link-button<% if (selection == "overview") { %> selected<% } %>">
		<span class="title">
			<a href="${ ui.pageLink("medicalChartViewPatient", [ patientId: patient.id ]) }">
				Overview
			</a>
		</span>
	</div>
	
	<% oneTimeForms.each { %>
		<div class="link-button<% if (selection == "form-${ it.formUuid }") { %> selected<% } %>">
			<span class="title">
				<a href="${ ui.pageLink("medicalChartViewPatient", [ patientId: patient.id, formUuid: it.formUuid ]) }">
					${ it.label }
				</a>
			</span>
		</div>
	<% } %>

	<% programs.each { %>
		<div class="link-button<% if (selection == "program-${ it.id }") { %> selected<% } %>">
			<span class="title">
				<a href="${ ui.pageLink("medicalChartViewPatient", [ patientId: patient.id, patientProgramId: it.id ]) }">
					${ ui.format(it.program) }
				</a>
			</span>
			<br/>
			<span class="description">
				from ${ ui.format(it.dateEnrolled) }
				<% if (it.dateCompleted) { %>
					to ${ ui.format(it.dateCompleted) }
				<% } %>
				<% if (it.outcome) { %>
					<br/>
					Outcome: <b>${ ui.format(it.outcome) }</b>
				<% } %>
			</span>
		</div>		
	<% } %>
	
	<div style="padding-bottom: 0.5em;">
		<u>Visits</u>
	</div>

	<% if (!visits) { %>
		None
	<% } %>

	<% visits.each { visit -> %>
		<div class="link-button<% if (selection == "visit-${ visit.id }") { %> selected<% } %>">
			<span class="title">
				<a href="${ ui.pageLink("medicalChartViewPatient", [ patientId: patient.id, visitId: visit.id ]) }">
					${ ui.format(visit.visitType) } Visit
				</a>
			</span>
			<br/>
			<span class="description">
				from ${ ui.format(visit.startDatetime) }
				<% if (visit.stopDatetime) { %>
					to ${ ui.format(visit.stopDatetime) }
				<% } %>
			</span>
		</div>
	<% } %>
	
</div>

<div id="col2">
	<% if (visit) { %>
	
		<fieldset>
			<legend>
				<b>${ ui.format(visit.visitType) } visit</b>
			</legend>
			at <b>${ ui.format(visit.location) }</b><br/>
			from ${ ui.format(visit.startDatetime) }
			<% if (visit.stopDatetime) { %>
				to ${ ui.format(visit.stopDatetime) }
			<% } %>
			
			<br/>
			<br/>
			<u>Forms</u>
			<% if (visit.encounters) { %>
				${ ui.includeFragment("viewableEncounters", [ encounters: visit.encounters ]) }
			<% } else { %>
				<br/>
				None
			<% } %>
		</fieldset>

	<% } else if (form) { %>

		<h3>${ ui.format(form) }</h3>

		<% if (encounter) { %>
			${ ui.includeFragment("showHtmlForm", [ id: "showHtmlForm" ]) }
		
			<script type="text/javascript">
				jq(function() {
					publish('showHtmlForm/showEncounter', { encounterId: ${ encounter.id } });
				});
			</script>
		<% } else { %>
			Not Filled Out
		<% } %>
	
	<% } else if (program) { %>
	
		<h3>${ ui.format(program.program) }</h3>
		<table>
			<tr>
				<td>Enrolled:</td>
				<td>${ ui.format(program.dateEnrolled) }</td>
			</tr>
			<tr>
				<td>Completed:</td>
				<td>${ ui.format(program.dateCompleted) }</td>
			</tr>
			<tr>
				<td>Outcome:</td>
				<td>${ ui.format(program.outcome) }</td>
			</tr>
		</table>

	<% } else {
		def cs = context.conceptService
		def conceptList = [
			cs.getConcept(5089),
			cs.getConcept(5497)
		]
	%>

		${ ui.includeFragment("obsTableByDate", [
				concepts: conceptList
			]) }
		

	<% } %>
</div>

<% if (visit) { %>
	
	${ ui.includeFragment("showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	
	${ ui.includeFragment("dialogSupport") }

<% } %>