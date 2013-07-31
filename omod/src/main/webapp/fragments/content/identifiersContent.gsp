<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Identifier Types" ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
		</tr>
	</thead>
	<tbody>
	<% identifiers.each { identifier -> %>
		<tr>
			<td>${ identifier.name }</td>
		</tr>
	<% } %>
	</tbody>
</table>