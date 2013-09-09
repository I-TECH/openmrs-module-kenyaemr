<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Identifier Types" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th style="text-align: left">Format</th>
		</tr>
	</thead>
	<tbody>
	<% identifiers.each { identifier -> %>
		<tr>
			<td>${ identifier.name }</td>
			<td>${ identifier.format ?: "" }</td>
		</tr>
	<% } %>
	</tbody>
</table>