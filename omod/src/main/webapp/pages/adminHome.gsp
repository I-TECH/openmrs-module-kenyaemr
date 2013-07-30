<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	/**
	 * Formats a list of informational SimpleObjects as a decorated table
	 */
	def formatInfoList = { list ->
		def ret = "<table class=\"ke-table-decorated ke-table-vertical\"><tbody>"
		list.each { obj ->
			ret += "<tr>"
			obj.each { entry ->
				def property = entry.key
				if (property == "version") {
					// Version numbers look best left-aligned
					ret += "<td style=\"text-align: left\">${ obj[property] != null ? obj[property] : "-" }</td>"
				} else if (property == "status") {
					ret += "<td style=\"text-align: right\">"
					if (obj[property] instanceof Boolean) {
						// Use icon instead of text
						def icon = obj[property] ? "success.png" : "alert.png"
						ret += "<img src=\"" + ui.resourceLink("kenyaui", "images/" + icon) + "\" alt=\"\" />"
					} else {
						ret += "<img src=\"" + ui.resourceLink("kenyaui", "images/alert.png") + "\" alt=\"" + obj[property] + "\" />"
					}
					ret += "</td>"
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
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
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
					],
					[
							iconProvider: "kenyaui",
							icon: "buttons/admin_content.png",
							label: "Content",
							active: (section == "content"),
							href: ui.pageLink("kenyaemr", "adminHome", [ section: "content" ])
					]
			]
	]) }

	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "Tasks",
			items: [
					[ iconProvider: "kenyaui", icon: "buttons/users_manage.png", label: "Manage Accounts", href: ui.pageLink("kenyaemr", "adminManageAccounts") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_setup.png", label: "Redo First-time Setup", href: ui.pageLink("kenyaemr", "adminFirstTimeSetup") ]/*,
					[ iconProvider: "kenyaui", icon: "buttons/admin_update.png", label: "Install New Software Version", href: ui.pageLink("kenyaemr", "adminSoftwareVersion") ]*/
			]
	]) }
</div>

<div id="content-main">

	<% if (section == "content") { %>

		${ ui.includeFragment("kenyaemr", "admin/packagesContent") }
		${ ui.includeFragment("kenyaemr", "admin/programsContent") }
		${ ui.includeFragment("kenyaemr", "admin/formsContent") }
		${ ui.includeFragment("kenyaemr", "admin/identifiersContent") }

	<% } else { %>

		<% infoCategories.each { %>
		<div class="ke-panel-frame">
			<div class="ke-panel-heading">${ it.key }</div>
			<div class="ke-panel-content">
				<% if (it.value instanceof java.util.List) { %>
					${ formatInfoList(it.value) }
				<% } else { %>
					${ ui.format(it.value) }
				<% } %>
			</div>
		</div>
		<% } %>

	<% } %>

</div>