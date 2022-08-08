<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Search for a Patient" ])

	ui.includeJavascript("kenyaemr", "controllers/patient.js")

	def defaultWhich = config.defaultWhich ?: "all"

	def id = config.id ?: ui.generateId();
%>
<form id="${ id }" ng-controller="PatientSearchForm" ng-init="init('${ defaultWhich }')">
	<label  class="ke-field-label">Which patients</label>
	<span class="ke-field-content">
		<input type="radio" ng-model="which" ng-change="updateSearch()" value="all" /> All
		&nbsp;&nbsp;
		<input type="radio" ng-model="which" ng-change="updateSearch()" value="checked-in" /> Only Checked In
	</span>

	<label class="ke-field-label">ID or name (3 chars min)</label>
	<span class="ke-field-content">
		<input type="text" name="query" ng-model="query" ng-change="delayOnChange(updateSearch, 1000)" style="width: 260px" />
	</span>
</form>