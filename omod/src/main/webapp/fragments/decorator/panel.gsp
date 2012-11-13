<%
	/* Decorates content with a styled panel */
%>
<div class="panel-frame">
	<% if (config.heading) { %>
		<div class="panel-heading">${ config.heading }</div>
	<% } %>
	<div class="panel-content">
		<%= config.content %>
	</div>
</div>
