<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Programs" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>${ ui.message("general.name") }</th>
			<th style="text-align: left">Enrollment Form</th>
			<th style="text-align: left">Visit Forms</th>
			<th style="text-align: left">Completion Form</th>
		</tr>
	</thead>
	<tbody>
	<% programs.each { program -> %>
		<tr>
			<td>${ program.name }</td>
			<td>${ program.enrollmentForm }</td>
			<td>${ program.visitForms.join("<br />") }</td>
			<td>${ program.completionForm }</td>
		</tr>
	<% } %>
	</tbody>
</table>