<%
	ui.includeJavascript("kenyaemr", "controllers/flags.js")
%>
<% if (currentApp) { %>
<div ng-controller="PatientFlags" ng-init="init('${ currentApp.id }', ${ config.patient.id })">
	<span ng-repeat="flag in flags" class="ke-flagtag">{{ flag.message }}</span>
</div>
<% } %>