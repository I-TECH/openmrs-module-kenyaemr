<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Reports" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th>Allowed Apps</th>
		</tr>
	</thead>
	<tbody>
	<% reports.each { report -> %>
		<tr>
			<td>${ report.name }</td>
			<td>${ report.allowedApps.join("<br />") }</td>
		</tr>
	<% } %>
	</tbody>
</table>