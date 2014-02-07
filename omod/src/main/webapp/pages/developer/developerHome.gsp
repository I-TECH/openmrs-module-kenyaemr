<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "Overview", iconProvider: "kenyaui", icon: "buttons/developer_overview.png", active: (section == "overview"), href: ui.pageLink("kenyaemr", "developer/developerHome") ],
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
	<% if (section == "modules") { %>
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