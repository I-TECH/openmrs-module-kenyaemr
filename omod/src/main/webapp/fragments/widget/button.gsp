<%
// config supports icon, iconProvider, label, extra, href

def id = config.id ?: ui.randomId("button")
%>
<style>
.button > .icon {
	float: left;
}
.button > .labels {
	float: left;
	font-size: 1.4em;
	padding-left: 0.5em;
	padding-right: 0.5em;
}
.button > .labels > .extra {
	font-size: 0.8em;
}
</style>

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
	<% if (config.href) { %></a><% } %>
</button>

<% if (config.href) { %>
	<script>
		jq(function() {
			jq('#${ id }').click(function() {
				location.href = '${ ui.escapeJs(config.href) }'
			});
		});
	</script>
<% } %>