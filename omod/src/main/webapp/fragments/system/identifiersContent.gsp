<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Identifier Types" ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th style="text-align: left">Format</th>
		</tr>
	</thead>
	<tbody>
	<% identifiers.each { identifier -> %>
		<tr>
			<td>${ identifier.name }</td>
			<td style="text-align: left">${ identifier.format ?: "" }</td>
		</tr>
	<% } %>
	</tbody>
</table>