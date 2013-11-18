<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
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
					[ iconProvider: "kenyaui", icon: "buttons/report_queue.png", label: "Manage Report Queue", href: ui.pageLink("kenyaemr", "admin/manageReportQueue") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_setup.png", label: "Redo First-time Setup", href: ui.pageLink("kenyaemr", "admin/firstTimeSetup") ]
			]
	]) }
</div>

<div class="ke-page-content">

<% if (section == "content") { %>

	${ ui.includeFragment("kenyaui", "widget/tabMenu", [ items: [
			[ label: "Programs", tabid: "programs" ],
			[ label: "Forms", tabid: "forms" ],
			[ label: "Identifiers", tabid: "identifiers" ],
			[ label: "Flags", tabid: "flags" ],
			[ label: "Reports", tabid: "reports" ]
	] ]) }

	<div class="ke-tab" data-tabid="programs">${ ui.includeFragment("kenyaemr", "system/programsContent") }</div>
	<div class="ke-tab" data-tabid="forms">${ ui.includeFragment("kenyaemr", "system/formsContent") }</div>
	<div class="ke-tab" data-tabid="identifiers">${ ui.includeFragment("kenyaemr", "system/identifiersContent") }</div>
	<div class="ke-tab" data-tabid="flags">${ ui.includeFragment("kenyaemr", "system/flagsContent") }</div>
	<div class="ke-tab" data-tabid="reports">${ ui.includeFragment("kenyaemr", "system/reportsContent") }</div>

<% } else if (section == "modules") { %>
	${ ui.includeFragment("kenyaemr", "system/loadedModules") }
<% } else { %>
	${ ui.includeFragment("kenyaemr", "system/systemInformation") }
	${ ui.includeFragment("kenyaemr", "system/databaseSummary") }
<% } %>

</div>