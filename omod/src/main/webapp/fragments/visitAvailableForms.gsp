<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Available Visit Forms" ])

	config.require("visit")

	def onFormClick = { form ->
		def visitId = currentVisit ? currentVisit.id : null
		def opts = [ appId: currentApp.id, visitId: visitId, patientId: currentPatient.id, formUuid: form.formUuid, returnUrl: ui.thisUrl() ]
    	"""location.href = '${ ui.pageLink('kenyaemr', 'enterForm', opts) }';"""
	}
%>

${ ui.includeFragment("kenyaui", "widget/formStack", [ forms: availableForms, onFormClick: onFormClick ]) }