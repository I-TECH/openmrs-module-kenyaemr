<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])

	def formatMap = { map ->
		def ret = "<table>"
		map.each {
			ret += '<tr valign="top">'
			ret += "<th>${ it.key }</th>"
			ret += "<td>${ ui.format(it.value) }</td>"
			ret += "</tr>"
		}
		ret += "</table>"
		return ret
	}
%>

<div id="content-side">
	<div class="panel-frame">
		<div class="panel-heading">Tasks</div>

		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
			iconProvider: "kenyaemr",
			icon: "buttons/users_manage.png",
			label: "Manage Accounts",
			href: ui.pageLink("kenyaemr", "adminManageAccounts")
		]) }

		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
			iconProvider: "kenyaemr",
			icon: "buttons/admin_setup.png",
			label: "Redo First-time Setup",
			href: ui.pageLink("kenyaemr", "adminFirstTimeSetup")
		]) }


		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
			iconProvider: "kenyaemr",
			icon: "buttons/admin_update.png",
			label: "Install New Software Version",
			href: ui.pageLink("kenyaemr", "adminSoftwareVersion")
		]) }
	</div>
</div>

<div id="content-main">

	<script type="text/javascript">
		jq(function() {
			jq('.accordion').accordion();
		});
	</script>

	<div class="accordion">
		<% info.each { %>
			<h3><a href="#">${ it.key }</a></h3>
			<div>
				<% if (it.value instanceof java.util.Map) { %>
					${ formatMap(it.value) }
				<% } else { %>
					${ ui.format(it.value) }
				<% } %>
			</div>
		<% } %>
	</div>
</div>