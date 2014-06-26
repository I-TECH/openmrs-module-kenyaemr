<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/account.js")

	def menuItems = [
			[ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "registration/createPatient") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Create Patient", items: menuItems ]) }

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Help</div>
		<div class="ke-panel-content">
			If the registrant has worked at this facility then you should search to see if they already exist as a
			person in the EMR and create the patient record from that.
		</div>
	</div>
</div>

<div class="ke-page-content">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Step 2 :Select Existing Account</div>
		<div class="ke-panel-controls" style="overflow: auto" ng-controller="AccountSearchForm" ng-init="init('non-patients')">
			<table style="width: 100%">
				<tr>
					<td style="width: 50%; text-align: left; vertical-align: middle">
						Filter <input type="text" ng-model="query" ng-change="updateSearch()" />
					</td>
				</tr>
			</table>
		</div>
		<div class="ke-panel-content" ng-controller="AccountSearchResults" ng-init="init('${ currentApp.id }', 'kenyaemr', 'registration/createPatient2')">
			<div class="ke-stack-item ke-navigable" ng-repeat="account in results" ng-click="onResultClick(account)">
				${ ui.includeFragment("kenyaemr", "account/result.full") }
			</div>
		</div>
	</div>

</div>