<%
	ui.decorateWith("standardAppPage")
%>

<h3>System Information</h3>

<table>
	<% info.each { %>
		<tr>
			<th>${ it.key }</th>
			<td>${ ui.format(it.value) }</td>
		</tr>
	<% } %>
</table>

<h3>Recent Errors</h3>

Ideally we can record errors that occur (especially if the end-user sees them) and allow them to be reported from here.
(In a first pass this would just mean giving the admin a textarea to be copied to the clipboard.)