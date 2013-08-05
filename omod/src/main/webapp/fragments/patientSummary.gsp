<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Patient Summary" ])
%>
<div class="ke-stack-item">
	${ ui.includeFragment("kenyaui", "widget/editButton", [ href: ui.pageLink("kenyaemr", "registration/editPatient", [ patientId: patient.id, returnUrl: ui.thisUrl() ]) ]) }

	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Name", value: kenyaUi.formatPersonName(patient) ]) }

	<% patient.activeAttributes.each { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(it.attributeType), value: it ]) }
	<% } %>
</div>
<% if (forms) { %>
<div class="ke-stack-item">
	<% forms.each { form -> %>
		${ ui.includeFragment("kenyaui", "widget/button", [
				iconProvider: form.iconProvider,
				icon: form.icon,
				label: form.name,
				extra: "Edit form",
				href: ui.pageLink("kenyaemr", "editPatientForm", [
					appId: currentApp.id,
					patientId: patient.id,
					formUuid: form.formUuid,
					returnUrl: ui.thisUrl()
				])
		]) }
	<% } %>
</div>
<% } %>