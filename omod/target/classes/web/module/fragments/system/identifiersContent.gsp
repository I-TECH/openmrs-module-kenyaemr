<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Identifier Types" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th>${ ui.message("general.format") }</th>
			<th>Required</th>
		</tr>
	</thead>
	<tbody>
	<% identifiers.each { identifier -> %>
		<tr>
			<td>${ identifier.name }</td>
			<td>${ identifier.format ?: "" }</td>
			<td>${ identifier.required ? ui.message("general.yes") : ui.message("general.no") }</td>
		</tr>
	<% } %>
	</tbody>
</table>