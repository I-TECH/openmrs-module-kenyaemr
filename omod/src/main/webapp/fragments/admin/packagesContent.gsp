<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Metadata Packages" ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th>Version</th>
			<th>Imported</th>
		</tr>
	</thead>
	<tbody>
	<% packages.each { pkg -> %>
		<tr>
			<td>${ pkg.name }</td>
			<td>${ pkg.version }</td>
			<td><img src="${ ui.resourceLink("kenyaui", "images/" + (pkg.imported ? "success.png" : "alert.png")) }" alt="" /></td>
		</tr>
	<% } %>
	</tbody>
</table>