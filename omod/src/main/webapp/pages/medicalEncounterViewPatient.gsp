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
	
	.encounter-panel {
		border: 1px #e0e0e0 solid;
		cursor: pointer;
		margin: 2px 0px;
		padding: 0.2em;
	}
	.encounter-panel:hover {
		background-color: white;
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

<script>
	jq(function() {
		jq('.encounter-panel').click(function(event) {
			var encId = jq(this).find('input[name=encounterId]').val();
			var title = jq(this).find('input[name=title]').val();
			publish('showHtmlForm/showEncounter', { encounterId: encId, editButtonLabel: 'Edit', deleteButtonLabel: 'Delete' });
			showDivAsDialog('#showHtmlForm', title);
			return false;
		});
	});
	
	function enterHtmlForm(htmlFormId, title) {
		showDialog({
			title: title,
			fragment: "enterHtmlForm",
			config: {
				patient: ${ patient.id },
				htmlFormId: htmlFormId
			}
		});
	}
</script>

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
			End: ${ v.stopDatetime ? ui.format(v.stopDatetime) : "<i>ongoing</i>" } <br/>
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
	
		<% if (availableForms) { %>
			<fieldset>
				<legend>Fill out a form</legend>
				<% availableForms.each { %>
					${ ui.includeFragment("widget/button", [
						iconProvider: "uilibrary",
						icon: it.icon,
						label: it.label,
						onClick: "enterHtmlForm(" + it.htmlFormId + ", '" + it.label + "');"
					]) }
				<% } %>
			</fieldset>
		<% } %>
		
		<% if (visit.encounters) { %>
			Forms already filled out
	
			<% visit.encounters.findAll {
				!it.voided
			}.each { %>
				<div class="encounter-panel">
					<input type="hidden" name="encounterId" value="${ it.encounterId }"/>
					<input type="hidden" name="title" value="${ ui.escapeAttribute(ui.format(visit.visitType) + " - " + ui.format(it.form ?: it.encounterType)) }"/>
					${ ui.includeFragment("encounterSummary", [ encounter: it, maxObs: 2 ]) }
				</div>
			<% } %>
		<% } %>
	
	<% } else {
		// do this here to avoid annoying template engine issue
		def jsSuccess = "location.href = pageLink('medicalEncounterViewPatient', " + "{" + "patientId: ${ patient.id }, visitId: data.visitId" + "});"
	%>

		${ ui.includeFragment("widget/popupForm", [
				buttonConfig: [
					iconProvider: "uilibrary",
					icon: "user_add_32.png",
					label: "Is Patient Here?",
					classes: [ "padded" ],
					extra: "Check In"
				],
				popupTitle: "Check In to Clinic",
				prefix: "visit",
				commandObject: newCurrentVisit,
				hiddenProperties: [ "patient" ],
				properties: [ "startDatetime", "visitType" ],
				fragment: "registrationUtil",
				action: "startVisit",
				successCallbacks: [ jsSuccess ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel")
			]) }
	<% } %>
	
	<br/>

</div>

<% if (visit) { %>
	
	${ ui.includeFragment("showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	
	${ ui.includeFragment("dialogSupport") }

<% } %>