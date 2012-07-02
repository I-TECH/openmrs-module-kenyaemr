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
		width: 38%;
	}
	
	#col2 {
		float: left;
		padding-left: 0.5em;
		border-left: 1px black solid;
		width: 60%;
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
	
	#content fieldset {
		position: relative;
	}
	
	.edit-button {
		position: absolute;
		top: 0.5em;
		right: 0.3em;
		font-size: 0.8em;
	}
</style>

<div id="col1">
	<fieldset class="editable">
		<legend>
			Patient
		</legend>
		<div class="edit-button">
			<a href="${ ui.pageLink("registrationEditPatient", [patientId: patient.id]) }">Edit</a>
		</div>
		
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
			<% [ clinicNumberIdType, hivNumberIdType ].each { %>
				<span class="identifier">
					<span class="identifier-type">${ ui.format(it) }:</span>
					<span class="identifier-value">${ ui.format(patient.getPatientIdentifier(it)?.identifier) }</span><br/>
				</span>
			<% } %>
			<% if (patient.patientIdentifier.identifierType.uuid == MC.OPENMRS_ID_UUID) {
				def prefId = patient.patientIdentifier
			%>
				<span class="identifier">
					<span class="identifier-type">${ ui.format(prefId.identifierType) }:</span>
					<span class="identifier-value">${ ui.format(prefId.identifier) }</span><br/>
				</span>
			<% } %>
		</div>
		
		<div class="attributes">
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
			<% if (!selected) { %>
				<script>
					jq('#visit-${ v.id }').click(function() {
						location.href = '${ ui.escapeJs(ui.pageLink("registrationViewPatient", [ patientId: patient.id, visitId: v.id ])) }';
					});
				</script>
			<% } %>
		</div>
	<% } %>
	
	${ ui.includeFragment("programPanel", [
			patient: patient,
			program: hivProgram,
			registrationFormUuid: "e4b506c1-7379-42b6-a374-284469cba8da",
			exitFormUuid: "e3237ede-fa70-451f-9e6c-0908bc39f8b9"
		]) }
	
</div>

<div id="col2" <% if (visit) { %>class="selected-visit"<% } %>>
	<h4>
	<% if (visit) { %>
		Current ${ ui.format(visit.visitType) } Visit
	<% } else { %>
		No current visit
	<% } %>
	</h4>
	
	<% if (visit) { %>

		${ ui.includeFragment("availableForms", [ visit: visit ]) }
		
		<% if (!visit.stopDatetime) { %>
			<br/>
			<%= ui.includeFragment("widget/popupForm", [
				id: "check-out-form",
				buttonConfig: [
					label: "Check Out",
					classes: [ "padded" ],
					extra: "and Close Visit",
					iconProvider: "uilibrary",
					icon: "user_close_32.png"
				],
				/* dialogOpts: """{ open: function() { jq('#check-out-form input[type=submit]').focus(); } }""", */
				popupTitle: "Check Out",
				fields: [
					[ hiddenInputName: "visit.visitId", value: visit.visitId ],
					[ label: "End Date and Time", formFieldName: "visit.stopDatetime", class: java.util.Date, initialValue: new Date(), fieldFragment: "field/java.util.Date.datetime" ]
				],
				fragment: "registrationUtil",
				action: "editVisit",
				successCallbacks: [ "location.href = '${ ui.pageLink("registrationViewPatient", [ patientId: patient.id ]) }'" ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel"),
				submitLoadingMessage: "Checking Out"
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
					label: "Check In For Visit",
					classes: [ "padded" ],
					extra: "Patient is Here"
				],
				popupTitle: "Check In For Visit",
				prefix: "visit",
				commandObject: newCurrentVisit,
				hiddenProperties: [ "patient" ],
				properties: [ "visitType", "startDatetime" ],
				propConfig: [
					"visitType": [ type: "radio" ],
				],
				fieldConfig: [
					"startDatetime": [ fieldFragment: "field/java.util.Date.datetime" ]
				],
				fragment: "registrationUtil",
				action: "startVisit",
				successCallbacks: [ jsSuccess ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel"),
				submitLoadingMessage: "Checking In"
			]) %>
	<% } %>
	
	<br/>

</div>

<% if (visit) { %>
	
	${ ui.includeFragment("showHtmlForm", [ id: "showHtmlForm", style: "display: none" ]) }
	
	${ ui.includeFragment("dialogSupport") }

<% } %>