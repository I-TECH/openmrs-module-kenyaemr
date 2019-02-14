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

	<% } %>


	<div style="width:auto; margin:0 auto; text-align: center; background-color: #803625; padding: 10px; border-radius: 4px">
	    <h4 style="color: white; align: left;">KenyaEMR Release Notes</h4>
		<a href="../help/releases/index.html") }" style="color: white;" target="_blank"> <img src="${ ui.resourceLink("kenyaemr", "images/help/releases.png") }" /></a>
	</div>
	<br />
	<div style="width: auto; margin:0 auto; text-align: left; background-color: #803625; padding: 10px; border-radius: 4px; valign: top">
	    <h4 style="color: white">Self Learning Tools and Resources</h4>
	    <div>
	        <a href="../help/eSessions/index.html"><img src="${ ui.resourceLink("kenyaemr", "images/help/esessions_icon.png") }" /></a>
	        <a href="../help/index.html"><img src="${ ui.resourceLink("kenyaemr", "images/help/job_aids_icon.png") }" /></a>
	        <a href="../help/guidelines/index.html"><img src="${ ui.resourceLink("kenyaemr", "images/help/guidelines_icon.png") }" /></a>
	        <a href="../help/analysis-tools/index.html"><img src="${ ui.resourceLink("kenyaemr", "images/help/analysis_tools_icon.png") }" /></a>
	    </div>
	</div>
	<br />
	<div style="width:auto; margin:0 auto; text-align: left; background-color: #803625; padding: 10px; border-radius: 4px" valign: top>
	    <h4 style="color: white; valign: top">Contact Information</h4>
	     <div>
            <img src="${ ui.resourceLink("kenyaemr", "images/help/champion_icon.png") }"  target="_blank" />
            <img src="${ ui.resourceLink("kenyaemr", "images/help/email_icon.png") }"  target="_blank" />
            <img src="${ ui.resourceLink("kenyaemr", "images/help/phone_icon.png") }"  target="_blank" />
           </div>
    </div>

</div>
<div class="ke-panel-footer">
	<button type="button" onclick="kenyaui.closeDialog()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/close.png") }" /> Close</button>
</div>