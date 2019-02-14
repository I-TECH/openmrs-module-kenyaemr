<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Roles" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th>Allowed Apps</th>
		</tr>
	</thead>
	<tbody>
	<% roles.each { role -> %>
		<tr>
			<td>${ role.name }</td>
			<td>${ role.allowedApps.join(", ") }</td>
		</tr>
	<% } %>
	</tbody>
</table>