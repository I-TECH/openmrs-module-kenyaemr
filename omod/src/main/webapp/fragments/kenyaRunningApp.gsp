<%
	// supports doNotShowApp (default false, to just show a bar, and login info, but not the running app)
	def showApp = config.doNotShowApp != true
	
	def running = appStatus != null;
	def appLabel = running ? (appStatus?.app?.label ?: ui.message("appFramework.runningApp.unknownApp")) : null
%>

<div id="running-app-header">
	<span id="running-app-home" style="float: left">
		<a href="/${ contextPath }/index.htm?<% if (config.context) { %>${ config.context }<% } %>">
			Home
		</a>
		<% if (running && showApp) { %>
			&nbsp;
			&#187;
		<% } %>
	</span>
	<% if (running && showApp) { %>
		<a href="/${ contextPath }/${ appStatus.app.homepageUrl }" id="running-app" style="float: left">
			<% if (appStatus?.app?.tinyIconUrl) { %>
				<img src="${ appStatus.app.tinyIconUrl }"/>
			<% } %>
			<span id="running-app-label">${ appLabel }</span>
		</a>
	<% } %>
	<span id="running-app-user" style="float: right">
		<% if (context.authenticatedUser) { %>
			${ context.authenticatedUser.personName }
			|
			<a href="/${ contextPath }/logout">Log Out</a>
		<% } else { %>
			Not Logged In
		<% } %>
	</span>
	<span style="clear: both">&nbsp;</span>
</div>