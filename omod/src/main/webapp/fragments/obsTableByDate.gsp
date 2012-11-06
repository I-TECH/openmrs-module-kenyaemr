<%
	def kenyaEmrWebUtils = context.loadClass("org.openmrs.module.kenyaemr.util.KenyaEmrWebUtils")

	config.require("id")
	config.require("concepts")
%>

<table class="decorated" id="${ config.id }" style="${ config.style ? config.style : "" }">
	<thead>
	<tr>
		<th>Date</th>
		<% config.concepts.each { %>
		<th nowrap="nowrap">${ ui.format(it) }</th>
		<% } %>
	</tr>
	</thead>
	<tbody>
	<% if (!data) { %>
	<tr>
		<td></td>
		<td colspan="${ config.concepts.size() }">${ ui.message("general.none") }</td>
	</tr>
	<% } %>
	<% data.each { date, results -> %>
	<tr>
		<td nowrap="nowrap"><%= kenyaEmrWebUtils.formatDateNoTime(date) %></td>
		<% config.concepts.each { concept -> %>
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