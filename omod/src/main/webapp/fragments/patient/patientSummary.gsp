<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Registration", frameOnly: true ])
%>
<div class="ke-panel-content">
	<div class="ke-stack-item">
		<% if (recordedAsDeceased) { %>
		<div class="ke-warning" style="margin-bottom: 5px">
			Patient has been recorded as deceased in a program form. Please update the registration form.
		</div>
		<% } %>

		${ ui.includeFragment("kenyaui", "widget/buttonlet", [ type: "edit", href: ui.pageLink("kenyaemr", "registration/editPatient", [ patientId: patient.id, returnUrl: ui.thisUrl() ]) ]) }

		<% patient.activeAttributes.each { %>
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(it.attributeType), value: it ]) }
		<% } %>
	</div>
</div>
<% if (forms) { %>
<div class="ke-panel-footer">
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