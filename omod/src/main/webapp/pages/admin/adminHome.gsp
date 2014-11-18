<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])
%>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [
			heading: "Tasks",
			items: [
					[ iconProvider: "kenyaui", icon: "buttons/users_manage.png", label: "Manage accounts", href: ui.pageLink("kenyaemr", "admin/manageAccounts") ],
					[ iconProvider: "kenyaui", icon: "buttons/report_queue.png", label: "Manage report queue", href: ui.pageLink("kenyaemr", "admin/manageReportQueue") ],
					[ iconProvider: "kenyaui", icon: "buttons/admin_setup.png", label: "Redo first-time setup", href: ui.pageLink("kenyaemr", "admin/firstTimeSetup") ],
                    [ iconProvider: "kenyaui", icon: "buttons/backup_restore.png", label: "Back Up and Restore", href: ui.pageLink("kenyaemr", "admin/manageBackups") ]
			]
	]) }
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "system/serverInformation") }
	${ ui.includeFragment("kenyaemr", "system/databaseSummary") }
	${ ui.includeFragment("kenyaemr", "system/externalRequirements") }
</div>