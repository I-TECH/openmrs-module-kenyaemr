<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<a href="${ ui.pageLink("adminManageAccounts") }">Back to Account Management</a>

<% if (person) { %>

	${ ui.includeFragment("adminEditAccount") }

<% } else { %>
	
	<h3>Create a new Account</h3>

	${ ui.includeFragment("adminNewAccount") }
	
<% } %>

