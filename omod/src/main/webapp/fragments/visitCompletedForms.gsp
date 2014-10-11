<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Completed Visit Forms" ])

	def onEncounterClick = { encounter ->
		"""kenyaemr.openEncounterDialog('${ currentApp.id }', ${ encounter.id });"""
	}
%>

${ ui.includeFragment("kenyaemr", "widget/encounterStack", [ encounters: encounters, onEncounterClick: onEncounterClick ]) }