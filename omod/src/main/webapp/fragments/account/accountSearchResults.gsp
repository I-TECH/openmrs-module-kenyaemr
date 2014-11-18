<%
	ui.includeJavascript("kenyaemr", "controllers/account.js")

	def heading = config.heading ?: "Matching Accounts"

	def clickable = config.pageProvider && config.page
%>
<div class="ke-panel-frame" ng-controller="AccountSearchResults" ng-init="init('${ currentApp.id }', '${ config.pageProvider ?: '' }', '${ config.page ?: '' }')">
	<div class="ke-panel-heading">${ heading }</div>
	<div class="ke-panel-content">
		<% if (clickable) { %>
		<div class="ke-stack-item ke-navigable" ng-repeat="account in results" ng-click="onResultClick(account)">
			${ ui.includeFragment("kenyaemr", "account/result.full", [ showUsernames: config.showUsernames ]) }
		</div>
		<% } else { %>
		<div class="ke-stack-item" ng-repeat="account in results">
			${ ui.includeFragment("kenyaemr", "account/result.full", [ showUsernames: config.showUsernames ]) }
		</div>
		<% } %>
		<div ng-if="results.length == 0" style="text-align: center; font-style: italic">None</div>
	</div>
</div>