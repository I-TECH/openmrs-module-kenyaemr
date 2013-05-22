<%
	// supports doNotShowApp (default false, to just show a bar, and login info, but not the running app)
	def showApp = config.doNotShowApp != true
	
	def running = appStatus != null;
	def appLabel = running ? (appStatus?.app?.label ?: ui.message("appFramework.runningApp.unknownApp")) : null

	def appMenuItems = [
		"""<a href="/${ contextPath }/index.htm?${ config.context ? config.context : "" }"><img src="${ ui.resourceLink("kenyaui", "images/toolbar/home.png") }" width="12" height="12" />&nbsp;&nbsp;Home</a>"""
	]
	if (running && showApp) {
		appMenuItems << """<a href="/${ contextPath }/${ appStatus.app.homepageUrl }">${ appLabel }</a>"""
	}

	def userMenuItems = []
	if (context.authenticatedUser) {
		userMenuItems << """<span>Logged in as <i>${ context.authenticatedUser.personName }</i></span>"""
		userMenuItems << """<a href="${ ui.pageLink("kenyaemr", "profile") }">My Profile</a>"""
		userMenuItems << """<a href="/${ contextPath }/logout">Log Out</a>"""
		userMenuItems << """<a href="javascript:ke_showHelp()"><img src="${ ui.resourceLink("kenyaui", "images/toolbar/help.png") }" width="12" height="12" />&nbsp;&nbsp;Help</a>"""
	} else {
		userMenuItems << "Not Logged In"
	}
%>

<div class="ke-toolbar">
	<div class="ke-apptoolbar">
		<% appMenuItems.each { item -> %><div class="ke-toolbar-item">${ item }</div><% } %>
	</div>
	<div class="ke-usertoolbar">
		<% userMenuItems.each { item -> %><div class="ke-toolbar-item">${ item }</div><% } %>
	</div>
	<div style="clear: both"></div>
</div>
<div id="help-content" style="display: none">
	${ui.includeFragment("kenyaemr", "help") }
</div>
<script type="text/javascript">
	function ke_showHelp() {
		kenyaui.openPanelDialog('Help', jq('#help-content').html(), 90, 90);

		// The regular button handler won't work as this part of the DOM is being duplicated, ids included
		jq('.close-help-button').click(function() {
			kenyaui.closeModalDialog();
		});
	}
</script>