<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Loaded Modules" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th>Version</th>
			<th style="text-align: right">Started</th>
		</tr>
	</thead>
	<tbody>
	<% modules.each { module -> %>
		<tr>
			<td>${ module.name }</td>
			<td>${ module.version }</td>
			<td style="text-align: right">
				<img src="${ ui.resourceLink("kenyaui", "images/" + (module.started ? "success.png" : "alert.png")) }" alt="${ module.started ? "Started" : "Stopped" }" />
			</td>
		</tr>
	<% } %>
	</tbody>
</table>