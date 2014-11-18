<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Search for an Account" ])

	ui.includeJavascript("kenyaemr", "controllers/account.js")

	def defaultWhich = config.defaultWhich ?: "all"

	def id = config.id ?: ui.generateId();
%>
<form id="${ id }" ng-controller="AccountSearchForm" ng-init="init('${ defaultWhich }')">
	<label  class="ke-field-label">Which accounts</label>
	<span class="ke-field-content">
		<input type="radio" ng-model="which" ng-change="updateSearch()" value="all" /> All
		&nbsp;&nbsp;
		<input type="radio" ng-model="which" ng-change="updateSearch()" value="providers" /> Providers
		&nbsp;&nbsp;
		<input type="radio" ng-model="which" ng-change="updateSearch()" value="users" /> Users
	</span>

	<label class="ke-field-label">Name or username (3 chars min)</label>
	<span class="ke-field-content">
		<input type="text" name="query" ng-model="query" ng-change="updateSearch()" style="width: 260px" />
	</span>
</form>