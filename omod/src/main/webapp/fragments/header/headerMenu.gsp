<%
	def appMenuItems = []
	def userMenuItems = []

	if (context.authenticatedUser) {

		appMenuItems << """<a href="/${ contextPath }/index.htm?${ config.context ? config.context : "" }"><img src="${ ui.resourceLink("kenyaui", "images/toolbar/home.png") }" width="12" height="12" />&nbsp;&nbsp;Home</a>"""

		if (currentApp) {
			appMenuItems << """<a href="/${ contextPath }/${ currentApp.url }">${ currentApp.label }</a>"""
		}

		userMenuItems << """<span>Logged in as <i>${ context.authenticatedUser.personName }</i></span>"""
		userMenuItems << """<a href="${ ui.pageLink("kenyaemr", "profile") }">My Profile</a>"""
		userMenuItems << """<a href="javascript:ke_logout()">Log Out</a>"""
	} else {
		userMenuItems << "<span><em>Not Logged In</em></span>"
	}

	userMenuItems << """<a href="javascript:ke_showHelp()"><img src="${ ui.resourceLink("kenyaui", "images/toolbar/help.png") }" width="12" height="12" />&nbsp;&nbsp;Help</a>"""
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
<script type="text/javascript">
	function ke_logout() {
		kenyaui.openConfirmDialog({ heading: 'Logout', message: 'Logout and end session?', okCallback: function() {
			ui.navigate('/${ contextPath }/logout');
		}});
	}
	function ke_showHelp() {
		var currentAppId = ${ currentApp ? ("'" + currentApp.id + "'" ) : "null" };
		kenyaui.openDynamicDialog({ heading: 'Help', url: ui.pageLink('kenyaemr', 'dialog/helpDialog', { appId: currentAppId }), width: 90, height: 90 });
	}
</script>