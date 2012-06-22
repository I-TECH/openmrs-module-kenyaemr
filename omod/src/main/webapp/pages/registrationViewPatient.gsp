<%
	ui.decorateWith("standardKenyaEmrPage", [ afterAppHeader: ui.includeFragment("selectedPatientHeader") ])
%>

<style>
	fieldset {
		margin-bottom: 0.6em;
	}
	
	#col1 {
		float: left;
		padding-right: 4px;
	}
	
	#col2 {
		float: left;
		padding-left: 0.5em;
		border-left: 1px black solid;
		width: 50%;
		height: 100%;
	}
	
	.person-name, .demographics, .identifiers, .attributes {
		margin-bottom: 0.5em;
	}
	
	.person-name {
		font-weight: bold;
	}

	.identifier-type, .attribute-type {
		font-size: 0.8em;
		color: #808080;
	}
	.identifier-value, .attribute-value {
	}

	.encounter-panel {
		border: 1px #e0e0e0 solid;
		cursor: pointer;
		margin: 2px 0px;
	}
	.encounter-panel:hover {
		text-decoration: underline;
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
	
	.padded {
		padding: 1em;
	}
</style>

<script>
	jq(function() {
		jq('.encounter-panel').click(function(event) {
			var encId = jq(event.srcElement).find('input[name=encounterId]').val();
			var title = jq(event.srcElement).find('input[name=title]').val();
			publish('showHtmlForm/showEncounter', encId);
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
			Patient Details
		</legend>
		
		<div class="person-name">
			${ patient.personName }
		</div>
		<div class="demographics">
			${ ui.message("Patient.gender." + (patient.gender == 'M' ? 'male' : 'female')) },
			${ patient.age } year(s)<br/>
			Born:
			<% if (patient.birthdateEstimated) { %>~<% } %>
			${ ui.format(patient.birthdate) }
		</div>
		
		<div class="identifiers">
			<% patient.activeIdentifiers.findAll {
				it.identifierType.uuid == MC.OPENMRS_ID_UUID || it.identifierType.uuid == MC.PATIENT_CLINIC_NUMBER_UUID 
			}.each { %>
				<span class="identifier">
					<span class="identifier-type">${ ui.format(it.identifierType) }:</span>
					<span class="identifier-value">${ it.identifier }</span><br/>
				</span>
			<% } %>
		</div>
		
		<div class="attributes">
			<div class="attribute-type">TODO determine whether we're using any PersonAttributes</div>
			<% patient.activeAttributes.each { %>
				<span class="attribute">
					<span class="attribute-type">${ ui.format(it.attributeType) }:</span>
					<span class="attribute-value">${ ui.format(it) }</span><br/>
			<% } %>
		</div>
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
			End: ${ ui.format(v.stopDatetime) }
			<hr/>
			<% if (!v.encounters) { %>
				No data recorded
			<% } %>
			<% v.encounters.each { %>
				<div class="encounter-panel">
					<input type="hidden" name="encounterId" value="${ it.encounterId }"/>
					<input type="hidden" name="title" value="${ ui.escapeAttribute(ui.format(v.visitType) + " - " + ui.format(it.form ?: it.encounterType)) }"/>
					${ ui.format(it.encounterType) }
					by
					<% it.providersByRoles.each { %>
						${ ui.format(it.key) }:
						<%= it.value.collect { ui.format(it) } .join(", ") %>
					<% } %>
				</div>
			<% } %>
			<% if (!selected) { %>
				<script>
					jq('#visit-${ v.id }').click(function() {
						location.href = '${ ui.escapeJs(ui.pageLink("registrationViewPatient", [ patientId: patient.id, visitId: v.id ])) }';
					});
				</script>
			<% } %>
		</div>
	<% } %>
</div>

<div id="col2" <% if (visit) { %>class="selected-visit"<% } %>>
	<h4>
	<% if (visit) { %>
		Current ${ ui.format(visit.visitType) }
	<% } else { %>
		No current visit
	<% } %>
	</h4>
	
	<% if (visit) { %>

		<% availableForms.each { %>
			${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: it.icon,
				label: it.label,
				classes: [ "padded" ],
				onClick: "enterHtmlForm(" + it.htmlFormId + ", '" + it.label + "');"
			]) }
			<br/>
		<% } %>
		
		<% if (!visit.stopDatetime) { %>
			<%= ui.includeFragment("widget/popupForm", [
				id: "check-out-form",
				buttonConfig: [
					label: "Is Patient Leaving?",
					classes: [ "padded" ],
					extra: "Check Out",
					iconProvider: "uilibrary",
					icon: "user_close_32.png"
				],
				/* dialogOpts: """{ open: function() { jq('#check-out-form input[type=submit]').focus(); } }""", */
				popupTitle: "Check Out",
				fields: [
					[ hiddenInputName: "visit.visitId", value: visit.visitId ],
					[ label: "End Date and Time", formFieldName: "visit.stopDatetime", class: java.util.Date, initialValue: new Date() ]
				],
				fragment: "registrationUtil",
				action: "editVisit",
				successCallbacks: [ "location.reload()" ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel")
			]) %>
		<% } %>
	
	<% } else {
		// do this here to avoid annoying template engine issue
		def jsSuccess = "location.href = pageLink('registrationViewPatient', " + "{" + "patientId: ${ patient.id }, visitId: data.visitId" + "});"
	%>

		<%= ui.includeFragment("widget/popupForm", [
				id: "check-in-form",
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
				properties: [ "visitType", "startDatetime" ],
				propConfig: [
					"visitType": [ type: "radio" ],
				],
				fragment: "registrationUtil",
				action: "startVisit",
				successCallbacks: [ jsSuccess ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel")
			]) %>
	<% } %>
	
	<br/>

</div>

<% if (visit) { %>
	
	${ ui.includeFragment("showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	
	${ ui.includeFragment("dialogSupport") }

<% } %>