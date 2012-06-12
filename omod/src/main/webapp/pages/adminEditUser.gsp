<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<a href="${ ui.pageLink("adminManageUsers") }">Back to User Management</a>

<% if (user) { %>

	${ ui.includeFragment("adminEditUser") }

<% } else { %>
	
	<h3>Create a new User</h3>

	${ ui.includeFragment("adminNewUser") }
	
<% } %>

