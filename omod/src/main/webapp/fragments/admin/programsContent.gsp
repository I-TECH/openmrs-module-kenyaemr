<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Programs" ])
%>
<table class="ke-table-decorated ke-table-vertical">
	<thead>
		<tr>
			<th>Name</th>
			<th style="text-align: left">Enrollment Form</th>
			<th style="text-align: left">Visit Forms</th>
			<th style="text-align: left">Completion Form</th>
		</tr>
	</thead>
	<tbody>
	<% programs.each { program -> %>
		<tr>
			<td>${ program.name }</td>
			<td style="text-align: left">${ program.enrollmentForm }</td>
			<td style="text-align: left">${ program.visitForms.join("<br />") }</td>
			<td style="text-align: left">${ program.completionForm }</td>
		</tr>
	<% } %>
	</tbody>
</table>