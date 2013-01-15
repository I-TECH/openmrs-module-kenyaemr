<%
	// supports doNotShowApp (default false, to just show a bar, and login info, but not the running app)
	def showApp = config.doNotShowApp != true
	
	def running = appStatus != null;
	def appLabel = running ? (appStatus?.app?.label ?: ui.message("appFramework.runningApp.unknownApp")) : null

	def appMenuItems = [
		"""<a href="/${ contextPath }/index.htm?${ config.context ? config.context : "" }">Home</a>"""
	]
	if (running && showApp) {
		appMenuItems << """<a href="/${ contextPath }/${ appStatus.app.homepageUrl }">${ appLabel }</a>"""
	}

	def userMenuItems = []
	if (context.authenticatedUser) {
		userMenuItems << """Logged in as <i>${ context.authenticatedUser.personName }</i>"""
		userMenuItems << """<a href="${ ui.pageLink("kenyaemr", "profile") }">My Profile</a>"""
		userMenuItems << """<a href="/${ contextPath }/logout">Log Out</a>"""
	} else {
		userMenuItems << "Not Logged In"
	}
%>

<div id="appheader">
	<div id="appheader-appmenu">
		${ appMenuItems.join("&nbsp;&nbsp;&#187;&nbsp;&nbsp;") }
	</div>
	<div id="appheader-usermenu">
		${ userMenuItems.join(" | ") }
	</div>

	<div style="clear: both"></div>
</div>