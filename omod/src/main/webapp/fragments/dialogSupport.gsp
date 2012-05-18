<% ui.includeCss("jquery-ui.css") %>
<% ui.includeJavascript("jquery.js") %>
<% ui.includeJavascript("jquery-ui.js") %>
<% ui.includeJavascript("uiframework.js") %>

<script>
	\$(document).ready(function() {
		\$('#openmrsDialog').dialog({
			autoOpen: false,
			draggable: false,
			resizable: false,
			show: null,
			width: '90%',
			height: '90%',
			modal: true,
			close: function(event, ui) { dialogCurrentlyShown = null }
		});
	});
</script>

<div id="openmrsDialog" style="display: none; padding: 0.2em;"></div>