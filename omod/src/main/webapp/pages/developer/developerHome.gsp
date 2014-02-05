<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	def menuItems = [
			[ label: "Overview", iconProvider: "kenyaui", icon: "buttons/developer_overview.png", active: (section == "overview"), href: ui.pageLink("kenyaemr", "developer/developerHome") ],
			[ label: "Groovy console", iconProvider: "kenyaui", icon: "buttons/groovy.png", active: (section == "groovy"), href: ui.pageLink("kenyaemr", "developer/developerHome", [ section: "groovy" ]) ]
	]
%>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Developer", items: menuItems ]) }
</div>

<div class="ke-page-content">
<% if (section == "groovy") { %>
	${ ui.includeFragment("kenyaemr", "system/groovyConsole") }
<% } else { %>
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Overview</div>
		<div class="ke-panel-content">
			You are currently logged in the super user. Misuse of this account to perform unauthorised activities is a
			disciplinary offence. This account has sole access to the legacy OpenMRS user interface which you can access
			from here.

			<div style="text-align: center; padding-top: 20px">
				<button onclick="ui.navigate('/' + OPENMRS_CONTEXT_PATH + '/admin')">
					<img src="${ ui.resourceLink("kenyaui", "images/buttons/legacy.png") }" /> Legacy admin UI
				</button>
			</div>
		</div>
	</div>
<% } %>

</div>