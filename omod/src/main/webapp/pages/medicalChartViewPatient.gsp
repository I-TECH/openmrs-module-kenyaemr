<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ patient: patient ])
%>

<style type="text/css">
#col1, #col2 {
	float: left;
	height: 100%;
}

#col1 {
	width: 360px;
	margin-right: 0.5em;
}

.panel-menu {
	border-radius: 3px;
	background-color: #858580;
	margin-bottom: 10px;
	padding: 3px;
}

.panel-menu .title {
	padding: 5px;
	text-align: center;
	color: white;
	font-weight: bold;
}

.panel-menu-button {
	padding: 5px;
}

.panel-menu-button:nth-child(odd) {
	background-color: #EEE
}

.panel-menu-button:nth-child(even) {
	background-color: #DDD
}

.panel-menu-button a {
	color: #444;
	font-weight: bold;
	text-decoration: none;
}

.panel-menu-button .description {
	font-size: 0.6em;
}

.selected:nth-child(odd), .selected:nth-child(even) {
	background-color: #EEB;
}

fieldset {
	margin-bottom: 1em;
}
</style>

<script type="text/javascript">
	jq(function() {
		ui.applyAlternatingClasses('table.decorated tbody', 'alternate-shading-even', 'alternate-shading-odd');
	});
</script>

<div id="col1">

	<div class="panel-menu">
		<div class="<% if (selection == "overview") { %>selected<% } %> panel-menu-button">
			<a href="${ ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id ]) }">
				Overview
			</a>
		</div>

		<% oneTimeForms.each { %>
		<div class="<% if (selection == "form-${ it.formUuid }") { %>selected<% } %> panel-menu-button">
			<a href="${ ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, formUuid: it.formUuid ]) }">
				${ it.label }
			</a>
		</div>
		<% } %>

		<% programs.each { %>
		<div class="panel-menu-button<% if (selection == "program-${ it.id }") { %> selected<% } %>">
			<a href="${ ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, patientProgramId: it.id ]) }">
				${ ui.format(it.program) }
			</a>
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
	</div>

	<div class="panel-menu">
		<div class="title">Visits</div>

		<% if (!visits) { %>
		None
		<% } %>

		<% visits.each { visit -> %>
		<div class="panel-menu-button<% if (selection == "visit-${ visit.id }") { %> selected<% } %>">
			<a href="${ ui.pageLink("kenyaemr", "medicalChartViewPatient", [ patientId: patient.id, visitId: visit.id ]) }">
				${ ui.format(visit.visitType) } Visit
			</a>
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
		${ ui.includeFragment("kenyaemr", "viewableEncounters", [ encounters: visit.encounters ]) }
		<% } else { %>
		<br/>
		None
		<% } %>
	</fieldset>

	<% } else if (form) { %>

	<h3>${ ui.format(form) }</h3>

	<% if (encounter) { %>
	${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm" ]) }

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

	<%
	} else {
		def cs = context.conceptService
		def conceptList = [ cs.getConcept(5089), cs.getConcept(5497) ] // Weight and CD4
	%>
	<table>
		<tr>
			<td style="vertical-align: top">${ ui.includeFragment("kenyaemr", "obsTableByDate", [id: "tblhistory", concepts: conceptList]) }</td>
			<td style="vertical-align: top">${ ui.includeFragment("kenyaemr", "obsGraphByDate", [id: "cd4graph", concepts: conceptList, showUnits: true, style: "width: 400px; height: 300px" ]) }</td>
		</tr>
	</table>
	<% } %>
</div>

<% if (visit) { %>

${ ui.includeFragment("kenyaemr", "showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }

${ ui.includeFragment("kenyaemr", "dialogSupport") }

<% } %>