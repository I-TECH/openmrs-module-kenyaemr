<%
	ui.decorateWith("kenyaui", "panel", [ heading: ui.message("kenyaemr.admin.systemRequirements") ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<th style="text-align: left; width: 32px">&nbsp;</th>
		<th style="text-align: left">Name</th>
		<th>Required</th>
		<th>Found</th>
	</thead>
	<tbody>
		<% requirements.each { requirement -> %>
		<tr>
			<td style="text-align: left"><img src="${ ui.resourceLink("kenyaui", "images/" + (requirement.pass ? "success.png" : "alert.png")) }" /></td>
			<td style="text-align: left">${ requirement.name }</td>
			<td>${ requirement.versionRequired }</td>
			<td>${ requirement.versionFound ?: "<i>None</i>" }</td>
		</tr>
		<% } %>
	</tbody>
</table>