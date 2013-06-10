<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Available Forms" ])

	config.require("visit")

	def onFormClick = { form ->
		def visitId = visit ? visit.id : null
		def opts = [ appId: currentApp.id, visitId: visitId, patientId: patient.id, formUuid: form.formUuid, returnUrl: ui.thisUrl() ]
    	"""location.href = '${ ui.pageLink('kenyaemr', 'enterHtmlForm', opts) }';"""
	}
%>

${ ui.includeFragment("kenyaui", "widget/formStack", [ forms: availableForms, onFormClick: onFormClick ]) }