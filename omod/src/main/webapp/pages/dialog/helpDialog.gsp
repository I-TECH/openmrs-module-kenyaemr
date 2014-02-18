<%
	// Help dialog content - loaded into a modal dialog
%>
<div class="ke-panel-content">
	<% if (externalHelpUrl) { %>
	<script type="text/javascript">
		jQuery(function() {
			var externalHelpUrl = '${ externalHelpUrl }';
			var currentAppId = ${ appId ? ("'" + appId + "'" ) : "null" };

			kenyaemr.fetchHelpResources(externalHelpUrl, currentAppId, function(resources) {
				if (resources.length > 0) {
					jQuery('#external-help').show();
				}

				_.each(resources, function(res) {
					jQuery('#external-help-links').append('<div><img src="' + res.icon + '" class="ke-glyph" />&nbsp;&nbsp;<a href="' + res.url + '" target="_blank">' + res.name + '</a></div>');
				});
			});
		});
	</script>

	<div id="external-help" style="display: none">
		You may find the following resources helpful <em>(all links open in new windows)</em>:
		<br />
		<!-- Resource links dynamically added to this -->
		<div id="external-help-links" style="padding: 10px 0 10px 10px"></div>
	</div>

	Click <a href="${ externalHelpUrl }" target="_blank">here</a> to view all help resources.
	<% } %>

	If you are experiencing a problem you should contact your clinic's IT admin for support.
	You may also submit a support ticket. To do so:

	<br />

	<div style="width:450px; margin:0 auto; text-align: center; background-color: #e8e7e2; padding: 10px; border-radius: 4px">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/phone.png") }" class="ke-glyph" /> Call the help desk for free at <strong>${ supportNumber }</strong><br />
		or<br />
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/email.png") }" class="ke-glyph" /> Email <a href="mailto:${ supportEmail }">${ supportEmail }</a>
		<br />
		<br />
		Please include your facility code which is <strong>${ facilityCode }</strong>
	</div>
	<br />

</div>
<div class="ke-panel-footer">
	<button type="button" onclick="kenyaui.closeDialog()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/close.png") }" /> Close</button>
</div>