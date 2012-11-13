<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Completed Forms" ])

	config.require("visit")
%>

<% if (encounters) { %>
	${ ui.includeFragment("kenyaemr", "viewableEncounters", [ encounters: encounters ]) }
<% } else { %>
	<i>None</i>
<% } %>