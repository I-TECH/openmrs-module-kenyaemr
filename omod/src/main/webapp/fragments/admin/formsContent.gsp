<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Forms" ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th style="text-align: left">Encounter Type</th>
			<th>Status</th>
		</tr>
	</thead>
	<tbody>
	<% forms.each { form -> %>
		<tr>
			<td>${ form.name }</td>
			<td style="text-align: left">${ form.encounterType }</td>
			<td><img src="${ ui.resourceLink("kenyaui", "images/" + (form.loaded ? "success.png" : "alert.png")) }" alt="${ form.error ?: "" }" /></td>
		</tr>
	<% } %>
	</tbody>
</table>