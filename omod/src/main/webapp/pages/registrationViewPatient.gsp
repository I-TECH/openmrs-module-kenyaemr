<%
	ui.decorateWith("standardAppPage")
	
	def showVisit = { v ->
		def selected = v == visit
		def ret = """<div class="${ selected ? "active-" : "" }visit">"""
		if (!selected)
			ret += """<a href="${ ui.pageLink("registrationViewPatient", [patientId: patient.id, visitId: v.id ]) }">"""
		ret += """${ ui.format(v.startDatetime) } - ${ ui.format(v.visitType) }"""
		if (!selected)
			ret += "</a>"
		ret += "</div>"
		return ret
	}
%>

<style>
	#col1 {
		float: left;
		margin-right: 1em;
	}
	
	#col2 {
		float: left;
		margin-right: 0px;
	}
	
	#col3 {
		float: left;
		position: relative;
		left: -1px; // so active tab from col2 is on top of the left border 
		margin-left: 0px;
		border-left: 1px black solid;
		padding-left: 0.6em;
		height: 100%;
	}
	
	#registrationDetails > .icon {
		float: left;
		padding-right: 1em;
	}
	#registrationDetails > .demographics {
		float: left;
	}
	#registrationDetails > .identifiers {
	}
	#registrationDetails > .identifiers .identifier {
		clear: left;
		display: block;
		padding-top: 0.5em;
		padding-bottom: 0.5em;
	}
	#registrationDetails > .identifiers .identifier-type {
		font-decoration: underline;
		font-size: 0.8em;
	}
	#registrationDetails > .identifiers .identifier-value {
		font-weight: bold;
	}
	
	.visit, .active-visit {
		display: block;
		border: 1px black solid;
		padding: 0.3em;
		margin: 0.2em 0em 0.2em 0.2em; 
	}
	
	.active-visit {
		border-right: none;
		background-color: #F5F5DC;
		position: relative;
		z-index: 1; // tab needs to be over the left border in col3
	}
	
	.visit-group-label {
		font-weight: bold;
		display: block;
	}
</style>

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
	<% if (currentVisits) { %>
		
		<span class="visit-group-label">Currently at Clinic</span>
		
		<% currentVisits.each { %>
			${ showVisit(it) }
		<% } %>
	
	<% } else {
		// do this here to avoid annoying template engine issue
		def jsSuccess = "location.href = pageLink('registrationViewPatient', " + "{" + "patientId: ${ patient.id }, visitId: data.visitId" + "});"
	%>

		<span class="visit-group-label">No current visit</span>
		
		${ ui.includeFragment("widget/popupForm", [
				buttonConfig: [
					iconProvider: "uilibrary",
					icon: "user_add_32.png",
					label: "Is Patient Here?",
					extra: "Check In"
				],
				popupTitle: "Check In to Clinic",
				prefix: "visit",
				commandObject: newCurrentVisit,
				hiddenProperties: [ "patient" ],
				properties: [ "startDatetime", "visitType" ],
				fragment: "registrationData",
				action: "startVisit",
				successCallbacks: [ jsSuccess ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel")
			]) }
	<% } %>
	
	<br/>

	<% if (pastVisits.size() == 0) { %>
		
		<span class="visit-group-label">No past visits</span>
	
	<% } else { %>

		<span class="visit-group-label">Past Visits</span>
		
	<% } %>
		
	${ ui.includeFragment("widget/popupForm", [
			buttonConfig: [
				label: "Add"
			],
			popupTitle: "Record Past Visit",
			prefix: "visit",
			commandObject: newPastVisit,
			hiddenProperties: [ "patient" ],
			properties: [ "startDatetime", "stopDatetime", "visitType" ],
			fragment: "registrationData",
			action: "startVisit",
			successCallbacks: [ "location.reload()" ],
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
		]) }
	
	<% pastVisits.each { %>
		${ showVisit(it) }
	<% } %>
	
</div>

<% if (visit) { %>

	<div id="col3">
		
		<h4>Visit: ${ ui.format(visit.visitType) }</h4>

		<table>
			<tr>
				<td>Location:</td>
				<td>${ ui.format(visit.location) }</td>
			</tr>
			<tr>
				<td>Started:</td>
				<td>${ ui.format(visit.startDatetime) }</td>
			</tr>
			<tr>
				<td>Ended:</td>
				<td>${ ui.format(visit.stopDatetime) }</td>
			</tr>
		</table>
		
		<hr/>
		
		<% if (visit.encounters.size() == 0) { %>
			No Encounters
		<% } %>
		
		<% if (visit.encounters) visit.encounters.each { %>
			${ ui.format(it.encounterDatetime) } :
			${ ui.format(it.encounterType) }
			by:
			<% it.providersByRoles.each { %>
				${ ui.format(it.key) }: ${ ui.format(it.value) }
			<% } %>
		<% } %>
		
		<% if (!visit.stopDatetime) { %>
			<div style="position: fixed; bottom: 0.5em; text-align: center; padding: 0.5em; background-color: #e0e0e0;">
				${ ui.includeFragment("widget/popupForm", [
					buttonConfig: [
						label: "Is Patient Leaving?",
						extra: "Check Out",
						iconProvider: "uilibrary",
						icon: "user_close_32.png"
					],
					popupTitle: "Check Out",
					fields: [
						[ hiddenInputName: "visit.visitId", value: visit.visitId ],
						[ label: "End Date and Time", formFieldName: "visit.stopDatetime", class: java.util.Date, initialValue: new Date() ]
					],
					fragment: "registrationData",
					action: "editVisit",
					successCallbacks: [ "location.reload()" ],
					submitLabel: ui.message("general.submit"),
					cancelLabel: ui.message("general.cancel")
				]) }
			</div>
		<% } %>
	</div>

<% } %>