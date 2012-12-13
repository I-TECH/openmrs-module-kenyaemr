<%
	config.require("items")
%>
<div class="panel-frame">
	<% if (config.heading) { %>
	<div class="panel-heading">${ config.heading }</div>
	<%
	}
	for (def itemConfig : config.items) { %>
   		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", itemConfig) }
	<% } %>
</div>



