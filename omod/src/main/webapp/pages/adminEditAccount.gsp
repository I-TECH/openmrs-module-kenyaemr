<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<a href="${ ui.pageLink("kenyaemr", "adminManageAccounts") }">Back to Account Management</a>

<% if (person) { %>

	${ ui.includeFragment("kenyaemr", "adminEditAccount") }

<% } else { %>
	
	<h3>Create a new Account</h3>

	${ ui.includeFragment("kenyaemr", "adminNewAccount") }
	
<% } %>

