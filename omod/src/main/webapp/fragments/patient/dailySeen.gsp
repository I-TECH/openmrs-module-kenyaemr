<%
    ui.includeJavascript("kenyaemr", "controllers/patient.js")

    def heading = "patients seen on "
    if (isToday)
        heading += "Today"
    else if (isYesterday)
        heading += "Yesterday"
    else
        heading += kenyaui.formatDate(date)
%>

<div class="ke-panel-frame" ng-controller="DailySeen" ng-init="init('${ currentApp.id }', '${ kenyaui.formatDateParam(date) }', '${ config.pageProvider }', '${ config.page }')">
    <div class="ke-panel-heading">{{ seen.length }} ${ heading }</div>
    <div class="ke-panel-content">
        <div class="ke-stack-item ke-navigable" ng-repeat="patient in seen" ng-click="onResultClick(patient)">
            ${ ui.includeFragment("kenyaemr", "patient/results.full.more") }
        </div>
        <div ng-if="seen.length == 0" style="text-align: center; font-style: italic">None</div>
    </div>
</div>