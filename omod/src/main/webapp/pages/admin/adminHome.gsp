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
					[ iconProvider: "kenyaui", icon: "buttons/users_manage.png", label: "Manage accounts", href: ui.pageLink("kenyaemr", "admin/manageAccounts") ],
					[ iconProvider: "kenyaui", icon: "buttons/report_queue.png", label: "Manage report queue", href: ui.pageLink("kenyaemr", "admin/manageReportQueue") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_setup.png", label: "Redo first-time setup", href: ui.pageLink("kenyaemr", "admin/firstTimeSetup") ]
			]
	]) }
</div>

<div class="ke-page-content">

<% if (section == "content") { %>

	<div id="content-tabs" class="ke-tabs">
		<div class="ke-tabmenu">
			<div class="ke-tabmenu-item" data-tabid="apps">Apps</div>
			<div class="ke-tabmenu-item" data-tabid="programs">Programs</div>
			<div class="ke-tabmenu-item" data-tabid="forms">Forms</div>
			<div class="ke-tabmenu-item" data-tabid="identifiers">Identifiers</div>
			<div class="ke-tabmenu-item" data-tabid="flags">Flags</div>
			<div class="ke-tabmenu-item" data-tabid="reports">Reports</div>
		</div>
		<div class="ke-tab" data-tabid="apps">${ ui.includeFragment("kenyaemr", "system/appsContent") }</div>
		<div class="ke-tab" data-tabid="programs">${ ui.includeFragment("kenyaemr", "system/programsContent") }</div>
		<div class="ke-tab" data-tabid="forms">${ ui.includeFragment("kenyaemr", "system/formsContent") }</div>
		<div class="ke-tab" data-tabid="identifiers">${ ui.includeFragment("kenyaemr", "system/identifiersContent") }</div>
		<div class="ke-tab" data-tabid="flags">${ ui.includeFragment("kenyaemr", "system/flagsContent") }</div>
		<div class="ke-tab" data-tabid="reports">${ ui.includeFragment("kenyaemr", "system/reportsContent") }</div>
	</div>

<% } else if (section == "modules") { %>
	${ ui.includeFragment("kenyaemr", "system/loadedModules") }
<% } else { %>
	${ ui.includeFragment("kenyaemr", "system/systemInformation") }
	${ ui.includeFragment("kenyaemr", "system/databaseSummary") }
<% } %>

</div>