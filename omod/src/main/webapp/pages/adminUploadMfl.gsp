<%
	context.requirePrivilege("Upload Master Facility List")
%>

<% if (numSaved) { %>
	Saved ${ numSaved } locations. <br/>
<% } %>

<form method="post">
	Facility Code, Facility Name, County, Type <br/>
	<textarea name="csv" rows="10" cols="80"></textarea>
	<br/>
	<input type="submit"/>
</form>