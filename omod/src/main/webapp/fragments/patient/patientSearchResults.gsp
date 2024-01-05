<%
	ui.includeJavascript("kenyaemr", "controllers/patient.js")

	def heading = config.heading ?: "Matching Patients"
%>
<div class="ke-panel-frame" ng-controller="PatientSearchResults" ng-init="init('${ currentApp.id }', '${ config.pageProvider }', '${ config.page }')">
	<div class="ke-panel-heading">${ heading }</div>
	<div class="ke-panel-content">
		<div class="ke-stack-item ke-navigable" ng-repeat="patient in results" ng-click="onResultClick(patient)">
			${ ui.includeFragment("kenyaemr", "patient/result.full") }
		</div>
		<div ng-if="results.length == 0 && !showLoader" style="text-align: center; font-style: italic">None</div>
		<div ng-if= "showLoader" style="text-align: center;"> 
		<img src="${ ui.resourceLink("kenyaui", "images/loader_small.gif") }" />
		</div>
	</div>
</div>