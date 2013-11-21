<%
	ui.includeJavascript("kenyaemr", "controllers/account.js")

	def heading = config.heading ?: "Matching Accounts"
%>
<div class="ke-panel-frame" ng-controller="AccountSearchResults" ng-init="init('${ currentApp.id }', '${ config.pageProvider }', '${ config.page }')">
	<div class="ke-panel-heading">${ heading }</div>
	<div class="ke-panel-content">
		<div class="ke-stack-item ke-navigable" ng-repeat="account in results" ng-click="onResultClick(account)">
			${ ui.includeFragment("kenyaemr", "account/result.full") }
		</div>
		<div ng-if="results.length == 0" style="text-align: center; font-style: italic">None</div>
	</div>
</div>