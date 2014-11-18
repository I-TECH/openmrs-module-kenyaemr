<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/patient.js")

	def menuItems = [
			[ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to accounts", href: ui.pageLink("kenyaemr", "admin/manageAccounts") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Create Account", items: menuItems ]) }

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Help</div>
		<div class="ke-panel-content">
			If the registrant has been treated at this facility then you should search to see if they already exist as
			a person in the EMR and create the account from that.
		</div>
	</div>
</div>

<div class="ke-page-content">

	<script type="text/javascript">
		function ke_useNewPerson() {
			ui.navigate('kenyaemr', 'admin/createAccount2');
		}
	</script>

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Step 1: Select Existing Patient</div>
		<div class="ke-panel-controls" style="overflow: auto" ng-controller="PatientSearchForm" ng-init="init('non-accounts')">
			<table style="width: 100%">
				<tr>
					<td style="width: 50%; text-align: left; vertical-align: middle">
						Search by name or ID <input type="text" ng-model="query" ng-change="updateSearch()" />
					</td>
					<td style="width: 50%; text-align: right; vertical-align: middle">
						<button type="button" onclick="ke_useNewPerson()">
							<img src="${ ui.resourceLink("kenyaui", "images/buttons/account_add.png") }" /> Use new person
						</button>
					</td>
				</tr>
			</table>
		</div>
		<div class="ke-panel-content" ng-controller="PatientSearchResults" ng-init="init('${ currentApp.id }', 'kenyaemr', 'admin/createAccount2')">
			<div class="ke-stack-item ke-navigable" ng-repeat="patient in results" ng-click="onResultClick(patient)">
				${ ui.includeFragment("kenyaemr", "patient/result.full") }
			</div>
		</div>
	</div>

</div>