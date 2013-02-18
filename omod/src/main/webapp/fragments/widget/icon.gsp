<%
	config.require("iconProvider")
	config.require("icon")

	def overlayIcon = config.overlayIcon

	if (config.useEditOverlay)
		overlayIcon = "buttons/_overlay_edit.png"
	else if (config.useViewOverlay)
		overlayIcon = "buttons/_overlay_view.png"

	def tooltip = config.tooltip ?: ""
%>
<div style="position: relative; width: 32px; height: 32px">
	<img src="${ ui.resourceLink(config.iconProvider, "images/" + config.icon) }" style="position: absolute; top: 0; right: 0; width: 32px; height: 32px; z-index: 0" alt="${ tooltip }" />
	<% if (overlayIcon) { %>
	<img src="${ ui.resourceLink(config.iconProvider, "images/" + overlayIcon) }" style="position: absolute; top: 0; right: 0; width: 32px; height: 32px; z-index: 1" alt="${ tooltip }" />
	<% } %>
</div>