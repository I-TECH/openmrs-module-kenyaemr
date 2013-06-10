<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Patient Summary" ])
%>
<div class="ke-stack-item">
	${ ui.includeFragment("kenyaui", "widget/editButton", [ href: ui.pageLink("kenyaemr", "registrationEditPatient", [ patientId: patient.id, returnUrl: ui.thisUrl() ]) ]) }

	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Name", value: kenyaEmrUi.formatPersonName(patient.personName) ]) }

	<% patient.activeAttributes.each { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(it.attributeType), value: it ]) }
	<% } %>
</div>
<div class="ke-stack-item">
<% forms.each { form -> %>
	${ ui.includeFragment("kenyaui", "widget/button", [
			iconProvider: form.iconProvider,
			icon: form.icon,
			label: form.label,
			extra: "Edit form",
			href: ui.pageLink("kenyaemr", "editPatientHtmlForm", [
				appId: currentApp.id,
				patientId: patient.id,
				formUuid: form.formUuid,
				returnUrl: ui.thisUrl()
			])
	]) }
<% } %>
</div>