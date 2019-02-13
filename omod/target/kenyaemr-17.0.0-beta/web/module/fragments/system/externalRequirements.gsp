<%
	ui.decorateWith("kenyaui", "panel", [ heading: ui.message("kenyaemr.admin.externalRequirements") ])
%>
<table class="ke-table-vertical">
	<thead>
		<th style="text-align: left; width: 32px">&nbsp;</th>
		<th style="text-align: left">Name</th>
		<th>Required</th>
		<th>Found</th>
	</thead>
	<tbody>
		<% requirements.each { requirement -> %>
		<tr>
			<td style="text-align: left"><img src="${ ui.resourceLink("kenyaui", "images/" + (requirement.satisfied ? "success.png" : "alert.png")) }" /></td>
			<td style="text-align: left">${ requirement.name }</td>
			<td>${ requirement.requiredVersion }</td>
			<td>${ requirement.foundVersion ?: "<i>None</i>" }</td>
		</tr>
		<% } %>
	</tbody>
</table>