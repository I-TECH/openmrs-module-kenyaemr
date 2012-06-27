<%
	ui.decorateWith("standardKenyaEmrPage", [ patient: patient ])
%>

<style>
	fieldset {
		margin-bottom: 0.6em;
	}
	
	#col1 {
		float: left;
		padding-right: 4px;
		width: 30%;
	}
	
	#col2 {
		float: left;
		padding-left: 0.5em;
		border-left: 1px black solid;
		width: 68%;
		height: 100%;
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

<div id="col1">
	<fieldset>
		<legend>
			Summary
			<a href="#">(more info TODO)</a>
		</legend>
		
		TODO: Summary of high-value clinical data
	</fieldset>

	<% activeVisits.each { v ->
		def selected = v == visit
	%>
		<div id="visit-${ v.id }" class="active-visit <% if (selected) { %>selected-visit<% } else { %>selectable<% } %>">
			<h4>
				<img src="${ ui.resourceLink("kenyaemr", "images/checked_in_16.png") }"/>
				${ ui.format(v.visitType) }
			</h4>
			Location: ${ ui.format(v.location) } <br/>
			Start: ${ ui.format(v.startDatetime) } <br/>
			End: ${ ui.format(v.stopDatetime) } <br/>
			<% if (!selected) { %>
				<script>
					jq('#visit-${ v.id }').click(function() {
						location.href = '${ ui.escapeJs(ui.pageLink("medicalEncounterViewPatient", [ patientId: patient.id, visitId: v.id ])) }';
					});
				</script>
			<% } %>
		</div>
	<% } %>
</div>

<div id="col2" <% if (visit) { %>class="selected-visit"<% } %>>
	<% if (!visit) { %>
		<h4>No current visit</h4>
	<% } %>
	
	<% if (visit) { %>

		${ ui.includeFragment("availableForms", [ visit: visit ]) }	
		
	<% } else { %>

		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "user_add_32.png",
				label: "Go to Registration",
				classes: [ "padded" ],
				extra: "to Check In",
				href: ui.pageLink("registrationViewPatient", [ patientId: patient.id ])
			]) }

	<% } %>
	
	<br/>

</div>

<% if (visit) { %>
	
	${ ui.includeFragment("showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	
	${ ui.includeFragment("dialogSupport") }

<% } %>