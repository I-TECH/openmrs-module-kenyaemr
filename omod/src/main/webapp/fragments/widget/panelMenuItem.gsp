<%
	/* Creates a menu item for inclusion in panel widget */

	config.require("label");

	def styles = [ "ke-control", "ke-menuitem" ]
	if (config.active)
		styles << "ke-menuitem-active"
	if (config.href)
		styles << "ke-clickable"
%>

<div class="${ styles.join(" ") }">
	<% if (config.icon && config.iconProvider) { %>
		${ ui.includeFragment("kenyaui", "widget/icon", [ iconProvider: config.iconProvider, icon: config.icon ]) }
	<% }

	if (config.href) { %>
		<a href="${ config.href }" <% if (!config.extra) { %>style="margin-top: 6px"<% } %> >${ config.label }</a>
	<% } else { %>
		<span class="ke-label">${ config.label }</span>
	<% }
	if (config.extra) { %>
		<br />
		<span class="ke-extra">${ config.extra }</span>
	<% } %>
</div>