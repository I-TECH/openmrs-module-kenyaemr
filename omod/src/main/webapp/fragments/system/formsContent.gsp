<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Forms" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th style="text-align: left">Encounter Type</th>
			<th style="text-align: left">Allowed Apps</th>
		</tr>
	</thead>
	<tbody>
	<% forms.each { form -> %>
		<tr>
			<td>${ form.name }</td>
			<td style="text-align: left">${ form.encounterType }</td>
			<td style="text-align: left">${ form.allowedApps.join("<br />") }</td>
		</tr>
	<% } %>
	</tbody>
</table>