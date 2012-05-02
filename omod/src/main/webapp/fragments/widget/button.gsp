<%
// config supports icon, iconProvider, label, extra, href

def id = ui.randomId("button")
%>
<style>
.button > .icon {
	float: left;
}
.button > .labels {
	float: left;
	font-size: 1.4em;
}
.button > .labels > .extra {
	font-size: 0.8em;
}
</style>

<% if (config.href) { %><a href="${ ui.escapeAttribute(config.href) }"><% } %>
<button id="${ id }" class="button">
	<% if (config.icon) { %>
		<img class="icon" src="${ ui.resourceLink(config.iconProvider, "images/" + config.icon) }" />
	<% } %>
	<span class="labels">
		<% if (config.label) { %>
			<span class="label">${ config.label }</span>
		<% } %>
		<% if (config.extra) { %>
			<br/>
			<span class="extra">${ config.extra }</span>
		<% } %>
	</span>
</button>
<% if (config.href) { %></a><% } %>