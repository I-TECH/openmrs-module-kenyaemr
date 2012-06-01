<%
	config.require("encounter")
	// supports maxObs (don't show more than this many obs, default 10)
	
	def maxObs = config.maxObs ?: 100
	
	def enc = config.encounter
	
	def displayObs = { obs ->
		"${ ui.format(obs.concept) }: ${ ui.format(obs) }"
	}
%>

<div style="float: left">
	<b><u>${ ui.format(enc.form ?: enc.encounterType) }</u></b> <br/>
</div>

<div style="float: right">
	<u>Providers</u><br/>
	<% enc.providersByRoles.each { role, providers -> %>
		${ ui.format(role) }:
		<%= providers.collect { ui.format(it) } .join(", ") %><br/>
	<% } %>
</div>

<div style="clear: left">
	<% def soFar = 0 %>
	<% enc.getObsAtTopLevel(false).each { %>
		<% if (soFar < maxObs ) { %>
			<% if (!it.obsGrouping) {
				soFar += 1
			%>
				${ displayObs(it) }<br/>
			<% } %>
		<% } else if (soFar == maxObs) {
			soFar += 1
		%>
			...
		<% } %>
	<% } %>
</div>
