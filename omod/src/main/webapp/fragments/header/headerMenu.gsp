<%
	def appMenuItems = []
	def userMenuItems = []

	if (context.authenticatedUser) {

		appMenuItems << """<a href="/${ contextPath }/index.htm?${ config.context ? config.context : "" }"><img src="${ ui.resourceLink("kenyaui", "images/toolbar/home.png") }" width="12" height="12" />&nbsp;&nbsp;Home</a>"""

		if (currentApp) {
			appMenuItems << """<a href="/${ contextPath }/${ currentApp.homepageUrl }">${ currentApp.label }</a>"""
		}

		userMenuItems << """<span>Logged in as <i>${ context.authenticatedUser.personName }</i></span>"""
		userMenuItems << """<a href="${ ui.pageLink("kenyaemr", "profile") }">My Profile</a>"""
		userMenuItems << """<a href="/${ contextPath }/logout">Log Out</a>"""
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
<div id="help-content" style="display: none">
	${ ui.includeFragment("kenyaemr", "help") }
</div>
<script type="text/javascript">
	function ke_showHelp() {
		kenyaui.openPanelDialog('Help', helpDialogHtml, 90, 90);
	}

	jq(function(){
		helpDialogHtml = jq('#help-content').html();
		jq('#help-content').remove();
	});
</script>