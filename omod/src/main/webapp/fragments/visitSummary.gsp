<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Visit Summary" ])

	config.require("visit")
%>

Type: <b>${ ui.format(visit.visitType) }</b><br />
Location: <b>${ ui.format(visit.location) }</b><br />
From <b>${ ui.format(visit.startDatetime) }</b> <% if (visit.stopDatetime) { %> to <b>${ ui.format(visit.stopDatetime) }</b> <% } %>