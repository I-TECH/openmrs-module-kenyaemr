<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/person.js")

	def menuItems = [
			[ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "registration/registrationHome") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Create Patient", items: menuItems ]) }

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Help</div>
		<div class="ke-panel-content">
			Patients, providers and users all exist as persons in the EMR. If the registrant has worked at this facility then you should search and see if they already exist as a person.
		</div>
	</div>
</div>

<div class="ke-page-content">

	<script type="text/javascript">
		function ke_useNewPerson() {
			ui.navigate('kenyaemr', 'registration/createPatient2');
		}
	</script>

	<div class="ke-panel-frame" ng-controller="PersonSearch" ng-init="init('${ currentApp.id }', 'non-patients', 'kenyaemr', 'registration/createPatient2')">
		<div class="ke-panel-heading">Step 1: Select Person</div>
		<div class="ke-panel-controls" style="overflow: auto">
			<table style="width: 100%">
				<tr>
					<td style="width: 50%; text-align: left; vertical-align: middle">
						<img src="${ ui.resourceLink("kenyaui", "images/buttons/patient_from_person.png") }" style="vertical-align: middle" /> Filter
						<input type="text" ng-model="query" ng-change="refresh()" />
					</td>
					<td style="width: 50%; text-align: right; vertical-align: middle">
						<button type="button" onclick="ke_useNewPerson()">
							<img src="${ ui.resourceLink("kenyaui", "images/buttons/account_add.png") }" /> Use new person
						</button>
					</td>
				</tr>
			</table>
		</div>
		<div class="ke-panel-content">
			<div class="ke-stack-item ke-navigable" ng-repeat="person in results" ng-click="onResultClick(person)">
				<table style="width: 100%">
					<tr>
						<td style="width: 32px; vertical-align: top; padding-right: 5px">
							<img ng-src="${ ui.resourceLink("kenyaui", "images/buttons/person_") }{{ person.gender }}.png" />
						</td>
						<td style="text-align: left; vertical-align: top">
							<strong>{{ person.name }}</strong><br/>
							{{ person.birthDate }}
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

</div>