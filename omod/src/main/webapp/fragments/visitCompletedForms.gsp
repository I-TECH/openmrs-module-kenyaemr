<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Completed Visit Forms" ])

	def onEncounterClick = { encounter ->
		def title = ui.format(encounter.form ?: encounter.encounterType)
		"""kenyaemr.openEncounterDialog('${ currentApp.id }', ${ encounter.id },'${ title }');"""
	}
%>

${ ui.includeFragment("kenyaemr", "widget/encounterStack", [ encounters: encounters, onEncounterClick: onEncounterClick ]) }