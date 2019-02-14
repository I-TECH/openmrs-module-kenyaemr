<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Patient Flags" ])
%>
<table class="ke-table-vertical">
	<thead>
		<tr>
			<th>Class</th>
			<th style="text-align: left">Message</th>
		</tr>
	</thead>
	<tbody>
	<% flags.each { flag -> %>
		<tr>
			<td>${ flag.className }</td>
			<td>${ flag.message }</td>
		</tr>
	<% } %>
	</tbody>
</table>