<table class="decorated" id="tblHistory">
	<thead>
	<tr>
		<th>Date</th>
		<% concepts.each { %>
		<th nowrap="nowrap">${ ui.format(it) }</th>
		<% } %>
	</tr>
	</thead>
	<tbody>
	<% if (!data) { %>
	<tr>
		<td></td>
		<td colspan="${ concepts.size() }">${ ui.message("general.none") }</td>
	</tr>
	<% } %>
	<% data.each { date, results -> %>
	<tr>
		<td nowrap="nowrap">${ ui.format(date) }</td>
		<% concepts.each { concept -> %>
		<td>
			<%
			def obs = results[concept]
			if (obs) {
			%>
				${ ui.format(obs) }
			<% } %>
		</td>
		<% } %>
	</tr>
	<% } %>
	</tbody>
</table>