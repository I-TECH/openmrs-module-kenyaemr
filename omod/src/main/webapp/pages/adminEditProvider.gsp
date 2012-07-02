<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<a href="${ ui.pageLink("adminManageProvider") }">Back to Provider Management</a>

<% if (provider) { %>

	${ ui.includeFragment("adminEditProvider") }

<% } else { %>
	
	<h3>Create a new Provider</h3>

	${ ui.includeFragment("adminNewProvider") }
	
<% } %>

