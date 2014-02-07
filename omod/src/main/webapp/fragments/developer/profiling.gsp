<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Report profiling", frameOnly: true ])

	ui.includeJavascript("kenyaemr", "controllers/developer.js")
%>

<div ng-controller="ReportProfiling" ng-init="init()">
	<div class="ke-panel-content">
		<div class="ke-datapoint">
			<span class="ke-label">Report evaluation profiling</span>: <span class="ke-value">{{ enabled ? "ON" : "OFF" }}</span>
		</div>
	</div>

	<div class="ke-panel-controls">
		<button ng-if="!enabled" ng-click="setEnabled(true)"><img src="${ ui.resourceLink("images/glyphs/enable.png") }" /> Enable</button>
		<button ng-if="enabled" ng-click="setEnabled(false)"><img src="${ ui.resourceLink("images/glyphs/disable.png") }" /> Disable</button>
	</div>
</div>