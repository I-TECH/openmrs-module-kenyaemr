<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	/**
	 * Formats a list of informational SimpleObjects as a decorated table
	 */
	def formatInfoList = { list ->
		def ret = "<table class=\"table-decorated table-vertical\"><tbody>"
		list.each { obj ->
			ret += "<tr>"
			obj.each { entry ->
				def property = entry.key
				if (property == "version") {
					// Version numbers look best left-aligned
					ret += "<td style=\"text-align: left\">${ obj[property] != null ? obj[property] : "-" }</td>"
				} else if (property == "started" || property == "imported") {
					// Use icon instead of text
					def icon = obj[property] ? "success.png" : "alert.png"
					ret += "<td style=\"text-align: right\"><img src=\"" + ui.resourceLink("kenyaui", "images/" + icon) + "\" alt=\"\" /></td>"
				} else {
					ret += "<td>${ obj[property] }</td>"
				}
			}
			ret += "</tr>"
		}
		ret += "</tbody></table>"
		return ret
	}
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [
			heading: "Information",
			items: [
					[
							iconProvider: "kenyaui",
							icon: "buttons/admin_overview.png",
							label: "General",
							active: (section == "overview"),
							href: ui.pageLink("kenyaemr", "adminHome")
					],
					[
							iconProvider: "kenyaui",
							icon: "buttons/admin_modules.png",
							label: "Modules",
							active: (section == "modules"),
							href: ui.pageLink("kenyaemr", "adminHome", [ section: "modules" ])
					]
			]
	]) }

	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [
			heading: "Tasks",
			items: [
					[ iconProvider: "kenyaui", icon: "buttons/users_manage.png", label: "Manage Accounts", href: ui.pageLink("kenyaemr", "adminManageAccounts") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_setup.png", label: "Redo First-time Setup", href: ui.pageLink("kenyaemr", "adminFirstTimeSetup") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_update.png", label: "Install New Software Version", href: ui.pageLink("kenyaemr", "adminSoftwareVersion") ]
			]
	]) }
</div>

<div id="content-main">

	<% infoCategories.each { %>

	<div class="panel-frame">
		<div class="panel-heading">${ it.key }</div>
		<div class="panel-content">
			<% if (it.value instanceof java.util.List) { %>
				${ formatInfoList(it.value) }
			<% } else { %>
				${ ui.format(it.value) }
			<% } %>
		</div>
	</div>

	<% } %>

</div>