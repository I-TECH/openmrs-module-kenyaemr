<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Metadata Packages" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th>Version</th>
		</tr>
	</thead>
	<tbody>
	<% packages.each { pkg -> %>
		<tr>
			<td>${ pkg.name }</td>
			<td>${ pkg.version }</td>
		</tr>
	<% } %>
	</tbody>
</table>