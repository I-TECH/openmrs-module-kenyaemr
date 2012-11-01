<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<a href="${ ui.pageLink("kenyaemr", "adminManageUsers") }">Back to User Management</a>

<% if (user) { %>

	${ ui.includeFragment("kenyaemr", "adminEditUser") }

<% } else { %>
	
	<h3>Create a new User</h3>

	${ ui.includeFragment("kenyaemr", "adminNewUser") }
	
<% } %>

