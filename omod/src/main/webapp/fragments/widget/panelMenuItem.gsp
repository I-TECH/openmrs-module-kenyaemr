<%
	/* Creates a menu item for inclusion in panel widget */

	config.require("label");

	def styles = [ "panel-menuitem" ]
	if (config.active)
		styles << "panel-menuitem-active"
	if (config.href)
		styles << "clickable"
%>

<div class="${ styles.join(" ") }">
	<% if (config.icon && config.iconProvider) { %>
		<img src="${ ui.resourceLink(config.iconProvider, "images/" + config.icon) }" alt="" />
	<% }

	if (config.href) { %>
		<a href="${ config.href }" <% if (!config.extra) { %>style="margin-top: 6px"<% } %> >${ config.label }</a>
	<% } else { %>
		<span class="panel-menuitem-text">${ config.label }</span>
	<% }
	if (config.extra) { %>
		<br />
		<span class="panel-menuitem-description">${ config.extra }</span>
	<% } %>
</div>