<%
	// Help dialog content - loaded into a modal dialog
%>
<div class="ke-panel-content">
	If you are experiencing a problem you should contact your clinic's IT admin for support.

	<% if (externalHelpUrl) { %>
		<script type="text/javascript">
			function endsWith(string, pattern) {
				var d = string.length - pattern.length;
				return d >= 0 && string.indexOf(pattern, d) === d;
			}

			jQuery(function() {
				jQuery.getJSON('${ externalHelpUrl }/content.json')
						.success(function(data) {
							var currentAppId = ${ appId ? ("'" + appId + "'" ) : "null" };

							// Display help resource links for current app
							_.each(data.resources, function(resource) {
								if ((_.isEmpty(resource.apps) && !currentAppId) || _.contains(resource.apps, currentAppId)) {
									var name = resource.name;
									var url = '${ externalHelpUrl }/' + resource.file;
									var type = endsWith(resource.file, '.pdf') ? 'pdf' : 'video';
									var icon = '${ ui.resourceLink("kenyaui", "images/glyphs/") }' + type + ".png";
									jQuery('#resource-links').append('<div><img src="' + icon + '" class="ke-glyph" />&nbsp;&nbsp;<a href="' + url + '" target="_blank">' + name + '</a></div>');
								}
							});
						})
						.error(function() {
							kenyaui.notifyError('Unable to connect to external help');
						});
			});
		</script>

		You may also find the following resources helpful <em>(all links open in new windows)</em>:
		<br />
		<div id="resource-links" style="padding: 10px 0 10px 10px"></div>

		Click <a href="${ externalHelpUrl }" target="_blank">here</a> to view all help resources. If those do not resolve your problem, then you can submit a support ticket. To do so:<br />
	<% } %>

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