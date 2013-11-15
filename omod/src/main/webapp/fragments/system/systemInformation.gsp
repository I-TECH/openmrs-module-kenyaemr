<%
	ui.decorateWith("kenyaui", "panel", [ heading: "System Information" ])

	ui.includeJavascript("kenyaemr", "controllers/system.js")
%>
<div ng-app="kenyaemr" ng-controller="SystemController" ng-init="init('${ currentApp.id }')">
	<table class="ke-table-vertical">
		<tbody>
			<tr ng-repeat="infopoint in systemInformation">
				<td>{{ infopoint.label }}</td>
				<td>{{ infopoint.value }}</td>
			</tr>
		</tbody>
	</table>
</div>