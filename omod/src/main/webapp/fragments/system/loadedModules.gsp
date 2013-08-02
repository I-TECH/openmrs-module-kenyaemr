<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Loaded Modules" ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th style="text-align: left">Version</th>
			<th style="text-align: right">Started</th>
		</tr>
	</thead>
	<tbody>
	<% modules.each { module -> %>
		<tr>
			<td>${ module.name }</td>
			<td style="text-align: left">${ module.version }</td>
			<td style="text-align: right">
				<img src="${ ui.resourceLink("kenyaui", "images/" + (module.started ? "success.png" : "alert.png")) }" alt="${ module.started ? "Started" : "Stopped" }" />
			</td>
		</tr>
	<% } %>
	</tbody>
</table>