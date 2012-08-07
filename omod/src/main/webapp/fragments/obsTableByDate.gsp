<table class="decorated">
	<thead>
		<tr>
			<th></th>
			<% concepts.each { %>
				<th>
					${ ui.format(it) }
				</th>
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
				<th>${ ui.format(date) }</th>
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