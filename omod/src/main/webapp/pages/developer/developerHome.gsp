<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "Overview", iconProvider: "kenyaui", icon: "buttons/developer_overview.png", active: (section == "overview"), href: ui.pageLink("kenyaemr", "developer/developerHome") ],
			[ label: "Content", iconProvider: "kenyaui", icon: "buttons/admin_content.png", active: (section == "content"), href: ui.pageLink("kenyaemr", "developer/developerHome", [ section: "content" ]) ],
			[ label: "Modules", iconProvider: "kenyaui", icon: "buttons/admin_modules.png", active: (section == "modules"), href: ui.pageLink("kenyaemr", "developer/developerHome", [ section: "modules" ]) ],
			[ label: "Profiling", iconProvider: "kenyaui", icon: "buttons/profiling.png", active: (section == "profiling"), href: ui.pageLink("kenyaemr", "developer/developerHome", [ section: "profiling" ]) ],
			[ label: "Validation", iconProvider: "kenyaui", icon: "buttons/validation.png", active: (section == "validation"), href: ui.pageLink("kenyaemr", "developer/developerHome", [ section: "validation" ]) ],
			[ label: "Groovy console", iconProvider: "kenyaui", icon: "buttons/groovy.png", active: (section == "groovy"), href: ui.pageLink("kenyaemr", "developer/developerHome", [ section: "groovy" ]) ]
	]
%>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Developer", items: menuItems ]) }
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
			<div class="ke-tabmenu-item" data-tabid="roles">Roles</div>
		</div>
		<div class="ke-tab" data-tabid="apps">${ ui.includeFragment("kenyaemr", "system/appsContent") }</div>
		<div class="ke-tab" data-tabid="programs">${ ui.includeFragment("kenyaemr", "system/programsContent") }</div>
		<div class="ke-tab" data-tabid="forms">${ ui.includeFragment("kenyaemr", "system/formsContent") }</div>
		<div class="ke-tab" data-tabid="identifiers">${ ui.includeFragment("kenyaemr", "system/identifiersContent") }</div>
		<div class="ke-tab" data-tabid="flags">${ ui.includeFragment("kenyaemr", "system/flagsContent") }</div>
		<div class="ke-tab" data-tabid="reports">${ ui.includeFragment("kenyaemr", "system/reportsContent") }</div>
		<div class="ke-tab" data-tabid="roles">${ ui.includeFragment("kenyaemr", "system/rolesContent") }</div>
	</div>
	<% } else if (section == "modules") { %>
	${ ui.includeFragment("kenyaemr", "developer/loadedModules") }
	<% } else if (section == "profiling") { %>
	${ ui.includeFragment("kenyaemr", "developer/profiling") }
	<% } else if (section == "validation") { %>
	${ ui.includeFragment("kenyaemr", "developer/validation") }
	<% } else if (section == "groovy") { %>
	${ ui.includeFragment("kenyaemr", "developer/groovyConsole") }
	<% } else { %>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Overview</div>
		<div class="ke-panel-content">
			<div class="ke-warning">
			You are currently logged in as a developer. Misuse of this account to perform unauthorised activities is a
			disciplinary offence.
			</div>

			<div style="text-align: center; padding-top: 20px">
				<button onclick="ui.navigate('/' + OPENMRS_CONTEXT_PATH + '/admin')">
					<img src="${ ui.resourceLink("kenyaui", "images/buttons/legacy.png") }" /> Legacy admin UI
				</button>
			</div>
		</div>
	</div>

	<% } %>

</div>