<%
	config.require("fields") // will be passed to labeledField widget
%>
<table>
	<tr>
	<% config.fields.each { %>
		<% if (it instanceof String) { %>
			<td>${ it }</td>
		<% } else { %>
			<td><%= ui.includeFragment("widget/labeledField", it) %></td>
		<% } %>
	<% } %>
	</tr>
</table>
