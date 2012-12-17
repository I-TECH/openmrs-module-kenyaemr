<%
	/* Decorates content with a styled panel */
%>
<div class="panel-frame" xmlns="http://www.w3.org/1999/html">
	<% if (config.editUrl) { %>
	<div class="panel-editlink clickable" onclick="location.href='${ config.editUrl }'">
		<img src="${ ui.resourceLink("kenyaemr", "images/edit.png") }"/> Edit
	</div>
	<% } %>
	<% if (config.heading) { %>
	<div class="panel-heading">${ config.heading }</div>
	<% } %>
	<div class="panel-content">
		<%= config.content %>
	</div>
</div>
