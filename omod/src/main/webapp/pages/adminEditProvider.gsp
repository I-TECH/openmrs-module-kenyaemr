<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<a href="${ ui.pageLink("kenyaemr", "adminManageProvider") }">Back to Provider Management</a>

<% if (provider) { %>

	${ ui.includeFragment("kenyaemr", "adminEditProvider") }

<% } else { %>
	
	<h3>Create a new Provider</h3>

	${ ui.includeFragment("kenyaemr", "adminNewProvider") }
	
<% } %>

