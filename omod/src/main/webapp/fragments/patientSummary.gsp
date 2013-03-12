<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Patient Summary" ])
%>
<div class="ke-stack-item">
	${ ui.includeFragment("kenyaui", "widget/editButton", [ href: ui.pageLink("kenyaemr", "registrationEditPatient", [ patientId: patient.id, returnUrl: ui.thisUrl() ]) ]) }
	<b>${ ui.includeFragment("kenyaemr", "personName", [ name: patient.personName ]) }<br />
	${ ui.message("Patient.gender." + (patient.gender == 'M' ? 'male' : 'female')) },
	${ ui.includeFragment("kenyaemr", "personAgeAndBirthdate", [ person: patient ]) }</b>
</div>
<div class="ke-stack-item">
	<% [ clinicNumberIdType, hivNumberIdType ].each { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(it), value: patient.getPatientIdentifier(it)?.identifier ]) }
	<% } %>
	<% if (patient.patientIdentifier.identifierType.uuid == MetadataConstants.OPENMRS_ID_UUID) { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label:  ui.format(patient.patientIdentifier.identifierType), value: patient.patientIdentifier.identifier ]) }
	<% } %>
</div>
<% if (patient.activeAttributes) { %>
<div class="ke-stack-item">
	<% patient.activeAttributes.each { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(it.attributeType), value: it ]) }
	<% } %>
</div>
<% } %>
<div class="ke-stack-item">
	<% forms.each { form -> %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			iconProvider: form.iconProvider,
			icon: form.icon,
			label: form.label,
			href: ui.pageLink("kenyaemr", "editPatientHtmlForm", [
				patientId: patient.id,
				formUuid: form.formUuid,
				returnUrl: ui.thisUrl()
			])
	]) }
	<% } %>
</div>