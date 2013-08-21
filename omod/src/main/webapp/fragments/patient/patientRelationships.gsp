<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Relationships" ])
%>

<div style="float: right">
	${ ui.includeFragment("kenyaui", "widget/editButton", [ href: ui.pageLink("kenyaemr", "TODO", [ patientId: patient.id, returnUrl: ui.thisUrl() ]) ]) }
</div>

<%
if (relationships.size() > 0) {
	relationships.each { %>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(it.relationship), value: it.link ]) }
<% }
} else { %>
	<em>None</em>
<% } %>

<div style="clear: both"></div>