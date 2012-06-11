<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<style>
	#alerts {
		margin: 0.5em;
	}
	.alert {
		background-color: yellow;
		border: 1px black dashed;
		margin-right: 0.5em;
		padding: 0.2em;
	}
	
	#col1, #col2, #col3 {
		float: left;
		width: 32%;
		height: 100%;
	}
	#col1, #col2 {
		margin-right: 0.5em;
	}
	
	.active-visit {
		font-weight: bold;
	}
	
	fieldset {
		margin-bottom: 1em;
	}
</style>

<script>
jq(function() {
	ui.applyAlternatingClasses('table.decorated tbody', 'alternate-shading-even', 'alternate-shading-odd');
});
</script>

<div id="alerts">
	<span class="alert">
		Should we show some alerts here with some decoration?
	</span>
	<span class="alert">
		If they're short, we have room for lots.
	</span>
</div>

<div id="col1">
	<fieldset id="registrationDetails">
		<legend>
			Registration Details
		</legend>
		
		<div class="icon">
			<img width="32" height="32" src="${ ui.resourceLink("uilibrary", "images/patient_" + patient.gender + ".gif") }"/>
		</div>
		
		<div class="demographics">
			<b>${ patient.personName }</b> <br/>
			${ patient.gender }, ${ patient.age }y
		</div>
		
		<div class="identifiers">
			<% patient.activeIdentifiers.each { %>
				<span class="identifier">
					<span class="identifier-type">${ it.identifierType.name }</span><br/>
					<span class="identifier-value">${ it.identifier }</span><br/>
				</span>
			<% } %>
			
			<% patient.activeAttributes.each { %>
				${ ui.format(it.attributeType) }: ${ ui.format(it) }<br/>
			<% } %>
		</div>
	</fieldset>
</div>

<div id="col2">
	<fieldset id="hiv-overview">
		<legend>
			HIV Overview
		</legend>
		If in HIV program, include details here like last CD4, current regimen, regimen changes, etc.
	</fieldset>

	<fieldset id="recent-labs">
		<legend>
			Recent Labs
		</legend>
		For all patients, show recent lab results
	</fieldset>
	
	<fieldset id="clinical-overview">
		<legend>
			Overview
		</legend>
		For all patients, show a graph of weight progression, annotated with when visits happened
		${ ui.includeFragment("obsTableByDate", [ concepts: [ 5089 ] ]) }
	</fieldset>
</div>

<div id="col3">
	<fieldset id="visits">
		<legend>
			Visits
		</legend>
		<% if (visits.size() == 0) { %>
			${ ui.message("general.none") }
		<% } else { %>
			<table class="decorated">
				<thead>
					<tr>
						<th>Type</th>
						<th>Start</th>
						<th>End</th>
					</tr>
				</thead>
				<tbody>
					<% visits.each {
						def active = !it.stopDatetime
					%>
						<tr <% if (active) { %>class="active-visit"<% } %>>
							<td>${ ui.format(it.visitType) }</td>
							<td>${ ui.format(it.startDatetime) }</td>
							<td>
								<% if (active) { %>
									Active
								<% } else { %>
									${ ui.format(it.stopDatetime) }
								<% } %>
							</td>
						</tr>
					<% } %>
				</tbody>
			</table>
		<% } %>
	</fieldset>
</div>