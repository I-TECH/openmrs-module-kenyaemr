<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Completed Forms" ])

	config.require("visit")
%>

<%
if (encounters && encounters.size > 0) {
	encounters.each {
		println ui.includeFragment("kenyaemr", "encounterPanel", [ encounter: it ])
	}
} else {
	println "<i>None</i>"
}
 %>