<%
	config.require("itemFormatter") // name of a function(item), will be used to paint each result
	// supports numResultsFormatter, name of a function(listOfItems), should return something like "5 patient(s) found", or use kenyaemr.defaultNumResultsFormatter
	// supports clickFunction, name of a function() whose this will be the panel, if clicked

	// supports noneMessage (default "general.none", only takes effect if numResultsFormatter is not specified)
	def noneMessage = config.noneMessage ?: "general.none"
	if (config.numResultsFormatter)
		noneMessage = null
%>

<style>
	.panel:nth-child(odd) {
		background-color: #e0e0e0;
	}	

	.panel {
		<% if (config.clickFunction) { %> cursor: pointer; <% } %>
		border: 1px #a0a0a0 solid;
		margin-bottom: 0.5em;
		padding: 0.2em;
	}
	.panel .title {
		display: block;
		font-weight: bold;
		font-size: 1.1em;
	}
	.panel .icon {
		float: left;
		padding-right: 0.3em;
	}
	.panel .leftText {
		float: left;
	}
	.panel .one-column {
		float: left;
	}
</style>

<div id="${ config.id }">
	<div class="num-results"></div>
	<% if (noneMessage) { %>
		<div class="no-results">${ ui.message(noneMessage) }</div>
	<% } %>
	<div class="results"></div>
</div>

<script>
subscribe("${ config.id }/show", function(event, data) {
	<% if (config.numResultsFormatter) { %>
		jq('#${ config.id } > .num-results').html(${ config.numResultsFormatter }(data));
	<% } %>
	<% if (noneMessage) { %>
		if (data.length == 0) {
			jq('#${ config.id } > .no-results').show();
		} else {
			jq('#${ config.id } > .no-results').hide();
		}
	<% } %>

	jq('#${ config.id } > .results').html('');

	for (var i = 0; i < data.length; ++i) {
		var html = ${ config.itemFormatter }(data[i]);
		jq(html)
			.appendTo(jq('#${ config.id } > .results'))
			<% if (config.clickFunction) { %> .click(${ config.clickFunction }) <% } %>
	}	
});
</script>