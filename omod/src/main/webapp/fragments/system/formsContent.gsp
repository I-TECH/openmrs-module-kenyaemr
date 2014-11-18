<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Forms" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th>Encounter Type</th>
			<th>Allowed Apps</th>
		</tr>
	</thead>
	<tbody>
	<% forms.each { form -> %>
		<tr>
			<td>${ form.name }</td>
			<td>${ form.encounterType }</td>
			<td>${ form.allowedApps.join("<br />") }</td>
		</tr>
	<% } %>
	</tbody>
</table>