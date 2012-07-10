<fieldset class="editable patient-summary-box">
	<legend>
		Patient
	</legend>
	<div class="edit-button">
		<a href="${ ui.pageLink("registrationEditPatient", [patientId: patient.id, returnUrl: ui.thisUrl() ]) }">Edit</a>
	</div>
	
	<div class="person-name">
		${ ui.includeFragment("kenyaemrPersonName", [ name: patient.personName ]) }
	</div>
	<div class="demographics">
		${ ui.message("Patient.gender." + (patient.gender == 'M' ? 'male' : 'female')) },
		${ patient.age } year(s)<br/>
		Born:
		<% if (patient.birthdateEstimated) { %><i>approx</i><% } %>
		${ ui.format(patient.birthdate) }
	</div>
	
	<div class="identifiers">
		<% [ clinicNumberIdType, hivNumberIdType ].each { %>
			<span class="identifier">
				<span class="identifier-type">${ ui.format(it) }:</span>
				<span class="identifier-value">${ ui.format(patient.getPatientIdentifier(it)?.identifier) }</span><br/>
			</span>
		<% } %>
		<% if (patient.patientIdentifier.identifierType.uuid == MetadataConstants.OPENMRS_ID_UUID) {
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
	
	${ ui.includeFragment("widget/button", [
			iconProvider: "uilibrary",
			icon: "comment_user_32.png",
			label: "Family History",
			href: ui.pageLink("editPatientHtmlForm", [
				patientId: patient.id,
				formUuid: MetadataConstants.FAMILY_HISTORY_FORM_UUID,
				returnUrl: ui.thisUrl()
			])
		]) }
	<% if (patient.gender == 'F') { %>
		<br/>
		${ ui.includeFragment("widget/button", [
			iconProvider: "uilibrary",
			icon: "home_32.png",
			label: "Obstetric History",
			href: ui.pageLink("editPatientHtmlForm", [
				patientId: patient.id,
				formUuid: MetadataConstants.OBSTETRIC_HISTORY_FORM_UUID,
				returnUrl: ui.thisUrl()
			])
		]) } 
	<% } %>
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
		registrationFormUuid: MetadataConstants.HIV_PROGRAM_ENROLLMENT_FORM_UUID,
		exitFormUuid: MetadataConstants.HIV_PROGRAM_DISCONTINUATION_FORM_UUID
	]) }
