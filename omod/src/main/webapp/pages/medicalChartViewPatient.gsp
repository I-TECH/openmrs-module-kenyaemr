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
	}
		
	fieldset {
		margin-bottom: 1em;
	}
	
	.link-button {
		border: 1px black solid;
		margin-bottom: 0.5em;
		padding: 0.3em;
	}
</style>

<script>
jq(function() {
	ui.applyAlternatingClasses('table.decorated tbody', 'alternate-shading-even', 'alternate-shading-odd');
});
</script>

<div id="col1">

	<div class="link-button">
		<a href="${ ui.pageLink("medicalChartViewPatient", [ patientId: patient.id ]) }">
			Overview
		</a>
	</div>
	
	<% visits.each { visit -> %>
		<div class="link-button">
			<a href="${ ui.pageLink("medicalChartViewPatient", [ patientId: patient.id, visitId: visit.id ]) }">
				${ ui.format(visit.visitType) } <br/>
				from ${ ui.format(visit.startDatetime) }
				<% if (visit.stopDatetime) { %>
					<br/>
					to ${ ui.format(visit.stopDatetime) }
				<% } %>
			</a>
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