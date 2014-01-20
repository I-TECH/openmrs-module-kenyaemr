<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Registered Apps" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>ID</th>
			<th>Label</th>
			<th>Homepage</th>
		</tr>
	</thead>
	<tbody>
	<% apps.each { app -> %>
		<tr>
			<td>${ app.id }</td>
			<td>${ app.label }</td>
			<td>${ app.url }</td>
		</tr>
	<% } %>
	</tbody>
</table>