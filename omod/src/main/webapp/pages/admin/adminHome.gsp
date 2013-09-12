<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	/**
	 * Formats a list of informational SimpleObjects as a decorated table
	 */
	def formatInfoList = { list ->
		def ret = "<table class=\"ke-table-vertical\"><tbody>"
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

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "Information",
			items: [
					[
							iconProvider: "kenyaui",
							icon: "buttons/admin_overview.png",
							label: "General",
							active: (section == "overview"),
							href: ui.pageLink("kenyaemr", "admin/adminHome")
					],
					[
							iconProvider: "kenyaui",
							icon: "buttons/admin_modules.png",
							label: "Modules",
							active: (section == "modules"),
							href: ui.pageLink("kenyaemr", "admin/adminHome", [ section: "modules" ])
					],
					[
							iconProvider: "kenyaui",
							icon: "buttons/admin_content.png",
							label: "Content",
							active: (section == "content"),
							href: ui.pageLink("kenyaemr", "admin/adminHome", [ section: "content" ])
					]
			]
	]) }

	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "Tasks",
			items: [
					[ iconProvider: "kenyaui", icon: "buttons/users_manage.png", label: "Manage Accounts", href: ui.pageLink("kenyaemr", "admin/manageAccounts") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_setup.png", label: "Redo First-time Setup", href: ui.pageLink("kenyaemr", "admin/firstTimeSetup") ]
			]
	]) }
</div>

<div class="ke-page-content">

<% if (section == "content") { %>

	${ ui.includeFragment("kenyaui", "widget/tabMenu", [ items: [
			[ label: "Metadata", tabid: "metadata" ],
			[ label: "Programs", tabid: "programs" ],
			[ label: "Forms", tabid: "forms" ],
			[ label: "Identifiers", tabid: "identifiers" ],
			[ label: "Flags", tabid: "flags" ],
			[ label: "Reports", tabid: "reports" ]
	] ]) }

	<div class="ke-tab" data-tabid="metadata">${ ui.includeFragment("kenyaemr", "system/packagesContent") }</div>
	<div class="ke-tab" data-tabid="programs">${ ui.includeFragment("kenyaemr", "system/programsContent") }</div>
	<div class="ke-tab" data-tabid="forms">${ ui.includeFragment("kenyaemr", "system/formsContent") }</div>
	<div class="ke-tab" data-tabid="identifiers">${ ui.includeFragment("kenyaemr", "system/identifiersContent") }</div>
	<div class="ke-tab" data-tabid="flags">${ ui.includeFragment("kenyaemr", "system/flagsContent") }</div>
	<div class="ke-tab" data-tabid="reports">${ ui.includeFragment("kenyaemr", "system/reportsContent") }</div>

<% } else if (section == "modules") { %>

	${ ui.includeFragment("kenyaemr", "system/loadedModules") }

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