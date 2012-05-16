<%
// supports style (css style)

// listens for the event (id)/showEncounter (takes an encounter id)
%>

<div id="${ config.id }" <% if (config.style) { %>style="${ config.style }"<% } %>>
</div>

<script>
	subscribe('${ config.id }/showEncounter', function(message, encId) {
		jq('#${ config.id }').html('');
		getFragmentActionAsJson('showHtmlForm', 'viewFormHtml', { encounterId: encId }, function(data) {
			jq('#${ config.id }').html(data.html);
		});
	});
</script>